package domain.rent.Validation

import cats.data.EitherT

trait RentValidationAlgebra[F[_]] {
  def unoccupied(itemId: Long): EitherT[F, CannotRentItem.type, Unit]
}
