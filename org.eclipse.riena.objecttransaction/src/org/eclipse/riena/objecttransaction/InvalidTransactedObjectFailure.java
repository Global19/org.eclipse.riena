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
package org.eclipse.riena.objecttransaction;

/**
 * This failure is thrown when the transacted object is not valid in this
 * context
 * 
 */
public class InvalidTransactedObjectFailure extends ObjectTransactionFailure {

	/**
	 * @param message
	 */
	public InvalidTransactedObjectFailure(String message) {
		super(message);
	}

}