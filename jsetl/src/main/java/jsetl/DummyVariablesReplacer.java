package jsetl;

import jsetl.annotation.NotNull;

import java.util.*;

/**
 * This class is used to replace dummy variables in constraints (filter and negated filter for example) and logical objects (pattern).
 * This class handles all dummy variables substitutions and is able to generate new substitutions for dummy variables when needed.
 * @author Andrea Fois
 */
class DummyVariablesReplacer {

    /////////////////////////////////////////
    ///// PRIVATE MEMBERS ///////////////////
    /////////////////////////////////////////

    /**
     * A map used to remember the substitutions already created for dummy variables.
     */
    private final Map<LObject, LObject> variablesSubstitutions = new HashMap<>();

    /**
     * List containing all dummy variables.
     */
    private final ArrayList<LObject> dummyVariables;


    /////////////////////////////////////////
    ///// PROTECTED CONSTRUCTORS ////////////
    /////////////////////////////////////////

    /**
     * Constructs a dummy variables replacer that will treat as dummy variables each variable in {@code dummyVariables}.
     * Moreover, this constructor generates new substitutions for each dummy variable.
     * @param dummyVariables list of dummy variables. It can't contain {@code null} values.
     */
    protected DummyVariablesReplacer(@NotNull List<LObject> dummyVariables){
        assert dummyVariables != null;
        assert dummyVariables.stream().noneMatch(Objects::isNull);

        this.dummyVariables = new ArrayList(dummyVariables);
        generateNewSubstitutions();
    }

    /**
     * Constructs a copy of {@code this}: a new dummy variables replacer that has the same list of dummy variables.
     * @param dummyVariablesReplacer dummy variables replacer to copy.
     */
    protected DummyVariablesReplacer(@NotNull DummyVariablesReplacer dummyVariablesReplacer){
        assert dummyVariablesReplacer != null;
        dummyVariables = (ArrayList<LObject>) dummyVariablesReplacer.dummyVariables.clone();
        generateNewSubstitutions();
        assert  dummyVariables != null;
        assert variablesSubstitutions != null;
    }


    /////////////////////////////////////////
    ///// PROTECTED METHODS /////////////////
    /////////////////////////////////////////

    /**
     * This method is used to create new substitutions for each dummy variable in {@code dummyVariables}.
     * Each new instance is created through the nullary constructor of the class of the dummy variable.
     * The generated dummy variables replace completely those previously created and are stored internally in {@code this} object.
     * @throws IllegalStateException if this method is not able to create a new instance of the class of a dummy variable
     * using its nullary constructor.
     */
    protected void generateNewSubstitutions(){
        variablesSubstitutions.clear();
        for(LObject dummy : dummyVariables)
            try {
                variablesSubstitutions.put(dummy, dummy.getClass().newInstance());
            }catch(InstantiationException exception){
                throw new IllegalStateException("UNABLE TO CREATE NEW INSTANCE OF " + dummy.getClass());
            }
            catch (IllegalAccessException exception){
                throw new IllegalStateException("UNABLE TO ACCESS CONSTRUCTOR WITH 0 PARAMETERS OF CLASS " + dummy.getClass());
            }
    }

    /**
     * Uses the (previously constructed) mapping of dummy variables and substitutions to replace each dummy variable
     * with its substitution in {@code constraint} and return the so-constructed constraint.
     * The parameter {@code constraint} is not modified.
     * @param constraint constraint used to create the replaced constraint.
     * @return the constraint created.
     */
    @NotNull
    protected Constraint replaceInConstraint(@NotNull Constraint constraint){
        assert constraint != null;

        Constraint replaced = constraint;

        for(LObject dummy : dummyVariables)
            replaced = new ConstraintMapper(dummy, variablesSubstitutions.get(dummy)).mapConstraintDeeply(replaced);

        assert replaced != null;
        return replaced;
    }

    /**
     * Uses the (previously constructed) mapping of dummy variables and substitutions to replace each dummy variable
     * with its substitution in {@code lObject} and return the so-constructed object.
     * The parameter {@code lObject} is not modified.
     * @param lObject logical object used to create the replaced object.
     * @return the object created.
     */
    @NotNull
    protected Object replaceInLObject(@NotNull LObject lObject){
        assert lObject != null;

        Object replaced = lObject;

        for(LObject dummy : dummyVariables)
            if(replaced instanceof LObject)
                replaced = new LObjectMapper(dummy, variablesSubstitutions.get(dummy)).mapLObjectDeeply(replaced);
            else
                break;
        assert replaced != null;
        return replaced;
    }

    /**
     * Returns the list of dummy variables in {@code this} (the actual list, not a clone).
     * None of the elements in the result is {@code null}.
     * @return the list of dummy variables.
     */
    protected @NotNull ArrayList<LObject> getDummyVariables(){
        assert dummyVariables != null;

        assert dummyVariables.stream().noneMatch(Objects::isNull);
        return dummyVariables;
    }

    /**
     * Returns a clone of this {@code DummyVariablesReplacer}. The same as {@code new DummyVariablesReplacer(this)}.
     * @return a clone of {@code this}.
     */
    @Override
    protected @NotNull
    DummyVariablesReplacer clone(){
        return new DummyVariablesReplacer(this);
    }
}
