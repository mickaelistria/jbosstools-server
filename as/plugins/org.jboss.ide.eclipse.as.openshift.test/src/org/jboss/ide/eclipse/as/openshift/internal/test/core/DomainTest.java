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
package org.jboss.ide.eclipse.as.openshift.internal.test.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URLEncoder;

import org.jboss.ide.eclipse.as.openshift.core.OpenshiftException;
import org.jboss.ide.eclipse.as.openshift.core.SSHKey;
import org.jboss.ide.eclipse.as.openshift.core.internal.marshalling.DomainRequestJsonMarshaller;
import org.jboss.ide.eclipse.as.openshift.internal.core.request.CreateDomainRequest;
import org.jboss.ide.eclipse.as.openshift.internal.core.request.OpenshiftJsonRequestFactory;
import org.jboss.ide.eclipse.as.openshift.internal.test.core.fakes.TestSSHKey;
import org.junit.Test;

/**
 * @author André Dietisheim
 */
public class DomainTest {

	private static final String USERNAME = "toolsjboss@gmail.com";
	private static final String PASSWORD = "1q2w3e";

	@Test
	public void canMarshallDomainCreateResponse() throws IOException, OpenshiftException {
		String expectedRequestString =
				"password="
						+ PASSWORD
						+ "&json_data=%7B"
						+ "%22rhlogin%22+%3A+"
						+ "%22"
						+ URLEncoder.encode(USERNAME, "UTF-8")
						+ "%22"
						+ "%2C+%22debug%22+%3A+%22true%22"
						+ "%2C+%22namespace%22+%3A+%22toolsjboss%40gmail.com%22"
						+ "%2C+%22alter%22+%3A+%22false%22"
						+ "%2C+%22ssh%22+%3A+%22AAAAB3NzaC1yc2EAAAADAQABAAAAgQC6BGRDydfGsQHhnZgo43dEfLzSJBke%2FhE8MLBBG1%2B5ZwktsrE%2Bf2VdVt0McRLVAO6rdJRyMUX0rTbm7SABRVSX%2BzeQjlfqbbUtYFc7TIfd4RQc3GaISG1rS3C4svRSjdWaG36vDY2KxowdFvpKj8i8IYNPlLoRA%2F7EzzyneS6iyw%3D%3D%22"
						+ "%7D";

		SSHKey sshKey = TestSSHKey.create();
		CreateDomainRequest request = new CreateDomainRequest(USERNAME, sshKey, USERNAME, true);
		String requestString =
				new OpenshiftJsonRequestFactory(
						PASSWORD,
						new DomainRequestJsonMarshaller().marshall(request))
						.create();
		assertEquals(expectedRequestString, requestString);
	}
}