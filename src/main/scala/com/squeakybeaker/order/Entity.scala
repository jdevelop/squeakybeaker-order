package com.squeakybeaker.order

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Entity {

  object Orders {

    object ItemType extends Enumeration {
      val Soup, Sandwich, Special = Value
    }

    case class OrderItem(kind: ItemType.Value, name: String)

    case class Order(items: Seq[OrderItem])

    case class Menu(soups: Seq[OrderItem], specials: Seq[OrderItem], sandwiches: Seq[OrderItem])

  }

  object Users {

    case class User(userId: String, username: String)

  }

}