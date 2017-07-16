package hu.bme.mit.theta.core.utils;

import static hu.bme.mit.theta.core.decl.Decls.Const;
import static hu.bme.mit.theta.core.type.anytype.Exprs.Ite;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Bool;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.False;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Imply;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Or;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.True;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Xor;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Add;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Div;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Eq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Geq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Gt;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Int;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Leq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Lt;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Mul;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Neg;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Neq;
import static hu.bme.mit.theta.core.type.inttype.IntExprs.Sub;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Add;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Div;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Eq;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Geq;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Gt;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Leq;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Lt;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Mul;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Neg;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Neq;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Rat;
import static hu.bme.mit.theta.core.type.rattype.RatExprs.Sub;
import static hu.bme.mit.theta.core.utils.ExprUtils.simplify;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hu.bme.mit.theta.core.Expr;
import hu.bme.mit.theta.core.decl.ConstDecl;
import hu.bme.mit.theta.core.model.BasicValuation;
import hu.bme.mit.theta.core.model.Valuation;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.core.type.inttype.IntType;

public class ExprSimplifierTest {

	private final ConstDecl<BoolType> cx = Const("x", Bool());
	private final ConstDecl<BoolType> cy = Const("y", Bool());
	private final ConstDecl<BoolType> cz = Const("z", Bool());
	private final ConstDecl<IntType> ca = Const("a", Int());
	private final ConstDecl<IntType> cb = Const("b", Int());
	private final ConstDecl<IntType> cc = Const("c", Int());

	private final Expr<BoolType> x = cx.getRef();
	private final Expr<BoolType> y = cy.getRef();
	private final Expr<BoolType> z = cz.getRef();
	private final Expr<IntType> a = ca.getRef();
	private final Expr<IntType> b = cb.getRef();
	private final Expr<IntType> c = cc.getRef();

	@Test
	public void testNot() {
		assertEquals(False(), simplify(Not(And(True(), True()))));
		assertEquals(True(), simplify(Not(And(False(), True()))));
	}

	@Test
	public void testAnd() {
		assertEquals(x, simplify(And(True(), x, True())));
		assertEquals(False(), simplify(And(True(), x, False())));
		assertEquals(And(x, y, z), simplify(And(x, And(y, z))));
		assertEquals(True(), simplify(And(True(), True())));
	}

	@Test
	public void testOr() {
		assertEquals(x, simplify(Or(False(), x, False())));
		assertEquals(True(), simplify(Or(False(), x, True())));
	}

	@Test
	public void testImply() {
		assertEquals(True(), simplify(Imply(False(), x)));
		assertEquals(True(), simplify(Imply(x, True())));
		assertEquals(x, simplify(Imply(True(), x)));
		assertEquals(Not(x), simplify(Imply(x, False())));
	}

	@Test
	public void testIff() {
		assertEquals(Not(x), simplify(Iff(False(), x)));
		assertEquals(x, simplify(Iff(x, True())));
		assertEquals(x, simplify(Iff(True(), x)));
		assertEquals(Not(x), simplify(Iff(x, False())));
		assertEquals(True(), simplify(Iff(x, x)));
	}

	@Test
	public void testXor() {
		assertEquals(x, simplify(Xor(False(), x)));
		assertEquals(Not(x), simplify(Xor(x, True())));
		assertEquals(Not(x), simplify(Xor(True(), x)));
		assertEquals(x, simplify(Xor(x, False())));
		assertEquals(False(), simplify(Xor(x, x)));
	}

	@Test
	public void testEq() {
		assertEquals(True(), simplify(Eq(Int(2), Int(2))));
		assertEquals(False(), simplify(Eq(Int(2), Int(-2))));
		assertEquals(True(), simplify(Eq(Rat(1, 2), Rat(1, 2))));
		assertEquals(False(), simplify(Eq(Rat(1, 2), Rat(-1, 2))));
		assertEquals(True(), simplify(Eq(a, a)));
		assertEquals(Eq(a, b), simplify(Eq(a, b)));
	}

	@Test
	public void testNeq() {
		assertEquals(False(), simplify(Neq(Int(2), Int(2))));
		assertEquals(True(), simplify(Neq(Int(2), Int(-2))));
		assertEquals(False(), simplify(Neq(Rat(1, 2), Rat(1, 2))));
		assertEquals(True(), simplify(Neq(Rat(1, 2), Rat(-1, 2))));
		assertEquals(False(), simplify(Neq(a, a)));
		assertEquals(Neq(a, b), simplify(Neq(a, b)));
	}

	@Test
	public void testGeq() {
		assertEquals(True(), simplify(Geq(Rat(8, 4), Rat(2, 1))));
		assertEquals(True(), simplify(Geq(Rat(3, 4), Rat(2, 3))));
		assertEquals(True(), simplify(Geq(Rat(9, 4), Rat(2, 1))));
		assertEquals(False(), simplify(Geq(Rat(2, 1), Rat(9, 4))));
		assertEquals(True(), simplify(Geq(a, a)));
	}

	@Test
	public void testGt() {
		assertEquals(False(), simplify(Gt(Rat(8, 4), Rat(2, 1))));
		assertEquals(True(), simplify(Gt(Rat(3, 4), Rat(2, 3))));
		assertEquals(True(), simplify(Gt(Rat(9, 4), Rat(2, 1))));
		assertEquals(False(), simplify(Gt(Rat(2, 1), Rat(9, 4))));
		assertEquals(False(), simplify(Gt(a, a)));
	}

