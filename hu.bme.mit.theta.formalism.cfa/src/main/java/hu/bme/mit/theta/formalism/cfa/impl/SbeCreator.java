package hu.bme.mit.theta.formalism.cfa.impl;

import static hu.bme.mit.theta.core.expr.Exprs.Not;
import static hu.bme.mit.theta.core.stmt.impl.Stmts.Assign;
import static hu.bme.mit.theta.core.stmt.impl.Stmts.Assume;
import static hu.bme.mit.theta.core.stmt.impl.Stmts.Havoc;

import java.util.List;

import hu.bme.mit.theta.common.Product2;
import hu.bme.mit.theta.common.Tuple;
import hu.bme.mit.theta.core.stmt.AssertStmt;
import hu.bme.mit.theta.core.stmt.AssignStmt;
import hu.bme.mit.theta.core.stmt.AssumeStmt;
import hu.bme.mit.theta.core.stmt.BlockStmt;
import hu.bme.mit.theta.core.stmt.DeclStmt;
import hu.bme.mit.theta.core.stmt.DoStmt;
import hu.bme.mit.theta.core.stmt.HavocStmt;
import hu.bme.mit.theta.core.stmt.IfElseStmt;
import hu.bme.mit.theta.core.stmt.IfStmt;
import hu.bme.mit.theta.core.stmt.ReturnStmt;
import hu.bme.mit.theta.core.stmt.SkipStmt;
import hu.bme.mit.theta.core.stmt.Stmt;
import hu.bme.mit.theta.core.stmt.WhileStmt;
import hu.bme.mit.theta.core.type.Type;
import hu.bme.mit.theta.core.utils.StmtVisitor;
import hu.bme.mit.theta.formalism.cfa.CFA;
import hu.bme.mit.theta.formalism.cfa.CfaEdge;
import hu.bme.mit.theta.formalism.cfa.CfaLoc;

public class SbeCreator {

	public static CFA create(final Stmt stmt) {
		final MutableCfa cfa = new MutableCfa();
		final SBECreatorVisitor visitor = new SBECreatorVisitor(cfa);
		stmt.accept(visitor, Tuple.of(cfa.getInitLoc(), cfa.getFinalLoc()));
		return ImmutableCfa.copyOf(cfa);
	}

	private static class SBECreatorVisitor implements StmtVisitor<Product2<CfaLoc, CfaLoc>, Void> {

		private final MutableCfa cfa;

		private SBECreatorVisitor(final MutableCfa cfa) {
			this.cfa = cfa;
		}

		private Void visitSimple(final Stmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaEdge edge = cfa.createEdge(source, target);
			edge.getStmts().add(stmt);

			return null;
		}

		@Override
		public Void visit(final SkipStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			cfa.createEdge(source, target);

			return null;
		}

		@Override
		public <DeclType extends Type, ExprType extends DeclType> Void visit(final DeclStmt<DeclType, ExprType> stmt,
				final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaEdge edge = cfa.createEdge(source, target);
			if (stmt.getInitVal().isPresent()) {
				edge.getStmts().add(Assign(stmt.getVarDecl(), stmt.getInitVal().get()));
			} else {
				edge.getStmts().add(Havoc(stmt.getVarDecl()));
			}

			return null;
		}

		@Override
		public Void visit(final AssumeStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public Void visit(final AssertStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaEdge normalEdge = cfa.createEdge(source, target);
			normalEdge.getStmts().add(Assume(stmt.getCond()));

			final CfaEdge errorEdge = cfa.createEdge(source, cfa.getErrorLoc());
			errorEdge.getStmts().add(Assume(Not(stmt.getCond())));

			return null;
		}

		@Override
		public <DeclType extends Type, ExprType extends DeclType> Void visit(final AssignStmt<DeclType, ExprType> stmt,
				final Product2<CfaLoc, CfaLoc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public <DeclType extends Type> Void visit(final HavocStmt<DeclType> stmt,
				final Product2<CfaLoc, CfaLoc> param) {
			return visitSimple(stmt, param);
		}

		@Override
		public Void visit(final BlockStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final List<? extends Stmt> stmts = stmt.getStmts();

			if (stmts.isEmpty()) {
				cfa.createEdge(source, target);
			} else {
				final Stmt head = stmts.get(0);
				final List<? extends Stmt> tail = stmts.subList(1, stmts.size());
				processNonEmptyBlock(cfa, source, target, head, tail);
			}

			return null;
		}

		private void processNonEmptyBlock(final MutableCfa cfa, final CfaLoc source, final CfaLoc target,
				final Stmt head, final List<? extends Stmt> tail) {

			if (head instanceof ReturnStmt<?> || tail.isEmpty()) {
				head.accept(this, Tuple.of(source, target));
			} else {
				final CfaLoc middle = cfa.createLoc();
				head.accept(this, Tuple.of(source, middle));

				final Stmt newHead = tail.get(0);
				final List<? extends Stmt> newTail = tail.subList(1, tail.size());

				processNonEmptyBlock(cfa, middle, target, newHead, newTail);
			}
		}

		@Override
		public <ReturnType extends Type> Void visit(final ReturnStmt<ReturnType> stmt,
				final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();

			final CfaEdge edge = cfa.createEdge(source, cfa.getFinalLoc());
			edge.getStmts().add(stmt);

			return null;
		}

		@Override
		public Void visit(final IfStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaLoc thenLoc = cfa.createLoc();
			final CfaEdge thenEdge = cfa.createEdge(source, thenLoc);
			thenEdge.getStmts().add(Assume(stmt.getCond()));
			stmt.getThen().accept(this, Tuple.of(thenLoc, target));

			final CfaEdge elseEdge = cfa.createEdge(source, target);
			elseEdge.getStmts().add(Assume(Not(stmt.getCond())));

			return null;
		}

		@Override
		public Void visit(final IfElseStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaLoc thenLoc = cfa.createLoc();
			final CfaEdge thenEdge = cfa.createEdge(source, thenLoc);
			thenEdge.getStmts().add(Assume(stmt.getCond()));
			stmt.getThen().accept(this, Tuple.of(thenLoc, target));

			final CfaLoc elseLoc = cfa.createLoc();
			final CfaEdge elseEdge = cfa.createEdge(source, elseLoc);
			elseEdge.getStmts().add(Assume(Not(stmt.getCond())));
			stmt.getElse().accept(this, Tuple.of(elseLoc, target));

			return null;
		}

		@Override
		public Void visit(final WhileStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaLoc doLoc = cfa.createLoc();
			final CfaEdge enterEdge = cfa.createEdge(source, doLoc);
			enterEdge.getStmts().add(Assume(stmt.getCond()));

			stmt.getDo().accept(this, Tuple.of(doLoc, source));

			final CfaEdge exitEdge = cfa.createEdge(source, target);
			exitEdge.getStmts().add(Assume(Not(stmt.getCond())));

			return null;
		}

		@Override
		public Void visit(final DoStmt stmt, final Product2<CfaLoc, CfaLoc> param) {
			final CfaLoc source = param._1();
			final CfaLoc target = param._2();

			final CfaLoc doLoc = cfa.createLoc();
			stmt.getDo().accept(this, Tuple.of(source, doLoc));

			final CfaEdge entryEdge = cfa.createEdge(doLoc, source);
			entryEdge.getStmts().add(Assume(stmt.getCond()));

			final CfaEdge exitEdge = cfa.createEdge(doLoc, target);
			exitEdge.getStmts().add(Assume(Not(stmt.getCond())));

			return null;
		}

	}

}
