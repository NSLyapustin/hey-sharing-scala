package infrastructure.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.implicits.catsSyntaxOptionId
import domain.item._
import doobie._
import doobie.implicits._
import tsec.authentication.IdentityStore
import tsec.jws.JWSSerializer
import tsec.jws.mac.{JWSMacCV, JWSMacHeader}
import tsec.mac.jca.{MacErrorM, MacSigningKey}

private object ItemSQL {
  def insert(item: Item, userId: Long): Update0 = sql"""
    INSERT INTO ITEMS (NAME, PRICE, DURATION, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID)
    VALUES (${item.name}, ${item.price}, ${item.period}, ${item.image}, ${item.countOfViews}, ${item.description}, ${item.category}, ${item.status}, ${item.address}, ${userId})
""".update

  def update(item: Item, id: Long): Update0 = sql"""
    UPDATE ITEMS
    SET NAME = ${item.name},
        PRICE = ${item.price},
        DURATION = ${item.period},
        IMAGE = ${item.image},
        COUNT_OF_VIEWS = ${item.countOfViews},
        DESCRIPTION = ${item.description},
        CATEGORY = ${item.category},
        STATUS = ${item.status},
        ADDRESS = ${item.address},
        USER_ID = ${id}
    WHERE ID = $id
""".update
}
class DoobieItemRepositoryInterpreter[F[_]: Bracket[*[_], Throwable]](val xa: Transactor[F])
  extends ItemRepositoryAlgebra[F]
    with IdentityStore[F, Long, Item] { self =>
  import  ItemSQL._

  override def create(item: Item, userId: Long): F[Item] = insert(item, userId)
    .withUniqueGeneratedKeys[Long](columns = "id")
    .map(id => item.copy(id = id.some)).transact(xa)

  override def update(item: Item): OptionT[F, Item] = ???

  override def findByName(itemName: String): OptionT[F, Item] = ???

  override def list(pageSize: Int, offset: Int): F[List[Item]] = ???

  override def get(id: Long): OptionT[F, Item] = ???
}

object DoobieItemRepositoryInterpreter {
  def apply[F[_]: Bracket[*[_], Throwable]](xa: Transactor[F]): DoobieItemRepositoryInterpreter[F] =
    new DoobieItemRepositoryInterpreter(xa)
}
