package com.squeakybeaker.order.snippet


import net.liftweb.util._
import Helpers._
import java.text.SimpleDateFormat
import com.squeakybeaker.order.lib.{UserSession, DBAware}
import com.squeakybeaker.order.model.DateProvider


/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object CurrentOrder extends DBAware {

  def listSummary = {
    val today = new java.sql.Date(DateProvider.getCurrentDate.getTime)
    val yesterday = new java.sql.Date(DateProvider.getPrevDate.getTime)
    val currentItems = dao.aggregateRecords(today)
    val yesterdayItems = dao.aggregateRecords(yesterday)

    def mapItems(pName: String)(z: (String, Int)) = {
      z match {
        case (name, count) => ("#" + pName + "Name *") #> name & ("#" + pName + "Count *") #> count
      }
    }

    "#todayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(today) &
      "#yesterdayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(yesterday) &
      "#today *" #> currentItems.map(mapItems("today")) &
      "#yesterday *" #> yesterdayItems.map(mapItems("yesterday"))
  }

  def listCurrentOrders = {
    val today = new java.sql.Date(DateProvider.getCurrentLunchDate().getTime)
    val itemz = dao.loadOrdersForToday(today).groupBy {
      case (item, user) => user._3
    }
    "#listItems *" #> itemz.map {
      case (name, orderz) => "#user *" #> name & {
        if (UserSession.is.displayName == name) {
          "#user [class+]" #> "my-order"
        } else {
          "veryfunnything *" #> "ook"
        }
      } &
        "#orders *" #> orderz.map {
          it => "#order *" #> it._1._1 & {
            if (UserSession.is.displayName == name) {
              "#order [class+]" #> "my-order"
            } else {
              "veryfunnything *" #> "ook"
            }
          }
        }
    }
  }

}