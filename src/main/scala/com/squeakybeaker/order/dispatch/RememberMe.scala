package com.squeakybeaker.order.dispatch

import com.squeakybeaker.order.lib.{UserSession, DBAware}
import java.util.UUID
import net.liftweb.http.provider.{HTTPCookie, HTTPRequest}
import net.liftweb.common.Full
import org.slf4j.LoggerFactory
import com.squeakybeaker.order.model.Entity.User
import net.liftweb.http.{S, LiftSession}

/**
 * User: Eugene Dzhurinsky
 * Date: 6/28/13
 */
object RememberMe extends DBAware {

  val LOG = LoggerFactory.getLogger("com.squeakybeaker.order.dispatch.RememberMe")

  private val cookieMame = "cc4b55f955a498bf7f2302eeaa795b98"

  def proceedCookie(sess: LiftSession) = {
    LOG.debug("Processing user request")
    val req = S.request.get
    req.cookies.filter(_.name == cookieMame).foreach {
      v =>
        LOG.debug("Found cookie {}", v)
        dao.lookup(UUID.fromString(v.value.get)).foreach {
          u =>
            LOG.debug("Found user", u)
            UserSession.set(u)
        }
    }
  }

  private def nextDate = (System.currentTimeMillis() / 1000 + 30 * 24 * 3600)

  def userCookie(u: User) = {
    val cookie = HTTPCookie(
      cookieMame, u.id.toString).
      copy(maxAge = Full(nextDate.toInt))
    LOG.debug("Setting cookie: {}", cookie)
    cookie
  }

}