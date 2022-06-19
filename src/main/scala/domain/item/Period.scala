package domain.item

final case class Period(periodRepl: String)

object Period {
  val Day: Period = Period("Day")
  val Week: Period = Period("Week")
  val Month: Period = Period("Month")

  def getRepr(p: Period): String = p.periodRepl
}

