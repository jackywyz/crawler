###Crawler web message in scala with htmlparser and xpath

* this is a usage example:

```scala
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

```
* if you want specail output ,you can:

```scala
class MySql[T] extends Outer[T]{
   def out(ret:T)  {
    //TODO
   }
}

```

###Requirement
* scala2.9.2
* xsbt-0.11.3
