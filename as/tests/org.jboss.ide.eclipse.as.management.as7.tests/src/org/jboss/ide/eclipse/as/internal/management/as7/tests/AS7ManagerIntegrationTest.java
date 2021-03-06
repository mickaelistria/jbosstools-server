/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.ide.eclipse.as.internal.management.as7.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import org.jboss.ide.eclipse.as.internal.management.as7.tests.utils.AS7ManagerTestUtils;
import org.jboss.ide.eclipse.as.internal.management.as71.AS71Manager;
import org.jboss.ide.eclipse.as.management.core.JBoss7DeploymentState;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManangerException;
import org.jboss.ide.eclipse.as.management.core.JBoss7ServerState;
import org.jboss.ide.eclipse.as.test.server.JBossManagerTest.MockAS7ManagementDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author André Dietisheim
 */
public class AS7ManagerIntegrationTest {

	private AS71Manager manager;

	@Before
	public void setUp() throws IOException {
		assertTrue("There is no server at " + AS7ManagerTestUtils.LOCALHOST +
				" that listens on port " + AS71Manager.MGMT_PORT,
				AS7ManagerTestUtils.isListening(AS7ManagerTestUtils.LOCALHOST, AS71Manager.MGMT_PORT));
		this.manager = new AS71Manager( new MockAS7ManagementDetails(
				AS7ManagerTestUtils.LOCALHOST, AS71Manager.MGMT_PORT));
	}

	@After
	public void tearDown() {
		manager.dispose();
	}

	@Ignore
	@Test
	public void canDeploy() throws Exception {
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(warFile));

			String response = AS7ManagerTestUtils.waitForRespose(
					"minimalistic", AS7ManagerTestUtils.LOCALHOST, AS7ManagerTestUtils.WEB_PORT);
			assertTrue(response != null
					&& response.indexOf("minimalistic") >= 0);
		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(warFile, manager);
		}
	}

	@Ignore
	@Test
	public void deployedWarIsResponding() throws Exception {
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(warFile));

			String response = AS7ManagerTestUtils.waitForRespose(
					"minimalistic", AS7ManagerTestUtils.LOCALHOST, AS7ManagerTestUtils.WEB_PORT);
			assertTrue(response != null
					&& response.indexOf("minimalistic") >= 0);

		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(warFile, manager);
		}
	}

	@Ignore
	@Test(expected = JBoss7ManangerException.class)
	public void cannotDeployWarTwice() throws Exception {
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(warFile));
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(warFile));
		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(warFile, manager);
		}
	}

	@Ignore
	@Test(expected = JBoss7ManangerException.class)
	public void cannotUndeployNondeployed() throws JBoss7ManangerException, InterruptedException, ExecutionException {
		AS7ManagerTestUtils.waitUntilFinished(manager.undeploy("inexistant"));
	}

	@Ignore
	@Test
	public void canReplaceWar() throws Exception {
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		File warFile2 = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.GWT_HELLOWORLD_WAR);
		String name = warFile.getName();
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(name, warFile));
			AS7ManagerTestUtils.waitUntilFinished(manager.replace(name, warFile2));
			String response = AS7ManagerTestUtils.waitForRespose(
					"minimalistic", AS7ManagerTestUtils.LOCALHOST, AS7ManagerTestUtils.WEB_PORT);
			assertTrue(response != null
					&& response.indexOf("GWT") >= 0);
		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(name, manager);
		}
	}

	@Test
	public void getEnabledStateIfDeploymentIsDeployed() throws URISyntaxException, IOException, JBoss7ManangerException {
		String deploymentName = "testDeployment";
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.deploy(deploymentName, warFile));
			JBoss7DeploymentState state = manager.getDeploymentState(deploymentName);
			assertNotNull(state);
			assertThat(state, equalTo(JBoss7DeploymentState.STARTED));
		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(deploymentName, manager);
		}
	}

	@Test
	public void getDisabledStateIfDeploymentIsOnlyAdded() throws URISyntaxException, IOException,
			JBoss7ManangerException {
		String deploymentName = "testDeployment";
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			AS7ManagerTestUtils.waitUntilFinished(manager.add(deploymentName, warFile));
			JBoss7DeploymentState state = manager.getDeploymentState(deploymentName);
			assertNotNull(state);
			assertThat(state, equalTo(JBoss7DeploymentState.STOPPED));
		} finally {
			AS7ManagerTestUtils.quietlyRemove(deploymentName, manager);
		}
	}

	@Test(expected = JBoss7ManangerException.class)
	public void getErrorIfDeploymentIsNotDeployed() throws URISyntaxException, IOException, JBoss7ManangerException {
		String deploymentName = "testDeployment";
		try {
			manager.getDeploymentState(deploymentName);
		} finally {
			AS7ManagerTestUtils.quietlyUndeploy(deploymentName, manager);
		}
	}

	@Ignore
	@Test
	public void canAddDeploymentDirectory() throws URISyntaxException, IOException, JBoss7ManangerException {
		String deploymentName = getRandomDeploymentName();
		File warFile = AS7ManagerTestUtils.getWarFile(AS7ManagerTestUtils.MINIMALISTIC_WAR);
		try {
			assertFalse(manager.hasDeployment(deploymentName));
			File deploymentDir = new File(System.getProperty("java.io.tmpdir"), "as7dir");
			manager.addDeploymentDirectory(deploymentDir.getAbsolutePath());
			
			AS7ManagerTestUtils.waitForResponseCode(200, deploymentName, AS7ManagerTestUtils.LOCALHOST, AS7ManagerTestUtils.WEB_PORT);
		} finally {
			AS7ManagerTestUtils.quietlyRemove(deploymentName, manager);
		}
	}

	@Test
	public void canGetServerState() throws JBoss7ManangerException {
		assertEquals(JBoss7ServerState.RUNNING, manager.getServerState());
	}

	@Test
	public void canStopServer() throws JBoss7ManangerException, UnknownHostException, IOException {
		manager.stopServer();
		assertFalse(
				AS7ManagerTestUtils.isListening(AS7ManagerTestUtils.LOCALHOST, AS71Manager.MGMT_PORT));
	}

	private String getRandomDeploymentName() {
		return String.valueOf(System.currentTimeMillis());
	}
}