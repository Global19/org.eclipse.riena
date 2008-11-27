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
package org.eclipse.riena.navigation.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.internal.navigation.Activator;
import org.eclipse.riena.navigation.IModuleGroupNodeExtension;
import org.eclipse.riena.navigation.IModuleNodeExtension;
import org.eclipse.riena.navigation.INavigationAssembler;
import org.eclipse.riena.navigation.INavigationAssemblyExtension;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.INavigationNodeProvider;
import org.eclipse.riena.navigation.ISubApplicationNodeExtension;
import org.eclipse.riena.navigation.ISubModuleNodeExtension;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.osgi.service.log.LogService;

/**
 * This class provides service methods to get information provided by
 * WorkAreaPresentationDefinitions and NavigationNodePresentationDefitinios
 * identified by a given presentationID.
 */
public class NavigationNodeProvider implements INavigationNodeProvider {

	private final static Logger LOGGER = Activator.getDefault().getLogger(NavigationNodeProvider.class);

	private Map<String, INavigationAssembler> assemblyId2AssemblerCache = new HashMap<String, INavigationAssembler>();

	/**
	 * 
	 */
	public NavigationNodeProvider() {
		// symbols are not replaced here - this happens upon creation of the NavigationNode
		Inject.extension(getNavigationAssemblyExtensionPointSafe()).useType(getNavigationAssemblyExtensionIFSafe())
				.into(this).andStart(Activator.getDefault().getContext());
	}

	private String getNavigationAssemblyExtensionPointSafe() {
		if (getNavigationAssemblyExtensionPoint() != null && getNavigationAssemblyExtensionPoint().trim().length() != 0) {
			return getNavigationAssemblyExtensionPoint();
		} else {
			return INavigationAssemblyExtension.EXTENSIONPOINT;
		}
	}

	/**
	 * Override this method if you intend to use a different extension point
	 * 
	 * @return The extension point used to contribute navigation assemblies
	 */
	public String getNavigationAssemblyExtensionPoint() {

		return INavigationAssemblyExtension.EXTENSIONPOINT;
	}

	private Class<? extends INavigationAssemblyExtension> getNavigationAssemblyExtensionIFSafe() {

		if (getNavigationAssemblyExtensionIF() != null && getNavigationAssemblyExtensionIF().isInterface()) {
			return getNavigationAssemblyExtensionIF();
		} else {
			return INavigationAssemblyExtension.class;
		}
	}

