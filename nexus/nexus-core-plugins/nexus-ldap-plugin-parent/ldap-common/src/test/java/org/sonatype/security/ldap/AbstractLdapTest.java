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
package org.sonatype.security.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;

import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.sonatype.ldaptestsuite.AbstractLdapTestEnvironment;

public abstract class AbstractLdapTest
    extends AbstractLdapTestEnvironment
{

    public static final String SECURITY_CONFIG_KEY = "security-xml-file";

    public static final String LDAP_CONFIGURATION_KEY = "application-conf";

    protected static final File PLEXUS_HOME = new File( getBasedir(), "target/plexus-home" );

    protected static final File CONF_HOME = new File( PLEXUS_HOME, "conf" );

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();

        CONF_HOME.mkdirs();

        File outputFile = new File( CONF_HOME, "ldap.xml" );

        // check if we have a custom ldap.xml for this test
        String classname = this.getClass().getName();
        File sourceLdapXml = new File( getBasedir() + "/target/test-classes/" + classname.replace( '.', '/' )
            + "-ldap.xml" );

        if ( sourceLdapXml.exists() )
        {
            this.interpolateLdapXml( sourceLdapXml, outputFile );
        }
        else
        {
            this.interpolateLdapXml( "/test-conf/conf/ldap.xml", outputFile );
        }
    }

    @Override
    protected void customizeContext( Context ctx )
    {
        ctx.put( SECURITY_CONFIG_KEY, new File( CONF_HOME, "security.xml" ).getAbsolutePath() );
        ctx.put( LDAP_CONFIGURATION_KEY, CONF_HOME.getAbsolutePath() );
    }

    /**
     * Copies a resource from from the classpath to the given output file and closes streams.
     * 
     * @param resource
     * @param outputFile
     * @throws IOException
     */
    protected void copyResourceToFile( String resource, File outputFile )
        throws IOException
    {
        InputStream in = getClass().getResourceAsStream( resource );
        OutputStream out = new FileOutputStream( outputFile );
        IOUtil.copy( in, out );
        IOUtil.close( in );
        IOUtil.close( out );
    }

    /**
     * Interpolates the ldap.xml file and copies it to the outputfile.
     * 
     * @param resource
     * @param outputFile
     * @throws IOException
     */
    protected void interpolateLdapXml( String resource, File outputFile )
        throws IOException
    {
        InputStream in = getClass().getResourceAsStream( resource );
        this.interpolateLdapXml( in, outputFile );
        IOUtil.close( in );
    }

    protected void interpolateLdapXml( File sourceFile, File outputFile )
        throws IOException
    {
        FileInputStream fis = new FileInputStream( sourceFile );
        this.interpolateLdapXml( fis, outputFile );
        IOUtil.close( fis );
    }

    private void interpolateLdapXml( InputStream inputStream, File outputFile )
        throws IOException
    {
        HashMap<String, String> interpolationMap = new HashMap<String, String>();
        interpolationMap.put( "port", Integer.toString( this.getLdapServer().getPort() ) );

        Reader reader = new InterpolationFilterReader( new InputStreamReader( inputStream ), interpolationMap );
        OutputStream out = new FileOutputStream( outputFile );
        IOUtil.copy( reader, out );
        IOUtil.close( out );
    }

}
