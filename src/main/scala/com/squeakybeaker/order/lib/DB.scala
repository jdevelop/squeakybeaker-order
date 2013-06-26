package com.squeakybeaker.order.lib

import scala.slick.driver.H2Driver
import scala.slick.session.Database
import net.liftweb.util.Props
import net.liftweb.util.Props.RunModes
import com.squeakybeaker.order.model.DAO

/**
 * User: Eugene Dzhurinsky
 * Date: 6/16/13
 */
object DB {

  lazy val profileSettings = {

    Props.mode match {
      case RunModes.Development =>
        val db = H2Driver.profile

        val dbAccess = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        val access = new DAO(db)

        dbAccess withSession {
          try {
            access.create
          } catch {
            case e: Exception =>
          }
        }
        (db, dbAccess, access)
      case RunModes.Staging =>
        val db = H2Driver.profile

        val dbAccess = Database.forURL("jdbc:h2:file:test1;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        val access = new DAO(db)

        dbAccess withSession {
          try {
            access.create
          } catch {
            case e: Exception =>
          }
        }
        (db, dbAccess, access)
    }


  }

}