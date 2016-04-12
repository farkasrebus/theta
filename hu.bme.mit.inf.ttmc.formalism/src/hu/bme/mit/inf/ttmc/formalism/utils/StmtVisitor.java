package hu.bme.mit.inf.ttmc.formalism.utils;

import hu.bme.mit.inf.ttmc.core.type.Type;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.AssertStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.AssignStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.AssumeStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.BlockStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.DeclStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.DoStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.HavocStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.IfElseStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.IfStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.ReturnStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.SkipStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.WhileStmt;

public interface StmtVisitor<P, R> {

	public R visit(SkipStmt stmt, P param);

	public <DeclType extends Type, ExprType extends DeclType> R visit(DeclStmt<DeclType, ExprType> stmt, P param);

	public R visit(AssumeStmt stmt, P param);

	public R visit(AssertStmt stmt, P param);

	public <DeclType extends Type, ExprType extends DeclType> R visit(AssignStmt<DeclType, ExprType> stmt, P param);

	public <DeclType extends Type> R visit(HavocStmt<DeclType> stmt, P param);

	public R visit(BlockStmt stmt, P param);

	public <ReturnType extends Type> R visit(ReturnStmt<ReturnType> stmt, P param);

	public R visit(IfStmt stmt, P param);

	public R visit(IfElseStmt stmt, P param);

	public R visit(WhileStmt stmt, P param);

	public R visit(DoStmt stmt, P param);

}
