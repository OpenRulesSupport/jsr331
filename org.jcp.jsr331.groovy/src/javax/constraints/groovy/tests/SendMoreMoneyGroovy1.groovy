package javax.constraints.groovy.tests

/*-------------------------------------------------------------
 Solve the puzzle:
   S E N D
 + M O R E
 ---------
 M O N E Y
 where different letters represent different digits.
 ------------------------------------------------------------ */

import javax.constraints.groovy.ProblemGroovy;

ProblemGroovy p = new ProblemGroovy("SendMoreMoney");
// define variables
S = p.variable("S",1..9)
E = p.variable("E",[0,1,2,3,4,5,6,7,8,9])
N = p.variable("N",0,9)
D = p.variable("D",0,9)
M = p.variable("M",1,9)
O = p.variable("O",0,9)
R = p.variable("R",0,9)
Y = p.variable("Y",0,9)

p.postAllDifferent([ S, E, N, D, M, O, R, Y ])

// Post constraint SEND + MORE = MONEY 
SEND = 1000*S + 100*E + 10*N + D
MORE = 1000*M + 100*O + 10*R + E
MONEY = 10000*M + 1000*O + 100*N + 10*E + Y
p.post(SEND + MORE, "=", MONEY)

// Problem Resolution
p.solver.findSolution()
p.log "Solution: ${SEND} + ${MORE} = ${MONEY}"

