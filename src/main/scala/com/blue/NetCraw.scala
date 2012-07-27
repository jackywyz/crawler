package com.blue
import java.net._
import scala.xml._
import parsing._

object NetCraw{
import java.io._
import scala.io._
implicit def enrichFile( file: File ) = new RichFile( file )

trait Outer[T]{ def out(p:T):Unit }
class Fi(p:String) extends Outer[String]{
  def out(ret:String){
      val file = new File(p)
      file.text = ret 
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
class RichFile( file: File ) {

  def text = Source.fromFile( file ).mkString

  def text_=( s: String ) {
    val out = new PrintWriter( file )
    try{ out.print( s ) }
    finally{ out.close }
  }
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

object test{
import NetCraw._
 val url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="
  def main (args: Array [String]) {
    def extra(elem:Node)={
      val nd =  (elem  \\ "table"\\ "td") .filter{node =>( node \ "@class").text=="tdc2"}
      nd(1).text
    } 
      while(true){
        NetCraw.netParse(url,extra _,new Fi("out.txt"))
        Thread.sleep(1*60*100*1000)
      } 
      
}
}
