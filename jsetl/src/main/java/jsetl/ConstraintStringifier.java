package jsetl;

import jsetl.annotation.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * This class provides static methods to compute the string representation of
 * constraint conjunctions and atomic constraints. It is also used to retrieve
 * the string representation of the internal view of those constraints.
 * To compute such string representations the patterns contained
 * in {@code jsetl.configuration.constraintPatterns} are used.
 * @author Andrea Fois
 */
class ConstraintStringifier {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC MEMBERS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constant string representing the token used in the constraint patterns file
     * to refer to the name of the constraint.
     */
    public static final @NotNull String nameReference = "?name";

    /**
     * Length of the token used to reference the name of the constraint.
     */
    public static final int nameReferenceLength = nameReference.length();

    /**
     * Regular expression matching the tokens used in the constraint patterns file
     * to refer to the arguments of the constraint.
     */
    public static final @NotNull String argumentReference= "%*\\?[1234].*"; //?1, ?2, ?3, ?4

    /**
     * Length of the tokens used to refer to the arguments of the constraint.
     */
    public static final int argumentReferenceLength = 2;

    /**
     * Constant string representing the token used in the constraint patterns file to refer
     * to the current alternative of the constraint.
     */
    public static final @NotNull String alternativeReference = "?alternative";

    /**
     * Length of the token used to refer to the current alternative of the constraint.
     */
    public static final int alternativeReferenceLength = alternativeReference.length();

    /**
     * Constant string representing the token used in the constraint patterns file to refer to
     * the {@code solved} state of the constraint.
     */
    public static final @NotNull String solvedReference = "?solved";

    /**
     * Length of the token used to refer to the {@code solved} field of the constraint.
     */
    public static final int solvedReferenceLength = solvedReference.length();

    /**
     * Resource bundle used to retrieve the patterns for the printing of constraints. The patterns are located in
     * {@code jsetl.configuration.constraintPatterns}.
     */
    public static final ResourceBundle constraintPatterns = ResourceBundle.getBundle("jsetl.configuration.constraintPatterns");


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC STATIC METHODS ////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the string representation of the constraint conjunction {@code constraint}.
     * @param constraint constraint conjunction.
     * @return the string representation of {@code constraint}.
     */
    public static @NotNull String stringify(@NotNull Constraint constraint){
        Objects.requireNonNull(constraint);

        String stringified = stringify(constraint, ConstraintStringifier::stringify);

        assert stringified != null;
        return stringified;
    }

    /**
     * Returns a string representing the internal view of the constraint conjunction {@code constraint}.
     * @param constraint constraint conjunction.
     * @return a string representing the internal view of {@code constraint}.
     */
    public static @NotNull String internalStringify(@NotNull Constraint constraint){
        Objects.requireNonNull(constraint);

        String internalStringified = stringify(constraint, ConstraintStringifier::internalStringify);

        assert internalStringified != null;
        return internalStringified;
    }

    /**
     * Returns the string representation of the atomic constraint {@code aConstraint}.
     * @param aConstraint an atomic constraint.
     * @return the string representation of {@code aConstraint}.
     */
    public static @NotNull String stringify(@NotNull AConstraint aConstraint){
        Objects.requireNonNull(aConstraint);

        String pattern = getPattern(aConstraint);
        String result = applyPattern(pattern, aConstraint);

        assert result != null;
        return result;
    }

