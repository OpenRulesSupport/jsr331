package jsetl.exception;

/**
 * Exceptions of this type are raised inside {@code Solver}, when a constraint is found to be unsatisfiable,
 * to stop the solution of atomic constraints and start the backtracking procedure.
 */
public class Fail extends RuntimeException {
    //EMPTY
}
