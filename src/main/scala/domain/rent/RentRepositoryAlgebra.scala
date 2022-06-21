package domain.rent

trait RentRepositoryAlgebra[F[_]] {
  def create(rent: Rent, itemId: Long, userId: Long): F[Rent]
}
