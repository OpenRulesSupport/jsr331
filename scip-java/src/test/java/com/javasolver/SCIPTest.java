package com.javasolver;

import org.junit.Test;

import com.javasolver.SCIP;
import com.javasolver.SCIPVariable;

import static org.junit.Assert.*;

public class SCIPTest {
    @Test
    public void solvesBinaryKnapsack() {
        try (SCIP model = new SCIP().name("knapsack")) {
            SCIPVariable x = model.addVariable().binary().obj(6).name("x");
            SCIPVariable y = model.addVariable().binary().obj(5).name("y");
            SCIPVariable z = model.addVariable().binary().obj(4).name("z");
            model.createExpression().add(4, x).add(3, y).add(2, z).leq(5);

            assertSame(SCIP.Status.OPTIMAL, model.maximize());
            assertEquals(9.0, model.getObjectiveValue(), 1e-7);
            assertEquals(0.0, x.getSolution(), 1e-7);
            assertEquals(1.0, y.getSolution(), 1e-7);
            assertEquals(1.0, z.getSolution(), 1e-7);
            assertEquals(0.0, model.getGap(), 1e-12);
        }
    }

    @Test
    public void solvesMixedIntegerModelWithOffset() {
        try (SCIP model = new SCIP()) {
            SCIPVariable x = model.addVariable().integer().obj(1);
            SCIPVariable y = model.addVariable().bounds(0, 10).obj(.25);
            model.createExpression().add(2, x).add(y).geq(3.5);
            model.createExpression().add(7).add(x).add(.25, y).asObjective();

            assertSame(SCIP.Status.OPTIMAL, model.minimize());
            assertEquals(7.875, model.getObjectiveValue(), 1e-7);
            assertEquals(0.0, x.getSolution(), 1e-7);
            assertEquals(3.5, y.getSolution(), 1e-7);
        }
    }

    @Test
    public void provesIntegerInfeasibility() {
        try (SCIP model = new SCIP()) {
            SCIPVariable x = model.addVariable().integer();
            model.createExpression().add(x).eq(.5);
            assertSame(SCIP.Status.INFEASIBLE, model.solve());
        }
    }

    @Test
    public void rejectsVariablesFromAnotherModel() {
        try (SCIP first = new SCIP(); SCIP second = new SCIP()) {
            SCIPVariable foreign = first.addVariable();
            try {
                second.createExpression().add(foreign);
                fail("Expected IllegalArgumentException");
            } catch (IllegalArgumentException expected) {
                assertTrue(expected.getMessage().contains("another model"));
            }
        }
    }
}
