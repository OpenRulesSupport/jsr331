package jsetl.ris.cache.circularlist;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

/**
 * Instances of this list are circular lists.
 * @param <T> The type of the values contained in the circular list.
 *
 * @author Andrea Fois
 * @see CircularListNode
 * @see CircularListIterator
 */
public class CircularList<T> implements Iterable<T> {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Reference to the first node of the circular list.
     */
    @Nullable
    CircularListNode<T> first;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an empty circular list.
     */
    public CircularList(){
        first = null;
    }

    /**
     * Constructs a new circular list that contains the same values of {@code circularList} in
     * the same order. The values are shared, the nodes are recreated.
     * @param circularList a circular list to copy.
     */
    public CircularList(@NotNull CircularList<T> circularList){
        this();
        if(!circularList.isEmpty()){
            CircularListNode<T> node = circularList.first;
            do{
                this.add(node.value);
                node = node.next;
            }while (node != circularList.first);
        }

        assert this.size() == circularList.size();
    }


    ///////////////////////////////////////////////////////////////
    ///////// PUBLIC METHODS //////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Adds the given value at the end (after the node before the first node of the circular list) of the circular list.
     * @param value the value to add.
     */
    public void add(@Nullable T value){
        if(isEmpty()){
            CircularListNode<T> nextLastNode = new CircularListNode<>(value);
            first =  nextLastNode;
        }
        else {
            CircularListNode<T> nextLastNode = new CircularListNode<>(first.previous, value, first);
            first.previous = nextLastNode;
        }
     }

    /**
     * Checks whether the circular list is empty or not.
     * @return {@code true} if the circular list is empty, {@code false} otherwise.
     */
    public boolean isEmpty(){
        return first == null;
     }

    /**
     * Returns an iterator over the circular list. This method should only be called if the list
     * is not empty. The returned iterator iterates indefinitely cycling through the elements of the circular list.
     * @return an iterator over the circular list.
     */
    public @NotNull CircularListIterator<T> iterator(){
        assert !this.isEmpty();
        return new CircularListIterator<>(this);
     }

    /**
     * Computes and returns the size of the circular list.
     * @return the size of the circular list.
     */
     public int size(){
        int size = 0;

        if(isEmpty())
            return size;

        CircularListNode<T> node = first;

        do{
            ++size;
            node = node.next;
        }while(node != first);

        assert size > 0;
        return size;
     }

}
