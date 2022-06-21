package domain.item.Repo

import cats.data.OptionT
import domain.item.Models.Item

trait ItemRepositoryAlgebra[F[_]] {
  def create(item: Item, userId: Long): F[Item]

  def update(item: Item, userId: Long): OptionT[F, Item]

  def get(itemId: Long): OptionT[F, Item]

  def findByName(itemName: String): OptionT[F, Item]

  def list(pageSize: Int, offset: Int): F[List[Item]]
}
