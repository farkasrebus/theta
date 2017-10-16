/*
 *  Copyright 2017 Budapest University of Technology and Economics
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package hu.bme.mit.theta.formalism.sts.aiger;

import static com.google.common.base.Preconditions.checkNotNull;
import static hu.bme.mit.theta.core.type.anytype.Exprs.Prime;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.And;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Bool;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Iff;
import static hu.bme.mit.theta.core.type.booltype.BoolExprs.Not;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.theta.core.decl.Decls;
import hu.bme.mit.theta.core.decl.VarDecl;
import hu.bme.mit.theta.core.type.Expr;
import hu.bme.mit.theta.core.type.booltype.BoolType;
import hu.bme.mit.theta.formalism.sts.STS;

/**
 * A basic AIGER parser that encodes each variable of the circuit with a STS
 * variable. It supports AIGER format 1.7.
 */
public class BasicAigerParser implements AigerParser {

	@Override
	public STS parse(final String fileName) throws IOException {

		final STS.Builder builder = STS.builder();
		final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

		try {
			int maxVars;
			int inputs;
			int latches;
			int outputs;
			int andGates;
			// Parse header
			final String[] header = checkNotNull(br.readLine(), "Header expected").split(" ");
			maxVars = Integer.parseInt(header[1]);
			inputs = Integer.parseInt(header[2]);
			latches = Integer.parseInt(header[3]);
			outputs = Integer.parseInt(header[4]);
			andGates = Integer.parseInt(header[5]);
			// Create variables
			final List<VarDecl<BoolType>> vars = new ArrayList<>(maxVars + 1);
			final List<Expr<BoolType>> outVars = new ArrayList<>(1);
			for (int i = 0; i <= maxVars; ++i) {
				vars.add(Decls.Var("v" + i, Bool()));
			}
			// v0 is the constant 'false'
			builder.addInvar(Not(vars.get(0).getRef()));

			// Inputs
			for (int i = 0; i < inputs; ++i) {
				br.readLine();
			}

			// Latches
			for (int i = 0; i < latches; ++i) {
				final String v[] = checkNotNull(br.readLine(), "Latch expected").split(" ");
				final int v1 = Integer.parseInt(v[0]);
				final int v2 = Integer.parseInt(v[1]);
				builder.addInit(Not(vars.get(v1 / 2).getRef()));
				builder.addTrans(Iff(Prime(vars.get(v1 / 2).getRef()),
						v2 % 2 == 0 ? vars.get(v2 / 2).getRef() : Not(vars.get(v2 / 2).getRef())));
			}

			// Outputs
			for (int i = 0; i < outputs; ++i) {
				final int v = Integer.parseInt(br.readLine());
				outVars.add(v % 2 == 0 ? vars.get(v / 2).getRef() : Not(vars.get(v / 2).getRef()));
			}

			// And gates
			for (int i = 0; i < andGates; ++i) {
				final String[] v = checkNotNull(br.readLine(), "And gate expected").split(" ");
				final int vo = Integer.parseInt(v[0]);
				final int vi1 = Integer.parseInt(v[1]);
				final int vi2 = Integer.parseInt(v[2]);
				builder.addInvar(Iff(vars.get(vo / 2).getRef(),
						And(vi1 % 2 == 0 ? vars.get(vi1 / 2).getRef() : Not(vars.get(vi1 / 2).getRef()),
								vi2 % 2 == 0 ? vars.get(vi2 / 2).getRef() : Not(vars.get(vi2 / 2).getRef()))));
			}

			br.close();

			// Requirement
			if (outVars.size() == 1) {
				builder.setProp(Not(outVars.get(0)));
			} else {
				throw new UnsupportedOperationException(
						"Currently only models with a single output variable are supported (this model has "
								+ outVars.size() + ").");
			}

			return builder.build();
		} finally {
			br.close();
		}
	}
}
