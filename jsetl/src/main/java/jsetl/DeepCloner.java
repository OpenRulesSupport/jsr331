package jsetl;


import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.annotation.UnsupportedOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to deeply clone a logical object.
 * The cloned object will have the same structure of the original object, as far as
 * logical objects are concerned.
 * If the same object appears multiple times inside the object that is being cloned, each of its
 * occurrences will be replaced with the same clone. Clones will have the same type as the originals.
 * Only logical objects and constraints are deeply cloned whereas the other Java objects are treated as atoms
 * and are returned as they are.
 * NOTE: If the object that needs to be cloned has a loop inside it (an object that contains itself) then the clonation
 * will loop indefinitely.
 *
 * If multiple objects are visited with the same instance of {@code DeepCloner}
 * and they share some of their variables, then those variables will be replaced with the same clones.
 *
 * @author Andrea Fois
 */
class DeepCloner implements Visitor{

    ///////////////////////////////////////////
    /////// PRIVATE MEMBERS ///////////////////
    ///////////////////////////////////////////

    /**
     * Map containing the mapping of original objects with their clones.
     * It is used to substitute more instances of the same object with the same clone.
     */
    private final Map<Object, Object> clones = new HashMap<>();


    ///////////////////////////////////////////
    /////// VISIT JAVA OBJECT /////////////////
    ///////////////////////////////////////////

    /**
     * Clones the given {@code object}. If it is an instance of {@code Visitable} it deeply clones it.
     * If it is not it simply returns the original {@code object}.
     * @param object object to clone.
     * @return a clone of {@code object}, if it is {@code Visitable}, {@code object} itself otherwise.
     */
    @Override
    public @Nullable Object visit(@Nullable  Object object){
        Object result;
        if(object != null && object instanceof Visitable)
               result = ((Visitable) object).accept(this);
       else
           result = object;

       assert result != null;
       return result;
    }


    ///////////////////////////////////////////
    /////// VISIT CONSTRAINTS /////////////////
    ///////////////////////////////////////////

    /**
     * Returns a deep clone of the given atomic constraint.
     * @param aConstraint object to clone.
     * @return a deep clone of {@code aConstraint}.
     */
    @Override
    @UnsupportedOperation
    public @NotNull Object visit(@NotNull AConstraint aConstraint) {
        throw new UnsupportedOperationException(); //TODO implement it
    }

    /**
     * Returns a deep clone of the given constraint.
     * @param constraint object to clone.
     * @return a deep clone of {@code constraint}.
     */
    @Override
    @UnsupportedOperation
    public @NotNull Object visit(@NotNull Constraint constraint) {
        throw new UnsupportedOperationException(); //TODO implement it
    }


    ///////////////////////////////////////////
    /////// VISIT LOGICAL VARIABLES ///////////
    ///////////////////////////////////////////

    /**
     * Returns a deep clone of the given logical variable.
     * @param lVar object to clone.
     * @return a deep clone of {@code lVar}.
     */
    @Override
    public @NotNull LVar visit(@NotNull LVar lVar){
        Objects.requireNonNull(lVar);
        assert lVar.getClass().equals(LVar.class);

        LVar result;
        if(!lVar.isBound())
            result = getClone(lVar);
        else
            result = new LVar(visit(lVar.getValue()));

        assert result != null;
        return result;
    }

    /**
     * Returns a deep clone of the given integer logical variable.
     * @param intLVar object to clone.
     * @return a deep clone of {@code intLVar}.
     */
    @Override
    public @NotNull IntLVar visit(@NotNull IntLVar intLVar){
        Objects.requireNonNull(intLVar);
        assert intLVar.getClass().equals(IntLVar.class);

        IntLVar result;
        if(!intLVar.isBound())
            result = getClone(intLVar);
        else
            result = new IntLVar(intLVar.getValue());

        assert result != null;
        return result;
    }

    /**
     * Returns a deep clone of the given boolean logical variable.
     * @param boolLVar object to clone.
     * @return a deep clone of {@code boolLVar}.
     */
    @Override
    @UnsupportedOperation
    public @NotNull BoolLVar visit(@NotNull BoolLVar boolLVar){
        throw new UnsupportedOperationException(); //TODO implement it
    }

    /**
     * Returns a deep clone of the given s logical variable.
     * @param setLVar object to clone.
     * @return a deep clone of {@code setLVar}.
     */
    @Override
    @UnsupportedOperation
    public @NotNull SetLVar visit(@NotNull SetLVar setLVar){
        throw new UnsupportedOperationException(); //TODO implement it
    }


    ///////////////////////////////////////////
    /////// VISIT LOGICAL LISTS ///////////////
    ///////////////////////////////////////////

    /**
     * Returns a deep clone of the given logical list.
     * @param lList object to clone.
     * @return a deep clone of {@code lList}.
     */
    @Override
    public @NotNull LList visit(@NotNull LList lList){
        Objects.requireNonNull(lList);
        assert lList.getClass().equals(LList.class);

        LList result;
        if(!lList.isBound())
            result = getClone(lList);
        else if(lList.isEmpty())
            result = LList.empty();
        else {

            ArrayList<Object> list = lList.toArrayList();
            ArrayList<Object> cloned = new ArrayList<>(list.size());
            for (Object elem : list)
                cloned.add(visit(elem));

            LList tail = lList.getTail();
            LList clonedTail = null;
            if (tail != null)
                clonedTail = visit(tail);

            result = new LList(cloned, clonedTail);
        }
        assert result != null;
        return result;
    }

