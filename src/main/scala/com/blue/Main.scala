package com.blue
import akka.actor._
import com.typesafe.config._
import collection.JavaConversions._
object Main{
  def getKey(key:String,config:Config):String = {
      config.getString("craw."+key)
  }
  def main(args: Array[String]) {
    import collection.JavaConverters._ 
     List(1,2).asJava
     val config = ConfigFactory.load
     if(config.hasPath("craw")){
       val keys = config.getObject("craw").keySet.toSet
       keys foreach{ job=>
           val url= getKey(job+".url",config)
           val fun= getKey(job+".fun",config)
           val inKey= getKey(job+".inKey",config)
           val extra = (new com.twitter.util.Eval).apply[xml.Node=>String](fun)
           val context = ActorSystem("app")
           val rev = context.actorOf(Props[RecvParse])
           rev ! Parameter(url,extra,new NetCraw.MySql(""),inKey)
      }
     }
  }
}
