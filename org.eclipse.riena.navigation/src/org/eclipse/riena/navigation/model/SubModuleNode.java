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
package org.eclipse.riena.navigation.model;

import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.listener.ISubModuleNodeListener;

/**
 * Default implementation for the sub module node
 */
public class SubModuleNode extends NavigationNode<ISubModuleNode, ISubModuleNode, ISubModuleNodeListener> implements
		ISubModuleNode {

	private boolean selectable = true;
	private boolean closeSubTree = false;
	private boolean closable = false;

	/**
	 * Creates a SubModuleNode.
	 */
	public SubModuleNode() {
		super(null);
	}

	public Class<ISubModuleNode> getValidChildType() {
		return ISubModuleNode.class;
	}

	/**
	 * Creates a SubModuleNode.
	 * 
	 * @param nodeId
	 *            Identifies the node in the application model tree.
	 */
	public SubModuleNode(final NavigationNodeId nodeId) {
		super(nodeId);
	}

	/**
	 * Creates a SubModuleNode.
	 * 
	 * @param nodeId
	 *            Identifies the node in the application model tree.
	 * @param label
	 *            Label of the sub module displayed in the sub modules title
	 *            bar.
	 */
	public SubModuleNode(final NavigationNodeId nodeId, final String label) {
		super(nodeId, label);
	}

	/**
	 * Creates a SubModuleNode.
	 * 
	 * @param label
	 *            Label of the sub module displayed in the sub modules title
	 *            bar.
	 */
	public SubModuleNode(final String label) {
		this(null, label);
	}

	/**
	 * @since 1.2
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * @since 1.2
	 */
	public void setSelectable(final boolean folderNode) {
		this.selectable = folderNode;
	}

	/**
	 * @since 3.0
	 */
	public boolean isCloseSubTree() {
		return closeSubTree;
	}

	/**
	 * @since 3.0
	 */
	public void setCloseSubTree(final boolean close) {
		this.closeSubTree = close;
	}

	/**
	 * @since 3.0
	 */
	public boolean isClosable() {
		return closable;
	}

	/**
	 * @since 3.0
	 */
	public void setClosable(final boolean closeable) {
		this.closable = closeable;
	}

}
