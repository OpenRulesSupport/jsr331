/**
 *  Cumulative.java 
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

import JaCoP.constraints.Constraint;
import JaCoP.core.Constants;
import JaCoP.core.Domain;
import JaCoP.core.Interval;
import JaCoP.core.IntervalDomain;
import JaCoP.core.JaCoPException;
import JaCoP.core.Store;
import JaCoP.core.Variable;

/**
 * Cumulative implements the cumulative/4 constraint using edge-finding
 * algorithm and profile information on the resource use.
 * 
 * @author Krzysztof Kuchcinski and Radoslaw Szymanek
 * @version 2.4
 */

public class Cumulative extends Constraint implements Constants {

	static int IdNumber = 1;

	static final boolean debug = false, debugNarr = false;

	final static short type = cumul;

	/**
	 * It specifies if the edge finding algorithm should be used.
	 */
	public boolean doEdgeFinding = true;
	
	
	/**
	 * It specifies if the profiles should be computed to propagate 
	 * onto limit variable.
	 */
	public boolean doProfile = true;

	Variable limit;

	/**
	 * It contains information about maximal profile contributed by tasks.
	 */
	public Profile maxProfile = null;

	/**
	 * It contains information about minimal profile contributed by regions
	 * for certain occupied by tasks.
	 */
	public Profile minProfile = null;

	CumulativeProfiles cumulativeProfiles = new CumulativeProfiles();

	Task Ts[];

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 */
	public Cumulative(ArrayList<? extends Variable> starts,
					  ArrayList<? extends Variable> durations, 
					  ArrayList<? extends Variable> resources,
					  Variable limit) {

		queueIndex = 2;
		
		numberId = IdNumber++;

		if (starts.size() == durations.size() && durations.size() == resources.size()) {
			Ts = new Task[starts.size()];
			for (int i = 0; i < starts.size(); i++) {
				if (((Variable) durations.get(i)).min() >= 0
						&& ((Variable) resources.get(i)).min() >= 0) {
					Ts[i] = new Task((Variable) starts.get(i),
							(Variable) durations.get(i), (Variable) resources.get(i));
					numberArgs = (short) (numberArgs + 3);
				} else {
					throw new JaCoPException("\nDurations and resources must be > 0= in cumulative");
				}
			}
			if (limit.min() >= 0) {
				this.limit = limit;
				numberArgs++;
			} else {
				throw new JaCoPException("\nResource limit must be >= 0 in cumulative");
			}
		} else {
			throw new JaCoPException( "\nNot equal sizes of Variable vectors in cumulative" );
		}
	}

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 * @param edgeFinding true if edge finding algorithm should be used.
	 */
	public Cumulative(ArrayList<? extends Variable> starts,
					  ArrayList<? extends Variable> durations, 
					  ArrayList<? extends Variable> resources,
					  Variable limit, 
					  boolean edgeFinding) {
		this(starts, durations, resources, limit);
		doEdgeFinding = edgeFinding;
	}

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 * @param edgeFinding true if edge finding algorithm should be used.
	 * @param profile specifies if the profiles should be computed in order to reduce limit variable.
	 */
	public Cumulative(ArrayList<? extends Variable> starts,
					  ArrayList<? extends Variable> durations, 
					  ArrayList<? extends Variable> resources,
					  Variable limit, 
					  boolean edgeFinding, 
					  boolean profile) {
		this(starts, durations, resources, limit);
		doEdgeFinding = edgeFinding;
		doProfile = profile;
	}

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 */
	public Cumulative(Variable[] starts, 
					  Variable[] durations,
					  Variable[] resources,
					  Variable limit) {

		queueIndex = 2;

		numberId = IdNumber++;

		if (starts.length == durations.length && durations.length == resources.length) {
			Ts = new Task[starts.length];
			for (int i = 0; i < starts.length; i++) {
				if (((Variable) durations[i]).min() >= 0
						&& ((Variable) resources[i]).min() >= 0) {
					Ts[i] = new Task((Variable) starts[i], (Variable) durations[i],
							(Variable) resources[i]);
					numberArgs = (short) (numberArgs + 3);
				} else {
					throw new JaCoPException("\nDurations and resources must be > 0= in cumulative");
				}
			}
			if (limit.min() >= 0) {
				this.limit = limit;
				numberArgs++;
			} else {
				throw new JaCoPException( "\nResource limit must be >= 0 in cumulative" );
			}
		} else {
			throw new JaCoPException( "\nNot equal sizes of Variable vectors in cumulative" );
		}
	}

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 * @param edgeFinding true if edge finding algorithm should be used.
	 */

	public Cumulative(Variable[] starts, 
					  Variable[] durations, 
					  Variable[] resources,
					  Variable limit, 
					  boolean edgeFinding) {
		this(starts, durations, resources, limit);
		doEdgeFinding = edgeFinding;
	}

