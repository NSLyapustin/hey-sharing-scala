package domain.item.Models

import domain.item.Dto.{ItemCreateRequestDto, ItemUpdateRequestDto}

case class Item(
                 id: Option[Long] = None,
                 name: String,
                 price: Double,
                 image: String,
                 countOfViews: Int,
                 description: String,
                 category: Category,
                 status: ItemStatus,
                 address: String,
                 userId: Option[Long]
               )

object Item {
  def from(itemDto: ItemCreateRequestDto, userId: Option[Long]): Item =
    new Item(None, itemDto.name, itemDto.price, itemDto.image, 0, itemDto.description, itemDto.category, ItemStatus.AwaitingConfirmation, itemDto.address, userId)

  def from(itemDto: ItemUpdateRequestDto, userId: Option[Long]): Item =
    new Item(itemDto.id, itemDto.name, itemDto.price, itemDto.image, 0, itemDto.description, itemDto.category, ItemStatus.AwaitingConfirmation, itemDto.address, userId)
}
