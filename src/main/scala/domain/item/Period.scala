package domain.item

import enumeratum._

sealed trait Period extends EnumEntry

case object Period extends Enum[Period] with CirceEnum[Period] {
  case object Day extends Period
  case object Week extends Period
  case object Month extends Period

  val values = findValues
}

