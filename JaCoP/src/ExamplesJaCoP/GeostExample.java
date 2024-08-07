/**
 *  GeostExample.java 
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

package ExamplesJaCoP;

import java.util.ArrayList;

import JaCoP.constraints.Constraint;
import JaCoP.constraints.geost.DBox;
import JaCoP.constraints.geost.ExternalConstraint;
import JaCoP.constraints.geost.Geost;
import JaCoP.constraints.geost.GeostObject;
import JaCoP.constraints.geost.InArea;
import JaCoP.constraints.geost.NonOverlapping;
import JaCoP.constraints.geost.Shape;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * It specifies an example where squares of the given size must be placed within
 * a square of a given size. 
 *
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class GeostExample extends Example {

	Store store;
	ArrayList<Variable> vars;

	Variable[] varsX;
	Variable[] varsY;
	Variable[] size;

	/**
	 * It specifies and runs a very simple Geost example. It is just
	 * to present how Geost constraint can be created and imposed to store.
	 * 
	 * @param args no parameters read.
	 */
	public static void main(String args[]) {

		GeostExample example = new GeostExample();
		example.model();
		example.search();

	}

	public void model() {


		store = new Store();

		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<GeostObject> objects = new ArrayList<GeostObject>();
		ArrayList<ExternalConstraint> constraints = new ArrayList<ExternalConstraint>(); 

		// Object O1- shapes 1, 2, 3 and 4
		ArrayList<DBox> shape1 = new ArrayList<DBox>();
		shape1.add(new DBox(new int[] {0,0}, new int[] {2,1}));
		shape1.add(new DBox(new int[] {0,1}, new int[] {1,2}));
		shape1.add(new DBox(new int[] {1,2}, new int[] {3,1}));
		shapes.add( new Shape(0, shape1) );

		ArrayList<DBox> shape2 = new ArrayList<DBox>();
		shape2.add(new DBox(new int[] {0,0}, new int[] {3,1}));
		shape2.add(new DBox(new int[] {0,1}, new int[] {1,3}));
		shape2.add(new DBox(new int[] {2,1}, new int[] {1,1}));
		shapes.add( new Shape(1, shape2) );

		ArrayList<DBox> shape3 = new ArrayList<DBox>();
		shape3.add(new DBox(new int[] {0,0}, new int[] {2,1}));
		shape3.add(new DBox(new int[] {1,1}, new int[] {1,2}));
		shape3.add(new DBox(new int[] {2,2}, new int[] {3,1}));
		shapes.add ( new Shape(2, shape3) );

		ArrayList<DBox> shape4 = new ArrayList<DBox>();
		shape4.add(new DBox(new int[] {0,0}, new int[] {3,1}));
		shape4.add(new DBox(new int[] {0,1}, new int[] {1,1}));
		shape4.add(new DBox(new int[] {2,1}, new int[] {1,3}));
		shapes.add( new Shape(3, shape4) );

		Variable X_o1 = new Variable(store, "x1", 0, 1);
		Variable Y_o1 = new Variable(store, "y1", 0, 1);
		Variable[] coords_o1 = {X_o1, Y_o1};
		Variable shape_o1 = new Variable(store, "shape_o1", 0, 3);
		Variable start_o1 = new Variable(store, "start_o1", 2, 2);
		Variable duration_o1 = new Variable(store, "duration_o1", 12, 12);
		Variable end_o1 = new Variable(store, "end_o1", 14, 14);
		GeostObject o1 = new GeostObject(0, coords_o1, shape_o1, start_o1, duration_o1, end_o1);
		objects.add(o1);

		//  Object o2- shapes 5 and 6
		ArrayList<DBox> shape5 = new ArrayList<DBox>();
		shape5.add(new DBox(new int[] {0,0}, new int[] {2,1}));
		shape5.add(new DBox(new int[] {1,1}, new int[] {1,1}));
		shape5.add(new DBox(new int[] {0,2}, new int[] {2,1}));
		shapes.add( new Shape(4, shape5) );

		ArrayList<DBox> shape6 = new ArrayList<DBox>();
		shape6.add(new DBox(new int[] {0,0}, new int[] {3,1}));
		shape6.add(new DBox(new int[] {0,1}, new int[] {1,1}));
		shape6.add(new DBox(new int[] {2,1}, new int[] {1,1}));
		shapes.add( new Shape(5, shape6) );

		Variable X_o2 = new Variable(store, "x2", 0, 2);
		Variable Y_o2 = new Variable(store, "y2", 0, 1);
		Variable[] coords_o2 = {X_o2, Y_o2};
		Variable shape_o2 = new Variable(store, "shape_o2", 4, 5);
		Variable start_o2 = new Variable(store, "start_o2", 10, 10);
		Variable duration_o2 = new Variable(store, "duration_o2", 12, 12);
		Variable end_o2 = new Variable(store, "end_o2", 22, 22);
		GeostObject o2 = new GeostObject(1, coords_o2, shape_o2, start_o2, duration_o2, end_o2);
		objects.add(o2);

		//  Object o3- shapes 7 and 8
		ArrayList<DBox> shape7 = new ArrayList<DBox>();
		shape7.add(new DBox(new int[] {0,0}, new int[] {3,2}));
		shapes.add( new Shape(6, shape7) );

		ArrayList<DBox> shape8 = new ArrayList<DBox>();
		shape8.add(new DBox(new int[] {0,0}, new int[] {2,3}));
		shapes.add( new Shape(7, shape8) );

		Variable X_o3 = new Variable(store, "x3", 0, 4);
		Variable Y_o3 = new Variable(store, "y3", 0, 0);
		Variable[] coords_o3 = {X_o3, Y_o3};
		Variable shape_o3 = new Variable(store, "shape_o3", 6, 7);
		Variable start_o3 = new Variable(store, "start_o3", 10, 10);
		Variable duration_o3 = new Variable(store, "duration_o3", 12, 12);
		Variable end_o3 = new Variable(store, "end_o3", 22, 22);
		GeostObject o3 = new GeostObject(2, coords_o3, shape_o3, start_o3, duration_o3, end_o3);
		objects.add(o3);

		//  Object o4- shape 9
		ArrayList<DBox> shape9 = new ArrayList<DBox>();
		shape9.add(new DBox(new int[] {0,0}, new int[] {1,4}));
		shapes.add( new Shape(8, shape9) );

		Variable X_o4 = new Variable(store, "x4", 0, 1);
		Variable Y_o4 = new Variable(store, "y4", 0, 1);
		Variable[] coords_o4 = {X_o4, Y_o4};
		Variable shape_o4 = new Variable(store, "shape_o4", 8, 8);
		Variable start_o4 = new Variable(store, "start_o4", 14, 14);
		Variable duration_o4 = new Variable(store, "duration_o4", 8, 8);
		Variable end_o4 = new Variable(store, "end_o4", 22, 22);
		GeostObject o4 = new GeostObject(3, coords_o4, shape_o4, start_o4, duration_o4, end_o4);
		objects.add(o4);

		// dimension 2 must be added so the time dimension is also present.
		int[] dimensions = {0, 1, 2};
		NonOverlapping constraint1 = new NonOverlapping(objects, dimensions);
		constraints.add(constraint1);
		InArea constraint2 = new InArea(new DBox(new int[] {0,0}, new int[] {5,4}), null);
		constraints.add(constraint2);

		Constraint c = new Geost(objects, constraints, shapes);
		store.impose(c);

	}

	public boolean search() {

		long T1, T2, T;
		T1 = System.currentTimeMillis();

		boolean result = store.consistency();

		//      System.out.println(shapes+"\n"+objects);

		//      Search label = new DepthFirstSearch();

		//      Variable[] vars = {X_o1, Y_o1, X_o2, Y_o2, X_o3, Y_o3, X_o4, Y_o4};
		//      SelectChoicePoint select = new SimpleSelect(vars, null,
		//  						    new IndomainMin());
		//      Result = label.labeling(store, select);

		if (result) {
			System.out.println("*** Yes");
			System.out.println(store);
		}
		else
			System.out.println("*** No");

		T2 = System.currentTimeMillis();
		T = T2 - T1;
		System.out.println("\n\t*** Execution time = "+ T + " ms");

		return result;
	}

}
