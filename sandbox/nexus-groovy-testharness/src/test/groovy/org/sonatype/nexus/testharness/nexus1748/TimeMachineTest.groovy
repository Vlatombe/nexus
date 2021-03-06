/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.testharness.nexus1748

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.component.annotations.*
import org.testng.annotations.*

import org.sonatype.nexus.groovytest.NexusCompatibility
import static org.testng.Assert.*@Component(role = TimeMachineTest.class)
public class TimeMachineTest implements Contextualizable {


	@Requirement(role = ColdFusionReactor.class, hint = "java")
	def javaReactor;

	@Requirement(role = ColdFusionReactor.class, hint = "groovy")
	def groovyReactor;
	
	def context;

	@Test
    @NexusCompatibility (minVersion = "1.3")
	void testPlexusContext(){
		assertNotNull context
	}

	@Test
    @NexusCompatibility (minVersion = "1.3")
	void testPlexusJavaWiring()
	{
		assertNotNull javaReactor
		assertTrue javaReactor.givePower( 10000 );
		assertFalse javaReactor.givePower( Integer.MAX_VALUE );
	}

	@Test
    @NexusCompatibility (minVersion = "1.3")
	void testPlexusGroovyWiring()
	{
		assertNotNull groovyReactor
		assertTrue groovyReactor.givePower( 10000 );
		assertFalse groovyReactor.givePower( Integer.MAX_VALUE );
	}

	@Test(expectedExceptions = [IllegalArgumentException.class])
    @NexusCompatibility (minVersion = "1.3")
	void testJavaException()
	{
		assertTrue javaReactor.givePower( -1 );
	}

	@Test(expectedExceptions = [IllegalArgumentException.class])
    @NexusCompatibility (minVersion = "1.3")
	void testGroovyException()
	{
		assertTrue groovyReactor.givePower( -1 );
	}

	void contextualize( org.codehaus.plexus.context.Context context ) 
	{
	    this.context = context;
	}
}