	/**
	 * It creates a cumulative constraint.
	 * @param starts variables denoting starts of the tasks.
	 * @param durations variables denoting durations of the tasks.
	 * @param resources variables denoting resource usage of the tasks.
	 * @param limit the overall limit of resources which has to be used.
	 * @param edgeFinding true if edge finding algorithm should be used.
	 * @param profile specifies if the profiles should be computed in order to reduce limit variable.
	 */
	public Cumulative(Variable[] starts,
					  Variable[] durations,
					  Variable[] resources,
					  Variable limit, 
					  boolean edgeFinding, 
					  boolean profile) {
		this(starts, durations, resources, limit);
		doEdgeFinding = edgeFinding;
		doProfile = profile;
	}

	boolean after(Task l, ArrayList<Task> S) {
		int startS = MaxInt;
		long a = 0;
		boolean afterS = true;

		if (S.size() > 0) {
			if (debug)
				System.out.println("Checking if " + l + " can be after " + S);
			for (Task t : S) {
				int tEST = t.EST();
				if (tEST <= startS)
					startS = tEST;
				a += t.areaMin();
			}

			afterS = ((l.LCT() - startS) * limit.max() - a >= l.areaMin());

			if (debug)
				System.out.println("s(S')= " + startS + ",  c(l)= " + l.LCT()
						+ ",  a(Sp)= " + a + ",  afterS= " + afterS);
		}
		return afterS;
	}

	@Override
	public ArrayList<Variable> arguments() {

		ArrayList<Variable> Variables = new ArrayList<Variable>(1);

		for (Task t : Ts)
			Variables.add(t.start);
		for (Task t : Ts)
			Variables.add(t.dur);
		for (Task t : Ts)
			Variables.add(t.res);
		Variables.add(limit);
		return Variables;
	}

	boolean before(Task l, ArrayList<Task> S) {
		int completionS = MinInt;
		int a = 0;
		boolean beforeS = true;

		if (S.size() > 0) {
			if (debug)
				System.out.println("Checking if " + l.toString()
						+ " can be before tasks in " + S.toString());
			for (Task t : S) {
				int tLCT = t.LCT();
				if (tLCT >= completionS)
					completionS = tLCT;
				a += t.areaMin();
			}

			beforeS = ((completionS - l.EST()) * limit.max() >= a + l.areaMin());

			if (debug)
				System.out.println("s(l)= " + l.EST() + ",  c(S')= "
						+ completionS + ",  a(Sp)= " + a + ",  beforeS= "
						+ beforeS);
		}
		return beforeS;
	}

	boolean between(Task l, ArrayList<Task> S) {
		int completionS = MinInt, startS = MaxInt;
		long a = 0, larea = 0;
		boolean betweenS = true;

		if (S.size() > 0) {
			if (debug)
				System.out.println("Checking if " + l
						+ " can be between tasks in " + S);
			for (Task t : S) {
				int tLCT = t.LCT();
				int tEST = t.EST();
				if (tLCT >= completionS)
					completionS = tLCT;
				if (tEST <= startS)
					startS = tEST;
				a += minOverlap(t, startS, completionS);
			}
			larea = minOverlap(l, startS, completionS);

			betweenS = ((completionS - startS) * limit.max() >= a + larea);
			if (debug)
				System.out.println("s(S')= " + startS + ",  c(S')= "
						+ completionS + ",  a(Sp)= " + a + ", l_area= " + larea
						+ ",  betweenS= " + betweenS);
		}
		return betweenS;
	}

	@Override
	public void removeLevel(int level) {
	}

	@Override
	public void consistency(Store store) {

		while (store.newPropagation) {
			store.newPropagation = false;
			if (doProfile) {
				cumulativeProfiles.make(Ts);
				minProfile = cumulativeProfiles.minProfile();
				maxProfile = cumulativeProfiles.maxProfile();
				if (debug)
					System.out
							.println("\n--------------------------------------"
									+ "\nMinProfile for "
									+ id()
									+ " :"
									+ minProfile
									+ "\nMaxProfile for "
									+ id()
									+ " :"
									+ maxProfile
									+ "\n--------------------------------------");

				limit.domain.in(store.level, limit, minProfile.max(),
						maxProfile.max());
				updateTasksRes(store);
				profileCheckTasks(store);
			}

			// Do edge finding only the last time and when
			// max limit is 1 (heuristic) !!!
			if (doEdgeFinding && !store.newPropagation) {
				// Phase-up - from highest lct down
				edgeFindingUp(store);
				// Phase-down - from lowest est up
				edgeFindingDown(store);
			}
		}
		minProfile = null;
		maxProfile = null;
	}

