package com.squeakybeaker.order.snippet

import com.squeakybeaker.order.model.{Entity, User}
import com.squeakybeaker.order.model.Entity.Orders.{ItemType, OrderItem}
import java.util.Date
import net.liftweb.http.{SHtml, S}

import net.liftweb.util._
import Helpers._

/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object MenuProcess {

  def render = {
    var special = ""
    var soup = ""
    var sandwich = ""

    def process() = {
      println(
        """Processed!
          | %1$s
          | %2$s
          | %3$s
        """.stripMargin format(special, soup, sandwich))
      if (User.loggedIn_?) {

        def option[T](param: String, f: => T): Option[T] = {
          if ("" == param) {
            None
          } else {
            Some(f)
          }
        }

        val sandwichItem = option(sandwich, OrderItem(ItemType.Sandwich, sandwich))
        val soupItem = option(soup, OrderItem(ItemType.Soup, soup))
        val specialItem = option(special, OrderItem(ItemType.Special, special))
        implicit val p = (User.currentUser.get.email.is, new Date())
        // TODO some validation here
        import Entity.OrdersPersistence.persistOrder
        sandwichItem.foreach(_.save())
        soupItem.foreach(_.save())
        specialItem.foreach(_.save())
      }
      S.redirectTo("/index")
    }

    if (User.loggedIn_?) {
      "name=soup" #> SHtml.onSubmit(soup = _) &
        "name=special" #> SHtml.onSubmit(special = _) &
        "name=sandwich" #> SHtml.onSubmit(sandwich = _) &
        "type=submit" #> SHtml.onSubmitUnit(process)
    } else {
      ":submit [disabled]" #> "disabled"
    }

  }


}