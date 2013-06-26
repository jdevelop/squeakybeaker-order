package com.squeakybeaker.order.dispatch

import net.liftweb.common.Box
import net.liftweb.http.{RedirectResponse, LiftResponse, S}
import org.slf4j.LoggerFactory
import com.squeakybeaker.order.lib.UserSession
import com.squeakybeaker.order.authentication.OAuth2Google

/**
 * User: Eugene Dzhurinsky
 * Date: 6/12/13
 */
object VerifyUserToken {

  val LOG = LoggerFactory.getLogger("com.jdevelop.dispatch.VerifyUserToken")

  type TokenResolver = (String) => Option[String]

  def verifyGoogleOauthToken(login: OAuth2Google, resolver: Option[TokenResolver] = None)(): Box[LiftResponse] = {
    val code = S.param("code")
    LOG.debug("Working with token: {}", code)
    code.map {
      token =>
        resolver.map(_.apply(token)).getOrElse(Some(token)).map {
          accessToken =>
            login.login(accessToken).map {
              u => UserSession.set(u)
                RedirectResponse("/order")
            }.getOrElse(RedirectResponse("/login"))
        }.get
    }
  }

}