package com.squeakybeaker.order.authentication

import net.liftweb.json.JsonAST.JObject
import org.slf4j.LoggerFactory
import com.squeakybeaker.order.model.{Entity, DAO}
import com.squeakybeaker.order.model.Entity.User
import com.squeakybeaker.order.lib.DBAware

/**
 * User: Eugene Dzhurinsky
 * Date: 6/11/13
 */
class OAuth2Google(dao: DAO) extends OAuth2 with DBAware {

  this: OAuthTransport =>

  import OAuth2Google.LOG


  override def login(token: String): Option[User] = {
    val url: String = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + token
    import net.liftweb.json.JsonParser._
    try {
      val mapVals = parse(retrievePage(url).mkString).asInstanceOf[JObject].values
      val email = mapVals.get("email").get.asInstanceOf[String]
      val name = mapVals.get("name").getOrElse("Anonymous")
      val userProfile = dao.lookup(email)
      Some(userProfile.getOrElse {
        val newUser = dao.createNew(email, name.toString)
        newUser
      })
    }
    catch {
      case e: Exception =>
        LOG.error("Can not authenticate user", e)
        None
    }
  }

}

object OAuth2Google {

  val LOG = LoggerFactory.getLogger(classOf[OAuth2Google])

  case class GoogleConfig(redirect: String, cid: String, secret: String)

  def requestAccessToken(c: GoogleConfig)(token: String): Option[String] = {
    import scalaj.http._
    LOG.debug("Getting access token: {}", token)
    try {
      val response = Http.post("https://accounts.google.com/o/oauth2/token").params(
        "code" -> token,
        "redirect_uri" -> c.redirect,
        "client_id" -> c.cid,
        "client_secret" -> c.secret,
        "grant_type" -> "authorization_code"
      ).asString
      LOG.debug("Oauth response: {}", response)
      import net.liftweb.json.JsonParser._
      val mapVals = parse(response).asInstanceOf[JObject].values
      mapVals.get("access_token").map(_.toString)
    } catch {
      case e: Exception =>
        LOG.error("Can't retrieve oauth token", e)
        None
    }
  }

}