package javax.constraints.linear.impl;

import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/** Minimal JNA mapping of the GLPK C API used by the JSR 331 adapter. */
interface GlpkLibrary extends Library {

    @Structure.FieldOrder({
        "msgLev", "method", "pricing", "ratioTest",
        "boundTolerance", "dualTolerance", "pivotTolerance",
        "objectiveLowerLimit", "objectiveUpperLimit",
        "iterationLimit", "timeLimit", "outputFrequency", "outputDelay",
        "presolve", "excludeFixed", "shiftBounds", "matrixForm", "reserved"
    })
    class SimplexParameters extends Structure {
        public int msgLev;
        public int method;
        public int pricing;
        public int ratioTest;
        public double boundTolerance;
        public double dualTolerance;
        public double pivotTolerance;
        public double objectiveLowerLimit;
        public double objectiveUpperLimit;
        public int iterationLimit;
        public int timeLimit;
        public int outputFrequency;
        public int outputDelay;
        public int presolve;
        public int excludeFixed;
        public int shiftBounds;
        public int matrixForm;
        public double[] reserved = new double[33];
    }

    @Structure.FieldOrder({
        "msgLev", "branching", "backtracking", "integerTolerance",
        "objectiveTolerance", "timeLimit", "outputFrequency", "outputDelay",
        "callback", "callbackInfo", "callbackSize", "preprocessing",
        "mipGap", "mirCuts", "gomoryCuts", "coverCuts", "cliqueCuts",
        "presolve", "binarize", "feasibilityPump", "proximitySearch",
        "proximityTimeLimit", "simpleRounding", "useSolution", "saveSolution",
        "alienSolver", "flip", "reserved"
    })
    class IntegerParameters extends Structure {
        public int msgLev;
        public int branching;
        public int backtracking;
        public double integerTolerance;
        public double objectiveTolerance;
        public int timeLimit;
        public int outputFrequency;
        public int outputDelay;
        public Pointer callback;
        public Pointer callbackInfo;
        public int callbackSize;
        public int preprocessing;
        public double mipGap;
        public int mirCuts;
        public int gomoryCuts;
        public int coverCuts;
        public int cliqueCuts;
        public int presolve;
        public int binarize;
        public int feasibilityPump;
        public int proximitySearch;
        public int proximityTimeLimit;
        public int simpleRounding;
        public int useSolution;
        public Pointer saveSolution;
        public int alienSolver;
        public int flip;
        public double[] reserved = new double[23];
    }

    String glp_version();

    Pointer glp_create_prob();

    void glp_delete_prob(Pointer problem);

    void glp_set_prob_name(Pointer problem, String name);

    void glp_set_obj_dir(Pointer problem, int direction);

    int glp_add_rows(Pointer problem, int count);

    int glp_add_cols(Pointer problem, int count);

    void glp_set_row_name(Pointer problem, int row, String name);

    void glp_set_col_name(Pointer problem, int column, String name);

    void glp_set_row_bnds(Pointer problem, int row, int type, double lower, double upper);

    void glp_set_col_bnds(Pointer problem, int column, int type, double lower, double upper);

    void glp_set_obj_coef(Pointer problem, int column, double coefficient);

    void glp_set_mat_row(
            Pointer problem, int row, int length, int[] indices, double[] values);

    void glp_set_col_kind(Pointer problem, int column, int kind);

    int glp_get_num_cols(Pointer problem);

    int glp_get_num_int(Pointer problem);

    String glp_get_col_name(Pointer problem, int column);

    void glp_init_smcp(SimplexParameters parameters);

    int glp_simplex(Pointer problem, SimplexParameters parameters);

    int glp_get_status(Pointer problem);

    double glp_get_col_prim(Pointer problem, int column);

    void glp_init_iocp(IntegerParameters parameters);

    int glp_intopt(Pointer problem, IntegerParameters parameters);

    int glp_mip_status(Pointer problem);

    double glp_mip_col_val(Pointer problem, int column);

    int glp_read_mps(Pointer problem, int format, Pointer parameters, String filename);
}
