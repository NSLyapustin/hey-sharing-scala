package domain.item.Validation

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._
import domain.item.Repo.ItemRepositoryAlgebra

class ItemValidationInterpreter[F[_]: Applicative](itemRepo: ItemRepositoryAlgebra[F]) extends ItemValidationAlgebra[F] {
  override def exists(itemId: Option[Long]): EitherT[F, ItemNotFoundError.type, Unit] =
    EitherT {
      itemId match {
        case Some(id) =>
          itemRepo.get(id).value.map {
            case Some(_) => Right(())
            case _ => Left[ItemNotFoundError.type, Unit](ItemNotFoundError)
          }
        case _ =>
          Either.left[ItemNotFoundError.type, Unit](ItemNotFoundError).pure[F]
      }
    }

  override def canUpdate(itemId: Option[Long], userId: Long): EitherT[F, UpdateNotAllowed.type, Unit] =
    EitherT {
      itemId match {
        case Some(id) => itemRepo.get(id).value.map {
          case Some(item) => if ((userId == item.userId.getOrElse(-1))) {
            Either.right[UpdateNotAllowed.type, Unit](())
          } else {
            Either.left[UpdateNotAllowed.type , Unit](UpdateNotAllowed)
          }
          case _ => Left[UpdateNotAllowed.type, Unit](UpdateNotAllowed)
        }
        case _ =>
          Either.left[UpdateNotAllowed.type, Unit](UpdateNotAllowed).pure[F]
      }
    }
}

object ItemValidationInterpreter {
  def apply[F[_]: Applicative](repository: ItemRepositoryAlgebra[F]) =
    new ItemValidationInterpreter[F](repository)
}