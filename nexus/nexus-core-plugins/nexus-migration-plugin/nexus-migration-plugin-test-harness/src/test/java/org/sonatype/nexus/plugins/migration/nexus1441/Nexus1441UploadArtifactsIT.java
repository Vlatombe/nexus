/**
 * Copyright (c) 2008-2011 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.nexus.plugins.migration.nexus1441;

import org.apache.maven.index.artifact.Gav;
import org.testng.annotations.Test;

public class Nexus1441UploadArtifactsIT
    extends AbstractDeployBridgeIT
{

    @Test
    public void deployRelease()
        throws Exception
    {
        Gav gav =
            new Gav( "nxcm280.direct", "direct-deploy-released", "1.0", null, "jar", null, null, null, false, false,
                     null, false, null );
        deploy( gav, "test-releases-local", false );
    }

    @Test
    public void deploySnapshot()
        throws Exception
    {
        Gav gav =
            new Gav( "nxcm280.direct", "direct-deployed-snapshot", "1.0-SNAPSHOT", null, "jar", null, null, null, true,
                     false, null, false, null );
        deploy( gav, "test-snapshots-local", false );
    }

}