	void edgeFindingDown(Store store) {

		TreeSet<Domain> estUpList = new TreeSet<Domain>(
				new DomainminComparator<Domain>());
		ArrayList<Task> S = new ArrayList<Task>(Ts.length), L = new ArrayList<Task>(
				Ts.length);
		int est0;
		Task t, l; // s, r=null;
		int compl; // startl, durl
		long totalArea = 0;
		int estS = 0;

		if (debug)
			System.out
					.println("------------------------------------------------\n"
							+ "Edge Finding Down\n"
							+ "------------------------------------------------");
		for (Task T : Ts)
			estUpList.add(T.start.dom());

		for (Domain EST : estUpList) {
			est0 = EST.min();
			if (debug)
				System.out.println("est0 = " + est0 + "\n=================");

			// Create S = {t|EST(t) >= est0}
			S.clear();
			// Create L = {t|EST(t) < est0 && LCT(t) > est0}
			L.clear();
			for (Task T : Ts) {
				if (T.dur.min() > 0 && T.res.min() > 0) {
					if (T.EST() >= est0)
						S.add(T);
					if (T.EST() < est0 && T.LCT() > est0)
						L.add(T);
				}
			}
			if (debug) {
				System.out.println("S = " + S);
				System.out.println("L = " + L);
			}

			// update upper bound if T cannot be the last in S
			for (Task T : S)
				notLast(store, T, S);

			if (S.size() != 0 && !fitTasksAfter(S, est0))
				store.throwFailException(this);

			while (S.size() != 0 && L.size() != 0) {
				// Select task l from L with the maximal area
				int indexOfl = maxArea(L);
				l = L.get(indexOfl);
				int l_LCT = l.LCT();
				int limitMax = limit.max();
				// System.out.println("Maxumum area task= " + l);
				// Checking if l can be between and after S

				boolean AFTER = true, BETWEEN = true;

				int startOfS = MaxInt, completionOfS = MinInt;
				long area1 = 0, area2 = 0, larea = 0;
				if (debug)
					System.out.println("Checking if " + l + " can be after "
							+ S);
				for (Task tt : S) {
					int tEST = tt.EST();
					int tLCT = tt.LCT();
					if (tEST <= startOfS)
						startOfS = tEST;
					if (tLCT >= completionOfS)
						completionOfS = tLCT;
					area1 += tt.areaMin();
					area2 += minOverlap(tt, startOfS, completionOfS);
				}
				totalArea = area1;
				estS = startOfS;
				AFTER = ((l_LCT - startOfS) * limitMax - area1 >= l.areaMin());

				// larea = l.dur.min()*l.res.min();
				larea = minOverlap(l, startOfS, completionOfS);
				BETWEEN = ((completionOfS - startOfS) * limitMax >= area2
						+ larea);

				if (AFTER && BETWEEN) {
					L.remove(indexOfl);
					removeFromS_Lct(S);
				} else {
					if (BETWEEN && !AFTER) {
						// update upper bound of l
						long slack, a = 0;
						int newCompl = MaxInt, startS, newStartl = MinInt;
						int maxuse = limitMax - l.res.min();

						compl = l_LCT;
						a = totalArea;
						startS = estS;
						slack = (l_LCT - startS) * limitMax - a - l.areaMin();

						int j = 0;
						ArrayList<Task> Tasks = new ArrayList<Task>();
						while (slack < 0 && j < S.size()) {
							t = S.get(j);

							if (t.res.min() <= maxuse || l_LCT <= t.LST()) {
								slack += t.areaMin();
							} else {
								Tasks.add(t);
							}
							j++;
						}

						if (slack < 0 && Tasks.size() != 0) {
							Task[] TaskArr = new Task[Tasks.size()];
							TaskArr = Tasks.toArray(TaskArr);
							Arrays.sort(TaskArr,
									new TaskDescLSTComparator<Task>());
							j = 0;
							int limitMin = limit.min();
							while (slack < 0 && j < TaskArr.length) {
								t = (Task) TaskArr[j];
								j++;
								newCompl = t.LST();
								slack = slack - (compl - newCompl) * limitMin
										+ t.areaMin();
								compl = newCompl;
							}

							newStartl = compl - l.dur.min();
							if (newStartl < l.LST()) {
								if (debugNarr)
									System.out
											.println(">>> Cumulative EF <<< 2. Narrowed "
													+ l.start
													+ " in "
													+ MinInt
													+ ".." + newStartl);

								l.start.domain.inMax(store.level, l.start,
										newStartl);

							}
						}

						if (before(l, S))
							L.remove(indexOfl);
						else
							removeFromS_Lct(S);
					} else {
						if (!BETWEEN && AFTER)
							L.remove(indexOfl);
						else {
							// ! BETWEEN && ! AFTER => BEFORE
							// l must be before S
							// update upper bound of l.Start and l.Dur

							if (debug)
								System.out.println("AFTER=" + AFTER
										+ " BETWEEN=" + BETWEEN + "!!!");

							int durationOfS = 0;
							compl = 0;
							for (Task T : S) {
								durationOfS += T.dur.min() * T.res.min();
								if (T.LCT() > compl) {
									compl = T.LCT();
								}
							}

							int finish = compl
									- (durationOfS + l.dur.min() * l.res.min())
									/ limit.max();
							if (l.start.max() > finish) {
								if (debugNarr)
									System.out
											.println(l
													+ " must be before\n"
													+ S
													+ "\n>>> Cumulative EF <<< 3. Narrowed "
													+ l.start + " in " + MinInt
													+ ".." + finish);

								l.start.domain.inMax(store.level, l.start,
										finish);
							}

							L.remove(indexOfl);
						}
					}
				}
			}
		}
	}

