package com.squeakybeaker.order.model

import java.util.Calendar

/**
 * User: Eugene Dzhurinsky
 * Date: 4/30/13
 */
object DateProvider {

  def getCurrentDate() = {
    val calendar = Calendar.getInstance()
    if (calendar.get(Calendar.HOUR_OF_DAY) > 10) {
      calendar.add(Calendar.DATE, 1)
    }
    calendar.getTime
  }

  def getCurrentLunchDate() = {
    val calendar = Calendar.getInstance()
    if (calendar.get(Calendar.HOUR_OF_DAY) > 14) {
      calendar.add(Calendar.DATE, 1)
    }
    calendar.getTime
  }

  def getPrevDate() = {
    val calendar = Calendar.getInstance()
    calendar.setTime(getCurrentDate())
    calendar.add(Calendar.DATE, -1)
    calendar.getTime
  }

}
