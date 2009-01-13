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
package org.eclipse.riena.security;

import junit.framework.Test;
import junit.framework.TestCase;

import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.tests.collect.NonGatherableTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;
import org.eclipse.riena.tests.collect.TestCollector;

/**
 * Tests all test cases within this package/sub-packages.
 */
@NonGatherableTestCase("This is not a �TestCase�!")
public class AllTests extends TestCase {

	@SuppressWarnings("unchecked")
	public static Test suite() {
		return TestCollector.createTestSuiteWith(Activator.getDefault().getBundle(), AllTests.class.getPackage(), true,
				NonUITestCase.class);
	}

}
