%-----------------------------------------------------------------------------%
% Requires that the number of distinct values in 'x' is 'n'.
%-----------------------------------------------------------------------------%
predicate nvalue(var int: n, array[int] of var int: x) =
        jacop_nvalue(n, x);

%    let { int: lx = lb(x),
%          int: ux = ub(x),
%        } in
%        n == sum(j in lx..ux) (
%	   bool2int(exists(i in index_set(x)) ( x[i] = j ))
%        );

predicate jacop_nvalue(var int: n, array[int] of var int: x);
