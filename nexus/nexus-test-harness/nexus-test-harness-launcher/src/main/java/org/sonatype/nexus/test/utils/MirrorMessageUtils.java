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
package org.sonatype.nexus.test.utils;

import java.io.IOException;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.model.MirrorResource;
import org.sonatype.nexus.rest.model.MirrorResourceListRequest;
import org.sonatype.nexus.rest.model.MirrorResourceListResponse;
import org.sonatype.nexus.rest.model.MirrorStatusResource;
import org.sonatype.nexus.rest.model.MirrorStatusResourceListResponse;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;
import org.testng.Assert;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.sonatype.nexus.test.utils.NexusRequestMatchers.*;
import com.thoughtworks.xstream.XStream;

public class MirrorMessageUtils
{
    private XStream xstream;

    private MediaType mediaType;

    private static final Logger LOG = LoggerFactory.getLogger( MirrorMessageUtils.class );

    public MirrorMessageUtils( XStream xstream, MediaType mediaType )
    {
        super();
        this.xstream = xstream;
        this.mediaType = mediaType;
    }

    public MirrorResourceListResponse getMirrors( String repositoryId )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", mediaType );

        String serviceURI = "service/local/repository_mirrors/" + repositoryId;

        String responseText = RequestFacade.doGetForText(serviceURI, representation, isSuccessful());
        LOG.debug( " getResourceFromResponse: " + responseText );

        representation = new XStreamRepresentation( xstream, responseText, mediaType );

        // this
        MirrorResourceListResponse resourceResponse =
            (MirrorResourceListResponse) representation.getPayload( new MirrorResourceListResponse() );

        Assert.assertNotNull( resourceResponse, "Resource Response shouldn't be null" );

        for ( MirrorResource resource : (List<MirrorResource>) resourceResponse.getData() )
        {
            Assert.assertNotNull( "Id shouldn't be null", resource.getId() );
        }

        return resourceResponse;
    }

    public MirrorResourceListResponse setMirrors( String repositoryId, MirrorResourceListRequest resourceRequest )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", mediaType );

        String serviceURI = "service/local/repository_mirrors/" + repositoryId;

        // now set the payload
        representation.setPayload( resourceRequest );

        String responseText = RequestFacade.doPostForText(serviceURI, representation, isSuccessful());
        LOG.debug( " getResourceFromResponse: " + responseText );

        representation = new XStreamRepresentation( xstream, responseText, mediaType );

        // this
        MirrorResourceListResponse resourceResponse =
            (MirrorResourceListResponse) representation.getPayload( new MirrorResourceListResponse() );

        Assert.assertNotNull( resourceResponse, "Resource Response shouldn't be null" );

        for ( MirrorResource resource : (List<MirrorResource>) resourceResponse.getData() )
        {
            Assert.assertNotNull( "Id shouldn't be null", resource.getId() );
        }

        for ( int i = 0; i < resourceResponse.getData().size(); i++ )
        {
            Assert.assertEquals( ( (MirrorResource) resourceResponse.getData().get( i ) ).getUrl(),
                                 ( (MirrorResource) resourceRequest.getData().get( i ) ).getUrl() );
        }

        return resourceResponse;
    }

    public MirrorStatusResourceListResponse getMirrorsStatus( String repositoryId )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", mediaType );

        String serviceURI = "service/local/repository_mirrors_status/" + repositoryId;

        String responseText = RequestFacade.doGetForText(serviceURI, representation, isSuccessful());

        LOG.debug( " getResourceFromResponse: " + responseText);

        representation = new XStreamRepresentation( xstream, responseText, mediaType );

        // this
        MirrorStatusResourceListResponse resourceResponse =
            (MirrorStatusResourceListResponse) representation.getPayload( new MirrorStatusResourceListResponse() );

        Assert.assertNotNull( resourceResponse, "Resource Response shouldn't be null" );

        for ( MirrorStatusResource resource : (List<MirrorStatusResource>) resourceResponse.getData() )
        {
            Assert.assertNotNull( "Id shouldn't be null", resource.getId() );
        }

        return resourceResponse;
    }

    public MirrorResourceListResponse getPredefinedMirrors( String repositoryId )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", mediaType );

        String serviceURI = "service/local/repository_predefined_mirrors/" + repositoryId;

        String responseText = RequestFacade.doGetForText(serviceURI, representation, isSuccessful());
        LOG.debug( " getResourceFromResponse: " + responseText );

        representation = new XStreamRepresentation( xstream, responseText, mediaType );

        // this
        MirrorResourceListResponse resourceResponse =
            (MirrorResourceListResponse) representation.getPayload( new MirrorResourceListResponse() );

        Assert.assertNotNull( resourceResponse, "Resource Response shouldn't be null" );

        for ( MirrorResource resource : (List<MirrorResource>) resourceResponse.getData() )
        {
            Assert.assertNotNull( "URL shouldn't be null", resource.getUrl() );
        }

        return resourceResponse;
    }
}
