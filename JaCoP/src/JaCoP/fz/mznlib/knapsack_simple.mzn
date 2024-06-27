predicate 
knapsack(int:n, array[int] of int:w, array[int] of int:p, 
         var int: W, var int: P, array[int] of var int:x) = 
	knapsack_simple(n,w,p,W,P,x);


predicate 
knapsack_simple(int:n, array[int] of int:w, array[int] of int:p, 
         var int: W, var int: P, array[int] of var int:x) = 
	assert(index_set(w) = 1..n,  
		"index set of weights must be 1..n",
	assert(index_set(p) = 1..n,  
		"index set of profits must be 1..n",
	assert(lb(x) >= 0,
		"variables in knapsack must be non-negative",
	assert(lb(w) >= 0,
		"weights in knapsack must be non-negative",
	sum(i in 1..n)(w[i] * x[i]) = W /\
	sum(i in 1..n)(p[i] * x[i]) = P
	))));
