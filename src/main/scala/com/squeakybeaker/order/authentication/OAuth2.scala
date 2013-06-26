package com.squeakybeaker.order.authentication

import com.squeakybeaker.order.model.Entity.User

/**
 * User: Eugene Dzhurinsky
 * Date: 6/11/13
 */
trait OAuth2 {

  def login(token: String): Option[User]

}
