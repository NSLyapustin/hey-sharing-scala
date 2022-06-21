package infrastructure.repository

import cats.data.OptionT
import cats.effect.Bracket
import cats.implicits.{catsSyntaxOptionId, toFunctorOps}
import domain.item.Models.{Category, Item, ItemStatus}
import domain.item.Repo.ItemRepositoryAlgebra
import domain.item._
import domain.rent.Models.Period
import doobie._
import doobie.implicits._
import infrastructure.repository.SQLPagination.paginate
import tsec.authentication.IdentityStore

private object ItemSQL {
  implicit val StatusMeta: Meta[ItemStatus] =
    Meta[String].imap(ItemStatus.withName)(_.entryName)

  implicit val CategoryMeta: Meta[Category] =
    Meta[String].imap(Category.withName)(_.entryName)

  def insert(item: Item, userId: Long): Update0 = sql"""
    INSERT INTO ITEMS (NAME, PRICE, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID)
    VALUES (${item.name}, ${item.price}, ${item.image}, ${item.countOfViews}, ${item.description}, ${item.category}, ${item.status}, ${item.address}, ${userId})
""".update

  def update(item: Item, id: Long): Update0 = sql"""
    UPDATE ITEMS
    SET NAME = ${item.name},
        PRICE = ${item.price},
        IMAGE = ${item.image},
        COUNT_OF_VIEWS = ${item.countOfViews},
        DESCRIPTION = ${item.description},
        CATEGORY = ${item.category},
        STATUS = ${item.status},
        ADDRESS = ${item.address},
        USER_ID = ${id}
    WHERE ID = ${item.id}
""".update

  def select(itemId: Long): Query0[Item] =
    sql"""
      SELECT ID, NAME, PRICE, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID
      FROM ITEMS
      WHERE ID = $itemId
    """.query

  val selectAll: Query0[Item] =
    sql"""
      SELECT ID, NAME, PRICE, IMAGE, COUNT_OF_VIEWS, DESCRIPTION, CATEGORY, STATUS, ADDRESS, USER_ID
      FROM ITEMS
    """.query

  def updateStatus(itemId: Long, newStatus: ItemStatus): Update0 =
    sql"""
        UPDATE ITEMS
        SET STATUS = ${newStatus}
        WHERE ID = $itemId
       """.update
}
class DoobieItemRepositoryInterpreter[F[_]: Bracket[*[_], Throwable]](val xa: Transactor[F])
  extends ItemRepositoryAlgebra[F]
    with IdentityStore[F, Long, Item] { self =>
  import  ItemSQL._

  override def create(item: Item, userId: Long): F[Item] = insert(item, userId)
    .withUniqueGeneratedKeys[Long](columns = "id")
    .map(id => item.copy(id = id.some)).transact(xa)

  override def update(item: Item, userId: Long): OptionT[F, Item] = OptionT
    .fromOption[ConnectionIO](item.id)
    .semiflatMap(id => ItemSQL.update(item, userId).run.as(item))
    .transact(xa)

  override def list(pageSize: Int, offset: Int): F[List[Item]] = paginate(pageSize, offset)(selectAll).to[List].transact(xa)

  override def get(id: Long): OptionT[F, Item] = OptionT(select(id).option.transact(xa))

  override def findByName(itemName: String): OptionT[F, Item] = ???
}

object DoobieItemRepositoryInterpreter {
  def apply[F[_]: Bracket[*[_], Throwable]](xa: Transactor[F]): DoobieItemRepositoryInterpreter[F] =
    new DoobieItemRepositoryInterpreter(xa)
}
