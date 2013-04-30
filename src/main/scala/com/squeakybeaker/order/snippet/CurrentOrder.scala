package com.squeakybeaker.order.snippet

import net.liftweb.mapper.DB
import java.util.Calendar

import net.liftweb.util._
import Helpers._


/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object CurrentOrder {

  def listSummary = {
    val calendar = Calendar.getInstance()
    val today = new java.sql.Date(calendar.getTimeInMillis)
    calendar.add(Calendar.DATE, -1)
    val yesterday = new java.sql.Date(calendar.getTimeInMillis)
    val currentItems = DB.runQuery("select orderItem, count(*) as cnt from orderitems where orderdate = ? group by orderItem", List(today))._2
    val yesterdayItems = DB.runQuery("select orderItem, count(*) as cnt from orderitems where orderdate = ? group by orderItem", List(yesterday))._2

    def mapItems(pName: String)(z: List[String]) = {
      z match {
        case List(name, count) => ("#" + pName + "Name *") #> name & ("#" + pName + "Count *") #> count
      }
    }

    "#today *" #> currentItems.map(mapItems("today")) &
      "#yesterday *" #> yesterdayItems.map(mapItems("yesterday"))
  }

}
