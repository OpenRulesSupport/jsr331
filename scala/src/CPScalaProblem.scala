import javax.constraints.impl.Problem
import javax.constraints.impl.{Var => CPVar}
import javax.constraints.Solution
import javax.constraints.Oper

class CPScalaProblem(str_name : String) extends Problem{

	def	solution : Solution = this.getSolver().findSolution()
	
	def	makeVar(str_s : String, i_min : Int, i_max : Int) : CPVar = {
		var		lcl_v : CPVar = new CPVar(this, str_s, i_min, i_max)
		this.add(lcl_v)
		lcl_v
	}
	
	def allDiff(cl_cpVar : CPVar*) : Unit = {
			var		arr = cl_cpVar.toArray;
			this allDiff arr
	}
	
	def log(cl_cpVar : CPVar*) : Unit = {
			var		arr = cl_cpVar.toArray;
			this log arr.asInstanceOf[Array[javax.constraints.Var]]
	}
	
	def	allDiff(cla_v : Array[CPVar]) = allDifferent(cla_v.asInstanceOf[Array[javax.constraints.Var]]) post
		
	def	linear(coeff : Array[Int], value : Array[CPVar]) : Unit = linear(coeff.asInstanceOf[Array[Int]], value.asInstanceOf[Array[javax.constraints.Var]], Oper.EQ, 0) post
}