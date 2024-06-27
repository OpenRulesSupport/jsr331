/**
 *  Backtrackable.java 
 *  This file is part of JaCoP.
 *
 *  JaCoP is a Java Constraint Programming solver. 
 *	
 *  Copyright (C) 2000-2008 Krzysztof Kuchcinski and Radoslaw Szymanek
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

package JaCoP.core;

/**
 * @author Radoslaw Szymanek and Krzysztof Kuchcinski
 *
 * It specifies the interface of the backtrackable object which is being
 * recorded by Backtrackable Manager. 
 * 
 */
public interface Backtrackable {

	/**
	 * It specifies the function being called by the manager upon backtracking. 
	 * The manager may call this function for the objects which have not changed
	 * but it must call this function for all objects that have changed.
	 * @param removedLevel
	 */
	public void remove(int removedLevel);
	
	/**
	 * It returns the level of the object (its last copy of the state). 
	 * @return the object level
	 */
	public int level();
	
	/**
	 * It specifies the index/position of the backtrackable object. It 
	 * is equal to the object unique id.
	 * 
	 * @return index of the object. 
	 */
	public int index();
}
