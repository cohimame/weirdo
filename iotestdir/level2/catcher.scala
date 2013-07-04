import java.net.URL
import java.net.MalformedURLException

def urlFor(path: String) =
	try {
		new URL(path)
	} catch {
		case e: MalformedURLException =>
			new URL("http://www.scala-lang.org")
}

println(urlFor(".ru"))

val arg = "qw"

val matchResult = arg match {
	case "" => ""
	case "qw"  => "victory!"
	case _ => "you fail"
}

println(matchResult)