object SendMory {
	
  def main(args : Array[String]) : Unit = {
	  val		problem = new CPScalaProblem("SENDMORY")
	  
	  var		S = problem makeVar("S", 1, 9)
	  var		E = problem makeVar("E", 0, 9)
	  var		N = problem makeVar("N", 0, 9)
	  var		D = problem makeVar("D", 0, 9)
	   
	  var		M = problem makeVar("M", 1, 9)
	  var		O = problem makeVar("O", 0, 9)
	  var		R = problem makeVar("R", 0, 9)
	  var		Y = problem makeVar("Y", 0, 9)
	   
	  problem allDiff (S, E, N, D, M, O, R, Y)
	  
	   var		SENDMOREMONEY = Array(S, E, N, D, M, O, R, E, M, O, N, E, Y)
	   var		coeff = Array( 1000, 100, 10, 1, 1000, 100, 10, 1, -10000, -1000, -100, -10, -1 );
	   
	   problem linear(coeff, SENDMOREMONEY)
	   
	   if(problem.solution == null) {
	  	   problem log "no solution"
	   } else {
	  	   problem log(S, E, N, D, M, O, R, Y)
	   }
  }
}



