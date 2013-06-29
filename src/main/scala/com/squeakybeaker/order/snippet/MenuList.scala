package com.squeakybeaker.order.snippet

import net.liftweb.util._
import Helpers._

import com.squeakybeaker.order.Datasource.Datasources
import scala.xml.{NodeSeq, Text}
import com.squeakybeaker.order.model.Entity.{OrderItemView, ItemType}
import com.squeakybeaker.order.lib.{UserSession, DBAware}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/26/13
 */
object MenuList extends DBAware {

  private def list(kind: ItemType.Value, lst: String, item: String)(in: NodeSeq): NodeSeq = {
    val currentItem = dao.loadLastOrder(UserSession.is, kind)
    val itemz: Option[Seq[OrderItemView]] = Datasources.getData(kind)
    itemz match {
      case Some(list) => {
        ("#" + lst + " *") #> list.map {
          mItem => ("#" + item + " [value]") #> mItem.itemName &
            ("#" + item + "txt *") #> mItem.itemName & (
            currentItem match {
              case Some(x) if x == mItem => ("#" + item + " [checked+]") #> "checked"
              case _ => "just_nothing" #> ""
            }
            )
        }
      }.apply(in)
      case None => Text("Oops")
    }
  }

  def listSoups = list(ItemType.Soup, "soups", "soup") _

  def listSpecials = list(ItemType.Special, "specials", "special") _

  def listSandwiches = list(ItemType.Sandwich, "sandwiches", "sandwich") _

}