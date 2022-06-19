package domain.item

import cats.data.EitherT

trait ItemValidationAlgebra[F[_]] {
  def exists(item: Item): EitherT[F, ItemNotFoundError.type , Unit]
}
