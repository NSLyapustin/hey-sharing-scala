package domain.item.Validation

import cats.data.EitherT

trait ItemValidationAlgebra[F[_]] {
  def exists(itemId: Option[Long]): EitherT[F, ItemNotFoundError.type, Unit]

  def canUpdate(itemId: Option[Long], userId: Long): EitherT[F, UpdateNotAllowed.type, Unit]
}
