class Rational(n: Int, d: Int) {
	require(d != 0)
	
	private val g = gcd(n.abs, d.abs)
	
	val numer: Int = n / g
	val denom: Int = d / g
	
	def this(n: Int) = this(n,1)
	
	override def toString = numer +"/"+ denom
	
	def + (i: Int): Rational = new Rational(numer + i * denom, denom)
	
	def + (that: Rational): Rational = {
		new Rational(
			numer * that.denom + that.numer * denom,
			denom * that.denom
		)
	}

	def - (i: Int) = new Rational(numer - i*denom, denom)
	
	def - (that: Rational): Rational = 
		new Rational(
			numer * that.denom - that.numer * denom,
			denom * that.denom
		)
	
	def * (that: Rational): Rational = {
		new Rational( 
			numer * that.numer, 
			denom * that.denom
		)
	}
	
	private def gcd(a: Int , b : Int): Int ={
	if (b==0) 
		a 
	else 
		gcd(b , a % b)
	}


}

implicit def intToRational(x: Int) = new Rational(x)

val x = new Rational(5,7)
val y = new Rational(7,5)

println( x - 1 )
println( x -  new Rational(1) )


println(2 * new Rational(1,2))