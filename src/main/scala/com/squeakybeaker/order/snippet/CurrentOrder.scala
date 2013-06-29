package com.squeakybeaker.order.snippet


import net.liftweb.util._
import Helpers._
import java.text.SimpleDateFormat
import com.squeakybeaker.order.lib.DBAware
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

    def mapItems(pName: String)(z: (String,Int)) = {
      z match {
        case (name, count) => ("#" + pName + "Name *") #> name & ("#" + pName + "Count *") #> count
      }
    }

    "#todayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(today) &
      "#yesterdayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(yesterday) &
      "#today *" #> currentItems.map(mapItems("today")) &
      "#yesterday *" #> yesterdayItems.map(mapItems("yesterday"))
  }

}