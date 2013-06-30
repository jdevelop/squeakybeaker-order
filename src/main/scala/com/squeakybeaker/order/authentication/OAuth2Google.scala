package com.squeakybeaker.order.authentication

import net.liftweb.json.JsonAST.JObject
import org.slf4j.LoggerFactory
import com.squeakybeaker.order.model.DAO
import com.squeakybeaker.order.model.Entity.User
import com.squeakybeaker.order.lib.DBAware
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair

/**
 * User: Eugene Dzhurinsky
 * Date: 6/11/13
 */
class OAuth2Google(dao: DAO) extends OAuth2 with DBAware {

  this: OAuthTransport =>

  import OAuth2Google.LOG


  override def login(token: String): Option[User] = {
    val url: String = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=" + token
    LOG.debug("Accessing url: {}" + url)
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

  private val poolingConnMgr = new PoolingClientConnectionManager()

  private val httpClient: HttpClient = new DefaultHttpClient(poolingConnMgr)

  val LOG = LoggerFactory.getLogger(classOf[OAuth2Google])

  case class GoogleConfig(redirect: String, cid: String, secret: String)

  def requestAccessToken(c: GoogleConfig)(token: String): Option[String] = {
    LOG.debug("Getting access token: {}", token)
    var request : HttpPost = null
    try {
      val url = "https://accounts.google.com/o/oauth2/token"
      val prms = List(
        "code" -> token,
        "redirect_uri" -> c.redirect,
        "client_id" -> c.cid,
        "client_secret" -> c.secret,
        "grant_type" -> "authorization_code"
      )
      LOG.debug("Sending request to {} => {}", url, prms)
      import collection.JavaConverters._

      val nvps = prms.map {
        case (n, v) => new BasicNameValuePair(n, v)
      }.asJava

      request = new HttpPost(url)

      request.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"))

      val response = httpClient.execute(request)

      val content = response.getStatusLine.getStatusCode match {
        case HttpStatus.SC_OK => Some(response.getEntity.getContent)
        case _ =>
          LOG.error("Request resulted in ", io.Source.fromInputStream(response.getEntity.getContent).mkString)
          None
      }
      content.flatMap {
        v =>
          val oauthResp = io.Source.fromInputStream(v).mkString
          LOG.debug("Oauth response: {}", oauthResp)
          import net.liftweb.json.JsonParser._
          val mapVals = parse(oauthResp).asInstanceOf[JObject].values
          mapVals.get("access_token").map(_.toString)
      }
    } catch {
      case e: Exception =>
        LOG.error("Can't retrieve oauth token", e)
        None
    } finally {
      Option(request).foreach(_.abort())
    }
  }

}