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

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;
import org.sonatype.security.rest.model.UserForgotPasswordRequest;
import org.sonatype.security.rest.model.UserForgotPasswordResource;

import com.thoughtworks.xstream.XStream;

/**
 * Example of simple conversion to "fluent" api from static stuff. This opens the gates to paralell ITs too! Currently,
 * we cannot have them.
 * 
 * @author cstamas
 */
public class ForgotPasswordUtils
    extends ITUtil
{
    private final XStream xstream;

    public static ForgotPasswordUtils get( AbstractNexusIntegrationTest test )
    {
        return new ForgotPasswordUtils( test );
    }

    public ForgotPasswordUtils( AbstractNexusIntegrationTest test )
    {
        super( test );

        this.xstream = XStreamFactory.getXmlXStream();
    }

    public Response recoverUserPassword( String username, String email )
        throws Exception
    {
        String serviceURI = "service/local/users_forgotpw";
        UserForgotPasswordResource resource = new UserForgotPasswordResource();
        resource.setUserId( username );
        resource.setEmail( email );

        UserForgotPasswordRequest request = new UserForgotPasswordRequest();
        request.setData( resource );

        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", MediaType.APPLICATION_XML );
        representation.setPayload( request );

        return RequestFacade.sendMessage( serviceURI, Method.POST, representation );
    }
}
