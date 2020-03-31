//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints;

public enum VarSelectorType {
	/**
	 * selection of variables in order of definition
	 */
	INPUT_ORDER,
	/**
	 * smallest lower bound
	 */
	MIN_VALUE,
	/**
	 * largest upper bound
	 */
	MAX_VALUE,
	/**
	 * min size of domain, tie break undefined
	 */
	MIN_DOMAIN, 
	/**
	 * min size of domain, random tie break
	 */
	MIN_DOMAIN_MIN_VALUE,
	/**
	 * min size of domain, random tie break
	 */
	MIN_DOMAIN_RANDOM,
	/**
	 * max size of domain, random tie break
	 */
	MAX_DOMAIN_RANDOM,
	/**
	 * random selection of variables
	 */
	RANDOM,
	/**
	 * min size of domain as first criteria, tie break by degree
	 * that is the number of attached constraints 
	 */
	MIN_DOMAIN_MAX_DEGREE,
	/**
	 * min value of fraction of domain size and degree
	 */
	MIN_DOMAIN_OVER_DEGREE,
	/**
	 * min value of domain size over weighted degree
	 */
	MIN_DOMAIN_OVER_WEIGHTED_DEGREE,
	/**
	 * largest number of recorded failures in attached constraints
	 */
	MAX_WEIGHTED_DEGREE,
	/**
	 * largest impact, select variable which when assigned restricts 
	 * the domains of all other variables by the largest amount
	 */
	MAX_IMPACT,
	/**
	 * largest number of attached constraints
	 */
	MAX_DEGREE,
	/**
	 * largest difference between smallest and second smallest value in domain
	 */
	MAX_REGRET,
	/**
	 * custom variable selector
	 */
	CUSTOM
}