	void edgeFindingUp(Store store) {

		TreeSet<Domain> lctDownList = new TreeSet<Domain>(
				new DomainmaxComparator<Domain>());
		ArrayList<Task> S = new ArrayList<Task>(), L = new ArrayList<Task>();
		int lct0;
		Task t, l; // s
		int startl; // , durl, compl;
		long totalArea = 0;
		int lctS = 0;

		if (debug)
			System.out
					.println("------------------------------------------------\n"
							+ "Edge Finding Up\n"
							+ "------------------------------------------------");
		for (Task T : Ts)
			lctDownList.add(T.Completion());
		for (Domain LCT : lctDownList) {

			lct0 = LCT.max();
			if (debug)
				System.out.println("lct0 = " + lct0 + "\n=================");

			// Create S = {t|EST(t) <= lct0}
			S.clear();
			// Create L = {t|EST(t) < lct0 && LCT(t) > lct0}
			L.clear();
			for (Task T : Ts) {
				if (T.dur.min() > 0 && T.res.min() > 0) {
					if (T.LCT() <= lct0)
						S.add(T);
					if (T.EST() < lct0 && T.LCT() > lct0)
						L.add(T);
				}
			}
			if (debug) {
				System.out.println("\nS = " + S);
				System.out.println("L = " + L);
			}

			// update lower bound if T cannot be the first in S
			for (Task T : S)
				notFirst(store, T, S);

			if (S.size() != 0 && !fitTasksBefore(S, lct0))
				store.throwFailException(this);

			while (S.size() != 0 && L.size() != 0) {
				// Select task l from L with the maximal area
				int indexOfl = maxArea(L);
				l = L.get(indexOfl);
				int l_EST = l.EST();
				int limitMax = limit.max();
				// System.out.println("Maxumum area task= " + l);
				// Checking if l can be between and before S

				boolean BEFORE = true, BETWEEN = true;

				int completionOfS = MinInt, startOfS = MaxInt;
				long area1 = 0, area2 = 0, larea = 0;
				if (debug)
					System.out.println("Checking if " + l
							+ " can be before or between tasks in " + S);

				for (Task tt : S) {
					int tLCT = tt.LCT();
					int tEST = tt.EST();
					if (tLCT >= completionOfS)
						completionOfS = tLCT;
					area1 += tt.areaMin();
					if (tEST <= startOfS)
						startOfS = tEST;
					area2 += minOverlap(tt, startOfS, completionOfS);
				}

				totalArea = area1;
				lctS = completionOfS;
				BEFORE = ((completionOfS - l_EST) * limitMax >= area1
						+ l.areaMin());

				// larea = l.dur.min()*l.res.min();
				larea = minOverlap(l, startOfS, completionOfS);
				BETWEEN = ((completionOfS - startOfS) * limitMax >= area2
						+ larea);

				if (debug)
					System.out.println("BEFORE=" + BEFORE + " BETWEEN="
							+ BETWEEN + " completionOfS=" + completionOfS
							+ " startOfS=" + startOfS + " area2=" + area2
							+ "  larea=" + larea + "\nS = " + S + "\nl = " + l);

				if (BEFORE && BETWEEN) {
					L.remove(indexOfl);
					removeFromS_Est(S);
				} else {
					if (BETWEEN && !BEFORE) {
						// update lower bound of l

						long slack, a = 0;
						int completionS, // newCompl=MaxInt,
						newStartl = MinInt;
						int maxuse = limitMax - l.res.min();

						startl = l_EST;
						a = totalArea;
						completionS = lctS;
						slack = (completionS - l_EST) * limitMax - a
								- l.areaMin();

						int j = 0;
						ArrayList<Task> Tasks = new ArrayList<Task>();
						while (slack < 0 && j < S.size()) {
							t = S.get(j);

							if (t.res.min() <= maxuse || l_EST >= t.ECT()) {
								slack += t.areaMin();
							} else {
								Tasks.add(t);
							}
							j++;
						}

						if (slack < 0 && Tasks.size() != 0) {
							Task[] TaskArr = new Task[Tasks.size()];
							TaskArr = Tasks.toArray(TaskArr);
							Arrays.sort(TaskArr,
									new TaskAscECTComparator<Task>());

							j = 0;
							int limitMin = limit.min();
							while (slack < 0 && j < TaskArr.length) {
								t = (Task) TaskArr[j];
								j++;
								newStartl = t.ECT();
								slack = slack - (newStartl - startl) * limitMin
										+ t.areaMin();
								startl = newStartl;
							}
						}

						if (newStartl > l_EST) {
							if (debugNarr)
								System.out
										.println(">>> Cumulative EF <<< 0. Narrowed "
												+ l.start
												+ " in "
												+ startl
												+ ".." + MaxInt);

							l.start.domain.inMin(store.level, l.start,
									newStartl);

						}

						if (after(l, S))
							L.remove(indexOfl);
						else
							removeFromS_Est(S);
					} else {
						if (!BETWEEN && BEFORE)
							L.remove(indexOfl);
						else {
							// ! BETWEEN && ! BEFORE => AFTER S
							// l must be after S
							// update lower bound of l
							if (debug)
								System.out.println("BEFORE=" + BEFORE
										+ " BETWEEN=" + BETWEEN + "!!!");

							int durationOfS = 0;
							for (Task T : S)
								durationOfS += T.dur.min() * T.res.min();

							int start = startOfS + durationOfS / limit.max();
							if (start > l.start.min()) {
								if (debugNarr)
									System.out
											.println(l
													+ " must be after\n"
													+ S
													+ "\n>>> Cumulative EF <<< 1. Narrowed "
													+ l.start + " in " + start
													+ ".." + MaxInt);
								l.start.domain.inMin(store.level, l.start,
										start);
							}

							L.remove(indexOfl);
						}
					}
				}
			}
		}
	}

