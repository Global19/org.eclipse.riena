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
package org.eclipse.riena.ui.ridgets.swt;

import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;

import org.eclipse.riena.internal.ui.ridgets.swt.ActionObserver;
import org.eclipse.riena.ui.core.resource.IIconManager;
import org.eclipse.riena.ui.core.resource.IconManagerProvider;
import org.eclipse.riena.ui.core.resource.IconSize;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.IToggleButtonRidget;

/**
 *
 */
public abstract class AbstractToggleButtonRidget extends AbstractValueRidget implements IToggleButtonRidget {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final ActionObserver actionObserver;

	private Binding controlBinding;
	private String text;
	private String iconID;
	private boolean selected;
	private boolean textAlreadyInitialized;
	private boolean useRidgetIcon;

	public AbstractToggleButtonRidget() {
		super();
		actionObserver = new ActionObserver(this);
		textAlreadyInitialized = false;
		useRidgetIcon = false;
		addPropertyChangeListener(IRidget.PROPERTY_ENABLED, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				final boolean isEnabled = ((Boolean) evt.getNewValue()).booleanValue();
				updateSelection(isEnabled);
			}
		});
		addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				updateEnabled();
			}
		});
		addPropertyChangeListener(IToggleButtonRidget.PROPERTY_SELECTED, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				updateEnabled();
			}
		});
	}

	public void addListener(final IActionListener listener) {
		actionObserver.addListener(listener);
	}

	/**
	 * @deprecated use {@link #addListener(IActionListener)}
	 */
	@Deprecated
	public void addListener(final Object target, final String action) {
		final IActionListener listener = EventHandler.create(IActionListener.class, target, action);
		actionObserver.addListener(listener);
	}

	/**
	 * @since 2.0
	 */
	public void fireAction() {
		if (isVisible() && isEnabled()) {
			setSelected(!isSelected());
			actionObserver.widgetSelected(null);
		}
	}

	public String getIcon() {
		final IIconManager manager = IconManagerProvider.getInstance().getIconManager();
		final String icon = manager.getName(this.iconID);
		return icon;
	}

	public final String getText() {
		return text;
	}

	public boolean isSelected() {
		return selected;
	}

	public void removeListener(final IActionListener listener) {
		actionObserver.removeListener(listener);
	}

	public void setIcon(final String icon) {
		setIcon(icon, IconSize.NONE);
	}

	/**
	 * @since 2.0
	 */
	public void setIcon(final String icon, final IconSize size) {
		final boolean oldUseRidgetIcon = useRidgetIcon;
		useRidgetIcon = true;
		final String oldIconID = this.iconID;
		final IIconManager manager = IconManagerProvider.getInstance().getIconManager();
		this.iconID = manager.getIconID(icon, size);
		if (hasChanged(oldIconID, iconID) || !oldUseRidgetIcon) {
			updateUIIcon();
		}
	}

	public void setSelected(final boolean selected) {
		if (this.selected != selected) {
			final boolean oldValue = this.selected;
			this.selected = selected;
			actionObserver.widgetSelected(null);
			firePropertyChange(IToggleButtonRidget.PROPERTY_SELECTED, Boolean.valueOf(oldValue),
					Boolean.valueOf(selected));
			updateMandatoryMarkers();
		}
	}

	public final void setText(final String newText) {
		final String oldText = this.text;
		this.text = newText;
		updateUIText();
		firePropertyChange(IActionRidget.PROPERTY_TEXT, oldText, this.text);
	}

	// protected methods
	////////////////////

	@Override
	protected void bindUIControl() {
		final DataBindingContext context = getValueBindingSupport().getContext();
		if (getUIControl() != null) {
			controlBinding = context.bindValue(getUIControlSelectionObservable(), getRidgetObservable(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE), new UpdateValueStrategy(
							UpdateValueStrategy.POLICY_UPDATE)
							.setBeforeSetValidator(new CancelControlUpdateWhenDisabled()));
			initText();
			updateUIText();
			updateSelection(isEnabled());
			updateUIIcon();
		}
	}

	@Override
	protected IObservableValue getRidgetObservable() {
		return BeansObservables.observeValue(this, IToggleButtonRidget.PROPERTY_SELECTED);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Will return false if any of the following conditions are true:
	 * <ul>
	 * <li>the ridget is not enabled &ndash; via
	 * {@code ridget.setEnabled(false)}</li>
	 * <li>the ridget is output only and not selected</li>
	 * </ul>
	 */
	@Override
	public boolean isEnabled() {
		boolean isEnabled = super.isEnabled();
		if (isEnabled && isOutputOnly() && !isSelected()) {
			isEnabled = false;
		}
		return isEnabled;
	}

	@Override
	protected void unbindUIControl() {
		super.unbindUIControl();
		if (controlBinding != null) {
			controlBinding.dispose();
			controlBinding = null;
		}
	}

	// protected abstract methods
	/////////////////////////////

	protected abstract IObservableValue getUIControlSelectionObservable();

	protected abstract String getUIControlText();

	protected abstract void setUIControlImage(Image image);

	protected abstract void setUIControlSelection(boolean selected);

	protected abstract void setUIControlText(String text);

	/**
	 * Updates the mandatory marker state in this ridget and it's siblings
	 * (i.e. other ToggleButtonRidgets for Buttons in the same composite).
	 * 
	 * @since 3.0
	 */
	protected abstract void updateMandatoryMarkers();

	// helping methods
	//////////////////

	/**
	 * If the text of the ridget has no value, initialize it with the text of
	 * the UI control.
	 */
	private void initText() {
		if ((text == null) && (!textAlreadyInitialized)) {
			if ((getUIControl()) != null && !(getUIControl().isDisposed())) {
				text = getUIControlText();
				if (text == null) {
					text = EMPTY_STRING;
				}
				textAlreadyInitialized = true;
			}
		}
	}

	/**
	 * Update the selection state of this ridget's control (button)
	 * 
	 * @param isRidgetEnabled
	 *            true if this ridget is enabled, false otherwise
	 */
	private void updateSelection(final boolean isRidgetEnabled) {
		if (getUIControl() != null && MarkerSupport.isHideDisabledRidgetContent()) {
			if (!isRidgetEnabled) {
				setUIControlSelection(false);
			} else {
				setUIControlSelection(isSelected());
			}
		}
	}

	/**
	 * Updates the images of the control.
	 */
	private void updateUIIcon() {
		if (getUIControl() != null) {
			Image image = null;
			if (iconID != null) {
				image = getManagedImage(iconID);
			}
			if ((image != null) || useRidgetIcon) {
				setUIControlImage(image);
			}
		}
	}

	private void updateUIText() {
		if (getUIControl() != null) {
			setUIControlText(text);
		}
	}

	// helping classes
	//////////////////

	/**
	 * When the ridget is disabled, this validator will prevent the selected
	 * attribute of a control (Button) from changing -- unless
	 * HIDE_DISABLED_RIDGET_CONTENT is {@code false}.
	 */
	private final class CancelControlUpdateWhenDisabled implements IValidator {
		public IStatus validate(final Object value) {
			final boolean cancel = MarkerSupport.isHideDisabledRidgetContent() && !isEnabled();
			return cancel ? Status.CANCEL_STATUS : Status.OK_STATUS;
		}
	}
}
