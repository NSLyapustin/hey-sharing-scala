package domain.rent.Repo

import domain.rent.Models.Rent

trait RentRepositoryAlgebra[F[_]] {
  def create(rent: Rent, itemId: Long, userId: Long): F[Rent]
}