	@Test
	public void testLeq() {
		assertEquals(True(), simplify(Leq(Rat(8, 4), Rat(2, 1))));
		assertEquals(True(), simplify(Leq(Rat(2, 3), Rat(3, 4))));
		assertEquals(True(), simplify(Leq(Rat(2, 1), Rat(9, 4))));
		assertEquals(False(), simplify(Leq(Rat(9, 4), Rat(2, 1))));
		assertEquals(True(), simplify(Leq(a, a)));
	}

	@Test
	public void testLt() {
		assertEquals(False(), simplify(Lt(Rat(2, 1), Rat(8, 4))));
		assertEquals(True(), simplify(Lt(Rat(2, 3), Rat(3, 4))));
		assertEquals(True(), simplify(Lt(Rat(2, 1), Rat(9, 4))));
		assertEquals(False(), simplify(Lt(Rat(9, 4), Rat(2, 1))));
		assertEquals(False(), simplify(Lt(a, a)));
	}

	@Test
	public void testIntDiv() {
		assertEquals(Int(0), simplify(Div(Int(1), Int(2))));
		assertEquals(Int(1), simplify(Div(Int(3), Int(2))));
		assertEquals(Div(Int(0), a), simplify(Div(Int(0), a)));
	}

	@Test
	public void testRatDiv() {
		assertEquals(Rat(8, 9), simplify(Div(Rat(2, 3), Rat(3, 4))));
		assertEquals(Rat(1, 2), simplify(Div(Rat(2, 3), Rat(4, 3))));
		assertEquals(Rat(1, 3), simplify(Div(Rat(2, 3), Rat(2, 1))));
		assertEquals(Rat(1, 2), simplify(Div(Rat(2, 1), Rat(4, 1))));
		assertEquals(Div(Int(0), a), simplify(Div(Int(0), a)));
	}

	@Test
	public void testNeg() {
		assertEquals(Rat(1, 2), simplify(Neg(Neg(Neg(Neg(Rat(1, 2)))))));
		assertEquals(Rat(-1, 2), simplify(Neg(Neg(Neg(Neg(Neg(Rat(1, 2))))))));
		assertEquals(Int(182), simplify(Neg(Neg(Neg(Neg(Int(182)))))));
		assertEquals(Int(-182), simplify(Neg(Neg(Neg(Neg(Neg(Int(182))))))));
		assertEquals(a, simplify(Neg(Neg(Neg(Neg(a))))));
		assertEquals(Neg(a), simplify(Neg(Neg(Neg(Neg(Neg(a)))))));
	}

	@Test
	public void testSub() {
		assertEquals(Int(-1), simplify(Sub(Int(7), Int(8))));
		assertEquals(Rat(1, 4), simplify(Sub(Rat(3, 4), Rat(1, 2))));
		assertEquals(Rat(-1, 4), simplify(Sub(Rat(3, 4), Rat(1, 1))));
		assertEquals(Int(0), simplify(Sub(a, a)));
	}

	@Test
	public void testAdd() {
		assertEquals(Int(6), simplify(Add(Int(1), Int(2), Int(3))));
		assertEquals(Int(0), simplify(Add(Int(1), Int(2), Int(-3))));
		assertEquals(Rat(7, 12), simplify(Add(Rat(1, 3), Rat(1, 4))));
		assertEquals(Add(a, Int(4)), simplify(Add(Int(1), a, Int(3))));
		assertEquals(a, simplify(Add(Int(-3), a, Int(3))));
		assertEquals(Add(a, b, a, b, c), simplify(Add(a, Add(b, Add(a, Add(b, c))))));
	}

	@Test
	public void testMul() {
		assertEquals(Int(30), simplify(Mul(Int(2), Int(3), Int(5))));
		assertEquals(Mul(Int(10), a), simplify(Mul(Int(2), a, Int(5))));
		assertEquals(Int(0), simplify(Mul(Int(0), a, b)));
		assertEquals(Rat(1, 1), simplify(Mul(Rat(2, 1), Rat(1, 1), Rat(1, 2))));
		assertEquals(Rat(3, 4), simplify(Mul(Rat(3, 2), Rat(1, 1), Rat(1, 2))));
		assertEquals(Mul(a, b, a, b, c), simplify(Mul(a, Mul(b, Mul(a, Mul(b, c))))));
	}

	@Test
	public void testIte() {
		assertEquals(a, simplify(Ite(True(), a, b)));
		assertEquals(b, simplify(Ite(False(), a, b)));
		assertEquals(a, simplify(Ite(True(), Ite(True(), Ite(True(), a, b), b), b)));
	}

	@Test
	public void testComplex() {
		assertEquals(True(), simplify(Iff(And(x, True()), Or(x, False()))));
	}

	@Test
	public void testValuation() {
		final Valuation val = BasicValuation.builder().put(ca, Int(5)).put(cb, Int(9)).build();

		assertEquals(Int(14), simplify(Add(a, b), val));
		assertEquals(Add(c, Int(14)), simplify(Add(a, b, c), val));
	}
}
