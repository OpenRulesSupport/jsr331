package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.*;

/**
 * Objects of this class are logical lists that can only represent pairs, i.e., logical pairs.
 */
public class LPair extends LList 
                  implements Cloneable, Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an unbound logical pair with default name.
     */
    public LPair() {
        super();
    }

    /**
     * Constructs an unbound logical pair with name {@code name}.
     * @param name the name of the logical pair.
     */
    public LPair(@NotNull String name) {
        super(name);

        assert name != null;
    }

    /**
     * Constructs a logical pair {@code [first,second]} with default name.
     * @param first the first element of the pair.
     * @param second the second element of the pair.
     */
    public LPair(@NotNull Object first, @NotNull Object second) {
        this(defaultName(), first, second);

        assert first != null;
        assert second != null;
    }

    /**
     * Constructs a logical pair {@code [first,second]} with external name {@code name}.
     * @param name the name of the pair.
     * @param first the first element of the pair.
     * @param second the second element of the pair.
     */
    public LPair(@NotNull String name, @NotNull Object first, @NotNull Object second) {
        super(name, new ArrayList<>(Arrays.asList(first, second)), LList.empty());

        assert name != null;
        assert first != null;
        assert second != null;
    }
 
    /**
     * Constructs a logical pair equal to the logical pair {@code lPair} with default name.
     * Using this constructor is equivalent to creating a new unbound logical pair
     * and posting and solving the constraint {@code this.eq(lPair)}.
     * @param lPair the other logical pair.
     */
    public LPair(@NotNull LPair lPair) {
       super(lPair);

       assert  lPair != null;
    }

    /**
     * Constructs a logical pair equal to the logical pair {@code lPair} with the given name.
     * Using this constructor is equivalent to creating a new unbound logical pair
     * and posting and solving the constraint {@code this.eq(lPair)}.
     * @param name the name of the pair.
     * @param lPair the other logical pair.
     */
    public LPair(@NotNull String name, @NotNull LPair lPair) {
        super(name, lPair);

        assert name != null;
        assert lPair != null;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    //////////////// GENERAL UTILITY METHODS //////////////////////

    /**
     * Returns a bit-by-bit clone of this object.
     * @return a clone.
     */
    @Override
    public @NotNull LPair clone() {
        LPair cloned = (LPair) super.clone();
        assert cloned != null;
        return cloned;
    }

    /**
     * Sets the name of this logical pair to {@code name}.
     * @param name the new name for this logical pair.
     * @return {@code this} with the name changed.
     */
    public @NotNull LPair setName(@NotNull String name) {
        super.setName(name);
        return this;
    }

    /**
     * Sets the value of this logical pair to {@code pair}.
     * @param pair a list that must have length 2.
     * @return {@code this} with the value assigned.
     * @throws jsetl.exception.InitLObjectException if this pair
     * is bound.
     * @throws NullPointerException if {@code pair} contains {@code null} values.
     * @throws IllegalArgumentException if {@code pair} has not size {@code 2}.
     */
    public LPair setValue(@NotNull List<?> pair) {
        Objects.requireNonNull(pair);
        if(pair.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        if(pair.size() != 2)
            throw new IllegalArgumentException("ARGUMENT MUST BE A PAIR");

        super.setValue(pair);
        return this;
    }

    /**
     * Needed for the visitor design pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of {@code visitor.visit(this)}.
     */
    public @Nullable Object accept(@NotNull Visitor visitor){
        assert visitor != null;
        return visitor.visit(this);
    }


    //////////////// LPAIR METHODS /////////////////////////////////

    /**
     * Returns the first element of this logical pair, if the pair is bound.
     * @return the first element.
     * @throws jsetl.exception.NotInitLObjectException if this logical collection is not bound.
     */
    public @NotNull Object getFirst() {
        Object first = super.get(0);
        assert first != null;
        return first;
    }

    /**
     * Returns the second element of this logical pair, if the pair is bound.
     * @return the second element.
     * @throws jsetl.exception.NotInitLObjectException if this logical collection is not bound.
     * @throws IndexOutOfBoundsException if this logical collection is bound to an open list with only one
     * known element.
     */
    public @NotNull Object getSecond() {
        Object second = super.get(1);
        assert second != null;
        return second;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

     /**
     * Default open delimiter for {@code LCollection}s.
     * @return the default open delimiter for logical collections.
     */
     @Override
    protected @NotNull Character openDelimitator() {
        return '(';
    }

    /**
     * Default close delimiter for {@code LCollections}
     * @return a character which is the default close delimiter for collections.
     */
    @Override
    protected @NotNull Character closeDelimitator() {
        return ')';
    }
   
   /**
    * Returns {@code true} if the first argument of this logical pair is initialized, {@code false} otherwise.
    * @return {@code true} if the first argument of this logical pair is initialized, {@code false} otherwise.
    */
   protected boolean isFirstInit(){
       if(this.equ == null)
           if (this.initialized)
              if (this.elements.get(0) instanceof LVar)
                   return ((LVar)this.elements.get(0)).isBound();
              else return true;
           else return false;
       else
           return ((LPair)this.equ).isFirstInit();
   }

    /**
     * Returns {@code true} if the second argument of this logical pair is initialized, {@code false} otherwise.
     * @return {@code true} if the second argument of this logical pair is initialized, {@code false} otherwise.
     */
   protected boolean isSecondInit(){
       if(this.equ == null)
           if (this.initialized)
              if (this.elements.get(1) instanceof LVar)
                   return ((LVar)this.elements.get(1)).isBound();
              else return true;
           else return false;
       else
           return ((LPair)this.equ).isFirstInit();
   }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain.
     */
    @Override
    protected @Nullable LPair getEndOfEquChain(){
        return (LPair) super.getEndOfEquChain();
    }
}