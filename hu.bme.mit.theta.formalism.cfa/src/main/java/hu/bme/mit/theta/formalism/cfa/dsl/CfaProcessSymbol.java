package hu.bme.mit.theta.formalism.cfa.dsl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hu.bme.mit.theta.common.dsl.Environment;
import hu.bme.mit.theta.common.dsl.Scope;
import hu.bme.mit.theta.common.dsl.Symbol;
import hu.bme.mit.theta.common.dsl.SymbolTable;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.formalism.cfa.CFA;
import hu.bme.mit.theta.formalism.cfa.CFA.CfaLoc;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.EdgeContext;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.LocContext;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.ProcDeclContext;
import hu.bme.mit.theta.formalism.cfa.dsl.gen.CfaDslParser.VarDeclContext;

final class CfaProcessSymbol implements Symbol, Scope {

	private final CfaSpecification scope;
	private final SymbolTable symbolTable;

	private final String name;
	private final boolean main;
	private final List<CfaVariableSymbol> variables;
	private final List<CfaLocationSymbol> locations;
	private final List<CfaEdgeDefinition> edges;

	public CfaProcessSymbol(final CfaSpecification scope, final ProcDeclContext context) {
		checkNotNull(context);
		this.scope = checkNotNull(scope);
		symbolTable = new SymbolTable();

		name = context.id.getText();
		main = (context.main != null);
		variables = createVariables(context.varDecls);
		locations = createLocations(context.locs);
		edges = createEdges(context.edges);

		declareAll(variables);
		declareAll(locations);
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean isMain() {
		return main;
	}

	////

	public CFA instantiate(final Environment env) {
		final CFA cfa = new CFA();
		env.push();

		for (final CfaVariableSymbol variable : variables) {
			final VarDecl<?> var = variable.instantiate();
			env.define(variable, var);
		}

		for (final CfaLocationSymbol location : locations) {
			final CfaLoc loc = location.intantiate(cfa);
			env.define(location, loc);
		}

		for (final CfaEdgeDefinition edge : edges) {
			edge.instantiate(cfa, env);
		}

		env.pop();
		return cfa;
	}

	////

	@Override
	public Optional<CfaSpecification> enclosingScope() {
		return Optional.of(scope);
	}

	@Override
	public Optional<Symbol> resolve(final String name) {
		final Optional<Symbol> symbol = symbolTable.get(name);
		if (symbol.isPresent()) {
			return symbol;
		} else {
			return scope.resolve(name);
		}
	}

	////

	private void declareAll(final Iterable<? extends Symbol> symbols) {
		symbolTable.addAll(symbols);
	}

	private List<CfaVariableSymbol> createVariables(final List<VarDeclContext> varDeclContexts) {
		final List<CfaVariableSymbol> result = new ArrayList<>();
		for (final VarDeclContext varDeclContext : varDeclContexts) {
			final CfaVariableSymbol symbol = new CfaVariableSymbol(varDeclContext);
			result.add(symbol);
		}
		return result;
	}

	private List<CfaLocationSymbol> createLocations(final List<LocContext> locContexts) {
		final List<CfaLocationSymbol> result = new ArrayList<>();

		int nInitLocs = 0;
		int nFinalLocs = 0;
		int nErrorLocs = 0;

		for (final LocContext locContext : locContexts) {
			final CfaLocationSymbol symbol = new CfaLocationSymbol(locContext);

			if (symbol.isInit()) {
				nInitLocs++;
			} else if (symbol.isFinal()) {
				nFinalLocs++;
			} else if (symbol.isError()) {
				nErrorLocs++;
			}

			result.add(symbol);
		}

		checkArgument(nInitLocs == 1, "Exactly one initial location must be specififed");
		checkArgument(nFinalLocs == 1, "Exactly one final location must be specififed");
		checkArgument(nErrorLocs == 1, "Exactly one error location must be specififed");

		return result;
	}

	private List<CfaEdgeDefinition> createEdges(final List<EdgeContext> edgeContexts) {
		final List<CfaEdgeDefinition> result = new ArrayList<>();
		for (final EdgeContext edgeContext : edgeContexts) {
			final CfaEdgeDefinition edgeDefinition = new CfaEdgeDefinition(this, edgeContext);
			result.add(edgeDefinition);
		}
		return result;
	}

}