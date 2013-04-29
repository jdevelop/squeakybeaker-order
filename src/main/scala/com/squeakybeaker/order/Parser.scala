package com.squeakybeaker.order

import com.squeakybeaker.order.model.Entity
import Entity.Orders.{ItemType, OrderItem}
import java.io.InputStream
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Parser {


  trait MenuParser {

    def parse(is: InputStream): Seq[OrderItem]

  }

  object Parsers {

    implicit def streamToJSoup(is: InputStream): Document = Jsoup.parse(is, "UTF-8", "http://www.squeakybeaker.com/")

    import collection.JavaConversions._

    abstract class JsoupParser(expr: String, kind: ItemType.Value) extends MenuParser {
      def parse(is: InputStream): Seq[OrderItem] = {
        is.select(expr).map {
          case el: Element => OrderItem(kind, el.text())
        }
      }
    }

    private object SoupP extends JsoupParser("h2:has(strong:contains(Soups:)) ~ div", ItemType.Soup)

    private object SpecialP extends JsoupParser("h4:contains(Lunch Special) + p", ItemType.Special)

    private object SandwichP extends JsoupParser("td:has(h4 > span:contains(Our Top Twelve)) > p", ItemType.Sandwich)

    def getParser(kind: ItemType.Value): MenuParser = {
      kind match {
        case ItemType.Soup => SoupP
        case ItemType.Sandwich => SandwichP
        case ItemType.Special => SpecialP
      }
    }

  }

}
