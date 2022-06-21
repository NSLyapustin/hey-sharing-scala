package domain.item.Models

case class Item(
                 id: Option[Long] = None,
                 name: String,
                 price: Double,
                 image: String,
                 countOfViews: Double,
                 description: String,
                 category: Category,
                 status: ItemStatus,
                 address: String,
                 userId: Option[Long]
               )
