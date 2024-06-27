/**
 *  Diff.java 
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.jdom.Element;

import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;
import JaCoP.constraints.Profile;
import JaCoP.constraints.ProfileItem;

/**
 * Diff constraint assures that any two rectangles from a vector of rectangles
 * does not overlap in at least one direction. It is a simple implementation which
 * does not use sophisticated techniques for efficient backtracking.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Diff extends Constraint implements Constants {

	static int IdNumber = 1;

	static final boolean trace = false, traceNarr = false;

	final static short type = diff;

	Store currentStore = null;
	
	/**
	 * It specifies if the constraint should compute and use the profile.
	 */
	public boolean doProfile = true;

	int minPosition = 0;

	Rectangle rectangles[];

	int stamp = 0;

	HashSet<Variable> variableQueue = new HashSet<Variable>();

	/**
	 * It specifies a diffn constraint. 
	 * @param rectangles list of rectangles which can not overlap in at least one dimension.
	 */
	public Diff(ArrayList<? extends ArrayList<? extends Variable>> rectangles) {

		queueIndex = 2;

		numberId = IdNumber++;
		
		int size = (rectangles.get(0)).size();
		this.rectangles = new Rectangle[rectangles.size()];

		int i = 0;

		for (ArrayList<? extends Variable> R : rectangles)
			if (R.size() == size) {
				Rectangle rect = new Rectangle(R);
				this.rectangles[i] = rect;
				i++;
				numberArgs = (short) (numberArgs + size);
			} else {
				String s = "\nNot equal sizes of rectangle vectors in Diff";
				throw new JaCoPException(s);
			}
		// }
		if (size / 2 != 2) {
			String s = "\nRectangles of size > 2 not currently supported by Diff";
			throw new JaCoPException(s);
		}
	}

	/**
	 * It specifies a diff constraint. 
	 * @param profile specifies is the profiles are used.
	 * @param rectangles list of rectangles which can not overlap in at least one dimension.
	 */
	public Diff(ArrayList<? extends ArrayList<? extends Variable>> rectangles,
				boolean profile) {
		
		this(rectangles);
		
		doProfile = profile;
	
	}

	
	/**
	 * It constructs a diff constraint.
	 * @param o1 list of variables denoting origin of the rectangle in the first dimension.
	 * @param o2 list of variables denoting origin of the rectangle in the second dimension.
	 * @param l1 list of variables denoting length of the rectangle in the first dimension.
	 * @param l2 list of variables denoting length of the rectangle in the second dimension.
	 */
	public Diff(ArrayList<? extends Variable> o1,
				ArrayList<? extends Variable> o2,
				ArrayList<? extends Variable> l1,
				ArrayList<? extends Variable> l2) {

		queueIndex = 2;
		
		numberId = IdNumber++;

		int size = o1.size();
		if (size == o1.size() && size == o2.size() && size == l1.size()
				&& size == l2.size()) {
			rectangles = new Rectangle[size];
			for (int i = 0; i < size; i++) {
				ArrayList<Variable> R = new ArrayList<Variable>(4);
				R.add(o1.get(i));
				R.add(o2.get(i));
				R.add(l1.get(i));
				R.add(l2.get(i));
				Rectangle rect = new Rectangle(R);
				rectangles[i] = rect;
				numberArgs = (short) (numberArgs + 4);
			}
		} else {
			String s = "\nNot equal sizes of Variable vectors in Diff";
			throw new JaCoPException(s);
		}
	}

	/**
	 * It constructs a diff constraint.
	 * @param o1 list of variables denoting origin of the rectangle in the first dimension.
	 * @param o2 list of variables denoting origin of the rectangle in the second dimension.
	 * @param l1 list of variables denoting length of the rectangle in the first dimension.
	 * @param l2 list of variables denoting length of the rectangle in the second dimension.
	 * @param profile it specifies if the profile should be computed and used.
	 */
	public Diff(ArrayList<? extends Variable> o1,
				ArrayList<? extends Variable> o2, 
				ArrayList<? extends Variable> l1,
				ArrayList<? extends Variable> l2,
				boolean profile) {
		this(o1, o2, l1, l2);
		doProfile = profile;
	}

	/**
	 * It constructs a diff constraint.
	 * @param o1 list of variables denoting origin of the rectangle in the first dimension.
	 * @param o2 list of variables denoting origin of the rectangle in the second dimension.
	 * @param l1 list of variables denoting length of the rectangle in the first dimension.
	 * @param l2 list of variables denoting length of the rectangle in the second dimension.
	 */

	public Diff(Variable[] o1, 
				Variable[] o2,
				Variable[] l1, 
				Variable[] l2) {

		queueIndex = 2;
		
		numberId = IdNumber++;

		int size = o1.length;
		if (size == o1.length && size == o2.length && size == l1.length
				&& size == l2.length) {
			rectangles = new Rectangle[size];
			for (int i = 0; i < size; i++) {
				Variable[] R = { o1[i], o2[i], l1[i], l2[i] };
				Rectangle rect = new Rectangle(R);
				rectangles[i] = rect;
				numberArgs = (short) (numberArgs + 4);
			}
		} else {
			String s = "\nNot equal sizes of Variable vectors in Diff";
			throw new JaCoPException(s);
		}
	}

	/**
	 * It constructs a diff constraint.
	 * @param o1 list of variables denoting origin of the rectangle in the first dimension.
	 * @param o2 list of variables denoting origin of the rectangle in the second dimension.
	 * @param l1 list of variables denoting length of the rectangle in the first dimension.
	 * @param l2 list of variables denoting length of the rectangle in the second dimension.
	 * @param profile it specifies if the profile should be computed and used.
	 */
	public Diff(Variable[] o1, 
				Variable[] o2,
				Variable[] l1, 
				Variable[] l2,
				boolean profile) {
		this(o1, o2, l1, l2);
		doProfile = profile;
	}

	/**
	 * It specifies a diff constraint. 
	 * @param rectangles list of rectangles which can not overlap in at least one dimension.
	 */
	public Diff(Variable[][] rectangles) {

		queueIndex = 2;
		Variable[] R;
		
		numberId = IdNumber++;
		int size = rectangles[0].length;
		this.rectangles = new Rectangle[rectangles.length];

		for (int i = 0; i < rectangles.length; i++) {
			R = rectangles[i];
			if (R.length == size) {
				Rectangle rect = new Rectangle(R);
				this.rectangles[i] = rect;
				numberArgs = (short) (numberArgs + size);
			} else {
				String s = "\nNot equal sizes of rectangle vectors in Diff";
				throw new JaCoPException(s);
			}
		}
		if (size / 2 != 2) {
			String s = "\nRectangles of size > 2 not currently supported by Diff";
			throw new JaCoPException(s);

		}
	}

	/**
	 * It specifies a diff constraint. 
	 * @param profile specifies is the profiles are used.
	 * @param rectangles list of rectangles which can not overlap in at least one dimension.
	 */
	public Diff(Variable[][] rectangles, boolean profile) {
		this(rectangles);
		doProfile = profile;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>();

		for (Rectangle r : rectangles) {
			for (int i = 0; i < r.dim; i++)
				Variables.add(r.origin[i]);
			for (int i = 0; i < r.dim(); i++)
				Variables.add(r.length[i]);
		}
		return Variables;
	}

	@Override
	public void removeLevel(int level) {
		variableQueue.clear();
	}

	@Override
	public void consistency(Store store) {

		currentStore = store;
		// System.out.println("---------------------------------");
		// System.out.println("Diff consistency checking"
		// + "\n"+this);

		while (store.newPropagation) {
			store.newPropagation = false;

			HashSet<Variable> fdvs = variableQueue;
			variableQueue = new HashSet<Variable>();
			// System.out.println(fdvs);
			narrowRectangles(fdvs);
		}
		// System.out.println(this.toStringFull());
	}

	boolean containsChangedVariable(Rectangle r, HashSet<Variable> fdvQueue) {
		boolean contains = false;
		int dim = r.dim;
		int i = 0;
		while (!contains && i < dim) {
			contains = contains || fdvQueue.contains(r.origin[i])
					|| fdvQueue.contains(r.length[i]);
			i++;
		}
		return contains;
	}

	boolean doesNotFit(int j, Rectangle r, Profile barrier) {
		boolean excludedState = true;

		Domain rOriginJdom = r.origin[j].dom();
		Domain rLengthJdom = r.length[j].dom();
		int minJ = rOriginJdom.min();
		int maxJ = rOriginJdom.max() + rLengthJdom.min();
		int durJ = rLengthJdom.min();
		int currentJposition = minJ;
		int k = 0, barrierSize = barrier.size();
		while (k < barrierSize && excludedState) {
			ProfileItem p = barrier.get(k);
			int hinderStart = p.min;
			int hinderStop = p.max;
			if (hinderStart - currentJposition >= durJ)
				excludedState = false;
			currentJposition = hinderStop;
			k++;
		}
		if (excludedState && maxJ - currentJposition >= durJ)
			excludedState = false;
		return excludedState;
	}

	int findMaxLength(int i, int length, Rectangle r) {

		int maxLength = length;
		Domain origin = r.origin[i].domain;
		int dur = r.length[i].min();
		int originSize = origin.noIntervals();

		for (int m = 0; m < originSize; m++) {
			// Interval s1 = origin.intervals[m];
			// for (Interval s1 : origin) {
			int intervalLength = origin.rightElement(m) - origin.leftElement(m)
					+ dur;
			if (maxLength < intervalLength)
				maxLength = intervalLength;
		}
		return maxLength;
	}

	
	boolean findRectangles(Rectangle r, ArrayList<IntRectangle> UsedRect,
			ArrayList<Rectangle> ProfileCandidates, HashSet<Variable> fdvQueue) {

		boolean contains = false, checkArea = false;

		long area = 0;
		long commonArea = 0;
		int totalNumberOfRectangles = 0;
		int dim = r.dim;
		int startMin[] = new int[dim];
		int stopMax[] = new int[dim];
		int minLength[] = new int[dim];
		int r_min[] = new int[dim], r_max[] = new int[dim];
		for (int i = 0; i < startMin.length; i++) {
			startMin[i] = MaxInt;
			stopMax[i] = 0;
			minLength[i] = r.length[i].min();

			Domain rOriginDom = r.origin[i].dom();
			r_min[i] = rOriginDom.min();
			r_max[i] = rOriginDom.max() + r.length[i].max();
		}

		int sOriginMin[] = new int[dim], sOriginMax[] = new int[dim], sLengthMin[] = new int[dim];

		for (Rectangle s : rectangles) {

			boolean overlap = true;

			if (r != s) {
				boolean sChanged = containsChangedVariable(s, fdvQueue);

				IntRectangle Use = new IntRectangle(dim);
				long sArea = 1;
				long partialCommonArea = 1;

				boolean use = true, minLength0 = false;
				int s_min, s_max, start, stop;
				int m = 0, j = 0;

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

					// min length == 0
					minLength0 = minLength0 || (sLengthMin[m] <= 0);

					m++;
				}

				if (overlap) {
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

							sArea *= sLengthMin[i];
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
					// end for
					commonArea += partialCommonArea;
				}
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
				availArea *= (stopMax[i] - startMin[i]);
				rectNumber *= ((stopMax[i] - startMin[i]) / minLength[i]);
			}

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

	Rectangle[] getRectangles() {
		return rectangles;
	}

	@Override
	public String id() {
		if (id != null)
			return id;
		else
			return  id_diff + numberId;
	}

	// registers the constraint in the constraint store
	@Override
	public void impose(Store store) {
		Variable v;
		int level = store.level;

		// When should it deregister? If there is any
		// time for it, then it should be implemented
		store.registerRemoveLevelListener(this);

		for (Rectangle r : rectangles) {
			for (int i = 0; i < r.dim(); i++) {
				v = r.origin[i];
				v.putModelConstraint(this, getConsistencyPruningEvent(v));
				queueVariable(level, v);

				v = r.length[i];
				v.putModelConstraint(this, getConsistencyPruningEvent(v));
				queueVariable(level, v);
			}
		}
		store.addChanged(this);
		store.countConstraint();
	}

	boolean intervalOverlap(int min1, int max1, int min2, int max2) {
		return !(min1 >= max2 || max1 <= min2);
	}

	Pair minForbiddenInterval(int start, int i, Rectangle r,
			ArrayList<IntRectangle> ConsideredRect) {

		if (notFit(i, r, ConsideredRect, start)) {
			// System.out.println("New start = " + start + ".." + (int)(start +
			// minPosition));
			return new Pair(start, start + minPosition);
		} else
			return new Pair(-1, -1);
	}

	void narrowIth(int i, Rectangle r, ArrayList<IntRectangle> UsedRect,
			ArrayList<Rectangle> ProfileCandidates) {
		int s;
		int j = (i == 0) ? 1 : 0;
		// int rSize = r.origin[j].max() + r.length[j].min() -
		// r.origin[j].min();
		// int rLengthJMin = r.length[j].min();
		int rLengthIMin = r.length[i].min();
		int barierSize = 0;

		if (ProfileCandidates.size() != 0 && doProfile)
			profileNarrowing(i, r, ProfileCandidates);

		if (UsedRect.size() != 0) {

			IntRectangle[] UsedRectArray = new IntRectangle[UsedRect.size()];

			UsedRectArray = UsedRect.toArray(UsedRectArray);

			TreeSet<IntRectangle> starts = new TreeSet<IntRectangle>(
					new DimIMinComparator<IntRectangle>(i));
			for (IntRectangle m : UsedRectArray)
				starts.add(m);

			// int sizeOfstartsOfR = (r.origin[0].dom().size >
			// r.origin[1].dom().size) ? r.origin[0]
			// .dom().size
			// : r.origin[1].dom().size;

			int sizeOfstartsOfR = (r.origin[0].domain.noIntervals() > r.origin[1].domain
					.noIntervals()) ? r.origin[0].domain.noIntervals()
					: r.origin[1].domain.noIntervals();

			IntRectangle[] startsOfR = new IntRectangle[sizeOfstartsOfR];

			for (int k = 0; k < sizeOfstartsOfR; k++)
				startsOfR[k] = new IntRectangle(r.dim);

			for (int k = 0; k < r.dim; k++) {
				Domain rOrigin = r.origin[k].dom();
				int rOriginSize = rOrigin.noIntervals();
				for (int n = 0; n < sizeOfstartsOfR; n++) {
					if (n < rOriginSize)
						startsOfR[n].add(rOrigin.leftElement(n), 0);
					else
						startsOfR[n].add(rOrigin.min(), 0);
				}
			}
			for (IntRectangle rect : startsOfR) {
				// System.out.print(rect+" ");
				starts.add(rect);
			}
			// System.out.println();

			ArrayList<IntRectangle> ConsideredRect = new ArrayList<IntRectangle>();
			for (IntRectangle ir : starts) {
				s = ir.origin[i];
				// System.out.println("*** start = " + s);

				ConsideredRect.clear();
				// long rectSize = 0;
				for (IntRectangle t : UsedRectArray) {
					int tCompletion = t.origin[i] + t.length[i];

					// if ( t.origin[i] - s < rLengthIMin && s <= tCompletion) {
					if (t.origin[i] <= s && s - rLengthIMin < tCompletion) {
						ConsideredRect.add(t);
						// rectSize += t.length[j];
					}
				}

				if (ConsideredRect.size() != 0
				// && rSize < (rectSize + (rLengthJMin - 1) *
				// ConsideredRect.size())
				) {
					// for (Interval rI : r.origin[i].dom()) {
					// if ( s >= rI.Min && s <= rI.Max ) {
					Domain rIdom = r.origin[i].dom();
					if (s >= rIdom.min() && s <= rIdom.max()) {
						// System.out.println("Checking rectangles in dimension
						// "+i+
						// " starting at time interval "+ s + ".."
						// +(int)(s+r.length(i).min()-1)+
						// "\nCosideredRect =" + ConsideredRect);

						Pair exclude = minForbiddenInterval(s, i, r,
								ConsideredRect);

						if (exclude.Max != -1) {
							// FD Update = FDset.complement(new FD(exclude.Min-
							// r.length[i].min()+1,
							// exclude.Max-1));
							IntervalDomain Update = new IntervalDomain(MinInt,
									exclude.Min - r.length[i].min());
							Update.addDom(exclude.Max, MaxInt);

							if (traceNarr) {
								System.out
										.print("7. Obligatory rectangles Narrow "
												+ r.origin[i] + " in " + Update);
							}

							// currentStore.in(r.origin[i], Update);

							r.origin[i].domain.in(currentStore.level,
									r.origin[i], Update);

							if (traceNarr)
								System.out.println(" -->" + r.origin[i]);
						}
					}
					// }
				}
			}

			// Update rectangles length in direction i
			// sort rectangles on increasing origin i
			IntRectangle maxRect = new IntRectangle(r.dim);
			for (int k = 0; k < r.dim; k++) {
				Domain rOrigin = r.origin[k].dom(), rLength = r.length[k].dom();
				if (k == i)
					maxRect.add(rOrigin.max(), rLength.max());
				else
					maxRect.add(rOrigin.min(), rLength.min());
			}

			ArrayList<IntRectangle> ConsideredRectDur = new ArrayList<IntRectangle>();
			for (IntRectangle t : UsedRectArray) {
				if (t.overlap(maxRect)) {
					ConsideredRectDur.add(t);
					barierSize += t.length[j];
				}
			}

			if (ConsideredRectDur.size() != 0
			// && rSize < ( barierSize + (rLengthJMin - 1) *
			// ConsideredRectDur.size())
			) {

				IntRectangle[] rects = new IntRectangle[ConsideredRectDur
						.size()];
				rects = ConsideredRectDur.toArray(rects);
				Arrays.sort(rects, new DimIMinComparator<IntRectangle>(i));

				// Object[] rects = ConsideredRectDur.toArray();
				// Comparator<Object> c = new DimIMinComparator<Object>(i);
				// Arrays.sort(rects, c);

				// System.out.println("Considered rectangles : " +
				// ConsideredRectDur);

				Profile barrier = new Profile(diff2);
				boolean lengthOK = true;
				int newMaxLength = 0;
				int n = 0;
				while (n < rects.length && lengthOK) {
					IntRectangle hinder = rects[n];
					barrier.addToProfile(hinder.origin[j], hinder.origin[j]
							+ hinder.length[j], 1);
					if (doesNotFit(j, r, barrier)) {
						lengthOK = false;
						newMaxLength = hinder.origin[i] -
						// ((Interval)r.origin[i].dom().lastElement()).Min;
								r.origin[i].min();
					}
					n++;
				}
				// System.out.println(barrier+ "-->> "+lengthOK
				// + ", new max length = " + newMaxLength);
				if (!lengthOK) {
					// update length in dimension j
					int maxLength = findMaxLength(i, newMaxLength, r);

					if (maxLength < r.length[i].max()) {
						if (traceNarr)
							System.out
									.println("9. Obligatory rectangles Narrow "
											+ r.length[i] + " in " + 0 + ".."
											+ maxLength);
						r.length[i].domain.in(currentStore.level, r.length[i],
								0, maxLength);
						// currentStore.in(r.length[i], 0, maxLength);
					}
				}
			}
		}
	}

	void narrowRectangle(Rectangle r, ArrayList<IntRectangle> UsedRect,
			ArrayList<Rectangle> ProfileCandidates) {

		if (trace) {
			System.out.println("Narrowing " + r);
			System.out.println(UsedRect);
		}

		for (int i = 0; i < r.dim; i++)
			// narrow in i-th dimension
			narrowIth(i, r, UsedRect, ProfileCandidates);
	}

	void narrowRectangles(HashSet<Variable> fdvQueue) {
		boolean needToNarrow = false;
		ArrayList<IntRectangle> UsedRect = new ArrayList<IntRectangle>();
		ArrayList<Rectangle> ProfileCandidates = new ArrayList<Rectangle>();

		for (Rectangle r : rectangles) {
			boolean settled = true, minLengthEq0 = false;
			int maxLevel = 0;
			for (int i = 0; i < r.dim; i++) {
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

				// if ( ! r.minLengthEq0() && // Check for rectangle r which has
				// // all lengths > 0
				// !(r.settled() && r.maxLevel()<currentStore.level()) ) {
				// // and are not fixed already

				// System.out.println(r+", "+ containsChangedVariable(r,
				// fdvQueue));
				needToNarrow = needToNarrow
						|| containsChangedVariable(r, fdvQueue);

				UsedRect.clear();
				ProfileCandidates.clear();
				boolean ntN = findRectangles(r, UsedRect, ProfileCandidates,
						fdvQueue);
				needToNarrow = needToNarrow || ntN;

				// Checking r against all s with minUse in the domain of r
				if (needToNarrow)
					narrowRectangle(r, UsedRect, ProfileCandidates);
			}
		}
	}

	boolean notFit(int i, Rectangle r, ArrayList<IntRectangle> ConsideredRect,
			int barierPosition) {
		Profile barrier = new Profile(diff2);
		int minimalAfter = 0;
		int j = 0;
		boolean excludedState = true;
		while (excludedState && j < r.dim) {
			if (i != j) {
				// System.out.println(r.toStringFull()+"\n"+ConsideredRect );
				Domain rOriginJdom = r.origin[j].dom();
				Domain rLengthJdom = r.length[j].dom();
				int minJ = rOriginJdom.min();
				int maxJ = rOriginJdom.max() + rLengthJdom.min();
				int durJ = rLengthJdom.min();

				int currentJposition = minJ;
				// System.out.println("max position for r=" + maxJ+
				// ", min duration of r=" + durJ+", start position=" +
				// currentJposition);
				barrier.clear();
				for (IntRectangle hinder : ConsideredRect) {
					// System.out.println("["+ hinder.origin[j] + ".."
					// + (int)(hinder.origin[j]
					// + hinder.length[j]) +"), "
					// + (int)(hinder.origin[i] + hinder.length[i] -
					// barierPosition));
					int hinderJ = hinder.origin[j];
					int hinderValue = hinder.origin[i] + hinder.length[i]
							- barierPosition;
					barrier.addToProfile(hinderJ, hinderJ + hinder.length[j],
							hinderValue);
					if (minimalAfter > hinderValue)
						minimalAfter = hinderValue;
				}
				// System.out.println("Barrier : " + barrier);

				int k = 0, barrierSize = barrier.size();
				while (k < barrierSize && excludedState) {
					ProfileItem p = barrier.get(k);
					int hinderStart = p.min;
					int hinderStop = p.max;
					if (hinderStart - currentJposition >= durJ)
						excludedState = false;
					currentJposition = hinderStop;
					k++;
					// System.out.println("Hinder = " + hinderStart + ".." +
					// hinderStop);
					// System.out.println("*** Excluded = " + excludedState);
				}
				if (excludedState && maxJ - currentJposition >= durJ)
					excludedState = false;

				if (excludedState) {
					ProfileItem first = barrier.get(0);
					ProfileItem last = barrier.get(barrier.size() - 1);
					if (minJ < first.min) // exist free space before first
						// obstacle
						barrier.addToProfile(minJ, first.min, minimalAfter);
					if (maxJ > last.max) // exist free space after last
						// obstacle
						barrier.addToProfile(last.max, maxJ, minimalAfter);
					ArrayList<Interval> toAdd = new ArrayList<Interval>();
					for (int m = 0; m < barrier.size() - 1; m++) {
						ProfileItem p = barrier.get(m);
						ProfileItem pNext = barrier.get(m + 1);
						if (p.max != pNext.min)
							toAdd.add(new Interval(p.max, pNext.min));
					}
					// for (ProfileItem p : barrier) System.out.print(p + " ");
					for (Interval v : toAdd) {
						// System.out.println("\n*** adding " + v);
						barrier.addToProfile(v.min, v.max, minimalAfter);
					}
					// for (ProfileItem p : barrier) System.out.print(p + " ");
					// System.out.println("minimalAfter = " + minimalAfter);

					int minSizeAfterBarier = MaxInt;
					for (int m = 0; m < barrier.size(); m++) {
						ProfileItem p = barrier.get(m);
						if (p.value < minSizeAfterBarier) {
							if (p.value == minimalAfter
									&& p.max - p.min >= durJ) {
								minSizeAfterBarier = minimalAfter;
								break;
							}
							if (p.value > minimalAfter)
								minSizeAfterBarier = p.value;
						}
					}
					minPosition = minSizeAfterBarier;

					// System.out.print("==> [");
					// for (ProfileItem p : barrier) System.out.print(p + " ");
					// System.out.println("], minSizeAfterBarrier = " +
					// minSizeAfterBarier);
				}
			}
			j++;
		}

		// System.out.println("2. " + excludedState + ", minPosition = " +
		// minPosition);
		return excludedState;
	}

	void profileCheckInterval(Store store, DiffnProfile Profile, int limit,
			Variable Start, Variable Duration, int iMin, int i_max,
			Variable Resources) {

		int dur = Duration.min();
		int iMax = i_max + dur;
		for (ProfileItem p : Profile) {
			if (trace)
				System.out.println("Comparing " + "[" + iMin + ", " + i_max
						+ "]" + " with profile item " + p);

			if (intervalOverlap(iMin, iMax, p.min, p.max)) {
				if (limit - p.value < Resources.min()) {
					// Check for possible narrowing of Start or fail
					Domain StartDom = Start.dom();
					int updateMin = p.min - dur + 1, updateMax = p.max - 1;
					// FD Update = new FD(updateMin, updateMax);
					if (!(updateMin > StartDom.max() || updateMax < StartDom
							.min())) {

						IntervalDomain Update = new IntervalDomain(MinInt,
								p.min - dur);
						Update.addDom(p.max, MaxInt);
						// Domain Update = new FD(MinInt, p.Min - dur);
						// Update.addDom(p.Max, MaxInt);

						if (traceNarr)
							System.out.print("6. Profile Narrowed " + Start
									+ " \\ " + Update);

						Start.domain.in(store.level, Start, Update);

						// store.in(Start, Update);

						if (traceNarr)
							System.out.println(" => " + Start);
					}
				} else {
					Domain StartDom = Start.dom();
					int start = StartDom.max(), stop = StartDom.min() + dur;
					if (start < stop
							&& intervalOverlap(start, stop, p.min, p.max)) {
						int updateMax = limit - p.value;
						if (updateMax < Resources.max()) {
							IntervalDomain Update = new IntervalDomain(0,
									updateMax);
							// Domain Update = new FD(0, updateMax);
							if (traceNarr)
								System.out.println("8. Profile Narrowed "
										+ Resources + " in " + Update);

							Resources.domain.in(store.level, Resources, Update);
							// store.in(Resources, Update);

							if (traceNarr)
								System.out.println(" => " + Resources);
						}
					}
				}
			}
		}
	}

	void profileCheckRectangle(DiffnProfile Profile, Rectangle r, int i, int j) {

		Variable s = r.origin[i];
		Variable dur = r.length[i];
		Variable resUse = r.length[j];
		Domain rOriginJdom = r.origin[j].dom();
		int limit = rOriginJdom.max() + resUse.max() - rOriginJdom.min();

		if (trace)
			System.out.println("Start time = " + s + ", resource use = "
					+ resUse);

		Domain sDom = s.dom();

		for (int m = 0; m < sDom.noIntervals(); m++) {
			profileCheckInterval(currentStore, Profile, limit, s, dur, sDom
					.leftElement(m), sDom.rightElement(m), resUse);
		}
	}

	void profileNarrowing(int i, Rectangle r,
			ArrayList<Rectangle> ProfileCandidates) {
		// check profile first

		Domain rOriginIdom = r.origin[i].dom();
		int rOriginIdomMin = rOriginIdom.min();
		int rOriginIdomMax = rOriginIdom.max();
		DiffnProfile Profile = new DiffnProfile();

		for (int j = 0; j < r.dim; j++) {
			if (j != i) {
				Profile.make(j, i, r, rOriginIdomMin, rOriginIdomMax
						+ r.length[i].min(), ProfileCandidates);

				if (Profile.size() != 0) {
					if (trace) {
						System.out.println(r + "\n" + ProfileCandidates);
						System.out.println("Profile in dimension " + i
								+ " and " + j + "\n" + Profile);
					}

					profileCheckRectangle(Profile, r, i, j);

				}
			}
		}
	}

	@Override
	public void queueVariable(int level, Variable V) {
		if (level == stamp)
			variableQueue.add(V);
		else {
			variableQueue.clear();
			stamp = level;
			variableQueue.add(V);
		}
	}

	@Override
	public void removeConstraint() {
		for (Rectangle R : rectangles) {
			for (int i = 0; i < R.dim; i++) {
				Variable v = R.origin[i];
				v.removeConstraint(this);
				v = R.length[i];
				v.removeConstraint(this);
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
			int j = i + 1;
			while (sat && j < rectangles.length) {
				rectj = rectangles[j];
				sat = sat && !recti.domOverlap(rectj);
				j++;
			}
			i++;
		}
		return sat;
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : diff (");

		int i = 0;
		for (Rectangle R : rectangles) {
			result.append(R);
			if (i < rectangles.length - 1)
				result.append(", ");
			i++;
		}
		return result.append(")").toString();
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_diff);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		for (int i = 0; i < rectangles.length; i++)
			for (int j = 0; j < rectangles[i].dim(); j++) {
				scopeVars.add(rectangles[i].origin(j));
				scopeVars.add(rectangles[i].length(j));
			}

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));			
		
		for (int i = 0; i < rectangles.length; i++) {

			org.jdom.Element rect = rectangles[i].toXML();
			constraint.addContent(rect);

		}

		return constraint;

	}

    @Override
	public short type() {
		return type;
	}

	/**
	 * It constructs the constraint from XML description. 
	 * @param constraint an XCSP description of the constraint.
	 * @param store constraint store in which the constraint is being created.
	 * @return constraint constructed from XML element.
	 */
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

		return new Diff(rects);	
		
	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			for (Rectangle r : rectangles) { 
				for (Variable v : r.length) v.weight++;
				for (Variable v : r.origin) v.weight++;
			}
		}
	}

	class DimIMinComparator<T extends IntRectangle> implements Comparator<T> {

		int i;

		DimIMinComparator() {
		}

		DimIMinComparator(int dimension) {
			i = dimension;
		}

		public int compare(T o1, T o2) {
			int v1 = o1.origin[i];
			int v2 = o2.origin[i];
			return v1 - v2;
		}

	}

	class Pair {
		int Min, Max;

		Pair(int i1, int i2) {
			Min = i1;
			Max = i2;
		}
	}
	
}



