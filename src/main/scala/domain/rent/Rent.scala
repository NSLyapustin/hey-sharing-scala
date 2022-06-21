package domain.rent

import java.util.Date

case class Rent(
  id: Option[Long],
  periodType: Period,
  countOfPeriod: Int,
  productID: Long
)
