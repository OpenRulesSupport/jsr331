/**
 *  Disjoint.java 
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


package JaCoP.constraints;

import java.util.ArrayList;
import java.util.HashSet;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Disjoint constraint assures that any two rectangles from a vector of
 * rectangles does not overlap in at least one direction.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Disjoint extends Diff implements Constants {

	final static short type = disjoint;

	static int IdNumber = 0;
	
	Diff2Var EvalRects[];

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param o1 list of variables denoting the origin in the first dimension.
	 * @param o2 list of variables denoting the origin in the second dimension.
	 * @param l1 list of variables denoting the length in the first dimension.
	 * @param l2 list of variables denoting the length in the second dimension.
	 * @param profile specifies if the profile should be computed.
	 */
	public Disjoint(Store store, 
				 ArrayList<Variable> o1,
				 ArrayList<Variable> o2,
				 ArrayList<Variable> l1,
				 ArrayList<Variable> l2,
				 boolean profile) {
		this(store, o1, o2, l1, l2);
		doProfile = profile;
	}

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param rectangles list of rectangles with origins and lengths in both dimensions.
	 */
	
	public Disjoint(Store store,
				 	ArrayList<? extends ArrayList<? extends Variable>> rectangles) {
	
		super(rectangles);

		EvalRects = new Diff2Var[rectangles.size()];
		
		for (int j = 0; j < EvalRects.length; j++)
			EvalRects[j] = new Diff2Var(store, this.rectangles);
	
	}
	
	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param rectangles list of rectangles with origins and lengths in both dimensions.
	 * @param profile specifies if the profile is computed and used.
	 */

	public Disjoint(Store store,
				 ArrayList<? extends ArrayList<? extends Variable>> rectangles,
			     boolean profile) {
		this(store, rectangles);
		doProfile = profile;
	}

	
	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param o1 list of variables denoting the origin in the first dimension.
	 * @param o2 list of variables denoting the origin in the second dimension.
	 * @param l1 list of variables denoting the length in the first dimension.
	 * @param l2 list of variables denoting the length in the second dimension.
	 */
	public Disjoint(Store store, 
				 ArrayList<? extends Variable> o1,
				 ArrayList<? extends Variable> o2,
				 ArrayList<? extends Variable> l1,
				 ArrayList<? extends Variable> l2) {

		super(o1, o2, l1, l2);

		Diff.IdNumber--;
		numberId = IdNumber++;

		EvalRects = new Diff2Var[o1.size()];
		
		for (int j = 0; j < EvalRects.length; j++)
			EvalRects[j] = new Diff2Var(store, rectangles);
	}

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param o1 list of variables denoting the origin in the first dimension.
	 * @param o2 list of variables denoting the origin in the second dimension.
	 * @param l1 list of variables denoting the length in the first dimension.
	 * @param l2 list of variables denoting the length in the second dimension.
	 */
	
	public Disjoint(Store store, 
				 Variable[] o1, 
				 Variable[] o2, 
				 Variable[] l1,
				 Variable[] l2) {
		
		super(o1, o2, l1, l2);

		Diff.IdNumber--;
		numberId = IdNumber++;

		EvalRects = new Diff2Var[o1.length];
		
		for (int j = 0; j < EvalRects.length; j++)
			EvalRects[j] = new Diff2Var(store, rectangles);
	}

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param o1 list of variables denoting the origin in the first dimension.
	 * @param o2 list of variables denoting the origin in the second dimension.
	 * @param l1 list of variables denoting the length in the first dimension.
	 * @param l2 list of variables denoting the length in the second dimension.
	 * @param profile specifies if the profile should be computed.
	 */	
	public Disjoint(Store store, 
				 Variable[] o1, 
				 Variable[] o2, 
				 Variable[] l1,
				 Variable[] l2, 
				 boolean profile) {
		this(store, o1, o2, l1, l2);
		doProfile = profile;
	}

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param rectangles list of rectangles with origins and lengths in both dimensions.
	 */

	public Disjoint(Store store, Variable[][] rectangles) {
		super(rectangles);

		Diff.IdNumber--;
		numberId = IdNumber++;

		EvalRects = new Diff2Var[rectangles.length];
		for (int j = 0; j < EvalRects.length; j++)
			EvalRects[j] = new Diff2Var(store, this.rectangles);
	}

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param rectangles list of rectangles with origins and lengths in both dimensions.
	 * @param profile specifies if the profile is computed and used.
	 */

	public Disjoint(Store store, Variable[][] rectangles, boolean profile) {
		this(store, rectangles);
		doProfile = profile;
	}	
	
	@Override
	public boolean satisfied() {
		boolean sat = true;

		Rectangle recti, rectj;
		int i = 0;
		while (sat && i < rectangles.length) {
			recti = rectangles[i];
			int j = 0;
			Rectangle toEvaluate[] = ((Diff2VarValue) EvalRects[i].value()).Rects;
			while (sat && j < toEvaluate.length) {
				rectj = toEvaluate[j];
				sat = sat && !recti.domOverlap(rectj);
				j++;
			}
			i++;
		}
		return sat;
	}

	@Override
	void narrowRectangles(HashSet<Variable> fdvQueue) {
		Rectangle r;
		boolean needToNarrow = false;

		for (int l = 0; l < rectangles.length; l++) {
			r = rectangles[l];

			boolean settled = true, minLengthEq0 = false;
			int maxLevel = 0;
			for (int i = 0; i < r.dim(); i++) {
				Domain rOrigin = r.origin[i].dom();
				Domain rLength = r.length[i].dom();
				settled = settled && rOrigin.singleton() && rLength.singleton();

				minLengthEq0 = minLengthEq0 || (rLength.min() < 0);

				int originStamp = rOrigin.stamp, lengthStamp = rLength.stamp;
				if (maxLevel < originStamp)
					maxLevel = originStamp;
				if (maxLevel < lengthStamp)
					maxLevel = lengthStamp;
			}

			if (!minLengthEq0 && // Check for rectangle r which has
					// all lengths > 0
					!(settled && maxLevel < currentStore.level)) {
				// and are not fixed already

				needToNarrow = needToNarrow
						|| containsChangedVariable(r, fdvQueue);

				ArrayList<IntRectangle> UsedRect = new ArrayList<IntRectangle>();
				ArrayList<Rectangle> ProfileCandidates = new ArrayList<Rectangle>();
				ArrayList<Rectangle> OverlappingRects = new ArrayList<Rectangle>();
				boolean ntN = findRectangles(r, l, UsedRect, ProfileCandidates,
						OverlappingRects, fdvQueue);

				needToNarrow = needToNarrow || ntN;

				// Checking r against all s with minUse in the domain of r
				if (needToNarrow) {

					if (OverlappingRects.size() != ((Diff2VarValue) EvalRects[l]
							.value()).Rects.length) {
						Diff2VarValue newRects = new Diff2VarValue();
						newRects.setValue(OverlappingRects);
						EvalRects[l].update(newRects);
					}

					narrowRectangle(r, UsedRect, ProfileCandidates);
				}
			}
		}
	}

	boolean findRectangles(Rectangle r, int index,
			ArrayList<IntRectangle> UsedRect,
			ArrayList<Rectangle> ProfileCandidates,
			ArrayList<Rectangle> OverlappingRects, HashSet<Variable> fdvQueue) {
		Rectangle s;
		boolean contains = false, checkArea = false;

		long area = 0;
		int totalNumberOfRectangles = 0;
		int dim = r.dim();
		int startMin[] = new int[dim];
		int stopMax[] = new int[dim];
		int minLength[] = new int[dim];
		int r_min[] = new int[dim], r_max[] = new int[dim];
		for (int i = 0; i < startMin.length; i++) {
			Domain rLengthDom = r.length[i].dom();
			startMin[i] = MaxInt;
			stopMax[i] = 0;
			minLength[i] = rLengthDom.min();

			Domain rOriginDom = r.origin[i].dom();
			r_min[i] = rOriginDom.min();
			r_max[i] = rOriginDom.max() + rLengthDom.max();
		}

		Rectangle toEvaluate[] = ((Diff2VarValue) EvalRects[index].value()).Rects;
		for (int l = 0; l < toEvaluate.length; l++) {
			s = toEvaluate[l];

			boolean overlap = true;

			if (r != s) {
				boolean sChanged = containsChangedVariable(s, fdvQueue);

				IntRectangle Use = new IntRectangle(dim);
				long sArea = 1;

				boolean use = true, minLength0 = false;
				int s_min, s_max, start, stop;
				int m = 0, j = 0;
				int sOriginMin[] = new int[dim], sOriginMax[] = new int[dim], sLengthMin[] = new int[dim];

				while (overlap && m < dim) {
					// check if domains of r and s overlap
					Domain sOriginIdom = s.origin[m].dom();
					Domain sLengthIdom = s.length[m].dom();
					int sLengthIMin = sLengthIdom.min();
					int sOriginIMax = sOriginIdom.max();
					s_min = sOriginIdom.min();
					s_max = sOriginIMax + sLengthIdom.max();
					overlap = overlap
							&& intervalOverlap(r_min[m], r_max[m], s_min, s_max);

					// min start, max stop and min length
					sOriginMin[m] = s_min;
					sOriginMax[m] = sOriginIMax + sLengthIMin;
					sLengthMin[m] = sLengthIMin;

					// check if s occupies some space
					start = sOriginIMax;
					stop = s_min + sLengthIMin;
					if (start <= stop) {
						Use.add(start, stop - start);
						j++;
						// Profile[j] += length;
					} else
						use = false;

					// min length == 0
					minLength0 = minLength0 || (sLengthMin[m] <= 0);

					m++;
				}

				if (overlap) {

					OverlappingRects.add(s);

					if (use) { // rectangles taking space
						UsedRect.add(Use);
						contains = contains || sChanged;
					}

					if (!minLength0) { // profile candiates
						if (j > 0) {
							ProfileCandidates.add(s);
							contains = contains || sChanged;
						}

						checkArea = true;
						totalNumberOfRectangles++;
						for (int i = 0; i < dim; i++) {
							if (sOriginMin[i] < startMin[i])
								startMin[i] = sOriginMin[i];
							if (sOriginMax[i] > stopMax[i])
								stopMax[i] = sOriginMax[i];
							if (minLength[i] > sLengthMin[i])
								minLength[i] = sLengthMin[i];

							sArea = sArea * sLengthMin[i];
						}
						area += sArea;
					}
				}
			}
		}

		if (checkArea) { // check whether there is
			// enough room for all rectangles
			area += r.minArea();
			long availArea = 1;
			long rectNumber = 1;
			for (int i = 0; i < startMin.length; i++) {
				Domain rOriginIdom = r.origin[i].dom();
				Domain rLengthIdom = r.length[i].dom();
				int rOriginIMin = rOriginIdom.min(), rOriginIMax = rOriginIdom
						.max(), rLengthIMin = rLengthIdom.min();
				if (rOriginIMin < startMin[i])
					startMin[i] = rOriginIMin;
				if (rOriginIMax + rLengthIMin > stopMax[i])
					stopMax[i] = rOriginIMax + rLengthIMin;
			}
			boolean minEqZero = false;
			for (int i = 0; i < startMin.length; i++) {
				availArea = availArea * (stopMax[i] - startMin[i]);
				if (minLength[i] == 0) {
					minEqZero = true;
				} else
					rectNumber = rectNumber
							* ((stopMax[i] - startMin[i]) / minLength[i]);
			}
			if (minEqZero)
				rectNumber = Long.MAX_VALUE;

			if (availArea < area)
				currentStore.throwFailException(this);
			else
			// check whether there is enough room for
			// all minimal rectangles
			if (rectNumber < (totalNumberOfRectangles + 1))
				currentStore.throwFailException(this);
		}

		return contains;
	}

	@Override
	void profileNarrowing(int i, Rectangle r,
			ArrayList<Rectangle> ProfileCandidates) {
		// check profile first

		Domain rOriginIdom = r.origin[i].dom();
		int rOriginIdomMin = rOriginIdom.min();
		int rOriginIdomMax = rOriginIdom.max();
		DiffnProfile Profile = new DiffnProfile();

		for (int j = 0; j < r.dim; j++) {
			if (j != i && r.length[i].min() != 0) {

				Profile.make(j, i, r, rOriginIdomMin, rOriginIdomMax
						+ r.length[i].min(), ProfileCandidates);

				if (Profile.size() != 0) {
					if (trace) {
						System.out.println(" *** " + r + "\n"
								+ ProfileCandidates);
						System.out.println("Profile in dimension " + i
								+ " and " + j + "\n" + Profile);
					}

					profileCheckRectangle(Profile, r, i, j);

				}
			}
		}
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return  id_disjoint + numberId;
	}
	
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : disjoint( ");
		
		for (int i = 0; i < rectangles.length - 1; i++) {
			result.append(rectangles[i]);
			result.append(", ");
			
		}
		result.append(rectangles[rectangles.length - 1]);
		result.append(")");
		
		return result.toString();

	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("id", id() );
		constraint.setAttribute("type", id_disjoint);

		for (int i = 0; i < rectangles.length; i++) {

			org.jdom.Element rectangle = rectangles[i].toXML();
			constraint.addContent(rectangle);

		}

		return constraint;

	}

}
