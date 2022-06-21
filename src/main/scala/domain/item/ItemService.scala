package domain.item

import cats.{Functor, Monad}
import cats.data.EitherT.liftF
import cats.data.{EitherT, OptionT}
import domain.user.User

class ItemService[F[_]: Monad](itemRepo: ItemRepositoryAlgebra[F], validation: ItemValidationAlgebra[F]) {
  def createItem(item: Item, userId: Long)(implicit M: Monad[F]): F[Item] = itemRepo.create(item, userId)

  def list(pageSize: Int, offset: Int): F[List[Item]] =
    itemRepo.list(pageSize, offset)

  def get(itemId: Long): EitherT[F, ItemNotFoundError.type, Item] = itemRepo.get(itemId).toRight(ItemNotFoundError)

  def update(item: Item, userId: Long): EitherT[F, UpdateNotAllowed.type, Item] =
    for {
      _ <- validation.canUpdate(item.id, userId)
      saved <- itemRepo.update(item, userId).toRight(UpdateNotAllowed)
    } yield saved
}

object ItemService {
  def apply[F[_]: Monad](
                   repository: ItemRepositoryAlgebra[F],
                   validation: ItemValidationAlgebra[F],
                 ): ItemService[F] =
    new ItemService[F](repository, validation)
}
