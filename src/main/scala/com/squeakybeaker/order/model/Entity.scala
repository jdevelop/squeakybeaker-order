package com.squeakybeaker.order.model

import java.sql.Date
import java.security.MessageDigest

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Entity {

  object ItemType extends Enumeration {
    val Soup, Sandwich, Special = Value
  }

  case class User(email: String, displayName: String)

  case class OrderItem(itemName: String, placed: Date, username: String, itemType: ItemType.Value)

  case class OrderItemView(itemType: ItemType.Value, itemName: String)

  trait UserPersistence {

    this: DatabaseProfile =>

    import profile.simple._

    object UserP extends Table[(String, String, String)]("users") {

      def email = column[String]("email", O.NotNull)

      def displayName = column[String]("display_name", O.NotNull)

      def password = column[String]("password", O.NotNull)

      def * = email ~ displayName ~ password

    }

    def createNew(username: String, displayName: String)(implicit s: Session) = {
      UserP.insert((username, displayName, md5(username + System.currentTimeMillis().toString)))
      User(username,displayName)
    }

    def lookup(username: String)(implicit s: Session) = {
      val q = for (
        c <- UserP if c.email === username
      ) yield c
      q.take(1).list().headOption.map {
        case (uname, dname, pwd) => User(uname, dname)
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
      OrderItemP.insert((item.itemName, item.placed, user.email, item.itemType.toString))
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

    def removeCurrentOrder(user: User, date: Date)(implicit s: Session) {
      (for {
        oi <- OrderItemP if oi.username === user.email && oi.placeDate === date
      } yield oi).delete
    }

  }

}