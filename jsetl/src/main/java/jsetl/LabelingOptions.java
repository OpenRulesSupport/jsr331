package jsetl;

import jsetl.comparator.*;
import jsetl.annotation.NotNull;
import jsetl.exception.UnsupportedHeuristicException;

import java.util.*;

/**
 * Objects of this class provide options for labeling variables of type {@code IntLVar}, {@code SetLVar} and {@code BoolLVar}
 * It provides methods to assign the first value to a {@code IntLVar}, {@code SetLVar} and {@code BoolLVar} and
 * to choose which variable to label first out of a list of variables.
 */
public class LabelingOptions {

    //////////////////////////////////////////////////////
    ////////////// STATIC MEMBERS ////////////////////////
    //////////////////////////////////////////////////////

    /**
     * Default value for the public field {@code valueForIntLVar}.
     * The first value assigned is the greatest lower bound of the domain of the {@code IntLVar}.
     * Its value is {@code ValHeuristic.GLB}.
     */
    private final static ValHeuristic DEFAULT_VALUE_FOR_INTLVAR = ValHeuristic.GLB;

    /**
     * Default value for the public field {@code variableChosenInAList}.
     * The variable chosen is the leftmost value in the collection, i.e. the first.
     * Its value is {@code VarHeuristic.LEFT_MOST}.
     *
     */
    private final static VarHeuristic DEFAULT_VARIABLE_CHOSEN_IN_A_LIST = VarHeuristic.LEFT_MOST;

    /**
     * Default value for the public field {@code valueForSetLVar}.
     * Its value is {@code SetHeuristic.FIRST_NIN}.
     */
    private final static SetHeuristic DEFAULT_VALUE_FOR_SETLVAR = SetHeuristic.FIRST_NIN;

    /**
     * Default value for the public field {@code valueForBoolLVar}.
     * Its value is {@code BoolHeuristic.FALSE}.
     */
    private final static BoolHeuristic DEFAULT_VALUE_FOR_BOOLLVAR = BoolHeuristic.FALSE;


    //////////////////////////////////////////////////////
    ////////////// DATA MEMBERS //////////////////////////
    //////////////////////////////////////////////////////

    /**
     * Heuristic to use when giving values to an {@code IntLVar} when labeling.
     */
    public ValHeuristic valueForIntLVar = DEFAULT_VALUE_FOR_INTLVAR;

    /**
     * Heuristic to use when choosing which variable to label first in a list
     */
    public VarHeuristic variableChosenInAList = DEFAULT_VARIABLE_CHOSEN_IN_A_LIST;

    /**
     * Heuristic to use when giving values to a {@code SetLVar} when labeling.
     */
    public SetHeuristic valueForSetLVar = DEFAULT_VALUE_FOR_SETLVAR;

    /**
     * Heuristic to use when giving values to a {@code BoolLVar} when labeling.
     */
    public BoolHeuristic valueForBoolLVar = DEFAULT_VALUE_FOR_BOOLLVAR;


    //////////////////////////////////////////////////////
    ////////////// CONSTRUCTORS //////////////////////////
    //////////////////////////////////////////////////////
    /**
     * Creates a new object of class LabelingOptions with each public field (heuristics) initialized with its default value.
     */
    public 
    LabelingOptions() {
        // DO NOTHING.
    }


    //////////////////////////////////////////////////////
    ////////////// PROTECTED METHODS /////////////////////
    //////////////////////////////////////////////////////

    /**
     * Uses the heuristic contained in {@code valueForBoolLVar} to assign the first value to a {@code BoolLVar}.
     * @return a boolean which is the value to assign.
     * @throws UnsupportedHeuristicException if the heuristic contained in the field {@code valueForBoolLVar} is not
     * {@code FALSE, TRUE, RANDOM}.
     */
    protected @NotNull Boolean
    getBoolValue() {
        switch (valueForBoolLVar) {
            case FALSE:
                return false;
            case TRUE:
                return true;
            case RANDOM:
                Random rnd = new Random();
                int num = rnd.nextInt(2);
                return num == 0;
            default:
                throw new UnsupportedHeuristicException();
        }
    }

    /**
     * Uses the heuristic contained in {@code valueForIntLVar} to assign the a value to a {@code IntLVar}.
     *
     * @param multiInterval multi-interval from which the integer value is get.
     * @return an integer with the value to assign.
     * @throws UnsupportedHeuristicException if the value in the field {@code valueForIntLVAr} is not
     * {@code GLB, LUB, MID_MOST, MEDIAN, RANGE_RANDOM, EQUI_RANDOM, MID_RANDOM}.
     */
    protected @NotNull Integer
    getIntValue(@NotNull MultiInterval multiInterval)
    throws UnsupportedHeuristicException {
        assert multiInterval != null;
        Integer result;

        switch (valueForIntLVar) {
            case GLB:
                result = multiInterval.getGlb();
                break;
            case LUB:
                result = multiInterval.getLub();
                break;
            case MID_MOST:
                result = multiInterval.getMidMostElement();
                break;
            case MEDIAN:
                result = multiInterval.getMedianElement();
                break;
            case RANGE_RANDOM:
                result = multiInterval.getEquiRandomElement();
                break;
            case EQUI_RANDOM:
                result = multiInterval.getEquiRandomElement();
                break;
            case MID_RANDOM:
                result = multiInterval.getMidRandomElement();
                break;
            default:
                throw new UnsupportedHeuristicException();
        }

        assert result != null;
        return result;
    }

