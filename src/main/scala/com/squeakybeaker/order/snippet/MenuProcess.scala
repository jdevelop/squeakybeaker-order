package com.squeakybeaker.order.snippet

import com.squeakybeaker.order.model.{DateProvider, Entity, User}
import com.squeakybeaker.order.model.Entity.Orders.{ItemType, OrderItem}
import java.util.Date
import net.liftweb.http.{SHtml, S}

import net.liftweb.util._
import Helpers._
import com.squeakybeaker.order.model.Entity.OrdersPersistence.OrderItemP
import net.liftweb.mapper.{By, DB}
import net.liftweb.db.DefaultConnectionIdentifier

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
        val email = User.currentUser.get.email.is
        val currentDate = DateProvider.getCurrentDate()
        implicit val p = (email, currentDate)
        // TODO some validation here
        import Entity.OrdersPersistence.persistOrder
        DB.use(DefaultConnectionIdentifier) {
          case dbh => DB.prepareStatement("delete from " + OrderItemP.dbTableName + " where username=? and orderdate=?", dbh) {
            case psth =>
              psth.setString(1, email);
              psth.setDate(2, new java.sql.Date(currentDate.getTime))
              psth.execute()
          }
        }
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