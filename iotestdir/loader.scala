/*мой код выглядит так:*/

import ru.infocrypt.crypto._
class HelloWorld {

	Class.forName("ru.infocrypt.bicrypt.Bicrypt").
  		newInstance().asInstanceOf[ { def loadLibrary }].loadLibrary

	println("Version: " + Bicrypt.getVersionInfo)	
}