package domain.item

final case class Status(statusRepl: String)

object Status {
  val AwaitingConfirmation: Status = Status("AwaitingConfirmation")
  val AtTheReceptionPoint: Status = Status("AtTheReceptionPoint")
  val AtTheTenant: Status = Status("AtTheTenant")
}