    /**
     * Returns a deep clone of the given logical pair.
     * @param lPair object to clone.
     * @return a deep clone of {@code lPair}.
     */
    @Override
    public @NotNull LPair visit(@NotNull LPair lPair){
        Objects.requireNonNull(lPair);
        assert lPair.getClass().equals(LPair.class);

        LPair result;
        if(!lPair.isBound())
            result = getClone(lPair);
        else {
            ArrayList<Object> list = lPair.toArrayList();
            ArrayList<Object> cloned = new ArrayList<>(list.size());
            for (Object elem : list)
                cloned.add(visit(elem));

            LList tail = lPair.getTail();

            result = new LPair(cloned.get(0), cloned.get(1));
        }

        assert result != null;
        return result;
    }


    ///////////////////////////////////////////
    /////// VISIT LOGICAL SETS ////////////////
    ///////////////////////////////////////////

    /**
     * Returns a deep clone of the given logical set.
     * @param lSet object to clone.
     * @return a deep clone of {@code lSet}.
     */
    @Override
    public @NotNull LSet visit(@NotNull LSet lSet){
        Objects.requireNonNull(lSet);
        assert lSet.getClass().equals(LSet.class);

        LSet result;
        if(!lSet.isBound())
            result = getClone(lSet);
        else {
            ArrayList<Object> list = lSet.toArrayList();
            ArrayList<Object> cloned = new ArrayList<>(list.size());
            for (Object elem : list)
                cloned.add(visit(elem));

            LSet tail = lSet.getTail();
            LSet clonedTail = visit(tail);

            result = new LSet(cloned, clonedTail);
        }

        assert result != null;
        return result;
    }

    /**
     * Returns a deep clone of the given integer logical set.
     * @param intLSet object to clone.
     * @return a deep clone of {@code intLSet}.
     */
    @Override
    public @NotNull IntLSet visit(@NotNull IntLSet intLSet){
        Objects.requireNonNull(intLSet);
        assert intLSet.getClass().equals(IntLSet.class);

        IntLSet result;
        if(!intLSet.isBound())
            result = getClone(intLSet);
        else if(intLSet.isEmpty())
            result = IntLSet.empty();
        else {
            ArrayList<Object> list = intLSet.toArrayList();
            ArrayList<Object> cloned = new ArrayList<>(list.size());
            for (Object elem : list)
                cloned.add(visit(elem));

            LSet tail = intLSet.getTail();
            IntLSet clonedTail = (IntLSet) visit(tail);

            result = new IntLSet(cloned, clonedTail);
        }
        assert result != null;
        return result;
    }

    /**
     * Returns a deep clone of the given logical map.
     * @param lMap object to clone.
     * @return a deep clone of {@code lMap}.
     */
    @Override
    public @NotNull Object visit(@NotNull LMap lMap) {
        throw new UnsupportedOperationException(); //TODO implement it
    }

    /**
     * Returns a deep clone of the given logical binary relation.
     * @param lRel object to clone.
     * @return a deep clone of {@code lRel}.
     */
    @Override
    public @NotNull Object visit(@NotNull LRel lRel) {
        throw new UnsupportedOperationException(); //TODO implement it
    }

    /**
     * Returns a deep clone of the given restricted intensional set.
     * @param ris object to clone.
     * @return a deep clone of {@code ris}.
     */
    @Override
    public @NotNull Object visit(@NotNull Ris ris) {
        throw new UnsupportedOperationException(); //TODO implement it
    }

    /**
     * Returns a deep clone of the given cartesian product.
     * @param cp object to clone.
     * @return a deep clone of {@code cp}.
     */
    @Override
    public @NotNull Object visit(@NotNull CP cp){
        throw new UnsupportedOperationException(); // TODO: implement
    }


    ///////////////////////////////////////////
    /////// PRIVATE METHODS ///////////////////
    ///////////////////////////////////////////

    /**
     * Returns the clone of the given object. The given object is treated as an atom and the clone will
     * be created using the nullary constructor of the run-time type of {@code object}.
     * If called multiple times with the same object it will return the same result.
     * @param object object to clone.
     * @param <T> type of the parameter and of the result.
     * @return a clone for {@code object}.
     */
    private @NotNull <T> T getClone(@NotNull T object) {
        assert object != null;

        T clone = (T) clones.get(object);
        if(clone == null) {
            try {
                clone = (T) object.getClass().newInstance();
            }catch(IllegalAccessException exception){
                throw new IllegalArgumentException("TYPE NOT SUPPORTED: " + object.getClass());
            }
            catch(InstantiationException exception){
                throw new IllegalArgumentException("TYPE NOT SUPPORTED: " + object.getClass());
            }
            clones.put(object, clone);
        }

        assert clone != null;
        return clone;
    }
}
