def exp = "x*3+y*4-z*7 <= 16"
	
println "Match " + exp

//pattern = ~/("<="|">="|"=="|"<"|">"|"!="|"=")/
pattern = ~/(<=|>=|==|<|>|!=|=)/
matcher = pattern.matcher(exp) 
matcher.each { match -> println match }
//matcher.split { list -> println list}

opers = []
matcher.each { match -> opers << match } 
println "Opers: " + opers

println "Split " + exp
//exp.trim()
operators = /(<=|>=|==|<|>|!=|=)/
parts = exp.split(operators)
parts.each { println it.trim()}

operands = split(exp,{operators} )
operands.each { println it }