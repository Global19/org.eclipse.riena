/*******************************************************************************
 * Copyright (c) 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.objecttransaction.state;

import org.eclipse.core.runtime.Assert;

/**
 * StateMachine which takes a State, applies an Action on it and the result is a
 * new State. If an Action is invalid for a certain State, the
 * InvalidActionFailure is thrown.
 * 
 */
public final class StateMachine {

	private StateMachine() {
		// utility
	}

	/**
	 * Creates an initialState for a given action.
	 * 
	 * @param action
	 *            for which to create a state
	 * @return initialState
	 * @throws InvalidActionFailure
	 */
	public static State initAction(Action action) throws InvalidActionFailure {
		if (action.equals(Action.NEW) || action.equals(Action.ADD) || action.equals(Action.SET)) {
			return State.CREATED;
		} else if (action.equals(Action.DELETE) || action.equals(Action.REMOVE)) {
			return State.DELETED;
		}

		throw new InvalidActionFailure("unknown action " + Action.toString(action));
	}

	/**
	 * Applies an action on a currentState and returns the resulting State. If
	 * the Action is invalid for this state an InvalidActionFailure is thrown
	 * 
	 * @param currentState
	 * @param action
	 * @return State this is the new State after applying the action
	 * @throws InvalidActionFailure
	 * @pre currentState!=null
	 */
	public static State processAction(State currentState, Action action) throws InvalidActionFailure {
		Assert.isNotNull(currentState, "currentState cannot be null");
		// CREATED
		if (currentState.equals(State.CREATED)) {
			if (action.equals(Action.SET) || action.equals(Action.ADD) || action.equals(Action.REMOVE)) {
				return currentState;
			} else if (action.equals(Action.NEW)) {
				return currentState;
			} else if (action.equals(Action.DELETE)) {
				return State.VANISHED;
			}
			// MODIFIED
		} else if (currentState.equals(State.MODIFIED)) {
			if (action.equals(Action.NEW)) {
				throw new InvalidActionFailure("state is MODIFIED, can't set to NEW");
			} else if (action.equals(Action.SET) || action.equals(Action.ADD) || action.equals(Action.REMOVE)) {
				return currentState;
			} else if (action.equals(Action.DELETE)) {
				return State.DELETED;
			}
			// DELETED
		} else if (currentState.equals(State.DELETED)) {
			throw new InvalidActionFailure("currentState is DELETED, no action possible");
			// VANISHED
		} else if (currentState.equals(State.VANISHED)) {
			throw new InvalidActionFailure("currentState is VANISHED, no action possible");
			// CLEAN
		} else if (currentState.equals(State.CLEAN)) {
			if (action.equals(Action.NEW)) {
				throw new InvalidActionFailure("state is MODIFIED, can't set to NEW");
			} else if (action.equals(Action.SET) || action.equals(Action.ADD) || action.equals(Action.REMOVE)) {
				return State.MODIFIED;
			} else if (action.equals(Action.DELETE)) {
				return State.DELETED;
			}
		}
		throw new InvalidActionFailure("unknown action for currentState state is " + State.toString(currentState) + " action is " + Action.toString(action));
	}

	/**
	 * Merges two States and create a resulting State. The newState (second
	 * parameter) is the more current State that is updating the existing
	 * current State (first Parameter).
	 * 
	 * @param currentState
	 * @param newState
	 * @return State the new state after the merge
	 * @throws InvalidActionFailure
	 */
	public static State mergeStates(State currentState, State newState) throws InvalidActionFailure {
		if (currentState.equals(newState)) {
			return currentState;
		}
		if (currentState.equals(State.CREATED)) {
			if (newState.equals(State.DELETED) || newState.equals(State.VANISHED)) {
				return State.VANISHED;
			} else {
				return State.CREATED;
			}
		} else if (currentState.equals(State.MODIFIED) && (newState.equals(State.DELETED) || newState.equals(State.VANISHED))) {
			return State.DELETED;
		} else if (currentState.equals(State.MODIFIED) && newState.equals(State.CREATED)) {
			return State.MODIFIED;
		} else if (currentState.equals(State.DELETED) || currentState.equals(State.VANISHED)) {
			throw new InvalidActionFailure("current State is " + State.toString(currentState) + " new State is " + State.toString(newState) + " invalid change");
		} else if (currentState.equals(State.CLEAN)) {
			if (newState.equals(State.MODIFIED) || newState.equals(State.DELETED)) {
				return newState;
			}
			if (newState.equals(State.VANISHED)) {
				throw new InvalidActionFailure("current State is " + State.toString(currentState) + " new State is " + State.toString(newState)
						+ " invalid change");
			}
		}
		throw new InvalidActionFailure("unknown current State " + State.toString(currentState) + " and new State " + State.toString(newState));
	}
}