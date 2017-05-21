package fun.scala

import org.scalajs.dom.ext.Ajax

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalajs.concurrent.JSExecutionContext.Implicits.queue

@JSExportTopLevel("fun.scala.Client")
case class Client(baseUrl: String = "http://localhost:8080") {
  @JSExport
  def next(): js.Promise[Any] = {
    Ajax.get(url("/next"), headers = Map("Content-Type" -> "application/json"))
      .map { xhr =>
        import io.circe.parser.decode, fun.scala.Video._
        decode[Video](xhr.responseText) match {
          case Right(data) => data
          case Left(_) => js.undefined
        }
      }
  }.toJSPromise

  private def url(path: String): String = baseUrl + path
}
