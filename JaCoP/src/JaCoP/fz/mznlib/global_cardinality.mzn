%-----------------------------------------------------------------------------%
% Requires that 'c[i]' is the number of occurences of 'i' in 'x'.
%-----------------------------------------------------------------------------%
predicate global_cardinality(array[int] of var int: x,
                             array[int] of var int: c) =
    jacop_gcc(x, c, min(index_set(c)));

predicate jacop_gcc(array[int] of var int: x, array[int] of var int: c, int : index);