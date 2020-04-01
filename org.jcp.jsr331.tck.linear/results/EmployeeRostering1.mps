NAME          EmployeeRostering1
ROWS
 N  _OBJ_
 E  C1
 E  C2
 E  C3
 E  C4
 E  C5
 E  C6
 E  C7
 E  C8
COLUMNS
    M0000     'MARKER'                 'INTORG'
    MonFT     C1                   1   C8                 100   
    MonPT     C1                   1   C8                 150   
    TueFT     C2                   1   C8                 100   
    TuePT     C2                   1   C8                 150   
    WedFT     C3                   1   C8                 100   
    WedPT     C3                   1   C8                 150   
    ThuFT     C4                   1   C8                 100   
    ThuPT     C4                   1   C8                 150   
    FriFT     C5                   1   C8                 100   
    FriPT     C5                   1   C8                 150   
    SatFT     C6                   1   C8                 100   
    SatPT     C6                   1   C8                 150   
    SunFT     C7                   1   C8                 100   
    SunPT     C7                   1   C8                 150   
    Cost      C8                  -1   
    M0001     'MARKER'                 'INTEND'
    Cost      _OBJ_               -1   
RHS
    rhs       C1                   5   C2                   8   
    rhs       C3                   9   C4                  10   
    rhs       C5                  16   C6                  18   
    rhs       C7                  12   
BOUNDS
 UP bnd       MonFT               14   
 UP bnd       MonPT                4   
 UP bnd       TueFT               14   
 UP bnd       TuePT                4   
 UP bnd       WedFT               14   
 UP bnd       WedPT                4   
 UP bnd       ThuFT               14   
 UP bnd       ThuPT                4   
 UP bnd       FriFT               14   
 UP bnd       FriPT                4   
 UP bnd       SatFT               14   
 UP bnd       SatPT                4   
 UP bnd       SunFT               14   
 UP bnd       SunPT                4   
 UP bnd       Cost             14000   
ENDATA
