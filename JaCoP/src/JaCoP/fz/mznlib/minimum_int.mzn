%-----------------------------------------------------------------------------%
% Constrains 'm' to be the minimum of the values in 'x'.
% Assumptions: |x| > 0.
%-----------------------------------------------------------------------------%

predicate minimum_int(var int: m, array[int] of var int: x) =
     jacop_minimum(m, x);

predicate jacop_minimum(var int: m, array[int] of var int: x);

%    let { int: l = min(index_set(x)),
%          int: u = max(index_set(x)),
%          int: ly = lb(x),
%          int: uy = ub(x),
%          array[l..u] of var ly..uy: y } in
%    y[l] = x[l] /\
%    m = y[u] /\
%    forall (i in l+1 .. u) ( y[i] == min(x[i],y[i-1]) );
