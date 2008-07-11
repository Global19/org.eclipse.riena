/*******************************************************************************
 * Copyright (c) 2007 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation;


/**
 * Describes the ability of a navigation node to carry a listener The ability is
 * not a part of the INavigationNode, because there are nodes which are not
 * listen able
 */
public interface INavigationNodeListenerable<S extends INavigationNode<C>, C extends INavigationNode<?>, L extends INavigationNodeListener<S, C>> {

	void addListener(L pListener);

	void removeListener(L pListener);

}
