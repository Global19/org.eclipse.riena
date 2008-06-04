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
package org.eclipse.riena.navigation.ui.swt.lnf.rienadefault;

import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.navigation.ui.swt.lnf.AbstractLnfRenderer;
import org.eclipse.riena.navigation.ui.swt.lnf.LnfManager;
import org.eclipse.riena.navigation.ui.swt.utils.ImageUtil;
import org.eclipse.riena.navigation.ui.swt.utils.SwtUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Renderer of the title bar of an embedded view.
 */
public class EmbeddedTitlebarRenderer extends AbstractLnfRenderer {

	private final static int TITLEBAR_LABEL_PADDING_LEFT = 5;
	private final static int TITLEBAR_LABEL_PADDING = 4;
	private final static int TITLEBAR_ICON_TEXT_GAP = 4;

	private HoverBorderRenderer hoverBorderRenderer;
	private Image image;
	private String icon;
	private boolean active;
	private boolean pressed;
	private boolean hover;

	public EmbeddedTitlebarRenderer() {
		super();
		setHoverBorderRenderer(getLnfBorderRenderer());
		image = null;
		icon = ""; //$NON-NLS-1$
		active = false;
		pressed = false;
		hover = false;
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfRenderer#dispose()
	 */
	public void dispose() {
		if (getImage() != null) {
			getImage().dispose();
			setImage(null);
		}
	}

	/**
	 * Computes the size (height) of the title bar.
	 * 
	 * @param gc -
	 *            <code>GC</code> of the component <code>Control</code>
	 * @param wHint -
	 *            the width hint
	 * @param hHint -
	 *            the height hint
	 * @return a Point representing the size of the title bar
	 */
	public Point computeSize(GC gc, int wHint, int hHint) {

		Font font = getTitlebarFont();
		gc.setFont(font);
		FontMetrics fontMetrics = gc.getFontMetrics();

		int h = fontMetrics.getHeight() + TITLEBAR_LABEL_PADDING * 2;

		return new Point(wHint, h);

	}

	/**
	 * Returns the font of the title bar.
	 * 
	 * @return font
	 */
	private Font getTitlebarFont() {
		RienaDefaultLnf lnf = LnfManager.getLnf();
		Font font = lnf.getFont("EmbeddedTitlebar.font"); //$NON-NLS-1$
		return font;
	}

	/**
	 * @param value -
	 *            title text
	 * 
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.AbstractLnfRenderer#paint(org.eclipse.swt.graphics.GC,
	 *      java.lang.Object)
	 */
	public void paint(GC gc, Object value) {

		gc.setAdvanced(true);
		gc.setAntialias(SWT.OFF);

		Font font = getTitlebarFont();
		gc.setFont(font);

		// Background
		RienaDefaultLnf lnf = LnfManager.getLnf();
		Color startColor = lnf.getColor("EmbeddedTitlebar.passiveBackgroundStartColor"); //$NON-NLS-1$
		Color endColor = lnf.getColor("EmbeddedTitlebar.passiveBackgroundEndColor"); //$NON-NLS-1$
		if (isActive()) {
			startColor = lnf.getColor("EmbeddedTitlebar.activeBackgroundStartColor"); //$NON-NLS-1$
			endColor = lnf.getColor("EmbeddedTitlebar.activeBackgroundEndColor"); //$NON-NLS-1$
		}
		gc.setForeground(startColor);
		gc.setBackground(endColor);
		int x = getBounds().x;
		int y = getBounds().y;
		int w = getBounds().width;
		int h = getBounds().height;
		if (isPressed()) {
			gc.fillRectangle(x, y, w, h);
		} else {
			gc.fillGradientRectangle(x, y, w, h, true);
		}

		// Border
		Color borderColor = lnf.getColor("EmbeddedTitlebar.passiveBorderColor"); //$NON-NLS-1$
		if (isActive()) {
			borderColor = lnf.getColor("EmbeddedTitlebar.activeBorderColor"); //$NON-NLS-1$
		}
		gc.setForeground(borderColor);
		// - top
		x = getBounds().x + 1;
		y = getBounds().y;
		w = getWidth() - 2;
		gc.drawLine(x, y, x + w, y);
		// - bottom
		y = getBounds().y + getHeight();
		gc.drawLine(x, y, x + w, y);
		// - left
		x = getBounds().x;
		y = getBounds().y + 1;
		h = getHeight() - 2;
		gc.drawLine(x, y, x, y + h);
		// - right
		x = getBounds().x + getWidth();
		gc.drawLine(x, y, x, y + h);

		// Icon
		x = getBounds().x + TITLEBAR_LABEL_PADDING_LEFT;
		if (getImage() != null) {
			y = getBounds().y + (getHeight() - getImage().getImageData().height) / 2;
			gc.drawImage(getImage(), x, y);
			x += getImage().getImageData().width + TITLEBAR_ICON_TEXT_GAP;
		}

		// Text
		String text = ""; //$NON-NLS-1$
		if (value instanceof String) {
			text = (String) value;
		}
		if (!StringUtils.isEmpty(text)) {
			gc.setForeground(lnf.getColor("EmbeddedTitlebar.foreground")); //$NON-NLS-1$

			int y2 = (getHeight() - gc.getFontMetrics().getHeight()) / 2;
			if ((getHeight() - gc.getFontMetrics().getHeight()) % 2 != 0) {
				y2++;
			}
			y = getBounds().y + y2;
			int maxWidth = getWidth() - (x - getBounds().x) - TITLEBAR_LABEL_PADDING;
			text = SwtUtilities.clipText(gc, text, maxWidth);
			gc.drawText(text, x, y, true);
		}

		// Hover border
		if (isHover() && !isPressed()) {
			x = getBounds().x;
			y = getBounds().y;
			w = getBounds().width;
			h = getHeight();
			getHoverBorderRenderer().setBounds(x, y, w, h);
			getHoverBorderRenderer().paint(gc, null);
		}

	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
		setImage(ImageUtil.getImage(icon));
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public boolean isHover() {
		return hover;
	}

	public void setHover(boolean hover) {
		this.hover = hover;
	}

	private Image getImage() {
		return image;
	}

	private void setImage(Image image) {
		this.image = image;
	}

	private int getHeight() {
		return getBounds().height - 1;
	}

	private int getWidth() {
		return getBounds().width - 1;
	}

	/**
	 * @return the hoverBorderRenderer
	 */
	public HoverBorderRenderer getHoverBorderRenderer() {
		return hoverBorderRenderer;
	}

	/**
	 * @param hoverBorderRenderer
	 *            the hoverBorderRenderer to set
	 */
	public void setHoverBorderRenderer(HoverBorderRenderer hoverBorderRenderer) {
		this.hoverBorderRenderer = hoverBorderRenderer;
	}

	private HoverBorderRenderer getLnfBorderRenderer() {

		HoverBorderRenderer renderer = (HoverBorderRenderer) LnfManager.getLnf().getRenderer(
				"SubModuleViewRenderer.hoverBorderRenderer"); //$NON-NLS-1$
		if (renderer == null) {
			renderer = new HoverBorderRenderer();
		}
		return renderer;

	}

}
