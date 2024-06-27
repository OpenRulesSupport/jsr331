predicate 
knapsack(int:n, array[int] of int:w, array[int] of int:p, 
         var int: W, var int: P, array[int] of var int:x) = 
	assert(index_set(w) = 1..n,  
		"index set of weights must be 1..n",
	assert(index_set(p) = 1..n,  
		"index set of profits must be 1..n",
	assert(lb(x) >= 0,
		"variables in knapsack must be non-negative",
	assert(lb(w) >= 0,
		"weights in knapsack must be non-negative",

	 jacop_knapsack(w,p,W,P,x)

	 ))));

predicate
jacop_knapsack(array[int] of int:w, array[int] of int:p, 
         var int: W, var int: P, array[int] of var int:x);


%	knapsack_decomp(n,w,p,W,P,x);


%predicate 
%knapsack_decomp(int:n, array[int] of int:w, array[int] of int:p, 
%         var int: W, var int: P, array[int] of var int:x) = 
%	assert(index_set(w) = 1..n,  
%		"index set of weights must be 1..n",
%	assert(index_set(p) = 1..n,  
%		"index set of profits must be 1..n",
%	assert(lb(x) >= 0,
%		"variables in knapsack must be non-negative",
%	assert(lb(w) >= 0,
%		"weights in knapsack must be non-negative",
%	let { int: wmax = ub(W),
%	      array[0..n,0..wmax] of var 0..ub(P): prof
%	    } in
%	forall(k in 0..wmax)(prof[0,k] = 0) /\
%	forall(i in 1..n)(prof[i,0] = 0) /\
%	forall(i in 1..n, k in 1..wmax)(
%		prof[i,k] == max(j in dom(x[i]) where k - j * w[i] >= 0)(
%				bool2int(x[i] == j) * (prof[i-1,k - j*w[i]] + j * p[i]))
%	) /\
%	P = prof[n,W]
%	))));
