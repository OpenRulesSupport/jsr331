package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.annotation.UnsupportedOperation;

import java.util.Iterator;

/**
 * This class is used to map objects into other objects based on substitutions
 * of their part that are those deducible from {@code original} and {@code mapped}.
 * This class makes the assumption that {@code original} and {@code mapped} have the same
 * structure if they are logical objects. <br>
 * Moreover, this class assumes that if the same object appears more than once in {@code original}
 * than all of its substitutions in {@code mapped} are the same. <br>
 * Objects that are not logical are treated as atoms and are not substituted.<br>
 * It is also assumed that only logical objects that are completely variables do actually differ from
 * {@code original} to {@code mapped}.<br>
 * None of the assumptions above are checked. <br>
 * Note that currently {@code CP}s are treated as atoms and are substituted only in their entirety
 * and not deeply.
 * Note that currently {@code Ris}s are mapped by mapping their domain, filter and pattern (and not control term).
 * <br><br>
 * For example, if {@code original} is {@code [X, [Y, 1]]} and
 * {@code mapped} is {@code [[1, 2, Y, Z], [3, 1]]},
 * then the mapping of {@code [X, Y, 3]} is {@code [[1, 2, Y, Z], [3], 3]}.
 *
 * @author Andrea Fois
 */
class LObjectMapper {

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


    //////////////////////////////////////////////////////////
    ///////////////// PROTECTED CONSTRUCTORS /////////////////
    //////////////////////////////////////////////////////////

    /**
     * Creates a logical objects mapper that uses a mapping function deduced
     * from the mapping of {@code original} into {@code mapped}.
     *
     * @param original object before the application of mapping.
     * @param mapped object after the application of mapping.
     */
    protected LObjectMapper(@NotNull Object original, @NotNull Object mapped){
        assert original != null;
        assert mapped != null;

        this.original = original;
        this.mapped = mapped;
    }


    //////////////////////////////////////////////////////////
    ///////////////// PROTECTED METHODS //////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code object}. Parts of {@code object} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code object}. If {@code object} is {@code null} this method returns {@code null}.
     *
     * @param object object to map.
     * @return the application of the mapping function to {@code object}.
     */
    protected Object mapLObjectDeeply(@Nullable Object object){
        if(object == null)
            return null;

        Object mappedObject = getOriginalPartMapped(object);
        if(mappedObject != object)
            return mappedObject;

        if(object instanceof LObject)
            return mapLObjectDeeply((LObject) object);

        assert object != null;
        return object;
    }


