package com.squeakybeaker.order.snippet

import com.squeakybeaker.order.model.DateProvider
import net.liftweb.http.{SHtml, S}

import net.liftweb.util._
import Helpers._
import com.squeakybeaker.order.lib.{DBAware, UserSession}
import com.squeakybeaker.order.model.Entity.{OrderItem, ItemType, OrderItemView}
import org.slf4j.LoggerFactory

/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object MenuProcess extends DBAware {

  val LOG = LoggerFactory.getLogger("com.squeakybeaker.order.snippet.MenuProcess")

  def render = {
    var special = ""
    var soup = ""
    var sandwich = ""
    var salad = ""

    def process(saveResults: Boolean)() = {
      LOG.debug("Running saveResults with {}", saveResults)
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
        val saladItem = option(salad, OrderItemView(ItemType.Salad, salad))
        val email = UserSession.is.email
        val currentDate = new java.sql.Date(DateProvider.getCurrentDate().getTime)
        implicit val p = (email, currentDate)
        // TODO some validation here
        val user = UserSession.is
        dao.removeCurrentOrder(user, new java.sql.Date(currentDate.getTime))
        if (saveResults) {
          List(sandwichItem, soupItem, specialItem, saladItem).flatten.foreach {
            case it: OrderItemView => dao.addRecord(user, OrderItem(it.itemName, currentDate, user.email, it.itemType))
          }
        }
      }
      S.redirectTo("/index")
    }

    "name=soup" #> SHtml.onSubmit(soup = _) &
      "name=special" #> SHtml.onSubmit(special = _) &
      "name=sandwich" #> SHtml.onSubmit(sandwich = _) &
      "name=salad" #> SHtml.onSubmit(salad = _) &
      "name=reject" #> SHtml.onSubmitUnit(process(false)) &
      "name=store" #> SHtml.onSubmitUnit(process(true))

  }


}