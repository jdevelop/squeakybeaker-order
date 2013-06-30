package com.squeakybeaker.order

import com.squeakybeaker.order.model.Entity.OrderItemView
import com.squeakybeaker.order.model.Entity.ItemType._

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object StaticData {

  val sandwiches = Seq(
    OrderItemView(Sandwich, "#1 Grilled Chicken with pesto, tomato and fresh mozzarella"),
    OrderItemView(Sandwich, "#2 Roast Beef with greens, boursin cheese, horseradish mayonnaise and roasted red peppers"),
    OrderItemView(Sandwich, "#3 Grilled Reuben - corned beef with sauerkraut, swiss cheese, and russian dressing"),
    OrderItemView(Sandwich, "#4 Grilled Cubano – roast pork loin, ham, swiss, mustard, mayo, relish, and hot peppers"),
    OrderItemView(Sandwich, "#5 Mom’s Meatball Grinder - with melted provolone"),
    OrderItemView(Sandwich, "#6 Vegetarian Sandwich with hummus, tomato, red onion, cucumber, sprouts, avocado and feta"),
    OrderItemView(Sandwich, "#7 Chicken Salad with granny smith apples and lettuce"),
    OrderItemView(Sandwich, "#8 Grilled Vegetable Panini grilled eggplant zucchini, goat cheese, roasted red peppers and garlic aioli"),
    OrderItemView(Sandwich, "#9 Chicken Melt – grilled chicken, melted cheddar, avocado, honey mustard, mayo, lettuce, tomato and red onion"),
    OrderItemView(Sandwich, "#10 The Rachel – grilled turkey, melted swiss, russian dressing and homemade coleslaw"),
    OrderItemView(Sandwich, "#11 The Italian – imported genoa salami, mortadella, viginia ham, melted provolone, lettuce, tomato, pickles, onions, mayo, dijon, extra virgin olive oil and vinegar"),
    OrderItemView(Sandwich, "#12 Tuna Melt – our homemade tuna salad with slices of tomato and melted swiss"))

  val soups = Seq(OrderItemView(Soup, "Spicy Red Kidney Bean (Vegan, Gluten Free)"),
    OrderItemView(Soup, "Indian Chicken Soup (Gluten Free, Lactose Free)"),
    OrderItemView(Soup, "North African Cauliflower (Vegan, Gluten Free)"))

  val salads = Seq(
    OrderItemView(Salad,"Greek Salad romaine lettuce, tomatoes, red onion, cucumbers, kalamata olives, imported feta and our homemade lemon vinaigrette 7.01"),
    OrderItemView(Salad,"Classic Caesar Salad romaine lettuce, imported parmesan, homemade caesar dressing 6.25"),
    OrderItemView(Salad,"Mixed Green Salad with tomato, cucumber, red onion and our homemade dijon vinaigrette 6.54"),
    OrderItemView(Salad,"Spinach Salad with bleu cheese, toasted walnuts, pears, and our own homemade bacon sherry vinaigrette 7.71"),
    OrderItemView(Salad,"Cobb Salad our mixed greens salad with avocado, bleu cheese, bacon, egg, grilled chicken and homemade balsamic vinaigrette 8.25")
  )

}