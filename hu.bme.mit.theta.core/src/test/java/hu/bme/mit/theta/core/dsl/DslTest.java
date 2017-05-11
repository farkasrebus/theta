package hu.bme.mit.theta.core.dsl;

import static hu.bme.mit.theta.core.expr.Exprs.And;
import static hu.bme.mit.theta.core.expr.Exprs.Eq;
import static hu.bme.mit.theta.core.expr.Exprs.False;
import static hu.bme.mit.theta.core.expr.Exprs.Geq;
import static hu.bme.mit.theta.core.expr.Exprs.Gt;
import static hu.bme.mit.theta.core.expr.Exprs.Iff;
import static hu.bme.mit.theta.core.expr.Exprs.Imply;
import static hu.bme.mit.theta.core.expr.Exprs.Int;
import static hu.bme.mit.theta.core.expr.Exprs.IntDiv;
import static hu.bme.mit.theta.core.expr.Exprs.Ite;
import static hu.bme.mit.theta.core.expr.Exprs.Leq;
import static hu.bme.mit.theta.core.expr.Exprs.Lt;
import static hu.bme.mit.theta.core.expr.Exprs.Mod;
import static hu.bme.mit.theta.core.expr.Exprs.Mul;
import static hu.bme.mit.theta.core.expr.Exprs.Neg;
import static hu.bme.mit.theta.core.expr.Exprs.Neq;
import static hu.bme.mit.theta.core.expr.Exprs.Not;
import static hu.bme.mit.theta.core.expr.Exprs.Or;
import static hu.bme.mit.theta.core.expr.Exprs.Rat;
import static hu.bme.mit.theta.core.expr.Exprs.RatDiv;
import static hu.bme.mit.theta.core.expr.Exprs.Rem;
import static hu.bme.mit.theta.core.expr.Exprs.Sub;
import static hu.bme.mit.theta.core.expr.Exprs.True;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import hu.bme.mit.theta.core.decl.Decl;
import hu.bme.mit.theta.core.expr.Expr;
import hu.bme.mit.theta.core.type.Type;

@RunWith(Parameterized.class)
public class DslTest {

	@Parameter(value = 0)
	public String actual;

	@Parameter(value = 1)
	public Expr<? extends Type> expected;

	@Parameter(value = 2)
	public Collection<Decl<?>> decls;

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {

				{ "true or false and not 1%2 > 2%3", Or(True(), And(False(), Not(Gt(Rat(1, 2), Rat(2, 3))))), null },

				{ "true or (false and not 1 < 2)", Or(True(), And(False(), Not(Lt(Int(1), Int(2))))), null },

				{ "(true or false) and not - 5 = 4 - 1",
						And(Or(True(), False()), Not(Eq(Neg(Int(5)), Sub(Int(4), Int(1))))), null },

				{ "true iff false imply true", Iff(True(), Imply(False(), True())), null },

				{ "1 * 2 * 3 /= 4 - 1", Neq(Mul(Int(1), Int(2), Int(3)), Sub(Int(4), Int(1))), null },

				{ "(1 div 2) <= 4 / 5", Leq(IntDiv(Int(1), Int(2)), RatDiv(Int(4), Int(5))), null },

				{ "if 1 >= 2 then 1 rem 2 else 3 mod 5",
						Ite(Geq(Int(1), Int(2)), Rem(Int(1), Int(2)), Mod(Int(3), Int(5))), null },

		});
	}

	@Test
	public void test() {
		final CoreDslManager manager = new CoreDslManager();

		if (decls != null) {
			decls.forEach(decl -> manager.declare(decl));
		}

		Assert.assertEquals(expected, manager.parseExpr(actual));
	}
}
