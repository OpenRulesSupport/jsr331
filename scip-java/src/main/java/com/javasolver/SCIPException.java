package com.javasolver;

/** Indicates an error reported by the native SCIP library. */
public class SCIPException extends RuntimeException {
    private final int returnCode;

    SCIPException(String operation, int returnCode) {
        super(operation + " failed with SCIP return code " + returnCode);
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
