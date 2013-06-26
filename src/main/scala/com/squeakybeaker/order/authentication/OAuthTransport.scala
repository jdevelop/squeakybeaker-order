package com.squeakybeaker.order.authentication

import java.net.URL

/**
 * User: Eugene Dzhurinsky
 * Date: 6/12/13
 */
trait OAuthTransport {

  def retrievePage(uri: String): io.Source = {
    io.Source.fromURL(new URL(uri))
  }

}