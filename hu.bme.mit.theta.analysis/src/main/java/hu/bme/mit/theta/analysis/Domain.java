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
package hu.bme.mit.theta.analysis;

/**
 * Common interface for abstract domain with top and bottom elements and a
 * partial order.
 */
public interface Domain<S extends State> {

	/**
	 * Checks if the given state is a top element in the domain.
	 *
	 * @param state
	 * @return
	 */
	boolean isTop(S state);

	/**
	 * Checks if the given state is a bottom element in the domain.
	 *
	 * @param state
	 * @return
	 */
	boolean isBottom(S state);

	/**
	 * Checks if state1 is less or equal to state2.
	 *
	 * @param state1
	 * @param state2
	 * @return
	 */
	boolean isLeq(S state1, S state2);

}
