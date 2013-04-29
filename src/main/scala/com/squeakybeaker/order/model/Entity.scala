package com.squeakybeaker.order.model

import net.liftweb.mapper._
import com.squeakybeaker.order.model.Entity.Orders.OrderItem
import java.util.Date

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

  }

  object OrdersPersistence {

    class OrderItemP extends LongKeyedMapper[OrderItemP] {

      override def getSingleton = OrderItemP

      def primaryKeyField = id

      object id extends MappedLongIndex(this)

      object username extends MappedString(this, 50)

      object orderDate extends MappedDate(this)

      object orderItem extends MappedText(this)

    }

    object OrderItemP extends OrderItemP with LongKeyedMetaMapper[OrderItemP] {

      override def dbTableName = "orderitems"

    }

    implicit def persistOrder(item: OrderItem)(implicit p: (String, Date)) = {
      OrderItemP.create.
        orderDate(p._2).
        username(p._1).
        orderItem(item.name)
    }

  }

}