    //////////////////////////////////////////////////////////
    ///////////////// PRIVATE METHODS ////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lObject}. Parts of {@code lObject} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lObject}.
     *
     * @param lObject object to map.
     * @return the application of the mapping function to {@code lObject}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LObject lObject){
        assert lObject != null;

        Object mappedLObject;
        if(lObject instanceof LVar)
            mappedLObject = mapLObjectDeeply((LVar) lObject);
        else
            mappedLObject = mapLObjectDeeply((LCollection) lObject);

        assert mappedLObject != null;
        return mappedLObject;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lVar}. Parts of {@code lVar} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lVar}.
     *
     * @param lVar object to map.
     * @return the application of the mapping function to {@code lVar}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LVar lVar){
        assert lVar != null;

        Object mappedLVar = null;
        if(lVar instanceof IntLVar)
            mappedLVar = mapLObjectDeeply((IntLVar) lVar);
        else if(lVar instanceof BoolLVar)
            mappedLVar = mapLObjectDeeply((BoolLVar) lVar);
        else if(lVar instanceof SetLVar)
            mappedLVar = mapLObjectDeeply((SetLVar) lVar);
        else{
            assert lVar.getClass().equals(LVar.class);
            mappedLVar = getOriginalPartMapped(lVar);
        }

        assert mappedLVar != null;
        return mappedLVar;
    }

    /**
     * @throws UnsupportedOperationException always.
     */
    @NotNull
    @UnsupportedOperation
    private Object mapLObjectDeeply(@NotNull BoolLVar boolLVar){
        assert boolLVar != null;
        throw new UnsupportedOperationException("BOOLLVAR MAPPING NOT CURRENTLY SUPPORTED");
    }

    /**
     * @throws UnsupportedOperationException always.
     */
    @NotNull
    @UnsupportedOperation
    private Object mapLObjectDeeply(@NotNull SetLVar setLVar){
        assert setLVar != null;
        throw new UnsupportedOperationException("SETLVAR MAPPING NOT CURRENTLY SUPPORTED");
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code intLVar}. Parts of {@code intLVar} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code intLVar}.
     *
     * @param intLVar object to map.
     * @return the application of the mapping function to {@code intLVar}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull IntLVar intLVar){
        assert intLVar != null;
        assert intLVar.getClass().equals(IntLVar.class);

        Object mappedIntLVar = getOriginalPartMapped(intLVar);

        assert mappedIntLVar != null;
        return mappedIntLVar;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lCollection}. Parts of {@code lCollection} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lCollection}.
     *
     * @param lCollection object to map.
     * @return the application of the mapping function to {@code lCollection}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LCollection lCollection){
        assert lCollection != null;

        Object mappedLCollection;

        if(!lCollection.isBound())
            mappedLCollection = lCollection;
        else if(lCollection.isEmpty())
            mappedLCollection = lCollection;
        else if(lCollection instanceof LList)
            mappedLCollection = mapLObjectDeeply((LList) lCollection);
        else
            mappedLCollection = mapLObjectDeeply((LSet) lCollection);

        assert mappedLCollection != null;
        return mappedLCollection;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lList}. Parts of {@code lList} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lList}.
     *
     * @param lList object to map.
     * @return the application of the mapping function to {@code lList}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LList lList){
        assert lList != null;
        if(lList instanceof LPair)
            return mapLObjectDeeply((LPair) lList);

        LList replaced = (LList) getOriginalPartMapped(lList.getTail());

        for(Object element : lList)
            replaced = replaced.insn(mapLObjectDeeply(element));

        assert replaced != null;
        return replaced;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lPair}. Parts of {@code lPair} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lPair}.
     *
     * @param lPair object to map.
     * @return the application of the mapping function to {@code lPair}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LPair lPair){
        assert lPair != null;

        LList tail = (LList) getOriginalPartMapped(lPair.getTail());
        LList replaced = new LList();
        if(!lPair.isBound())
            return replaced;

        for(Object element : lPair)
            replaced = replaced.insn(mapLObjectDeeply(element));

        LPair result = new LPair();
        result.setRest(tail);
        result.setValue(replaced.getElements());

        assert result != null;
        return result;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code lSet}. Parts of {@code lSet} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code lSet}.
     *
     * @param lSet object to map.
     * @return the application of the mapping function to {@code lSet}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull LSet lSet){
        assert lSet != null;

        Object mappedLSet;
        if(lSet instanceof Ris)
            mappedLSet = mapLObjectDeeply((Ris) lSet);
        else if(lSet instanceof CP)
            mappedLSet = mapLObjectDeeply((CP) lSet);
        else {
            LSet replaced = (LSet) getOriginalPartMapped(lSet.getTail());

            for (Object element : lSet)
                replaced = replaced.ins(mapLObjectDeeply(element));

            mappedLSet = replaced;
        }

        assert  mappedLSet != null;
        return mappedLSet;
    }

    /**
     * Returns an object which is the result of the mapping function
     * applied to the argument {@code ris}. Parts of {@code ris} that
     * are not mapped are returned as they are including, possibly, the entire
     * {@code ris}. The mapped {@code Ris} has its domain, filter and pattern
     * (but not its control term) mapped.
     *
     * @param ris object to map.
     * @return the application of the mapping function to {@code ris}.
     */
    @NotNull
    private Object mapLObjectDeeply(@NotNull Ris ris){
        assert ris != null;

        LObject controlTerm = ris.getControlTerm();
        LSet domainMapped = (LSet) mapLObjectDeeply(ris.getDomain());
        ConstraintClass filterMapped = new ConstraintMapper(original, mapped).mapConstraintDeeply(ris.getFilter());
        LObject patternMapped = (LObject) mapLObjectDeeply(ris.getPattern());

        return new Ris(controlTerm, domainMapped, filterMapped, patternMapped);
    }

    /**
     * @throws UnsupportedOperationException always.
     */
    @NotNull
    @UnsupportedOperation
    private Object mapLObjectDeeply(@NotNull CP cp){
        assert cp != null;
        throw new UnsupportedOperationException("CP MAPPING NOT CURRENTLY SUPPORTED");
    }

    /**
     * If {@code originalPart} is not instance of {@code LObject} returns {@code originalPart}.
     * If {@code originalPart} is instance of {@code LObject} then it searches in {@code original}
     * for {@code originalPart} and if it finds it then it returns its mapping in {@code mapped},
     * otherwise it returns {@code originalPart} itself.
     *
     * @param originalPart object to search direct mapping in {@code original} and {@code mapped}.
     * @return the mapping of {@code originalPart}.
     */
    @NotNull
    private Object getOriginalPartMapped(@NotNull Object originalPart){
        assert originalPart != null;

        Object mappedOriginalPart = originalPart;
        if(!(originalPart instanceof LObject)){
            return mappedOriginalPart;
        }

        else if(originalPart == original && originalPart instanceof IntLVar && mapped instanceof Integer)
            mappedOriginalPart = new IntLVar((Integer)mapped);
        else if(originalPart == original)
            mappedOriginalPart = mapped;
        else if(original instanceof LCollection && mapped instanceof LCollection){
            Iterator<Object> originalElements = ((LCollection) original).iterator();
            Iterator<Object> mappedElements = ((LCollection) mapped).iterator();

            while(originalElements.hasNext() && mappedElements.hasNext()){
                Object attemptedMapping = new LObjectMapper(originalElements.next(), mappedElements.next()).getOriginalPartMapped(originalPart);
                if(attemptedMapping != originalPart) {
                    mappedOriginalPart = attemptedMapping;
                    break;
                }
            }
        }

        assert mappedOriginalPart != null;
        return mappedOriginalPart;
    }
}

