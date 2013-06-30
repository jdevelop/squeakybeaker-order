package com.squeakybeaker.order

import java.io.InputStream
import org.apache.http.impl.conn.PoolingClientConnectionManager
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import com.squeakybeaker.order.model.Entity.{OrderItemView, OrderItem, ItemType}

/**
 * User: Eugene Dzhurinsky
 * Date: 4/14/13
 */
object Datasource {

  trait DataProvider {

    def stream[T](f: InputStream => T): Option[T]

  }

  object Datasources {

    private val poolingConnMgr = new PoolingClientConnectionManager()

    private val httpClient: HttpClient = new DefaultHttpClient(poolingConnMgr)

    private class HttpSource(url: String) extends DataProvider {
      def stream[T](f: InputStream => T): Option[T] = {
        val request = new HttpGet(url)
        try {
          val response = httpClient.execute(request)
          response.getStatusLine.getStatusCode match {
            case HttpStatus.SC_OK => Some(f(response.getEntity.getContent))
            case _ => None
          }
        } finally {
          request.abort()
        }
      }
    }

    def getData(kind: ItemType.Value): Option[Seq[OrderItemView]] = {
      import ItemType._
      val stream = kind match {
        case Soup => new HttpSource("http://www.squeakybeaker.com/")
        case Special => new HttpSource("http://www.squeakybeaker.com/")
        case Sandwich => new HttpSource("http://www.squeakybeaker.com/?page_id=91")
        case Salad => new HttpSource("http://www.squeakybeaker.com/?page_id=91")
      }
      stream.stream(Parser.Parsers.getParser(kind).parse)
    }

  }

}