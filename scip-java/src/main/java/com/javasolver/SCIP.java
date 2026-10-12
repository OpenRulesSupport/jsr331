package com.javasolver;

import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import jnr.ffi.byref.PointerByReference;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A compact Java modeling API backed by the native SCIP solver. */
public final class SCIP implements AutoCloseable {
    public enum Status { OPTIMAL, INFEASIBLE, UNBOUNDED, INFEASIBLE_OR_UNBOUNDED, LIMIT, INTERRUPTED, UNKNOWN }

    private static final int SCIP_OKAY = 1;
    private static final SCIPNative NATIVE = NativeLoader.load();
    private static final Runtime RUNTIME = Runtime.getSystemRuntime();

    private final List<SCIPVariable> variables = new ArrayList<>();
    private final List<SCIPConstraint> constraints = new ArrayList<>();
    private final Map<String, Double> realParameters = new LinkedHashMap<>();
    private final Map<String, Integer> intParameters = new LinkedHashMap<>();
    private final Map<String, Long> longParameters = new LinkedHashMap<>();
    private String name = "model";
    private boolean maximize;
    private boolean verbose;
    private double objectiveOffset;
    private Pointer nativeModel;
    private double[] solution;
    private double objectiveValue = Double.NaN;
    private double primalBound = Double.NaN;
    private double dualBound = Double.NaN;
    private double gap = Double.NaN;
    private long nodes;
    private Status status = Status.UNKNOWN;

    public SCIP name(String value) {
        if (value == null || value.isEmpty()) throw new IllegalArgumentException("Model name cannot be empty");
        name = value;
        changed();
        return this;
    }

    public SCIPVariable addVariable() {
        SCIPVariable variable = new SCIPVariable(this, variables.size());
        variables.add(variable);
        changed();
        return variable;
    }

    public SCIPExpression createExpression() { return new SCIPExpression(this); }

    SCIPConstraint addConstraint(Map<SCIPVariable, Double> terms, double lhs, double rhs) {
        if (terms.isEmpty()) throw new IllegalArgumentException("Constraint must contain at least one variable");
        if (Double.isNaN(lhs) || Double.isNaN(rhs) || lhs > rhs)
            throw new IllegalArgumentException("Invalid constraint bounds");
        SCIPConstraint constraint = new SCIPConstraint("c_" + constraints.size(), terms, lhs, rhs);
        constraints.add(constraint);
        changed();
        return constraint;
    }

    void setObjective(Map<SCIPVariable, Double> terms, double offset) {
        for (SCIPVariable variable : variables) variable.objective = 0.0;
        for (Map.Entry<SCIPVariable, Double> term : terms.entrySet()) term.getKey().objective = term.getValue();
        objectiveOffset = offset;
        changed();
    }

    public SCIP maximization() { maximize = true; changed(); return this; }
    public SCIP minimization() { maximize = false; changed(); return this; }
    public Status maximize() { maximization(); return solve(); }
    public Status minimize() { minimization(); return solve(); }
    public SCIP verbose(boolean value) { verbose = value; changed(); return this; }

    public SCIP timeLimit(double seconds) { return setRealParam("limits/time", seconds); }
    public SCIP relativeGap(double value) { return setRealParam("limits/gap", value); }
    public SCIP absoluteGap(double value) { return setRealParam("limits/absgap", value); }
    public SCIP nodeLimit(long value) { return setLongintParam("limits/nodes", value); }
    public SCIP solutionLimit(int value) { return setIntParam("limits/solutions", value); }
    public SCIP threads(int value) { return setIntParam("parallel/maxnthreads", value); }

    public SCIP setRealParam(String parameter, double value) {
        SCIPVariable.requireFinite(value, "parameter value");
        realParameters.put(parameter, value);
        changed();
        return this;
    }

    public SCIP setIntParam(String parameter, int value) {
        intParameters.put(parameter, value);
        changed();
        return this;
    }

    public SCIP setLongintParam(String parameter, long value) {
        longParameters.put(parameter, value);
        changed();
        return this;
    }

    public Status solve() {
        closeNative();
        PointerByReference modelRef = new PointerByReference();
        check("SCIPcreate", NATIVE.SCIPcreate(modelRef));
        nativeModel = modelRef.getValue();
        try {
            check("SCIPincludeDefaultPlugins", NATIVE.SCIPincludeDefaultPlugins(nativeModel));
            NATIVE.SCIPsetMessagehdlrQuiet(nativeModel, verbose ? 0 : 1);
            check("SCIPcreateProbBasic", NATIVE.SCIPcreateProbBasic(nativeModel, name));
            check("SCIPsetObjsense", NATIVE.SCIPsetObjsense(nativeModel, maximize ? -1 : 1));
            if (objectiveOffset != 0.0)
                check("SCIPaddOrigObjoffset", NATIVE.SCIPaddOrigObjoffset(nativeModel, objectiveOffset));
            applyParameters();

            double infinity = NATIVE.SCIPinfinity(nativeModel);
            List<Pointer> nativeVariables = createVariables(infinity);
            createConstraints(nativeVariables, infinity);
            check("SCIPsolve", NATIVE.SCIPsolve(nativeModel));

            int nativeStatus = NATIVE.SCIPgetStatus(nativeModel);
            status = mapStatus(nativeStatus);
            nodes = NATIVE.SCIPgetNNodes(nativeModel);
            primalBound = NATIVE.SCIPgetPrimalbound(nativeModel);
            dualBound = NATIVE.SCIPgetDualbound(nativeModel);
            gap = NATIVE.SCIPgetGap(nativeModel);
            Pointer best = NATIVE.SCIPgetBestSol(nativeModel);
            if (best != null) {
                solution = new double[variables.size()];
                for (int i = 0; i < solution.length; i++)
                    solution[i] = NATIVE.SCIPgetSolVal(nativeModel, best, nativeVariables.get(i));
                objectiveValue = NATIVE.SCIPgetSolOrigObj(nativeModel, best);
            } else {
                solution = null;
                objectiveValue = Double.NaN;
            }
            return status;
        } catch (RuntimeException error) {
            closeNative();
            throw error;
        }
    }

