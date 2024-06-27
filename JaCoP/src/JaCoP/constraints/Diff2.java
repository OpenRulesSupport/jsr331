/**
 *  Diff2.java 
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
import java.util.List;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Store;
import JaCoP.core.Variable;

import JaCoP.constraints.Rectangle;

/**
 * Diff2 constraint assures that any two rectangles from a vector of rectangles
 * does not overlap in at least one direction.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Diff2 extends Diff implements Constants {

	final static short type = diff2;

	static int IdNumber = 0;
	
	Diff2Var EvalRects[];

        boolean exceptionListPresent = false;

	/**
	 * It creates a diff2 constraint.
	 * @param store constraint store in which the constraint is created.
	 * @param o1 list of variables denoting the origin in the first dimension.
	 * @param o2 list of variables denoting the origin in the second dimension.
	 * @param l1 list of variables denoting the length in the first dimension.
	 * @param l2 list of variables denoting the length in the second dimension.
	 * @param profile specifies if the profile should be computed.
	 */
	public Diff2(Store store, 
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
	
	public Diff2(Store store,
				 ArrayList<? extends ArrayList<? extends Variable>> rectangles) {
	
		super(rectangles);

		Diff.IdNumber--;
		numberId = IdNumber++;

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

	public Diff2(Store store,
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
	public Diff2(Store store, 
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
	
	public Diff2(Store store, 
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
	public Diff2(Store store, 
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

	public Diff2(Store store, Variable[][] rectangles) {
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

	public Diff2(Store store, Variable[][] rectangles, boolean profile) {
		this(store, rectangles);
		doProfile = profile;
	}

	/**
	 * Conditional Diff2. The rectangles that are specified on the list
	 * Exclusive are excluded from checking that they must be non-overlapping.
	 * The rectangles are numbered from 1, for example list [[1,3], [3,4]]
	 * specifies that rectangles 1 and 3 as well as 3 and 4 can overlap each
	 * other.
	 * 
	 * @param store - the constraint store to which the constraint is imposed.
	 * @param rect  - list of rectangles, each rectangle represented by a list of variables.
	 * @param exclusive - list of rectangles pairs which can overlap.
	 */
	public Diff2(Store store, ArrayList<ArrayList<Variable>> rect,
			ArrayList<ArrayList<Integer>> exclusive) {

		super(rect);

	        exceptionListPresent = true;

		EvalRects = new Diff2Var[rect.size()];
		
		for (int j = 0; j < EvalRects.length; j++) 
			EvalRects[j] = new Diff2Var(store, onList(j, exclusive));
		
	}

	/**
	 * Conditional Diff2. The rectangles that are specified on the list
	 * Exclusive are excluded from checking that they must be non-overlapping.
	 * The rectangles are numbered from 1, for example list [[1,3], [3,4]]
	 * specifies that rectangles 1 and 3 as well as 3 and 4 can overlap each
	 * other.
	 * 
	 * @param store - the constraint store to which the constraint is imposed.
	 * @param rect  - list of rectangles, each rectangle represented by a list of variables.
	 * @param exclusive - list of rectangles pairs which can overlap.
	 */
	public Diff2(Store store, Variable[][] rect,
			ArrayList<ArrayList<Integer>> exclusive) {

		super(rect);

	        exceptionListPresent = true;

		EvalRects = new Diff2Var[rect.length];
		
		for (int j = 0; j < EvalRects.length; j++)
			EvalRects[j] = new Diff2Var(store, onList(j, exclusive));

	}

	private Rectangle[] onList(int index,
			ArrayList<ArrayList<Integer>> exclusiveList) {

		ArrayList<Rectangle> list = new ArrayList<Rectangle>();
		
		for (int i = 0; i < rectangles.length; i++)
			if (notOverlapping(index + 1, i + 1, exclusiveList))
				list.add(rectangles[i]);
				
		return list.toArray(new Rectangle[list.size()]);
	}

	boolean notOverlapping(int i, 
						   int j,
						   ArrayList<ArrayList<Integer>> exclusiveList) {

		boolean onList = false;
		int l = 0;
		
		while (!onList && l < exclusiveList.size()) {
			ArrayList<Integer> v = exclusiveList.get(l);
			onList = (i == v.get(0) && j == v.get(1))
					|| (i == v.get(1) && j == v.get(0));
			l++;
		}

		return !onList;
	}

	boolean findRectangles(Rectangle r, int index,
			ArrayList<IntRectangle> UsedRect,
			ArrayList<Rectangle> ProfileCandidates,
			ArrayList<Rectangle> OverlappingRects, HashSet<Variable> fdvQueue) {

		// Rectangle s;
		boolean contains = false, checkArea = false;

		long area = 0;
		long commonArea = 0;
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

		for (Rectangle s : ((Diff2VarValue) EvalRects[index].value()).Rects) {
			boolean overlap = true;

			if (r != s) {

				boolean sChanged = containsChangedVariable(s, fdvQueue);

				IntRectangle Use = new IntRectangle(dim);
				long sArea = 1;
				long partialCommonArea = 1;

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
					if (start < stop) {
						Use.add(start, stop - start);
						j++;
					} else
						use = false;

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
					} // profile candidate end

					// calculate area within rectangle r possible placement
					for (int i = 0; i < dim; i++) {
						if (sOriginMin[i] <= r_min[i]) {
							if (sOriginMax[i] <= r_max[i]) {
								int distance1 = sOriginMin[i] + sLengthMin[i] - r_min[i];
								sLengthMin[i] = (distance1 > 0 ) ? distance1 : 0;
							}
							else {
								// sOriginMax[i] > r_max[i])
								int rmax = r.origin[i].max() + r.length[i].min();

								int distance1 = sOriginMin[i] + sLengthMin[i] - r_min[i];
								int distance2 = sLengthMin[i] - (sOriginMax[i] - rmax);
								if (distance1 > rmax - r_min[i]) distance1 = rmax - r_min[i];
								if (distance2 > rmax - r_min[i]) distance2 = rmax - r_min[i];
								if (distance1 < distance2)
									sLengthMin[i] = (distance1 > 0) ? distance1 : 0;
								else
									if (distance2 > 0) {
										if (distance2 < sLengthMin[i])
											sLengthMin[i] = distance2;
									}
									else
										sLengthMin[i] = 0;
							}
						}
						else // sOriginMin[i] > r_min[i]
							if (sOriginMax[i] > r_max[i]) {
								int distance2 = sLengthMin[i] - (sOriginMax[i] - (r.origin[i].max() + r.length[i].min()));
								if (distance2 > 0) {
									if (distance2 < sLengthMin[i])
										sLengthMin[i] = distance2;
								}
								else
									sLengthMin[i] = 0;
							}
						partialCommonArea = partialCommonArea * sLengthMin[i];
					}
					commonArea += partialCommonArea;
				}
				if (!exceptionListPresent)
				    if (commonArea + r.minArea() > (r_max[0]-r_min[0])*(r_max[1]-r_min[1])) 
   					currentStore.throwFailException(this);
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
			for (int i = 0; i < startMin.length; i++) {
				availArea = availArea * (stopMax[i] - startMin[i]);
				rectNumber = rectNumber
				* ((stopMax[i] - startMin[i]) / minLength[i]);
			}

			if (!exceptionListPresent)
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
	void narrowRectangles(HashSet<Variable> fdvQueue) {

		boolean needToNarrow = false;

		for (int l = 0; l < rectangles.length; l++) {
			Rectangle r = rectangles[l];

			boolean settled = true, minLengthEq0 = false;
			int maxLevel = 0;
			for (int i = 0; i < r.dim(); i++) {
				Domain rOrigin = r.origin[i].dom();
				Domain rLength = r.length[i].dom();
				settled = settled && rOrigin.singleton() && rLength.singleton();

				minLengthEq0 = minLengthEq0 || (rLength.min() <= 0);

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


	@Override
	public boolean satisfied() {
		boolean sat = true;

		Rectangle recti, rectj;
		int i = 0;
		while (sat && i < rectangles.length) {
			recti = rectangles[i];
			int j = 0;
			Rectangle[] toEvaluate = ((Diff2VarValue) EvalRects[i].value()).Rects;
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
	public String id() {
		if (id != null)
			return id;
		else
			return  id_diff2 + numberId;
	}
	
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : diff2( ");
		
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

		org.jdom.Element constraint = super.toXML();
		
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_diff2);

		return constraint;

	}
	
	@SuppressWarnings("unchecked")
	static public Constraint fromXML(Element constraint, Store store) {
		
		ArrayList<ArrayList<Variable>> rects = new ArrayList<ArrayList<Variable>>();

		for (org.jdom.Element rect : (List<org.jdom.Element>) constraint
				.getChildren("rectangle")) {

			int dim = Integer.valueOf(rect
					.getAttributeValue("dimension"));

			ArrayList<Variable> nextRect = new ArrayList<Variable>();

			for (int i = 0; i < dim; i++)
				nextRect.add(store.findVariable(rect.getAttributeValue("origin" + i)));

			for (int i = 0; i < dim; i++)
				nextRect.add(store.findVariable(rect.getAttributeValue("length" + i)));

			rects.add(nextRect);

		}

		return new Diff2(store, rects);
		
	}

}
