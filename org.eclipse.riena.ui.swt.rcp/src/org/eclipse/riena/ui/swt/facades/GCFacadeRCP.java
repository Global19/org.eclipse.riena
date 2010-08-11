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

package org.eclipse.riena.ui.swt.facades;

import org.eclipse.swt.graphics.GC;

/**
 * Implements {@link GCFacade} for RCP.
 */
public class GCFacadeRCP extends GCFacade {

	@Override
	public void drawRoundRectangle(final GC gc, final int x, final int y, final int width, final int height,
			final int arcWidth, final int arcHeight) {
		gc.drawRoundRectangle(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawLine(final GC gc, final int x1, final int y1, final int x2, final int y2) {
		gc.drawLine(x1, y1, x2, y2);
	}

	@Override
	public int getAdvanceWidth(final GC gc, final char ch) {
		return gc.getAdvanceWidth(ch);
	}

	@Override
	public void setAdvanced(final GC gc, final boolean isEnabled) {
		gc.setAdvanced(isEnabled);
	}

	@Override
	public void setAntialias(final GC gc, final int option) {
		gc.setAntialias(option);
	}

	@Override
	public void setLineDash(final GC gc, final int[] dashes) {
		gc.setLineDash(dashes);
	}

}
