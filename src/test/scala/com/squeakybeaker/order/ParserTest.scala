package com.squeakybeaker.order

import org.scalatest.FlatSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.squeakybeaker.order.model.Entity.{OrderItemView, ItemType}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
@RunWith(classOf[JUnitRunner])
class ParserTest extends FlatSpec {

  behavior of "Parser"

  import Parser.Parsers.getParser

  it should "correctly parse soups" in {
    val parser = getParser(ItemType.Soup)
    val soups = parser.parse(classOf[ParserTest].getResourceAsStream("/index.html"))
    assert(StaticData.soups === soups)
  }

  it should "correctly parse specials" in {
    val parser = getParser(ItemType.Special)
    val specials = parser.parse(classOf[ParserTest].getResourceAsStream("/index.html"))
    assert(Seq(OrderItemView(ItemType.Special, "Mac and Cheese with Ham and Greens Salad 10.95")) === specials)
  }

  it should "correctly parse sandwiches" in {
    val parser = getParser(ItemType.Sandwich)
    val sandwiches = parser.parse(classOf[ParserTest].getResourceAsStream("/sandwich.html"))
    assert(StaticData.sandwiches === sandwiches)
  }

  it should "correctly parse salads" in {
    val parser = getParser(ItemType.Salad)
    val salads = parser.parse(classOf[ParserTest].getResourceAsStream("/sandwich.html"))
    assert(StaticData.salads === salads)
  }

}