import java.net._
import scala.xml._
import org.xml.sax.InputSource
import parsing._
import java.sql.{Connection, DriverManager, ResultSet}

class OpMySql(user:String,passwd:String,port:Int=3306,db:String="fee"){
  def getConn()= {
    val dbc = "jdbc:mysql://localhost:"+port+"/"+db+"?user="+user+"&password="+passwd
   classOf[com.mysql.jdbc.Driver]
   val conn = DriverManager.getConnection(dbc)
   val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
   conn
  } 
 
  def select(sql:String):List[String]={
   val list = collection.mutable.ListBuffer.empty[String] 
   val conn = this getConn()
   val statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
  try {
    val rs= statement.executeQuery(sql)
    while(rs.next) list+=rs.getString("phone") 
  }
  finally {
    conn.close
  }
  list toList
  }

  def update(sql:String)={
    val conn = this getConn()
    try {
     val prep = conn.prepareStatement(sql)
     prep.executeUpdate
    }finally{ conn.close }
  }
}


object NetCraw{
 val url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="
 val AREA = List("北京", "上海", "天津", "重庆", "四川", 
     "广东", "广西", "浙江", "江苏", "江西", 
     "湖北", "湖南", "山东", "山西", "河南", 
     "河北", "黑龙江", "吉林", "辽宁", "安徽", 
     "福建", "陕西", "云南", "贵州", "海南", 
     "青海", "宁夏", "内蒙古", "甘肃", "新疆", "西藏")

val  AREA_CODE = List( "01", "02", "03", "04", "05", 
     "06", "07", "08", "09", "10", 
     "11", "12", "13", "14", "15", 
     "16", "17", "18", "19", "20", 
     "21", "22", "23", "24", "25", 
     "26", "27", "28", "29", "30", "31") 

val area_map= AREA zip AREA_CODE

  def netParse(su:String):Elem={
    var url = new URL(su)
    var conn = url.openConnection

    XML.load(conn.getInputStream)
  }

  def crawPhone(mysql:OpMySql,list:List[String]){
     list foreach{ ph=>
      val  phone = ph slice (2,ph.length-1)
      val source = new org.xml.sax.InputSource(url+phone)
      val adapter = new HTML5Parser 
      val elem = adapter.loadXML(source)
      val node= (elem \\ "table"\\ "td") .filter{node =>( node \ "@class").text=="tdc2"}
      val areas = node(1).text 
      val area = area_map find { ar =>areas contains(ar._1)} 
      if(area.get != None) { 
      mysql update("insert into  sms_phone(phone,provcode,province) values('"+ph+"','"+area.get._2 +"','"+area.get._1+"')")
      mysql update("delete from phones where phone="+ph) 
     } 
   } 
  }
  def main (args: Array [String]) {
      val mysql = new OpMySql("jacky", "tomas")
      while(true){
        val list = mysql select("select phone from phones" )
        crawPhone(mysql,list)
        Thread.sleep(2*60*60*1000)
      } 
      
      }   
  }

class HTML5Parser extends NoBindingFactoryAdapter {

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

