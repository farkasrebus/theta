package hu.bme.mit.inf.ttmc.program.ui;

import java.util.Collection;

import hu.bme.mit.inf.ttmc.constraint.type.Type;
import hu.bme.mit.inf.ttmc.constraint.ui.ConstraintModel;
import hu.bme.mit.inf.ttmc.formalism.decl.ProcDecl;

public interface ProgramModel extends ConstraintModel {

	public Collection<ProcDecl<? extends Type>> getProcDecls();
	public ProcedureModel getProcedureModel(ProcDecl<? extends Type> procDecl);
	
}