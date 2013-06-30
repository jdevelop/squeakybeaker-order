package com.squeakybeaker.order.model

import scala.slick.driver.ExtendedProfile
import com.squeakybeaker.order.lib.DBAware

/**
 * User: Eugene Dzhurinsky
 * Date: 6/25/13
 */
case class DAO(profile: ExtendedProfile)
  extends Entity.UserOrderLink with Entity.OrdersPersistence with Entity.UserPersistence with DBAware
  with DatabaseProfile {

  def create = {
    import profile.simple._
    (UserP.ddl ++ OrderItemP.ddl).create
  }

}