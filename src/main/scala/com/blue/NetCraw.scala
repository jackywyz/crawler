package com.blue
import java.net._
import scala.xml._
import parsing._
import org.scalaquery.ql.extended.MySQLDriver.Implicit._ 
import org.scalaquery.simple.{GetResult, StaticQuery => Q}
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.session._

object NetCraw{
import java.io._
import scala.io._
implicit def enrichFile( file: File ) = new RichFile( file )

class RichFile( file: File ) {

  def text = Source.fromFile( file ).mkString

  def text_=( s: String ) {
    val out = new PrintWriter( file )
    try{ out.print( s ) }
    finally{ out.close }
  }
}

trait Outer[T]{ def out(p:T):Unit }
class Fi(p:String) extends Outer[String]{
  def out(ret:String){
      val file = new File(p)
      file.text = ret 
}
}

class MySql(url:String) extends Outer[String]{
  val db =Database.forURL(url,driver="com.mysql.jdbc.Driver")
  val insql = Q[(Int)]
  def out(sql:String){
    db withSession{
       (insql+sql).first
   }      
  }
}

def netParse[T](url:String,extractor:Node=>T,out:Outer[T]=null,delay:Int=0,suffix:String=""):T={
    val source = new org.xml.sax.InputSource(url+suffix)
    val adapter = new XPATHParser 
    val ret:T = extractor(adapter.loadXML(source))

    if(out !=null) out.out(ret) 
    Thread.sleep(delay*1000)
    ret 
 }

}

class XPATHParser extends NoBindingFactoryAdapter {

  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource) = {
    import nu.validator.htmlparser.{sax,common}
    import sax.HtmlParser
    import common.XmlViolationPolicy

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }
}

case class Message[T](taskName:String,out:NetCraw.Outer[T])
class RecvParse extends akka.actor.Actor{
  val config = com.typesafe.config.ConfigFactory.load
  val log = akka.event.Logging(context.system, this)
  def receive = {
    case Message(name,out)=>
       val conf = config.getConfig("craw")
       val url = conf.getString(name+".url") 
       val fun= conf.getString(name+".fun")
       val extra = (new com.twitter.util.Eval).apply[Node=>Any](fun)
       val ret = NetCraw.netParse(url,extra,out)
       log.info(ret toString) 
    case _ =>
  }
}
