package org.eclipse.riena.ui.ridgets.swt;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.swt.ChoiceComposite;
import org.eclipse.riena.ui.swt.CompletionCombo;
import org.eclipse.riena.ui.swt.utils.SWTBindingPropertyLocator;
import org.eclipse.riena.ui.swt.utils.SwtUtilities;

/**
 * Focus listener that also prevents the widget corresponding to this ridget
 * from getting the UI focus when the ridget is not focusable or output only.
 * <p>
 * The algorithm is as follows:
 * <ul>
 * <li>if the widget is non-focusable, select the next focusable widget</li>
 * <li>if the widget is output only, select the next focusable widget</li>
 * <li>if the widget is output only and was clicked, accept focus</li>
 * <li>in any other case, accept focus</li>
 * </ul>
 * Implementation note: SWT will invoke the focusGained, focusLost methods
 * before the mouseDown method.
 * 
 * @see AbstractSWTRidget#setFocusable(boolean).
 * 
 * @since 3.0
 */
public final class FocusManager extends MouseAdapter implements FocusListener {

	private final AbstractSWTRidget ridget;

	private boolean clickToFocus;

	/**
	 * Create a new instance.
	 * 
	 * @param ridget
	 *            a ridget instance; never null.
	 */
	FocusManager(final AbstractSWTRidget ridget) {
		Assert.isNotNull(ridget);
		this.ridget = ridget;
	}

	/**
	 * Add the required listeners for operation of the focus manager to the
	 * given control.
	 * 
	 * @param control
	 *            a control instance; never null
	 */
	public void addListeners(final Control control) {
		control.addFocusListener(this);
		control.addMouseListener(this);
	}

	public void focusGained(final FocusEvent e) {
		if (isFocusable()) {
			// trace("## focus gained: %s %d", e.widget, e.widget.hashCode());
			ridget.fireFocusGained();
		} else {
			final Control target = findFocusTarget((Control) e.widget);
			if (target != null) {
				// trace("## %s %d -> %s %d", e.widget, e.widget.hashCode(), target, target.hashCode());
				target.setFocus();
			} else { // no suitable control found
				// trace("!! %s %d -> NO TARGET", e.widget, e.widget.hashCode());
			}
		}
	}

	public void focusLost(final FocusEvent e) {
		if (isFocusable()) {
			clickToFocus = false;
			ridget.fireFocusLost();
		}
	}

	@Override
	public void mouseDown(final MouseEvent e) {
		if (ridget.isFocusable() && ridget.isOutputOnly()) {
			// trace("## mouse DOWN: %s %d", e.widget, e.widget.hashCode());
			clickToFocus = true;
			((Control) e.widget).setFocus();
		}
	}

	/**
	 * Remove the required listeners for operation of the focus manager from the
	 * given control.
	 * 
	 * @param control
	 *            a control instance; never null
	 */
	public void removeListeners(final Control control) {
		control.removeFocusListener(this);
		control.removeMouseListener(this);
	}

	// helping methods
	//////////////////

	/**
	 * Tests whether the given control can get the focus or cannot.
	 * 
	 * @param control
	 *            UI control
	 * @return {@code true} if control can get the focus; otherwise
	 *         {@code false}.
	 */
	private boolean canGetFocus(final Control control) {
		// skip disabled or hidden
		if (!control.isEnabled() || !control.isVisible()) {
			return false;
		}
		// skip read-only
		if (SwtUtilities.hasStyle(control, SWT.READ_ONLY)) {
			return false;
		}
		if (control instanceof Text && !((Text) control).getEditable()) {
			return false;
		}
		if (control instanceof ChoiceComposite && !((ChoiceComposite) control).getEditable()) {
			return false;
		}
		if (control instanceof CompletionCombo && !((CompletionCombo) control).getEditable()) {
			return false;
		}
		// skip IMarkableRidgets that are not focusable  or output only
		final String bindingId = SWTBindingPropertyLocator.getInstance().locateBindingProperty(control);
		if (bindingId != null) {
			final Object controlsRidget = ridget.getController().getRidget(bindingId);
			if (controlsRidget instanceof IMarkableRidget) {
				final IMarkableRidget markableRidget = (IMarkableRidget) controlsRidget;
				return markableRidget.isFocusable() && !markableRidget.isOutputOnly();
			}
			// skip IRidgets that are not focusable
			if (controlsRidget instanceof IRidget) {
				return ((IRidget) controlsRidget).isFocusable();
			}
		}
		// skip Composites that have no children that can get focus
		if (control instanceof Composite) {
			return findFocusTarget(null, (Composite) control) != null;
		}
		return true;
	}

	private Control findFocusTarget(final Control control) {
		Control result = null;
		Control start = control;
		while (start.getParent() != null && result == null) {
			final Composite parent = start.getParent();
			result = findFocusTarget(start, parent);
			start = parent;
		}
		return result;
	}

	private Control findFocusTarget(final Control start, final Composite parent) {
		Control result = null;
		final Control[] siblings = parent.getTabList();
		int myIndex = -1;
		// find index for control
		for (int i = 0; myIndex == -1 && i < siblings.length; i++) {
			if (siblings[i] == start) {
				myIndex = i;
			}
		}
		// find next possible control
		for (int i = myIndex + 1; result == null && i < siblings.length; i++) {
			final Control candidate = siblings[i];
			if (canGetFocus(candidate)) {
				result = candidate;
			}
		}
		// find previous possible control
		for (int i = 0; result == null && i < myIndex; i++) {
			final Control candidate = siblings[i];
			if (canGetFocus(candidate)) {
				result = candidate;
			}
		}
		return result;
	}

	private boolean isFocusable() {
		return (ridget.isFocusable() && !ridget.isOutputOnly()) || clickToFocus;
	}

	@SuppressWarnings("unused")
	private void trace(final String format, final Object... args) {
		System.out.println(String.format(format, args));
	}
}