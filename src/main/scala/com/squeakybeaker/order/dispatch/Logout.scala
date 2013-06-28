package com.squeakybeaker.order.dispatch

import net.liftweb.http.{RedirectResponse, LiftResponse}
import net.liftweb.common.{Full, Box}
import com.squeakybeaker.order.lib.UserSession

/**
 * User: Eugene Dzhurinsky
 * Date: 6/28/13
 */
object Logout {

  def logout(): Box[LiftResponse] = {
    UserSession.set(null)
    Full(RedirectResponse("/index"))
  }

}
