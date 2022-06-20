package infrastructure.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.implicits.catsSyntaxOptionId
import domain.item._
import doobie._
import doobie.implicits._
import infrastructure.repository.SQLPagination.paginate
import tsec.authentication.IdentityStore

private object ItemSQL {
  implicit val StatusMeta: Meta[ItemStatus] =
    Meta[String].imap(ItemStatus.withName)(_.entryName)

  implicit val CategoryMeta: Meta[Category] =
    Meta[String].imap(Category.withName)(_.entryName)

  implicit val PeriodMeta: Meta[Period] =
    Meta[String].imap(Period.withName)(_.entryName)

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

  def select(itemId: Long): Query0[Item] =
    sql"""
      SELECT ID, NAME, PRICE, DURATION, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID
      FROM ITEMS
      WHERE ID = $itemId
    """.query

  val selectAll: Query0[Item] =
    sql"""
      SELECT ID, NAME, PRICE, DURATION, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID
      FROM ITEMS
    """.query
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

  override def list(pageSize: Int, offset: Int): F[List[Item]] = paginate(pageSize, offset)(selectAll).to[List].transact(xa)

  override def get(id: Long): OptionT[F, Item] = OptionT(select(id).option.transact(xa))
}

object DoobieItemRepositoryInterpreter {
  def apply[F[_]: Bracket[*[_], Throwable]](xa: Transactor[F]): DoobieItemRepositoryInterpreter[F] =
    new DoobieItemRepositoryInterpreter(xa)
}