	int est(ArrayList<Task> S) {
		int estS = MaxInt;

		for (Task t : S) {
			int tEST = t.EST();
			if (tEST < estS)
				estS = tEST;
		}
		return estS;
	}

	boolean fitTasksAfter(ArrayList<Task> S, int est0) {
		int durOfS = 0, lctOfS = MinInt, maxCompl = MaxInt, minDur = MaxInt, minRes = MaxInt;
		boolean FitAfter;

		for (Task t : S) {
			maxCompl = t.LCT();
			int Dur = t.dur.min();
			int Res = t.res.min();

			if (lctOfS < maxCompl)
				lctOfS = maxCompl;
			if (minDur > Dur)
				minDur = Dur;
			if (minRes > Res)
				minRes = Res;

			durOfS += Dur * Res;
		}

		int limitMax = limit.max();
		long availableArea = (lctOfS - est0) * limitMax;
		if (debug)
			System.out.println("Fit tasks of " + S + " after " + est0 + " = "
					+ (availableArea >= durOfS));
		FitAfter = availableArea >= durOfS;
		if (FitAfter)
			FitAfter = ((lctOfS - est0) / minDur) * (limitMax / minRes) >= S
					.size();
		return FitAfter;
	}

	boolean fitTasksBefore(ArrayList<Task> S, int lct0) {
		int durOfS = 0, estOfS = MaxInt, minStart = 0, minDur = MaxInt, minRes = MaxInt;
		boolean FitBefore;

		for (Task t : S) {
			minStart = t.EST();
			int Dur = t.dur.min();
			int Res = t.res.min();

			if (estOfS > minStart)
				estOfS = minStart;
			if (minDur > Dur)
				minDur = Dur;
			if (minRes > Res)
				minRes = Res;

			durOfS += Dur * Res;
		}

		int limitMax = limit.max();
		long availableArea = (lct0 - estOfS) * limitMax;
		if (debug)
			System.out.println("Fit tasks of " + S + " before " + lct0 + " = "
					+ " Available are: " + availableArea + " Area: " + durOfS);

		FitBefore = availableArea >= durOfS;
		if (FitBefore)
			FitBefore = ((lct0 - estOfS) / minDur) * (limitMax / minRes) >= S
					.size();
		return FitBefore;
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
			return Constants.BOUND;
	}

	Task[] getTasks() {
		return Ts;
	}

	@Override
	public String id() {
		
		if (id != null)
			return id;
		else
			return  id_cumulative + numberId;
	
	}

	@Override
	public void impose(Store store) {

		for (Task T : Ts) {
			T.start.putModelConstraint(this, getConsistencyPruningEvent(T.start));
			T.dur.putModelConstraint(this, getConsistencyPruningEvent(T.dur));
			T.res.putModelConstraint(this, getConsistencyPruningEvent(T.res));
		}

		limit.putModelConstraint(this, getConsistencyPruningEvent(limit));

		store.addChanged(this);
		store.countConstraint();
	}

	boolean intervalOverlap(int min1, int max1, int min2, int max2) {
		return !(min1 >= max2 || max1 <= min2);
	}

	int lct(ArrayList<Task> S) {
		int lctS = MinInt;

		for (Task t : S) {
			int tLCT = t.LCT();
			if (tLCT > lctS)
				lctS = tLCT;
		}
		return lctS;
	}

	int maxArea(ArrayList<Task> Ts) {
		long area = 0, newArea = 0;
		// Task t;
		int index = 0; // Ts_size=Ts.size();

		// Select task with the maximal area
		int i = 0;
		for (Task t : Ts) {
			newArea = t.areaMin();
			if (area < newArea) {
				area = newArea;
				index = i;
			}
			i++;
		}
		return index;
	}

	long minOverlap(Task t, int est, int lct) {

		int tDur_min = 0;
		int tdur = t.dur.min();
		int tect = t.ECT();
		int tlst = t.LST();
		int temp1 = tect - est;
		int temp2 = lct - tlst;

		if (est <= tlst)
			if (tect >= lct)
				// |---t----|
				// |--------------|
				// est lct
				tDur_min = temp2 < tdur ? temp2 : tdur;
			else
				// tect < lct
				// |---t----|
				// |--------------|
				// est lct
				tDur_min = tdur;
		else
		// est > tlst
		if (tect > est)
			if (tect <= lct)
				// |---t----|
				// |--------------|
				// est lct
				tDur_min = temp1 < tdur ? temp1 : tdur;
			else
				// tect > lct
				// |--------t---------|
				// |--------------|
				// est lct
				tDur_min = lct - est < tdur ? lct - est : tdur;
		else
			// tect <= est
			tDur_min = 0;
		return tDur_min * t.res.min();
	}

