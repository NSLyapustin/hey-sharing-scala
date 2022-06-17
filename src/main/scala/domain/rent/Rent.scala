package domain.rent

import domain.product.Period
import java.util.Date

case class Rent(
  id: Long,
  fromDate: Date,
  toDate: Date,
  periodType: Period,
  countOfPeriod: Integer,
  productID: Long
)