    /**
     * Returns a string representing the internal view of the atomic constraint {@code aConstraint}.
     * @param aConstraint an atomic constraint.
     * @return a string representing the internal view of the atomic constraint {@code aConstraint}.
     */
    public static @NotNull String internalStringify(@NotNull AConstraint aConstraint){
        Objects.requireNonNull(aConstraint);

        String internalViewPattern = constraintPatterns.getString("internalView"); //number of arguments doesn't matter.
        String replaced = applyPattern(internalViewPattern, aConstraint);

        assert replaced != null;
        return replaced;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE STATIC METHODS ///////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns a string representation for the constraint conjunction {@code constraint}
     * using the provided {@code aConstraintStringifier} to compute the string representation
     * for each atomic constraint in the conjunction.
     * @param constraint constraint conjunction to stringify.
     * @param aConstraintStringifier the stringifier to use to compute the string representation
     *                               of atomic constraints.
     * @return the computed string representation for {@code constraint}.
     */
    private static @NotNull String stringify(@NotNull Constraint constraint, @NotNull AConstraintStringifier aConstraintStringifier ){
        assert constraint != null;
        assert aConstraintStringifier != null;

        int n = constraint.size();
        if (n == 0)
            return "[]";
        else {
            StringBuilder builder = new StringBuilder();
            Iterator<AConstraint> iterator = constraint.iterator();
            builder.append(aConstraintStringifier.stringify(iterator.next()));
            iterator.forEachRemaining(aConstraint -> {
                builder.append(" AND ");
                builder.append(aConstraintStringifier.stringify(aConstraint));
            });

            String out = builder.toString();
            assert out != null;
            return out;
        }
    }

    /**
     * Returns the standard pattern that should be used to compute the standard string representation
     * of the atomic constraint {@code aConstraint}.
     * @param aConstraint an atomic constraint.
     * @return the retrieved pattern.
     */
    private static  @NotNull String getPattern(@NotNull AConstraint aConstraint){
        assert aConstraint != null;

        String name = Environment.code_to_name(aConstraint.constraintKindCode);
        int numberOfArguments = (int) Stream.of(
                aConstraint.argument1,
                aConstraint.argument2,
                aConstraint.argument3,
                aConstraint.argument4
        ).filter(Objects::nonNull).count();

        if(numberOfArguments < 0 || numberOfArguments > AConstraint.MAX_ACONSTRAINT_ARGUMENTS)
            throw new IllegalArgumentException("WRONG NUMBER OF ARGUMENTS: " + numberOfArguments);

        String pattern;
        if(constraintPatterns.containsKey(name))
            pattern = constraintPatterns.getString(name);
        else
            pattern = constraintPatterns.getString("defaultPattern" + numberOfArguments);

        assert pattern != null;
        return pattern;
    }

    /**
     * Applies the pattern {@code pattern} to the atomic constraint {@code aConstraint}, replacing each reference
     * to the corresponding string retrieved from {@code aConstraint}.
     * @param pattern pattern to apply.
     * @param aConstraint atomic constraint used to apply the pattern.
     * @return the result of the application of {@code pattern} to {@code aConstraint}.
     */
    private static @NotNull String applyPattern(@NotNull String pattern, @NotNull AConstraint aConstraint){
        assert pattern != null;
        assert aConstraint != null;

        String name = Environment.code_to_name(aConstraint.constraintKindCode);
        if(name.startsWith("_"))
            name = name.substring(1);

        StringBuilder builder = new StringBuilder();
        int i = 0;
        while(i < pattern.length()){
            if(refersToName(pattern, i)){
                builder.append(name);
                i += nameReferenceLength;
            }
            else if(refersToArgument(pattern, i)){
                builder.append(getReferencedArgument(pattern, i, aConstraint));
                i += argumentReferenceLength;
            }
            else if(refersToAlternative(pattern, i)){
                builder.append(aConstraint.alternative);
                i += alternativeReferenceLength;
            }
            else if(refersToSolved(pattern, i)){
                builder.append(aConstraint.getSolved());
                i += solvedReferenceLength;
            }
            else {
                builder.append(pattern.charAt(i));
                ++i;
            }
        }

        return builder.toString();
    }

    /**
     * Checks whether the parameter {@code string} contains a reference to the name of the constraint at the given {@code offset}.
     * @param string string to check.
     * @param offset offset to check from.
     * @return {@code true} if a reference to the name of the constraint is found at offset {@code offset} in {@code string},
     * {@code false} otherwise.
     */
    private static boolean refersToName(@NotNull String string, int offset){
        return string.startsWith(nameReference, offset);
    }

    /**
     * Checks whether the parameter {@code string} contains a reference to an argument at the given {@code offset}.
     * @param string string to check.
     * @param offset offset to check from.
     * @return {@code true} if a reference to an argument is found at offset {@code offset} in {@code string},
     * {@code false} otherwise.
     */
    private static boolean refersToArgument(@NotNull String string, int offset){
        return string.substring(offset).matches(argumentReference);
    }

    /**
     * Returns the standard string representation of the i-th argument of {@code aConstraint}, where i is the number
     * of the argument referenced in {@code string} at the given {@code offset} (if the argument is {@code null} returns "null").
     * The numeration starts from 1.
     * <i>UNCHECKED PRECONDITION of this method is that {@code string} do actually contain a reference to an argument
     * at the position {@code offset}.
     * Use the method {@code refersToArgument} to check for this precondition.</i>
     * @param string the string used to retrieved the referenced argument index.
     * @param offset the offset of the reference of the argument.
     * @param aConstraint atomic constraint that contains the referenced argument.
     * @return the string representation of the retrieved argument.
     * @see ConstraintStringifier#refersToArgument(String, int)
     */
    private static @NotNull
    String getReferencedArgument(@NotNull String string, int offset, @NotNull AConstraint aConstraint){
        int argumentIndex = Character.getNumericValue(string.charAt(offset + 1));
        Object argument = aConstraint.getArg(argumentIndex);
        return String.valueOf(argument);
    }

    /**
     * Checks whether the parameter {@code string} contains a reference to the "alternative" value at the given {@code offset}.
     * @param string string to check.
     * @param offset offset to check from.
     * @return {@code true} if a reference to the "alternative" value is found at offset {@code offset} in {@code string},
     * {@code false} otherwise.
     */
    private static boolean refersToAlternative(@NotNull String string, int offset){
        assert string != null;
        return string.substring(offset).startsWith(alternativeReference);
    }

    /**
     * Checks whether the parameter {@code string} contains a reference to the "solved" value at the given {@code offset}.
     * @param string string to check.
     * @param offset offset to check from.
     * @return {@code true} if a reference to the "solved" value is found at offset {@code offset} in {@code string},
     * {@code false} otherwise.
     */
    private static boolean refersToSolved(@NotNull String string, int offset){
        assert string != null;
        return string.substring(offset).startsWith(solvedReference);
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC STATIC INTERFACES /////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Functional interface used to refer to the of stringifier method to use.
     * The only method of this interface returns a string representation for an
     * atomic constraint.
     */
    @FunctionalInterface
    private interface AConstraintStringifier{

        /**
         * Returns the string representation of the given atomic constraint.
         * @param aConstraint atomic constraint.
         * @return the string version of {@code aConstraint}.
         */
        @NotNull String stringify(@NotNull AConstraint aConstraint);

    }

}
