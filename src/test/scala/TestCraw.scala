package com.blue
import com.twitter.io._
import com.twitter.util._
import NetCraw._
import akka.actor._
import scala.xml._
class Te(f:String) extends Outer[(String,Int)]{
      def out(s:(String,Int))={
        println(s)
      }
}

object test{
    implicit val system = akka.actor.ActorSystem()
    def log = system.log

    val url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="

    def main (args: Array [String]) {
          def extra(elem:Node)={
            val nd =  (elem  \\ "table"\\ "td") .filter{node =>( node \ "@class").text=="tdc2"}
            nd(1).text
          }
          
          //using the akka actor craw 
          val context = ActorSystem("app")
          val rev = context.actorOf(Props[RecvParse])
          rev ! Message("name",new Te("out.txt")) 

          //directly call the parse method
          while(true){
            NetCraw.netParse(url,extra _,new Fi("out.txt"))
            Thread.sleep(1*60*100*1000)
          } 
          
    }
}
