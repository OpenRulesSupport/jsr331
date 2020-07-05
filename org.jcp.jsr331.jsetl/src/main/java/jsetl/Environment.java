package jsetl;

import jsetl.annotation.NotNull;

/**
 * This class provides a way to link integer codes identifying kinds of atomic constraints with their names.
 * In the following documentation the parameters of the (up to) four parameters of the constraints will be called
 * A,B,C,D.
 */
class Environment {

    ///////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////
    ///////////////////////////////////////////////////////////

    /**
     * Constant containing the maximum number of constraints supported (built-in or user-defined).
     * It is also the size of {@code constraintNames}. Its value is currently {@code 200}.
     * @see Environment#constraintNames
     */
    private static final int MAXIMUM_CONSTRAINTS_NUMBER = 200;

    /**
     * The first index reserved for user-defined constraints. Its value is currently {@code 100}.
     */
    protected static final int FIRST_NEW_CONSTRAINTS_CODE = 100;

    /**
     * First index available for user-defined constraints (automatically updated when new
     * constraints are added).
     */
    private static int newConstraintsCode = FIRST_NEW_CONSTRAINTS_CODE;

    /**
     * Contains the number of built-in constraints.
     */
    private static int numberOfBuiltInConstraints = 0;


    ///////////////////////////////////////////////////////////
    //////////////// (A)CONSTRAINT CODES //////////////////////

    //////////////// PRIMITIVE CONSTRAINTS ////////////////////

    /**
     * Code for equality constraint (A = B);
     */
    protected static final int eqCode = getMethodCode(true);          // Previous fixed code 0

    /**
     * Code for inequality constraint (A != B).
     */
    protected static final int neqCode = getMethodCode(true);         // Previous fixed code 1

    /**
     * Code for membership constraint (A in B).
     */
    protected static final int inCode = getMethodCode(true);          // Previous fixed code 2

    /**
     * Code for not membership constraint (A not in B).
     */
    protected static final int ninCode = getMethodCode(true);         // Previous fixed code 3

    /**
     * Code for a constraint used internally for the resolution
     * of a special case of logical sets equality constraints.
     */
    protected static final int selectCode = getMethodCode(true);      // used only within the eq constraint rewriting rules, previous fixed code 22


    //////////////// SET CONSTRAINTS ///////////////////////////

    /**
     * Code for disjunction constraint (A || B).
     */
    protected static final int disjCode = getMethodCode(true);

    /**
     * Code for union constraint (A union B = C).
     */
    protected static final int unionCode = getMethodCode(true);

    /**
     * Code for subset constraint (A subset or equal B).
     */
    protected static final int subsetCode = getMethodCode(true);

    /**
     * Code for intersection constraint (A intersected with B = C).
     */
    protected static final int intersCode = getMethodCode(true);

    /**
     * Code for difference constraint (A - B = C).
     */
    protected static final int diffCode = getMethodCode(true);

    /**
     * Code for removal of an element constraint (A - {B} = C).
     */
    protected static final int lessCode = getMethodCode(true);

    /**
     * Code for size constraint (|A| = B).
     */
    protected static final int sizeCode = getMethodCode(true);

    /**
     * Code for complement of finite logical sets constraint (A = Universe - B).
     */
    protected static final int complCode = getMethodCode(true);

    /**
     * Code for union is subset constraint ((A union B) subset or equal C).
     */
    protected static final int unionSubsetCode = getMethodCode(true);

    /**
     * Code for subset of union constraint (C subset or equal (A union B)).
     */
    protected static final int subsetUnionCode = getMethodCode(true);


    //////////////// INTEGER LOGICAL VARIABLE CONSTRAINTS ///////

    /**
     * Code for greater than or equal to constraint (A >= B).
     */
    protected static final int geCode = getMethodCode(true);

    /**
     * Code for greater than constraint (A > B).
     */
    protected static final int gtCode = getMethodCode(true);

    /**
     * Code for less than or equal to constraint (A <= B).
     */
    protected static final int leCode = getMethodCode(true);

    /**
     * Code for less than constraint (A < B).
     */
    protected static final int ltCode = getMethodCode(true);

