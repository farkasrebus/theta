package hu.bme.mit.inf.ttmc.constraint.tests;

import org.junit.Before;
import org.junit.Test;

import hu.bme.mit.inf.ttmc.constraint.ConstraintManager;
import hu.bme.mit.inf.ttmc.constraint.ConstraintManagerImpl;
import hu.bme.mit.inf.ttmc.constraint.expr.ConstRefExpr;
import hu.bme.mit.inf.ttmc.constraint.factory.DeclFactory;
import hu.bme.mit.inf.ttmc.constraint.factory.ExprFactory;
import hu.bme.mit.inf.ttmc.constraint.factory.TypeFactory;
import hu.bme.mit.inf.ttmc.constraint.type.BoolType;
import hu.bme.mit.inf.ttmc.constraint.utils.impl.CNFExprChecker;
import org.junit.Assert;

public class CnfExprCheckerTests {
	// Manager and factories
		private ConstraintManager manager;
		private ExprFactory efc;
		private DeclFactory dfc;
		private TypeFactory tfc;
		// Constants for testing
		private ConstRefExpr<BoolType> cA, cB, cC;
		// CNF checker
		private CNFExprChecker cnfChecker;
		
		@Before
		public void before(){
			// Create manager and get factories
			manager = new ConstraintManagerImpl();
			efc = manager.getExprFactory();
			dfc = manager.getDeclFactory();
			tfc = manager.getTypeFactory();
			// Create constants
			cA = efc.Ref(dfc.Const("A", tfc.Bool()));
			cB = efc.Ref(dfc.Const("B", tfc.Bool()));
			cC = efc.Ref(dfc.Const("C", tfc.Bool()));
			// Create CNF checker
			cnfChecker = new CNFExprChecker();
		}
		
		@SuppressWarnings("unchecked")
		@Test
		public void testCnfCheckerTrue() {
			// A
			Assert.assertTrue(cnfChecker.isExprInCNF(cA));
			// !A
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.Not(cA)));
			// !A or B or !C
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.Or(efc.Not(cA), cB, efc.Not(cC))));
			// !A and B and !C
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.And(efc.Not(cA), cB, efc.Not(cC))));
			// !A and (B and !C)
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.And(efc.Not(cA), efc.And(cB, efc.Not(cC)))));
			// !A and (B or !C)
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.And(efc.Not(cA), efc.Or(cB, efc.Not(cC)))));
		}
		

		@SuppressWarnings("unchecked")
		@Test
		public void testCnfCheckerFalse() {
			// !!A
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Not(efc.Not(cA))));
			// !A and B and !C
			Assert.assertTrue(cnfChecker.isExprInCNF(efc.And(efc.Not(cA), cB, efc.Not(cC))));
			// !A or (B and !C)
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Or(efc.Not(cA), efc.And(cB, efc.Not(cC)))));
			// !(A and B)
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Not(efc.And(cA, cB))));
			// !(A or B)
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Not(efc.Or(cA, cB))));
			// A -> B
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Imply(cA, cB)));
			// A <-> B
			Assert.assertFalse(cnfChecker.isExprInCNF(efc.Iff(cA, cB)));
		}
}