	void notFirst(Store store, Task s, ArrayList<Task> S) {
		int sEST = s.EST(); // sLCT = s.LCT();
		int completionS = MinInt, newStartl = MinInt, startl = sEST;
		long a = 0, slack, maxuse = limit.max() - s.res.min();
		boolean notBeforeS;

		if (S.size() > 0) {
			if (debug)
				System.out.println("Not first " + s + " in " + S);
			for (Task t : S) {
				// if ( ! t.equals(s) ) {
				if (t != s) {
					int tLCT = t.LCT();
					if (tLCT >= completionS)
						completionS = tLCT;
					a += t.areaMin();
				}
			}
			slack = (completionS - sEST) * limit.max() - a - s.areaMin();
			// System.out.println("slack = "+ slack);
			notBeforeS = (slack < 0);
			if (debug)
				System.out.println("s(l)= " + sEST + ",  c(S')= " + completionS
						+ ",  a(S)= " + a + ",  notBeforeS= " + notBeforeS);

			// Upadate LB for task s

			int j = 0;
			ArrayList<Task> Tasks = new ArrayList<Task>();
			while (slack < 0 && j < S.size()) {
				Task t = S.get(j);
				// if ( ! t.equals(s) ) {
				if (t != s) {
					// System.out.println("s= "+s + ", t= " + t +
					// "\nmaxuse = " + maxuse + ", " +
					// t.res.min());

					if (t.res.min() <= maxuse || sEST >= t.ECT()) {
						slack += t.areaMin();
					} else {
						Tasks.add(t);
					}
				}
				j++;
			}

			// System.out.println("slack after = " + slack +
			// "Tasks = " + Tasks );
			if (slack < 0 && Tasks.size() != 0) {
				Task[] TaskArr = new Task[Tasks.size()];
				TaskArr = Tasks.toArray(TaskArr);
				Arrays.sort(TaskArr, new TaskAscECTComparator<Task>());

				j = 0;
				int limitMin = limit.min();
				while (slack < 0 && j < TaskArr.length) {
					Task t = (Task) TaskArr[j];
					j++;
					newStartl = t.ECT();
					slack = slack - (newStartl - startl) * limitMin
							+ t.areaMin();
					startl = newStartl;
				}

				if (newStartl > sEST) {
					if (debugNarr)
						System.out.println(">>> Cumulative EF <<< 4. Narrowed "
								+ s.start + " in " + startl + ".." + MaxInt);
					// store.in(s.start, newStartl, MaxInt);

					s.start.domain.inMin(store.level, s.start, newStartl);

					// if ( s.LaCT() - newStartl < ((Variable)s.dur).max() ) {
					// if ( traceNarr )
					// System.out.println(", "+
					// (Variable)s.dur +
					// " in " +
					// 0 + ".." + (int)(s.LaCT() - newStartl));
					// store.in(s.dur, 0, s.LaCT() - newStartl );
					// }
				}
			}
		}
	}

	void notLast(Store store, Task s, ArrayList<Task> S) {
		int sLCT = s.LCT(); // sEST = s.EST();
		int compl = sLCT;

		int startS = MaxInt, newCompl = MaxInt, newStartl = MinInt;
		long a = 0, slack, maxuse = limit.max() - s.res.min();
		boolean notLastInS;

		if (S.size() > 0) {
			if (debug)
				System.out.println("Not last " + s + " in " + S);
			for (Task t : S) {
				if (t != s) {
					int tEST = t.EST();
					if (tEST <= startS)
						startS = tEST;
					a += t.areaMin();
				}
			}
			slack = (sLCT - startS) * limit.max() - a - s.areaMin();
			notLastInS = (slack < 0);

			if (debug)
				System.out.println("s(S')= " + startS + ",  c(l)= " + sLCT
						+ ",  a(S)= " + a + ",  notLastInS= " + notLastInS);

			// Upadate UB for task s

			int j = 0;
			ArrayList<Task> Tasks = new ArrayList<Task>();
			while (slack < 0 && j < S.size()) {
				Task t = S.get(j);
				if (t != s) {

					if (t.res.min() <= maxuse || sLCT <= t.LST()) {
						slack += t.areaMin();
					} else {
						Tasks.add(t);
					}
				}
				j++;
			}

			if (slack < 0 && Tasks.size() != 0) {
				Task[] TaskArr = new Task[Tasks.size()];
				TaskArr = Tasks.toArray(TaskArr);
				Arrays.sort(TaskArr, new TaskDescLSTComparator<Task>());

				j = 0;
				int limitMin = limit.min();
				while (slack < 0 && j < TaskArr.length) {
					Task t = (Task) TaskArr[j];
					j++;
					newCompl = t.LST();
					slack = slack - (compl - newCompl) * limitMin + t.areaMin();
					compl = newCompl;
				}

				newStartl = compl - s.dur.min();
				if (newStartl < s.start.max()) {
					if (debugNarr)
						System.out.println(">>> Cumulative EF <<< 5. Narrowed "
								+ s.start + " in " + MinInt + ".." + newStartl);

					s.start.domain.inMax(store.level, s.start, newStartl);
				}
			}
		}
	}

