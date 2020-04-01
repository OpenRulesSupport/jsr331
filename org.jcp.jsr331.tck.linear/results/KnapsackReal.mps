NAME          KnapsackReal
ROWS
 N  _OBJ_
 L  C1
 E  C2
COLUMNS
    M0000     'MARKER'                 'INTORG'
    Vi0       C1                   1   C2              15.200   
    Vi1       C1                   2   C2              10.750   
    Vi2       C1                   3   C2               5.500   
    M0001     'MARKER'                 'INTEND'
    Vr0       C2                  -1   
    Vr0       _OBJ_                1   
RHS
    rhs       C1                  25   
BOUNDS
 UP bnd       Vi0                 20   
 UP bnd       Vi1                 30   
 UP bnd       Vi2                 40   
 UP bnd       Vr0                  0   
ENDATA
