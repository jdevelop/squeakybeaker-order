package com.squeakybeaker.order.snippet

import net.liftweb.mapper.DB
import java.util.Calendar

import net.liftweb.util._
import Helpers._
import java.text.SimpleDateFormat
import com.squeakybeaker.order.model.DateProvider


/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object CurrentOrder {

  def listSummary = {
    val today = new java.sql.Date(DateProvider.getCurrentDate.getTime)
    val yesterday = new java.sql.Date(DateProvider.getPrevDate.getTime)
    val currentItems = DB.runQuery("select orderItem, count(*) as cnt from orderitems where orderdate = ? group by orderItem", List(today))._2
    val yesterdayItems = DB.runQuery("select orderItem, count(*) as cnt from orderitems where orderdate = ? group by orderItem", List(yesterday))._2

    def mapItems(pName: String)(z: List[String]) = {
      z match {
        case List(name, count) => ("#" + pName + "Name *") #> name & ("#" + pName + "Count *") #> count
      }
    }

    "#todayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(today) &
      "#yesterdayDate *" #> new SimpleDateFormat("MM-dd-yyyy").format(yesterday) &
      "#today *" #> currentItems.map(mapItems("today")) &
      "#yesterday *" #> yesterdayItems.map(mapItems("yesterday"))
  }

}
