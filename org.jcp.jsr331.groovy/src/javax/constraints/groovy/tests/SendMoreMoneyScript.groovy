SendMoreMoneyScriptackage javax.constraints.groovy.tests

// define variables
S = variable("S",1..9)
E = variable("E",[0,1,2,3,4,5,6,7,8,9])
N = variable("N",0,9)
D = variable("D",0,9)
M = variable("M",1,9)
O = variable("O",0,9)
R = variable("R",0,9)
Y = variable("Y",0,9)

postAllDifferent([ S, E, N, D, M, O, R, Y ])

// Post constraint SEND + MORE = MONEY 
SEND = 1000*S + 100*E + 10*N + D
MORE = 1000*M + 100*O + 10*R + E
MONEY = 10000*M + 1000*O + 100*N + 10*E + Y
p.post(SEND + MORE, "=", MONEY)

// Problem Resolution
findSolution()
log "Solution: ${SEND} + ${MORE} = ${MONEY}"