package com.squeakybeaker.order.snippet

import net.liftweb.util._
import Helpers._

import com.squeakybeaker.order.Datasource.Datasources
import scala.xml.{NodeSeq, Text}
import com.squeakybeaker.order.model.Entity.{OrderItemView, ItemType}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/26/13
 */
object MenuList {

  private def list(kind: ItemType.Value, lst: String, item: String)(in: NodeSeq): NodeSeq = {
    val itemz: Option[Seq[OrderItemView]] = Datasources.getData(kind)
    itemz match {
      case Some(list) => {
        ("#" + lst + " *") #> list.map {
          case mItem: OrderItemView => ("#" + item + " [value]") #> mItem.itemName &
            ("#" + item + "txt *") #> mItem.itemName
        }
      }.apply(in)
      case None => Text("Oops")
    }
  }

  def listSoups = list(ItemType.Soup, "soups", "soup") _

  def listSpecials = list(ItemType.Special, "specials", "special") _

  def listSandwiches = list(ItemType.Sandwich, "sandwiches", "sandwich") _


}