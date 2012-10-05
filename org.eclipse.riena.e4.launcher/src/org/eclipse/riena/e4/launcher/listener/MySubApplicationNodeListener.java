package org.eclipse.riena.e4.launcher.listener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.advanced.MAdvancedFactory;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import org.eclipse.riena.e4.launcher.Activator;
import org.eclipse.riena.e4.launcher.E4XMIConstants;
import org.eclipse.riena.e4.launcher.binder.SubApplicationBinder;
import org.eclipse.riena.e4.launcher.part.NavigationPart;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.listener.SubApplicationNodeListener;
import org.eclipse.riena.navigation.ui.controllers.SubApplicationController;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.views.NavigationViewPart;
import org.eclipse.riena.ui.workarea.IWorkareaDefinition;
import org.eclipse.riena.ui.workarea.WorkareaManager;

@SuppressWarnings("restriction")
public class MySubApplicationNodeListener extends SubApplicationNodeListener {
	private static final String PERSPECTIVES_EXT_POINT = "org.eclipse.ui.perspectives"; //$NON-NLS-1$

	@Inject
	private IEclipseContext context;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Shows the specified perspective (sub-application).
	 */
	@Override
	public void activated(final ISubApplicationNode source) {
		if (source != null) {
			showPerspective(source);
			if (source.getNavigationNodeController() == null) {
				createNodeController(source);
			}

			prepare(source);
		}
	}

	/**
	 * Creates the {@link SubApplicationController} and binds it to {@link ISubApplicationNode}
	 */
	private void createNodeController(final ISubApplicationNode source) {
		final SubApplicationBinder binder = new SubApplicationBinder(source);
		ContextInjectionFactory.inject(binder, context);
		binder.bind();
	}

	private void showPerspective(final ISubApplicationNode source) {
		final String perspectiveId = SwtViewProvider.getInstance().getSwtViewId(source).getId();
		final EModelService modelService = context.get(EModelService.class);
		final MApplication searchRoot = context.get(MApplication.class);
		final List<MPerspective> perspectives = modelService
				.findElements(searchRoot, perspectiveId, MPerspective.class, null, EModelService.IN_ANY_PERSPECTIVE);

		MPerspective perspective = null;
		if (perspectives.isEmpty()) {
			final IExtensionRegistry extensionRegistry = context.get(IExtensionRegistry.class);
			final MElementContainer perspectiveStack = (MPerspectiveStack) modelService.find(E4XMIConstants.PERSPECTIVE_STACK_ID, searchRoot);
			for (final IConfigurationElement e : extensionRegistry.getConfigurationElementsFor(PERSPECTIVES_EXT_POINT)) {
				if (perspectiveId.equals(e.getAttribute("id"))) {
					final MElementContainer p = MAdvancedFactory.INSTANCE.createPerspective();
					p.setElementId(e.getAttribute("id"));
					perspectiveStack.getChildren().add(p);
					p.setParent(perspectiveStack);

					final MPart navigationPart = MBasicFactory.INSTANCE.createPart();
					navigationPart.setElementId(NavigationViewPart.ID);
					// construct the String as in Application.e4xmi
					final String pluginId = Activator.getDefault().getBundleContext().getBundle().getSymbolicName();
					navigationPart.setContributionURI("bundleclass://" + pluginId + "/" + NavigationPart.class.getName());

					p.getChildren().add(navigationPart);
					navigationPart.setParent(p);

					final MPartStack partStack = MBasicFactory.INSTANCE.createPartStack();
					partStack.setElementId(E4XMIConstants.CONTENT_PART_STACK_ID);
					p.getChildren().add(partStack);
					partStack.setParent(p);

					perspective = (MPerspective) p;
				}
			}
		} else {
			perspective = perspectives.get(0);
		}

		if (perspective == null) {
			throw new IllegalStateException("Perspective not found, id: " + perspectiveId);
		}

		perspective.getParent().setSelectedElement(perspective);
		//		final EPartService partService = context.get(EPartService.class);
		//		partService.switchPerspective(perspective);

		//		try {
		//			PlatformUI.getWorkbench().showPerspective(perspectiveId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		//		} catch (final WorkbenchException e) {
		//			throw new UIViewFailure(e.getMessage(), e);
		//		}
	}

	@Override
	public void disposed(final ISubApplicationNode source) {
		final SwtViewProvider viewProvider = SwtViewProvider.getInstance();
		final String perspectiveId = viewProvider.getSwtViewId(source).getId();

		throw new UnsupportedOperationException("TODO close perspective");
		//		viewProvider.unregisterSwtViewId(source);

		//		final IWorkbench workbench = PlatformUI.getWorkbench();
		//		final IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
		//		final IPerspectiveDescriptor perspDesc = registry.findPerspectiveWithId(perspectiveId);
		//		workbench.getActiveWorkbenchWindow().getActivePage().closePerspective(perspDesc, false, false);
	}

	/**
	 * Prepares every sub-module whose definition requires preparation.
	 * 
	 * @param node
	 *            navigation node
	 */
	static void prepare(final INavigationNode<?> node) {
		if ((node == null) || (node.getParent() == null)) {
			return;
		}

		if (node instanceof ISubModuleNode) {
			final ISubModuleNode subModuleNode = (ISubModuleNode) node;
			final IWorkareaDefinition definition = WorkareaManager.getInstance().getDefinition(subModuleNode);
			if ((definition != null) && definition.isRequiredPreparation() && subModuleNode.isCreated()) {
				subModuleNode.prepare();
			}
		}

		/*
		 * The number of children can change while iterating. Only observe the node children !before! the iteration begins. Any child added while iterating will
		 * be handled automatically if preparation is required. Just ensure that there will be no concurrent modification of the children list while iterating
		 * over it. Conclusion is a copy..
		 */
		final List<INavigationNode<?>> children = new ArrayList<INavigationNode<?>>(node.getChildren());
		for (final INavigationNode<?> child : children) {
			prepare(child);
		}
	}
}