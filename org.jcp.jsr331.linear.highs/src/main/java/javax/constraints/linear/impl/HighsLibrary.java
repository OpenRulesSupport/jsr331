package javax.constraints.linear.impl;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * Minimal JNA mapping of the HiGHS C API used by the JSR 331 adapter.
 * The bundled native builds use HiGHS's 64-bit {@code HighsInt} option.
 */
interface HighsLibrary extends Library {

    Pointer Highs_create();

    void Highs_destroy(Pointer highs);

    Pointer Highs_version();

    long Highs_passLp(
            Pointer highs, long numCol, long numRow, long numNz, long matrixFormat,
            long sense, double offset, double[] colCost, double[] colLower,
            double[] colUpper, double[] rowLower, double[] rowUpper,
            long[] aStart, long[] aIndex, double[] aValue);

    long Highs_passMip(
            Pointer highs, long numCol, long numRow, long numNz, long matrixFormat,
            long sense, double offset, double[] colCost, double[] colLower,
            double[] colUpper, double[] rowLower, double[] rowUpper,
            long[] aStart, long[] aIndex, double[] aValue, long[] integrality);

    long Highs_readModel(Pointer highs, String filename);

    long Highs_run(Pointer highs);

    long Highs_getModelStatus(Pointer highs);

    long Highs_getIntInfoValue(Pointer highs, String info, long[] value);

    long Highs_getSolution(Pointer highs, double[] colValue, double[] colDual,
            double[] rowValue, double[] rowDual);

    long Highs_getNumCol(Pointer highs);

    long Highs_getColName(Pointer highs, long column, byte[] name);

    long Highs_setBoolOptionValue(Pointer highs, String option, long value);

    long Highs_setDoubleOptionValue(Pointer highs, String option, double value);
}
