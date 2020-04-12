package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@code DeepExplorer} can explore deeply an {@code object} and call the provided
 * {@code explorationTask} for each of the nodes of the exploration tree, passing the node object as
 * parameter of {@code explorationTask#executeTask}.
 * Note that only instances of {@code LCollection}, {@code Constraint} and {@code AConstraint} are explored
 * deeply, the other nodes (and in particular those which are instances of {@code LVar}) are always leaves of
 * the exploration tree: {@code explorationTask#executeTask} is called with them as parameters but they are not explored internally.
 * The exploration is carried on as a DFS visit of the object
 * and if the same node appears multiple times it is only explored once.
 * Multiple calls to {@code explore} or {@code exploreInternally} will not explore the objects
 * already explored by previous calls.
 * None of the methods of this class are thread safe.
 *
 * @author Andrea Fois
 */
class DeepExplorer{

    //////////////////////////////////////////////////////////
    /////////////// PRIVATE MEMBERS //////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Set containing all explored nodes. It is used to avoid re-exploring the same node twice.
     */
    private Set<Object> exploredNodes = new HashSet<>();

    /**
     * Boolean telling whether the explorer should stop the exploration and return {@code returnedObject}.
     * This value becomes {@code true} when {@code DeepExplorer#stopExploration} is called.
     * @see DeepExplorer#stopExploration
     * @see DeepExplorer#returnedObject
     */
    private boolean stopExploration = false;

    /**
     * The object returned after the exploration. Defaults to {@code null}.
     * It is set by the call to {@code DeepExplorer#stopExploration}.
     */
    private Object returnedObject;

    /**
     * When this variable is set to {@code true} the current object will not be explored deeply.
     * Only the current object will be skipped.
     */
    private boolean skipCurrentObject = false;


    //////////////////////////////////////////////////////////
    /////////////// PROTECTED METHODS ////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Tells the deep explorer not to explore internally the current object.
     * Only the current object will not explored deeply.
     */
    protected void doNotExploreCurrentObject(){
        skipCurrentObject = true;
    }

    /**
     * Calling this method inside {@code explore} or {@code exploreInternally} will result in
     * the exploration being stopped and in {@code returnedObject} being returned.
     * @param returnedObject the object that will be returned by the exploration.
     */
    protected void stopExploration(@Nullable Object returnedObject){
        this.stopExploration = true;
        this.returnedObject = returnedObject;
    }

    /**
     * Explores deeply an object. The method {@code explorationTask.executeTask}
     * will be called with {@code object} as parameter, and for each node found in the
     * DFS visit of {@code object}.
     * Note that only nodes that are instances of {@code LObject}, {@code Constraint}
     * or {@code AConstraint} are explored deeply, the other nodes are always leaves of the DFS tree.
     * This is the only method that directly calls {@code explorationTask#executeTask}.
     *
     * @param object root of the exploration tree.
     * @param explorationTask provides a task to execute for each node of the exploration tree
     *                        by its method {@code executeTask}, which is called with the node
     *                        as parameter.
     * @return the object set to be returned by a call of the method {@code stopExploration} in {@code explorationTask},
     * {@code null} if that method was not called.
     */
    protected @Nullable Object explore(@NotNull Object object, @NotNull ExplorationTask explorationTask){
        assert object != null;
        assert explorationTask != null;

        stopExploration = false;

        doExploration(object, explorationTask);

        return returnedObject;
    }

    /**
     * The object to explore deeply. The method {@code explorationTask.executeTask}
     * will not be called with {@code object} as parameter, but it will for each node found in the
     * DFS visit of {@code object}.
     * Note that only nodes that are instances of {@code LObject}, {@code Constraint}
     * or {@code AConstraint} are explored deeply, the other nodes are always leaves of the DFS tree.
     *
     * @param object root of the exploration tree.
     * @param explorationTask provides a task to execute for each node of the exploration tree
     *                        by its method {@code executeTask}, which is called with the node
     *                        as parameter.
     * @return the object set to be returned by a call of the method {@code stopExploration} in {@code explorationTask},
     * {@code null} if that method was not called.
     */
    protected @Nullable Object exploreInternally(@NotNull Object object, @NotNull ExplorationTask explorationTask){
        assert object != null;
        assert explorationTask != null;

        stopExploration = false;

        doInternalExploration(object, explorationTask);

        return returnedObject;
    }


    //////////////////////////////////////////////////////////
    /////////////// PRIVATE METHODS //////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * Explores deeply an object. The method {@code explorationTask.executeTask}
     * will be called with {@code object} as parameter, and for each node found in the
     * DFS visit of {@code object}.
     * Note that only nodes that are instances of {@code LObject}, {@code Constraint}
     * or {@code AConstraint} are explored deeply, the other nodes are always leaves of the DFS tree.
     * This is the only method that directly calls {@code explorationTask#executeTask}.
     * The exploration is stopped if the executed task calls the method {@code DeepExplorer#stopExploration}
     *
     * @param object root of the exploration tree.
     * @param explorationTask provides a task to execute for each node of the exploration tree
     *                        by its method {@code executeTask}, which is called with the node
     *                        as parameter.
     */
    private void doExploration(@NotNull Object object, @NotNull ExplorationTask explorationTask){
        assert object != null;
        assert explorationTask != null;

        if(exploredNodes.contains(object))
            return;

        exploredNodes.add(object);

        //explore object
        explorationTask.executeTask(object);

        if(!stopExploration)
            if(!skipCurrentObject)
                explorationRouter(object, explorationTask);
            else
                skipCurrentObject = false;
    }

    /**
     * The object to explore deeply. The method {@code explorationTask.executeTask}
     * will not be called with {@code object} as parameter, but it will for each node found in the
     * DFS visit of {@code object}.
     * Note that only nodes that are instances of {@code LObject}, {@code Constraint}
     * or {@code AConstraint} are explored deeply, the other nodes are always leaves of the DFS tree.
     * The exploration is stopped if the executed task calls the method {@code DeepExplorer#stopExploration}
     *
     * @param object root of the exploration tree.
     * @param explorationTask provides a task to execute for each node of the exploration tree
     *                        by its method {@code executeTask}, which is called with the node
     *                        as parameter.
     */
    private  void doInternalExploration(@NotNull Object object, @NotNull ExplorationTask explorationTask){
        assert object != null;
        assert explorationTask != null;

        if(exploredNodes.contains(object))
            return;

        if(!stopExploration)
            explorationRouter(object, explorationTask);
    }


    /**
     * This method is used to route the recursive part of exploration to the correct method
     * based on the run-time type of {@code object}.
     *
     * @param object object to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explorationRouter(@NotNull Object object, @NotNull ExplorationTask explorationTask){
        assert object != null;
        assert explorationTask != null;

        if(object instanceof LObject)
            explore((LObject) object, explorationTask);
        else if(object instanceof Constraint)
            explore((Constraint) object, explorationTask);
        else if(object instanceof AConstraint)
            explore((AConstraint) object, explorationTask);
    }

    /**
     * Explores deeply a logical object. Routes the call to the right method depending on the
     * run-time type of {@code lObject}.
     * @param lObject logical object to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull LObject lObject, @NotNull ExplorationTask explorationTask){
        assert lObject != null;
        assert explorationTask != null;

        if(lObject instanceof LVar)
            explore((LVar) lObject, explorationTask);
        else
            explore((LCollection) lObject, explorationTask);
    }

    /**
     * Explores a logical variable. A logical variable is a leaf of the exploration tree.
     * @param lVar logical variable to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull LVar lVar, @NotNull ExplorationTask explorationTask){
        assert lVar != null;
        assert explorationTask != null;

        if(lVar.isBound())
            doExploration(lVar.getValue(), explorationTask);
    }

    /**
     * Explores deeply a logical collection. The exploration is called recursively
     * for each of its known elements and for its tail.
     * @param lCollection logical collection to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull LCollection lCollection, @NotNull ExplorationTask explorationTask){
        assert lCollection != null;
        assert explorationTask != null;

        if(lCollection instanceof CP) {
            explore((CP) lCollection, explorationTask);
            return;
        }

        if(lCollection instanceof Ris){
            explore((Ris) lCollection, explorationTask);
            return;
        }

        if(!lCollection.isBound())
            return;

        for(Object elem : lCollection)
            doExploration(elem, explorationTask);
        LCollection tail = lCollection.getTail();
        if(tail != null)
            doExploration((Object) tail, explorationTask);
    }

    /**
     * Explores deeply a cartesian product. The exploration is called recursively
     * on internal parts.
     * @param cp cartesian product to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree
     */
    private void explore(@NotNull CP cp, @NotNull ExplorationTask explorationTask){
        Object A = cp.getFirstSet();

        if(A != null)
            explore(A, explorationTask);

        Object B = cp.getSecondSet();

        if(B != null)
            explore(B, explorationTask);

    }

    /**
     * Explores deeply a restricted intensional set. The exploration is called recursively
     * on its control term, domain, filter and pattern.
     * @param ris restricted intensional set to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull Ris ris, @NotNull ExplorationTask explorationTask){
        assert ris != null;
        assert explorationTask != null;

        doExploration((Object) ris.getControlTerm(), explorationTask);
        doExploration((Object) ris.getPattern(), explorationTask);
        doExploration((Object) ris.getFilter(), explorationTask);
        doExploration((Object) ris.getDomain(), explorationTask);
    }

    /**
     * Explores deeply a constraint conjunction. The exploration is called recursively
     * for each {@code AConstraint} in the constraint conjunction.
     * @param constraint constraint conjunction to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull Constraint constraint, @NotNull ExplorationTask explorationTask){
        assert constraint != null;
        assert explorationTask != null;

        Iterator<AConstraint> aConstraintIterator = constraint.iterator();
        while(aConstraintIterator.hasNext())
            doExploration((Object) aConstraintIterator.next(), explorationTask);
    }

    /**
     * Explores deeply an atomic constraint. The exploration is called recursively
     * for each (non {@code null}) argument of the atomic constraint.
     * @param aConstraint atomic constraint to explore deeply.
     * @param explorationTask task to execute for each node of the exploration tree.
     */
    private void explore(@NotNull AConstraint aConstraint, @NotNull ExplorationTask explorationTask){
        assert aConstraint != null;
        assert explorationTask != null;

        if(aConstraint.argument1 != null)
            doExploration(aConstraint.argument1, explorationTask);
        if(aConstraint.argument2 != null)
            doExploration(aConstraint.argument2, explorationTask);
        if(aConstraint.argument3 != null)
            doExploration(aConstraint.argument3, explorationTask);
        if(aConstraint.argument4 != null)
            doExploration(aConstraint.argument4, explorationTask);
    }


    //////////////////////////////////////////////////////////
    /////////////// INNER CLASSES ////////////////////////////
    //////////////////////////////////////////////////////////

    /**
     * This interface is implemented and instantiated to provide a task to execute for each object
     * found when exploring an object (possibly logical).
     */
    interface ExplorationTask{

        /**
         * Task to execute for each object found during an exploration
         * @param object the node being explored currently
         */
        void executeTask(@NotNull Object object);
    }
}