    /**
     * Chooses a variable in the provided list using the heuristic stored in {@code variableChosenInAList}
     *
     * @param list list of boolean logical variables from which a boolean logical variable is to be extracted.
     * @return the variable chosen.
     * @throws UnsupportedHeuristicException if the value of the field {@code variableChosenInAList} is not
     * {@code LEFT_MOST, RIGHT_MOST, MID_MOST, RANDOM}.
     */
    protected @NotNull BoolLVar
    getBoolVariable(@NotNull ArrayList<BoolLVar> list)
    throws UnsupportedHeuristicException {
        assert list != null;
        BoolLVar chosen;

        switch (variableChosenInAList) {
            case LEFT_MOST:
                chosen = list.get(0);
                break;
            case RIGHT_MOST:
                chosen = list.get(list.size() - 1);
                break;
            case MID_MOST:
                chosen = list.get((list.size() - 1) / 2);
                break;
            case RANDOM:
                Random rnd = new Random();
                chosen = list.get(rnd.nextInt(list.size()));
                break;
            default:
                throw new UnsupportedHeuristicException();
        }

        assert chosen != null;
        return chosen;
    }

    /**
     * Chooses a variable in the provided list using the heuristic stored in {@code variableChosenInAList}
     *
     * @param list list of integer logical variables from which an integer logical variable is to be extracted.
     * @return the variable chosen.
     * @throws UnsupportedHeuristicException if the value of the field {@code variableChosenInAList} is not
     * {@code LEFT_MOST, MIN, MAX, FIRST_FAIL, RIGHT_MOST, MID_MOST, RANDOM}.
     */
    protected @NotNull IntLVar
    getIntVariable(@NotNull ArrayList<IntLVar> list)
    throws UnsupportedHeuristicException {
        assert list != null;
        IntLVar chosen;

        switch (variableChosenInAList) {
            case LEFT_MOST:
                chosen = list.get(0);
                break;
            case MIN:
                chosen = Collections.min(list, new CmpIntLVarDomByGlb());
                break;
            case MAX:
                chosen = Collections.max(list, new CmpIntLVarDomByLub());
                break;
            case FIRST_FAIL:
                chosen = Collections.min(list, new CmpIntLVarDomBySize());
                break;
            case RIGHT_MOST:
                chosen = list.get(list.size() - 1);
                break;
            case MID_MOST:
                chosen = list.get((list.size() - 1) / 2);
                break;
            case RANDOM:
                Random rnd = new Random();
                chosen = list.get(rnd.nextInt(list.size()));
                break;
            default:
                throw new UnsupportedHeuristicException();
        }

        assert chosen != null;
        return chosen;
    }

    /**
     * Chooses a variable in the provided list using the heuristic stored in {@code variableChosenInAList}.
     *
     * @param list list of set logical variables from which a s logical variable is to be extracted.
     * @return the variable chosen.
     * @throws UnsupportedHeuristicException if the value of the field {@code variableChosenInAList} is not
     * {@code LEFT_MOST, MIN, MAX, FIRST_FAIL, RIGHT_MOST, MID_MOST, RANDOM}.
     */
    protected @NotNull SetLVar
    getSetVariable(@NotNull ArrayList<SetLVar> list)
    throws UnsupportedHeuristicException {
        assert list != null;
        SetLVar chosen;

        switch (variableChosenInAList) {
            case LEFT_MOST:
                chosen = list.get(0);
                break;
            case MIN:
                chosen = Collections.min(list, new CmpFSVarDomByGlb());
                break;
            case MAX:
                chosen = Collections.max(list, new CmpFSVarDomByLub());
                break;
            case FIRST_FAIL:
                chosen = Collections.min(list, new CmpFSVarDomBySize());
                break;
            case RIGHT_MOST:
                chosen = list.get(list.size() - 1);
                break;
            case MID_MOST:
                chosen = list.get((list.size() - 1) / 2);
                break;
            case RANDOM:
                Random rnd = new Random();
                chosen = list.get(rnd.nextInt(list.size()));
                break;
            default:
                throw new UnsupportedHeuristicException();
        }

        assert chosen != null;
        return chosen;
    }
    
}
