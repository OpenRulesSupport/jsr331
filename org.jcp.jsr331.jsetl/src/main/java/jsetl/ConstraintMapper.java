package jsetl;

import jsetl.annotation.NotNull;
import java.util.Iterator;
import java.util.Objects;

/**
 * This class is used to map constraints into other constraints based on substitutions
 * of their part that are those deducible from {@code original} and {@code mapped}.
 * This class makes the assumption that {@code original} and {@code mapped} have the same
 * structure if they are logical objects. <br>
 * Moreover, this class assumes that if the same object appears more than once in {@code original}
 * than all of its substitutions in {@code mapped} are the same. <br>
 * Objects that are not logical are treated as atoms and are not substituted.<br>
 * It is also assumed that only logical objects that are completely variables do actually differ from
 * {@code original} to {@code mapped}.<br>
 * None of the assumptions above are checked. <br>
 * Note that currently {@code CP} and {@code Ris} are treated as atoms and are substituted only in their entirety
 * and not deeply.
 * <br><br>
 * For example, if {@code original} is {@code [X, [Y, 1]]} and
 * {@code mapped} is {@code [[1, 2, Y, Z], [3, 1]]},
 * then the mapping of {@code [X, [Y], 3] in A} is {@code [[1, 2, Y, Z], [3], 3] in A}.
 *
 * @author Andrea Fois
 */
class ConstraintMapper {

    //////////////////////////////////////////////////////////
    ///////////////// PRIVATE MEMBERS ////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Original object, used to deduce the domain of the mapping function.
     * Its structure is used, in combination with that of {@code mapped} to deduce
     * mapping function.
     */
    private final Object original;

    /**
     * Mapped object, used to deduce the range of the mapping function.
     * Its structure is used, in combination with that of {@code original} to deduce
     * mapping function.
     */
    private final Object mapped;

    /**
     * Logical objects mapper used to map arguments of constraints.
     */
    private final LObjectMapper lObjectMapper;


    //////////////////////////////////////////////////////////
    ///////////////// PROTECTED CONSTRUCTORS /////////////////
    //////////////////////////////////////////////////////////

    /**
     * Creates a constraint mapper whose mapping function is deduced by the mapping of
     * {@code original} into {@code mapped}.
     *
     * @param original original object, before the application of the mapping function.
     * This object is used, in conjunction with  {@code mapped}, to deduce the mapping function.
     * @param mapped the result of the application of the mapping function on {@code original}.
     * This object is used, in conjunction with  {@code original}, to deduce the mapping function.
     */
    protected ConstraintMapper(@NotNull final Object original, @NotNull final Object mapped){
        Objects.requireNonNull(original);
        Objects.requireNonNull(mapped);

        this.original = original;
        this.mapped = mapped;
        this.lObjectMapper = new LObjectMapper(original, mapped);
    }


    //////////////////////////////////////////////////////////
    ///////////////// PROTECTED METHODS //////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Creates and returns a new constraint which is the result of the mapping function applied to
     * the parameter {@code constraint}. The mapping function used is that deduced by the parameters
     * of the constructor.
     *
     * @param constraint constraint to map.
     * @return the mapping of {@code constraint}.
     */
    @NotNull
    protected ConstraintClass mapConstraintDeeply(@NotNull final ConstraintClass constraint){
        assert constraint != null;

        final ConstraintClass mappedConstraint = mapDeeply(constraint, original, mapped);

        assert mappedConstraint != null;
        return mappedConstraint;
    }


