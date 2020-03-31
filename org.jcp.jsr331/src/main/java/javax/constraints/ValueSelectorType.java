//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

public enum ValueSelectorType {
	/**
	 * try values in increasing order one at a time 
	 * without removing failed values on backtracking
	 */
	IN_DOMAIN,
	/**
	 * try values in increasing order, remove value on backtracking
	 */
	MIN,
	/**
	 * try values in increasing order, remove value on backtracking
	 */
	MAX,
	/**
	 * try to alternate minimal and maximal values
	 */
	MIN_MAX_ALTERNATE,
	/**
	 * try values in the middle of domain,
	 * the closest to (min+max)/2
	 */
	MIDDLE,
	/**
	 * try the median values first,
	 * e.g if domain has 5 values, try the third value first
	 */
	MEDIAN,
	/**
	 * try a random value
	 */
	RANDOM,
	/**
	 * try a value which causes the smallest domain reduction in all other variables
	 */
	MIN_IMPACT,
	/**
	 * custom selector
	 */
	CUSTOM
}