    private List<Pointer> createVariables(double infinity) {
        List<Pointer> pointers = new ArrayList<>(variables.size());
        for (SCIPVariable variable : variables) {
            PointerByReference ref = new PointerByReference();
            check("SCIPcreateVarBasic", NATIVE.SCIPcreateVarBasic(nativeModel, ref, variable.name,
                    bound(variable.lower, infinity), bound(variable.upper, infinity),
                    variable.objective, nativeType(variable.type)));
            Pointer pointer = ref.getValue();
            check("SCIPaddVar", NATIVE.SCIPaddVar(nativeModel, pointer));
            pointers.add(pointer);
            check("SCIPreleaseVar", NATIVE.SCIPreleaseVar(nativeModel, ref));
        }
        return pointers;
    }

    private void createConstraints(List<Pointer> nativeVariables, double infinity) {
        int addressSize = RUNTIME.addressSize();
        for (SCIPConstraint constraint : constraints) {
            int count = constraint.terms.size();
            Pointer vars = Memory.allocateDirect(RUNTIME, (long) count * addressSize);
            Pointer coefficients = Memory.allocateDirect(RUNTIME, (long) count * Double.BYTES);
            int i = 0;
            for (Map.Entry<SCIPVariable, Double> term : constraint.terms.entrySet()) {
                vars.putPointer((long) i * addressSize, nativeVariables.get(term.getKey().index));
                coefficients.putDouble((long) i * Double.BYTES, term.getValue());
                i++;
            }
            PointerByReference ref = new PointerByReference();
            check("SCIPcreateConsBasicLinear", NATIVE.SCIPcreateConsBasicLinear(nativeModel, ref,
                    constraint.name, count, vars, coefficients,
                    bound(constraint.lhs, infinity), bound(constraint.rhs, infinity)));
            check("SCIPaddCons", NATIVE.SCIPaddCons(nativeModel, ref.getValue()));
            check("SCIPreleaseCons", NATIVE.SCIPreleaseCons(nativeModel, ref));
        }
    }

    private void applyParameters() {
        for (Map.Entry<String, Double> entry : realParameters.entrySet())
            check("SCIPsetRealParam(" + entry.getKey() + ")", NATIVE.SCIPsetRealParam(nativeModel, entry.getKey(), entry.getValue()));
        for (Map.Entry<String, Integer> entry : intParameters.entrySet())
            check("SCIPsetIntParam(" + entry.getKey() + ")", NATIVE.SCIPsetIntParam(nativeModel, entry.getKey(), entry.getValue()));
        for (Map.Entry<String, Long> entry : longParameters.entrySet())
            check("SCIPsetLongintParam(" + entry.getKey() + ")", NATIVE.SCIPsetLongintParam(nativeModel, entry.getKey(), entry.getValue()));
    }

    public void writeProblem(File file) {
        if (nativeModel == null) throw new IllegalStateException("Solve the model before writing it");
        check("SCIPwriteOrigProblem", NATIVE.SCIPwriteOrigProblem(nativeModel, file.getAbsolutePath(), null, 0));
    }

    public double getSolution(SCIPVariable variable) {
        if (variable == null || variable.owner != this) throw new IllegalArgumentException("Variable belongs to another model");
        if (solution == null) throw new IllegalStateException("No feasible solution is available");
        return solution[variable.index];
    }

    public double getObjectiveValue() {
        if (solution == null) throw new IllegalStateException("No feasible solution is available");
        return objectiveValue;
    }

    public Status getStatus() { return status; }
    public double getPrimalBound() { return primalBound; }
    public double getDualBound() { return dualBound; }
    public double getGap() { return gap; }
    public long getNodeCount() { return nodes; }
    public int getNumVariables() { return variables.size(); }
    public int getNumConstraints() { return constraints.size(); }

    void changed() {
        if (nativeModel != null) closeNative();
        solution = null;
        objectiveValue = primalBound = dualBound = gap = Double.NaN;
        nodes = 0;
        status = Status.UNKNOWN;
    }

    @Override public void close() { closeNative(); }

    private void closeNative() {
        if (nativeModel != null) {
            PointerByReference ref = new PointerByReference(nativeModel);
            int code = NATIVE.SCIPfree(ref);
            nativeModel = null;
            if (code != SCIP_OKAY) throw new SCIPException("SCIPfree", code);
        }
    }

    private static void check(String operation, int code) {
        if (code != SCIP_OKAY) throw new SCIPException(operation, code);
    }

    private static double bound(double value, double infinity) {
        if (value == Double.POSITIVE_INFINITY) return infinity;
        if (value == Double.NEGATIVE_INFINITY) return -infinity;
        return value;
    }

    private static int nativeType(SCIPVariable.Type type) {
        if (type == SCIPVariable.Type.BINARY) return 0;
        if (type == SCIPVariable.Type.INTEGER) return 1;
        return 3;
    }

    private static Status mapStatus(int value) {
        switch (value) {
            case 1: return Status.OPTIMAL;
            case 2: return Status.INFEASIBLE;
            case 3: return Status.UNBOUNDED;
            case 4: return Status.INFEASIBLE_OR_UNBOUNDED;
            case 10: case 11: return Status.INTERRUPTED;
            case 20: case 21: case 22: case 23: case 24: case 25:
            case 26: case 27: case 28: case 29: case 30: return Status.LIMIT;
            default: return Status.UNKNOWN;
        }
    }
}