    /**
     * Code for sum constraint (A = B + C).
     */
    protected static final int sumCode = getMethodCode(true);

    /**
     * Code for subtraction constraint (A = B - C).
     */
    protected static final int subCode = getMethodCode(true);

    /**
     * Code for multiplication constraint (A = B * C).
     */
    protected static final int mulCode = getMethodCode(true);

    /**
     * Code for exact division constraint (A = B / C).
     */
    protected static final int divCode = getMethodCode(true);

    /**
     * Code for remainder (module) constraint (A = B mod C).
     */
    protected static final int modCode = getMethodCode(true);

    /**
     * Code for absolute value constraint (A = |B|).
     */
    protected static final int absCode = getMethodCode(true);

    /**
     * Code for domain constraint (dom(A) = B).
     */
    protected static final int domCode = getMethodCode(true);

    /**
     * Code for labeling constraint (label(A)).
     */
    protected static final int labelCode = getMethodCode(true);


    //////////////// NEGATIVE SET CONSTRAINTS //////////////////

    /**
     * Code for not disjoint constraint (A not|| B).
     */
    protected static final int ndisjCode = getMethodCode(true);

    /**
     * Code for not union constraint (A union B != C).
     */
    protected static final int nunionCode = getMethodCode(true);

    /**
     * Code for not subset constraint (A not (subset or equal) B).
     */
    protected static final int nsubsetCode = getMethodCode(true);

    /**
     * Code for not intersection constraint (A intersection B != C).
     */
    protected static final int nintersCode = getMethodCode(true);

    /**
     * Code for not difference constraint (A - B != C).
     */
    protected static final int ndiffCode = getMethodCode(true);


    //////////////// BOOLEAN LOGICAL VARIABLES CONSTRAINTS ///////

    /**
     * Code for boolean not constraint (A = !B).
     */
    protected static final int notBoolCode = getMethodCode(true);

    /**
     * Code for boolean and constraint (A = B && C).
     */
    protected static final int andBoolCode = getMethodCode(true);

    /**
     * Code for boolean or constraint (A = B || C).
     */
    protected static final int orBoolCode = getMethodCode(true);

    /**
     * Code for boolean implication constraint (A = (B implies C)).
     */
    protected static final int impliesBoolCode = getMethodCode(true);

    /**
     * Code for boolean iff (if and only if) (A = (B iff C)).
     */
    protected static final int iffBoolCode = getMethodCode(true);


    //////////////// META CONSTRAINTS ////////////////////////

    /**
     * Code for the always satisfiable constraint (TRUE).
     */
    protected static final int trueCode = getMethodCode(true);

    /**
     * Code for the never satisfiable constraint (FALSE).
     */
    protected static final int falseCode = getMethodCode(true);

    /**
     * Code for non-deterministic or constraint (A OR B).
     * Non-deterministically adds A (and then, backtracking, B) to the constraint store.
     */
    protected static final int orCode = getMethodCode(true);

    /**
     * Code for deterministic or test (A is satisfiable or B is satisfiable).
     * The constraint is handled only when both its arguments are ground.
     */
    protected static final int orTestCode = getMethodCode(true);

    /**
     * Code for deterministic not test (A is not satisfiable).
     * The constraint is handled only when its argument is ground.
     */
    protected static final int notTestCode = getMethodCode(true);

    /**
     * Code for deterministic implication test (A implies B).
     * The constraint is handled only when both its arguments are ground.
     */
    protected static final int impliesTestCode = getMethodCode(true);


    //////////////// PARTIAL FUNCTION CONSTRAINTS ////////////////////

    /**
     * Code for range constraint on partial functions (range(A) = B).
     */
    protected static final int pfRanCode = getMethodCode(true);

    /**
     * Code for domain constraint on partial functions (domain(A) = B).
     */
    protected static final int pfDomCode = getMethodCode(true);

    /**
     * Code for partial function constraint (A is a partial function).
     */
    protected static final int pfunCode = getMethodCode(true);

    /**
     * Code for composition constraint on partial functions (A comp B = C).
     */
    protected static final int pfCompCode = getMethodCode(true);


