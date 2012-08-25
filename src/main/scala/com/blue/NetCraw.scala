/*
 * Copyright (A) 2011-2012 jackywyz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blue
import java.net._
import scala.xml._
import parsing._
import org.scalaquery.ql.extended.MySQLDriver.Implicit._ 
import org.scalaquery.simple.{GetResult, StaticQuery => Q}
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.session._
import akka.actor._

object NetCraw{
import java.io._
import scala.io._
implicit def enrichFile( file: File ) = new RichFile( file )

class RichFile( file: File ) {
  def text = Source.fromFile( file ).getLines.toList

  def text_=( s: String ) {
    val out = new PrintWriter(new FileWriter(file,true ))
    try{ out.println( s ) }
    finally{ out.close }
  }
}

trait Outer[T]{ 
  def from(p:String):List[String] 
  def out(p:T):Unit 
}

class Fi(p:String) extends Outer[String]{
  def out(ret:String){
      val file = new File(p)
      file.text = ret 
 }
  def from(name:String):List[String]={
     val file = new File(name)
     file.text
  }
}

class MySql(url:String) extends Outer[String]{
  val db =Database.forURL(url,driver="com.mysql.jdbc.Driver")
  val insql = Q[(Int)]
  val ssql = Q[String]

  def out(sql:String){
    db withSession{
       (insql+sql).first
   }      
  }
  def from(sql:String):List[String]={
    db withSession{
      (ssql+sql).list
    } 
  }
}

def netParse[T](url:String,extractor:Node=>T,out:Outer[T]=null,delay:Int=0):T={
    val source = new org.xml.sax.InputSource(url)
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
case class Parameter[T](url:String,extra:Node=>T,out:NetCraw.Outer[T],inKey:String=null)
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
    case Parameter(url,extra,out,inKey)=>
       if(inKey !=null) {
        val keys = out.from(inKey)
        keys foreach{ key=>
          self ! Parameter(url+key,extra,out) 
        }
       }else{
          val ret = NetCraw.netParse(url,extra,out)
          log.info(ret toString) 
       }
    case _ =>
  }
}
