package domain.rent.Service

import cats.Monad
import cats.data.EitherT
import cats.syntax.all._
import domain.item.Models.ItemStatus.AtTheTenant
import domain.item.Service.ItemService
import domain.rent.Models.Rent
import domain.rent.Repo.RentRepositoryAlgebra
import domain.rent.Validation.{CannotRentItem, RentValidationAlgebra}

class RentService[F[_]: Monad](rentRepo: RentRepositoryAlgebra[F], itemService: ItemService[F], validation: RentValidationAlgebra[F]) {
  def createRent(rent: Rent, itemId: Long, userId: Long): EitherT[F, CannotRentItem.type, Rent] =
    for {
      _ <- validation.unoccupied(itemId)
      _ <- itemService.updateStatus(itemId, AtTheTenant).leftMap(_ => CannotRentItem)
      rent <- EitherT.liftF(rentRepo.create(rent, itemId, userId))
    } yield rent
}

object RentService {
  def apply[F[_]: Monad](
                          repository: RentRepositoryAlgebra[F],
                          validation: RentValidationAlgebra[F],
                          itemService: ItemService[F]
                        ): RentService[F] =
    new RentService[F](repository, itemService, validation)
}
