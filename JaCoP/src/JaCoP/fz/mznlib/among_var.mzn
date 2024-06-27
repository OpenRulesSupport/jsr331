%-----------------------------------------------------------------------------%
% Requires exactly 'n' variables in 'x' to take one of the values in 'v'.
%-----------------------------------------------------------------------------%
predicate among_var(var int: n, array[int] of var int: x, array[int] of var int: v) =
	  jacop_among_var(x, v, n);

predicate jacop_among_var(array[int] of var int: x, array[int] of var int: v, var int: n);