package com.squeakybeaker.order

import org.scalatest.FlatSpec
import com.squeakybeaker.order.Entity.Orders.{OrderItem, ItemType}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

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
    assert(Seq(OrderItem(ItemType.Soup, "Spicy Red Kidney Bean (Vegan, Gluten Free)"),
      OrderItem(ItemType.Soup, "Indian Chicken Soup (Gluten Free, Lactose Free)"),
      OrderItem(ItemType.Soup, "North African Cauliflower (Vegan, Gluten Free)")) === soups)
  }

  it should "correctly parse specials" in {
    val parser = getParser(ItemType.Special)
    val specials = parser.parse(classOf[ParserTest].getResourceAsStream("/index.html"))
    assert(Seq(OrderItem(ItemType.Special, "Mac and Cheese with Ham and Greens Salad 10.95")) === specials)
  }

  it should "correctly parse sandwiches" in {
    val parser = getParser(ItemType.Sandwich)
    val sandwiches = parser.parse(classOf[ParserTest].getResourceAsStream("/sandwich.html"))
    import ItemType.Sandwich
    assert(Seq(
      OrderItem(Sandwich,"#1 Grilled Chicken with pesto, tomato and fresh mozzarella"),
      OrderItem(Sandwich,"#2 Roast Beef with greens, boursin cheese, horseradish mayonnaise and roasted red peppers"),
      OrderItem(Sandwich,"#3 Grilled Reuben - corned beef with sauerkraut, swiss cheese, and russian dressing"),
      OrderItem(Sandwich,"#4 Grilled Cubano – roast pork loin, ham, swiss, mustard, mayo, relish, and hot peppers"),
      OrderItem(Sandwich,"#5 Mom’s Meatball Grinder - with melted provolone"),
      OrderItem(Sandwich,"#6 Vegetarian Sandwich with hummus, tomato, red onion, cucumber, sprouts, avocado and feta"),
      OrderItem(Sandwich,"#7 Chicken Salad with granny smith apples and lettuce"),
      OrderItem(Sandwich,"#8 Grilled Vegetable Panini grilled eggplant zucchini, goat cheese, roasted red peppers and garlic aioli"),
      OrderItem(Sandwich,"#9 Chicken Melt – grilled chicken, melted cheddar, avocado, honey mustard, mayo, lettuce, tomato and red onion"),
      OrderItem(Sandwich,"Add Bacon 2.00"),
      OrderItem(Sandwich,"#10 The Rachel – grilled turkey, melted swiss, russian dressing and homemade coleslaw"),
      OrderItem(Sandwich,"#11 The Italian – imported genoa salami, mortadella, viginia ham, melted provolone, lettuce, tomato, pickles, onions, mayo, dijon, extra virgin olive oil and vinegar"),
      OrderItem(Sandwich,"#12 Tuna Melt – our homemade tuna salad with slices of tomato and melted swiss")) === sandwiches)
  }

}