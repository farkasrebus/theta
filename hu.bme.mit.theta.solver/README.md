<<<<<<< HEAD
This project provides a common interfaces over different SMT solvers. SMT solvers can be used to query satisfiability of expressions and to generate interpolants and unsat cores. There is a separate project for each solver that wraps that solver to match the common interface. In order to be able to exchange solvers easily, the common interfaces should be preferred (except instantiating the concrete solvers).
=======
This project provides common interfaces over different SMT solvers. SMT solvers can be used to query satisfiability of expressions and to generate interpolants and unsat cores. There is a separate project for each concrete solver that wraps it to match the common interface. In order to be able to exchange solvers easily, the common interfaces should be preferred (except when instantiating the concrete solvers).
>>>>>>> upstream/master
