package bootstrap.liftweb

 import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import net.liftweb.util.{Props, LoanWrapper}
import com.squeakybeaker.order.lib.{DB, UserSession}
import net.liftweb.sitemap.Loc.If
import com.squeakybeaker.order.authentication.{OAuthTransport, OAuth2Google}
import com.squeakybeaker.order.dispatch.{Logout, VerifyUserToken}
import com.squeakybeaker.order.model.DAO


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.squeakybeaker.order")

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index",
      Menu("Order") / "order" >> If (UserSession.loggedIn_? _, S ? "Login required"),
      Menu("Login") / "login" >> If (UserSession.anonymous_? _, S ? "Already authenticated"),
      Menu("Logout") / "logout" >> If (UserSession.loggedIn_? _, S ? "")
    )

    val (db, dbAccess, access) = DB.profileSettings

    LiftRules.setSiteMapFunc(() => sitemap())

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => UserSession.loggedIn_?)

    val tokenVerifier = new OAuth2Google(access) with OAuthTransport

    val googleConfig = OAuth2Google.GoogleConfig(
      Props.get("gmail_oauth_callback", ""),
      Props.get("gmail_oauth_client", ""),
      Props.get("gmail_oauth_secret", ""))

    val googleLogin = Some {
      OAuth2Google.requestAccessToken(googleConfig) _
    }

    LiftRules.dispatch.append {
      case Req("oauth2callback" :: Nil, _, _) =>
        VerifyUserToken.verifyGoogleOauthToken(tokenVerifier, googleLogin) _
    }.append {
      case Req("logout" :: Nil, _, _) =>
        Logout.logout _
    }


    LiftRules.allAround.append {
      new LoanWrapper {
        def apply[T](f: => T): T = {
          dbAccess withSession {
            f
          }
        }
      }
    }
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }

  LiftRules.htmlProperties.default.set((r: Req) =>new Html5Properties(r.userAgent))

}
