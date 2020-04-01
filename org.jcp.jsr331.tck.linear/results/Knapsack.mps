NAME          Knapsack
ROWS
 N  _OBJ_
 E  C1
 L  C2
 E  C3
 E  C4
 E  C5
COLUMNS
    M0000     'MARKER'                 'INTORG'
    Vi0       C1                   1   C3                  15   
    Vi0       C4                   1   
    Vi1       C1                   2   C3                  10   
    Vi1       C4                   1   
    Vi2       C1                   3   C3                  18   
    Vi2       C4                   1   
    Vi3       C1                  -1   C2                   1   
    Vi4       C4                  -1   C5                   1   
    Vi5       C3                  -1   
    M0001     'MARKER'                 'INTEND'
    Vi5       _OBJ_                1   
RHS
    rhs       C2                  25   C5                  10   
BOUNDS
 UP bnd       Vi0                 20   
 UP bnd       Vi1                 30   
 UP bnd       Vi2                 40   
 UP bnd       Vi3                200   
 UP bnd       Vi4                 90   
 UP bnd       Vi5               1320   
ENDATA
