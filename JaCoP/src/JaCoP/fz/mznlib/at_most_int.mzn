%-----------------------------------------------------------------------------%
% Requires at most 'n' variables in 'x' to take the value 'v'.
%-----------------------------------------------------------------------------%

predicate at_most_int(int: n, array[int] of var int: x, int: v) =
    let {
      var int : n_var :: var_is_introduced,
      set of int : v_set = {v}
    }
    in
      n_var <= n /\
      among(n_var, x, v_set);


%    sum(i in index_set(x)) ( bool2int(x[i] == v) ) <= n;