    //////////////// RELATIONAL CONSTRAINTS //////////////////

    /**
     * Code for identity constraint (A = identity(B)).
     */
    protected static final int idCode = getMethodCode(true);

    /**
     * Code for inverse constraint (A = inverse(B)).
     */
    protected static final int invCode = getMethodCode(true);

    /**
     * Code for range constraint on binary relations (range(A) = B).
     */
    protected static final int brRanCode = getMethodCode(true);

    /**
     * Code for domain constraint on binary relations (domain(A) = B).
     */
    protected static final int brDomCode = getMethodCode(true);

    /**
     * Code for composition of binary relations constraint(A comp B = C).
     */
    protected static final int brCompCode = getMethodCode(true);

    /**
     * Code for composition subset of binary relations constraint ((A comp B) subset or equal to C).
     */
    protected static final int compSubsetCode = getMethodCode(true);

    /**
     * Code for subset of composition of binary relations constraint (C subset or equal to (A comp B)).
     */
    protected static final int subsetCompCode = getMethodCode(true);


    ///////////////////////////////////////////////////////////
    //////////////// OTHER STATIC MEMBERS /////////////////////

    /**
     * Array containing the constraint names corresponding to each constraint code.
     */
    private static String[] constraintNames = getConstraintNames();


    ///////////////////////////////////////////////////////////
    //////////////// PROTECTED STATIC METHODS /////////////////
    ///////////////////////////////////////////////////////////

    /**
     * Convert the integer constraint identifier to the associated String.
     * @param constraintCode an integer constraint identifier. It must be a valid constraint code.
     * @return The string corresponding to the integer constraint identifier.
     */
    protected static @NotNull String code_to_name(int constraintCode) {
        assert constraintCode >= 0;
        assert (constraintCode < numberOfBuiltInConstraints) || (constraintCode >= FIRST_NEW_CONSTRAINTS_CODE && constraintCode < newConstraintsCode);

        String name = constraintNames[constraintCode];
        assert name != null;
        return name;
    }

    /**
     * Convert the string constraint identifier to the associated (unique) integer.
     * @param name the constraint name.
     * @return the integer constraint identifier associated to the name passed.
     */
    protected static int name_to_code(@NotNull String name){
        assert name != null;

        return findName(name);
    }

    /**
     * Searches the constraint code corresponding to the parameter {@code name}.
     * If it is not found then a new name is added bound to the first available constraint code.
     * @param name constraint name.
     * @return the constraint code corresponding to the given constraint {@code name}.
     */
    protected static int name_to_code_add_if_not_found(@NotNull String name){
        assert name != null;

        int code = Environment.name_to_code(name);
        if (code == -1)
            return Environment.addName(name);
        else
            return code;
    }


    ///////////////////////////////////////////////////////////
    //////////////// PRIVATE STATIC METHODS /////////////////
    ///////////////////////////////////////////////////////////

    /**
     * Insert a new constraint name.
     * @param name The new constraint string identifier.
     * @return The integer identifier for the new constraint.
     */
    private static int addName(@NotNull String name) {
        assert name != null;

        int code = getMethodCode(false);
        constraintNames[code] = name;
        return code;
    }
             
    /**
     * Gets the corresponding integer constraint identifier for the given string.
     * @param name the name of the constraint to find.
     * @return the integer code for the kind of atomic constraint.
     */
    private static int findName(@NotNull String name){
        assert name != null;

        for(int i = 0; i < numberOfBuiltInConstraints; i++)
            if (name.equals(constraintNames[i])) return (i);
        for(int i = FIRST_NEW_CONSTRAINTS_CODE; i < newConstraintsCode; i++)
            if (name.equals(constraintNames[i])) return (i);
        return -1;
    }

