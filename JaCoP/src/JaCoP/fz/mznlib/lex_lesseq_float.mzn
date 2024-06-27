%-----------------------------------------------------------------------------%
% Requires that the array 'x' is lexicographically less than or equal to
% array 'y'.  Compares them from first to last element, regardless of indices
%-----------------------------------------------------------------------------%

predicate lex_lesseq_float(array[int] of var float: x,
                     array[int] of var float: y) =
    let { int: lx = min(index_set(x)),
          int: ux = max(index_set(x)),
          int: ly = min(index_set(y)),
          int: uy = max(index_set(y)),
          int: size = max(ux - lx, uy - ly),
          array[0..size+1] of var bool: b }
    in
        forall (i in 0..size) (
            b[i] = ((x[lx+i] < y[ly+i])
                     \/ ((x[lx+i] == y[ly+i]) /\ b[i+1]))
        ) /\ b[0];

predicate lex_leq_float(array[int] of var float: x,
		array[int] of var float: y) =
	lex_lesseq(x, y);
