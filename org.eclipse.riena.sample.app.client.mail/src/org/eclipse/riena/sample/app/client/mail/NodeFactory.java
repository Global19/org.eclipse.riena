/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.sample.app.client.mail;

import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.ui.workarea.WorkareaManager;

/**
 * Factory to help create {@link IModuleNode}s and {@link ISubModuleNode}s.
 */
public final class NodeFactory {

	private NodeFactory() {
		// prevent instantiation
	}

	public static IModuleNode createModule(final NavigationNodeId nodeId, final String caption,
			final IModuleGroupNode parent) {
		final IModuleNode result = new ModuleNode(nodeId, caption);
		parent.addChild(result);
		return result;
	}

	public static ISubModuleNode createSubModule(final NavigationNodeId nodeId, final String caption,
			final IModuleNode parent, final String viewId) {
		final ISubModuleNode result = new SubModuleNode(nodeId, caption);
		// path found via org.eclipse.riena.ui.swt.imagePaths in plugin.xml
		result.setIcon("generic_element.gif"); //$NON-NLS-1$
		parent.addChild(result);
		WorkareaManager.getInstance().registerDefinition(result, viewId);
		return result;
	}
}
