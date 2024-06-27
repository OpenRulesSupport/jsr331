package scheduler;

///////////////////////////////////////////////////////////////////////////////
/*
 * Copyright Exigen Group 1998, 1999, 2000, 2002
 * 320 Amboy Ave., Metuchen, NJ, 08840, USA, www.exigengroup.com
 *
 * The copyright to the computer program(s) herein
 * is the property of Exigen Group, USA. All rights reserved.
 * The program(s) may be used and/or copied only with
 * the written permission of Exigen Group
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
///////////////////////////////////////////////////////////////////////////////

import com.exigen.ie.constrainer.*;
import com.exigen.ie.scheduler.*;

/**
 */
public final class ThreeOvens
{
    final static int OVENS = 3;

    public static void main(String args[]) throws Exception
    {
        Constrainer C = new Constrainer("TwoOvens Scheduling Example");
        Schedule S = new Schedule(C,0,11);
        S.setName("TwoOvens");

        long executionStart = System.currentTimeMillis();

        Job jA = S.addJob(1, "JobA");
        Job jB = S.addJob(4, "JobB");
        Job jC = S.addJob(4, "JobC");
        Job jD = S.addJob(2, "JobD");
        Job jE = S.addJob(4, "JobE");
        Job jF = S.addJob(1, "JobF");
        Job jG = S.addJob(3, "JobG");
        Job jH = S.addJob(3, "JobH");
        Job jI = S.addJob(2, "JobI");
        Job jJ = S.addJob(1, "JobJ");

        Job jK = S.addJob(3, "JobK");
        Job jL = S.addJob(2, "JobL");
        Job jM = S.addJob(2, "JobM");


       AlternativeResourceSet res = new AlternativeResourceSet();

        Resource oven1 = S.addResourceDiscrete(3,"oven1");
        Resource oven2 = S.addResourceDiscrete(3,"oven2");
        Resource oven3 = S.addResourceDiscrete(3,"oven3");

        res.add(oven1);
        res.add(oven2);
        res.add(oven3);

        oven1.setCapacityMax(0,1,2);
        oven1.setCapacityMax(1,2,1);
        oven1.setCapacityMax(2,3,0);
        oven1.setCapacityMax(3,5,1);
        oven1.setCapacityMax(5,10,3);
        oven1.setCapacityMax(10,11,1);

        oven2.setCapacityMax(0,5,2);
        oven2.setCapacityMax(5,7,1);
        oven2.setCapacityMax(7,8,0);
        oven2.setCapacityMax(8,11,2);

        oven3.setCapacityMax(0,4,0);
        oven3.setCapacityMax(4,6,1);
        oven3.setCapacityMax(6,8,2);
        oven3.setCapacityMax(8,9,1);
        oven3.setCapacityMax(9,11,0);

        C.postConstraint(jA.requires(res,2));
        C.postConstraint(jB.requires(res,1));
        C.postConstraint(jC.requires(res,1));
        C.postConstraint(jD.requires(res,1));
        C.postConstraint(jE.requires(res,2));
        C.postConstraint(jF.requires(res,1));
        C.postConstraint(jG.requires(res,1));
        C.postConstraint(jH.requires(res,2));
        C.postConstraint(jI.requires(res,1));
        C.postConstraint(jJ.requires(res,3));

        C.postConstraint(jK.requires(res,1));
        C.postConstraint(jL.requires(res,1));
        C.postConstraint(jM.requires(res,1));

        Goal solution = new GoalSetTimes(S.jobs());

        long solveStart = System.currentTimeMillis();

        // C.traceExecution();
        // C.traceFailures();
        // C.trace(vars);

        C.printInformation();

        if (!C.execute(solution))
            System.out.println("Can not solve "+solution);
        else
        {
            System.out.println("1st solution:");
            for(int i=0; i < S.jobs().size(); ++i)
            {
                Job job = (Job)S.jobs().elementAt(i);
                System.out.println(job);
            }
        }
        System.out.println(oven1);
        System.out.println(oven2);
        System.out.println(oven3);


        long solveTime = System.currentTimeMillis() - solveStart;
        long executionTime = System.currentTimeMillis() - executionStart;

        System.out.println("Execution time: "+executionTime+" msec");
        System.out.println("Solving time: "+solveTime+" msec");

    }
}
