package com.squeakybeaker.order

import java.io.InputStream
import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Document}
import com.squeakybeaker.order.model.Entity.{OrderItemView, ItemType, OrderItem}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Parser {


  trait MenuParser {

    def parse(is: InputStream): Seq[OrderItemView]

  }

  object Parsers {

    implicit def streamToJSoup(is: InputStream): Document = Jsoup.parse(is, "UTF-8", "http://www.squeakybeaker.com/")

    import collection.JavaConversions._

    abstract class JsoupParser(expr: String, kind: ItemType.Value) extends MenuParser {
      def parse(is: InputStream): Seq[OrderItemView] = {
        is.select(expr).filter(txt => !txt.text().isEmpty && !txt.text().startsWith("Add")).map {
          case el: Element => OrderItemView(kind, el.text())
        }
      }
    }

    private object SoupP extends JsoupParser("h2:has(strong:contains(Soups:)) ~ div, h2:has(strong:contains(Soups:)) ~ p", ItemType.Soup)

    private object SaladP extends JsoupParser("p:has(strong:contains(Salads)) ~ p", ItemType.Salad)

    private object SpecialP extends JsoupParser("h4:contains(Lunch Special) + p", ItemType.Special)

    private object SandwichP extends JsoupParser("td:has(h4 > span:contains(Our Top Twelve)) > p", ItemType.Sandwich)

    def getParser(kind: ItemType.Value): MenuParser = {
      kind match {
        case ItemType.Soup => SoupP
        case ItemType.Sandwich => SandwichP
        case ItemType.Special => SpecialP
        case ItemType.Salad => SaladP
      }
    }

  }

}