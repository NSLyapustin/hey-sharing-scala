package domain.item

import enumeratum._

sealed trait Category extends EnumEntry

case object Category extends Enum[Category] with CirceEnum[Category] {
  case object Vehicle extends Category
  case object Appliances extends Category
  case object Electronics extends Category
  case object Furniture extends Category
  case object HobbiesAndLeisure extends Category
  case object Clothes extends Category
  case object Other extends Category

  val values = findValues
}