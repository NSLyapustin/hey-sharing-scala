package infrastructure.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.implicits.catsSyntaxOptionId
import doobie._
import doobie.implicits._
import domain.rent.{Period, Rent, RentRepositoryAlgebra}
import doobie.implicits.toSqlInterpolator
import tsec.authentication.IdentityStore

private object RentSQL {
  implicit val PeriodMeta: Meta[Period] =
    Meta[String].imap(Period.withName)(_.entryName)

  def select(rentId: Long): Query0[Rent] =
    sql"""
      SELECT ID, PERIOD_TYPE, COUNT_OF_PERIOD, ITEM_ID, USER_ID
      FROM RENTS
      WHERE ID = $rentId
    """.query

  def insert(rent: Rent, userId: Long, itemId: Long): Update0 =
    sql"""
        INSERT INTO RENTS (PERIOD_TYPE, COUNT_OF_PERIOD, ITEM_ID, USER_ID)
        VALUES (${rent.periodType}, ${rent.countOfPeriod}, ${itemId}, ${userId})
    """.update
}

class DoobieRentRepositoryInterpreter[F[_]: Bracket[*[_], Throwable]](val xa: Transactor[F])
  extends RentRepositoryAlgebra[F]
    with IdentityStore[F, Long, Rent] {
  self =>

  import RentSQL._

  override def create(rent: Rent, itemId: Long, userId: Long): F[Rent] =
    insert(rent, userId, itemId).withUniqueGeneratedKeys[Long]("id").map(id => rent.copy(id = id.some)).transact(xa)

  override def get(id: Long): OptionT[F, Rent] = OptionT(select(id).option.transact(xa))
}

object DoobieRentRepositoryInterpreter {
  def apply[F[_]: Bracket[*[_], Throwable]](xa: Transactor[F]): DoobieRentRepositoryInterpreter[F] =
    new DoobieRentRepositoryInterpreter(xa)
}
