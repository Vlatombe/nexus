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
package org.sonatype.nexus.proxy.maven.uid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.nexus.proxy.item.uid.Attribute;
import org.sonatype.nexus.proxy.item.uid.RepositoryItemUidAttributeSource;

/**
 * The attributes implemented in Nexus Maven plugin contributing Maven specific UID attributes.
 * 
 * @author cstamas
 */
@Component( role = RepositoryItemUidAttributeSource.class, hint = "maven" )
public class MavenRepositoryItemUidAttributeSource
    implements RepositoryItemUidAttributeSource
{
    private final Map<Class<?>, Attribute<?>> attributes;

    public MavenRepositoryItemUidAttributeSource()
    {
        Map<Class<?>, Attribute<?>> attrs = new HashMap<Class<?>, Attribute<?>>( 6 );

        attrs.put( IsMavenArtifactAttribute.class, new IsMavenArtifactAttribute() );
        attrs.put( IsMavenSnapshotArtifactAttribute.class, new IsMavenSnapshotArtifactAttribute() );
        attrs.put( IsMavenChecksumAttribute.class, new IsMavenChecksumAttribute() );
        attrs.put( IsMavenPomAttribute.class, new IsMavenPomAttribute() );
        attrs.put( IsMavenRepositoryMetadataAttribute.class, new IsMavenRepositoryMetadataAttribute() );
        attrs.put( IsMavenArtifactSignatureAttribute.class, new IsMavenArtifactSignatureAttribute() );

        this.attributes = Collections.unmodifiableMap( attrs );
    }

    @Override
    public Map<Class<?>, Attribute<?>> getAttributes()
    {
        return attributes;
    }
}