    /**
     * Returns a new integer to identify an atomic constraints, different from each other generated previously
     *
     * @param isBuiltIn a boolean whose truth value tells if the atomic constraint for which the code is being generated is built in or not.
     * @return a unique integer used to identify a kind of atomic constraint.
     * @throws IllegalStateException if there is no more space for the type of constraint that is being added (built-in or not).
     */
    private static int getMethodCode(boolean isBuiltIn) {
        if (isBuiltIn) {
            if(numberOfBuiltInConstraints < FIRST_NEW_CONSTRAINTS_CODE)
                return numberOfBuiltInConstraints++;
            else
                throw new IllegalStateException("CAN'T HAVE MORE THAN " + FIRST_NEW_CONSTRAINTS_CODE + " BUILT-IN CONSTRAINTS");
        }
        else {
            if(newConstraintsCode < MAXIMUM_CONSTRAINTS_NUMBER)
                return newConstraintsCode++;
            else
                throw new IllegalStateException("CAN'T HAVE MORE THAN " + (MAXIMUM_CONSTRAINTS_NUMBER - FIRST_NEW_CONSTRAINTS_CODE) + " USER-DEFINED CONSTRAINTS");
        }
    }

    /**
     * Creates an array of strings of size {@code MAXIMUM_CONSTRAINTS_NUMBER} and assigns
     * the proper name to each built-in constraint code.
     * @return the created array of names.
     * @see Environment#MAXIMUM_CONSTRAINTS_NUMBER
     */
    private static @NotNull String[] getConstraintNames(){
        constraintNames = new String[MAXIMUM_CONSTRAINTS_NUMBER];
        constraintNames[eqCode]         = "_eq";
        constraintNames[neqCode]        = "_neq";
        constraintNames[inCode]         = "_in";
        constraintNames[ninCode]        = "_nin";
        constraintNames[selectCode]     = "_select";
        constraintNames[orCode]         = "_or";
        constraintNames[orTestCode]     = "_orTest";
        constraintNames[notTestCode]    = "_notTest";
        constraintNames[impliesTestCode]= "_impliesTest";

        constraintNames[unionCode]      = "_union";
        constraintNames[disjCode]       = "_disj";
        constraintNames[subsetCode]     = "_subset";
        constraintNames[intersCode]     = "_inters";
        constraintNames[diffCode]       = "_diff";
        constraintNames[lessCode]       = "_less";
        constraintNames[sizeCode]       = "_size";
        constraintNames[complCode]      = "_compl";
        constraintNames[unionSubsetCode] = "_unionSubset";
        constraintNames[subsetUnionCode] = "_subsetUnion";

        constraintNames[nunionCode]     = "_nunion";
        constraintNames[ndisjCode]      = "_ndisj";
        constraintNames[nsubsetCode]    = "_nsubset";
        constraintNames[nintersCode]    = "_ninters";
        constraintNames[ndiffCode]      = "_ndiff";

        constraintNames[geCode]         = "_ge";
        constraintNames[gtCode]         = "_gt";
        constraintNames[leCode]         = "_le";
        constraintNames[ltCode]         = "_lt";
        constraintNames[sumCode]        = "_sum";
        constraintNames[subCode]        = "_sub";
        constraintNames[mulCode]        = "_mul";
        constraintNames[divCode]        = "_div";
        constraintNames[modCode]        = "_mod";

        constraintNames[domCode]        = "_dom";
        constraintNames[labelCode]      = "_label";

        constraintNames[absCode]        = "_abs";
        constraintNames[andBoolCode]    = "_andBool";
        constraintNames[orBoolCode]     = "_orBool";
        constraintNames[impliesBoolCode]= "_impliesBool";
        constraintNames[iffBoolCode]    = "_iffBool";
        constraintNames[notBoolCode]    = "_notBool";

        constraintNames[pfRanCode]        = "_pfran";
        constraintNames[pfDomCode]     = "_pfdom";
        constraintNames[pfunCode]       = "_pfun";
        constraintNames[pfCompCode]       = "_pfcomp";
        constraintNames[idCode]         = "_id";
        constraintNames[invCode]        = "_inv";

        constraintNames[brRanCode]        = "_ran";
        constraintNames[brDomCode]     = "_brdom";
        constraintNames[brCompCode]       = "_comp";
        constraintNames[compSubsetCode] = "_compSubset";
        constraintNames[subsetCompCode] = "_subsetComp";

        constraintNames[trueCode]        = "_true";
        constraintNames[falseCode]        = "_false";

        assert constraintNames != null;
        return constraintNames;
    }
}
