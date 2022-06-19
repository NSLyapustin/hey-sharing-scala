package domain.item

import cats.data.OptionT

trait ItemRepositoryAlgebra[F[_]] {
  def create(item: Item, userId: Long): F[Item]

  def update(item: Item): OptionT[F, Item]

  def get(itemId: Long): OptionT[F, Item]

  def findByName(itemName: String): OptionT[F, Item]

  def list(pageSize: Int, offset: Int): F[List[Item]]
}
