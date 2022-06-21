package domain.rent.Models

case class Rent(
                 id: Option[Long],
                 periodType: Period,
                 countOfPeriod: Int,
                 productID: Long
               )
