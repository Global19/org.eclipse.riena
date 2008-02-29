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
package org.eclipse.riena.objecttransaction.context;

import org.eclipse.riena.objecttransaction.ObjectTransactionFailure;

/**
 * Failure indicating, that somethin goes wrong while managing the transaction
 * in the current context
 * 
 */

public class ObjectTransactionContextFailure extends ObjectTransactionFailure {

	/**
	 * Creates a new instance of this failure
	 * 
	 * @param message
	 *            the message to show.
	 */
	public ObjectTransactionContextFailure(String message) {
		super(message);
	}

}
