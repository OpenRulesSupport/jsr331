package org.jcp.jsr331.samples;

import javax.constraints.*;

public class CoinCombinations {
    
    public static void main(String[] args) {
        
        long startTime = System.currentTimeMillis();
        Problem p = ProblemFactory.newProblem("Coins");

        // Define the coin denominations and their values in cents
        int[] coinDenominations = {1, 2, 5, 10, 20, 50, 100};
        int[] coinValues = {1, 2, 5, 10, 20, 50, 100};

        // Define the target amount in cents (1 Euro = 100 cents)
        int targetAmount = 100;

        // Create an array of variables to represent the number of each coin denomination used
        Var[] coinCounts = new Var[coinDenominations.length];
        for (int i = 0; i < coinDenominations.length; i++) {
            coinCounts[i] = p.variable("Coin_" + coinValues[i], 0, 100);
        }

        // Define the constraint: Total value of coins must be equal to the target amount
        int[] coeffs = new int[coinDenominations.length];
        for (int i = 0; i < coinDenominations.length; i++) {
            coeffs[i] = coinValues[i];
        }
        Var totalValue = p.scalProd(coeffs,coinCounts);
        p.post(totalValue, "=", targetAmount);

        // Find all solutions
        Solution[] solutions = p.getSolver().findAllSolutions();
        for (Solution solution : solutions) {
            solution.log();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + (endTime - startTime +1) + " ms");
    }
}

