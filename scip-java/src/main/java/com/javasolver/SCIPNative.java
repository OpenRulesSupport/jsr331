package com.javasolver;

import jnr.ffi.Pointer;
import jnr.ffi.byref.PointerByReference;

/** Minimal binding to SCIP's stable public C API. */
public interface SCIPNative {
    int SCIPcreate(PointerByReference scip);
    int SCIPfree(PointerByReference scip);
    int SCIPincludeDefaultPlugins(Pointer scip);
    int SCIPcreateProbBasic(Pointer scip, String name);
    int SCIPsetObjsense(Pointer scip, int objsense);
    int SCIPaddOrigObjoffset(Pointer scip, double value);

    int SCIPcreateVarBasic(Pointer scip, PointerByReference variable, String name,
                           double lower, double upper, double objective, int type);
    int SCIPaddVar(Pointer scip, Pointer variable);
    int SCIPreleaseVar(Pointer scip, PointerByReference variable);

    int SCIPcreateConsBasicLinear(Pointer scip, PointerByReference constraint, String name,
                                  int count, Pointer variables, Pointer coefficients,
                                  double lhs, double rhs);
    int SCIPaddCons(Pointer scip, Pointer constraint);
    int SCIPreleaseCons(Pointer scip, PointerByReference constraint);

    int SCIPsolve(Pointer scip);
    int SCIPgetStatus(Pointer scip);
    Pointer SCIPgetBestSol(Pointer scip);
    double SCIPgetSolVal(Pointer scip, Pointer solution, Pointer variable);
    double SCIPgetSolOrigObj(Pointer scip, Pointer solution);
    double SCIPgetPrimalbound(Pointer scip);
    double SCIPgetDualbound(Pointer scip);
    double SCIPgetGap(Pointer scip);
    long SCIPgetNNodes(Pointer scip);
    double SCIPinfinity(Pointer scip);

    int SCIPsetRealParam(Pointer scip, String name, double value);
    int SCIPsetIntParam(Pointer scip, String name, int value);
    int SCIPsetLongintParam(Pointer scip, String name, long value);
    void SCIPsetMessagehdlrQuiet(Pointer scip, int quiet);
    int SCIPwriteOrigProblem(Pointer scip, String filename, String extension, int genericNames);
}
