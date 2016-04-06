package hu.bme.mit.inf.ttmc.formalism.utils.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import hu.bme.mit.inf.ttmc.formalism.common.stmt.BlockStmt;
import hu.bme.mit.inf.ttmc.formalism.common.stmt.Stmt;

public class StmtUtils {

	public static List<? extends Stmt> getSubStmts(final Stmt stmt) {
		if (stmt instanceof BlockStmt) {
			final BlockStmt blockStmt = (BlockStmt) stmt;
			return blockStmt.getStmts().stream().map(s -> getSubStmts(s)).flatMap(c -> c.stream())
					.collect(Collectors.toList());
		} else {
			return Collections.singletonList(stmt);
		}
	}

}