	void profileCheckInterval(Store store, Variable Start, Variable Duration,
			Interval i, Variable Resources, int mustUseMin, int mustUseMax) {

		for (ProfileItem p : minProfile) {
			if (debug)
				System.out
						.println("Comparing " + i + " with profile item " + p);

			if (intervalOverlap(i.min, i.max + Duration.min(), p.min, p.max)) {
				if (debug)
					System.out.println("Overlapping");
				if (limit.max() - p.value < Resources.min()) {
					// Check for possible narrowing or fail
					if (mustUseMin != -1) {
						ProfileItem use = new ProfileItem(mustUseMin,
								mustUseMax, Resources.min());
						ProfileItem left = new ProfileItem();
						ProfileItem right = new ProfileItem();
						p.subtract(use, left, right);

						if (left.min != -1) {
							int UpdateMin = left.min - Duration.min() + 1, UpdateMax = left.max - 1;
							if (!(UpdateMin > Start.max() || UpdateMax < Start
									.min())) {
								if (debugNarr)
									System.out
											.print(">>> Cumulative Profile 7a. Narrowed "
													+ Start
													+ " \\ "
													+ new IntervalDomain(
															UpdateMin,
															UpdateMax));

								Start.domain.inComplement(store.level, Start,
										UpdateMin, UpdateMax);
								if (debugNarr)
									System.out.println(" => " + Start);
							}
						}

						if (right.min != -1) {
							int UpdateMin = right.min - Duration.min() + 1, UpdateMax = right.max - 1;
							if (!(UpdateMin > Start.max() || UpdateMax < Start
									.min())) {
								if (debugNarr)
									System.out
											.print(">>> Cumulative Profile 7b. Narrowed "
													+ Start
													+ " \\ "
													+ new IntervalDomain(
															UpdateMin,
															UpdateMax));

								Start.domain.inComplement(store.level, Start,
										UpdateMin, UpdateMax);
								if (debugNarr)
									System.out.println(" => " + Start);
							}
						}

						if (Start.max() < right.min
								&& Start.dom().noIntervals() == 1) {
							int rs = right.min - Start.min();
							if (rs < Duration.max()) {
								if (debugNarr)
									System.out
											.println(">>> Cumulative Profile 9. Narrow "
													+ Duration + " in 0.." + rs);
								Duration.domain.inMax(store.level, Duration, rs);
							}
						}
					} else { // (mustUse.min() == -1 )
						int UpdateMin = p.min - Duration.min() + 1, UpdateMax = p.max - 1;
						if (!(UpdateMin > Start.max() || UpdateMax < Start
								.min())) {
							if (debugNarr)
								System.out
										.print(">>> Cumulative Profile 6. Narrowed "
												+ Start
												+ " \\ "
												+ new IntervalDomain(UpdateMin,
														UpdateMax));

							Start.domain.inComplement(store.level, Start,
									UpdateMin, UpdateMax);
							if (debugNarr)
								System.out.println(" => " + Start);
						}
					}
				} else { // ( Overlapping &&
					// limit.max() - p.Value >= Resources.min() )
					if (mustUseMin != -1
							&& !(mustUseMax <= p.min() || mustUseMin >= p.max())) {
						int offset = 0;
						if (intervalOverlap(p.min(), p.max(), mustUseMin,
								mustUseMax))
							offset = Resources.min();
						if (debugNarr)
							System.out
									.println(">>> Cumulative Profile 8. Narrowed "
											+ Resources
											+ " in 0.."
											+ (int) (limit.max() - p.value + offset));

						Resources.domain.in(store.level, Resources, 0, limit
								.max()
								- p.value + offset);
					}
				}
			} else { // ( ( i.min() >= p.max() || i.max()+dur <= p.min()) )
				if (Start.max() < p.min && Start.dom().noIntervals() == 1) {
					// System.out.println("Nonoverlaping "+Start+", "+i+", "+p);
					int ps = p.min - Start.min();
					if (ps < Duration.max()
							&& limit.max() - p.value < Resources.min()) {
						if (debugNarr)
							System.out
									.println(">>> Cumulative Profile 10. Narrowed "
											+ Duration + " in 0.." + ps);
	
						Duration.domain.inMax(store.level, Duration, ps);
					}
				}
			}
		}
	}

	void profileCheckTasks(Store store) {
		IntTask minUse = new IntTask();

		for (Task t : Ts) {
			Variable resUse = t.res;
			Variable dur = t.dur;
			// check only for tasks which can not have duration or resources = 0
			if (dur.min() > 0 && resUse.min() > 0) {
				int A = -1, B = -1;
				if (t.minUse(minUse)) {
					A = minUse.start();
					B = minUse.stop();
				}

				if (debug)
					System.out.println("Start time = " + t.start
							+ ", resource use = " + resUse
							+ ", minimal use = {" + A + ".." + B + "}");

				Domain tStartDom = t.start.dom();

				for (int m = 0; m < tStartDom.noIntervals(); m++)
					profileCheckInterval(store, t.start, dur, tStartDom
							.getInterval(m), resUse, A, B);
			}
		}
	}

	@Override
	public void queueVariable(int level, Variable V) {
	}

	@Override
	public void removeConstraint() {
		for (Task T : Ts) {
			T.start.removeConstraint(this);
			T.dur.removeConstraint(this);
			T.res.removeConstraint(this);
		}
		limit.removeConstraint(this);
	}

