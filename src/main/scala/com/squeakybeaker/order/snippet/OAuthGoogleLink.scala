package com.squeakybeaker.order.snippet

import net.liftweb.util.Props
import net.liftweb.http.CurrentReq

/**
 * User: Eugene Dzhurinsky
 * Date: 6/14/13
 */
object OAuthGoogleLink {

  import net.liftweb.util.BindHelpers._

  def linkHref = {
    "a [href]" #> {
      val port = CurrentReq.value.request.serverPort match {
        case 80 => ""
        case p => ":" + p.toString
      }
      val redirect_uri = CurrentReq.value.request.scheme + "://" + CurrentReq.value.request.serverName + port + "/oauth2callback"
      "https://accounts.google.com/o/oauth2/auth?response_type=code&" +
        "client_id=" + Props.get("gmail_oauth_client", "") + "&" +
        "redirect_uri=" + redirect_uri + "&access_type=online&" +
        "scope=https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile"
    }
  }

}