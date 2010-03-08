/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.example.client.controllers;

import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.IInfoFlyoutRidget;
import org.eclipse.riena.ui.ridgets.IInfoFlyoutRidget.Info;
import org.eclipse.riena.ui.swt.InfoFlyout;

/**
 * Controller for the {@link InfoFlyout} example.
 */
public class InfoFlyoutSubModuleController extends SubModuleController {

	@Override
	public void configureRidgets() {
		IActionRidget button = getRidget(IActionRidget.class, "flyoutButton"); //$NON-NLS-1$
		button.addListener(new IActionListener() {
			public void callback() {
				IInfoFlyoutRidget flyout = getInfoFlyout();
				Info info = new Info("arrowRight", //$NON-NLS-1$
						"This is an IInfoFlyout. It can show and image and two lines of text."); //$NON-NLS-1$
				flyout.addInfo(info);
			}
		});
	}
}