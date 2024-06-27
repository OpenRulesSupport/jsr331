s = new GroovyShell()
text = """
x=70
println "x= "+x
x
"""



try {
	x = 5+6
	println "Inside "+x
	
	//s.evaluate(text)
	def value = s.evaluate(text) //"println 20; 50")
	println value
}
catch(Exception e) {
println "Didn't work!"
e.printStackTrace()
}

