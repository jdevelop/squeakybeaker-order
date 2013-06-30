package com.squeakybeaker.order.model

import java.sql.Date
import java.security.MessageDigest
import java.util.UUID
import org.slf4j.LoggerFactory

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Entity {

  val LOG = LoggerFactory.getLogger("com.squeakybeaker.order.model.Entity")

  object ItemType extends Enumeration {
    val Soup, Sandwich, Special = Value
  }

  case class User(id: UUID, email: String, displayName: String)

  case class OrderItem(itemName: String, placed: Date, username: String, itemType: ItemType.Value)

  case class OrderItemView(itemType: ItemType.Value, itemName: String)

  trait UserPersistence {

    this: DatabaseProfile =>

    import profile.simple._

    object UserP extends Table[(String, String, String, String)]("users") {

      def id = column[String]("uuid", O.PrimaryKey)

      def email = column[String]("email", O.NotNull)

      def displayName = column[String]("display_name", O.NotNull)

      def password = column[String]("password", O.NotNull)

      def * = id ~ email ~ displayName ~ password

    }

    def createNew(username: String, displayName: String)(implicit s: Session) = {
      val uuid = UUID.randomUUID()
      UserP.insert((uuid.toString, username, displayName, md5(username + System.currentTimeMillis().toString)))
      User(uuid, username, displayName)
    }

    def lookup(username: String)(implicit s: Session) = {
      val q = for (
        c <- UserP if c.email === username
      ) yield c
      q.take(1).list().headOption.map {
        case (id, uname, dname, pwd) => User(UUID.fromString(id), uname, dname)
      }
    }

    def lookup(uuid: UUID)(implicit s: Session) = {
      val q = for (
        c <- UserP if c.id === uuid.toString
      ) yield c
      q.take(1).list().headOption.map {
        case (id, uname, dname, pwd) => User(UUID.fromString(id), uname, dname)
      }
    }

    private def md5(s: String) = {
      MessageDigest.getInstance("MD5").digest(s.getBytes).map("%02X".format(_)).mkString
    }

  }

  trait OrdersPersistence {

    this: DatabaseProfile =>

    import profile.simple._

    object OrderItemP extends Table[(String, Date, String, String)]("orders") {
      def itemName = column[String]("item_name", O.NotNull)

      def placeDate = column[Date]("place_date", O.NotNull)

      def username = column[String]("username", O.NotNull)

      def itemType = column[String]("item_type", O.NotNull)

      def * = itemName ~ placeDate ~ username ~ itemType
    }

    def addRecord(itemName: String, placed: Date, username: String, itemType: ItemType.Value)(implicit s: Session) = {
      OrderItemP.insert((itemName, placed, username, itemType.toString))
      OrderItem(itemName, placed, username, itemType)
    }

    def addRecord(user: User, item: OrderItem)(implicit s: Session) {
      OrderItemP.insert((item.itemName, item.placed, user.id.toString, item.itemType.toString))
    }

    def aggregateRecords(date: Date)(implicit s: Session): List[(String, Int)] = {
      val q = for {
        oi <- OrderItemP if oi.placeDate === date
      } yield oi
      q.groupBy(_.itemName).map {
        row => (row._1, row._1.count)
      }.list
    }

    def aggregateTypes(date: Date)(implicit s: Session): List[(String, Int)] = {
      val q = for {
        oi <- OrderItemP if oi.placeDate === date
      } yield oi
      q.groupBy(_.itemType).map {
        row => (row._1, row._1.count)
      }.list
    }

    def loadOrder(date: Date, user: User)(implicit s: Session): List[OrderItemView] = {
      (for {
        oi <- OrderItemP if (oi.placeDate === date && oi.username === user.id.toString)
      } yield oi).list.map {
        case oip => OrderItemView(Entity.ItemType.withName(oip._4), oip._1)
      }
    }

    def loadLastOrder(user: User, itemType: ItemType.Value)(implicit s: Session): Option[OrderItemView] = {
      (for {
        oi <- OrderItemP if (oi.itemType === itemType.toString && oi.username === user.id.toString)
      } yield oi).sortBy(_.placeDate.desc.nullsDefault).firstOption.map {
        case oip => OrderItemView(Entity.ItemType.withName(oip._4), oip._1)
      }
    }

    def removeCurrentOrder(user: User, date: Date)(implicit s: Session) {
      (for {
        oi <- OrderItemP if oi.username === user.id.toString && oi.placeDate === date
      } yield oi).delete
    }

  }

  trait UserOrderLink {

    this: DatabaseProfile with UserPersistence with OrdersPersistence =>

    import profile.simple._

    def loadOrdersForToday(date: Date)(implicit s: Session) = {
      val q = for {
        oi <- OrderItemP if oi.placeDate === date
        u <- UserP if u.id === oi.username
      } yield (oi, u)
      val itemz = q.sortBy(_._2.displayName).list
      LOG.debug("List of items: {}", itemz)
      itemz
    }


  }

}