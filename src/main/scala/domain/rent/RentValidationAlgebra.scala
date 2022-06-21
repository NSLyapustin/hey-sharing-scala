package domain.rent

import cats.data.EitherT
import domain.item.Item

trait RentValidationAlgebra[F[_]] {
  def unoccupied(itemId: Long): EitherT[F, CannotRentItem.type, Unit]
}