	public Class<? extends INavigationAssemblyExtension> getNavigationAssemblyExtensionIF() {

		return INavigationAssemblyExtension.class;
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#createNode(org.eclipse.riena.navigation.INavigationNode,
	 *      org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	@SuppressWarnings("unchecked")
	protected INavigationNode<?> _provideNode(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {
		INavigationNode<?> targetNode = findNode(getRootNode(sourceNode), targetId);
		if (targetNode == null) {
			if (LOGGER.isLoggable(LogService.LOG_DEBUG)) {
				LOGGER.log(LogService.LOG_DEBUG, "createNode: " + targetId); //$NON-NLS-1$
			}

			INavigationAssembler assembler = getNavigationAssembler(targetId, argument);
			if (assembler != null) {
				prepareNavigationNodeBuilder(targetId, assembler);
				targetNode = assembler.buildNode(targetId, argument);
				INavigationNode parentNode = null;
				if (argument != null && argument.getParentNodeId() != null) {
					parentNode = _provideNode(sourceNode, argument.getParentNodeId(), null);
				} else {
					parentNode = _provideNode(sourceNode, new NavigationNodeId(assembler.getAssembly()
							.getParentTypeId()), null);
				}
				parentNode.addChild(targetNode);
			} else {
				throw new ExtensionPointFailure("NavigationNodeType not found. ID=" + targetId.getTypeId()); //$NON-NLS-1$
			}
		}
		return targetNode;
	}

	protected GenericNavigationAssembler createDefaultAssembler() {
		return new GenericNavigationAssembler();
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#createNode(org.eclipse.riena.navigation.INavigationNode,
	 *      org.eclipse.riena.navigation.NavigationNodeId,
	 *      org.eclipse.riena.navigation.NavigationArgument)
	 */
	public INavigationNode<?> provideNode(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {

		return _provideNode(sourceNode, targetId, argument);
	}

	/**
	 * Used to prepare the NavigationNodeBuilder in a application specific way.
	 * 
	 * @param targetId
	 * @param assembler
	 */
	protected void prepareNavigationNodeBuilder(NavigationNodeId targetId, INavigationAssembler assembler) {
		// do nothing by default
	}

	public Collection<INavigationAssembler> getNavigationAssemblers() {
		return assemblyId2AssemblerCache.values();
	}

	public void registerNavigationAssembler(String id, INavigationAssembler assembler) {
		assemblyId2AssemblerCache.put(id, assembler);
	}

	public INavigationAssembler getNavigationAssembler(NavigationNodeId nodeId, NavigationArgument argument) {

		// TODO should we cache the result?
		if (nodeId != null && nodeId.getTypeId() != null) {
			for (INavigationAssembler probe : getNavigationAssemblers()) {
				if (probe.acceptsToBuildNode(nodeId, argument)) {
					return probe;
				}
			}
		}

		return null;
	}

	/**
	 * @param node
	 * @return
	 */
	protected INavigationNode<?> getRootNode(INavigationNode<?> node) {
		if (node.getParent() == null) {
			return node;
		}
		return getRootNode(node.getParent());
	}

	/**
	 * @param node
	 * @param targetId
	 * @return
	 */
	protected INavigationNode<?> findNode(INavigationNode<?> node, NavigationNodeId targetId) {
		if (targetId == null) {
			return null;
		}
		if (targetId.equals(node.getNodeId())) {
			return node;
		}
		for (INavigationNode<?> child : node.getChildren()) {
			INavigationNode<?> foundNode = findNode(child, targetId);
			if (foundNode != null) {
				return foundNode;
			}
		}
		return null;
	}

	public void register(INavigationAssemblyExtension assembly) {

		INavigationAssembler assembler = assembly.createNavigationAssembler();
		if (assembler == null) {
			assembler = createDefaultAssembler();
		}
		assembler.setAssembly(assembly);

		registerNavigationAssembler(assembly.getTypeId(), assembler);

		// TODO register for parent?
		if (assembly.getSubApplicationNode() != null) {
			register(assembly.getSubApplicationNode(), assembler, assembly);
		}
		if (assembly.getModuleGroupNode() != null) {
			register(assembly.getModuleGroupNode(), assembler, assembly);
		}
		if (assembly.getModuleNode() != null) {
			register(assembly.getModuleNode(), assembler, assembly);
		}
		if (assembly.getSubModuleNode() != null) {
			register(assembly.getSubModuleNode(), assembler, assembly);
		}
	}

	public void register(ISubApplicationNodeExtension subapplication, INavigationAssembler assembler,
			INavigationAssemblyExtension assembly) {

		for (IModuleGroupNodeExtension group : subapplication.getModuleGroupNodes()) {
			register(group, assembler, assembly);
		}
	}

	public void register(IModuleGroupNodeExtension group, INavigationAssembler assembler,
			INavigationAssemblyExtension assembly) {

		for (IModuleNodeExtension module : group.getModuleNodes()) {
			register(module, assembler, assembly);
		}
	}

	public void register(IModuleNodeExtension module, INavigationAssembler assembler,
			INavigationAssemblyExtension assembly) {

		for (ISubModuleNodeExtension submodule : module.getSubModuleNodes()) {
			register(submodule, assembler, assembly);
		}
	}

	public void register(ISubModuleNodeExtension submodule, INavigationAssembler assembler,
			INavigationAssemblyExtension assembly) {

		for (ISubModuleNodeExtension nestedSubmodule : submodule.getSubModuleNodes()) {
			register(nestedSubmodule, assembler, assembly);
		}
	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#cleanUp()
	 */
	public void cleanUp() {
		// nothing to do
	}

	/**
	 * This is called by extension injection to provide the extension points
	 * found
	 * 
	 * @param data
	 *            The navigation assemblies contributed by all extension points
	 */
	public void update(INavigationAssemblyExtension[] data) {

		cleanUp();
		for (INavigationAssemblyExtension assembly : data) {
			register(assembly);
		}
	}
}