/*
JSR-331 "Constraint Programming API" Release 2.1.0
JSR-331 Implementation based on Constrainer 5.4.0
Solution #1:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[1]
Solution #2:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[2] Coin_100[0]
Solution #3:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[5] Coin_50[0] Coin_100[0]
Solution #4:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #5:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #6:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #7:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[4] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #8:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #9:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[6] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #10:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[8] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #11:
     Coin_1[0] Coin_2[0] Coin_5[0] Coin_10[10] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #12:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #13:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #14:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #15:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #16:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #17:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #18:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #19:
     Coin_1[0] Coin_2[0] Coin_5[2] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #20:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #21:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #22:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #23:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #24:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #25:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #26:
     Coin_1[0] Coin_2[0] Coin_5[4] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #27:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #28:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #29:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #30:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #31:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #32:
     Coin_1[0] Coin_2[0] Coin_5[6] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #33:
     Coin_1[0] Coin_2[0] Coin_5[8] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #34:
     Coin_1[0] Coin_2[0] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #35:
     Coin_1[0] Coin_2[0] Coin_5[8] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #36:
     Coin_1[0] Coin_2[0] Coin_5[8] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #37:
     Coin_1[0] Coin_2[0] Coin_5[8] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #38:
     Coin_1[0] Coin_2[0] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #39:
     Coin_1[0] Coin_2[0] Coin_5[10] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #40:
     Coin_1[0] Coin_2[0] Coin_5[10] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #41:
     Coin_1[0] Coin_2[0] Coin_5[10] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #42:
     Coin_1[0] Coin_2[0] Coin_5[12] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #43:
     Coin_1[0] Coin_2[0] Coin_5[12] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #44:
     Coin_1[0] Coin_2[0] Coin_5[12] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #45:
     Coin_1[0] Coin_2[0] Coin_5[14] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #46:
     Coin_1[0] Coin_2[0] Coin_5[14] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #47:
     Coin_1[0] Coin_2[0] Coin_5[16] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #48:
     Coin_1[0] Coin_2[0] Coin_5[16] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #49:
     Coin_1[0] Coin_2[0] Coin_5[18] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #50:
     Coin_1[0] Coin_2[0] Coin_5[20] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #51:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #52:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #53:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #54:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #55:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #56:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #57:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #58:
     Coin_1[0] Coin_2[5] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #59:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #60:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #61:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #62:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #63:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #64:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #65:
     Coin_1[0] Coin_2[5] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #66:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #67:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #68:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #69:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #70:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #71:
     Coin_1[0] Coin_2[5] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #72:
     Coin_1[0] Coin_2[5] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #73:
     Coin_1[0] Coin_2[5] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #74:
     Coin_1[0] Coin_2[5] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #75:
     Coin_1[0] Coin_2[5] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #76:
     Coin_1[0] Coin_2[5] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #77:
     Coin_1[0] Coin_2[5] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #78:
     Coin_1[0] Coin_2[5] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #79:
     Coin_1[0] Coin_2[5] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #80:
     Coin_1[0] Coin_2[5] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #81:
     Coin_1[0] Coin_2[5] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #82:
     Coin_1[0] Coin_2[5] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #83:
     Coin_1[0] Coin_2[5] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #84:
     Coin_1[0] Coin_2[5] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #85:
     Coin_1[0] Coin_2[5] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #86:
     Coin_1[0] Coin_2[5] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #87:
     Coin_1[0] Coin_2[5] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #88:
     Coin_1[0] Coin_2[5] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #89:
     Coin_1[0] Coin_2[5] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #90:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #91:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #92:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #93:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #94:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #95:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #96:
     Coin_1[0] Coin_2[10] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #97:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #98:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #99:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #100:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #101:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #102:
     Coin_1[0] Coin_2[10] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #103:
     Coin_1[0] Coin_2[10] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #104:
     Coin_1[0] Coin_2[10] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #105:
     Coin_1[0] Coin_2[10] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #106:
     Coin_1[0] Coin_2[10] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #107:
     Coin_1[0] Coin_2[10] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #108:
     Coin_1[0] Coin_2[10] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #109:
     Coin_1[0] Coin_2[10] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #110:
     Coin_1[0] Coin_2[10] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #111:
     Coin_1[0] Coin_2[10] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #112:
     Coin_1[0] Coin_2[10] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #113:
     Coin_1[0] Coin_2[10] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #114:
     Coin_1[0] Coin_2[10] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #115:
     Coin_1[0] Coin_2[10] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #116:
     Coin_1[0] Coin_2[10] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #117:
     Coin_1[0] Coin_2[10] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #118:
     Coin_1[0] Coin_2[10] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #119:
     Coin_1[0] Coin_2[10] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #120:
     Coin_1[0] Coin_2[10] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #121:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #122:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #123:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #124:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #125:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #126:
     Coin_1[0] Coin_2[15] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #127:
     Coin_1[0] Coin_2[15] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #128:
     Coin_1[0] Coin_2[15] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #129:
     Coin_1[0] Coin_2[15] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #130:
     Coin_1[0] Coin_2[15] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #131:
     Coin_1[0] Coin_2[15] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #132:
     Coin_1[0] Coin_2[15] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #133:
     Coin_1[0] Coin_2[15] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #134:
     Coin_1[0] Coin_2[15] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #135:
     Coin_1[0] Coin_2[15] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #136:
     Coin_1[0] Coin_2[15] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #137:
     Coin_1[0] Coin_2[15] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #138:
     Coin_1[0] Coin_2[15] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #139:
     Coin_1[0] Coin_2[15] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #140:
     Coin_1[0] Coin_2[15] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #141:
     Coin_1[0] Coin_2[15] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #142:
     Coin_1[0] Coin_2[15] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #143:
     Coin_1[0] Coin_2[15] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #144:
     Coin_1[0] Coin_2[15] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #145:
     Coin_1[0] Coin_2[20] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #146:
     Coin_1[0] Coin_2[20] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #147:
     Coin_1[0] Coin_2[20] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #148:
     Coin_1[0] Coin_2[20] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #149:
     Coin_1[0] Coin_2[20] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #150:
     Coin_1[0] Coin_2[20] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #151:
     Coin_1[0] Coin_2[20] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #152:
     Coin_1[0] Coin_2[20] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #153:
     Coin_1[0] Coin_2[20] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #154:
     Coin_1[0] Coin_2[20] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #155:
     Coin_1[0] Coin_2[20] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #156:
     Coin_1[0] Coin_2[20] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #157:
     Coin_1[0] Coin_2[20] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #158:
     Coin_1[0] Coin_2[20] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #159:
     Coin_1[0] Coin_2[20] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #160:
     Coin_1[0] Coin_2[20] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #161:
     Coin_1[0] Coin_2[20] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #162:
     Coin_1[0] Coin_2[20] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #163:
     Coin_1[0] Coin_2[25] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #164:
     Coin_1[0] Coin_2[25] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #165:
     Coin_1[0] Coin_2[25] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #166:
     Coin_1[0] Coin_2[25] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #167:
     Coin_1[0] Coin_2[25] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #168:
     Coin_1[0] Coin_2[25] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #169:
     Coin_1[0] Coin_2[25] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #170:
     Coin_1[0] Coin_2[25] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #171:
     Coin_1[0] Coin_2[25] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #172:
     Coin_1[0] Coin_2[25] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #173:
     Coin_1[0] Coin_2[25] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #174:
     Coin_1[0] Coin_2[25] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #175:
     Coin_1[0] Coin_2[25] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #176:
     Coin_1[0] Coin_2[30] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #177:
     Coin_1[0] Coin_2[30] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #178:
     Coin_1[0] Coin_2[30] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #179:
     Coin_1[0] Coin_2[30] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #180:
     Coin_1[0] Coin_2[30] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #181:
     Coin_1[0] Coin_2[30] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #182:
     Coin_1[0] Coin_2[30] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #183:
     Coin_1[0] Coin_2[30] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #184:
     Coin_1[0] Coin_2[30] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #185:
     Coin_1[0] Coin_2[35] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #186:
     Coin_1[0] Coin_2[35] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #187:
     Coin_1[0] Coin_2[35] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #188:
     Coin_1[0] Coin_2[35] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #189:
     Coin_1[0] Coin_2[35] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #190:
     Coin_1[0] Coin_2[35] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #191:
     Coin_1[0] Coin_2[40] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #192:
     Coin_1[0] Coin_2[40] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #193:
     Coin_1[0] Coin_2[40] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #194:
     Coin_1[0] Coin_2[40] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #195:
     Coin_1[0] Coin_2[45] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #196:
     Coin_1[0] Coin_2[45] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #197:
     Coin_1[0] Coin_2[50] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #198:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #199:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #200:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #201:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #202:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #203:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #204:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #205:
     Coin_1[1] Coin_2[2] Coin_5[1] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #206:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #207:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #208:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #209:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #210:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #211:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #212:
     Coin_1[1] Coin_2[2] Coin_5[3] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #213:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #214:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #215:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #216:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #217:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #218:
     Coin_1[1] Coin_2[2] Coin_5[5] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #219:
     Coin_1[1] Coin_2[2] Coin_5[7] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #220:
     Coin_1[1] Coin_2[2] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #221:
     Coin_1[1] Coin_2[2] Coin_5[7] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #222:
     Coin_1[1] Coin_2[2] Coin_5[7] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #223:
     Coin_1[1] Coin_2[2] Coin_5[7] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #224:
     Coin_1[1] Coin_2[2] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #225:
     Coin_1[1] Coin_2[2] Coin_5[9] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #226:
     Coin_1[1] Coin_2[2] Coin_5[9] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #227:
     Coin_1[1] Coin_2[2] Coin_5[9] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #228:
     Coin_1[1] Coin_2[2] Coin_5[11] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #229:
     Coin_1[1] Coin_2[2] Coin_5[11] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #230:
     Coin_1[1] Coin_2[2] Coin_5[11] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #231:
     Coin_1[1] Coin_2[2] Coin_5[13] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #232:
     Coin_1[1] Coin_2[2] Coin_5[13] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #233:
     Coin_1[1] Coin_2[2] Coin_5[15] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #234:
     Coin_1[1] Coin_2[2] Coin_5[15] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #235:
     Coin_1[1] Coin_2[2] Coin_5[17] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #236:
     Coin_1[1] Coin_2[2] Coin_5[19] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #237:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #238:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #239:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #240:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #241:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #242:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #243:
     Coin_1[1] Coin_2[7] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #244:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #245:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #246:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #247:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #248:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #249:
     Coin_1[1] Coin_2[7] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #250:
     Coin_1[1] Coin_2[7] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #251:
     Coin_1[1] Coin_2[7] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #252:
     Coin_1[1] Coin_2[7] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #253:
     Coin_1[1] Coin_2[7] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #254:
     Coin_1[1] Coin_2[7] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #255:
     Coin_1[1] Coin_2[7] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #256:
     Coin_1[1] Coin_2[7] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #257:
     Coin_1[1] Coin_2[7] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #258:
     Coin_1[1] Coin_2[7] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #259:
     Coin_1[1] Coin_2[7] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #260:
     Coin_1[1] Coin_2[7] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #261:
     Coin_1[1] Coin_2[7] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #262:
     Coin_1[1] Coin_2[7] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #263:
     Coin_1[1] Coin_2[7] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #264:
     Coin_1[1] Coin_2[7] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #265:
     Coin_1[1] Coin_2[7] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #266:
     Coin_1[1] Coin_2[7] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #267:
     Coin_1[1] Coin_2[7] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #268:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #269:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #270:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #271:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #272:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #273:
     Coin_1[1] Coin_2[12] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #274:
     Coin_1[1] Coin_2[12] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #275:
     Coin_1[1] Coin_2[12] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #276:
     Coin_1[1] Coin_2[12] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #277:
     Coin_1[1] Coin_2[12] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #278:
     Coin_1[1] Coin_2[12] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #279:
     Coin_1[1] Coin_2[12] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #280:
     Coin_1[1] Coin_2[12] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #281:
     Coin_1[1] Coin_2[12] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #282:
     Coin_1[1] Coin_2[12] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #283:
     Coin_1[1] Coin_2[12] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #284:
     Coin_1[1] Coin_2[12] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #285:
     Coin_1[1] Coin_2[12] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #286:
     Coin_1[1] Coin_2[12] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #287:
     Coin_1[1] Coin_2[12] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #288:
     Coin_1[1] Coin_2[12] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #289:
     Coin_1[1] Coin_2[12] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #290:
     Coin_1[1] Coin_2[12] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #291:
     Coin_1[1] Coin_2[12] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #292:
     Coin_1[1] Coin_2[17] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #293:
     Coin_1[1] Coin_2[17] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #294:
     Coin_1[1] Coin_2[17] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #295:
     Coin_1[1] Coin_2[17] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #296:
     Coin_1[1] Coin_2[17] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #297:
     Coin_1[1] Coin_2[17] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #298:
     Coin_1[1] Coin_2[17] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #299:
     Coin_1[1] Coin_2[17] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #300:
     Coin_1[1] Coin_2[17] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #301:
     Coin_1[1] Coin_2[17] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #302:
     Coin_1[1] Coin_2[17] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #303:
     Coin_1[1] Coin_2[17] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #304:
     Coin_1[1] Coin_2[17] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #305:
     Coin_1[1] Coin_2[17] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #306:
     Coin_1[1] Coin_2[17] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #307:
     Coin_1[1] Coin_2[17] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #308:
     Coin_1[1] Coin_2[17] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #309:
     Coin_1[1] Coin_2[17] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #310:
     Coin_1[1] Coin_2[22] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #311:
     Coin_1[1] Coin_2[22] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #312:
     Coin_1[1] Coin_2[22] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #313:
     Coin_1[1] Coin_2[22] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #314:
     Coin_1[1] Coin_2[22] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #315:
     Coin_1[1] Coin_2[22] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #316:
     Coin_1[1] Coin_2[22] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #317:
     Coin_1[1] Coin_2[22] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #318:
     Coin_1[1] Coin_2[22] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #319:
     Coin_1[1] Coin_2[22] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #320:
     Coin_1[1] Coin_2[22] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #321:
     Coin_1[1] Coin_2[22] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #322:
     Coin_1[1] Coin_2[22] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #323:
     Coin_1[1] Coin_2[27] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #324:
     Coin_1[1] Coin_2[27] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #325:
     Coin_1[1] Coin_2[27] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #326:
     Coin_1[1] Coin_2[27] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #327:
     Coin_1[1] Coin_2[27] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #328:
     Coin_1[1] Coin_2[27] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #329:
     Coin_1[1] Coin_2[27] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #330:
     Coin_1[1] Coin_2[27] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #331:
     Coin_1[1] Coin_2[27] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #332:
     Coin_1[1] Coin_2[32] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #333:
     Coin_1[1] Coin_2[32] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #334:
     Coin_1[1] Coin_2[32] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #335:
     Coin_1[1] Coin_2[32] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #336:
     Coin_1[1] Coin_2[32] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #337:
     Coin_1[1] Coin_2[32] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #338:
     Coin_1[1] Coin_2[37] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #339:
     Coin_1[1] Coin_2[37] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #340:
     Coin_1[1] Coin_2[37] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #341:
     Coin_1[1] Coin_2[37] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #342:
     Coin_1[1] Coin_2[42] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #343:
     Coin_1[1] Coin_2[42] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #344:
     Coin_1[1] Coin_2[47] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #345:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #346:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #347:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #348:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #349:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #350:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #351:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #352:
     Coin_1[2] Coin_2[4] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #353:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #354:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #355:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #356:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #357:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #358:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #359:
     Coin_1[2] Coin_2[4] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #360:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #361:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #362:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #363:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #364:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #365:
     Coin_1[2] Coin_2[4] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #366:
     Coin_1[2] Coin_2[4] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #367:
     Coin_1[2] Coin_2[4] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #368:
     Coin_1[2] Coin_2[4] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #369:
     Coin_1[2] Coin_2[4] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #370:
     Coin_1[2] Coin_2[4] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #371:
     Coin_1[2] Coin_2[4] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #372:
     Coin_1[2] Coin_2[4] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #373:
     Coin_1[2] Coin_2[4] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #374:
     Coin_1[2] Coin_2[4] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #375:
     Coin_1[2] Coin_2[4] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #376:
     Coin_1[2] Coin_2[4] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #377:
     Coin_1[2] Coin_2[4] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #378:
     Coin_1[2] Coin_2[4] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #379:
     Coin_1[2] Coin_2[4] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #380:
     Coin_1[2] Coin_2[4] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #381:
     Coin_1[2] Coin_2[4] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #382:
     Coin_1[2] Coin_2[4] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #383:
     Coin_1[2] Coin_2[4] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #384:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #385:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #386:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #387:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #388:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #389:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #390:
     Coin_1[2] Coin_2[9] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #391:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #392:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #393:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #394:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #395:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #396:
     Coin_1[2] Coin_2[9] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #397:
     Coin_1[2] Coin_2[9] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #398:
     Coin_1[2] Coin_2[9] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #399:
     Coin_1[2] Coin_2[9] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #400:
     Coin_1[2] Coin_2[9] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #401:
     Coin_1[2] Coin_2[9] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #402:
     Coin_1[2] Coin_2[9] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #403:
     Coin_1[2] Coin_2[9] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #404:
     Coin_1[2] Coin_2[9] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #405:
     Coin_1[2] Coin_2[9] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #406:
     Coin_1[2] Coin_2[9] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #407:
     Coin_1[2] Coin_2[9] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #408:
     Coin_1[2] Coin_2[9] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #409:
     Coin_1[2] Coin_2[9] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #410:
     Coin_1[2] Coin_2[9] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #411:
     Coin_1[2] Coin_2[9] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #412:
     Coin_1[2] Coin_2[9] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #413:
     Coin_1[2] Coin_2[9] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #414:
     Coin_1[2] Coin_2[9] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #415:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #416:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #417:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #418:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #419:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #420:
     Coin_1[2] Coin_2[14] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #421:
     Coin_1[2] Coin_2[14] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #422:
     Coin_1[2] Coin_2[14] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #423:
     Coin_1[2] Coin_2[14] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #424:
     Coin_1[2] Coin_2[14] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #425:
     Coin_1[2] Coin_2[14] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #426:
     Coin_1[2] Coin_2[14] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #427:
     Coin_1[2] Coin_2[14] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #428:
     Coin_1[2] Coin_2[14] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #429:
     Coin_1[2] Coin_2[14] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #430:
     Coin_1[2] Coin_2[14] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #431:
     Coin_1[2] Coin_2[14] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #432:
     Coin_1[2] Coin_2[14] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #433:
     Coin_1[2] Coin_2[14] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #434:
     Coin_1[2] Coin_2[14] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #435:
     Coin_1[2] Coin_2[14] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #436:
     Coin_1[2] Coin_2[14] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #437:
     Coin_1[2] Coin_2[14] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #438:
     Coin_1[2] Coin_2[14] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #439:
     Coin_1[2] Coin_2[19] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #440:
     Coin_1[2] Coin_2[19] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #441:
     Coin_1[2] Coin_2[19] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #442:
     Coin_1[2] Coin_2[19] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #443:
     Coin_1[2] Coin_2[19] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #444:
     Coin_1[2] Coin_2[19] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #445:
     Coin_1[2] Coin_2[19] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #446:
     Coin_1[2] Coin_2[19] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #447:
     Coin_1[2] Coin_2[19] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #448:
     Coin_1[2] Coin_2[19] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #449:
     Coin_1[2] Coin_2[19] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #450:
     Coin_1[2] Coin_2[19] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #451:
     Coin_1[2] Coin_2[19] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #452:
     Coin_1[2] Coin_2[19] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #453:
     Coin_1[2] Coin_2[19] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #454:
     Coin_1[2] Coin_2[19] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #455:
     Coin_1[2] Coin_2[19] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #456:
     Coin_1[2] Coin_2[19] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #457:
     Coin_1[2] Coin_2[24] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #458:
     Coin_1[2] Coin_2[24] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #459:
     Coin_1[2] Coin_2[24] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #460:
     Coin_1[2] Coin_2[24] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #461:
     Coin_1[2] Coin_2[24] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #462:
     Coin_1[2] Coin_2[24] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #463:
     Coin_1[2] Coin_2[24] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #464:
     Coin_1[2] Coin_2[24] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #465:
     Coin_1[2] Coin_2[24] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #466:
     Coin_1[2] Coin_2[24] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #467:
     Coin_1[2] Coin_2[24] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #468:
     Coin_1[2] Coin_2[24] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #469:
     Coin_1[2] Coin_2[24] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #470:
     Coin_1[2] Coin_2[29] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #471:
     Coin_1[2] Coin_2[29] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #472:
     Coin_1[2] Coin_2[29] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #473:
     Coin_1[2] Coin_2[29] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #474:
     Coin_1[2] Coin_2[29] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #475:
     Coin_1[2] Coin_2[29] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #476:
     Coin_1[2] Coin_2[29] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #477:
     Coin_1[2] Coin_2[29] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #478:
     Coin_1[2] Coin_2[29] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #479:
     Coin_1[2] Coin_2[34] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #480:
     Coin_1[2] Coin_2[34] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #481:
     Coin_1[2] Coin_2[34] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #482:
     Coin_1[2] Coin_2[34] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #483:
     Coin_1[2] Coin_2[34] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #484:
     Coin_1[2] Coin_2[34] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #485:
     Coin_1[2] Coin_2[39] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #486:
     Coin_1[2] Coin_2[39] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #487:
     Coin_1[2] Coin_2[39] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #488:
     Coin_1[2] Coin_2[39] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #489:
     Coin_1[2] Coin_2[44] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #490:
     Coin_1[2] Coin_2[44] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #491:
     Coin_1[2] Coin_2[49] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #492:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #493:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #494:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #495:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #496:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #497:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #498:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #499:
     Coin_1[3] Coin_2[1] Coin_5[1] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #500:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #501:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #502:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #503:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #504:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #505:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #506:
     Coin_1[3] Coin_2[1] Coin_5[3] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #507:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #508:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #509:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #510:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #511:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #512:
     Coin_1[3] Coin_2[1] Coin_5[5] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #513:
     Coin_1[3] Coin_2[1] Coin_5[7] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #514:
     Coin_1[3] Coin_2[1] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #515:
     Coin_1[3] Coin_2[1] Coin_5[7] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #516:
     Coin_1[3] Coin_2[1] Coin_5[7] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #517:
     Coin_1[3] Coin_2[1] Coin_5[7] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #518:
     Coin_1[3] Coin_2[1] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #519:
     Coin_1[3] Coin_2[1] Coin_5[9] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #520:
     Coin_1[3] Coin_2[1] Coin_5[9] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #521:
     Coin_1[3] Coin_2[1] Coin_5[9] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #522:
     Coin_1[3] Coin_2[1] Coin_5[11] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #523:
     Coin_1[3] Coin_2[1] Coin_5[11] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #524:
     Coin_1[3] Coin_2[1] Coin_5[11] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #525:
     Coin_1[3] Coin_2[1] Coin_5[13] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #526:
     Coin_1[3] Coin_2[1] Coin_5[13] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #527:
     Coin_1[3] Coin_2[1] Coin_5[15] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #528:
     Coin_1[3] Coin_2[1] Coin_5[15] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #529:
     Coin_1[3] Coin_2[1] Coin_5[17] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #530:
     Coin_1[3] Coin_2[1] Coin_5[19] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #531:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #532:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #533:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #534:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #535:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #536:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #537:
     Coin_1[3] Coin_2[6] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #538:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #539:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #540:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #541:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #542:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #543:
     Coin_1[3] Coin_2[6] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #544:
     Coin_1[3] Coin_2[6] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #545:
     Coin_1[3] Coin_2[6] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #546:
     Coin_1[3] Coin_2[6] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #547:
     Coin_1[3] Coin_2[6] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #548:
     Coin_1[3] Coin_2[6] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #549:
     Coin_1[3] Coin_2[6] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #550:
     Coin_1[3] Coin_2[6] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #551:
     Coin_1[3] Coin_2[6] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #552:
     Coin_1[3] Coin_2[6] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #553:
     Coin_1[3] Coin_2[6] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #554:
     Coin_1[3] Coin_2[6] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #555:
     Coin_1[3] Coin_2[6] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #556:
     Coin_1[3] Coin_2[6] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #557:
     Coin_1[3] Coin_2[6] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #558:
     Coin_1[3] Coin_2[6] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #559:
     Coin_1[3] Coin_2[6] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #560:
     Coin_1[3] Coin_2[6] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #561:
     Coin_1[3] Coin_2[6] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #562:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #563:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #564:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #565:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #566:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #567:
     Coin_1[3] Coin_2[11] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #568:
     Coin_1[3] Coin_2[11] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #569:
     Coin_1[3] Coin_2[11] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #570:
     Coin_1[3] Coin_2[11] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #571:
     Coin_1[3] Coin_2[11] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #572:
     Coin_1[3] Coin_2[11] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #573:
     Coin_1[3] Coin_2[11] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #574:
     Coin_1[3] Coin_2[11] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #575:
     Coin_1[3] Coin_2[11] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #576:
     Coin_1[3] Coin_2[11] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #577:
     Coin_1[3] Coin_2[11] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #578:
     Coin_1[3] Coin_2[11] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #579:
     Coin_1[3] Coin_2[11] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #580:
     Coin_1[3] Coin_2[11] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #581:
     Coin_1[3] Coin_2[11] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #582:
     Coin_1[3] Coin_2[11] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #583:
     Coin_1[3] Coin_2[11] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #584:
     Coin_1[3] Coin_2[11] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #585:
     Coin_1[3] Coin_2[11] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #586:
     Coin_1[3] Coin_2[16] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #587:
     Coin_1[3] Coin_2[16] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #588:
     Coin_1[3] Coin_2[16] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #589:
     Coin_1[3] Coin_2[16] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #590:
     Coin_1[3] Coin_2[16] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #591:
     Coin_1[3] Coin_2[16] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #592:
     Coin_1[3] Coin_2[16] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #593:
     Coin_1[3] Coin_2[16] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #594:
     Coin_1[3] Coin_2[16] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #595:
     Coin_1[3] Coin_2[16] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #596:
     Coin_1[3] Coin_2[16] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #597:
     Coin_1[3] Coin_2[16] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #598:
     Coin_1[3] Coin_2[16] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #599:
     Coin_1[3] Coin_2[16] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #600:
     Coin_1[3] Coin_2[16] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #601:
     Coin_1[3] Coin_2[16] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #602:
     Coin_1[3] Coin_2[16] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #603:
     Coin_1[3] Coin_2[16] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #604:
     Coin_1[3] Coin_2[21] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #605:
     Coin_1[3] Coin_2[21] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #606:
     Coin_1[3] Coin_2[21] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #607:
     Coin_1[3] Coin_2[21] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #608:
     Coin_1[3] Coin_2[21] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #609:
     Coin_1[3] Coin_2[21] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #610:
     Coin_1[3] Coin_2[21] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #611:
     Coin_1[3] Coin_2[21] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #612:
     Coin_1[3] Coin_2[21] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #613:
     Coin_1[3] Coin_2[21] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #614:
     Coin_1[3] Coin_2[21] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #615:
     Coin_1[3] Coin_2[21] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #616:
     Coin_1[3] Coin_2[21] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #617:
     Coin_1[3] Coin_2[26] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #618:
     Coin_1[3] Coin_2[26] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #619:
     Coin_1[3] Coin_2[26] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #620:
     Coin_1[3] Coin_2[26] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #621:
     Coin_1[3] Coin_2[26] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #622:
     Coin_1[3] Coin_2[26] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #623:
     Coin_1[3] Coin_2[26] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #624:
     Coin_1[3] Coin_2[26] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #625:
     Coin_1[3] Coin_2[26] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #626:
     Coin_1[3] Coin_2[31] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #627:
     Coin_1[3] Coin_2[31] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #628:
     Coin_1[3] Coin_2[31] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #629:
     Coin_1[3] Coin_2[31] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #630:
     Coin_1[3] Coin_2[31] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #631:
     Coin_1[3] Coin_2[31] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #632:
     Coin_1[3] Coin_2[36] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #633:
     Coin_1[3] Coin_2[36] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #634:
     Coin_1[3] Coin_2[36] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #635:
     Coin_1[3] Coin_2[36] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #636:
     Coin_1[3] Coin_2[41] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #637:
     Coin_1[3] Coin_2[41] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #638:
     Coin_1[3] Coin_2[46] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #639:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #640:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #641:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #642:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #643:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #644:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #645:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #646:
     Coin_1[4] Coin_2[3] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #647:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #648:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #649:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #650:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #651:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #652:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #653:
     Coin_1[4] Coin_2[3] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #654:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #655:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #656:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #657:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #658:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #659:
     Coin_1[4] Coin_2[3] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #660:
     Coin_1[4] Coin_2[3] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #661:
     Coin_1[4] Coin_2[3] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #662:
     Coin_1[4] Coin_2[3] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #663:
     Coin_1[4] Coin_2[3] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #664:
     Coin_1[4] Coin_2[3] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #665:
     Coin_1[4] Coin_2[3] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #666:
     Coin_1[4] Coin_2[3] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #667:
     Coin_1[4] Coin_2[3] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #668:
     Coin_1[4] Coin_2[3] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #669:
     Coin_1[4] Coin_2[3] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #670:
     Coin_1[4] Coin_2[3] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #671:
     Coin_1[4] Coin_2[3] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #672:
     Coin_1[4] Coin_2[3] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #673:
     Coin_1[4] Coin_2[3] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #674:
     Coin_1[4] Coin_2[3] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #675:
     Coin_1[4] Coin_2[3] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #676:
     Coin_1[4] Coin_2[3] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #677:
     Coin_1[4] Coin_2[3] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #678:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #679:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #680:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #681:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #682:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #683:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #684:
     Coin_1[4] Coin_2[8] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #685:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #686:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #687:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #688:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #689:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #690:
     Coin_1[4] Coin_2[8] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #691:
     Coin_1[4] Coin_2[8] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #692:
     Coin_1[4] Coin_2[8] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #693:
     Coin_1[4] Coin_2[8] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #694:
     Coin_1[4] Coin_2[8] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #695:
     Coin_1[4] Coin_2[8] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #696:
     Coin_1[4] Coin_2[8] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #697:
     Coin_1[4] Coin_2[8] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #698:
     Coin_1[4] Coin_2[8] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #699:
     Coin_1[4] Coin_2[8] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #700:
     Coin_1[4] Coin_2[8] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #701:
     Coin_1[4] Coin_2[8] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #702:
     Coin_1[4] Coin_2[8] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #703:
     Coin_1[4] Coin_2[8] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #704:
     Coin_1[4] Coin_2[8] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #705:
     Coin_1[4] Coin_2[8] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #706:
     Coin_1[4] Coin_2[8] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #707:
     Coin_1[4] Coin_2[8] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #708:
     Coin_1[4] Coin_2[8] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #709:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #710:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #711:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #712:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #713:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #714:
     Coin_1[4] Coin_2[13] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #715:
     Coin_1[4] Coin_2[13] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #716:
     Coin_1[4] Coin_2[13] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #717:
     Coin_1[4] Coin_2[13] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #718:
     Coin_1[4] Coin_2[13] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #719:
     Coin_1[4] Coin_2[13] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #720:
     Coin_1[4] Coin_2[13] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #721:
     Coin_1[4] Coin_2[13] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #722:
     Coin_1[4] Coin_2[13] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #723:
     Coin_1[4] Coin_2[13] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #724:
     Coin_1[4] Coin_2[13] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #725:
     Coin_1[4] Coin_2[13] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #726:
     Coin_1[4] Coin_2[13] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #727:
     Coin_1[4] Coin_2[13] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #728:
     Coin_1[4] Coin_2[13] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #729:
     Coin_1[4] Coin_2[13] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #730:
     Coin_1[4] Coin_2[13] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #731:
     Coin_1[4] Coin_2[13] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #732:
     Coin_1[4] Coin_2[13] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #733:
     Coin_1[4] Coin_2[18] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #734:
     Coin_1[4] Coin_2[18] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #735:
     Coin_1[4] Coin_2[18] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #736:
     Coin_1[4] Coin_2[18] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #737:
     Coin_1[4] Coin_2[18] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #738:
     Coin_1[4] Coin_2[18] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #739:
     Coin_1[4] Coin_2[18] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #740:
     Coin_1[4] Coin_2[18] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #741:
     Coin_1[4] Coin_2[18] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #742:
     Coin_1[4] Coin_2[18] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #743:
     Coin_1[4] Coin_2[18] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #744:
     Coin_1[4] Coin_2[18] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #745:
     Coin_1[4] Coin_2[18] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #746:
     Coin_1[4] Coin_2[18] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #747:
     Coin_1[4] Coin_2[18] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #748:
     Coin_1[4] Coin_2[18] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #749:
     Coin_1[4] Coin_2[18] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #750:
     Coin_1[4] Coin_2[18] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #751:
     Coin_1[4] Coin_2[23] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #752:
     Coin_1[4] Coin_2[23] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #753:
     Coin_1[4] Coin_2[23] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #754:
     Coin_1[4] Coin_2[23] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #755:
     Coin_1[4] Coin_2[23] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #756:
     Coin_1[4] Coin_2[23] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #757:
     Coin_1[4] Coin_2[23] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #758:
     Coin_1[4] Coin_2[23] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #759:
     Coin_1[4] Coin_2[23] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #760:
     Coin_1[4] Coin_2[23] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #761:
     Coin_1[4] Coin_2[23] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #762:
     Coin_1[4] Coin_2[23] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #763:
     Coin_1[4] Coin_2[23] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #764:
     Coin_1[4] Coin_2[28] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #765:
     Coin_1[4] Coin_2[28] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #766:
     Coin_1[4] Coin_2[28] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #767:
     Coin_1[4] Coin_2[28] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #768:
     Coin_1[4] Coin_2[28] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #769:
     Coin_1[4] Coin_2[28] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #770:
     Coin_1[4] Coin_2[28] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #771:
     Coin_1[4] Coin_2[28] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #772:
     Coin_1[4] Coin_2[28] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #773:
     Coin_1[4] Coin_2[33] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #774:
     Coin_1[4] Coin_2[33] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #775:
     Coin_1[4] Coin_2[33] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #776:
     Coin_1[4] Coin_2[33] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #777:
     Coin_1[4] Coin_2[33] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #778:
     Coin_1[4] Coin_2[33] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #779:
     Coin_1[4] Coin_2[38] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #780:
     Coin_1[4] Coin_2[38] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #781:
     Coin_1[4] Coin_2[38] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #782:
     Coin_1[4] Coin_2[38] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #783:
     Coin_1[4] Coin_2[43] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #784:
     Coin_1[4] Coin_2[43] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #785:
     Coin_1[4] Coin_2[48] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #786:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #787:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #788:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #789:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #790:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #791:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #792:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #793:
     Coin_1[5] Coin_2[0] Coin_5[1] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #794:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #795:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #796:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #797:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #798:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #799:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #800:
     Coin_1[5] Coin_2[0] Coin_5[3] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #801:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #802:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #803:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #804:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #805:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #806:
     Coin_1[5] Coin_2[0] Coin_5[5] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #807:
     Coin_1[5] Coin_2[0] Coin_5[7] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #808:
     Coin_1[5] Coin_2[0] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #809:
     Coin_1[5] Coin_2[0] Coin_5[7] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #810:
     Coin_1[5] Coin_2[0] Coin_5[7] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #811:
     Coin_1[5] Coin_2[0] Coin_5[7] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #812:
     Coin_1[5] Coin_2[0] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #813:
     Coin_1[5] Coin_2[0] Coin_5[9] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #814:
     Coin_1[5] Coin_2[0] Coin_5[9] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #815:
     Coin_1[5] Coin_2[0] Coin_5[9] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #816:
     Coin_1[5] Coin_2[0] Coin_5[11] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #817:
     Coin_1[5] Coin_2[0] Coin_5[11] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #818:
     Coin_1[5] Coin_2[0] Coin_5[11] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #819:
     Coin_1[5] Coin_2[0] Coin_5[13] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #820:
     Coin_1[5] Coin_2[0] Coin_5[13] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #821:
     Coin_1[5] Coin_2[0] Coin_5[15] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #822:
     Coin_1[5] Coin_2[0] Coin_5[15] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #823:
     Coin_1[5] Coin_2[0] Coin_5[17] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #824:
     Coin_1[5] Coin_2[0] Coin_5[19] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #825:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #826:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #827:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #828:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #829:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #830:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #831:
     Coin_1[5] Coin_2[5] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #832:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #833:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #834:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #835:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #836:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #837:
     Coin_1[5] Coin_2[5] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #838:
     Coin_1[5] Coin_2[5] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #839:
     Coin_1[5] Coin_2[5] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #840:
     Coin_1[5] Coin_2[5] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #841:
     Coin_1[5] Coin_2[5] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #842:
     Coin_1[5] Coin_2[5] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #843:
     Coin_1[5] Coin_2[5] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #844:
     Coin_1[5] Coin_2[5] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #845:
     Coin_1[5] Coin_2[5] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #846:
     Coin_1[5] Coin_2[5] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #847:
     Coin_1[5] Coin_2[5] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #848:
     Coin_1[5] Coin_2[5] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #849:
     Coin_1[5] Coin_2[5] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #850:
     Coin_1[5] Coin_2[5] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #851:
     Coin_1[5] Coin_2[5] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #852:
     Coin_1[5] Coin_2[5] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #853:
     Coin_1[5] Coin_2[5] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #854:
     Coin_1[5] Coin_2[5] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #855:
     Coin_1[5] Coin_2[5] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #856:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #857:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #858:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #859:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #860:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #861:
     Coin_1[5] Coin_2[10] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #862:
     Coin_1[5] Coin_2[10] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #863:
     Coin_1[5] Coin_2[10] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #864:
     Coin_1[5] Coin_2[10] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #865:
     Coin_1[5] Coin_2[10] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #866:
     Coin_1[5] Coin_2[10] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #867:
     Coin_1[5] Coin_2[10] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #868:
     Coin_1[5] Coin_2[10] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #869:
     Coin_1[5] Coin_2[10] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #870:
     Coin_1[5] Coin_2[10] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #871:
     Coin_1[5] Coin_2[10] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #872:
     Coin_1[5] Coin_2[10] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #873:
     Coin_1[5] Coin_2[10] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #874:
     Coin_1[5] Coin_2[10] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #875:
     Coin_1[5] Coin_2[10] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #876:
     Coin_1[5] Coin_2[10] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #877:
     Coin_1[5] Coin_2[10] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #878:
     Coin_1[5] Coin_2[10] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #879:
     Coin_1[5] Coin_2[10] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #880:
     Coin_1[5] Coin_2[15] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #881:
     Coin_1[5] Coin_2[15] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #882:
     Coin_1[5] Coin_2[15] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #883:
     Coin_1[5] Coin_2[15] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #884:
     Coin_1[5] Coin_2[15] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #885:
     Coin_1[5] Coin_2[15] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #886:
     Coin_1[5] Coin_2[15] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #887:
     Coin_1[5] Coin_2[15] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #888:
     Coin_1[5] Coin_2[15] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #889:
     Coin_1[5] Coin_2[15] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #890:
     Coin_1[5] Coin_2[15] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #891:
     Coin_1[5] Coin_2[15] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #892:
     Coin_1[5] Coin_2[15] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #893:
     Coin_1[5] Coin_2[15] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #894:
     Coin_1[5] Coin_2[15] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #895:
     Coin_1[5] Coin_2[15] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #896:
     Coin_1[5] Coin_2[15] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #897:
     Coin_1[5] Coin_2[15] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #898:
     Coin_1[5] Coin_2[20] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #899:
     Coin_1[5] Coin_2[20] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #900:
     Coin_1[5] Coin_2[20] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #901:
     Coin_1[5] Coin_2[20] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #902:
     Coin_1[5] Coin_2[20] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #903:
     Coin_1[5] Coin_2[20] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #904:
     Coin_1[5] Coin_2[20] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #905:
     Coin_1[5] Coin_2[20] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #906:
     Coin_1[5] Coin_2[20] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #907:
     Coin_1[5] Coin_2[20] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #908:
     Coin_1[5] Coin_2[20] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #909:
     Coin_1[5] Coin_2[20] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #910:
     Coin_1[5] Coin_2[20] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #911:
     Coin_1[5] Coin_2[25] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #912:
     Coin_1[5] Coin_2[25] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #913:
     Coin_1[5] Coin_2[25] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #914:
     Coin_1[5] Coin_2[25] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #915:
     Coin_1[5] Coin_2[25] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #916:
     Coin_1[5] Coin_2[25] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #917:
     Coin_1[5] Coin_2[25] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #918:
     Coin_1[5] Coin_2[25] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #919:
     Coin_1[5] Coin_2[25] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #920:
     Coin_1[5] Coin_2[30] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #921:
     Coin_1[5] Coin_2[30] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #922:
     Coin_1[5] Coin_2[30] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #923:
     Coin_1[5] Coin_2[30] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #924:
     Coin_1[5] Coin_2[30] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #925:
     Coin_1[5] Coin_2[30] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #926:
     Coin_1[5] Coin_2[35] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #927:
     Coin_1[5] Coin_2[35] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #928:
     Coin_1[5] Coin_2[35] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #929:
     Coin_1[5] Coin_2[35] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #930:
     Coin_1[5] Coin_2[40] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #931:
     Coin_1[5] Coin_2[40] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #932:
     Coin_1[5] Coin_2[45] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #933:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #934:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #935:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #936:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #937:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #938:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #939:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #940:
     Coin_1[6] Coin_2[2] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #941:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #942:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #943:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #944:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #945:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #946:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #947:
     Coin_1[6] Coin_2[2] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #948:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #949:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #950:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #951:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #952:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #953:
     Coin_1[6] Coin_2[2] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #954:
     Coin_1[6] Coin_2[2] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #955:
     Coin_1[6] Coin_2[2] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #956:
     Coin_1[6] Coin_2[2] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #957:
     Coin_1[6] Coin_2[2] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #958:
     Coin_1[6] Coin_2[2] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #959:
     Coin_1[6] Coin_2[2] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #960:
     Coin_1[6] Coin_2[2] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #961:
     Coin_1[6] Coin_2[2] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #962:
     Coin_1[6] Coin_2[2] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #963:
     Coin_1[6] Coin_2[2] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #964:
     Coin_1[6] Coin_2[2] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #965:
     Coin_1[6] Coin_2[2] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #966:
     Coin_1[6] Coin_2[2] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #967:
     Coin_1[6] Coin_2[2] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #968:
     Coin_1[6] Coin_2[2] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #969:
     Coin_1[6] Coin_2[2] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #970:
     Coin_1[6] Coin_2[2] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #971:
     Coin_1[6] Coin_2[2] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #972:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #973:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #974:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #975:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #976:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #977:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #978:
     Coin_1[6] Coin_2[7] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #979:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #980:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #981:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #982:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #983:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #984:
     Coin_1[6] Coin_2[7] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #985:
     Coin_1[6] Coin_2[7] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #986:
     Coin_1[6] Coin_2[7] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #987:
     Coin_1[6] Coin_2[7] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #988:
     Coin_1[6] Coin_2[7] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #989:
     Coin_1[6] Coin_2[7] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #990:
     Coin_1[6] Coin_2[7] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #991:
     Coin_1[6] Coin_2[7] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #992:
     Coin_1[6] Coin_2[7] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #993:
     Coin_1[6] Coin_2[7] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #994:
     Coin_1[6] Coin_2[7] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #995:
     Coin_1[6] Coin_2[7] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #996:
     Coin_1[6] Coin_2[7] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #997:
     Coin_1[6] Coin_2[7] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #998:
     Coin_1[6] Coin_2[7] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #999:
     Coin_1[6] Coin_2[7] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1000:
     Coin_1[6] Coin_2[7] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1001:
     Coin_1[6] Coin_2[7] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1002:
     Coin_1[6] Coin_2[7] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1003:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1004:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1005:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1006:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1007:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1008:
     Coin_1[6] Coin_2[12] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1009:
     Coin_1[6] Coin_2[12] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1010:
     Coin_1[6] Coin_2[12] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1011:
     Coin_1[6] Coin_2[12] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1012:
     Coin_1[6] Coin_2[12] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1013:
     Coin_1[6] Coin_2[12] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1014:
     Coin_1[6] Coin_2[12] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1015:
     Coin_1[6] Coin_2[12] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1016:
     Coin_1[6] Coin_2[12] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1017:
     Coin_1[6] Coin_2[12] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1018:
     Coin_1[6] Coin_2[12] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1019:
     Coin_1[6] Coin_2[12] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1020:
     Coin_1[6] Coin_2[12] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1021:
     Coin_1[6] Coin_2[12] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1022:
     Coin_1[6] Coin_2[12] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1023:
     Coin_1[6] Coin_2[12] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1024:
     Coin_1[6] Coin_2[12] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1025:
     Coin_1[6] Coin_2[12] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1026:
     Coin_1[6] Coin_2[12] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1027:
     Coin_1[6] Coin_2[17] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1028:
     Coin_1[6] Coin_2[17] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1029:
     Coin_1[6] Coin_2[17] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1030:
     Coin_1[6] Coin_2[17] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1031:
     Coin_1[6] Coin_2[17] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1032:
     Coin_1[6] Coin_2[17] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1033:
     Coin_1[6] Coin_2[17] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1034:
     Coin_1[6] Coin_2[17] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1035:
     Coin_1[6] Coin_2[17] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1036:
     Coin_1[6] Coin_2[17] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1037:
     Coin_1[6] Coin_2[17] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1038:
     Coin_1[6] Coin_2[17] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1039:
     Coin_1[6] Coin_2[17] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1040:
     Coin_1[6] Coin_2[17] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1041:
     Coin_1[6] Coin_2[17] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1042:
     Coin_1[6] Coin_2[17] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1043:
     Coin_1[6] Coin_2[17] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1044:
     Coin_1[6] Coin_2[17] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1045:
     Coin_1[6] Coin_2[22] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1046:
     Coin_1[6] Coin_2[22] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1047:
     Coin_1[6] Coin_2[22] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1048:
     Coin_1[6] Coin_2[22] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1049:
     Coin_1[6] Coin_2[22] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1050:
     Coin_1[6] Coin_2[22] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1051:
     Coin_1[6] Coin_2[22] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1052:
     Coin_1[6] Coin_2[22] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1053:
     Coin_1[6] Coin_2[22] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1054:
     Coin_1[6] Coin_2[22] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1055:
     Coin_1[6] Coin_2[22] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1056:
     Coin_1[6] Coin_2[22] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1057:
     Coin_1[6] Coin_2[22] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1058:
     Coin_1[6] Coin_2[27] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1059:
     Coin_1[6] Coin_2[27] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1060:
     Coin_1[6] Coin_2[27] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1061:
     Coin_1[6] Coin_2[27] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1062:
     Coin_1[6] Coin_2[27] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1063:
     Coin_1[6] Coin_2[27] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1064:
     Coin_1[6] Coin_2[27] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1065:
     Coin_1[6] Coin_2[27] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1066:
     Coin_1[6] Coin_2[27] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1067:
     Coin_1[6] Coin_2[32] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1068:
     Coin_1[6] Coin_2[32] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1069:
     Coin_1[6] Coin_2[32] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1070:
     Coin_1[6] Coin_2[32] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1071:
     Coin_1[6] Coin_2[32] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1072:
     Coin_1[6] Coin_2[32] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1073:
     Coin_1[6] Coin_2[37] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1074:
     Coin_1[6] Coin_2[37] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1075:
     Coin_1[6] Coin_2[37] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1076:
     Coin_1[6] Coin_2[37] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1077:
     Coin_1[6] Coin_2[42] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1078:
     Coin_1[6] Coin_2[42] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1079:
     Coin_1[6] Coin_2[47] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1080:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1081:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1082:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1083:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1084:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1085:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1086:
     Coin_1[7] Coin_2[4] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1087:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1088:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1089:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1090:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1091:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1092:
     Coin_1[7] Coin_2[4] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1093:
     Coin_1[7] Coin_2[4] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1094:
     Coin_1[7] Coin_2[4] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1095:
     Coin_1[7] Coin_2[4] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1096:
     Coin_1[7] Coin_2[4] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1097:
     Coin_1[7] Coin_2[4] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1098:
     Coin_1[7] Coin_2[4] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1099:
     Coin_1[7] Coin_2[4] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1100:
     Coin_1[7] Coin_2[4] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1101:
     Coin_1[7] Coin_2[4] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1102:
     Coin_1[7] Coin_2[4] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1103:
     Coin_1[7] Coin_2[4] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1104:
     Coin_1[7] Coin_2[4] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1105:
     Coin_1[7] Coin_2[4] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1106:
     Coin_1[7] Coin_2[4] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1107:
     Coin_1[7] Coin_2[4] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1108:
     Coin_1[7] Coin_2[4] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1109:
     Coin_1[7] Coin_2[4] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1110:
     Coin_1[7] Coin_2[4] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1111:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1112:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1113:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1114:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1115:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1116:
     Coin_1[7] Coin_2[9] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1117:
     Coin_1[7] Coin_2[9] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1118:
     Coin_1[7] Coin_2[9] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1119:
     Coin_1[7] Coin_2[9] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1120:
     Coin_1[7] Coin_2[9] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1121:
     Coin_1[7] Coin_2[9] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1122:
     Coin_1[7] Coin_2[9] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1123:
     Coin_1[7] Coin_2[9] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1124:
     Coin_1[7] Coin_2[9] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1125:
     Coin_1[7] Coin_2[9] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1126:
     Coin_1[7] Coin_2[9] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1127:
     Coin_1[7] Coin_2[9] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1128:
     Coin_1[7] Coin_2[9] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1129:
     Coin_1[7] Coin_2[9] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1130:
     Coin_1[7] Coin_2[9] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1131:
     Coin_1[7] Coin_2[9] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1132:
     Coin_1[7] Coin_2[9] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1133:
     Coin_1[7] Coin_2[9] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1134:
     Coin_1[7] Coin_2[9] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1135:
     Coin_1[7] Coin_2[14] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1136:
     Coin_1[7] Coin_2[14] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1137:
     Coin_1[7] Coin_2[14] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1138:
     Coin_1[7] Coin_2[14] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1139:
     Coin_1[7] Coin_2[14] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1140:
     Coin_1[7] Coin_2[14] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1141:
     Coin_1[7] Coin_2[14] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1142:
     Coin_1[7] Coin_2[14] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1143:
     Coin_1[7] Coin_2[14] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1144:
     Coin_1[7] Coin_2[14] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1145:
     Coin_1[7] Coin_2[14] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1146:
     Coin_1[7] Coin_2[14] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1147:
     Coin_1[7] Coin_2[14] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1148:
     Coin_1[7] Coin_2[14] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1149:
     Coin_1[7] Coin_2[14] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1150:
     Coin_1[7] Coin_2[14] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1151:
     Coin_1[7] Coin_2[14] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1152:
     Coin_1[7] Coin_2[14] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1153:
     Coin_1[7] Coin_2[19] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1154:
     Coin_1[7] Coin_2[19] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1155:
     Coin_1[7] Coin_2[19] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1156:
     Coin_1[7] Coin_2[19] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1157:
     Coin_1[7] Coin_2[19] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1158:
     Coin_1[7] Coin_2[19] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1159:
     Coin_1[7] Coin_2[19] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1160:
     Coin_1[7] Coin_2[19] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1161:
     Coin_1[7] Coin_2[19] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1162:
     Coin_1[7] Coin_2[19] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1163:
     Coin_1[7] Coin_2[19] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1164:
     Coin_1[7] Coin_2[19] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1165:
     Coin_1[7] Coin_2[19] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1166:
     Coin_1[7] Coin_2[24] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1167:
     Coin_1[7] Coin_2[24] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1168:
     Coin_1[7] Coin_2[24] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1169:
     Coin_1[7] Coin_2[24] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1170:
     Coin_1[7] Coin_2[24] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1171:
     Coin_1[7] Coin_2[24] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1172:
     Coin_1[7] Coin_2[24] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1173:
     Coin_1[7] Coin_2[24] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1174:
     Coin_1[7] Coin_2[24] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1175:
     Coin_1[7] Coin_2[29] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1176:
     Coin_1[7] Coin_2[29] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1177:
     Coin_1[7] Coin_2[29] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1178:
     Coin_1[7] Coin_2[29] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1179:
     Coin_1[7] Coin_2[29] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1180:
     Coin_1[7] Coin_2[29] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1181:
     Coin_1[7] Coin_2[34] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1182:
     Coin_1[7] Coin_2[34] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1183:
     Coin_1[7] Coin_2[34] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1184:
     Coin_1[7] Coin_2[34] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1185:
     Coin_1[7] Coin_2[39] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1186:
     Coin_1[7] Coin_2[39] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1187:
     Coin_1[7] Coin_2[44] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1188:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #1189:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1190:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1191:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1192:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1193:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1194:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1195:
     Coin_1[8] Coin_2[1] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1196:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1197:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1198:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1199:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1200:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1201:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1202:
     Coin_1[8] Coin_2[1] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1203:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1204:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1205:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1206:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1207:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1208:
     Coin_1[8] Coin_2[1] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1209:
     Coin_1[8] Coin_2[1] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1210:
     Coin_1[8] Coin_2[1] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1211:
     Coin_1[8] Coin_2[1] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1212:
     Coin_1[8] Coin_2[1] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1213:
     Coin_1[8] Coin_2[1] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1214:
     Coin_1[8] Coin_2[1] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1215:
     Coin_1[8] Coin_2[1] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1216:
     Coin_1[8] Coin_2[1] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1217:
     Coin_1[8] Coin_2[1] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1218:
     Coin_1[8] Coin_2[1] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1219:
     Coin_1[8] Coin_2[1] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1220:
     Coin_1[8] Coin_2[1] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1221:
     Coin_1[8] Coin_2[1] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1222:
     Coin_1[8] Coin_2[1] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1223:
     Coin_1[8] Coin_2[1] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1224:
     Coin_1[8] Coin_2[1] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1225:
     Coin_1[8] Coin_2[1] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1226:
     Coin_1[8] Coin_2[1] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1227:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1228:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1229:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1230:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1231:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1232:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1233:
     Coin_1[8] Coin_2[6] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1234:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1235:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1236:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1237:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1238:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1239:
     Coin_1[8] Coin_2[6] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1240:
     Coin_1[8] Coin_2[6] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1241:
     Coin_1[8] Coin_2[6] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1242:
     Coin_1[8] Coin_2[6] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1243:
     Coin_1[8] Coin_2[6] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1244:
     Coin_1[8] Coin_2[6] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1245:
     Coin_1[8] Coin_2[6] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1246:
     Coin_1[8] Coin_2[6] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1247:
     Coin_1[8] Coin_2[6] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1248:
     Coin_1[8] Coin_2[6] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1249:
     Coin_1[8] Coin_2[6] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1250:
     Coin_1[8] Coin_2[6] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1251:
     Coin_1[8] Coin_2[6] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1252:
     Coin_1[8] Coin_2[6] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1253:
     Coin_1[8] Coin_2[6] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1254:
     Coin_1[8] Coin_2[6] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1255:
     Coin_1[8] Coin_2[6] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1256:
     Coin_1[8] Coin_2[6] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1257:
     Coin_1[8] Coin_2[6] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1258:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1259:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1260:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1261:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1262:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1263:
     Coin_1[8] Coin_2[11] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1264:
     Coin_1[8] Coin_2[11] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1265:
     Coin_1[8] Coin_2[11] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1266:
     Coin_1[8] Coin_2[11] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1267:
     Coin_1[8] Coin_2[11] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1268:
     Coin_1[8] Coin_2[11] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1269:
     Coin_1[8] Coin_2[11] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1270:
     Coin_1[8] Coin_2[11] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1271:
     Coin_1[8] Coin_2[11] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1272:
     Coin_1[8] Coin_2[11] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1273:
     Coin_1[8] Coin_2[11] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1274:
     Coin_1[8] Coin_2[11] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1275:
     Coin_1[8] Coin_2[11] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1276:
     Coin_1[8] Coin_2[11] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1277:
     Coin_1[8] Coin_2[11] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1278:
     Coin_1[8] Coin_2[11] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1279:
     Coin_1[8] Coin_2[11] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1280:
     Coin_1[8] Coin_2[11] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1281:
     Coin_1[8] Coin_2[11] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1282:
     Coin_1[8] Coin_2[16] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1283:
     Coin_1[8] Coin_2[16] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1284:
     Coin_1[8] Coin_2[16] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1285:
     Coin_1[8] Coin_2[16] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1286:
     Coin_1[8] Coin_2[16] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1287:
     Coin_1[8] Coin_2[16] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1288:
     Coin_1[8] Coin_2[16] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1289:
     Coin_1[8] Coin_2[16] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1290:
     Coin_1[8] Coin_2[16] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1291:
     Coin_1[8] Coin_2[16] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1292:
     Coin_1[8] Coin_2[16] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1293:
     Coin_1[8] Coin_2[16] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1294:
     Coin_1[8] Coin_2[16] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1295:
     Coin_1[8] Coin_2[16] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1296:
     Coin_1[8] Coin_2[16] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1297:
     Coin_1[8] Coin_2[16] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1298:
     Coin_1[8] Coin_2[16] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1299:
     Coin_1[8] Coin_2[16] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1300:
     Coin_1[8] Coin_2[21] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1301:
     Coin_1[8] Coin_2[21] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1302:
     Coin_1[8] Coin_2[21] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1303:
     Coin_1[8] Coin_2[21] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1304:
     Coin_1[8] Coin_2[21] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1305:
     Coin_1[8] Coin_2[21] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1306:
     Coin_1[8] Coin_2[21] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1307:
     Coin_1[8] Coin_2[21] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1308:
     Coin_1[8] Coin_2[21] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1309:
     Coin_1[8] Coin_2[21] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1310:
     Coin_1[8] Coin_2[21] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1311:
     Coin_1[8] Coin_2[21] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1312:
     Coin_1[8] Coin_2[21] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1313:
     Coin_1[8] Coin_2[26] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1314:
     Coin_1[8] Coin_2[26] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1315:
     Coin_1[8] Coin_2[26] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1316:
     Coin_1[8] Coin_2[26] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1317:
     Coin_1[8] Coin_2[26] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1318:
     Coin_1[8] Coin_2[26] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1319:
     Coin_1[8] Coin_2[26] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1320:
     Coin_1[8] Coin_2[26] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1321:
     Coin_1[8] Coin_2[26] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1322:
     Coin_1[8] Coin_2[31] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1323:
     Coin_1[8] Coin_2[31] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1324:
     Coin_1[8] Coin_2[31] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1325:
     Coin_1[8] Coin_2[31] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1326:
     Coin_1[8] Coin_2[31] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1327:
     Coin_1[8] Coin_2[31] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1328:
     Coin_1[8] Coin_2[36] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1329:
     Coin_1[8] Coin_2[36] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1330:
     Coin_1[8] Coin_2[36] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1331:
     Coin_1[8] Coin_2[36] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1332:
     Coin_1[8] Coin_2[41] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1333:
     Coin_1[8] Coin_2[41] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1334:
     Coin_1[8] Coin_2[46] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1335:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1336:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1337:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1338:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1339:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1340:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1341:
     Coin_1[9] Coin_2[3] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1342:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1343:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1344:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1345:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1346:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1347:
     Coin_1[9] Coin_2[3] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1348:
     Coin_1[9] Coin_2[3] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1349:
     Coin_1[9] Coin_2[3] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1350:
     Coin_1[9] Coin_2[3] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1351:
     Coin_1[9] Coin_2[3] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1352:
     Coin_1[9] Coin_2[3] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1353:
     Coin_1[9] Coin_2[3] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1354:
     Coin_1[9] Coin_2[3] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1355:
     Coin_1[9] Coin_2[3] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1356:
     Coin_1[9] Coin_2[3] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1357:
     Coin_1[9] Coin_2[3] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1358:
     Coin_1[9] Coin_2[3] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1359:
     Coin_1[9] Coin_2[3] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1360:
     Coin_1[9] Coin_2[3] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1361:
     Coin_1[9] Coin_2[3] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1362:
     Coin_1[9] Coin_2[3] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1363:
     Coin_1[9] Coin_2[3] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1364:
     Coin_1[9] Coin_2[3] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1365:
     Coin_1[9] Coin_2[3] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1366:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1367:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1368:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1369:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1370:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1371:
     Coin_1[9] Coin_2[8] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1372:
     Coin_1[9] Coin_2[8] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1373:
     Coin_1[9] Coin_2[8] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1374:
     Coin_1[9] Coin_2[8] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1375:
     Coin_1[9] Coin_2[8] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1376:
     Coin_1[9] Coin_2[8] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1377:
     Coin_1[9] Coin_2[8] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1378:
     Coin_1[9] Coin_2[8] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1379:
     Coin_1[9] Coin_2[8] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1380:
     Coin_1[9] Coin_2[8] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1381:
     Coin_1[9] Coin_2[8] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1382:
     Coin_1[9] Coin_2[8] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1383:
     Coin_1[9] Coin_2[8] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1384:
     Coin_1[9] Coin_2[8] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1385:
     Coin_1[9] Coin_2[8] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1386:
     Coin_1[9] Coin_2[8] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1387:
     Coin_1[9] Coin_2[8] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1388:
     Coin_1[9] Coin_2[8] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1389:
     Coin_1[9] Coin_2[8] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1390:
     Coin_1[9] Coin_2[13] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1391:
     Coin_1[9] Coin_2[13] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1392:
     Coin_1[9] Coin_2[13] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1393:
     Coin_1[9] Coin_2[13] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1394:
     Coin_1[9] Coin_2[13] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1395:
     Coin_1[9] Coin_2[13] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1396:
     Coin_1[9] Coin_2[13] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1397:
     Coin_1[9] Coin_2[13] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1398:
     Coin_1[9] Coin_2[13] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1399:
     Coin_1[9] Coin_2[13] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1400:
     Coin_1[9] Coin_2[13] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1401:
     Coin_1[9] Coin_2[13] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1402:
     Coin_1[9] Coin_2[13] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1403:
     Coin_1[9] Coin_2[13] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1404:
     Coin_1[9] Coin_2[13] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1405:
     Coin_1[9] Coin_2[13] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1406:
     Coin_1[9] Coin_2[13] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1407:
     Coin_1[9] Coin_2[13] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1408:
     Coin_1[9] Coin_2[18] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1409:
     Coin_1[9] Coin_2[18] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1410:
     Coin_1[9] Coin_2[18] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1411:
     Coin_1[9] Coin_2[18] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1412:
     Coin_1[9] Coin_2[18] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1413:
     Coin_1[9] Coin_2[18] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1414:
     Coin_1[9] Coin_2[18] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1415:
     Coin_1[9] Coin_2[18] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1416:
     Coin_1[9] Coin_2[18] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1417:
     Coin_1[9] Coin_2[18] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1418:
     Coin_1[9] Coin_2[18] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1419:
     Coin_1[9] Coin_2[18] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1420:
     Coin_1[9] Coin_2[18] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1421:
     Coin_1[9] Coin_2[23] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1422:
     Coin_1[9] Coin_2[23] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1423:
     Coin_1[9] Coin_2[23] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1424:
     Coin_1[9] Coin_2[23] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1425:
     Coin_1[9] Coin_2[23] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1426:
     Coin_1[9] Coin_2[23] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1427:
     Coin_1[9] Coin_2[23] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1428:
     Coin_1[9] Coin_2[23] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1429:
     Coin_1[9] Coin_2[23] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1430:
     Coin_1[9] Coin_2[28] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1431:
     Coin_1[9] Coin_2[28] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1432:
     Coin_1[9] Coin_2[28] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1433:
     Coin_1[9] Coin_2[28] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1434:
     Coin_1[9] Coin_2[28] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1435:
     Coin_1[9] Coin_2[28] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1436:
     Coin_1[9] Coin_2[33] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1437:
     Coin_1[9] Coin_2[33] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1438:
     Coin_1[9] Coin_2[33] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1439:
     Coin_1[9] Coin_2[33] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1440:
     Coin_1[9] Coin_2[38] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1441:
     Coin_1[9] Coin_2[38] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1442:
     Coin_1[9] Coin_2[43] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1443:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[1] Coin_100[0]
Solution #1444:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1445:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1446:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1447:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1448:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[5] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1449:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[7] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1450:
     Coin_1[10] Coin_2[0] Coin_5[0] Coin_10[9] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1451:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1452:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1453:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1454:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1455:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1456:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1457:
     Coin_1[10] Coin_2[0] Coin_5[2] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1458:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1459:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1460:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1461:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1462:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1463:
     Coin_1[10] Coin_2[0] Coin_5[4] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1464:
     Coin_1[10] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1465:
     Coin_1[10] Coin_2[0] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1466:
     Coin_1[10] Coin_2[0] Coin_5[6] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1467:
     Coin_1[10] Coin_2[0] Coin_5[6] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1468:
     Coin_1[10] Coin_2[0] Coin_5[6] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1469:
     Coin_1[10] Coin_2[0] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1470:
     Coin_1[10] Coin_2[0] Coin_5[8] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1471:
     Coin_1[10] Coin_2[0] Coin_5[8] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1472:
     Coin_1[10] Coin_2[0] Coin_5[8] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1473:
     Coin_1[10] Coin_2[0] Coin_5[10] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1474:
     Coin_1[10] Coin_2[0] Coin_5[10] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1475:
     Coin_1[10] Coin_2[0] Coin_5[10] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1476:
     Coin_1[10] Coin_2[0] Coin_5[12] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1477:
     Coin_1[10] Coin_2[0] Coin_5[12] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1478:
     Coin_1[10] Coin_2[0] Coin_5[14] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1479:
     Coin_1[10] Coin_2[0] Coin_5[14] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1480:
     Coin_1[10] Coin_2[0] Coin_5[16] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1481:
     Coin_1[10] Coin_2[0] Coin_5[18] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1482:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1483:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1484:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1485:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1486:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1487:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1488:
     Coin_1[10] Coin_2[5] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1489:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1490:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1491:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1492:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1493:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1494:
     Coin_1[10] Coin_2[5] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1495:
     Coin_1[10] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1496:
     Coin_1[10] Coin_2[5] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1497:
     Coin_1[10] Coin_2[5] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1498:
     Coin_1[10] Coin_2[5] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1499:
     Coin_1[10] Coin_2[5] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1500:
     Coin_1[10] Coin_2[5] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1501:
     Coin_1[10] Coin_2[5] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1502:
     Coin_1[10] Coin_2[5] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1503:
     Coin_1[10] Coin_2[5] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1504:
     Coin_1[10] Coin_2[5] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1505:
     Coin_1[10] Coin_2[5] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1506:
     Coin_1[10] Coin_2[5] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1507:
     Coin_1[10] Coin_2[5] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1508:
     Coin_1[10] Coin_2[5] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1509:
     Coin_1[10] Coin_2[5] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1510:
     Coin_1[10] Coin_2[5] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1511:
     Coin_1[10] Coin_2[5] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1512:
     Coin_1[10] Coin_2[5] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1513:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1514:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1515:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1516:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1517:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1518:
     Coin_1[10] Coin_2[10] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1519:
     Coin_1[10] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1520:
     Coin_1[10] Coin_2[10] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1521:
     Coin_1[10] Coin_2[10] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1522:
     Coin_1[10] Coin_2[10] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1523:
     Coin_1[10] Coin_2[10] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1524:
     Coin_1[10] Coin_2[10] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1525:
     Coin_1[10] Coin_2[10] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1526:
     Coin_1[10] Coin_2[10] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1527:
     Coin_1[10] Coin_2[10] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1528:
     Coin_1[10] Coin_2[10] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1529:
     Coin_1[10] Coin_2[10] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1530:
     Coin_1[10] Coin_2[10] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1531:
     Coin_1[10] Coin_2[10] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1532:
     Coin_1[10] Coin_2[10] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1533:
     Coin_1[10] Coin_2[10] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1534:
     Coin_1[10] Coin_2[10] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1535:
     Coin_1[10] Coin_2[10] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1536:
     Coin_1[10] Coin_2[10] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1537:
     Coin_1[10] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1538:
     Coin_1[10] Coin_2[15] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1539:
     Coin_1[10] Coin_2[15] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1540:
     Coin_1[10] Coin_2[15] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1541:
     Coin_1[10] Coin_2[15] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1542:
     Coin_1[10] Coin_2[15] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1543:
     Coin_1[10] Coin_2[15] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1544:
     Coin_1[10] Coin_2[15] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1545:
     Coin_1[10] Coin_2[15] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1546:
     Coin_1[10] Coin_2[15] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1547:
     Coin_1[10] Coin_2[15] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1548:
     Coin_1[10] Coin_2[15] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1549:
     Coin_1[10] Coin_2[15] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1550:
     Coin_1[10] Coin_2[15] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1551:
     Coin_1[10] Coin_2[15] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1552:
     Coin_1[10] Coin_2[15] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1553:
     Coin_1[10] Coin_2[15] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1554:
     Coin_1[10] Coin_2[15] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1555:
     Coin_1[10] Coin_2[20] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1556:
     Coin_1[10] Coin_2[20] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1557:
     Coin_1[10] Coin_2[20] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1558:
     Coin_1[10] Coin_2[20] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1559:
     Coin_1[10] Coin_2[20] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1560:
     Coin_1[10] Coin_2[20] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1561:
     Coin_1[10] Coin_2[20] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1562:
     Coin_1[10] Coin_2[20] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1563:
     Coin_1[10] Coin_2[20] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1564:
     Coin_1[10] Coin_2[20] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1565:
     Coin_1[10] Coin_2[20] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1566:
     Coin_1[10] Coin_2[20] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1567:
     Coin_1[10] Coin_2[20] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1568:
     Coin_1[10] Coin_2[25] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1569:
     Coin_1[10] Coin_2[25] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1570:
     Coin_1[10] Coin_2[25] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1571:
     Coin_1[10] Coin_2[25] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1572:
     Coin_1[10] Coin_2[25] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1573:
     Coin_1[10] Coin_2[25] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1574:
     Coin_1[10] Coin_2[25] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1575:
     Coin_1[10] Coin_2[25] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1576:
     Coin_1[10] Coin_2[25] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1577:
     Coin_1[10] Coin_2[30] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1578:
     Coin_1[10] Coin_2[30] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1579:
     Coin_1[10] Coin_2[30] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1580:
     Coin_1[10] Coin_2[30] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1581:
     Coin_1[10] Coin_2[30] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1582:
     Coin_1[10] Coin_2[30] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1583:
     Coin_1[10] Coin_2[35] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1584:
     Coin_1[10] Coin_2[35] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1585:
     Coin_1[10] Coin_2[35] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1586:
     Coin_1[10] Coin_2[35] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1587:
     Coin_1[10] Coin_2[40] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1588:
     Coin_1[10] Coin_2[40] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1589:
     Coin_1[10] Coin_2[45] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1590:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1591:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1592:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1593:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1594:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1595:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1596:
     Coin_1[11] Coin_2[2] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1597:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1598:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1599:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1600:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1601:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1602:
     Coin_1[11] Coin_2[2] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1603:
     Coin_1[11] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1604:
     Coin_1[11] Coin_2[2] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1605:
     Coin_1[11] Coin_2[2] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1606:
     Coin_1[11] Coin_2[2] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1607:
     Coin_1[11] Coin_2[2] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1608:
     Coin_1[11] Coin_2[2] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1609:
     Coin_1[11] Coin_2[2] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1610:
     Coin_1[11] Coin_2[2] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1611:
     Coin_1[11] Coin_2[2] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1612:
     Coin_1[11] Coin_2[2] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1613:
     Coin_1[11] Coin_2[2] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1614:
     Coin_1[11] Coin_2[2] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1615:
     Coin_1[11] Coin_2[2] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1616:
     Coin_1[11] Coin_2[2] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1617:
     Coin_1[11] Coin_2[2] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1618:
     Coin_1[11] Coin_2[2] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1619:
     Coin_1[11] Coin_2[2] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1620:
     Coin_1[11] Coin_2[2] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1621:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1622:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1623:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1624:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1625:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1626:
     Coin_1[11] Coin_2[7] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1627:
     Coin_1[11] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1628:
     Coin_1[11] Coin_2[7] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1629:
     Coin_1[11] Coin_2[7] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1630:
     Coin_1[11] Coin_2[7] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1631:
     Coin_1[11] Coin_2[7] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1632:
     Coin_1[11] Coin_2[7] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1633:
     Coin_1[11] Coin_2[7] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1634:
     Coin_1[11] Coin_2[7] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1635:
     Coin_1[11] Coin_2[7] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1636:
     Coin_1[11] Coin_2[7] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1637:
     Coin_1[11] Coin_2[7] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1638:
     Coin_1[11] Coin_2[7] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1639:
     Coin_1[11] Coin_2[7] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1640:
     Coin_1[11] Coin_2[7] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1641:
     Coin_1[11] Coin_2[7] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1642:
     Coin_1[11] Coin_2[7] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1643:
     Coin_1[11] Coin_2[7] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1644:
     Coin_1[11] Coin_2[7] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1645:
     Coin_1[11] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1646:
     Coin_1[11] Coin_2[12] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1647:
     Coin_1[11] Coin_2[12] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1648:
     Coin_1[11] Coin_2[12] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1649:
     Coin_1[11] Coin_2[12] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1650:
     Coin_1[11] Coin_2[12] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1651:
     Coin_1[11] Coin_2[12] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1652:
     Coin_1[11] Coin_2[12] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1653:
     Coin_1[11] Coin_2[12] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1654:
     Coin_1[11] Coin_2[12] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1655:
     Coin_1[11] Coin_2[12] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1656:
     Coin_1[11] Coin_2[12] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1657:
     Coin_1[11] Coin_2[12] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1658:
     Coin_1[11] Coin_2[12] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1659:
     Coin_1[11] Coin_2[12] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1660:
     Coin_1[11] Coin_2[12] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1661:
     Coin_1[11] Coin_2[12] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1662:
     Coin_1[11] Coin_2[12] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1663:
     Coin_1[11] Coin_2[17] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1664:
     Coin_1[11] Coin_2[17] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1665:
     Coin_1[11] Coin_2[17] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1666:
     Coin_1[11] Coin_2[17] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1667:
     Coin_1[11] Coin_2[17] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1668:
     Coin_1[11] Coin_2[17] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1669:
     Coin_1[11] Coin_2[17] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1670:
     Coin_1[11] Coin_2[17] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1671:
     Coin_1[11] Coin_2[17] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1672:
     Coin_1[11] Coin_2[17] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1673:
     Coin_1[11] Coin_2[17] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1674:
     Coin_1[11] Coin_2[17] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1675:
     Coin_1[11] Coin_2[17] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1676:
     Coin_1[11] Coin_2[22] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1677:
     Coin_1[11] Coin_2[22] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1678:
     Coin_1[11] Coin_2[22] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1679:
     Coin_1[11] Coin_2[22] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1680:
     Coin_1[11] Coin_2[22] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1681:
     Coin_1[11] Coin_2[22] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1682:
     Coin_1[11] Coin_2[22] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1683:
     Coin_1[11] Coin_2[22] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1684:
     Coin_1[11] Coin_2[22] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1685:
     Coin_1[11] Coin_2[27] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1686:
     Coin_1[11] Coin_2[27] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1687:
     Coin_1[11] Coin_2[27] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1688:
     Coin_1[11] Coin_2[27] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1689:
     Coin_1[11] Coin_2[27] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1690:
     Coin_1[11] Coin_2[27] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1691:
     Coin_1[11] Coin_2[32] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1692:
     Coin_1[11] Coin_2[32] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1693:
     Coin_1[11] Coin_2[32] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1694:
     Coin_1[11] Coin_2[32] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1695:
     Coin_1[11] Coin_2[37] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1696:
     Coin_1[11] Coin_2[37] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1697:
     Coin_1[11] Coin_2[42] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1698:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1699:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1700:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1701:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1702:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1703:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1704:
     Coin_1[12] Coin_2[4] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1705:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1706:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1707:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1708:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1709:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1710:
     Coin_1[12] Coin_2[4] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1711:
     Coin_1[12] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1712:
     Coin_1[12] Coin_2[4] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1713:
     Coin_1[12] Coin_2[4] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1714:
     Coin_1[12] Coin_2[4] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1715:
     Coin_1[12] Coin_2[4] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1716:
     Coin_1[12] Coin_2[4] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1717:
     Coin_1[12] Coin_2[4] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1718:
     Coin_1[12] Coin_2[4] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1719:
     Coin_1[12] Coin_2[4] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1720:
     Coin_1[12] Coin_2[4] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1721:
     Coin_1[12] Coin_2[4] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1722:
     Coin_1[12] Coin_2[4] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1723:
     Coin_1[12] Coin_2[4] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1724:
     Coin_1[12] Coin_2[4] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1725:
     Coin_1[12] Coin_2[4] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1726:
     Coin_1[12] Coin_2[4] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1727:
     Coin_1[12] Coin_2[4] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1728:
     Coin_1[12] Coin_2[4] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1729:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1730:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1731:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1732:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1733:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1734:
     Coin_1[12] Coin_2[9] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1735:
     Coin_1[12] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1736:
     Coin_1[12] Coin_2[9] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1737:
     Coin_1[12] Coin_2[9] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1738:
     Coin_1[12] Coin_2[9] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1739:
     Coin_1[12] Coin_2[9] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1740:
     Coin_1[12] Coin_2[9] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1741:
     Coin_1[12] Coin_2[9] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1742:
     Coin_1[12] Coin_2[9] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1743:
     Coin_1[12] Coin_2[9] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1744:
     Coin_1[12] Coin_2[9] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1745:
     Coin_1[12] Coin_2[9] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1746:
     Coin_1[12] Coin_2[9] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1747:
     Coin_1[12] Coin_2[9] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1748:
     Coin_1[12] Coin_2[9] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1749:
     Coin_1[12] Coin_2[9] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1750:
     Coin_1[12] Coin_2[9] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1751:
     Coin_1[12] Coin_2[9] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1752:
     Coin_1[12] Coin_2[9] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1753:
     Coin_1[12] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1754:
     Coin_1[12] Coin_2[14] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1755:
     Coin_1[12] Coin_2[14] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1756:
     Coin_1[12] Coin_2[14] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1757:
     Coin_1[12] Coin_2[14] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1758:
     Coin_1[12] Coin_2[14] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1759:
     Coin_1[12] Coin_2[14] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1760:
     Coin_1[12] Coin_2[14] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1761:
     Coin_1[12] Coin_2[14] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1762:
     Coin_1[12] Coin_2[14] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1763:
     Coin_1[12] Coin_2[14] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1764:
     Coin_1[12] Coin_2[14] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1765:
     Coin_1[12] Coin_2[14] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1766:
     Coin_1[12] Coin_2[14] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1767:
     Coin_1[12] Coin_2[14] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1768:
     Coin_1[12] Coin_2[14] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1769:
     Coin_1[12] Coin_2[14] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1770:
     Coin_1[12] Coin_2[14] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1771:
     Coin_1[12] Coin_2[19] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1772:
     Coin_1[12] Coin_2[19] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1773:
     Coin_1[12] Coin_2[19] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1774:
     Coin_1[12] Coin_2[19] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1775:
     Coin_1[12] Coin_2[19] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1776:
     Coin_1[12] Coin_2[19] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1777:
     Coin_1[12] Coin_2[19] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1778:
     Coin_1[12] Coin_2[19] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1779:
     Coin_1[12] Coin_2[19] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1780:
     Coin_1[12] Coin_2[19] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1781:
     Coin_1[12] Coin_2[19] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1782:
     Coin_1[12] Coin_2[19] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1783:
     Coin_1[12] Coin_2[19] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1784:
     Coin_1[12] Coin_2[24] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1785:
     Coin_1[12] Coin_2[24] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1786:
     Coin_1[12] Coin_2[24] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1787:
     Coin_1[12] Coin_2[24] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1788:
     Coin_1[12] Coin_2[24] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1789:
     Coin_1[12] Coin_2[24] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1790:
     Coin_1[12] Coin_2[24] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1791:
     Coin_1[12] Coin_2[24] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1792:
     Coin_1[12] Coin_2[24] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1793:
     Coin_1[12] Coin_2[29] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1794:
     Coin_1[12] Coin_2[29] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1795:
     Coin_1[12] Coin_2[29] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1796:
     Coin_1[12] Coin_2[29] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1797:
     Coin_1[12] Coin_2[29] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1798:
     Coin_1[12] Coin_2[29] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1799:
     Coin_1[12] Coin_2[34] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1800:
     Coin_1[12] Coin_2[34] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1801:
     Coin_1[12] Coin_2[34] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1802:
     Coin_1[12] Coin_2[34] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1803:
     Coin_1[12] Coin_2[39] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1804:
     Coin_1[12] Coin_2[39] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1805:
     Coin_1[12] Coin_2[44] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1806:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1807:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1808:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1809:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1810:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1811:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1812:
     Coin_1[13] Coin_2[1] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1813:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1814:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1815:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1816:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1817:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1818:
     Coin_1[13] Coin_2[1] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1819:
     Coin_1[13] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1820:
     Coin_1[13] Coin_2[1] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1821:
     Coin_1[13] Coin_2[1] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1822:
     Coin_1[13] Coin_2[1] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1823:
     Coin_1[13] Coin_2[1] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1824:
     Coin_1[13] Coin_2[1] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1825:
     Coin_1[13] Coin_2[1] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1826:
     Coin_1[13] Coin_2[1] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1827:
     Coin_1[13] Coin_2[1] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1828:
     Coin_1[13] Coin_2[1] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1829:
     Coin_1[13] Coin_2[1] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1830:
     Coin_1[13] Coin_2[1] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1831:
     Coin_1[13] Coin_2[1] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1832:
     Coin_1[13] Coin_2[1] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1833:
     Coin_1[13] Coin_2[1] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1834:
     Coin_1[13] Coin_2[1] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1835:
     Coin_1[13] Coin_2[1] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1836:
     Coin_1[13] Coin_2[1] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1837:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1838:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1839:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1840:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1841:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1842:
     Coin_1[13] Coin_2[6] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1843:
     Coin_1[13] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1844:
     Coin_1[13] Coin_2[6] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1845:
     Coin_1[13] Coin_2[6] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1846:
     Coin_1[13] Coin_2[6] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1847:
     Coin_1[13] Coin_2[6] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1848:
     Coin_1[13] Coin_2[6] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1849:
     Coin_1[13] Coin_2[6] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1850:
     Coin_1[13] Coin_2[6] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1851:
     Coin_1[13] Coin_2[6] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1852:
     Coin_1[13] Coin_2[6] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1853:
     Coin_1[13] Coin_2[6] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1854:
     Coin_1[13] Coin_2[6] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1855:
     Coin_1[13] Coin_2[6] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1856:
     Coin_1[13] Coin_2[6] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1857:
     Coin_1[13] Coin_2[6] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1858:
     Coin_1[13] Coin_2[6] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1859:
     Coin_1[13] Coin_2[6] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1860:
     Coin_1[13] Coin_2[6] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1861:
     Coin_1[13] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1862:
     Coin_1[13] Coin_2[11] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1863:
     Coin_1[13] Coin_2[11] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1864:
     Coin_1[13] Coin_2[11] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1865:
     Coin_1[13] Coin_2[11] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1866:
     Coin_1[13] Coin_2[11] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1867:
     Coin_1[13] Coin_2[11] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1868:
     Coin_1[13] Coin_2[11] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1869:
     Coin_1[13] Coin_2[11] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1870:
     Coin_1[13] Coin_2[11] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1871:
     Coin_1[13] Coin_2[11] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1872:
     Coin_1[13] Coin_2[11] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1873:
     Coin_1[13] Coin_2[11] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1874:
     Coin_1[13] Coin_2[11] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1875:
     Coin_1[13] Coin_2[11] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1876:
     Coin_1[13] Coin_2[11] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1877:
     Coin_1[13] Coin_2[11] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1878:
     Coin_1[13] Coin_2[11] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1879:
     Coin_1[13] Coin_2[16] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1880:
     Coin_1[13] Coin_2[16] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1881:
     Coin_1[13] Coin_2[16] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1882:
     Coin_1[13] Coin_2[16] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1883:
     Coin_1[13] Coin_2[16] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1884:
     Coin_1[13] Coin_2[16] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1885:
     Coin_1[13] Coin_2[16] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1886:
     Coin_1[13] Coin_2[16] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1887:
     Coin_1[13] Coin_2[16] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1888:
     Coin_1[13] Coin_2[16] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1889:
     Coin_1[13] Coin_2[16] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1890:
     Coin_1[13] Coin_2[16] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1891:
     Coin_1[13] Coin_2[16] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1892:
     Coin_1[13] Coin_2[21] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1893:
     Coin_1[13] Coin_2[21] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1894:
     Coin_1[13] Coin_2[21] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1895:
     Coin_1[13] Coin_2[21] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1896:
     Coin_1[13] Coin_2[21] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1897:
     Coin_1[13] Coin_2[21] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1898:
     Coin_1[13] Coin_2[21] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1899:
     Coin_1[13] Coin_2[21] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1900:
     Coin_1[13] Coin_2[21] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1901:
     Coin_1[13] Coin_2[26] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1902:
     Coin_1[13] Coin_2[26] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1903:
     Coin_1[13] Coin_2[26] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1904:
     Coin_1[13] Coin_2[26] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1905:
     Coin_1[13] Coin_2[26] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1906:
     Coin_1[13] Coin_2[26] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1907:
     Coin_1[13] Coin_2[31] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1908:
     Coin_1[13] Coin_2[31] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1909:
     Coin_1[13] Coin_2[31] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1910:
     Coin_1[13] Coin_2[31] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1911:
     Coin_1[13] Coin_2[36] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1912:
     Coin_1[13] Coin_2[36] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1913:
     Coin_1[13] Coin_2[41] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1914:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #1915:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1916:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1917:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1918:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1919:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1920:
     Coin_1[14] Coin_2[3] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1921:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1922:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1923:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1924:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1925:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1926:
     Coin_1[14] Coin_2[3] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1927:
     Coin_1[14] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1928:
     Coin_1[14] Coin_2[3] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1929:
     Coin_1[14] Coin_2[3] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1930:
     Coin_1[14] Coin_2[3] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1931:
     Coin_1[14] Coin_2[3] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1932:
     Coin_1[14] Coin_2[3] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1933:
     Coin_1[14] Coin_2[3] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1934:
     Coin_1[14] Coin_2[3] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1935:
     Coin_1[14] Coin_2[3] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1936:
     Coin_1[14] Coin_2[3] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1937:
     Coin_1[14] Coin_2[3] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1938:
     Coin_1[14] Coin_2[3] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1939:
     Coin_1[14] Coin_2[3] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1940:
     Coin_1[14] Coin_2[3] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1941:
     Coin_1[14] Coin_2[3] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1942:
     Coin_1[14] Coin_2[3] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1943:
     Coin_1[14] Coin_2[3] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1944:
     Coin_1[14] Coin_2[3] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1945:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #1946:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1947:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1948:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1949:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1950:
     Coin_1[14] Coin_2[8] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1951:
     Coin_1[14] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1952:
     Coin_1[14] Coin_2[8] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1953:
     Coin_1[14] Coin_2[8] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1954:
     Coin_1[14] Coin_2[8] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1955:
     Coin_1[14] Coin_2[8] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1956:
     Coin_1[14] Coin_2[8] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1957:
     Coin_1[14] Coin_2[8] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1958:
     Coin_1[14] Coin_2[8] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1959:
     Coin_1[14] Coin_2[8] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1960:
     Coin_1[14] Coin_2[8] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1961:
     Coin_1[14] Coin_2[8] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1962:
     Coin_1[14] Coin_2[8] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1963:
     Coin_1[14] Coin_2[8] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1964:
     Coin_1[14] Coin_2[8] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1965:
     Coin_1[14] Coin_2[8] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1966:
     Coin_1[14] Coin_2[8] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1967:
     Coin_1[14] Coin_2[8] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1968:
     Coin_1[14] Coin_2[8] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1969:
     Coin_1[14] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #1970:
     Coin_1[14] Coin_2[13] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1971:
     Coin_1[14] Coin_2[13] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1972:
     Coin_1[14] Coin_2[13] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1973:
     Coin_1[14] Coin_2[13] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1974:
     Coin_1[14] Coin_2[13] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1975:
     Coin_1[14] Coin_2[13] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1976:
     Coin_1[14] Coin_2[13] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1977:
     Coin_1[14] Coin_2[13] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1978:
     Coin_1[14] Coin_2[13] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1979:
     Coin_1[14] Coin_2[13] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1980:
     Coin_1[14] Coin_2[13] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1981:
     Coin_1[14] Coin_2[13] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1982:
     Coin_1[14] Coin_2[13] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1983:
     Coin_1[14] Coin_2[13] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1984:
     Coin_1[14] Coin_2[13] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1985:
     Coin_1[14] Coin_2[13] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1986:
     Coin_1[14] Coin_2[13] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1987:
     Coin_1[14] Coin_2[18] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #1988:
     Coin_1[14] Coin_2[18] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1989:
     Coin_1[14] Coin_2[18] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1990:
     Coin_1[14] Coin_2[18] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1991:
     Coin_1[14] Coin_2[18] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #1992:
     Coin_1[14] Coin_2[18] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1993:
     Coin_1[14] Coin_2[18] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1994:
     Coin_1[14] Coin_2[18] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1995:
     Coin_1[14] Coin_2[18] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1996:
     Coin_1[14] Coin_2[18] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #1997:
     Coin_1[14] Coin_2[18] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1998:
     Coin_1[14] Coin_2[18] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #1999:
     Coin_1[14] Coin_2[18] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2000:
     Coin_1[14] Coin_2[23] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2001:
     Coin_1[14] Coin_2[23] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2002:
     Coin_1[14] Coin_2[23] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2003:
     Coin_1[14] Coin_2[23] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2004:
     Coin_1[14] Coin_2[23] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2005:
     Coin_1[14] Coin_2[23] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2006:
     Coin_1[14] Coin_2[23] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2007:
     Coin_1[14] Coin_2[23] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2008:
     Coin_1[14] Coin_2[23] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2009:
     Coin_1[14] Coin_2[28] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2010:
     Coin_1[14] Coin_2[28] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2011:
     Coin_1[14] Coin_2[28] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2012:
     Coin_1[14] Coin_2[28] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2013:
     Coin_1[14] Coin_2[28] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2014:
     Coin_1[14] Coin_2[28] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2015:
     Coin_1[14] Coin_2[33] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2016:
     Coin_1[14] Coin_2[33] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2017:
     Coin_1[14] Coin_2[33] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2018:
     Coin_1[14] Coin_2[33] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2019:
     Coin_1[14] Coin_2[38] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2020:
     Coin_1[14] Coin_2[38] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2021:
     Coin_1[14] Coin_2[43] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2022:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #2023:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2024:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2025:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2026:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2027:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2028:
     Coin_1[15] Coin_2[0] Coin_5[1] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2029:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2030:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2031:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2032:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2033:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2034:
     Coin_1[15] Coin_2[0] Coin_5[3] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2035:
     Coin_1[15] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2036:
     Coin_1[15] Coin_2[0] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2037:
     Coin_1[15] Coin_2[0] Coin_5[5] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2038:
     Coin_1[15] Coin_2[0] Coin_5[5] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2039:
     Coin_1[15] Coin_2[0] Coin_5[5] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2040:
     Coin_1[15] Coin_2[0] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2041:
     Coin_1[15] Coin_2[0] Coin_5[7] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2042:
     Coin_1[15] Coin_2[0] Coin_5[7] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2043:
     Coin_1[15] Coin_2[0] Coin_5[7] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2044:
     Coin_1[15] Coin_2[0] Coin_5[9] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2045:
     Coin_1[15] Coin_2[0] Coin_5[9] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2046:
     Coin_1[15] Coin_2[0] Coin_5[9] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2047:
     Coin_1[15] Coin_2[0] Coin_5[11] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2048:
     Coin_1[15] Coin_2[0] Coin_5[11] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2049:
     Coin_1[15] Coin_2[0] Coin_5[13] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2050:
     Coin_1[15] Coin_2[0] Coin_5[13] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2051:
     Coin_1[15] Coin_2[0] Coin_5[15] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2052:
     Coin_1[15] Coin_2[0] Coin_5[17] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2053:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2054:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2055:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2056:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2057:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2058:
     Coin_1[15] Coin_2[5] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2059:
     Coin_1[15] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2060:
     Coin_1[15] Coin_2[5] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2061:
     Coin_1[15] Coin_2[5] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2062:
     Coin_1[15] Coin_2[5] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2063:
     Coin_1[15] Coin_2[5] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2064:
     Coin_1[15] Coin_2[5] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2065:
     Coin_1[15] Coin_2[5] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2066:
     Coin_1[15] Coin_2[5] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2067:
     Coin_1[15] Coin_2[5] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2068:
     Coin_1[15] Coin_2[5] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2069:
     Coin_1[15] Coin_2[5] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2070:
     Coin_1[15] Coin_2[5] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2071:
     Coin_1[15] Coin_2[5] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2072:
     Coin_1[15] Coin_2[5] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2073:
     Coin_1[15] Coin_2[5] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2074:
     Coin_1[15] Coin_2[5] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2075:
     Coin_1[15] Coin_2[5] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2076:
     Coin_1[15] Coin_2[5] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2077:
     Coin_1[15] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2078:
     Coin_1[15] Coin_2[10] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2079:
     Coin_1[15] Coin_2[10] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2080:
     Coin_1[15] Coin_2[10] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2081:
     Coin_1[15] Coin_2[10] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2082:
     Coin_1[15] Coin_2[10] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2083:
     Coin_1[15] Coin_2[10] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2084:
     Coin_1[15] Coin_2[10] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2085:
     Coin_1[15] Coin_2[10] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2086:
     Coin_1[15] Coin_2[10] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2087:
     Coin_1[15] Coin_2[10] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2088:
     Coin_1[15] Coin_2[10] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2089:
     Coin_1[15] Coin_2[10] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2090:
     Coin_1[15] Coin_2[10] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2091:
     Coin_1[15] Coin_2[10] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2092:
     Coin_1[15] Coin_2[10] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2093:
     Coin_1[15] Coin_2[10] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2094:
     Coin_1[15] Coin_2[10] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2095:
     Coin_1[15] Coin_2[15] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2096:
     Coin_1[15] Coin_2[15] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2097:
     Coin_1[15] Coin_2[15] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2098:
     Coin_1[15] Coin_2[15] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2099:
     Coin_1[15] Coin_2[15] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2100:
     Coin_1[15] Coin_2[15] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2101:
     Coin_1[15] Coin_2[15] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2102:
     Coin_1[15] Coin_2[15] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2103:
     Coin_1[15] Coin_2[15] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2104:
     Coin_1[15] Coin_2[15] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2105:
     Coin_1[15] Coin_2[15] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2106:
     Coin_1[15] Coin_2[15] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2107:
     Coin_1[15] Coin_2[15] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2108:
     Coin_1[15] Coin_2[20] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2109:
     Coin_1[15] Coin_2[20] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2110:
     Coin_1[15] Coin_2[20] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2111:
     Coin_1[15] Coin_2[20] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2112:
     Coin_1[15] Coin_2[20] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2113:
     Coin_1[15] Coin_2[20] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2114:
     Coin_1[15] Coin_2[20] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2115:
     Coin_1[15] Coin_2[20] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2116:
     Coin_1[15] Coin_2[20] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2117:
     Coin_1[15] Coin_2[25] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2118:
     Coin_1[15] Coin_2[25] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2119:
     Coin_1[15] Coin_2[25] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2120:
     Coin_1[15] Coin_2[25] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2121:
     Coin_1[15] Coin_2[25] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2122:
     Coin_1[15] Coin_2[25] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2123:
     Coin_1[15] Coin_2[30] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2124:
     Coin_1[15] Coin_2[30] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2125:
     Coin_1[15] Coin_2[30] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2126:
     Coin_1[15] Coin_2[30] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2127:
     Coin_1[15] Coin_2[35] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2128:
     Coin_1[15] Coin_2[35] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2129:
     Coin_1[15] Coin_2[40] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2130:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #2131:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2132:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2133:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2134:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2135:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2136:
     Coin_1[16] Coin_2[2] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2137:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2138:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2139:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2140:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2141:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2142:
     Coin_1[16] Coin_2[2] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2143:
     Coin_1[16] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2144:
     Coin_1[16] Coin_2[2] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2145:
     Coin_1[16] Coin_2[2] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2146:
     Coin_1[16] Coin_2[2] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2147:
     Coin_1[16] Coin_2[2] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2148:
     Coin_1[16] Coin_2[2] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2149:
     Coin_1[16] Coin_2[2] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2150:
     Coin_1[16] Coin_2[2] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2151:
     Coin_1[16] Coin_2[2] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2152:
     Coin_1[16] Coin_2[2] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2153:
     Coin_1[16] Coin_2[2] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2154:
     Coin_1[16] Coin_2[2] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2155:
     Coin_1[16] Coin_2[2] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2156:
     Coin_1[16] Coin_2[2] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2157:
     Coin_1[16] Coin_2[2] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2158:
     Coin_1[16] Coin_2[2] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2159:
     Coin_1[16] Coin_2[2] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2160:
     Coin_1[16] Coin_2[2] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2161:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2162:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2163:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2164:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2165:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2166:
     Coin_1[16] Coin_2[7] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2167:
     Coin_1[16] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2168:
     Coin_1[16] Coin_2[7] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2169:
     Coin_1[16] Coin_2[7] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2170:
     Coin_1[16] Coin_2[7] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2171:
     Coin_1[16] Coin_2[7] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2172:
     Coin_1[16] Coin_2[7] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2173:
     Coin_1[16] Coin_2[7] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2174:
     Coin_1[16] Coin_2[7] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2175:
     Coin_1[16] Coin_2[7] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2176:
     Coin_1[16] Coin_2[7] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2177:
     Coin_1[16] Coin_2[7] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2178:
     Coin_1[16] Coin_2[7] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2179:
     Coin_1[16] Coin_2[7] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2180:
     Coin_1[16] Coin_2[7] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2181:
     Coin_1[16] Coin_2[7] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2182:
     Coin_1[16] Coin_2[7] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2183:
     Coin_1[16] Coin_2[7] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2184:
     Coin_1[16] Coin_2[7] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2185:
     Coin_1[16] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2186:
     Coin_1[16] Coin_2[12] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2187:
     Coin_1[16] Coin_2[12] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2188:
     Coin_1[16] Coin_2[12] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2189:
     Coin_1[16] Coin_2[12] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2190:
     Coin_1[16] Coin_2[12] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2191:
     Coin_1[16] Coin_2[12] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2192:
     Coin_1[16] Coin_2[12] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2193:
     Coin_1[16] Coin_2[12] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2194:
     Coin_1[16] Coin_2[12] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2195:
     Coin_1[16] Coin_2[12] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2196:
     Coin_1[16] Coin_2[12] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2197:
     Coin_1[16] Coin_2[12] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2198:
     Coin_1[16] Coin_2[12] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2199:
     Coin_1[16] Coin_2[12] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2200:
     Coin_1[16] Coin_2[12] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2201:
     Coin_1[16] Coin_2[12] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2202:
     Coin_1[16] Coin_2[12] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2203:
     Coin_1[16] Coin_2[17] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2204:
     Coin_1[16] Coin_2[17] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2205:
     Coin_1[16] Coin_2[17] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2206:
     Coin_1[16] Coin_2[17] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2207:
     Coin_1[16] Coin_2[17] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2208:
     Coin_1[16] Coin_2[17] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2209:
     Coin_1[16] Coin_2[17] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2210:
     Coin_1[16] Coin_2[17] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2211:
     Coin_1[16] Coin_2[17] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2212:
     Coin_1[16] Coin_2[17] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2213:
     Coin_1[16] Coin_2[17] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2214:
     Coin_1[16] Coin_2[17] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2215:
     Coin_1[16] Coin_2[17] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2216:
     Coin_1[16] Coin_2[22] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2217:
     Coin_1[16] Coin_2[22] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2218:
     Coin_1[16] Coin_2[22] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2219:
     Coin_1[16] Coin_2[22] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2220:
     Coin_1[16] Coin_2[22] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2221:
     Coin_1[16] Coin_2[22] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2222:
     Coin_1[16] Coin_2[22] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2223:
     Coin_1[16] Coin_2[22] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2224:
     Coin_1[16] Coin_2[22] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2225:
     Coin_1[16] Coin_2[27] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2226:
     Coin_1[16] Coin_2[27] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2227:
     Coin_1[16] Coin_2[27] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2228:
     Coin_1[16] Coin_2[27] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2229:
     Coin_1[16] Coin_2[27] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2230:
     Coin_1[16] Coin_2[27] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2231:
     Coin_1[16] Coin_2[32] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2232:
     Coin_1[16] Coin_2[32] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2233:
     Coin_1[16] Coin_2[32] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2234:
     Coin_1[16] Coin_2[32] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2235:
     Coin_1[16] Coin_2[37] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2236:
     Coin_1[16] Coin_2[37] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2237:
     Coin_1[16] Coin_2[42] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2238:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2239:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2240:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2241:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2242:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2243:
     Coin_1[17] Coin_2[4] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2244:
     Coin_1[17] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2245:
     Coin_1[17] Coin_2[4] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2246:
     Coin_1[17] Coin_2[4] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2247:
     Coin_1[17] Coin_2[4] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2248:
     Coin_1[17] Coin_2[4] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2249:
     Coin_1[17] Coin_2[4] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2250:
     Coin_1[17] Coin_2[4] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2251:
     Coin_1[17] Coin_2[4] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2252:
     Coin_1[17] Coin_2[4] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2253:
     Coin_1[17] Coin_2[4] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2254:
     Coin_1[17] Coin_2[4] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2255:
     Coin_1[17] Coin_2[4] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2256:
     Coin_1[17] Coin_2[4] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2257:
     Coin_1[17] Coin_2[4] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2258:
     Coin_1[17] Coin_2[4] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2259:
     Coin_1[17] Coin_2[4] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2260:
     Coin_1[17] Coin_2[4] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2261:
     Coin_1[17] Coin_2[4] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2262:
     Coin_1[17] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2263:
     Coin_1[17] Coin_2[9] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2264:
     Coin_1[17] Coin_2[9] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2265:
     Coin_1[17] Coin_2[9] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2266:
     Coin_1[17] Coin_2[9] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2267:
     Coin_1[17] Coin_2[9] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2268:
     Coin_1[17] Coin_2[9] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2269:
     Coin_1[17] Coin_2[9] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2270:
     Coin_1[17] Coin_2[9] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2271:
     Coin_1[17] Coin_2[9] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2272:
     Coin_1[17] Coin_2[9] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2273:
     Coin_1[17] Coin_2[9] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2274:
     Coin_1[17] Coin_2[9] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2275:
     Coin_1[17] Coin_2[9] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2276:
     Coin_1[17] Coin_2[9] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2277:
     Coin_1[17] Coin_2[9] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2278:
     Coin_1[17] Coin_2[9] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2279:
     Coin_1[17] Coin_2[9] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2280:
     Coin_1[17] Coin_2[14] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2281:
     Coin_1[17] Coin_2[14] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2282:
     Coin_1[17] Coin_2[14] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2283:
     Coin_1[17] Coin_2[14] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2284:
     Coin_1[17] Coin_2[14] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2285:
     Coin_1[17] Coin_2[14] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2286:
     Coin_1[17] Coin_2[14] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2287:
     Coin_1[17] Coin_2[14] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2288:
     Coin_1[17] Coin_2[14] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2289:
     Coin_1[17] Coin_2[14] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2290:
     Coin_1[17] Coin_2[14] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2291:
     Coin_1[17] Coin_2[14] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2292:
     Coin_1[17] Coin_2[14] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2293:
     Coin_1[17] Coin_2[19] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2294:
     Coin_1[17] Coin_2[19] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2295:
     Coin_1[17] Coin_2[19] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2296:
     Coin_1[17] Coin_2[19] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2297:
     Coin_1[17] Coin_2[19] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2298:
     Coin_1[17] Coin_2[19] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2299:
     Coin_1[17] Coin_2[19] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2300:
     Coin_1[17] Coin_2[19] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2301:
     Coin_1[17] Coin_2[19] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2302:
     Coin_1[17] Coin_2[24] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2303:
     Coin_1[17] Coin_2[24] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2304:
     Coin_1[17] Coin_2[24] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2305:
     Coin_1[17] Coin_2[24] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2306:
     Coin_1[17] Coin_2[24] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2307:
     Coin_1[17] Coin_2[24] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2308:
     Coin_1[17] Coin_2[29] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2309:
     Coin_1[17] Coin_2[29] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2310:
     Coin_1[17] Coin_2[29] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2311:
     Coin_1[17] Coin_2[29] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2312:
     Coin_1[17] Coin_2[34] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2313:
     Coin_1[17] Coin_2[34] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2314:
     Coin_1[17] Coin_2[39] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2315:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #2316:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2317:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2318:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2319:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2320:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2321:
     Coin_1[18] Coin_2[1] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2322:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2323:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2324:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2325:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2326:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2327:
     Coin_1[18] Coin_2[1] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2328:
     Coin_1[18] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2329:
     Coin_1[18] Coin_2[1] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2330:
     Coin_1[18] Coin_2[1] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2331:
     Coin_1[18] Coin_2[1] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2332:
     Coin_1[18] Coin_2[1] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2333:
     Coin_1[18] Coin_2[1] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2334:
     Coin_1[18] Coin_2[1] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2335:
     Coin_1[18] Coin_2[1] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2336:
     Coin_1[18] Coin_2[1] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2337:
     Coin_1[18] Coin_2[1] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2338:
     Coin_1[18] Coin_2[1] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2339:
     Coin_1[18] Coin_2[1] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2340:
     Coin_1[18] Coin_2[1] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2341:
     Coin_1[18] Coin_2[1] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2342:
     Coin_1[18] Coin_2[1] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2343:
     Coin_1[18] Coin_2[1] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2344:
     Coin_1[18] Coin_2[1] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2345:
     Coin_1[18] Coin_2[1] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2346:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2347:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2348:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2349:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2350:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2351:
     Coin_1[18] Coin_2[6] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2352:
     Coin_1[18] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2353:
     Coin_1[18] Coin_2[6] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2354:
     Coin_1[18] Coin_2[6] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2355:
     Coin_1[18] Coin_2[6] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2356:
     Coin_1[18] Coin_2[6] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2357:
     Coin_1[18] Coin_2[6] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2358:
     Coin_1[18] Coin_2[6] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2359:
     Coin_1[18] Coin_2[6] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2360:
     Coin_1[18] Coin_2[6] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2361:
     Coin_1[18] Coin_2[6] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2362:
     Coin_1[18] Coin_2[6] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2363:
     Coin_1[18] Coin_2[6] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2364:
     Coin_1[18] Coin_2[6] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2365:
     Coin_1[18] Coin_2[6] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2366:
     Coin_1[18] Coin_2[6] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2367:
     Coin_1[18] Coin_2[6] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2368:
     Coin_1[18] Coin_2[6] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2369:
     Coin_1[18] Coin_2[6] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2370:
     Coin_1[18] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2371:
     Coin_1[18] Coin_2[11] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2372:
     Coin_1[18] Coin_2[11] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2373:
     Coin_1[18] Coin_2[11] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2374:
     Coin_1[18] Coin_2[11] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2375:
     Coin_1[18] Coin_2[11] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2376:
     Coin_1[18] Coin_2[11] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2377:
     Coin_1[18] Coin_2[11] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2378:
     Coin_1[18] Coin_2[11] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2379:
     Coin_1[18] Coin_2[11] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2380:
     Coin_1[18] Coin_2[11] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2381:
     Coin_1[18] Coin_2[11] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2382:
     Coin_1[18] Coin_2[11] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2383:
     Coin_1[18] Coin_2[11] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2384:
     Coin_1[18] Coin_2[11] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2385:
     Coin_1[18] Coin_2[11] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2386:
     Coin_1[18] Coin_2[11] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2387:
     Coin_1[18] Coin_2[11] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2388:
     Coin_1[18] Coin_2[16] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2389:
     Coin_1[18] Coin_2[16] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2390:
     Coin_1[18] Coin_2[16] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2391:
     Coin_1[18] Coin_2[16] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2392:
     Coin_1[18] Coin_2[16] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2393:
     Coin_1[18] Coin_2[16] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2394:
     Coin_1[18] Coin_2[16] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2395:
     Coin_1[18] Coin_2[16] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2396:
     Coin_1[18] Coin_2[16] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2397:
     Coin_1[18] Coin_2[16] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2398:
     Coin_1[18] Coin_2[16] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2399:
     Coin_1[18] Coin_2[16] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2400:
     Coin_1[18] Coin_2[16] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2401:
     Coin_1[18] Coin_2[21] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2402:
     Coin_1[18] Coin_2[21] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2403:
     Coin_1[18] Coin_2[21] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2404:
     Coin_1[18] Coin_2[21] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2405:
     Coin_1[18] Coin_2[21] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2406:
     Coin_1[18] Coin_2[21] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2407:
     Coin_1[18] Coin_2[21] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2408:
     Coin_1[18] Coin_2[21] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2409:
     Coin_1[18] Coin_2[21] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2410:
     Coin_1[18] Coin_2[26] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2411:
     Coin_1[18] Coin_2[26] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2412:
     Coin_1[18] Coin_2[26] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2413:
     Coin_1[18] Coin_2[26] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2414:
     Coin_1[18] Coin_2[26] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2415:
     Coin_1[18] Coin_2[26] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2416:
     Coin_1[18] Coin_2[31] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2417:
     Coin_1[18] Coin_2[31] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2418:
     Coin_1[18] Coin_2[31] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2419:
     Coin_1[18] Coin_2[31] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2420:
     Coin_1[18] Coin_2[36] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2421:
     Coin_1[18] Coin_2[36] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2422:
     Coin_1[18] Coin_2[41] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2423:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2424:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2425:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2426:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2427:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2428:
     Coin_1[19] Coin_2[3] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2429:
     Coin_1[19] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2430:
     Coin_1[19] Coin_2[3] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2431:
     Coin_1[19] Coin_2[3] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2432:
     Coin_1[19] Coin_2[3] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2433:
     Coin_1[19] Coin_2[3] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2434:
     Coin_1[19] Coin_2[3] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2435:
     Coin_1[19] Coin_2[3] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2436:
     Coin_1[19] Coin_2[3] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2437:
     Coin_1[19] Coin_2[3] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2438:
     Coin_1[19] Coin_2[3] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2439:
     Coin_1[19] Coin_2[3] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2440:
     Coin_1[19] Coin_2[3] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2441:
     Coin_1[19] Coin_2[3] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2442:
     Coin_1[19] Coin_2[3] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2443:
     Coin_1[19] Coin_2[3] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2444:
     Coin_1[19] Coin_2[3] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2445:
     Coin_1[19] Coin_2[3] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2446:
     Coin_1[19] Coin_2[3] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2447:
     Coin_1[19] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2448:
     Coin_1[19] Coin_2[8] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2449:
     Coin_1[19] Coin_2[8] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2450:
     Coin_1[19] Coin_2[8] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2451:
     Coin_1[19] Coin_2[8] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2452:
     Coin_1[19] Coin_2[8] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2453:
     Coin_1[19] Coin_2[8] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2454:
     Coin_1[19] Coin_2[8] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2455:
     Coin_1[19] Coin_2[8] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2456:
     Coin_1[19] Coin_2[8] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2457:
     Coin_1[19] Coin_2[8] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2458:
     Coin_1[19] Coin_2[8] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2459:
     Coin_1[19] Coin_2[8] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2460:
     Coin_1[19] Coin_2[8] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2461:
     Coin_1[19] Coin_2[8] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2462:
     Coin_1[19] Coin_2[8] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2463:
     Coin_1[19] Coin_2[8] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2464:
     Coin_1[19] Coin_2[8] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2465:
     Coin_1[19] Coin_2[13] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2466:
     Coin_1[19] Coin_2[13] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2467:
     Coin_1[19] Coin_2[13] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2468:
     Coin_1[19] Coin_2[13] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2469:
     Coin_1[19] Coin_2[13] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2470:
     Coin_1[19] Coin_2[13] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2471:
     Coin_1[19] Coin_2[13] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2472:
     Coin_1[19] Coin_2[13] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2473:
     Coin_1[19] Coin_2[13] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2474:
     Coin_1[19] Coin_2[13] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2475:
     Coin_1[19] Coin_2[13] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2476:
     Coin_1[19] Coin_2[13] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2477:
     Coin_1[19] Coin_2[13] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2478:
     Coin_1[19] Coin_2[18] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2479:
     Coin_1[19] Coin_2[18] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2480:
     Coin_1[19] Coin_2[18] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2481:
     Coin_1[19] Coin_2[18] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2482:
     Coin_1[19] Coin_2[18] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2483:
     Coin_1[19] Coin_2[18] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2484:
     Coin_1[19] Coin_2[18] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2485:
     Coin_1[19] Coin_2[18] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2486:
     Coin_1[19] Coin_2[18] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2487:
     Coin_1[19] Coin_2[23] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2488:
     Coin_1[19] Coin_2[23] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2489:
     Coin_1[19] Coin_2[23] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2490:
     Coin_1[19] Coin_2[23] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2491:
     Coin_1[19] Coin_2[23] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2492:
     Coin_1[19] Coin_2[23] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2493:
     Coin_1[19] Coin_2[28] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2494:
     Coin_1[19] Coin_2[28] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2495:
     Coin_1[19] Coin_2[28] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2496:
     Coin_1[19] Coin_2[28] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2497:
     Coin_1[19] Coin_2[33] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2498:
     Coin_1[19] Coin_2[33] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2499:
     Coin_1[19] Coin_2[38] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2500:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[4] Coin_50[0] Coin_100[0]
Solution #2501:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2502:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2503:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2504:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[4] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2505:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[6] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2506:
     Coin_1[20] Coin_2[0] Coin_5[0] Coin_10[8] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2507:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2508:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2509:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2510:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2511:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2512:
     Coin_1[20] Coin_2[0] Coin_5[2] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2513:
     Coin_1[20] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2514:
     Coin_1[20] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2515:
     Coin_1[20] Coin_2[0] Coin_5[4] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2516:
     Coin_1[20] Coin_2[0] Coin_5[4] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2517:
     Coin_1[20] Coin_2[0] Coin_5[4] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2518:
     Coin_1[20] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2519:
     Coin_1[20] Coin_2[0] Coin_5[6] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2520:
     Coin_1[20] Coin_2[0] Coin_5[6] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2521:
     Coin_1[20] Coin_2[0] Coin_5[6] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2522:
     Coin_1[20] Coin_2[0] Coin_5[8] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2523:
     Coin_1[20] Coin_2[0] Coin_5[8] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2524:
     Coin_1[20] Coin_2[0] Coin_5[8] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2525:
     Coin_1[20] Coin_2[0] Coin_5[10] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2526:
     Coin_1[20] Coin_2[0] Coin_5[10] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2527:
     Coin_1[20] Coin_2[0] Coin_5[12] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2528:
     Coin_1[20] Coin_2[0] Coin_5[12] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2529:
     Coin_1[20] Coin_2[0] Coin_5[14] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2530:
     Coin_1[20] Coin_2[0] Coin_5[16] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2531:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2532:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2533:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2534:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2535:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2536:
     Coin_1[20] Coin_2[5] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2537:
     Coin_1[20] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2538:
     Coin_1[20] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2539:
     Coin_1[20] Coin_2[5] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2540:
     Coin_1[20] Coin_2[5] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2541:
     Coin_1[20] Coin_2[5] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2542:
     Coin_1[20] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2543:
     Coin_1[20] Coin_2[5] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2544:
     Coin_1[20] Coin_2[5] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2545:
     Coin_1[20] Coin_2[5] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2546:
     Coin_1[20] Coin_2[5] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2547:
     Coin_1[20] Coin_2[5] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2548:
     Coin_1[20] Coin_2[5] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2549:
     Coin_1[20] Coin_2[5] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2550:
     Coin_1[20] Coin_2[5] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2551:
     Coin_1[20] Coin_2[5] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2552:
     Coin_1[20] Coin_2[5] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2553:
     Coin_1[20] Coin_2[5] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2554:
     Coin_1[20] Coin_2[5] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2555:
     Coin_1[20] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2556:
     Coin_1[20] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2557:
     Coin_1[20] Coin_2[10] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2558:
     Coin_1[20] Coin_2[10] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2559:
     Coin_1[20] Coin_2[10] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2560:
     Coin_1[20] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2561:
     Coin_1[20] Coin_2[10] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2562:
     Coin_1[20] Coin_2[10] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2563:
     Coin_1[20] Coin_2[10] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2564:
     Coin_1[20] Coin_2[10] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2565:
     Coin_1[20] Coin_2[10] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2566:
     Coin_1[20] Coin_2[10] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2567:
     Coin_1[20] Coin_2[10] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2568:
     Coin_1[20] Coin_2[10] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2569:
     Coin_1[20] Coin_2[10] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2570:
     Coin_1[20] Coin_2[10] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2571:
     Coin_1[20] Coin_2[10] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2572:
     Coin_1[20] Coin_2[10] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2573:
     Coin_1[20] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2574:
     Coin_1[20] Coin_2[15] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2575:
     Coin_1[20] Coin_2[15] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2576:
     Coin_1[20] Coin_2[15] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2577:
     Coin_1[20] Coin_2[15] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2578:
     Coin_1[20] Coin_2[15] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2579:
     Coin_1[20] Coin_2[15] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2580:
     Coin_1[20] Coin_2[15] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2581:
     Coin_1[20] Coin_2[15] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2582:
     Coin_1[20] Coin_2[15] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2583:
     Coin_1[20] Coin_2[15] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2584:
     Coin_1[20] Coin_2[15] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2585:
     Coin_1[20] Coin_2[15] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2586:
     Coin_1[20] Coin_2[20] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2587:
     Coin_1[20] Coin_2[20] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2588:
     Coin_1[20] Coin_2[20] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2589:
     Coin_1[20] Coin_2[20] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2590:
     Coin_1[20] Coin_2[20] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2591:
     Coin_1[20] Coin_2[20] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2592:
     Coin_1[20] Coin_2[20] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2593:
     Coin_1[20] Coin_2[20] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2594:
     Coin_1[20] Coin_2[20] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2595:
     Coin_1[20] Coin_2[25] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2596:
     Coin_1[20] Coin_2[25] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2597:
     Coin_1[20] Coin_2[25] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2598:
     Coin_1[20] Coin_2[25] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2599:
     Coin_1[20] Coin_2[25] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2600:
     Coin_1[20] Coin_2[25] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2601:
     Coin_1[20] Coin_2[30] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2602:
     Coin_1[20] Coin_2[30] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2603:
     Coin_1[20] Coin_2[30] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2604:
     Coin_1[20] Coin_2[30] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2605:
     Coin_1[20] Coin_2[35] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2606:
     Coin_1[20] Coin_2[35] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2607:
     Coin_1[20] Coin_2[40] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2608:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2609:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2610:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2611:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2612:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2613:
     Coin_1[21] Coin_2[2] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2614:
     Coin_1[21] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2615:
     Coin_1[21] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2616:
     Coin_1[21] Coin_2[2] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2617:
     Coin_1[21] Coin_2[2] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2618:
     Coin_1[21] Coin_2[2] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2619:
     Coin_1[21] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2620:
     Coin_1[21] Coin_2[2] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2621:
     Coin_1[21] Coin_2[2] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2622:
     Coin_1[21] Coin_2[2] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2623:
     Coin_1[21] Coin_2[2] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2624:
     Coin_1[21] Coin_2[2] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2625:
     Coin_1[21] Coin_2[2] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2626:
     Coin_1[21] Coin_2[2] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2627:
     Coin_1[21] Coin_2[2] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2628:
     Coin_1[21] Coin_2[2] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2629:
     Coin_1[21] Coin_2[2] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2630:
     Coin_1[21] Coin_2[2] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2631:
     Coin_1[21] Coin_2[2] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2632:
     Coin_1[21] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2633:
     Coin_1[21] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2634:
     Coin_1[21] Coin_2[7] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2635:
     Coin_1[21] Coin_2[7] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2636:
     Coin_1[21] Coin_2[7] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2637:
     Coin_1[21] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2638:
     Coin_1[21] Coin_2[7] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2639:
     Coin_1[21] Coin_2[7] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2640:
     Coin_1[21] Coin_2[7] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2641:
     Coin_1[21] Coin_2[7] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2642:
     Coin_1[21] Coin_2[7] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2643:
     Coin_1[21] Coin_2[7] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2644:
     Coin_1[21] Coin_2[7] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2645:
     Coin_1[21] Coin_2[7] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2646:
     Coin_1[21] Coin_2[7] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2647:
     Coin_1[21] Coin_2[7] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2648:
     Coin_1[21] Coin_2[7] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2649:
     Coin_1[21] Coin_2[7] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2650:
     Coin_1[21] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2651:
     Coin_1[21] Coin_2[12] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2652:
     Coin_1[21] Coin_2[12] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2653:
     Coin_1[21] Coin_2[12] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2654:
     Coin_1[21] Coin_2[12] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2655:
     Coin_1[21] Coin_2[12] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2656:
     Coin_1[21] Coin_2[12] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2657:
     Coin_1[21] Coin_2[12] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2658:
     Coin_1[21] Coin_2[12] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2659:
     Coin_1[21] Coin_2[12] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2660:
     Coin_1[21] Coin_2[12] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2661:
     Coin_1[21] Coin_2[12] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2662:
     Coin_1[21] Coin_2[12] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2663:
     Coin_1[21] Coin_2[17] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2664:
     Coin_1[21] Coin_2[17] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2665:
     Coin_1[21] Coin_2[17] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2666:
     Coin_1[21] Coin_2[17] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2667:
     Coin_1[21] Coin_2[17] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2668:
     Coin_1[21] Coin_2[17] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2669:
     Coin_1[21] Coin_2[17] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2670:
     Coin_1[21] Coin_2[17] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2671:
     Coin_1[21] Coin_2[17] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2672:
     Coin_1[21] Coin_2[22] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2673:
     Coin_1[21] Coin_2[22] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2674:
     Coin_1[21] Coin_2[22] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2675:
     Coin_1[21] Coin_2[22] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2676:
     Coin_1[21] Coin_2[22] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2677:
     Coin_1[21] Coin_2[22] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2678:
     Coin_1[21] Coin_2[27] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2679:
     Coin_1[21] Coin_2[27] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2680:
     Coin_1[21] Coin_2[27] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2681:
     Coin_1[21] Coin_2[27] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2682:
     Coin_1[21] Coin_2[32] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2683:
     Coin_1[21] Coin_2[32] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2684:
     Coin_1[21] Coin_2[37] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2685:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2686:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2687:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2688:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2689:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2690:
     Coin_1[22] Coin_2[4] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2691:
     Coin_1[22] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2692:
     Coin_1[22] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2693:
     Coin_1[22] Coin_2[4] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2694:
     Coin_1[22] Coin_2[4] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2695:
     Coin_1[22] Coin_2[4] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2696:
     Coin_1[22] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2697:
     Coin_1[22] Coin_2[4] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2698:
     Coin_1[22] Coin_2[4] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2699:
     Coin_1[22] Coin_2[4] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2700:
     Coin_1[22] Coin_2[4] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2701:
     Coin_1[22] Coin_2[4] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2702:
     Coin_1[22] Coin_2[4] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2703:
     Coin_1[22] Coin_2[4] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2704:
     Coin_1[22] Coin_2[4] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2705:
     Coin_1[22] Coin_2[4] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2706:
     Coin_1[22] Coin_2[4] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2707:
     Coin_1[22] Coin_2[4] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2708:
     Coin_1[22] Coin_2[4] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2709:
     Coin_1[22] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2710:
     Coin_1[22] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2711:
     Coin_1[22] Coin_2[9] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2712:
     Coin_1[22] Coin_2[9] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2713:
     Coin_1[22] Coin_2[9] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2714:
     Coin_1[22] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2715:
     Coin_1[22] Coin_2[9] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2716:
     Coin_1[22] Coin_2[9] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2717:
     Coin_1[22] Coin_2[9] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2718:
     Coin_1[22] Coin_2[9] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2719:
     Coin_1[22] Coin_2[9] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2720:
     Coin_1[22] Coin_2[9] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2721:
     Coin_1[22] Coin_2[9] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2722:
     Coin_1[22] Coin_2[9] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2723:
     Coin_1[22] Coin_2[9] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2724:
     Coin_1[22] Coin_2[9] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2725:
     Coin_1[22] Coin_2[9] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2726:
     Coin_1[22] Coin_2[9] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2727:
     Coin_1[22] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2728:
     Coin_1[22] Coin_2[14] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2729:
     Coin_1[22] Coin_2[14] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2730:
     Coin_1[22] Coin_2[14] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2731:
     Coin_1[22] Coin_2[14] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2732:
     Coin_1[22] Coin_2[14] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2733:
     Coin_1[22] Coin_2[14] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2734:
     Coin_1[22] Coin_2[14] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2735:
     Coin_1[22] Coin_2[14] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2736:
     Coin_1[22] Coin_2[14] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2737:
     Coin_1[22] Coin_2[14] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2738:
     Coin_1[22] Coin_2[14] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2739:
     Coin_1[22] Coin_2[14] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2740:
     Coin_1[22] Coin_2[19] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2741:
     Coin_1[22] Coin_2[19] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2742:
     Coin_1[22] Coin_2[19] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2743:
     Coin_1[22] Coin_2[19] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2744:
     Coin_1[22] Coin_2[19] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2745:
     Coin_1[22] Coin_2[19] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2746:
     Coin_1[22] Coin_2[19] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2747:
     Coin_1[22] Coin_2[19] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2748:
     Coin_1[22] Coin_2[19] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2749:
     Coin_1[22] Coin_2[24] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2750:
     Coin_1[22] Coin_2[24] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2751:
     Coin_1[22] Coin_2[24] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2752:
     Coin_1[22] Coin_2[24] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2753:
     Coin_1[22] Coin_2[24] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2754:
     Coin_1[22] Coin_2[24] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2755:
     Coin_1[22] Coin_2[29] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2756:
     Coin_1[22] Coin_2[29] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2757:
     Coin_1[22] Coin_2[29] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2758:
     Coin_1[22] Coin_2[29] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2759:
     Coin_1[22] Coin_2[34] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2760:
     Coin_1[22] Coin_2[34] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2761:
     Coin_1[22] Coin_2[39] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2762:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2763:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2764:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2765:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2766:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2767:
     Coin_1[23] Coin_2[1] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2768:
     Coin_1[23] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2769:
     Coin_1[23] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2770:
     Coin_1[23] Coin_2[1] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2771:
     Coin_1[23] Coin_2[1] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2772:
     Coin_1[23] Coin_2[1] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2773:
     Coin_1[23] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2774:
     Coin_1[23] Coin_2[1] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2775:
     Coin_1[23] Coin_2[1] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2776:
     Coin_1[23] Coin_2[1] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2777:
     Coin_1[23] Coin_2[1] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2778:
     Coin_1[23] Coin_2[1] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2779:
     Coin_1[23] Coin_2[1] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2780:
     Coin_1[23] Coin_2[1] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2781:
     Coin_1[23] Coin_2[1] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2782:
     Coin_1[23] Coin_2[1] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2783:
     Coin_1[23] Coin_2[1] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2784:
     Coin_1[23] Coin_2[1] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2785:
     Coin_1[23] Coin_2[1] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2786:
     Coin_1[23] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2787:
     Coin_1[23] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2788:
     Coin_1[23] Coin_2[6] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2789:
     Coin_1[23] Coin_2[6] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2790:
     Coin_1[23] Coin_2[6] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2791:
     Coin_1[23] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2792:
     Coin_1[23] Coin_2[6] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2793:
     Coin_1[23] Coin_2[6] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2794:
     Coin_1[23] Coin_2[6] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2795:
     Coin_1[23] Coin_2[6] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2796:
     Coin_1[23] Coin_2[6] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2797:
     Coin_1[23] Coin_2[6] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2798:
     Coin_1[23] Coin_2[6] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2799:
     Coin_1[23] Coin_2[6] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2800:
     Coin_1[23] Coin_2[6] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2801:
     Coin_1[23] Coin_2[6] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2802:
     Coin_1[23] Coin_2[6] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2803:
     Coin_1[23] Coin_2[6] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2804:
     Coin_1[23] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2805:
     Coin_1[23] Coin_2[11] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2806:
     Coin_1[23] Coin_2[11] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2807:
     Coin_1[23] Coin_2[11] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2808:
     Coin_1[23] Coin_2[11] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2809:
     Coin_1[23] Coin_2[11] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2810:
     Coin_1[23] Coin_2[11] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2811:
     Coin_1[23] Coin_2[11] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2812:
     Coin_1[23] Coin_2[11] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2813:
     Coin_1[23] Coin_2[11] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2814:
     Coin_1[23] Coin_2[11] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2815:
     Coin_1[23] Coin_2[11] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2816:
     Coin_1[23] Coin_2[11] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2817:
     Coin_1[23] Coin_2[16] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2818:
     Coin_1[23] Coin_2[16] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2819:
     Coin_1[23] Coin_2[16] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2820:
     Coin_1[23] Coin_2[16] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2821:
     Coin_1[23] Coin_2[16] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2822:
     Coin_1[23] Coin_2[16] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2823:
     Coin_1[23] Coin_2[16] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2824:
     Coin_1[23] Coin_2[16] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2825:
     Coin_1[23] Coin_2[16] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2826:
     Coin_1[23] Coin_2[21] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2827:
     Coin_1[23] Coin_2[21] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2828:
     Coin_1[23] Coin_2[21] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2829:
     Coin_1[23] Coin_2[21] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2830:
     Coin_1[23] Coin_2[21] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2831:
     Coin_1[23] Coin_2[21] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2832:
     Coin_1[23] Coin_2[26] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2833:
     Coin_1[23] Coin_2[26] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2834:
     Coin_1[23] Coin_2[26] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2835:
     Coin_1[23] Coin_2[26] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2836:
     Coin_1[23] Coin_2[31] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2837:
     Coin_1[23] Coin_2[31] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2838:
     Coin_1[23] Coin_2[36] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2839:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2840:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2841:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2842:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2843:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2844:
     Coin_1[24] Coin_2[3] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2845:
     Coin_1[24] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2846:
     Coin_1[24] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2847:
     Coin_1[24] Coin_2[3] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2848:
     Coin_1[24] Coin_2[3] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2849:
     Coin_1[24] Coin_2[3] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2850:
     Coin_1[24] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2851:
     Coin_1[24] Coin_2[3] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2852:
     Coin_1[24] Coin_2[3] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2853:
     Coin_1[24] Coin_2[3] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2854:
     Coin_1[24] Coin_2[3] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2855:
     Coin_1[24] Coin_2[3] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2856:
     Coin_1[24] Coin_2[3] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2857:
     Coin_1[24] Coin_2[3] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2858:
     Coin_1[24] Coin_2[3] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2859:
     Coin_1[24] Coin_2[3] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2860:
     Coin_1[24] Coin_2[3] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2861:
     Coin_1[24] Coin_2[3] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2862:
     Coin_1[24] Coin_2[3] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2863:
     Coin_1[24] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2864:
     Coin_1[24] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2865:
     Coin_1[24] Coin_2[8] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2866:
     Coin_1[24] Coin_2[8] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2867:
     Coin_1[24] Coin_2[8] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2868:
     Coin_1[24] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2869:
     Coin_1[24] Coin_2[8] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2870:
     Coin_1[24] Coin_2[8] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2871:
     Coin_1[24] Coin_2[8] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2872:
     Coin_1[24] Coin_2[8] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2873:
     Coin_1[24] Coin_2[8] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2874:
     Coin_1[24] Coin_2[8] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2875:
     Coin_1[24] Coin_2[8] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2876:
     Coin_1[24] Coin_2[8] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2877:
     Coin_1[24] Coin_2[8] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2878:
     Coin_1[24] Coin_2[8] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2879:
     Coin_1[24] Coin_2[8] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2880:
     Coin_1[24] Coin_2[8] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2881:
     Coin_1[24] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2882:
     Coin_1[24] Coin_2[13] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2883:
     Coin_1[24] Coin_2[13] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2884:
     Coin_1[24] Coin_2[13] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2885:
     Coin_1[24] Coin_2[13] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2886:
     Coin_1[24] Coin_2[13] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2887:
     Coin_1[24] Coin_2[13] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2888:
     Coin_1[24] Coin_2[13] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2889:
     Coin_1[24] Coin_2[13] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2890:
     Coin_1[24] Coin_2[13] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2891:
     Coin_1[24] Coin_2[13] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2892:
     Coin_1[24] Coin_2[13] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2893:
     Coin_1[24] Coin_2[13] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2894:
     Coin_1[24] Coin_2[18] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2895:
     Coin_1[24] Coin_2[18] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2896:
     Coin_1[24] Coin_2[18] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2897:
     Coin_1[24] Coin_2[18] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2898:
     Coin_1[24] Coin_2[18] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2899:
     Coin_1[24] Coin_2[18] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2900:
     Coin_1[24] Coin_2[18] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2901:
     Coin_1[24] Coin_2[18] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2902:
     Coin_1[24] Coin_2[18] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2903:
     Coin_1[24] Coin_2[23] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2904:
     Coin_1[24] Coin_2[23] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2905:
     Coin_1[24] Coin_2[23] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2906:
     Coin_1[24] Coin_2[23] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2907:
     Coin_1[24] Coin_2[23] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2908:
     Coin_1[24] Coin_2[23] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2909:
     Coin_1[24] Coin_2[28] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2910:
     Coin_1[24] Coin_2[28] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2911:
     Coin_1[24] Coin_2[28] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2912:
     Coin_1[24] Coin_2[28] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2913:
     Coin_1[24] Coin_2[33] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2914:
     Coin_1[24] Coin_2[33] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2915:
     Coin_1[24] Coin_2[38] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2916:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2917:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2918:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2919:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2920:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2921:
     Coin_1[25] Coin_2[0] Coin_5[1] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2922:
     Coin_1[25] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2923:
     Coin_1[25] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2924:
     Coin_1[25] Coin_2[0] Coin_5[3] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2925:
     Coin_1[25] Coin_2[0] Coin_5[3] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2926:
     Coin_1[25] Coin_2[0] Coin_5[3] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2927:
     Coin_1[25] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2928:
     Coin_1[25] Coin_2[0] Coin_5[5] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2929:
     Coin_1[25] Coin_2[0] Coin_5[5] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2930:
     Coin_1[25] Coin_2[0] Coin_5[5] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2931:
     Coin_1[25] Coin_2[0] Coin_5[7] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2932:
     Coin_1[25] Coin_2[0] Coin_5[7] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2933:
     Coin_1[25] Coin_2[0] Coin_5[7] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2934:
     Coin_1[25] Coin_2[0] Coin_5[9] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2935:
     Coin_1[25] Coin_2[0] Coin_5[9] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2936:
     Coin_1[25] Coin_2[0] Coin_5[11] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2937:
     Coin_1[25] Coin_2[0] Coin_5[11] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2938:
     Coin_1[25] Coin_2[0] Coin_5[13] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2939:
     Coin_1[25] Coin_2[0] Coin_5[15] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2940:
     Coin_1[25] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2941:
     Coin_1[25] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2942:
     Coin_1[25] Coin_2[5] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2943:
     Coin_1[25] Coin_2[5] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2944:
     Coin_1[25] Coin_2[5] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2945:
     Coin_1[25] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2946:
     Coin_1[25] Coin_2[5] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2947:
     Coin_1[25] Coin_2[5] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2948:
     Coin_1[25] Coin_2[5] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2949:
     Coin_1[25] Coin_2[5] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2950:
     Coin_1[25] Coin_2[5] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2951:
     Coin_1[25] Coin_2[5] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2952:
     Coin_1[25] Coin_2[5] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2953:
     Coin_1[25] Coin_2[5] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2954:
     Coin_1[25] Coin_2[5] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2955:
     Coin_1[25] Coin_2[5] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2956:
     Coin_1[25] Coin_2[5] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2957:
     Coin_1[25] Coin_2[5] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2958:
     Coin_1[25] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2959:
     Coin_1[25] Coin_2[10] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2960:
     Coin_1[25] Coin_2[10] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2961:
     Coin_1[25] Coin_2[10] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2962:
     Coin_1[25] Coin_2[10] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2963:
     Coin_1[25] Coin_2[10] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2964:
     Coin_1[25] Coin_2[10] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2965:
     Coin_1[25] Coin_2[10] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2966:
     Coin_1[25] Coin_2[10] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2967:
     Coin_1[25] Coin_2[10] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2968:
     Coin_1[25] Coin_2[10] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2969:
     Coin_1[25] Coin_2[10] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2970:
     Coin_1[25] Coin_2[10] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2971:
     Coin_1[25] Coin_2[15] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2972:
     Coin_1[25] Coin_2[15] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2973:
     Coin_1[25] Coin_2[15] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2974:
     Coin_1[25] Coin_2[15] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2975:
     Coin_1[25] Coin_2[15] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2976:
     Coin_1[25] Coin_2[15] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2977:
     Coin_1[25] Coin_2[15] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2978:
     Coin_1[25] Coin_2[15] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2979:
     Coin_1[25] Coin_2[15] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2980:
     Coin_1[25] Coin_2[20] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2981:
     Coin_1[25] Coin_2[20] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2982:
     Coin_1[25] Coin_2[20] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2983:
     Coin_1[25] Coin_2[20] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2984:
     Coin_1[25] Coin_2[20] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2985:
     Coin_1[25] Coin_2[20] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2986:
     Coin_1[25] Coin_2[25] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2987:
     Coin_1[25] Coin_2[25] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2988:
     Coin_1[25] Coin_2[25] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2989:
     Coin_1[25] Coin_2[25] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2990:
     Coin_1[25] Coin_2[30] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2991:
     Coin_1[25] Coin_2[30] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2992:
     Coin_1[25] Coin_2[35] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2993:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #2994:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #2995:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #2996:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #2997:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #2998:
     Coin_1[26] Coin_2[2] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #2999:
     Coin_1[26] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3000:
     Coin_1[26] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3001:
     Coin_1[26] Coin_2[2] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3002:
     Coin_1[26] Coin_2[2] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3003:
     Coin_1[26] Coin_2[2] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3004:
     Coin_1[26] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3005:
     Coin_1[26] Coin_2[2] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3006:
     Coin_1[26] Coin_2[2] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3007:
     Coin_1[26] Coin_2[2] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3008:
     Coin_1[26] Coin_2[2] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3009:
     Coin_1[26] Coin_2[2] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3010:
     Coin_1[26] Coin_2[2] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3011:
     Coin_1[26] Coin_2[2] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3012:
     Coin_1[26] Coin_2[2] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3013:
     Coin_1[26] Coin_2[2] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3014:
     Coin_1[26] Coin_2[2] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3015:
     Coin_1[26] Coin_2[2] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3016:
     Coin_1[26] Coin_2[2] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3017:
     Coin_1[26] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3018:
     Coin_1[26] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3019:
     Coin_1[26] Coin_2[7] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3020:
     Coin_1[26] Coin_2[7] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3021:
     Coin_1[26] Coin_2[7] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3022:
     Coin_1[26] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3023:
     Coin_1[26] Coin_2[7] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3024:
     Coin_1[26] Coin_2[7] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3025:
     Coin_1[26] Coin_2[7] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3026:
     Coin_1[26] Coin_2[7] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3027:
     Coin_1[26] Coin_2[7] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3028:
     Coin_1[26] Coin_2[7] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3029:
     Coin_1[26] Coin_2[7] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3030:
     Coin_1[26] Coin_2[7] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3031:
     Coin_1[26] Coin_2[7] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3032:
     Coin_1[26] Coin_2[7] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3033:
     Coin_1[26] Coin_2[7] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3034:
     Coin_1[26] Coin_2[7] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3035:
     Coin_1[26] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3036:
     Coin_1[26] Coin_2[12] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3037:
     Coin_1[26] Coin_2[12] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3038:
     Coin_1[26] Coin_2[12] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3039:
     Coin_1[26] Coin_2[12] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3040:
     Coin_1[26] Coin_2[12] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3041:
     Coin_1[26] Coin_2[12] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3042:
     Coin_1[26] Coin_2[12] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3043:
     Coin_1[26] Coin_2[12] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3044:
     Coin_1[26] Coin_2[12] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3045:
     Coin_1[26] Coin_2[12] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3046:
     Coin_1[26] Coin_2[12] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3047:
     Coin_1[26] Coin_2[12] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3048:
     Coin_1[26] Coin_2[17] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3049:
     Coin_1[26] Coin_2[17] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3050:
     Coin_1[26] Coin_2[17] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3051:
     Coin_1[26] Coin_2[17] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3052:
     Coin_1[26] Coin_2[17] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3053:
     Coin_1[26] Coin_2[17] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3054:
     Coin_1[26] Coin_2[17] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3055:
     Coin_1[26] Coin_2[17] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3056:
     Coin_1[26] Coin_2[17] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3057:
     Coin_1[26] Coin_2[22] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3058:
     Coin_1[26] Coin_2[22] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3059:
     Coin_1[26] Coin_2[22] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3060:
     Coin_1[26] Coin_2[22] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3061:
     Coin_1[26] Coin_2[22] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3062:
     Coin_1[26] Coin_2[22] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3063:
     Coin_1[26] Coin_2[27] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3064:
     Coin_1[26] Coin_2[27] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3065:
     Coin_1[26] Coin_2[27] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3066:
     Coin_1[26] Coin_2[27] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3067:
     Coin_1[26] Coin_2[32] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3068:
     Coin_1[26] Coin_2[32] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3069:
     Coin_1[26] Coin_2[37] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3070:
     Coin_1[27] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3071:
     Coin_1[27] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3072:
     Coin_1[27] Coin_2[4] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3073:
     Coin_1[27] Coin_2[4] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3074:
     Coin_1[27] Coin_2[4] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3075:
     Coin_1[27] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3076:
     Coin_1[27] Coin_2[4] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3077:
     Coin_1[27] Coin_2[4] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3078:
     Coin_1[27] Coin_2[4] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3079:
     Coin_1[27] Coin_2[4] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3080:
     Coin_1[27] Coin_2[4] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3081:
     Coin_1[27] Coin_2[4] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3082:
     Coin_1[27] Coin_2[4] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3083:
     Coin_1[27] Coin_2[4] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3084:
     Coin_1[27] Coin_2[4] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3085:
     Coin_1[27] Coin_2[4] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3086:
     Coin_1[27] Coin_2[4] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3087:
     Coin_1[27] Coin_2[4] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3088:
     Coin_1[27] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3089:
     Coin_1[27] Coin_2[9] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3090:
     Coin_1[27] Coin_2[9] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3091:
     Coin_1[27] Coin_2[9] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3092:
     Coin_1[27] Coin_2[9] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3093:
     Coin_1[27] Coin_2[9] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3094:
     Coin_1[27] Coin_2[9] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3095:
     Coin_1[27] Coin_2[9] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3096:
     Coin_1[27] Coin_2[9] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3097:
     Coin_1[27] Coin_2[9] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3098:
     Coin_1[27] Coin_2[9] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3099:
     Coin_1[27] Coin_2[9] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3100:
     Coin_1[27] Coin_2[9] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3101:
     Coin_1[27] Coin_2[14] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3102:
     Coin_1[27] Coin_2[14] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3103:
     Coin_1[27] Coin_2[14] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3104:
     Coin_1[27] Coin_2[14] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3105:
     Coin_1[27] Coin_2[14] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3106:
     Coin_1[27] Coin_2[14] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3107:
     Coin_1[27] Coin_2[14] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3108:
     Coin_1[27] Coin_2[14] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3109:
     Coin_1[27] Coin_2[14] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3110:
     Coin_1[27] Coin_2[19] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3111:
     Coin_1[27] Coin_2[19] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3112:
     Coin_1[27] Coin_2[19] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3113:
     Coin_1[27] Coin_2[19] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3114:
     Coin_1[27] Coin_2[19] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3115:
     Coin_1[27] Coin_2[19] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3116:
     Coin_1[27] Coin_2[24] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3117:
     Coin_1[27] Coin_2[24] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3118:
     Coin_1[27] Coin_2[24] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3119:
     Coin_1[27] Coin_2[24] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3120:
     Coin_1[27] Coin_2[29] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3121:
     Coin_1[27] Coin_2[29] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3122:
     Coin_1[27] Coin_2[34] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3123:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #3124:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3125:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3126:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3127:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3128:
     Coin_1[28] Coin_2[1] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3129:
     Coin_1[28] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3130:
     Coin_1[28] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3131:
     Coin_1[28] Coin_2[1] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3132:
     Coin_1[28] Coin_2[1] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3133:
     Coin_1[28] Coin_2[1] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3134:
     Coin_1[28] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3135:
     Coin_1[28] Coin_2[1] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3136:
     Coin_1[28] Coin_2[1] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3137:
     Coin_1[28] Coin_2[1] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3138:
     Coin_1[28] Coin_2[1] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3139:
     Coin_1[28] Coin_2[1] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3140:
     Coin_1[28] Coin_2[1] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3141:
     Coin_1[28] Coin_2[1] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3142:
     Coin_1[28] Coin_2[1] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3143:
     Coin_1[28] Coin_2[1] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3144:
     Coin_1[28] Coin_2[1] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3145:
     Coin_1[28] Coin_2[1] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3146:
     Coin_1[28] Coin_2[1] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3147:
     Coin_1[28] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3148:
     Coin_1[28] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3149:
     Coin_1[28] Coin_2[6] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3150:
     Coin_1[28] Coin_2[6] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3151:
     Coin_1[28] Coin_2[6] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3152:
     Coin_1[28] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3153:
     Coin_1[28] Coin_2[6] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3154:
     Coin_1[28] Coin_2[6] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3155:
     Coin_1[28] Coin_2[6] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3156:
     Coin_1[28] Coin_2[6] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3157:
     Coin_1[28] Coin_2[6] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3158:
     Coin_1[28] Coin_2[6] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3159:
     Coin_1[28] Coin_2[6] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3160:
     Coin_1[28] Coin_2[6] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3161:
     Coin_1[28] Coin_2[6] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3162:
     Coin_1[28] Coin_2[6] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3163:
     Coin_1[28] Coin_2[6] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3164:
     Coin_1[28] Coin_2[6] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3165:
     Coin_1[28] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3166:
     Coin_1[28] Coin_2[11] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3167:
     Coin_1[28] Coin_2[11] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3168:
     Coin_1[28] Coin_2[11] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3169:
     Coin_1[28] Coin_2[11] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3170:
     Coin_1[28] Coin_2[11] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3171:
     Coin_1[28] Coin_2[11] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3172:
     Coin_1[28] Coin_2[11] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3173:
     Coin_1[28] Coin_2[11] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3174:
     Coin_1[28] Coin_2[11] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3175:
     Coin_1[28] Coin_2[11] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3176:
     Coin_1[28] Coin_2[11] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3177:
     Coin_1[28] Coin_2[11] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3178:
     Coin_1[28] Coin_2[16] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3179:
     Coin_1[28] Coin_2[16] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3180:
     Coin_1[28] Coin_2[16] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3181:
     Coin_1[28] Coin_2[16] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3182:
     Coin_1[28] Coin_2[16] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3183:
     Coin_1[28] Coin_2[16] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3184:
     Coin_1[28] Coin_2[16] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3185:
     Coin_1[28] Coin_2[16] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3186:
     Coin_1[28] Coin_2[16] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3187:
     Coin_1[28] Coin_2[21] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3188:
     Coin_1[28] Coin_2[21] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3189:
     Coin_1[28] Coin_2[21] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3190:
     Coin_1[28] Coin_2[21] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3191:
     Coin_1[28] Coin_2[21] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3192:
     Coin_1[28] Coin_2[21] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3193:
     Coin_1[28] Coin_2[26] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3194:
     Coin_1[28] Coin_2[26] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3195:
     Coin_1[28] Coin_2[26] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3196:
     Coin_1[28] Coin_2[26] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3197:
     Coin_1[28] Coin_2[31] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3198:
     Coin_1[28] Coin_2[31] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3199:
     Coin_1[28] Coin_2[36] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3200:
     Coin_1[29] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3201:
     Coin_1[29] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3202:
     Coin_1[29] Coin_2[3] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3203:
     Coin_1[29] Coin_2[3] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3204:
     Coin_1[29] Coin_2[3] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3205:
     Coin_1[29] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3206:
     Coin_1[29] Coin_2[3] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3207:
     Coin_1[29] Coin_2[3] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3208:
     Coin_1[29] Coin_2[3] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3209:
     Coin_1[29] Coin_2[3] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3210:
     Coin_1[29] Coin_2[3] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3211:
     Coin_1[29] Coin_2[3] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3212:
     Coin_1[29] Coin_2[3] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3213:
     Coin_1[29] Coin_2[3] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3214:
     Coin_1[29] Coin_2[3] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3215:
     Coin_1[29] Coin_2[3] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3216:
     Coin_1[29] Coin_2[3] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3217:
     Coin_1[29] Coin_2[3] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3218:
     Coin_1[29] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3219:
     Coin_1[29] Coin_2[8] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3220:
     Coin_1[29] Coin_2[8] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3221:
     Coin_1[29] Coin_2[8] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3222:
     Coin_1[29] Coin_2[8] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3223:
     Coin_1[29] Coin_2[8] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3224:
     Coin_1[29] Coin_2[8] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3225:
     Coin_1[29] Coin_2[8] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3226:
     Coin_1[29] Coin_2[8] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3227:
     Coin_1[29] Coin_2[8] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3228:
     Coin_1[29] Coin_2[8] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3229:
     Coin_1[29] Coin_2[8] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3230:
     Coin_1[29] Coin_2[8] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3231:
     Coin_1[29] Coin_2[13] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3232:
     Coin_1[29] Coin_2[13] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3233:
     Coin_1[29] Coin_2[13] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3234:
     Coin_1[29] Coin_2[13] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3235:
     Coin_1[29] Coin_2[13] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3236:
     Coin_1[29] Coin_2[13] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3237:
     Coin_1[29] Coin_2[13] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3238:
     Coin_1[29] Coin_2[13] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3239:
     Coin_1[29] Coin_2[13] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3240:
     Coin_1[29] Coin_2[18] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3241:
     Coin_1[29] Coin_2[18] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3242:
     Coin_1[29] Coin_2[18] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3243:
     Coin_1[29] Coin_2[18] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3244:
     Coin_1[29] Coin_2[18] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3245:
     Coin_1[29] Coin_2[18] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3246:
     Coin_1[29] Coin_2[23] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3247:
     Coin_1[29] Coin_2[23] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3248:
     Coin_1[29] Coin_2[23] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3249:
     Coin_1[29] Coin_2[23] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3250:
     Coin_1[29] Coin_2[28] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3251:
     Coin_1[29] Coin_2[28] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3252:
     Coin_1[29] Coin_2[33] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3253:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[1] Coin_100[0]
Solution #3254:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3255:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3256:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3257:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[5] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3258:
     Coin_1[30] Coin_2[0] Coin_5[0] Coin_10[7] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3259:
     Coin_1[30] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3260:
     Coin_1[30] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3261:
     Coin_1[30] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3262:
     Coin_1[30] Coin_2[0] Coin_5[2] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3263:
     Coin_1[30] Coin_2[0] Coin_5[2] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3264:
     Coin_1[30] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3265:
     Coin_1[30] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3266:
     Coin_1[30] Coin_2[0] Coin_5[4] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3267:
     Coin_1[30] Coin_2[0] Coin_5[4] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3268:
     Coin_1[30] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3269:
     Coin_1[30] Coin_2[0] Coin_5[6] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3270:
     Coin_1[30] Coin_2[0] Coin_5[6] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3271:
     Coin_1[30] Coin_2[0] Coin_5[8] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3272:
     Coin_1[30] Coin_2[0] Coin_5[8] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3273:
     Coin_1[30] Coin_2[0] Coin_5[10] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3274:
     Coin_1[30] Coin_2[0] Coin_5[10] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3275:
     Coin_1[30] Coin_2[0] Coin_5[12] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3276:
     Coin_1[30] Coin_2[0] Coin_5[14] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3277:
     Coin_1[30] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3278:
     Coin_1[30] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3279:
     Coin_1[30] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3280:
     Coin_1[30] Coin_2[5] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3281:
     Coin_1[30] Coin_2[5] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3282:
     Coin_1[30] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3283:
     Coin_1[30] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3284:
     Coin_1[30] Coin_2[5] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3285:
     Coin_1[30] Coin_2[5] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3286:
     Coin_1[30] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3287:
     Coin_1[30] Coin_2[5] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3288:
     Coin_1[30] Coin_2[5] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3289:
     Coin_1[30] Coin_2[5] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3290:
     Coin_1[30] Coin_2[5] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3291:
     Coin_1[30] Coin_2[5] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3292:
     Coin_1[30] Coin_2[5] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3293:
     Coin_1[30] Coin_2[5] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3294:
     Coin_1[30] Coin_2[5] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3295:
     Coin_1[30] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3296:
     Coin_1[30] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3297:
     Coin_1[30] Coin_2[10] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3298:
     Coin_1[30] Coin_2[10] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3299:
     Coin_1[30] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3300:
     Coin_1[30] Coin_2[10] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3301:
     Coin_1[30] Coin_2[10] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3302:
     Coin_1[30] Coin_2[10] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3303:
     Coin_1[30] Coin_2[10] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3304:
     Coin_1[30] Coin_2[10] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3305:
     Coin_1[30] Coin_2[10] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3306:
     Coin_1[30] Coin_2[10] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3307:
     Coin_1[30] Coin_2[10] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3308:
     Coin_1[30] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3309:
     Coin_1[30] Coin_2[15] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3310:
     Coin_1[30] Coin_2[15] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3311:
     Coin_1[30] Coin_2[15] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3312:
     Coin_1[30] Coin_2[15] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3313:
     Coin_1[30] Coin_2[15] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3314:
     Coin_1[30] Coin_2[15] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3315:
     Coin_1[30] Coin_2[15] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3316:
     Coin_1[30] Coin_2[15] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3317:
     Coin_1[30] Coin_2[20] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3318:
     Coin_1[30] Coin_2[20] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3319:
     Coin_1[30] Coin_2[20] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3320:
     Coin_1[30] Coin_2[20] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3321:
     Coin_1[30] Coin_2[20] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3322:
     Coin_1[30] Coin_2[20] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3323:
     Coin_1[30] Coin_2[25] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3324:
     Coin_1[30] Coin_2[25] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3325:
     Coin_1[30] Coin_2[25] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3326:
     Coin_1[30] Coin_2[25] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3327:
     Coin_1[30] Coin_2[30] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3328:
     Coin_1[30] Coin_2[30] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3329:
     Coin_1[30] Coin_2[35] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3330:
     Coin_1[31] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3331:
     Coin_1[31] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3332:
     Coin_1[31] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3333:
     Coin_1[31] Coin_2[2] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3334:
     Coin_1[31] Coin_2[2] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3335:
     Coin_1[31] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3336:
     Coin_1[31] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3337:
     Coin_1[31] Coin_2[2] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3338:
     Coin_1[31] Coin_2[2] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3339:
     Coin_1[31] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3340:
     Coin_1[31] Coin_2[2] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3341:
     Coin_1[31] Coin_2[2] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3342:
     Coin_1[31] Coin_2[2] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3343:
     Coin_1[31] Coin_2[2] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3344:
     Coin_1[31] Coin_2[2] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3345:
     Coin_1[31] Coin_2[2] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3346:
     Coin_1[31] Coin_2[2] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3347:
     Coin_1[31] Coin_2[2] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3348:
     Coin_1[31] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3349:
     Coin_1[31] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3350:
     Coin_1[31] Coin_2[7] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3351:
     Coin_1[31] Coin_2[7] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3352:
     Coin_1[31] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3353:
     Coin_1[31] Coin_2[7] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3354:
     Coin_1[31] Coin_2[7] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3355:
     Coin_1[31] Coin_2[7] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3356:
     Coin_1[31] Coin_2[7] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3357:
     Coin_1[31] Coin_2[7] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3358:
     Coin_1[31] Coin_2[7] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3359:
     Coin_1[31] Coin_2[7] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3360:
     Coin_1[31] Coin_2[7] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3361:
     Coin_1[31] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3362:
     Coin_1[31] Coin_2[12] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3363:
     Coin_1[31] Coin_2[12] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3364:
     Coin_1[31] Coin_2[12] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3365:
     Coin_1[31] Coin_2[12] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3366:
     Coin_1[31] Coin_2[12] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3367:
     Coin_1[31] Coin_2[12] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3368:
     Coin_1[31] Coin_2[12] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3369:
     Coin_1[31] Coin_2[12] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3370:
     Coin_1[31] Coin_2[17] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3371:
     Coin_1[31] Coin_2[17] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3372:
     Coin_1[31] Coin_2[17] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3373:
     Coin_1[31] Coin_2[17] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3374:
     Coin_1[31] Coin_2[17] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3375:
     Coin_1[31] Coin_2[17] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3376:
     Coin_1[31] Coin_2[22] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3377:
     Coin_1[31] Coin_2[22] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3378:
     Coin_1[31] Coin_2[22] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3379:
     Coin_1[31] Coin_2[22] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3380:
     Coin_1[31] Coin_2[27] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3381:
     Coin_1[31] Coin_2[27] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3382:
     Coin_1[31] Coin_2[32] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3383:
     Coin_1[32] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3384:
     Coin_1[32] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3385:
     Coin_1[32] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3386:
     Coin_1[32] Coin_2[4] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3387:
     Coin_1[32] Coin_2[4] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3388:
     Coin_1[32] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3389:
     Coin_1[32] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3390:
     Coin_1[32] Coin_2[4] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3391:
     Coin_1[32] Coin_2[4] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3392:
     Coin_1[32] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3393:
     Coin_1[32] Coin_2[4] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3394:
     Coin_1[32] Coin_2[4] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3395:
     Coin_1[32] Coin_2[4] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3396:
     Coin_1[32] Coin_2[4] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3397:
     Coin_1[32] Coin_2[4] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3398:
     Coin_1[32] Coin_2[4] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3399:
     Coin_1[32] Coin_2[4] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3400:
     Coin_1[32] Coin_2[4] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3401:
     Coin_1[32] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3402:
     Coin_1[32] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3403:
     Coin_1[32] Coin_2[9] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3404:
     Coin_1[32] Coin_2[9] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3405:
     Coin_1[32] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3406:
     Coin_1[32] Coin_2[9] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3407:
     Coin_1[32] Coin_2[9] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3408:
     Coin_1[32] Coin_2[9] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3409:
     Coin_1[32] Coin_2[9] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3410:
     Coin_1[32] Coin_2[9] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3411:
     Coin_1[32] Coin_2[9] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3412:
     Coin_1[32] Coin_2[9] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3413:
     Coin_1[32] Coin_2[9] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3414:
     Coin_1[32] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3415:
     Coin_1[32] Coin_2[14] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3416:
     Coin_1[32] Coin_2[14] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3417:
     Coin_1[32] Coin_2[14] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3418:
     Coin_1[32] Coin_2[14] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3419:
     Coin_1[32] Coin_2[14] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3420:
     Coin_1[32] Coin_2[14] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3421:
     Coin_1[32] Coin_2[14] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3422:
     Coin_1[32] Coin_2[14] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3423:
     Coin_1[32] Coin_2[19] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3424:
     Coin_1[32] Coin_2[19] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3425:
     Coin_1[32] Coin_2[19] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3426:
     Coin_1[32] Coin_2[19] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3427:
     Coin_1[32] Coin_2[19] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3428:
     Coin_1[32] Coin_2[19] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3429:
     Coin_1[32] Coin_2[24] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3430:
     Coin_1[32] Coin_2[24] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3431:
     Coin_1[32] Coin_2[24] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3432:
     Coin_1[32] Coin_2[24] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3433:
     Coin_1[32] Coin_2[29] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3434:
     Coin_1[32] Coin_2[29] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3435:
     Coin_1[32] Coin_2[34] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3436:
     Coin_1[33] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3437:
     Coin_1[33] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3438:
     Coin_1[33] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3439:
     Coin_1[33] Coin_2[1] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3440:
     Coin_1[33] Coin_2[1] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3441:
     Coin_1[33] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3442:
     Coin_1[33] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3443:
     Coin_1[33] Coin_2[1] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3444:
     Coin_1[33] Coin_2[1] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3445:
     Coin_1[33] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3446:
     Coin_1[33] Coin_2[1] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3447:
     Coin_1[33] Coin_2[1] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3448:
     Coin_1[33] Coin_2[1] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3449:
     Coin_1[33] Coin_2[1] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3450:
     Coin_1[33] Coin_2[1] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3451:
     Coin_1[33] Coin_2[1] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3452:
     Coin_1[33] Coin_2[1] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3453:
     Coin_1[33] Coin_2[1] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3454:
     Coin_1[33] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3455:
     Coin_1[33] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3456:
     Coin_1[33] Coin_2[6] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3457:
     Coin_1[33] Coin_2[6] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3458:
     Coin_1[33] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3459:
     Coin_1[33] Coin_2[6] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3460:
     Coin_1[33] Coin_2[6] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3461:
     Coin_1[33] Coin_2[6] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3462:
     Coin_1[33] Coin_2[6] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3463:
     Coin_1[33] Coin_2[6] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3464:
     Coin_1[33] Coin_2[6] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3465:
     Coin_1[33] Coin_2[6] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3466:
     Coin_1[33] Coin_2[6] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3467:
     Coin_1[33] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3468:
     Coin_1[33] Coin_2[11] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3469:
     Coin_1[33] Coin_2[11] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3470:
     Coin_1[33] Coin_2[11] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3471:
     Coin_1[33] Coin_2[11] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3472:
     Coin_1[33] Coin_2[11] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3473:
     Coin_1[33] Coin_2[11] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3474:
     Coin_1[33] Coin_2[11] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3475:
     Coin_1[33] Coin_2[11] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3476:
     Coin_1[33] Coin_2[16] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3477:
     Coin_1[33] Coin_2[16] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3478:
     Coin_1[33] Coin_2[16] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3479:
     Coin_1[33] Coin_2[16] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3480:
     Coin_1[33] Coin_2[16] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3481:
     Coin_1[33] Coin_2[16] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3482:
     Coin_1[33] Coin_2[21] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3483:
     Coin_1[33] Coin_2[21] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3484:
     Coin_1[33] Coin_2[21] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3485:
     Coin_1[33] Coin_2[21] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3486:
     Coin_1[33] Coin_2[26] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3487:
     Coin_1[33] Coin_2[26] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3488:
     Coin_1[33] Coin_2[31] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3489:
     Coin_1[34] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3490:
     Coin_1[34] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3491:
     Coin_1[34] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3492:
     Coin_1[34] Coin_2[3] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3493:
     Coin_1[34] Coin_2[3] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3494:
     Coin_1[34] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3495:
     Coin_1[34] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3496:
     Coin_1[34] Coin_2[3] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3497:
     Coin_1[34] Coin_2[3] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3498:
     Coin_1[34] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3499:
     Coin_1[34] Coin_2[3] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3500:
     Coin_1[34] Coin_2[3] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3501:
     Coin_1[34] Coin_2[3] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3502:
     Coin_1[34] Coin_2[3] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3503:
     Coin_1[34] Coin_2[3] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3504:
     Coin_1[34] Coin_2[3] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3505:
     Coin_1[34] Coin_2[3] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3506:
     Coin_1[34] Coin_2[3] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3507:
     Coin_1[34] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3508:
     Coin_1[34] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3509:
     Coin_1[34] Coin_2[8] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3510:
     Coin_1[34] Coin_2[8] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3511:
     Coin_1[34] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3512:
     Coin_1[34] Coin_2[8] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3513:
     Coin_1[34] Coin_2[8] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3514:
     Coin_1[34] Coin_2[8] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3515:
     Coin_1[34] Coin_2[8] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3516:
     Coin_1[34] Coin_2[8] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3517:
     Coin_1[34] Coin_2[8] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3518:
     Coin_1[34] Coin_2[8] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3519:
     Coin_1[34] Coin_2[8] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3520:
     Coin_1[34] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3521:
     Coin_1[34] Coin_2[13] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3522:
     Coin_1[34] Coin_2[13] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3523:
     Coin_1[34] Coin_2[13] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3524:
     Coin_1[34] Coin_2[13] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3525:
     Coin_1[34] Coin_2[13] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3526:
     Coin_1[34] Coin_2[13] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3527:
     Coin_1[34] Coin_2[13] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3528:
     Coin_1[34] Coin_2[13] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3529:
     Coin_1[34] Coin_2[18] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3530:
     Coin_1[34] Coin_2[18] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3531:
     Coin_1[34] Coin_2[18] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3532:
     Coin_1[34] Coin_2[18] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3533:
     Coin_1[34] Coin_2[18] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3534:
     Coin_1[34] Coin_2[18] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3535:
     Coin_1[34] Coin_2[23] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3536:
     Coin_1[34] Coin_2[23] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3537:
     Coin_1[34] Coin_2[23] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3538:
     Coin_1[34] Coin_2[23] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3539:
     Coin_1[34] Coin_2[28] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3540:
     Coin_1[34] Coin_2[28] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3541:
     Coin_1[34] Coin_2[33] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3542:
     Coin_1[35] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3543:
     Coin_1[35] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3544:
     Coin_1[35] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3545:
     Coin_1[35] Coin_2[0] Coin_5[1] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3546:
     Coin_1[35] Coin_2[0] Coin_5[1] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3547:
     Coin_1[35] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3548:
     Coin_1[35] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3549:
     Coin_1[35] Coin_2[0] Coin_5[3] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3550:
     Coin_1[35] Coin_2[0] Coin_5[3] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3551:
     Coin_1[35] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3552:
     Coin_1[35] Coin_2[0] Coin_5[5] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3553:
     Coin_1[35] Coin_2[0] Coin_5[5] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3554:
     Coin_1[35] Coin_2[0] Coin_5[7] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3555:
     Coin_1[35] Coin_2[0] Coin_5[7] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3556:
     Coin_1[35] Coin_2[0] Coin_5[9] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3557:
     Coin_1[35] Coin_2[0] Coin_5[9] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3558:
     Coin_1[35] Coin_2[0] Coin_5[11] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3559:
     Coin_1[35] Coin_2[0] Coin_5[13] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3560:
     Coin_1[35] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3561:
     Coin_1[35] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3562:
     Coin_1[35] Coin_2[5] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3563:
     Coin_1[35] Coin_2[5] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3564:
     Coin_1[35] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3565:
     Coin_1[35] Coin_2[5] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3566:
     Coin_1[35] Coin_2[5] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3567:
     Coin_1[35] Coin_2[5] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3568:
     Coin_1[35] Coin_2[5] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3569:
     Coin_1[35] Coin_2[5] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3570:
     Coin_1[35] Coin_2[5] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3571:
     Coin_1[35] Coin_2[5] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3572:
     Coin_1[35] Coin_2[5] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3573:
     Coin_1[35] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3574:
     Coin_1[35] Coin_2[10] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3575:
     Coin_1[35] Coin_2[10] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3576:
     Coin_1[35] Coin_2[10] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3577:
     Coin_1[35] Coin_2[10] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3578:
     Coin_1[35] Coin_2[10] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3579:
     Coin_1[35] Coin_2[10] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3580:
     Coin_1[35] Coin_2[10] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3581:
     Coin_1[35] Coin_2[10] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3582:
     Coin_1[35] Coin_2[15] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3583:
     Coin_1[35] Coin_2[15] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3584:
     Coin_1[35] Coin_2[15] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3585:
     Coin_1[35] Coin_2[15] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3586:
     Coin_1[35] Coin_2[15] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3587:
     Coin_1[35] Coin_2[15] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3588:
     Coin_1[35] Coin_2[20] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3589:
     Coin_1[35] Coin_2[20] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3590:
     Coin_1[35] Coin_2[20] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3591:
     Coin_1[35] Coin_2[20] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3592:
     Coin_1[35] Coin_2[25] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3593:
     Coin_1[35] Coin_2[25] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3594:
     Coin_1[35] Coin_2[30] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3595:
     Coin_1[36] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3596:
     Coin_1[36] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3597:
     Coin_1[36] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3598:
     Coin_1[36] Coin_2[2] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3599:
     Coin_1[36] Coin_2[2] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3600:
     Coin_1[36] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3601:
     Coin_1[36] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3602:
     Coin_1[36] Coin_2[2] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3603:
     Coin_1[36] Coin_2[2] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3604:
     Coin_1[36] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3605:
     Coin_1[36] Coin_2[2] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3606:
     Coin_1[36] Coin_2[2] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3607:
     Coin_1[36] Coin_2[2] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3608:
     Coin_1[36] Coin_2[2] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3609:
     Coin_1[36] Coin_2[2] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3610:
     Coin_1[36] Coin_2[2] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3611:
     Coin_1[36] Coin_2[2] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3612:
     Coin_1[36] Coin_2[2] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3613:
     Coin_1[36] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3614:
     Coin_1[36] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3615:
     Coin_1[36] Coin_2[7] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3616:
     Coin_1[36] Coin_2[7] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3617:
     Coin_1[36] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3618:
     Coin_1[36] Coin_2[7] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3619:
     Coin_1[36] Coin_2[7] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3620:
     Coin_1[36] Coin_2[7] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3621:
     Coin_1[36] Coin_2[7] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3622:
     Coin_1[36] Coin_2[7] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3623:
     Coin_1[36] Coin_2[7] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3624:
     Coin_1[36] Coin_2[7] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3625:
     Coin_1[36] Coin_2[7] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3626:
     Coin_1[36] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3627:
     Coin_1[36] Coin_2[12] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3628:
     Coin_1[36] Coin_2[12] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3629:
     Coin_1[36] Coin_2[12] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3630:
     Coin_1[36] Coin_2[12] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3631:
     Coin_1[36] Coin_2[12] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3632:
     Coin_1[36] Coin_2[12] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3633:
     Coin_1[36] Coin_2[12] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3634:
     Coin_1[36] Coin_2[12] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3635:
     Coin_1[36] Coin_2[17] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3636:
     Coin_1[36] Coin_2[17] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3637:
     Coin_1[36] Coin_2[17] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3638:
     Coin_1[36] Coin_2[17] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3639:
     Coin_1[36] Coin_2[17] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3640:
     Coin_1[36] Coin_2[17] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3641:
     Coin_1[36] Coin_2[22] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3642:
     Coin_1[36] Coin_2[22] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3643:
     Coin_1[36] Coin_2[22] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3644:
     Coin_1[36] Coin_2[22] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3645:
     Coin_1[36] Coin_2[27] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3646:
     Coin_1[36] Coin_2[27] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3647:
     Coin_1[36] Coin_2[32] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3648:
     Coin_1[37] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3649:
     Coin_1[37] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3650:
     Coin_1[37] Coin_2[4] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3651:
     Coin_1[37] Coin_2[4] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3652:
     Coin_1[37] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3653:
     Coin_1[37] Coin_2[4] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3654:
     Coin_1[37] Coin_2[4] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3655:
     Coin_1[37] Coin_2[4] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3656:
     Coin_1[37] Coin_2[4] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3657:
     Coin_1[37] Coin_2[4] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3658:
     Coin_1[37] Coin_2[4] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3659:
     Coin_1[37] Coin_2[4] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3660:
     Coin_1[37] Coin_2[4] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3661:
     Coin_1[37] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3662:
     Coin_1[37] Coin_2[9] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3663:
     Coin_1[37] Coin_2[9] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3664:
     Coin_1[37] Coin_2[9] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3665:
     Coin_1[37] Coin_2[9] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3666:
     Coin_1[37] Coin_2[9] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3667:
     Coin_1[37] Coin_2[9] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3668:
     Coin_1[37] Coin_2[9] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3669:
     Coin_1[37] Coin_2[9] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3670:
     Coin_1[37] Coin_2[14] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3671:
     Coin_1[37] Coin_2[14] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3672:
     Coin_1[37] Coin_2[14] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3673:
     Coin_1[37] Coin_2[14] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3674:
     Coin_1[37] Coin_2[14] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3675:
     Coin_1[37] Coin_2[14] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3676:
     Coin_1[37] Coin_2[19] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3677:
     Coin_1[37] Coin_2[19] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3678:
     Coin_1[37] Coin_2[19] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3679:
     Coin_1[37] Coin_2[19] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3680:
     Coin_1[37] Coin_2[24] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3681:
     Coin_1[37] Coin_2[24] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3682:
     Coin_1[37] Coin_2[29] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3683:
     Coin_1[38] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3684:
     Coin_1[38] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3685:
     Coin_1[38] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3686:
     Coin_1[38] Coin_2[1] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3687:
     Coin_1[38] Coin_2[1] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3688:
     Coin_1[38] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3689:
     Coin_1[38] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3690:
     Coin_1[38] Coin_2[1] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3691:
     Coin_1[38] Coin_2[1] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3692:
     Coin_1[38] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3693:
     Coin_1[38] Coin_2[1] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3694:
     Coin_1[38] Coin_2[1] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3695:
     Coin_1[38] Coin_2[1] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3696:
     Coin_1[38] Coin_2[1] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3697:
     Coin_1[38] Coin_2[1] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3698:
     Coin_1[38] Coin_2[1] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3699:
     Coin_1[38] Coin_2[1] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3700:
     Coin_1[38] Coin_2[1] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3701:
     Coin_1[38] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3702:
     Coin_1[38] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3703:
     Coin_1[38] Coin_2[6] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3704:
     Coin_1[38] Coin_2[6] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3705:
     Coin_1[38] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3706:
     Coin_1[38] Coin_2[6] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3707:
     Coin_1[38] Coin_2[6] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3708:
     Coin_1[38] Coin_2[6] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3709:
     Coin_1[38] Coin_2[6] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3710:
     Coin_1[38] Coin_2[6] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3711:
     Coin_1[38] Coin_2[6] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3712:
     Coin_1[38] Coin_2[6] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3713:
     Coin_1[38] Coin_2[6] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3714:
     Coin_1[38] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3715:
     Coin_1[38] Coin_2[11] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3716:
     Coin_1[38] Coin_2[11] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3717:
     Coin_1[38] Coin_2[11] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3718:
     Coin_1[38] Coin_2[11] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3719:
     Coin_1[38] Coin_2[11] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3720:
     Coin_1[38] Coin_2[11] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3721:
     Coin_1[38] Coin_2[11] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3722:
     Coin_1[38] Coin_2[11] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3723:
     Coin_1[38] Coin_2[16] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3724:
     Coin_1[38] Coin_2[16] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3725:
     Coin_1[38] Coin_2[16] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3726:
     Coin_1[38] Coin_2[16] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3727:
     Coin_1[38] Coin_2[16] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3728:
     Coin_1[38] Coin_2[16] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3729:
     Coin_1[38] Coin_2[21] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3730:
     Coin_1[38] Coin_2[21] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3731:
     Coin_1[38] Coin_2[21] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3732:
     Coin_1[38] Coin_2[21] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3733:
     Coin_1[38] Coin_2[26] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3734:
     Coin_1[38] Coin_2[26] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3735:
     Coin_1[38] Coin_2[31] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3736:
     Coin_1[39] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3737:
     Coin_1[39] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3738:
     Coin_1[39] Coin_2[3] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3739:
     Coin_1[39] Coin_2[3] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3740:
     Coin_1[39] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3741:
     Coin_1[39] Coin_2[3] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3742:
     Coin_1[39] Coin_2[3] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3743:
     Coin_1[39] Coin_2[3] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3744:
     Coin_1[39] Coin_2[3] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3745:
     Coin_1[39] Coin_2[3] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3746:
     Coin_1[39] Coin_2[3] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3747:
     Coin_1[39] Coin_2[3] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3748:
     Coin_1[39] Coin_2[3] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3749:
     Coin_1[39] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3750:
     Coin_1[39] Coin_2[8] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3751:
     Coin_1[39] Coin_2[8] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3752:
     Coin_1[39] Coin_2[8] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3753:
     Coin_1[39] Coin_2[8] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3754:
     Coin_1[39] Coin_2[8] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3755:
     Coin_1[39] Coin_2[8] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3756:
     Coin_1[39] Coin_2[8] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3757:
     Coin_1[39] Coin_2[8] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3758:
     Coin_1[39] Coin_2[13] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3759:
     Coin_1[39] Coin_2[13] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3760:
     Coin_1[39] Coin_2[13] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3761:
     Coin_1[39] Coin_2[13] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3762:
     Coin_1[39] Coin_2[13] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3763:
     Coin_1[39] Coin_2[13] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3764:
     Coin_1[39] Coin_2[18] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3765:
     Coin_1[39] Coin_2[18] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3766:
     Coin_1[39] Coin_2[18] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3767:
     Coin_1[39] Coin_2[18] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3768:
     Coin_1[39] Coin_2[23] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3769:
     Coin_1[39] Coin_2[23] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3770:
     Coin_1[39] Coin_2[28] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3771:
     Coin_1[40] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[3] Coin_50[0] Coin_100[0]
Solution #3772:
     Coin_1[40] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3773:
     Coin_1[40] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3774:
     Coin_1[40] Coin_2[0] Coin_5[0] Coin_10[4] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3775:
     Coin_1[40] Coin_2[0] Coin_5[0] Coin_10[6] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3776:
     Coin_1[40] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3777:
     Coin_1[40] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3778:
     Coin_1[40] Coin_2[0] Coin_5[2] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3779:
     Coin_1[40] Coin_2[0] Coin_5[2] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3780:
     Coin_1[40] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3781:
     Coin_1[40] Coin_2[0] Coin_5[4] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3782:
     Coin_1[40] Coin_2[0] Coin_5[4] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3783:
     Coin_1[40] Coin_2[0] Coin_5[6] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3784:
     Coin_1[40] Coin_2[0] Coin_5[6] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3785:
     Coin_1[40] Coin_2[0] Coin_5[8] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3786:
     Coin_1[40] Coin_2[0] Coin_5[8] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3787:
     Coin_1[40] Coin_2[0] Coin_5[10] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3788:
     Coin_1[40] Coin_2[0] Coin_5[12] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3789:
     Coin_1[40] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3790:
     Coin_1[40] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3791:
     Coin_1[40] Coin_2[5] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3792:
     Coin_1[40] Coin_2[5] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3793:
     Coin_1[40] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3794:
     Coin_1[40] Coin_2[5] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3795:
     Coin_1[40] Coin_2[5] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3796:
     Coin_1[40] Coin_2[5] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3797:
     Coin_1[40] Coin_2[5] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3798:
     Coin_1[40] Coin_2[5] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3799:
     Coin_1[40] Coin_2[5] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3800:
     Coin_1[40] Coin_2[5] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3801:
     Coin_1[40] Coin_2[5] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3802:
     Coin_1[40] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3803:
     Coin_1[40] Coin_2[10] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3804:
     Coin_1[40] Coin_2[10] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3805:
     Coin_1[40] Coin_2[10] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3806:
     Coin_1[40] Coin_2[10] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3807:
     Coin_1[40] Coin_2[10] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3808:
     Coin_1[40] Coin_2[10] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3809:
     Coin_1[40] Coin_2[10] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3810:
     Coin_1[40] Coin_2[10] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3811:
     Coin_1[40] Coin_2[15] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3812:
     Coin_1[40] Coin_2[15] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3813:
     Coin_1[40] Coin_2[15] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3814:
     Coin_1[40] Coin_2[15] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3815:
     Coin_1[40] Coin_2[15] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3816:
     Coin_1[40] Coin_2[15] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3817:
     Coin_1[40] Coin_2[20] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3818:
     Coin_1[40] Coin_2[20] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3819:
     Coin_1[40] Coin_2[20] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3820:
     Coin_1[40] Coin_2[20] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3821:
     Coin_1[40] Coin_2[25] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3822:
     Coin_1[40] Coin_2[25] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3823:
     Coin_1[40] Coin_2[30] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3824:
     Coin_1[41] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3825:
     Coin_1[41] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3826:
     Coin_1[41] Coin_2[2] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3827:
     Coin_1[41] Coin_2[2] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3828:
     Coin_1[41] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3829:
     Coin_1[41] Coin_2[2] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3830:
     Coin_1[41] Coin_2[2] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3831:
     Coin_1[41] Coin_2[2] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3832:
     Coin_1[41] Coin_2[2] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3833:
     Coin_1[41] Coin_2[2] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3834:
     Coin_1[41] Coin_2[2] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3835:
     Coin_1[41] Coin_2[2] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3836:
     Coin_1[41] Coin_2[2] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3837:
     Coin_1[41] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3838:
     Coin_1[41] Coin_2[7] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3839:
     Coin_1[41] Coin_2[7] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3840:
     Coin_1[41] Coin_2[7] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3841:
     Coin_1[41] Coin_2[7] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3842:
     Coin_1[41] Coin_2[7] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3843:
     Coin_1[41] Coin_2[7] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3844:
     Coin_1[41] Coin_2[7] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3845:
     Coin_1[41] Coin_2[7] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3846:
     Coin_1[41] Coin_2[12] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3847:
     Coin_1[41] Coin_2[12] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3848:
     Coin_1[41] Coin_2[12] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3849:
     Coin_1[41] Coin_2[12] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3850:
     Coin_1[41] Coin_2[12] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3851:
     Coin_1[41] Coin_2[12] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3852:
     Coin_1[41] Coin_2[17] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3853:
     Coin_1[41] Coin_2[17] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3854:
     Coin_1[41] Coin_2[17] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3855:
     Coin_1[41] Coin_2[17] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3856:
     Coin_1[41] Coin_2[22] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3857:
     Coin_1[41] Coin_2[22] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3858:
     Coin_1[41] Coin_2[27] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3859:
     Coin_1[42] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3860:
     Coin_1[42] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3861:
     Coin_1[42] Coin_2[4] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3862:
     Coin_1[42] Coin_2[4] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3863:
     Coin_1[42] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3864:
     Coin_1[42] Coin_2[4] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3865:
     Coin_1[42] Coin_2[4] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3866:
     Coin_1[42] Coin_2[4] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3867:
     Coin_1[42] Coin_2[4] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3868:
     Coin_1[42] Coin_2[4] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3869:
     Coin_1[42] Coin_2[4] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3870:
     Coin_1[42] Coin_2[4] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3871:
     Coin_1[42] Coin_2[4] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3872:
     Coin_1[42] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3873:
     Coin_1[42] Coin_2[9] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3874:
     Coin_1[42] Coin_2[9] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3875:
     Coin_1[42] Coin_2[9] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3876:
     Coin_1[42] Coin_2[9] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3877:
     Coin_1[42] Coin_2[9] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3878:
     Coin_1[42] Coin_2[9] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3879:
     Coin_1[42] Coin_2[9] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3880:
     Coin_1[42] Coin_2[9] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3881:
     Coin_1[42] Coin_2[14] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3882:
     Coin_1[42] Coin_2[14] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3883:
     Coin_1[42] Coin_2[14] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3884:
     Coin_1[42] Coin_2[14] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3885:
     Coin_1[42] Coin_2[14] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3886:
     Coin_1[42] Coin_2[14] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3887:
     Coin_1[42] Coin_2[19] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3888:
     Coin_1[42] Coin_2[19] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3889:
     Coin_1[42] Coin_2[19] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3890:
     Coin_1[42] Coin_2[19] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3891:
     Coin_1[42] Coin_2[24] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3892:
     Coin_1[42] Coin_2[24] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3893:
     Coin_1[42] Coin_2[29] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3894:
     Coin_1[43] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3895:
     Coin_1[43] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3896:
     Coin_1[43] Coin_2[1] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3897:
     Coin_1[43] Coin_2[1] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3898:
     Coin_1[43] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3899:
     Coin_1[43] Coin_2[1] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3900:
     Coin_1[43] Coin_2[1] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3901:
     Coin_1[43] Coin_2[1] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3902:
     Coin_1[43] Coin_2[1] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3903:
     Coin_1[43] Coin_2[1] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3904:
     Coin_1[43] Coin_2[1] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3905:
     Coin_1[43] Coin_2[1] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3906:
     Coin_1[43] Coin_2[1] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3907:
     Coin_1[43] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3908:
     Coin_1[43] Coin_2[6] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3909:
     Coin_1[43] Coin_2[6] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3910:
     Coin_1[43] Coin_2[6] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3911:
     Coin_1[43] Coin_2[6] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3912:
     Coin_1[43] Coin_2[6] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3913:
     Coin_1[43] Coin_2[6] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3914:
     Coin_1[43] Coin_2[6] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3915:
     Coin_1[43] Coin_2[6] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3916:
     Coin_1[43] Coin_2[11] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3917:
     Coin_1[43] Coin_2[11] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3918:
     Coin_1[43] Coin_2[11] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3919:
     Coin_1[43] Coin_2[11] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3920:
     Coin_1[43] Coin_2[11] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3921:
     Coin_1[43] Coin_2[11] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3922:
     Coin_1[43] Coin_2[16] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3923:
     Coin_1[43] Coin_2[16] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3924:
     Coin_1[43] Coin_2[16] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3925:
     Coin_1[43] Coin_2[16] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3926:
     Coin_1[43] Coin_2[21] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3927:
     Coin_1[43] Coin_2[21] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3928:
     Coin_1[43] Coin_2[26] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3929:
     Coin_1[44] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3930:
     Coin_1[44] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3931:
     Coin_1[44] Coin_2[3] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3932:
     Coin_1[44] Coin_2[3] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3933:
     Coin_1[44] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3934:
     Coin_1[44] Coin_2[3] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3935:
     Coin_1[44] Coin_2[3] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3936:
     Coin_1[44] Coin_2[3] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3937:
     Coin_1[44] Coin_2[3] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3938:
     Coin_1[44] Coin_2[3] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3939:
     Coin_1[44] Coin_2[3] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3940:
     Coin_1[44] Coin_2[3] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3941:
     Coin_1[44] Coin_2[3] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3942:
     Coin_1[44] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3943:
     Coin_1[44] Coin_2[8] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3944:
     Coin_1[44] Coin_2[8] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3945:
     Coin_1[44] Coin_2[8] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3946:
     Coin_1[44] Coin_2[8] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3947:
     Coin_1[44] Coin_2[8] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3948:
     Coin_1[44] Coin_2[8] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3949:
     Coin_1[44] Coin_2[8] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3950:
     Coin_1[44] Coin_2[8] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3951:
     Coin_1[44] Coin_2[13] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3952:
     Coin_1[44] Coin_2[13] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3953:
     Coin_1[44] Coin_2[13] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3954:
     Coin_1[44] Coin_2[13] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3955:
     Coin_1[44] Coin_2[13] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3956:
     Coin_1[44] Coin_2[13] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3957:
     Coin_1[44] Coin_2[18] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3958:
     Coin_1[44] Coin_2[18] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3959:
     Coin_1[44] Coin_2[18] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3960:
     Coin_1[44] Coin_2[18] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3961:
     Coin_1[44] Coin_2[23] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3962:
     Coin_1[44] Coin_2[23] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3963:
     Coin_1[44] Coin_2[28] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3964:
     Coin_1[45] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #3965:
     Coin_1[45] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3966:
     Coin_1[45] Coin_2[0] Coin_5[1] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3967:
     Coin_1[45] Coin_2[0] Coin_5[1] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3968:
     Coin_1[45] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3969:
     Coin_1[45] Coin_2[0] Coin_5[3] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3970:
     Coin_1[45] Coin_2[0] Coin_5[3] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3971:
     Coin_1[45] Coin_2[0] Coin_5[5] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3972:
     Coin_1[45] Coin_2[0] Coin_5[5] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3973:
     Coin_1[45] Coin_2[0] Coin_5[7] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3974:
     Coin_1[45] Coin_2[0] Coin_5[7] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3975:
     Coin_1[45] Coin_2[0] Coin_5[9] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3976:
     Coin_1[45] Coin_2[0] Coin_5[11] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3977:
     Coin_1[45] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #3978:
     Coin_1[45] Coin_2[5] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3979:
     Coin_1[45] Coin_2[5] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3980:
     Coin_1[45] Coin_2[5] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3981:
     Coin_1[45] Coin_2[5] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3982:
     Coin_1[45] Coin_2[5] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3983:
     Coin_1[45] Coin_2[5] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3984:
     Coin_1[45] Coin_2[5] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3985:
     Coin_1[45] Coin_2[5] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3986:
     Coin_1[45] Coin_2[10] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3987:
     Coin_1[45] Coin_2[10] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3988:
     Coin_1[45] Coin_2[10] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3989:
     Coin_1[45] Coin_2[10] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3990:
     Coin_1[45] Coin_2[10] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3991:
     Coin_1[45] Coin_2[10] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3992:
     Coin_1[45] Coin_2[15] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #3993:
     Coin_1[45] Coin_2[15] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3994:
     Coin_1[45] Coin_2[15] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3995:
     Coin_1[45] Coin_2[15] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3996:
     Coin_1[45] Coin_2[20] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3997:
     Coin_1[45] Coin_2[20] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3998:
     Coin_1[45] Coin_2[25] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #3999:
     Coin_1[46] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #4000:
     Coin_1[46] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4001:
     Coin_1[46] Coin_2[2] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4002:
     Coin_1[46] Coin_2[2] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4003:
     Coin_1[46] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4004:
     Coin_1[46] Coin_2[2] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4005:
     Coin_1[46] Coin_2[2] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4006:
     Coin_1[46] Coin_2[2] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4007:
     Coin_1[46] Coin_2[2] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4008:
     Coin_1[46] Coin_2[2] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4009:
     Coin_1[46] Coin_2[2] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4010:
     Coin_1[46] Coin_2[2] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4011:
     Coin_1[46] Coin_2[2] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4012:
     Coin_1[46] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4013:
     Coin_1[46] Coin_2[7] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4014:
     Coin_1[46] Coin_2[7] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4015:
     Coin_1[46] Coin_2[7] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4016:
     Coin_1[46] Coin_2[7] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4017:
     Coin_1[46] Coin_2[7] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4018:
     Coin_1[46] Coin_2[7] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4019:
     Coin_1[46] Coin_2[7] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4020:
     Coin_1[46] Coin_2[7] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4021:
     Coin_1[46] Coin_2[12] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4022:
     Coin_1[46] Coin_2[12] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4023:
     Coin_1[46] Coin_2[12] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4024:
     Coin_1[46] Coin_2[12] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4025:
     Coin_1[46] Coin_2[12] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4026:
     Coin_1[46] Coin_2[12] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4027:
     Coin_1[46] Coin_2[17] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4028:
     Coin_1[46] Coin_2[17] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4029:
     Coin_1[46] Coin_2[17] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4030:
     Coin_1[46] Coin_2[17] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4031:
     Coin_1[46] Coin_2[22] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4032:
     Coin_1[46] Coin_2[22] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4033:
     Coin_1[46] Coin_2[27] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4034:
     Coin_1[47] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4035:
     Coin_1[47] Coin_2[4] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4036:
     Coin_1[47] Coin_2[4] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4037:
     Coin_1[47] Coin_2[4] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4038:
     Coin_1[47] Coin_2[4] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4039:
     Coin_1[47] Coin_2[4] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4040:
     Coin_1[47] Coin_2[4] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4041:
     Coin_1[47] Coin_2[4] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4042:
     Coin_1[47] Coin_2[4] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4043:
     Coin_1[47] Coin_2[9] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4044:
     Coin_1[47] Coin_2[9] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4045:
     Coin_1[47] Coin_2[9] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4046:
     Coin_1[47] Coin_2[9] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4047:
     Coin_1[47] Coin_2[9] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4048:
     Coin_1[47] Coin_2[9] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4049:
     Coin_1[47] Coin_2[14] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4050:
     Coin_1[47] Coin_2[14] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4051:
     Coin_1[47] Coin_2[14] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4052:
     Coin_1[47] Coin_2[14] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4053:
     Coin_1[47] Coin_2[19] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4054:
     Coin_1[47] Coin_2[19] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4055:
     Coin_1[47] Coin_2[24] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4056:
     Coin_1[48] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #4057:
     Coin_1[48] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4058:
     Coin_1[48] Coin_2[1] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4059:
     Coin_1[48] Coin_2[1] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4060:
     Coin_1[48] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4061:
     Coin_1[48] Coin_2[1] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4062:
     Coin_1[48] Coin_2[1] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4063:
     Coin_1[48] Coin_2[1] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4064:
     Coin_1[48] Coin_2[1] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4065:
     Coin_1[48] Coin_2[1] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4066:
     Coin_1[48] Coin_2[1] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4067:
     Coin_1[48] Coin_2[1] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4068:
     Coin_1[48] Coin_2[1] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4069:
     Coin_1[48] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4070:
     Coin_1[48] Coin_2[6] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4071:
     Coin_1[48] Coin_2[6] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4072:
     Coin_1[48] Coin_2[6] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4073:
     Coin_1[48] Coin_2[6] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4074:
     Coin_1[48] Coin_2[6] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4075:
     Coin_1[48] Coin_2[6] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4076:
     Coin_1[48] Coin_2[6] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4077:
     Coin_1[48] Coin_2[6] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4078:
     Coin_1[48] Coin_2[11] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4079:
     Coin_1[48] Coin_2[11] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4080:
     Coin_1[48] Coin_2[11] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4081:
     Coin_1[48] Coin_2[11] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4082:
     Coin_1[48] Coin_2[11] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4083:
     Coin_1[48] Coin_2[11] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4084:
     Coin_1[48] Coin_2[16] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4085:
     Coin_1[48] Coin_2[16] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4086:
     Coin_1[48] Coin_2[16] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4087:
     Coin_1[48] Coin_2[16] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4088:
     Coin_1[48] Coin_2[21] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4089:
     Coin_1[48] Coin_2[21] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4090:
     Coin_1[48] Coin_2[26] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4091:
     Coin_1[49] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4092:
     Coin_1[49] Coin_2[3] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4093:
     Coin_1[49] Coin_2[3] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4094:
     Coin_1[49] Coin_2[3] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4095:
     Coin_1[49] Coin_2[3] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4096:
     Coin_1[49] Coin_2[3] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4097:
     Coin_1[49] Coin_2[3] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4098:
     Coin_1[49] Coin_2[3] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4099:
     Coin_1[49] Coin_2[3] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4100:
     Coin_1[49] Coin_2[8] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4101:
     Coin_1[49] Coin_2[8] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4102:
     Coin_1[49] Coin_2[8] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4103:
     Coin_1[49] Coin_2[8] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4104:
     Coin_1[49] Coin_2[8] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4105:
     Coin_1[49] Coin_2[8] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4106:
     Coin_1[49] Coin_2[13] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4107:
     Coin_1[49] Coin_2[13] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4108:
     Coin_1[49] Coin_2[13] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4109:
     Coin_1[49] Coin_2[13] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4110:
     Coin_1[49] Coin_2[18] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4111:
     Coin_1[49] Coin_2[18] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4112:
     Coin_1[49] Coin_2[23] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4113:
     Coin_1[50] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[1] Coin_100[0]
Solution #4114:
     Coin_1[50] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4115:
     Coin_1[50] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4116:
     Coin_1[50] Coin_2[0] Coin_5[0] Coin_10[5] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4117:
     Coin_1[50] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4118:
     Coin_1[50] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4119:
     Coin_1[50] Coin_2[0] Coin_5[2] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4120:
     Coin_1[50] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4121:
     Coin_1[50] Coin_2[0] Coin_5[4] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4122:
     Coin_1[50] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4123:
     Coin_1[50] Coin_2[0] Coin_5[6] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4124:
     Coin_1[50] Coin_2[0] Coin_5[8] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4125:
     Coin_1[50] Coin_2[0] Coin_5[10] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4126:
     Coin_1[50] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4127:
     Coin_1[50] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4128:
     Coin_1[50] Coin_2[5] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4129:
     Coin_1[50] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4130:
     Coin_1[50] Coin_2[5] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4131:
     Coin_1[50] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4132:
     Coin_1[50] Coin_2[5] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4133:
     Coin_1[50] Coin_2[5] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4134:
     Coin_1[50] Coin_2[5] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4135:
     Coin_1[50] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4136:
     Coin_1[50] Coin_2[10] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4137:
     Coin_1[50] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4138:
     Coin_1[50] Coin_2[10] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4139:
     Coin_1[50] Coin_2[10] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4140:
     Coin_1[50] Coin_2[10] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4141:
     Coin_1[50] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4142:
     Coin_1[50] Coin_2[15] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4143:
     Coin_1[50] Coin_2[15] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4144:
     Coin_1[50] Coin_2[15] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4145:
     Coin_1[50] Coin_2[20] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4146:
     Coin_1[50] Coin_2[20] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4147:
     Coin_1[50] Coin_2[25] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4148:
     Coin_1[51] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4149:
     Coin_1[51] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4150:
     Coin_1[51] Coin_2[2] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4151:
     Coin_1[51] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4152:
     Coin_1[51] Coin_2[2] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4153:
     Coin_1[51] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4154:
     Coin_1[51] Coin_2[2] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4155:
     Coin_1[51] Coin_2[2] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4156:
     Coin_1[51] Coin_2[2] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4157:
     Coin_1[51] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4158:
     Coin_1[51] Coin_2[7] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4159:
     Coin_1[51] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4160:
     Coin_1[51] Coin_2[7] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4161:
     Coin_1[51] Coin_2[7] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4162:
     Coin_1[51] Coin_2[7] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4163:
     Coin_1[51] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4164:
     Coin_1[51] Coin_2[12] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4165:
     Coin_1[51] Coin_2[12] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4166:
     Coin_1[51] Coin_2[12] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4167:
     Coin_1[51] Coin_2[17] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4168:
     Coin_1[51] Coin_2[17] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4169:
     Coin_1[51] Coin_2[22] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4170:
     Coin_1[52] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4171:
     Coin_1[52] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4172:
     Coin_1[52] Coin_2[4] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4173:
     Coin_1[52] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4174:
     Coin_1[52] Coin_2[4] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4175:
     Coin_1[52] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4176:
     Coin_1[52] Coin_2[4] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4177:
     Coin_1[52] Coin_2[4] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4178:
     Coin_1[52] Coin_2[4] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4179:
     Coin_1[52] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4180:
     Coin_1[52] Coin_2[9] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4181:
     Coin_1[52] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4182:
     Coin_1[52] Coin_2[9] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4183:
     Coin_1[52] Coin_2[9] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4184:
     Coin_1[52] Coin_2[9] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4185:
     Coin_1[52] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4186:
     Coin_1[52] Coin_2[14] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4187:
     Coin_1[52] Coin_2[14] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4188:
     Coin_1[52] Coin_2[14] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4189:
     Coin_1[52] Coin_2[19] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4190:
     Coin_1[52] Coin_2[19] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4191:
     Coin_1[52] Coin_2[24] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4192:
     Coin_1[53] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4193:
     Coin_1[53] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4194:
     Coin_1[53] Coin_2[1] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4195:
     Coin_1[53] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4196:
     Coin_1[53] Coin_2[1] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4197:
     Coin_1[53] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4198:
     Coin_1[53] Coin_2[1] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4199:
     Coin_1[53] Coin_2[1] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4200:
     Coin_1[53] Coin_2[1] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4201:
     Coin_1[53] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4202:
     Coin_1[53] Coin_2[6] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4203:
     Coin_1[53] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4204:
     Coin_1[53] Coin_2[6] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4205:
     Coin_1[53] Coin_2[6] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4206:
     Coin_1[53] Coin_2[6] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4207:
     Coin_1[53] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4208:
     Coin_1[53] Coin_2[11] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4209:
     Coin_1[53] Coin_2[11] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4210:
     Coin_1[53] Coin_2[11] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4211:
     Coin_1[53] Coin_2[16] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4212:
     Coin_1[53] Coin_2[16] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4213:
     Coin_1[53] Coin_2[21] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4214:
     Coin_1[54] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4215:
     Coin_1[54] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4216:
     Coin_1[54] Coin_2[3] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4217:
     Coin_1[54] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4218:
     Coin_1[54] Coin_2[3] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4219:
     Coin_1[54] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4220:
     Coin_1[54] Coin_2[3] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4221:
     Coin_1[54] Coin_2[3] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4222:
     Coin_1[54] Coin_2[3] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4223:
     Coin_1[54] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4224:
     Coin_1[54] Coin_2[8] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4225:
     Coin_1[54] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4226:
     Coin_1[54] Coin_2[8] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4227:
     Coin_1[54] Coin_2[8] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4228:
     Coin_1[54] Coin_2[8] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4229:
     Coin_1[54] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4230:
     Coin_1[54] Coin_2[13] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4231:
     Coin_1[54] Coin_2[13] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4232:
     Coin_1[54] Coin_2[13] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4233:
     Coin_1[54] Coin_2[18] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4234:
     Coin_1[54] Coin_2[18] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4235:
     Coin_1[54] Coin_2[23] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4236:
     Coin_1[55] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4237:
     Coin_1[55] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4238:
     Coin_1[55] Coin_2[0] Coin_5[1] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4239:
     Coin_1[55] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4240:
     Coin_1[55] Coin_2[0] Coin_5[3] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4241:
     Coin_1[55] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4242:
     Coin_1[55] Coin_2[0] Coin_5[5] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4243:
     Coin_1[55] Coin_2[0] Coin_5[7] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4244:
     Coin_1[55] Coin_2[0] Coin_5[9] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4245:
     Coin_1[55] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4246:
     Coin_1[55] Coin_2[5] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4247:
     Coin_1[55] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4248:
     Coin_1[55] Coin_2[5] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4249:
     Coin_1[55] Coin_2[5] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4250:
     Coin_1[55] Coin_2[5] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4251:
     Coin_1[55] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4252:
     Coin_1[55] Coin_2[10] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4253:
     Coin_1[55] Coin_2[10] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4254:
     Coin_1[55] Coin_2[10] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4255:
     Coin_1[55] Coin_2[15] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4256:
     Coin_1[55] Coin_2[15] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4257:
     Coin_1[55] Coin_2[20] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4258:
     Coin_1[56] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4259:
     Coin_1[56] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4260:
     Coin_1[56] Coin_2[2] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4261:
     Coin_1[56] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4262:
     Coin_1[56] Coin_2[2] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4263:
     Coin_1[56] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4264:
     Coin_1[56] Coin_2[2] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4265:
     Coin_1[56] Coin_2[2] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4266:
     Coin_1[56] Coin_2[2] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4267:
     Coin_1[56] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4268:
     Coin_1[56] Coin_2[7] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4269:
     Coin_1[56] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4270:
     Coin_1[56] Coin_2[7] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4271:
     Coin_1[56] Coin_2[7] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4272:
     Coin_1[56] Coin_2[7] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4273:
     Coin_1[56] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4274:
     Coin_1[56] Coin_2[12] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4275:
     Coin_1[56] Coin_2[12] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4276:
     Coin_1[56] Coin_2[12] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4277:
     Coin_1[56] Coin_2[17] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4278:
     Coin_1[56] Coin_2[17] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4279:
     Coin_1[56] Coin_2[22] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4280:
     Coin_1[57] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4281:
     Coin_1[57] Coin_2[4] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4282:
     Coin_1[57] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4283:
     Coin_1[57] Coin_2[4] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4284:
     Coin_1[57] Coin_2[4] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4285:
     Coin_1[57] Coin_2[4] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4286:
     Coin_1[57] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4287:
     Coin_1[57] Coin_2[9] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4288:
     Coin_1[57] Coin_2[9] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4289:
     Coin_1[57] Coin_2[9] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4290:
     Coin_1[57] Coin_2[14] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4291:
     Coin_1[57] Coin_2[14] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4292:
     Coin_1[57] Coin_2[19] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4293:
     Coin_1[58] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4294:
     Coin_1[58] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4295:
     Coin_1[58] Coin_2[1] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4296:
     Coin_1[58] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4297:
     Coin_1[58] Coin_2[1] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4298:
     Coin_1[58] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4299:
     Coin_1[58] Coin_2[1] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4300:
     Coin_1[58] Coin_2[1] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4301:
     Coin_1[58] Coin_2[1] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4302:
     Coin_1[58] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4303:
     Coin_1[58] Coin_2[6] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4304:
     Coin_1[58] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4305:
     Coin_1[58] Coin_2[6] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4306:
     Coin_1[58] Coin_2[6] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4307:
     Coin_1[58] Coin_2[6] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4308:
     Coin_1[58] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4309:
     Coin_1[58] Coin_2[11] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4310:
     Coin_1[58] Coin_2[11] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4311:
     Coin_1[58] Coin_2[11] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4312:
     Coin_1[58] Coin_2[16] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4313:
     Coin_1[58] Coin_2[16] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4314:
     Coin_1[58] Coin_2[21] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4315:
     Coin_1[59] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4316:
     Coin_1[59] Coin_2[3] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4317:
     Coin_1[59] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4318:
     Coin_1[59] Coin_2[3] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4319:
     Coin_1[59] Coin_2[3] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4320:
     Coin_1[59] Coin_2[3] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4321:
     Coin_1[59] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4322:
     Coin_1[59] Coin_2[8] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4323:
     Coin_1[59] Coin_2[8] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4324:
     Coin_1[59] Coin_2[8] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4325:
     Coin_1[59] Coin_2[13] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4326:
     Coin_1[59] Coin_2[13] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4327:
     Coin_1[59] Coin_2[18] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4328:
     Coin_1[60] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[2] Coin_50[0] Coin_100[0]
Solution #4329:
     Coin_1[60] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4330:
     Coin_1[60] Coin_2[0] Coin_5[0] Coin_10[4] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4331:
     Coin_1[60] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4332:
     Coin_1[60] Coin_2[0] Coin_5[2] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4333:
     Coin_1[60] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4334:
     Coin_1[60] Coin_2[0] Coin_5[4] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4335:
     Coin_1[60] Coin_2[0] Coin_5[6] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4336:
     Coin_1[60] Coin_2[0] Coin_5[8] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4337:
     Coin_1[60] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4338:
     Coin_1[60] Coin_2[5] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4339:
     Coin_1[60] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4340:
     Coin_1[60] Coin_2[5] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4341:
     Coin_1[60] Coin_2[5] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4342:
     Coin_1[60] Coin_2[5] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4343:
     Coin_1[60] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4344:
     Coin_1[60] Coin_2[10] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4345:
     Coin_1[60] Coin_2[10] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4346:
     Coin_1[60] Coin_2[10] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4347:
     Coin_1[60] Coin_2[15] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4348:
     Coin_1[60] Coin_2[15] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4349:
     Coin_1[60] Coin_2[20] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4350:
     Coin_1[61] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4351:
     Coin_1[61] Coin_2[2] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4352:
     Coin_1[61] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4353:
     Coin_1[61] Coin_2[2] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4354:
     Coin_1[61] Coin_2[2] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4355:
     Coin_1[61] Coin_2[2] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4356:
     Coin_1[61] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4357:
     Coin_1[61] Coin_2[7] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4358:
     Coin_1[61] Coin_2[7] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4359:
     Coin_1[61] Coin_2[7] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4360:
     Coin_1[61] Coin_2[12] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4361:
     Coin_1[61] Coin_2[12] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4362:
     Coin_1[61] Coin_2[17] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4363:
     Coin_1[62] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4364:
     Coin_1[62] Coin_2[4] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4365:
     Coin_1[62] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4366:
     Coin_1[62] Coin_2[4] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4367:
     Coin_1[62] Coin_2[4] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4368:
     Coin_1[62] Coin_2[4] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4369:
     Coin_1[62] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4370:
     Coin_1[62] Coin_2[9] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4371:
     Coin_1[62] Coin_2[9] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4372:
     Coin_1[62] Coin_2[9] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4373:
     Coin_1[62] Coin_2[14] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4374:
     Coin_1[62] Coin_2[14] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4375:
     Coin_1[62] Coin_2[19] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4376:
     Coin_1[63] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4377:
     Coin_1[63] Coin_2[1] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4378:
     Coin_1[63] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4379:
     Coin_1[63] Coin_2[1] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4380:
     Coin_1[63] Coin_2[1] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4381:
     Coin_1[63] Coin_2[1] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4382:
     Coin_1[63] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4383:
     Coin_1[63] Coin_2[6] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4384:
     Coin_1[63] Coin_2[6] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4385:
     Coin_1[63] Coin_2[6] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4386:
     Coin_1[63] Coin_2[11] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4387:
     Coin_1[63] Coin_2[11] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4388:
     Coin_1[63] Coin_2[16] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4389:
     Coin_1[64] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4390:
     Coin_1[64] Coin_2[3] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4391:
     Coin_1[64] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4392:
     Coin_1[64] Coin_2[3] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4393:
     Coin_1[64] Coin_2[3] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4394:
     Coin_1[64] Coin_2[3] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4395:
     Coin_1[64] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4396:
     Coin_1[64] Coin_2[8] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4397:
     Coin_1[64] Coin_2[8] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4398:
     Coin_1[64] Coin_2[8] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4399:
     Coin_1[64] Coin_2[13] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4400:
     Coin_1[64] Coin_2[13] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4401:
     Coin_1[64] Coin_2[18] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4402:
     Coin_1[65] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4403:
     Coin_1[65] Coin_2[0] Coin_5[1] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4404:
     Coin_1[65] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4405:
     Coin_1[65] Coin_2[0] Coin_5[3] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4406:
     Coin_1[65] Coin_2[0] Coin_5[5] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4407:
     Coin_1[65] Coin_2[0] Coin_5[7] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4408:
     Coin_1[65] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4409:
     Coin_1[65] Coin_2[5] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4410:
     Coin_1[65] Coin_2[5] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4411:
     Coin_1[65] Coin_2[5] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4412:
     Coin_1[65] Coin_2[10] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4413:
     Coin_1[65] Coin_2[10] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4414:
     Coin_1[65] Coin_2[15] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4415:
     Coin_1[66] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4416:
     Coin_1[66] Coin_2[2] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4417:
     Coin_1[66] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4418:
     Coin_1[66] Coin_2[2] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4419:
     Coin_1[66] Coin_2[2] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4420:
     Coin_1[66] Coin_2[2] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4421:
     Coin_1[66] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4422:
     Coin_1[66] Coin_2[7] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4423:
     Coin_1[66] Coin_2[7] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4424:
     Coin_1[66] Coin_2[7] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4425:
     Coin_1[66] Coin_2[12] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4426:
     Coin_1[66] Coin_2[12] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4427:
     Coin_1[66] Coin_2[17] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4428:
     Coin_1[67] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4429:
     Coin_1[67] Coin_2[4] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4430:
     Coin_1[67] Coin_2[4] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4431:
     Coin_1[67] Coin_2[4] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4432:
     Coin_1[67] Coin_2[9] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4433:
     Coin_1[67] Coin_2[9] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4434:
     Coin_1[67] Coin_2[14] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4435:
     Coin_1[68] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4436:
     Coin_1[68] Coin_2[1] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4437:
     Coin_1[68] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4438:
     Coin_1[68] Coin_2[1] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4439:
     Coin_1[68] Coin_2[1] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4440:
     Coin_1[68] Coin_2[1] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4441:
     Coin_1[68] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4442:
     Coin_1[68] Coin_2[6] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4443:
     Coin_1[68] Coin_2[6] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4444:
     Coin_1[68] Coin_2[6] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4445:
     Coin_1[68] Coin_2[11] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4446:
     Coin_1[68] Coin_2[11] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4447:
     Coin_1[68] Coin_2[16] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4448:
     Coin_1[69] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4449:
     Coin_1[69] Coin_2[3] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4450:
     Coin_1[69] Coin_2[3] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4451:
     Coin_1[69] Coin_2[3] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4452:
     Coin_1[69] Coin_2[8] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4453:
     Coin_1[69] Coin_2[8] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4454:
     Coin_1[69] Coin_2[13] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4455:
     Coin_1[70] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4456:
     Coin_1[70] Coin_2[0] Coin_5[0] Coin_10[3] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4457:
     Coin_1[70] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4458:
     Coin_1[70] Coin_2[0] Coin_5[2] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4459:
     Coin_1[70] Coin_2[0] Coin_5[4] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4460:
     Coin_1[70] Coin_2[0] Coin_5[6] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4461:
     Coin_1[70] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4462:
     Coin_1[70] Coin_2[5] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4463:
     Coin_1[70] Coin_2[5] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4464:
     Coin_1[70] Coin_2[5] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4465:
     Coin_1[70] Coin_2[10] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4466:
     Coin_1[70] Coin_2[10] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4467:
     Coin_1[70] Coin_2[15] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4468:
     Coin_1[71] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4469:
     Coin_1[71] Coin_2[2] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4470:
     Coin_1[71] Coin_2[2] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4471:
     Coin_1[71] Coin_2[2] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4472:
     Coin_1[71] Coin_2[7] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4473:
     Coin_1[71] Coin_2[7] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4474:
     Coin_1[71] Coin_2[12] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4475:
     Coin_1[72] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4476:
     Coin_1[72] Coin_2[4] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4477:
     Coin_1[72] Coin_2[4] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4478:
     Coin_1[72] Coin_2[4] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4479:
     Coin_1[72] Coin_2[9] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4480:
     Coin_1[72] Coin_2[9] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4481:
     Coin_1[72] Coin_2[14] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4482:
     Coin_1[73] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4483:
     Coin_1[73] Coin_2[1] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4484:
     Coin_1[73] Coin_2[1] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4485:
     Coin_1[73] Coin_2[1] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4486:
     Coin_1[73] Coin_2[6] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4487:
     Coin_1[73] Coin_2[6] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4488:
     Coin_1[73] Coin_2[11] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4489:
     Coin_1[74] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4490:
     Coin_1[74] Coin_2[3] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4491:
     Coin_1[74] Coin_2[3] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4492:
     Coin_1[74] Coin_2[3] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4493:
     Coin_1[74] Coin_2[8] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4494:
     Coin_1[74] Coin_2[8] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4495:
     Coin_1[74] Coin_2[13] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4496:
     Coin_1[75] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4497:
     Coin_1[75] Coin_2[0] Coin_5[1] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4498:
     Coin_1[75] Coin_2[0] Coin_5[3] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4499:
     Coin_1[75] Coin_2[0] Coin_5[5] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4500:
     Coin_1[75] Coin_2[5] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4501:
     Coin_1[75] Coin_2[5] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4502:
     Coin_1[75] Coin_2[10] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4503:
     Coin_1[76] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4504:
     Coin_1[76] Coin_2[2] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4505:
     Coin_1[76] Coin_2[2] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4506:
     Coin_1[76] Coin_2[2] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4507:
     Coin_1[76] Coin_2[7] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4508:
     Coin_1[76] Coin_2[7] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4509:
     Coin_1[76] Coin_2[12] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4510:
     Coin_1[77] Coin_2[4] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4511:
     Coin_1[77] Coin_2[4] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4512:
     Coin_1[77] Coin_2[9] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4513:
     Coin_1[78] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4514:
     Coin_1[78] Coin_2[1] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4515:
     Coin_1[78] Coin_2[1] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4516:
     Coin_1[78] Coin_2[1] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4517:
     Coin_1[78] Coin_2[6] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4518:
     Coin_1[78] Coin_2[6] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4519:
     Coin_1[78] Coin_2[11] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4520:
     Coin_1[79] Coin_2[3] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4521:
     Coin_1[79] Coin_2[3] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4522:
     Coin_1[79] Coin_2[8] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4523:
     Coin_1[80] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[1] Coin_50[0] Coin_100[0]
Solution #4524:
     Coin_1[80] Coin_2[0] Coin_5[0] Coin_10[2] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4525:
     Coin_1[80] Coin_2[0] Coin_5[2] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4526:
     Coin_1[80] Coin_2[0] Coin_5[4] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4527:
     Coin_1[80] Coin_2[5] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4528:
     Coin_1[80] Coin_2[5] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4529:
     Coin_1[80] Coin_2[10] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4530:
     Coin_1[81] Coin_2[2] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4531:
     Coin_1[81] Coin_2[2] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4532:
     Coin_1[81] Coin_2[7] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4533:
     Coin_1[82] Coin_2[4] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4534:
     Coin_1[82] Coin_2[4] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4535:
     Coin_1[82] Coin_2[9] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4536:
     Coin_1[83] Coin_2[1] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4537:
     Coin_1[83] Coin_2[1] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4538:
     Coin_1[83] Coin_2[6] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4539:
     Coin_1[84] Coin_2[3] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4540:
     Coin_1[84] Coin_2[3] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4541:
     Coin_1[84] Coin_2[8] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4542:
     Coin_1[85] Coin_2[0] Coin_5[1] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4543:
     Coin_1[85] Coin_2[0] Coin_5[3] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4544:
     Coin_1[85] Coin_2[5] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4545:
     Coin_1[86] Coin_2[2] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4546:
     Coin_1[86] Coin_2[2] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4547:
     Coin_1[86] Coin_2[7] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4548:
     Coin_1[87] Coin_2[4] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4549:
     Coin_1[88] Coin_2[1] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4550:
     Coin_1[88] Coin_2[1] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4551:
     Coin_1[88] Coin_2[6] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4552:
     Coin_1[89] Coin_2[3] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4553:
     Coin_1[90] Coin_2[0] Coin_5[0] Coin_10[1] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4554:
     Coin_1[90] Coin_2[0] Coin_5[2] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4555:
     Coin_1[90] Coin_2[5] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4556:
     Coin_1[91] Coin_2[2] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4557:
     Coin_1[92] Coin_2[4] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4558:
     Coin_1[93] Coin_2[1] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4559:
     Coin_1[94] Coin_2[3] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4560:
     Coin_1[95] Coin_2[0] Coin_5[1] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4561:
     Coin_1[96] Coin_2[2] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4562:
     Coin_1[98] Coin_2[1] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Solution #4563:
     Coin_1[100] Coin_2[0] Coin_5[0] Coin_10[0] Coin_20[0] Coin_50[0] Coin_100[0]
Total elapsed time: 1278 ms
*/
