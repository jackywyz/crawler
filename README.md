###Crawler web message in scala with htmlparser and xpath

* this is a usage example:

```scala
object test{
import NetCraw._
class Te(f:String) extends Outer[(String,Int)]{
      def out(s:(String,Int))={
        println(s)
      }
}


 val url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile="
  def main (args: Array [String]) {
    def extra(elem:Node)={
      val nd =  (elem  \\ "table"\\ "td") .filter{node =>( node \ "@class").text=="tdc2"}
      nd(1).text
    } 
       //using the akka actor craw 
       val context = ActorSystem("app")
       val rev = context.actorOf(Props[RecvParse])
       // should config the `craw.cname.url` and `craw.cname.fun`(extracting xml node function) in the `application.conf` 
       rev ! Message("cname",new Te("out.txt")) 

       //directly call the parsing method
       while(true){
         NetCraw.netParse(url,extra _,new Fi("out.txt"))
         Thread.sleep(1*60*100*1000)
       } 
      
}
}

```

* config the craw properties in the `application.conf`:

```properties
craw{
   name1{
     inKey="" #the input key of url
     url=""
     fun=""
   }
       name2.inKey="select key from keys"
       name2.url="http://www.ip138.com:8080/search.asp?action=mobile&mobile=13845623"
       name2.fun =" (elem:xml.Node)=>{ val nd =  (elem  \\\\ \"table\" \\\\ \"td\") .filter{node =>( node \\ \"@class\").text==\"tdc2\"}; (nd(1).text,2)}"
}

#should use the '\' to escape the string "  and \
```

* if you want specail output ,you can:

```scala
class NoSql(url:String) extends Outer[T]{
   def out(ret:T)  {
    //TODO
   }
}

```

###Requirement
* scala2.9.2
* xsbt-0.12
