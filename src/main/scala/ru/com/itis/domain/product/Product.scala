package ru.com.itis.domain.product

import ru.com.itis.domain.rent.Rent
import ru.com.itis.domain.user.User

case class Product(
  id: Long,
  name: String,
  price: Integer,
  period: Period,
  image: String,
  countOfViews: Integer,
  description: String,
  category: Category,
  status: Status,
  address: String,
  userId: Long,
  likedUsersList: List[User],
  rents: List[Rent]
)

sealed trait Period {
  case object Day extends Period
  case object Week extends Period
  case object Month extends Period
}

sealed trait Category {
  case object Vehicle extends Category
  case object Appliances extends Category
  case object Electronics extends Category
  case object Furniture extends Category
  case object HobbiesAndLeisure extends Category
  case object Clothes extends Category
  case object Other extends Category
}

sealed trait Status {
  case object AwaitingConfirmation extends Status
  case object AtTheReceptionPoint extends Status
  case object AtTheTenant extends Status
}