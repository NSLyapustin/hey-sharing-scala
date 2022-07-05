package domain.item.Service

import cats.Monad
import cats.data.EitherT
import domain.item.Models.ItemStatus.AtTheTenant
import domain.item.Models.{Item, ItemStatus}
import domain.item.Repo.ItemRepositoryAlgebra
import domain.item.Validation.{ItemNotFoundError, ItemValidationAlgebra, UpdateNotAllowed}

class ItemService[F[_]: Monad](itemRepo: ItemRepositoryAlgebra[F], validation: ItemValidationAlgebra[F]) {
  def createItem(item: Item, userId: Long)(implicit M: Monad[F]): F[Item] = itemRepo.create(item, userId)

  def list(pageSize: Int, offset: Int): F[List[Item]] =
    itemRepo.list(pageSize, offset)

  def get(itemId: Long): EitherT[F, ItemNotFoundError.type, Item] = itemRepo.get(itemId).toRight(ItemNotFoundError)

  def update(item: Item, userId: Long): EitherT[F, UpdateNotAllowed.type, Item] =
    for {
      _ <- validation.canUpdate(item.id, userId)
      saved <- itemRepo.update(item).toRight(UpdateNotAllowed)
    } yield saved

  def updateStatus(itemId: Long, newStatus: ItemStatus): EitherT[F, ItemNotFoundError.type, Item] =
    for {
      _ <- validation.exists(Option(itemId))
      item <- get(itemId)
      updated <- itemRepo.update(item.copy(status = AtTheTenant)).toRight(ItemNotFoundError)
    } yield updated
}

object ItemService {
  def apply[F[_]: Monad](
                   repository: ItemRepositoryAlgebra[F],
                   validation: ItemValidationAlgebra[F],
                 ): ItemService[F] =
    new ItemService[F](repository, validation)
}
