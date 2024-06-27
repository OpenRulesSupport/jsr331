% flatzinc.mzn
% vim: ft=zinc ts=4 sw=4 et tw=0
% <rafe@csse.unimelb.edu.au>
% Fri Nov 21 12:17:05 EST 2008
%
% A list of constraints recognised by the G12 FlatZinc interpreter.

predicate all_different(array [int] of var int: as);
predicate array_bool_and(array [int] of var bool: as, var bool: r);
predicate array_bool_element(array [int] of bool: as, var int: b,
    var bool: c);
predicate array_bool_or(array [int] of var bool: as, var bool: r);
predicate array_float_element(array [int] of float: as, var int: b,
    var float: c);
predicate array_int_element(array [int] of int: as, var int: b, var int: c);
predicate array_set_element(array [int] of set of int: as, var int: b,
    var set of int: c);
predicate array_var_bool_element(array [int] of var bool: as, var int: b,
    var bool: c);
predicate array_var_float_element(array [int] of var float: as, var int: b,
    var float: c);
predicate array_var_int_element(array [int] of var int: as, var int: b,
    var int: c);
predicate array_var_set_element(array [int] of var set of int: as,
    var int: b, var set of int: c);
predicate bool2int(var bool: a, var int: b);
predicate bool_and(var bool: a, var bool: b, var bool: r);
predicate bool_clause(array [int] of var bool: as,
    array [int] of var bool: bs);
predicate bool_clause_reif(array [int] of var bool: as,
    array [int] of var bool: bs, var bool: r);
predicate bool_eq(var bool: a, var bool: b);
predicate bool_eq_reif(var bool: a, var bool: b, var bool: r);
predicate bool_ge(var bool: a, var bool: b);
predicate bool_ge_reif(var bool: a, var bool: b, var bool: r);
predicate bool_gt(var bool: a, var bool: b);
predicate bool_gt_reif(var bool: a, var bool: b, var bool: r);
predicate bool_le(var bool: a, var bool: b);
predicate bool_le_reif(var bool: a, var bool: b, var bool: r);
predicate bool_left_imp(var bool: a, var bool: b, var bool: r);
predicate bool_lt(var bool: a, var bool: b);
predicate bool_lt_reif(var bool: a, var bool: b, var bool: r);
predicate bool_ne(var bool: a, var bool: b);
predicate bool_ne_reif(var bool: a, var bool: b, var bool: r);
predicate bool_not(var bool: a, var bool: b);
predicate bool_or(var bool: a, var bool: b, var bool: r);
predicate bool_right_imp(var bool: a, var bool: b, var bool: r);
predicate bool_xor(var bool: a, var bool: b, var bool: r);
predicate float_eq(var float: a, var float: b);
predicate float_ge(var float: a, var float: b);
predicate float_gt(var float: a, var float: b);
predicate float_le(var float: a, var float: b);
predicate float_lin_eq(array [int] of float: as,
    array [int] of var float: bs, float: c);
predicate float_lin_ge(array [int] of float: as,
    array [int] of var float: bs, float: c);
predicate float_lin_gt(array [int] of float: as,
    array [int] of var float: bs, float: c);
predicate float_lin_le(array [int] of float: as,
    array [int] of var float: bs, float: c);
predicate float_lin_lt(array [int] of float: as,
    array [int] of var float: bs, float: c);
predicate float_lt(var float: a, var float: b);
predicate float_minus(var float: a, var float: b, var float: c);
predicate float_negate(var float: a, var float: b);
predicate float_plus(var float: a, var float: b, var float: c);
predicate int_abs(var int: a, var int: b);
predicate int_div(var int: a, var int: b, var int: c);
predicate int_eq(var int: a, var int: b);
predicate int_eq_reif(var int: a, var int: b, var bool: r);
predicate int_ge(var int: a, var int: b);
predicate int_ge_reif(var int: a, var int: b, var bool: r);
predicate int_gt(var int: a, var int: b);
predicate int_gt_reif(var int: a, var int: b, var bool: r);
predicate int_le(var int: a, var int: b);
predicate int_le_reif(var int: a, var int: b, var bool: r);
predicate int_lin_eq(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_eq_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lin_ge(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_ge_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lin_gt(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_gt_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lin_le(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_le_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lin_lt(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_lt_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lin_ne(array [int] of int: as, array [int] of var int: bs,
    int: c);
predicate int_lin_ne_reif(array [int] of int: as, array [int] of var int: bs,
    int: c, var bool: r);
predicate int_lt(var int: a, var int: b);
predicate int_lt_reif(var int: a, var int: b, var bool: r);
predicate int_max(var int: a, var int: b, var int: c);
predicate int_min(var int: a, var int: b, var int: c);
predicate int_minus(var int: a, var int: b, var int: c);
predicate int_mod(var int: a, var int: b, var int: c);
predicate int_ne(var int: a, var int: b);
predicate int_ne_reif(var int: a, var int: b, var bool: r);
predicate int_negate(var int: a, var int: b);
predicate int_plus(var int: a, var int: b, var int: c);
predicate int_times(var int: a, var int: b, var int: c);
predicate set_card(var set of int: a, var int: b);
predicate set_diff(var set of int: a, var set of int: b, var set of int: c);
predicate set_eq(var set of int: a, var set of int: b);
predicate set_eq_reif(var set of int: a, var set of int: b, var bool: r);
predicate set_ge(var set of int: a, var set of int: b);
predicate set_gt(var set of int: a, var set of int: b);
predicate set_in(var int: a, var set of int: b);
predicate set_in_reif(var int: a, var set of int: b, var bool: r);
predicate set_intersect(var set of int: a, var set of int: b,
    var set of int: c);
predicate set_le(var set of int: a, var set of int: b);
predicate set_lt(var set of int: a, var set of int: b);
predicate set_ne(var set of int: a, var set of int: b);
predicate set_ne_reif(var set of int: a, var set of int: b, var bool: r);
predicate set_subset(var set of int: a, var set of int: b);
predicate set_subset_reif(var set of int: a, var set of int: b, var bool: r);
predicate set_superset(var set of int: a, var set of int: b);
predicate set_superset_reif(var set of int: a, var set of int: b,
    var bool: r);
predicate set_symdiff(var set of int: a, var set of int: b,
    var set of int: c);
predicate set_union(var set of int: a, var set of int: b, var set of int: c);

