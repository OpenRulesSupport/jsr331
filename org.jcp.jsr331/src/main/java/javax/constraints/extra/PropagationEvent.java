//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//=============================================
package javax.constraints.extra;

public enum PropagationEvent {
	VALUE, 	// occurs when the domain size becomes 1 (variable is instantiated)
	MIN,	// occurs when the domain's minimum value is altered
	MAX,	// occurs when the domain's maximum value is altered
	REMOVE,	// occurs when value is removed from the domain
	RANGE,	// occurs when the domain's minimum or maximum value is altered	
	ANY		// occurs when any type of above events occurs for the domain
}