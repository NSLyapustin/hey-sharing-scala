package domain.item

import enumeratum._

sealed trait ItemStatus extends EnumEntry

case object ItemStatus extends Enum[ItemStatus] with CirceEnum[ItemStatus] {
  case object AwaitingConfirmation extends ItemStatus
  case object AtTheReceptionPoint extends ItemStatus
  case object AtTheTenant extends ItemStatus

  val values = findValues
}