import java.net._
import scala.xml._
import org.xml.sax.InputSource
import parsing._

object NetCraw{
 def netParse(url:String,extractor:Node=>String,out:Any,suffix:String=""):String={
    val source = new org.xml.sax.InputSource(url+suffix)
    val adapter = new XPATHParser 
    val elem = adapter.loadXML(source)
    extractor(elem)
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

def crawPhone(mysql:OpMySql,list:List[String]){
     list foreach{ ph=>
      val  phone = ph drop 2 
      val source = new org.xml.sax.InputSource(url+phone)
      val adapter = new XPATHParser 
      val elem = adapter.loadXML(source)
      val node= (elem \\ "table"\\ "td") .filter{node =>( node \ "@class").text=="tdc2"}
      val areas = node(1).text 
      val area = area_map find { ar =>areas contains(ar._1)}
      val ar = (areas toList) find {_.toByte == -96} 
      val as= if(ar !=None)areas split ar.get else null

      println(ph)
   } 
  }

 val url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="
  def main (args: Array [String]) {
      while(true){
        crawPhone(url,null)
        Thread.sleep(1*60*100*1000)
      } 
      
}
}
