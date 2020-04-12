Problem:    InsideOutsideProduction
Rows:       12
Columns:    12
Non-zeros:  30
Status:     OPTIMAL
Objective:  _OBJ_ = 372 (MINimum)

   No.   Row name   St   Activity     Lower bound   Upper bound    Marginal
------ ------------ -- ------------- ------------- ------------- -------------
     1 _OBJ_        B            372                             
     2 C1           NS             0             0             =            -1 
     3 C2           NS             0             0             =            -1 
     4 C3           NS             0             0             =            -1 
     5 C4           NU            20                          20          -0.4 
     6 C5           B              8                          40 
     7 C6           NS             0             0             =           0.8 
     8 C7           NL           100           100                         0.8 
     9 C8           NS             0             0             =           0.9 
    10 C9           NL           200           200                         0.9 
    11 C10          NS             0             0             =           0.4 
    12 C11          NL           300           300                         0.4 

   No. Column name  St   Activity     Lower bound   Upper bound    Marginal
------ ------------ -- ------------- ------------- ------------- -------------
     1 Vr0          B             40             0           100 
     2 Vr1          NL             0             0           200          0.06 
     3 Vr2          NL             0             0           300          0.02 
     4 Vr3          B             60             0           100 
     5 Vr4          B            200             0           200 
     6 Vr5          B            300             0           300 
     7 Vr6          B             24             0           310 
     8 Vr7          B            348             0           380 
     9 Vr8          B            100             0           200 
    10 Vr9          B            200             0           400 
    11 Vr10         B            300             0           600 
    12 Vr11         B            372             0           690 

Karush-Kuhn-Tucker optimality conditions:

KKT.PE: max.abs.err = 0.00e+000 on row 0
        max.rel.err = 0.00e+000 on row 0
        High quality

KKT.PB: max.abs.err = 0.00e+000 on row 0
        max.rel.err = 0.00e+000 on row 0
        High quality

KKT.DE: max.abs.err = 1.11e-016 on column 5
        max.rel.err = 3.97e-017 on column 5
        High quality

KKT.DB: max.abs.err = 0.00e+000 on row 0
        max.rel.err = 0.00e+000 on row 0
        High quality

End of output
