%-----------------------------------------------------------------------------%
% Requires that in each subsequence 'x[i], ..., x[i + l - 1]' the sum of the
% variables is between 'mn' and 'mx'.
%-----------------------------------------------------------------------------%
% XXX: this version is something like O((length(x) - l + 1) * l).  An
% O(length(x)) version may be possible.

predicate sequence(array[int] of var int: x, int: l, int: mn, int: mx) =

%     let {
%       set of int : l_set = {l}
%     }
%     in
%       jacop_sequence(x, l_set, l, mn, mx);


    let { int: lx = min(index_set(x)),
          int: ux = max(index_set(x)),
        } in
        forall (i in lx .. ux-l+1) ( 
            let { var int: sum_of_l = sum(j in i..i+l-1) ( x[j] ) } in
                mn <= sum_of_l /\ sum_of_l <= mx
        );

%predicate jacop_sequence(array[int] of var int: x, set of int: l_set, int: l, int: mn, int: mx);
