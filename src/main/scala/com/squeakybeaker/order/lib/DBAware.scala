package com.squeakybeaker.order.lib

import scala.slick.session.{Session, Database}

/**
 * User: Eugene Dzhurinsky
 * Date: 6/25/13
 */
trait DBAware {

  val dao = DB.profileSettings._3

  implicit def session: Session = Database.threadLocalSession

}