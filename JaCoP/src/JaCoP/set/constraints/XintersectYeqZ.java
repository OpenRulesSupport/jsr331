/**
 *  XintersectYeqZ.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *	Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *  
 *  Notwithstanding any other provision of this License, the copyright
 *  owners of this work supplement the terms of this License with terms
 *  prohibiting misrepresentation of the origin of this work and requiring
 *  that modified versions of this work be marked in reasonable ways as
 *  different from the original version. This supplement of the license
 *  terms is in accordance with Section 7 of GNU Affero General Public
 *  License version 3.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package JaCoP.set.constraints;

import java.util.ArrayList;

import JaCoP.constraints.Constraint;
import JaCoP.core.Constants;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.set.core.Set;
import JaCoP.set.core.SetDomain;

/**
 * It creates a constraint that makes sure that x intersected with y 
 * is equal to z. 
 * 
 * @author Krzysztof Kuchcinski and Robert Åkemalm
 * @version 2.4
 */

public class XintersectYeqZ extends Constraint implements Constants {

	static int IdNumber = 1;

	final static short type = XintersectYeqZSet;

	Variable x, y, z;

	/**
	 * It constructs an XintersectYeqZ constraint to restrict the domain of the variables X, Y and Z.
	 * @param x 
	 * @param y 
	 * @param z variable that is restricted to be the intersection of x and y.
	 */
	public XintersectYeqZ(Variable x,Variable y,Variable z) {
		numberId = IdNumber++;
		numberArgs = 1;
		this.x = x;
		this.y = y;
		this.z = z;

		if(x.domain.domainID() != SetDomain.SetDomainID || y.domain.domainID() != SetDomain.SetDomainID || z.domain.domainID() != SetDomain.SetDomainID){
			throw new JaCoPException("This constraint should only be used with setDomain.");
		}

	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> variables = new ArrayList<Variable>(2);

		variables.add(x);
		variables.add(y);
		variables.add(z);

		return variables;

	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {
		SetDomain s1 = (SetDomain) x.dom();
		SetDomain s2 = (SetDomain) y.dom();
		SetDomain s = (SetDomain) z.dom();

		//T7
		Set new_s1_glb = s1.glb.union(s.glb);
		Set new_s1_lub = s1.lub.subtract( (s1.lub.intersect(s2.glb)).subtract(s.lub));
		//T7'
		Set new_s2_glb = s2.glb.union(s.glb);
		Set new_s2_lub = s2.lub.subtract( (s2.lub.intersect(s1.glb)).subtract(s.lub));
		//T8
		Set new_s_glb = s.glb.union(s1.glb).intersect(s2.glb);
		Set new_s_lub = s.lub.intersect(s1.lub.intersect(s2.lub)); 

		store.newPropagation = false;
		((SetDomain)x.domain).in(store.level, x, new_s1_glb, new_s1_lub); 
		((SetDomain)y.domain).in(store.level, y, new_s2_glb, new_s2_lub);
		((SetDomain)z.domain).in(store.level, z, new_s_glb, new_s_lub);
	}

	@Override
	public org.jdom.Element getPredicateDescriptionXML() {
		return null;
	}

	@Override
	public int getConsistencyPruningEvent(Variable var) {

		// If consistency function mode
		if (consistencyPruningEvents != null) {
			Integer possibleEvent = consistencyPruningEvents.get(var);
			if (possibleEvent != null)
				return possibleEvent;
		}
		return Constants.ANY;		
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return id_XintersectYeqZ + numberId;
	}

	@Override
	public void impose(Store store) {
		x.putModelConstraint(this,getConsistencyPruningEvent(x));
		y.putModelConstraint(this,getConsistencyPruningEvent(y));
		z.putModelConstraint(this,getConsistencyPruningEvent(z));

		store.addChanged(this);
		store.countConstraint();
	}
	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		x.removeConstraint(this);
		y.removeConstraint(this);
		z.removeConstraint(this);
	}

	@Override
	public boolean satisfied() {
		return (x.singleton() && y.singleton() && z.singleton() && x.domain.intersect(y.domain).eq(z.domain));
	}

	@Override
	public String toString() {
		return id() + " : XintersectYeqZ(" + x + ", " + y + ", "+z+" )";
	}

	@Override
	public org.jdom.Element toXML() {

		throw new JaCoPException("XML load/save functionality for XintersectYeqZ not implemented.");

	}

	@Override
	public short type() {
		return type;
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			x.weight++;
			y.weight++;
			z.weight++;
		}
	}

}
