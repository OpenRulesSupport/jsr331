NAME          BlendingProblemReal
ROWS
 N  _OBJ_
 E  C1
 E  C2
 E  C3
 E  C4
 L  C5
 L  C6
 L  C7
 E  C8
 G  C9
 E  C10
 L  C11
 E  C12
 G  C13
 E  C14
 L  C15
 E  C16
 G  C17
 E  C18
 L  C19
 L  C20
COLUMNS
    M0000     'MARKER'                 'INTORG'
    Crd1-Sup  C1                  21   C2                   1   
    Crd1-Sup  C5                   1   C8                   2   
    Crd1-Sup  C10             -0.500   C20                  1   
    Crd1-Reg  C1                  11   C3                   1   
    Crd1-Reg  C5                   1   C12                  4   
    Crd1-Reg  C14             -1.500   C20                  1   
    Crd1-Die  C1                   1   C4                   1   
    Crd1-Die  C5                   1   C16                  6   
    Crd1-Die  C18             -0.500   C20                  1   
    Crd2-Sup  C1                  31   C2                   1   
    Crd2-Sup  C6                   1   C8                  -4   
    Crd2-Sup  C10                  1   C20                  1   
    Crd2-Reg  C1                  21   C3                   1   
    Crd2-Reg  C6                   1   C12                 -2   
    Crd2-Reg  C14                  0   C20                  1   
    Crd2-Die  C1                  11   C4                   1   
    Crd2-Die  C6                   1   C16                  0   
    Crd2-Die  C18                  1   C20                  1   
    Crd3-Sup  C1                  41   C2                   1   
    Crd3-Sup  C7                   1   C8                  -2   
    Crd3-Sup  C10                  2   C20                  1   
    Crd3-Reg  C1                  31   C3                   1   
    Crd3-Reg  C7                   1   C12                  0   
    Crd3-Reg  C14                  1   C20                  1   
    Crd3-Die  C1                  21   C4                   1   
    Crd3-Die  C7                   1   C16                  2   
    Crd3-Die  C18                  2   C20                  1   
    adv[Sup]  C1                  -1   C2                 -10   
    adv[Reg]  C1                  -1   C3                 -10   
    adv[Die]  C1                  -1   C4                 -10   
    costFunc  C1                  -1   
    M0001     'MARKER'                 'INTEND'
    costFunc  _OBJ_               -1   
RHS
    rhs       C2                3000   C3                2000   
    rhs       C4                1000   C5                5000   
    rhs       C6                5000   C7                5000   
    rhs       C20              14000   
BOUNDS
 UP bnd       Crd1-Sup         14000   
 UP bnd       Crd1-Reg         14000   
 UP bnd       Crd1-Die         14000   
 UP bnd       Crd2-Sup         14000   
 UP bnd       Crd2-Reg         14000   
 UP bnd       Crd2-Die         14000   
 UP bnd       Crd3-Sup         14000   
 UP bnd       Crd3-Reg         14000   
 UP bnd       Crd3-Die         14000   
 UP bnd       adv[Sup]         14000   
 UP bnd       adv[Reg]         14000   
 UP bnd       adv[Die]         14000   
 UP bnd       costFunc             0   
ENDATA
