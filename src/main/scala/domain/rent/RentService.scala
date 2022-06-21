package domain.rent

import cats.syntax.all._
import cats.Monad
import cats.data.{EitherT, OptionT}
import domain.item.{ItemService}
import domain.item.ItemStatus.AtTheTenant

class RentService[F[_]: Monad](rentRepo: RentRepositoryAlgebra[F], itemService: ItemService[F], validation: RentValidationAlgebra[F]) {
  def createRent(rent: Rent, itemId: Long, userId: Long): EitherT[F, CannotRentItem.type, Rent] =
    for {
      _ <- validation.unoccupied(itemId)
      _ <- changeStatus(itemId, userId)
      rent <- EitherT.liftF(rentRepo.create(rent, itemId, userId))
    } yield rent

  private def changeStatus(itemId: Long, userId: Long): EitherT[F, CannotRentItem.type, Unit] = {
    EitherT {
      for {
        item <- itemService.get(itemId).value.flatMap {
          case Right(item) => item.copy(status = AtTheTenant).pure[F]
        }
        result <- itemService.update(item, userId).pure[F]
      } yield Right()
    }
  }
}

object RentService {
  def apply[F[_]: Monad](
                          repository: RentRepositoryAlgebra[F],
                          validation: RentValidationAlgebra[F],
                          itemService: ItemService[F]
                        ): RentService[F] =
    new RentService[F](repository, itemService, validation)
}