	void removeFromS_Est(ArrayList<Task> S) {
		int estS;
		ArrayList<Task> TasksToRemove = new ArrayList<Task>(S.size());

		// S = S \ {s in S|est(s) = est(S)}
		estS = est(S);
		for (Task t : S) {
			if (estS == t.EST())
				TasksToRemove.add(t);
		}
		S.removeAll(TasksToRemove);
	}

	void removeFromS_Lct(ArrayList<Task> S) {
		int lctS;
		ArrayList<Task> TasksToRemove = new ArrayList<Task>(S.size());

		// S = S\{s in S|lct(s) = lct(S)}
		lctS = lct(S);
		for (Task t : S) {
			if (lctS == t.LCT()) {
				TasksToRemove.add(t);
			}
		}
		S.removeAll(TasksToRemove);
	}

	@Override
	public boolean satisfied() {

		Task T;
		boolean sat = true;

		// if profile has been computed make a quick check
		if (minProfile != null && maxProfile != null) {
			if ((minProfile.max() == maxProfile.max()) && limit.singleton()
					&& minProfile.max() == limit.min())
				return true;
			else
				return false;
		} else {
			// expensive checking
			if (limit.singleton()) {
				int i = 0;
				while (sat && i < Ts.length) {
					T = Ts[i];
					i++;
					sat = sat && T.start.singleton() && T.dur.singleton()
							&& T.res.singleton();
				}
				return sat;
			} else
				return false;
		}
	}

	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer( id() );
		
		result.append(" : cumulative([ ");
		for (int i = 0; i < Ts.length - 1; i++)
			result.append(Ts[i]).append(", ");
		
		result.append(Ts[Ts.length - 1]);
		
		result.append(" ]").append(", limit = ").append(limit).append(" )");
		
		return result.toString();
		
	}

	@Override
	public org.jdom.Element toXML() {

		org.jdom.Element constraint = new org.jdom.Element("constraint");
		constraint.setAttribute("name", id() );
		constraint.setAttribute("reference", id_cumulative);

		HashSet<Variable> scopeVars = new HashSet<Variable>();

		scopeVars.add(limit);
		for (int i = 0; i < Ts.length; i++) {
			scopeVars.add(Ts[i].Start());
			scopeVars.add(Ts[i].Dur());
			scopeVars.add(Ts[i].Res());
		}

		constraint.setAttribute("arity", String.valueOf(scopeVars.size()));

		StringBuffer scope = new StringBuffer();
		for (Variable var : scopeVars)
			scope.append(  var.id() ).append( " " );
		
		constraint.setAttribute("scope", scope.substring(0, scope.length() - 1));	
		
		org.jdom.Element limitEl = new org.jdom.Element("limit");
		limitEl.setText(limit.id());
		constraint.addContent(limitEl);

		for (int i = 0; i < Ts.length; i++) {

			org.jdom.Element task = Ts[i].toXML();
			constraint.addContent(task);

		}

		return constraint;

	}

	@Override
	public short type() {
		return type;
	}

	void updateTasksRes(Store store) {
		int limitMax = limit.max();
		for (Task T : Ts)
			T.res.domain.inMax(store.level, T.res, limitMax);
	}

	
	/**
	 * It constructs the constraint from XML description. 
	 * @param constraint an XCSP description of the constraint.
	 * @param store constraint store in which the constraint is being created.
	 * @return constraint constructed from XML element.
	 */
	@SuppressWarnings("unchecked")
	static public Constraint fromXML(Element constraint, Store store) {
		
		ArrayList<Variable> start = new ArrayList<Variable>();
		ArrayList<Variable> duration = new ArrayList<Variable>();
		ArrayList<Variable> resource = new ArrayList<Variable>();

		String limit = constraint.getChild("limit").getText();

		for (org.jdom.Element task : (List<org.jdom.Element>) constraint
				.getChildren("task")) {

			String s = task.getAttributeValue("start");
			String d = task.getAttributeValue("duration");
			String r = task.getAttributeValue("resource");

			start.add(store.findVariable(s));
			duration.add(store.findVariable(d));
			resource.add(store.findVariable(r));
		}

		return new Cumulative(start, duration, resource,
							  store.findVariable(limit));

	}

	@Override
	public void increaseWeight() {
		if (increaseWeight) {
			limit.weight++;
			for (Task t : Ts) { 
				t.dur.weight++;
				t.res.weight++;
				t.start.weight++;
			}
		}
	}
	
	class DomainmaxComparator<T extends Domain> implements Comparator<T> {

		DomainmaxComparator() {
		}

		public int compare(T o1, T o2) {
			return (o2.max() - o1.max());
		}
	}

	class DomainminComparator<T extends Domain> implements Comparator<T> {

		DomainminComparator() {
		}

		public int compare(T o1, T o2) {
			return (o1.min() - o2.min());
		}
	}

	class TaskAscECTComparator<T extends Task> implements Comparator<T> {

		TaskAscECTComparator() {
		}

		public int compare(T o1, T o2) {
			return (o1.Compl().min() - o2.Compl().min());
		}
	}

	class TaskDescLSTComparator<T extends Task> implements Comparator<T> {

		TaskDescLSTComparator() {
		}

		public int compare(T o1, T o2) {
			return (o2.start.max() - o1.start.max());
		}
	}

}
