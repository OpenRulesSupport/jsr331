package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * This class is used to retrieve the list of all the variables occurring in a constraint, atomic constraint or
 * logical object.
 * Note that only logical objects, constraints and atomic constraints are deeply scanned, normal objects (for example arrays) are
 * treated as atoms.
 *
 * @author Andrea Fois
 */
class VariablesGetter{

    ///////////////////////////////////////////
    //////// PRIVATE MEMBERS //////////////////
    ///////////////////////////////////////////

    /**
     * Used to store the set of objects already collected.
     * Used to avoid loops.
     */
    private final Set<LObject> objectsGotten = new HashSet<>();

    /**
     * When this variable is set to {@code true}, the exploration will skip {@code Ris}s.
     * Defaults to {@code false}.
     */
    private boolean avoidRis = false;

    /**
     * When this variable is set to {@code false}, the exploration will skip the internal
     * constraints associated with {@code IntLVar}s, {@code SetLVar}s and {@code BoolLVar}s.
     * Defaults to {@code false}.
     */
    private boolean ignoreVariablesInternalConstraints = false;

    ///////////////////////////////////////////
    //////// PROTECTED METHODS ////////////////
    ///////////////////////////////////////////

    /**
     * Using this method will make this {@code VariablesGetter} skip instances of {@code Ris} during the exploration.
     */
    protected void avoidRis(){
        avoidRis = true;
    }

    /**
     * Using this method will make this {@code VariablesGetter} skip
     * internal constraints associated with variables of type {@code IntLVar}, {@code SetLVar} and {@code BoolLVar}.
     */
    protected void ignoreVariablesInternalConstraints(){
        ignoreVariablesInternalConstraints = true;
    }

    /**
     * Returns the list of all the variables (i.e. unbound logical objects) occurring in {@code constraint}.
     * @param constraint constraint to look into for variables.
     * @return the list of unbound logical objects occurring in {@code constraint}. It will not contain {@code null} values.
     */
    protected @NotNull List<LObject> getVariables(@NotNull ConstraintClass constraint){
        assert constraint != null;

        List<LObject> variables = new LinkedList<>();

        Iterator<AConstraint> iterator = constraint.iterator();
        while(iterator.hasNext())
            variables.addAll(getVariables(iterator.next()));

        variables = removeIdenticalDuplicates(variables);

        assert variables != null;
        assert variables.stream().noneMatch(Objects::isNull);
        return variables;
    }


    /**
     * Returns the list of all the variables (i.e. unbound logical objects) occurring in {@code aConstraint}.
     * @param aConstraint atomic constraint to look into for variables.
     * @return the list of unbound logical objects occurring in {@code aConstraint}. It will not contain {@code null} values.
     */
    protected @NotNull List<LObject> getVariables(@NotNull AConstraint aConstraint){
        assert aConstraint != null;

        int n = 0;
        for(int i = 0; i < 4; ++i)
            if(aConstraint.getArg(i + 1) != null)
                ++n;
            else
                break;

        if(n == 0)
            return new ArrayList<>();

        List<LObject> variables = new LinkedList<>();

        assert aConstraint.argument1 != null;
        variables.addAll(getVariables(aConstraint.argument1));

        if(aConstraint.argument2 == null)
            return removeIdenticalDuplicates(variables);
        variables.addAll(getVariables(aConstraint.argument2));

        if(aConstraint.argument3 == null)
            return removeIdenticalDuplicates(variables);
        variables.addAll(getVariables(aConstraint.argument3));

        if(aConstraint.argument4 == null)
            return removeIdenticalDuplicates(variables);
        variables.addAll(getVariables(aConstraint.argument4));

        variables = removeIdenticalDuplicates(variables);

        assert variables != null;
        assert variables.stream().noneMatch(Objects::isNull);
        return variables;
    }

    /**
     * Returns the list of all the variables (i.e. unbound logical objects) occurring in {@code object}
     * Note that only logical objects are scanned deeply for variables, ordinary java objects (for example arrays) are treated as atoms.
     * @param object object to look into for variables.
     * @return the list of unbound logical objects occurring in {@code object}. It will not contain {@code null} values.
     */
    protected @NotNull List<LObject> getVariables(@Nullable Object object){
        List variables = new LinkedList();
        if(object == null)
            return variables;

        DeepExplorer explorer = new DeepExplorer();
        explorer.explore(object, (Object obj) -> {
            if(avoidRis && obj instanceof Ris){
                explorer.doNotExploreCurrentObject();
                return;
            }
            if(obj instanceof LObject && !((LObject) obj).isBound())
                variables.add(((LObject) obj).getEndOfEquChain());

            if(obj instanceof IntLVar){
                boolean alreadyAdded = false;
                for(LObject lObject : objectsGotten)
                    if(lObject.getEndOfEquChain() == ((IntLVar) obj).getEndOfEquChain()){
                        alreadyAdded = true;
                        break;
                    }
                if(!alreadyAdded) {
                    objectsGotten.add(((IntLVar) obj).getEndOfEquChain());
                    if(!ignoreVariablesInternalConstraints) variables.addAll(getVariables(((IntLVar) obj).getConstraint()));

                }
            }

            else if(obj instanceof SetLVar){
                boolean alreadyAdded = false;
                for(LObject lObject : objectsGotten)
                    if(lObject.getEndOfEquChain() == ((SetLVar) obj).getEndOfEquChain()){
                        alreadyAdded = true;
                        break;
                    }
                if(!alreadyAdded) {
                    objectsGotten.add(((SetLVar) obj).getEndOfEquChain());
                    if(!ignoreVariablesInternalConstraints) variables.addAll(getVariables(((SetLVar) obj).getConstraint()));

                }
            }

            else if(obj instanceof BoolLVar){
                boolean alreadyAdded = false;
                for(LObject lObject : objectsGotten)
                    if(lObject.getEndOfEquChain() == ((BoolLVar) obj).getEndOfEquChain()){
                        alreadyAdded = true;
                        break;
                    }
                if(!alreadyAdded) {
                    objectsGotten.add(((BoolLVar) obj).getEndOfEquChain());
                    if(!ignoreVariablesInternalConstraints) variables.addAll(getVariables(((BoolLVar) obj).getConstraint()));

                }
            }


        });

        assert variables != null;
        assert variables.stream().noneMatch(Objects::isNull);
        return variables;
    }


    ///////////////////////////////////////////
    //////// PRIVATE METHODS //////////////////
    ///////////////////////////////////////////

    /**
     * Returns a new list which is created by the removal of identical duplicates in {@code list}.
     * Two logical objects are considered identical duplicates if they are exactly the same object (operator {@code ==}):
     * their method {@code equals} is not called.
     *
     * @param list original list. It must not contain {@code null} values.
     * @return {@code list} without identical duplicates. It will not contain {@code null} values.
     */
    private @NotNull List<LObject> removeIdenticalDuplicates(@NotNull List<LObject> list){
        assert list != null;
        assert list.stream().noneMatch(Objects::isNull);

        List<LObject> newList = new LinkedList<>();

        for(LObject elem : list){
            boolean found = false;
            for(LObject newElem : newList)
                if(newElem == elem){
                    found = true;
                    break;
                }
            if(!found)
                newList.add(elem);
        }

        assert newList != null;
        assert newList.stream().noneMatch(Objects::isNull);
        return newList;
    }
}