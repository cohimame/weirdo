object Test {

import java.io._

   def demo():Unit ={
      var a = 60;       /* 60 = 0011 1100 */  
      var b = 13;       /* 13 = 0000 1101 */
      var c = 0;

      c = a & b;       /* 12 = 0000 1100 */ 
      println("a & b = " + c );

      c = a | b;           /* 61 = 0011 1101 */
      println("a | b = " + c );

      c = a ^ b;           /* 49 = 0011 0001 */
      println("a ^ b = " + c );

      c = ~a;              /* -61 = 1100 0011 */
      println("~a = " + c );

      c = a << 2;    /* 240 = 1111 0000 */
      println("a << 2 = " + c );

      c = a >> 2;    /* 215 = 1111 */
      println("a >> 2  = " + c );

      c = a >>> 2; /* 215 = 0000 1111 */
      println("a >>> 2 = " + c );
   }

   def crc16():Unit ={
      val divisor = 0x1021
      println("divisor= " + divisor)
   }

   def main(args: Array[String]) {
      //demo()
      //crc16()
      var byteReader = new ByteInputStream
   
   }
} 