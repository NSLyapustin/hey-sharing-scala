package domain.item

import domain.rent.Rent
import domain.user.User

case class Item(
  id: Option[Long] = None,
  name: String,
  price: Double,
  period: Period,
  image: String,
  countOfViews: Double,
  description: String,
  category: Category,
  status: Status,
  address: String
)