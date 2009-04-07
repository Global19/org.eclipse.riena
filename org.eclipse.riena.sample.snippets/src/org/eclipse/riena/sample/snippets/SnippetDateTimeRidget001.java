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
package org.eclipse.riena.sample.snippets;

import java.util.Date;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.beans.common.TypedBean;
import org.eclipse.riena.internal.ui.ridgets.swt.DateTimeRidget;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.swt.DefaultRealm;
import org.eclipse.riena.ui.ridgets.swt.SwtRidgetFactory;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 * Bind a java.util.Date to IDateTimeRidgets.
 */
public class SnippetDateTimeRidget001 {

	public static void main(String[] args) {
		Display display = new Display();
		new DefaultRealm();
		Shell shell = UIControlsFactory.createShell(display);
		shell.setLayout(new GridLayout(2, false));

		final TypedBean<Date> value = new TypedBean<Date>(new Date());

		GridDataFactory fill = GridDataFactory.fillDefaults();

		UIControlsFactory.createLabel(shell, "Value:"); //$NON-NLS-1$
		Text txtValue = UIControlsFactory.createTextOutput(shell);
		fill.applyTo(txtValue);

		UIControlsFactory.createLabel(shell, "Date:"); //$NON-NLS-1$
		DateTime dtDate = new DateTime(shell, SWT.DATE | SWT.MEDIUM | SWT.DROP_DOWN);
		fill.applyTo(dtDate);
		final DateTimeRidget dtDateRidget = (DateTimeRidget) SwtRidgetFactory.createRidget(dtDate);
		dtDateRidget.bindToModel(value, TypedBean.PROP_VALUE);
		dtDateRidget.updateFromModel();

		UIControlsFactory.createLabel(shell, "Time:"); //$NON-NLS-1$
		DateTime dtTime = new DateTime(shell, SWT.TIME | SWT.MEDIUM);
		fill.applyTo(dtTime);
		final DateTimeRidget dtTimeRidget = (DateTimeRidget) SwtRidgetFactory.createRidget(dtTime);
		dtTimeRidget.bindToModel(value, TypedBean.PROP_VALUE);
		dtTimeRidget.updateFromModel();

		Button btnSync = UIControlsFactory.createButton(shell);
		btnSync.setText("Sync"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().span(2, 1).applyTo(btnSync);
		IActionRidget btnSyncRidget = (IActionRidget) SwtRidgetFactory.createRidget(btnSync);
		btnSyncRidget.addListener(new IActionListener() {
			private long days = 0;

			public void callback() {
				days++;
				long millis = System.currentTimeMillis() + (days * 24 * 3600 * 1000);
				value.setValue(new Date(millis));
				dtDateRidget.updateFromModel();
				dtTimeRidget.updateFromModel();
			}
		});

		DataBindingContext dbc = new DataBindingContext();
		dbc.bindValue(WidgetProperties.text().observe(txtValue), BeansObservables.observeValue(value,
				TypedBean.PROP_VALUE), null, new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
