package com.squeakybeaker.order

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.squeakybeaker.order.model.Entity.ItemType

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
@RunWith(classOf[JUnitRunner])
class DatasourceTest extends FlatSpec {

  behavior of "Datasource"

  it should "read list of soups from website" in {
    val soups = Datasource.Datasources.getData(ItemType.Soup)
    assert(soups.isDefined)
    assert(3 === soups.get.length, soups)
  }

  it should "read list of specials from website" in {
    val specials = Datasource.Datasources.getData(ItemType.Special)
    assert(specials.isDefined)
    assert(1 === specials.get.length, specials)
  }

  it should "read list of sandwiches from website" in {
    val sandwiches = Datasource.Datasources.getData(ItemType.Sandwich)
    assert(sandwiches.isDefined)
    assert(StaticData.sandwiches === sandwiches.get)
  }

}
