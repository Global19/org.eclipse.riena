/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.ridgets;

/**
 * A decoration on a control.
 * 
 * @since 2.1
 */
public interface IControlDecoration {

	/**
	 * Return true if the decoration is visible, false otherwise.
	 */
	boolean isVisible();

	/**
	 * Hide the decoration.
	 */
	void hide();

	/**
	 * Show the decoration.
	 */
	void show();

}