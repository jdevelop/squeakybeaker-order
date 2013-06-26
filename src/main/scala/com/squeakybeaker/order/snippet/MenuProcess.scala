package com.squeakybeaker.order.snippet

import com.squeakybeaker.order.model.DateProvider
import net.liftweb.http.{SHtml, S}

import net.liftweb.util._
import Helpers._
import com.squeakybeaker.order.lib.{DBAware, UserSession}
import com.squeakybeaker.order.model.Entity.{OrderItem, ItemType, OrderItemView}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object MenuProcess extends DBAware {

  def render = {
    var special = ""
    var soup = ""
    var sandwich = ""

    def process() = {
      if (UserSession.loggedIn_?) {

        def option[T](param: String, f: => T): Option[T] = {
          if ("" == param) {
            None
          } else {
            Some(f)
          }
        }

        val sandwichItem = option(sandwich, OrderItemView(ItemType.Sandwich, sandwich))
        val soupItem = option(soup, OrderItemView(ItemType.Soup, soup))
        val specialItem = option(special, OrderItemView(ItemType.Special, special))
        val email = UserSession.is.email
        val currentDate = new java.sql.Date(DateProvider.getCurrentDate().getTime)
        implicit val p = (email, currentDate)
        // TODO some validation here
        val user = UserSession.is
        dao.removeCurrentOrder(user, new java.sql.Date(currentDate.getTime))
        List(sandwichItem, soupItem, specialItem).flatten.foreach {
          case it: OrderItemView => dao.addRecord(user, OrderItem(it.itemName, currentDate, user.email, it.itemType))
        }
      }
      S.redirectTo("/index")
    }

    if (UserSession.loggedIn_?) {
      "name=soup" #> SHtml.onSubmit(soup = _) &
        "name=special" #> SHtml.onSubmit(special = _) &
        "name=sandwich" #> SHtml.onSubmit(sandwich = _) &
        "type=submit" #> SHtml.onSubmitUnit(process)
    } else {
      ":submit [disabled]" #> "disabled"
    }

  }


}