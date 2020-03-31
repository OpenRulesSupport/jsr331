package javax.constraints.extra;

import java.util.Iterator;
import java.util.Set;

import javax.constraints.Problem;
import javax.constraints.Var;
import javax.constraints.VarSet;
import javax.constraints.impl.AbstractConstraint;
import javax.constraints.impl.BasicVarSet;


/**
 *
 * @author JF
 *
 */

public final class ConstraintElementAtOnSets extends AbstractConstraint {
	private final VarSet setVar;
	private final Var indexVar;
	private final Set[] sets;
	Propagator indexPropagator;

	public ConstraintElementAtOnSets(VarSet setVar, Set[] sets, Var indexVar) {
		super(indexVar.getProblem(), "ConstraintElementAtOnSets");
		this.setVar = setVar;
		this.indexVar = indexVar;
		this.sets = sets;
		indexPropagator = new IndexPropagator();
	}

	@Override
	public void post(){
		((javax.constraints.impl.AbstractVar)indexVar).
			addPropagator(indexPropagator, PropagationEvent.VALUE);
		BasicVarSet imp = (BasicVarSet)setVar;
		Set valueSet = imp.getValueSet();
		Iterator iter = valueSet.iterator();
		while(iter.hasNext()) {
			int val = ((Integer)iter.next()).intValue();
			Var vari;
			try {
				vari = imp.getRequiredVar(val);
				Propagator varPropagator = new VarPropagator( vari, val );
				((javax.constraints.impl.AbstractVar)vari).
					addPropagator(varPropagator, PropagationEvent.VALUE);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	/**
	 * reacts on value event for indexVar
	 */
	final class IndexPropagator implements Propagator {
		public void propagate(PropagationEvent event) throws Exception {
			//var.getProblem().log(event + ": " + var);
			if(indexVar.isBound())
				setVar.setValue(sets[indexVar.getValue()]);
		}
	}
	
	/**
	 * reacts on value event for variables in the VarSet
	 */
	final class VarPropagator implements Propagator {

		int value;
		Var var;

		public VarPropagator(Var theVar, int theValue) {
			this.var = theVar;
			this.value = theValue;
		}

		public void propagate(PropagationEvent event) throws Exception {
			Problem p = getProblem();
			Integer intValue = new Integer(value);
			if(var.isBound()) {
				if(var.getMin() == 1) { //if 'value' is required in the set
					for( int a = indexVar.getMin(); a <= indexVar.getMax(); a++) {
						if( !sets[a].contains(intValue)) {
							//indexVar.removeValue(a);
							p.post(indexVar,"!=",a);
						}
					}
				}
				else { // 'value' is not in the set
					for( int a = indexVar.getMin(); a <= indexVar.getMax(); a++) {
						if( sets[a].contains(intValue)){
							p.post(indexVar,"!=",a);
						}
					}
				}
			}
		}
	}
}