    //////////////////////////////////////////////////////////
    ///////////////// PRIVATE METHODS ////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Creates and returns a new constraint which is the result of the mapping function applied to
     * the parameter {@code constraint}. The mapping function used is that deduced by the mapping of
     * parameter {@code original} into {@code mapped}.
     *
     * @param constraint constraint to map.
     * @param original original object, before the application of the mapping function.
     * @param mapped mapped object, the result of the application of the mapping function on {@code original}.
     * @return the mapping of {@code constraint}.
     */
    @NotNull
    private ConstraintClass mapDeeply(@NotNull final ConstraintClass constraint, @NotNull Object original, @NotNull Object mapped){
        assert constraint != null;
        assert original != null;
        assert mapped != null;

        ConstraintClass replaced = new ConstraintClass();

        Iterator<AConstraint> aConstraints = constraint.iterator();
        while(aConstraints.hasNext())
            replaced.add(mapDeeply(aConstraints.next(), original, mapped));

        assert replaced != null;
        return replaced;
    }

    /**
     * Creates and returns a new constraint which is the result of the mapping function applied to
     * the parameter {@code aConstraint}. The mapping function used is those deduced by the mapping of
     * parameter {@code original} into {@code mapped}.
     *
     * @param aConstraint atomic constraint to map.
     * @param original original object, before the application of the mapping function.
     * @param mapped mapped object, the result of the application of the mapping function on {@code original}.
     * @return the mapping of {@code constraint}.
     */
    @NotNull
    private ConstraintClass mapDeeply(@NotNull AConstraint aConstraint, @NotNull Object original, @NotNull Object mapped){
        assert aConstraint != null;
        assert original != null;
        assert mapped != null;

        if(aConstraint.constraintKindCode == Environment.orCode){
            ConstraintClass orAConstraintMapped = mapOrAConstraintDeeply(aConstraint, original, mapped);

            assert orAConstraintMapped != null;
            return orAConstraintMapped;
        }

        Object arg1 = lObjectMapper.mapLObjectDeeply(aConstraint.argument1);
        Object arg2 = lObjectMapper.mapLObjectDeeply(aConstraint.argument2);
        Object arg3 = lObjectMapper.mapLObjectDeeply(aConstraint.argument3);
        Object arg4 = lObjectMapper.mapLObjectDeeply(aConstraint.argument4);

        String aConstraintName = aConstraint.getName();
        assert aConstraintName != null;
        AConstraint mappedAConstraint = new AConstraint(aConstraintName, arg1, arg2, arg3, arg4);
        if(aConstraint.getDefinitionConstraint())
            mappedAConstraint.setDefinitionConstraint(true);
        return new ConstraintClass(mappedAConstraint);
    }

    /**
     * Creates and returns a new constraint which is the result of the mapping function applied to
     * the parameter {@code aConstraint}, which is supposed to be an atomic constraint of kind  <i>or</i>.
     * The mapping function used is those deduced by the mapping of parameter {@code original} into {@code mapped}.
     *
     * @param aConstraint <i>or</i> atomic constraint to map.
     * @param original original object, before the application of the mapping function.
     * @param mapped mapped object, the result of the application of the mapping function on {@code original}.
     * @return the mapping of {@code constraint}.
     */
    @NotNull
    private ConstraintClass mapOrAConstraintDeeply(@NotNull AConstraint aConstraint, @NotNull Object original, @NotNull Object mapped) {
        assert aConstraint != null;
        assert original != null;
        assert mapped != null;

        assert aConstraint.constraintKindCode == Environment.orCode;
        assert aConstraint.argument1 instanceof ConstraintClass || aConstraint.argument1 instanceof AConstraint;
        assert aConstraint.argument2 instanceof ConstraintClass || aConstraint.argument2 instanceof AConstraint;
        assert aConstraint.argument3 == null;
        assert aConstraint.argument4 == null;


        ConstraintClass firstCase = new ConstraintClass((ConstraintClass) aConstraint.argument1);
        ConstraintClass secondCase = new ConstraintClass((ConstraintClass) aConstraint.argument2);

        ConstraintClass firstReplaced = mapDeeply(firstCase, original, mapped);
        ConstraintClass secondReplaced = mapDeeply(secondCase, original, mapped);

        ConstraintClass mappedConstraint = firstReplaced.or(secondReplaced);

        assert mappedConstraint != null;
        return mappedConstraint;
    }
}