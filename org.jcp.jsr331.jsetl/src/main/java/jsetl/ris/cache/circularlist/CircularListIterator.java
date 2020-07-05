package jsetl.ris.cache.circularlist;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.Iterator;
import java.util.Objects;

/**
 * Instances of this class are used to iterate through the circular list from which they are obtained.
 * The iteration is infinite if the circular list is not empty.
 * Moreover, these iterators are not invalidated by changes in the original circular list.
 *
 * @author Andrea Fois
 * @see CircularList
 * @see CircularListNode
 */
public class CircularListIterator<T> implements Iterator<T> {

    ///////////////////////////////////////////////////////////////
    ///////// DATA MEMBERS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Reference to the circular list.
     */
    private final CircularList circularList;

    /**
     * Current node in the iteration.
     */
    private CircularListNode<T> currentNode;


    ///////////////////////////////////////////////////////////////
    ///////// CONSTRUCTORS ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an iterator using the given {@code circularList}.
     * The iterations starts from the first ({@code circularList.first}) node in the circular list.
     * @param circularList reference to the circular list through which the iterator iterates.
     */
    CircularListIterator(@NotNull final CircularList<T> circularList) {
        assert circularList != null;
        assert circularList.first != null;
        assert !circularList.isEmpty();

        this.circularList = circularList;
        this.currentNode = circularList.first;
    }


    ///////////////////////////////////////////////////////////////
    ///////// PUBLIC METHODS //////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Checks if there is more {@code nodes} to iterate through.
     * @return {@code true} if there is another node in the sequence, {@code false} otherwise.
     */
    public boolean hasNext(){
        return currentNode != null;
    }

    /**
     * Returns the next node in the iteration and goes forward in the iterations.
     * @return the next node in the circular list iteration.
     */
    public @NotNull T next(){
       assert currentNode != null;

       T value = currentNode.value;
       currentNode = currentNode.next;

       assert value != null;
       return value;
    }

    /**
     * Removes the node returned in the last call of {@code next} from the circular list.
     */
    public void remove(){
       assert !circularList.isEmpty();

       if(currentNode == circularList.first && currentNode == circularList.first.previous){
           circularList.first = null;
           currentNode = null;
           return;
       }

       currentNode = currentNode.previous;
       CircularListNode<T> previous = currentNode.previous;
       CircularListNode<T> next = currentNode.next;
       previous.next = next;
       next.previous = previous;
       if(currentNode == circularList.first){
           circularList.first = next;
       }
       currentNode = next;
    }
}
