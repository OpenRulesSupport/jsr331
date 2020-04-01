NAME          BlendingProblem
ROWS
 N  _OBJ_
 E  C1
 E  C2
 E  C3
 E  C4
 L  C5
 L  C6
 L  C7
 G  C8
 L  C9
 G  C10
 L  C11
 G  C12
 L  C13
 L  C14
COLUMNS
    M0000     'MARKER'                 'INTORG'
    Vi0       C1                  21   C2                   1   
    Vi0       C5                   1   C8                   2   
    Vi0       C9                  -1   C14                  1   
    Vi1       C1                  11   C3                   1   
    Vi1       C5                   1   C10                  4   
    Vi1       C11                 -2   C14                  1   
    Vi2       C1                   1   C4                   1   
    Vi2       C5                   1   C12                  6   
    Vi2       C13                 -1   C14                  1   
    Vi3       C1                  31   C2                   1   
    Vi3       C6                   1   C8                  -4   
    Vi3       C9                   1   C14                  1   
    Vi4       C1                  21   C3                   1   
    Vi4       C6                   1   C10                 -2   
    Vi4       C11                  0   C14                  1   
    Vi5       C1                  11   C4                   1   
    Vi5       C6                   1   C12                  0   
    Vi5       C13                  1   C14                  1   
    Vi6       C1                  41   C2                   1   
    Vi6       C7                   1   C8                  -2   
    Vi6       C9                   2   C14                  1   
    Vi7       C1                  31   C3                   1   
    Vi7       C7                   1   C10                  0   
    Vi7       C11                  1   C14                  1   
    Vi8       C1                  21   C4                   1   
    Vi8       C7                   1   C12                  2   
    Vi8       C13                  2   C14                  1   
    Vi9       C1                  -1   C2                 -10   
    Vi10      C1                  -1   C3                 -10   
    Vi11      C1                  -1   C4                 -10   
    Vi12      C1                  -1   
    M0001     'MARKER'                 'INTEND'
    Vi12      _OBJ_                1   
RHS
    rhs       C2                3000   C3                2000   
    rhs       C4                1000   C5                5000   
    rhs       C6                5000   C7                5000   
    rhs       C14              14000   
BOUNDS
 UP bnd       Vi0              14000   
 UP bnd       Vi1              14000   
 UP bnd       Vi2              14000   
 UP bnd       Vi3              14000   
 UP bnd       Vi4              14000   
 UP bnd       Vi5              14000   
 UP bnd       Vi6              14000   
 UP bnd       Vi7              14000   
 UP bnd       Vi8              14000   
 UP bnd       Vi9              14000   
 UP bnd       Vi10             14000   
 UP bnd       Vi11             14000   
 UP bnd       Vi12           6888000   
ENDATA
