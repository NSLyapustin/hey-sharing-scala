package domain.item

import cats.Monad
import cats.data.EitherT.liftF

class ItemService[F[_]](itemRepo: ItemRepositoryAlgebra[F], validation: ItemValidationAlgebra[F]) {
  def createItem(item: Item, userId: Long)(implicit M: Monad[F]): F[Item] = {
    itemRepo.create(item, userId)
  }
}

object ItemService {
  def apply[F[_]](
                   repository: ItemRepositoryAlgebra[F],
                   validation: ItemValidationAlgebra[F],
                 ): ItemService[F] =
    new ItemService[F](repository, validation)
}
