NAME          BlendingProblem
ROWS
 N  obj
 E  c1
 L  c2
 G  c3
 L  c4
 L  c5
 L  c6
 L  c7
 E  c8
 L  c9
 G  c10
 E  c11
 E  c12
 L  c13
 G  c14
COLUMNS
    Crude1->Super       c1                 -21   c7                   1   
    Crude1->Super       c9                   1   c10                 -2   
    Crude1->Super       c12                  1   c13              0.500   
    Crude1->Regular     c1                 -11   c4               1.500   
    Crude1->Regular     c7                   1   c8                   1   
    Crude1->Regular     c9                   1   c14                 -4   
    Crude1->Diesel      c1                  -1   c3                  -6   
    Crude1->Diesel      c5               0.500   c7                   1   
    Crude1->Diesel      c9                   1   c11                  1   
    Crude2->Super       c1                 -31   c6                   1   
    Crude2->Super       c7                   1   c10                  4   
    Crude2->Super       c12                  1   c13                 -1   
    Crude2->Regular     c1                 -21   c4                   0   
    Crude2->Regular     c6                   1   c7                   1   
    Crude2->Regular     c8                   1   c14                  2   
    Crude2->Diesel      c1                 -11   c3                   0   
    Crude2->Diesel      c5                  -1   c6                   1   
    Crude2->Diesel      c7                   1   c11                  1   
    Crude3->Super       c1                 -41   c2                   1   
    Crude3->Super       c7                   1   c10                  2   
    Crude3->Super       c12                  1   c13                 -2   
    Crude3->Regular     c1                 -31   c2                   1   
    Crude3->Regular     c4                  -1   c7                   1   
    Crude3->Regular     c8                   1   c14                  0   
    Crude3->Diesel      c1                 -21   c2                   1   
    Crude3->Diesel      c3                  -2   c5                  -2   
    Crude3->Diesel      c7                   1   c11                  1   
    adv[Super]          c1                   1   c12                -10   
    adv[Regular]        c1                   1   c8                 -10   
    adv[Diesel]         c1                   1   c11                -10   
    costFunc            c1                   1   obj                 -1   
RHS
    rhs                 c2                5000   c6                5000   
    rhs                 c7               14000   c8                2000   
    rhs                 c9                5000   c11               1000   
    rhs                 c12               3000   
BOUNDS
 UP bnd                 Crude1->Super        5000   
 UP bnd                 Crude1->Regular        5000   
 UP bnd                 Crude1->Diesel        5000   
 UP bnd                 Crude2->Super        2500   
 UP bnd                 Crude2->Regular        5000   
 UP bnd                 Crude2->Diesel        2500   
 UP bnd                 Crude3->Super        1250   
 UP bnd                 Crude3->Regular        5000   
 UP bnd                 Crude3->Diesel        1250   
 UP bnd                 adv[Super]         575   
 UP bnd                 adv[Regular]        1300   
 UP bnd                 adv[Diesel]         775   
 UP bnd                 costFunc        607500   
ENDATA