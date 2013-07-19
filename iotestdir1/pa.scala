import scala.io.Source

class Value {
    var sum = 1
    def add(y:Int): Unit = sum += y
  }
  
  var moneyCounter = new Value 
  moneyCounter add 12  
  println(moneyCounter.sum)