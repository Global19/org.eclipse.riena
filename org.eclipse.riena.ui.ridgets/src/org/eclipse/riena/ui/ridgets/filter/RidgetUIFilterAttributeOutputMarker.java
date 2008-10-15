/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.ridgets.filter;

import org.eclipse.riena.ui.core.marker.OutputMarker;

/**
 * Filter attribute to provide a output marker for a ridget.
 */
public class RidgetUIFilterAttributeOutputMarker extends AbstractRidgetUIFilterMarkerAttribute {

	/**
	 * Creates a new instance of {@code
	 * RidgetUIFilterAttributeHiddenMandatoryMarker}.
	 * 
	 * @param id
	 *            - ID
	 */
	public RidgetUIFilterAttributeOutputMarker() {
		super(null, new OutputMarker(false));
	}

	/**
	 * Creates a new instance of {@code RidgetUIFilterAttributeOutputMarker}.
	 * 
	 * @param id
	 *            - ID
	 */
	public RidgetUIFilterAttributeOutputMarker(String id) {
		super(id, new OutputMarker(false));
	}

}
