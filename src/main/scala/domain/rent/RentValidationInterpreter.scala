package domain.rent

import cats.syntax.all._
import cats.Applicative
import cats.data.EitherT
import domain.item.{Item, ItemRepositoryAlgebra, UpdateNotAllowed}

class RentValidationInterpreter[F[_]: Applicative](itemRepo: ItemRepositoryAlgebra[F])
  extends RentValidationAlgebra[F] {
  override def unoccupied(itemId: Long): EitherT[F, CannotRentItem.type, Unit] =
  EitherT {
    itemRepo.get(itemId).value.map {
      case Some(_) => Either.right[CannotRentItem.type, Unit](())
      case None => Either.left[CannotRentItem.type , Unit](CannotRentItem)
    }
  }
}

object RentValidationInterpreter {
  def apply[F[_]: Applicative](repository: ItemRepositoryAlgebra[F]) =
    new RentValidationInterpreter[F](repository)
}
