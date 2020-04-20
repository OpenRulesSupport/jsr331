package jsetl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsetl.annotation.NotNull;
import static jsetl.Environment.*;
import static jsetl.Environment.trueCode;

/**
 * This class is used to take a constraint conjunction and create a new constraint conjunction
 * which is the negation of the original constraint (equality constraints become inequality constraints,
 * conjunctions become disjunctions, ...). <br>
 * Currently the following constraints are supported:
 *
 *
 *     <ul>
 *      <li> {@code  empty constraint -> empty constraint (copy instead of negation)}</li>
 *      <li> {@code  true -> false}</li>
 *      <li> {@code  false -> true}</li>
 *      <li> {@code  and -> or}</li>
 *      <li> {@code  or -> and}</li>
 *      <li> {@code  label -> label (copy instead of negation) }</li>
 *      <li> {@code  equality -> inequality}</li>
 *      <li> {@code  inequality -> equality}</li>
 *      <li> {@code  in -> not in}</li>
 *      <li> {@code  not in -> in}</li>
 *      <li> {@code  less than -> greater than or equal to}</li>
 *      <li> {@code  less than or equal to -> greater than }</li>
 *      <li> {@code  greater than -> less than or equal to}</li>
 *      <li> {@code  greater than or equal to -> less than }</li>
 *     </ul>
 *
 *
 * When a constraint outside the list above is found an {@code IllegalArgumentException} is thrown.
 * Moreover, constraints that are definitions of temporary variables needed for the flattening of expressions
 * must not be negated (and in fact they are just cloned, regardless of their kind).
 *
 * @author Andrea Fois
 */
class ConstraintNegator{

    ////////////////////////////////////////////////////////////
    ////////////// PROTECTED METHODS ///////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code constraint}.
     * @param constraint constraint to be negated.
     * @return the negation of {@code constraint}.
     * @throws IllegalArgumentException if {@code constraint} contains
     * constraints of unsupported kinds.
     */
    protected @NotNull
    ConstraintClass negate(@NotNull ConstraintClass constraint){
        assert constraint != null;

        if(constraint.isEmpty())
            return new ConstraintClass();

        if(constraint.size() == 1)
            if(!constraint.get(0).getDefinitionConstraint())
                return negate(constraint.get(0));
            else
                return new ConstraintClass(constraint.get(0).clone());

        ConstraintClass result = negateAnd(constraint);
        assert result != null;
        return result;
    }


