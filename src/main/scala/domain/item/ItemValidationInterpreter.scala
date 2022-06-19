package domain.item

import cats.Applicative
import cats.data.EitherT
import cats.syntax.all._

class ItemValidationInterpreter[F[_]: Applicative](itemRepo: ItemRepositoryAlgebra[F]) extends ItemValidationAlgebra[F] {
  override def exists(item: Item): EitherT[F, ItemNotFoundError.type, Unit] =
    EitherT {
      item.id match {
        case Some(id) =>
          itemRepo.get(id).value.map {
            case Some(_) => Right(())
            case _ => Left[ItemNotFoundError.type, Unit](ItemNotFoundError)
          }
        case _ =>
          Either.left[ItemNotFoundError.type, Unit](ItemNotFoundError).pure[F]
      }
    }
}

object ItemValidationInterpreter {
  def apply[F[_]: Applicative](repository: ItemRepositoryAlgebra[F]) =
    new ItemValidationInterpreter[F](repository)
}