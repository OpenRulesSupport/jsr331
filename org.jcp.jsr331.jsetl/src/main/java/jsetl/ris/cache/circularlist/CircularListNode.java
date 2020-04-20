package jsetl.ris.cache.circularlist;

import jsetl.annotation.NotNull;

/**
 * Instances of this class are nodes of a circular list. They have a value of type {@code T},
 * a reference to the previous node and to the next node.
 * @param <T> the type of the value contained in the node.
 * @author Andrea Fois
 * @see CircularList
 */
class CircularListNode<T> {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Reference to the previous node in the circular list.
     */
    @NotNull CircularListNode<T> previous;

    /**
     * Value stored in the node.
     */
    final @NotNull T value;

    /**
     * Reference to the next node in the circular list.
     */
    @NotNull CircularListNode<T> next;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs a new node that contains the given {@code value}, and the given references
     * for the {@code previous} and {@code next} node in the list.
     *
     * @param previous reference to the previous node in the list.
     * @param value value stored in the node.
     * @param next reference to the next node in the list.
     */
    CircularListNode(@NotNull final CircularListNode<T> previous, @NotNull final T value, @NotNull final CircularListNode<T> next) {
        assert previous != null;
        assert value != null;
        assert next != null;

        this.previous = previous;
        this.value = value;
        this.next = next;
    }

    /**
     * Constructs a new node that contains the given {@code value} and itself as the previous and next node.
     *
     * @param value value stored in the node.
     */
    CircularListNode(@NotNull final T value){
        assert value != null;

        this.previous = this;
        this.value = value;
        this.next = this;
    }
}