    ////////////////////////////////////////////////////////////
    ////////////// PRIVATE METHODS /////////////////////////////
    ////////////////////////////////////////////////////////////

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code aConstraint}.
     * @param aConstraint atomic constraint to be negated.
     * @return the negation of {@code aConstraint}.
     * @throws IllegalArgumentException if {@code aConstraint} has an unsupported constraint kind.
     */
    private @NotNull
    ConstraintClass negate(@NotNull AConstraint aConstraint){
        assert aConstraint != null;

        if(aConstraint.constraintKindCode == trueCode)
            return negateTrue(aConstraint);
        if(aConstraint.constraintKindCode == falseCode)
            return negateFalse(aConstraint);


        if (aConstraint.getDefinitionConstraint() || aConstraint.constraintKindCode == labelCode)
            return new ConstraintClass(aConstraint.clone());


        int cons = aConstraint.constraintKindCode;
        ConstraintClass result;
        if(cons == eqCode)
            result = negateEq(aConstraint);
        else if(cons == neqCode)
            result = negateNeq(aConstraint);
        else if(cons == inCode)
            result = negateIn(aConstraint);
        else if(cons == ninCode)
            result = negateNin(aConstraint);
        else if(cons == subsetCode)
            result = negateSubset(aConstraint);
        else if(cons == orCode)
            result = negateOr(aConstraint);
        else if(cons == ltCode)
            result = negateLt(aConstraint);
        else if(cons == leCode)
            result = negateLe(aConstraint);
        else if(cons == gtCode)
            result = negateGt(aConstraint);
        else if(cons == geCode)
            result =  negateGe(aConstraint);
        else if(cons == sizeCode)
            result = negateSize(aConstraint);
        else
            throw new IllegalArgumentException("CONSTRAINT TYPE NOT SUPPORTED YET: " + aConstraint.getName());

        assert result != null;
        return result;
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code or} constraint.
     * @param or atomic constraint to be negated.
     * @return the negation of {@code or}.
     * @throws IllegalArgumentException if the "or" constraints contains
     * constraints of unsupported kinds.
     */
    private @NotNull
    ConstraintClass negateOr(@NotNull AConstraint or){
        assert or != null;
        assert or.constraintKindCode == orCode;
        assert or.argument1 != null;
        assert or.argument1 instanceof AConstraint || or.argument2 instanceof ConstraintClass;

        assert or.argument2 != null;
        assert or.argument2 instanceof ConstraintClass || or.argument2 instanceof ConstraintClass;

        assert or.argument3 == null;
        assert or.argument4 == null;

        ConstraintClass first = null;
        if(or.argument1 instanceof AConstraint)
            first = new ConstraintClass((AConstraint) or.argument1);
        else if(or.argument1 instanceof ConstraintClass)
            first =(ConstraintClass) or.argument1;

        ConstraintClass second = null;
        if(or.argument2 instanceof AConstraint)
            second = new ConstraintClass((AConstraint) or.argument2);
        else if(or.argument2 instanceof ConstraintClass)
            second = (ConstraintClass) or.argument2;

        ConstraintClass firstNegated = negate(first);
        ConstraintClass secondNegated = negate(second);

        ConstraintClass result = firstNegated.and(secondNegated);
        assert result != null;
        return result;
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code and} constraint.
     * @param and constraint to be negated.
     * @return the negation of {@code and}.
     * @throws IllegalArgumentException if {@code and} contains constraint of unsupported kind.
     */
    private @NotNull
    ConstraintClass negateAnd(@NotNull ConstraintClass and){
        assert and != null;
        assert and.size() >= 2;

        Iterator<AConstraint> iterator = and.iterator();
        ArrayList<AConstraint> aConstraints = new ArrayList<>(and.size());
        iterator.forEachRemaining(aConstraints::add);

        List<AConstraint> first = aConstraints.subList(0, aConstraints.size() - 1);
        ConstraintClass firstConstraint = new ConstraintClass();
        first.forEach(firstConstraint::add);
        ConstraintClass firstNegated = negate(firstConstraint);
        ConstraintClass othersNegated = negate(aConstraints.get(aConstraints.size() - 1));


        String constraintType = "_or";
        if(and.get(and.size() - 2).getDefinitionConstraint() && and.get(and.size() - 2).constraintKindCode == Environment.eqCode || and.get(and.size() - 1).getDefinitionConstraint())
            constraintType = "_and";
        return new ConstraintClass(constraintType, firstNegated, othersNegated);
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code eq} constraint.
     * @param eq constraint to be negated.
     * @return the negation of {@code eq}.
     */
    private @NotNull
    ConstraintClass negateEq(@NotNull AConstraint eq){
        assert eq != null;
        assert eq.constraintKindCode == eqCode;
        assert eq.argument1 != null;
        assert eq.argument2 != null;
        assert eq.argument3 == null;
        assert eq.argument4 == null;

        return new ConstraintClass(new AConstraint(neqCode, eq.argument1, eq.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code neq} constraint.
     * @param neq constraint to be negated.
     * @return the negation of {@code neq}.
     */
    private @NotNull
    ConstraintClass negateNeq(@NotNull AConstraint neq){
        assert neq != null;
        assert neq.constraintKindCode == neqCode;
        assert neq.argument1 != null;
        assert neq.argument2 != null;
        assert neq.argument3 == null;
        assert neq.argument4 == null;

        return new ConstraintClass(new AConstraint(eqCode, neq.argument1, neq.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code in} constraint.
     * @param in constraint to be negated.
     * @return the negation of {@code in}.
     */
    private @NotNull
    ConstraintClass negateIn(@NotNull AConstraint in){
        assert in != null;
        assert in.constraintKindCode == inCode;
        assert in.argument1 != null;
        assert in.argument2 != null;
        assert in.argument3 == null;
        assert in.argument4 == null;

        return new ConstraintClass(new AConstraint(ninCode, in.argument1, in.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code nin} constraint.
     * @param nin constraint to be negated.
     * @return the negation of {@code nin}.
     */
    private @NotNull
    ConstraintClass negateNin(@NotNull AConstraint nin){
        assert nin != null;
        assert nin.constraintKindCode == ninCode;
        assert nin.argument1 != null;
        assert nin.argument2 != null;
        assert nin.argument3 == null;
        assert nin.argument4 == null;

        return new ConstraintClass(new AConstraint(inCode, nin.argument1, nin.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code subset} constraint.
     * @param subset constraint to be negated.
     * @return the negation of {@code subset}.
     */
    private @NotNull
    ConstraintClass negateSubset(@NotNull AConstraint subset){
        assert subset != null;
        assert subset.constraintKindCode == subsetCode;
        assert subset.argument1 != null;
        assert subset.argument2 != null;
        assert subset.argument3 == null;
        assert subset.argument4 == null;

        return new ConstraintClass(new AConstraint(nsubsetCode, subset.argument1, subset.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code lt} (less than) constraint.
     * @param lt constraint to be negated.
     * @return the negation of {@code lt}.
     */
    private @NotNull
    ConstraintClass negateLt(@NotNull AConstraint lt){
        assert lt != null;
        assert lt.constraintKindCode == ltCode;
        assert lt.argument1 != null;
        assert lt.argument2 != null;
        assert lt.argument3 == null;
        assert lt.argument4 == null;

        return new ConstraintClass(new AConstraint(Environment.geCode, lt.argument1, lt.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code le} (less than or equal to) constraint.
     * @param le constraint to be negated.
     * @return the negation of {@code le}.
     */
    private @NotNull
    ConstraintClass negateLe(@NotNull AConstraint le){
        assert le != null;
        assert le.constraintKindCode == leCode;
        assert le.argument1 != null;
        assert le.argument2 != null;
        assert le.argument3 == null;
        assert le.argument4 == null;

        return new ConstraintClass(new AConstraint(Environment.gtCode, le.argument1, le.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code gt} (greater than) constraint.
     * @param gt constraint to be negated.
     * @return the negation of {@code gt}.
     */
    private @NotNull
    ConstraintClass negateGt(@NotNull AConstraint gt){
        assert gt != null;
        assert gt.constraintKindCode == gtCode;
        assert gt.argument1 != null;
        assert gt.argument2 != null;
        assert gt.argument3 == null;
        assert gt.argument4 == null;

        return new ConstraintClass(new AConstraint(Environment.leCode, gt.argument1, gt.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code ge} (greater than or equal to) constraint.
     * @param ge constraint to be negated.
     * @return the negation of {@code ge}.
     */
    private @NotNull
    ConstraintClass negateGe(@NotNull AConstraint ge){
        assert ge != null;
        assert ge.constraintKindCode == geCode;
        assert ge.argument1 != null;
        assert ge.argument2 != null;
        assert ge.argument3 == null;
        assert ge.argument4 == null;

        return new ConstraintClass(new AConstraint(Environment.ltCode, ge.argument1, ge.argument2));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code size} constraint.
     * @param size constraint to be negated.
     * @return the negation of {@code size}.
     */
    private @NotNull
    ConstraintClass negateSize(@NotNull AConstraint size){
        assert size != null;
        assert size.constraintKindCode == sizeCode;
        assert size.argument1 != null;
        assert size.argument2 != null;
        assert size.argument3 == null;
        assert size.argument4 == null;

        IntLVar trueSize = new IntLVar();
        ConstraintClass trueSizeConstraint = new ConstraintClass("_size", size.argument1, trueSize);
        return trueSizeConstraint.and(trueSize.neq(size.argument2));
    }



    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code truec} constraint.
     * @param truec constraint to be negated.
     * @return the negation of {@code truec}.
     */
    private @NotNull
    ConstraintClass negateTrue(@NotNull AConstraint truec){
        assert truec != null;
        assert truec.constraintKindCode == trueCode;

        return new ConstraintClass(new AConstraint(falseCode, null, null));
    }

    /**
     * Constructs a new constraint which is the result of the negation of the
     * input {@code falsec} constraint.
     * @param falsec constraint to be negated
     * @return the negation of {@code falsec}
     */
    private ConstraintClass negateFalse(AConstraint falsec){
        assert falsec != null;
        assert falsec.constraintKindCode == falseCode;

        return new ConstraintClass(new AConstraint(trueCode, null, null));
    }

}
