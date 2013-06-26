package com.squeakybeaker.order.lib

import net.liftweb.http.SessionVar
import com.squeakybeaker.order.model.Entity.User

/**
 * User: Eugene Dzhurinsky
 * Date: 6/25/13
 */
object UserSession extends SessionVar[User](null){

  def loggedIn_? = is == null

}