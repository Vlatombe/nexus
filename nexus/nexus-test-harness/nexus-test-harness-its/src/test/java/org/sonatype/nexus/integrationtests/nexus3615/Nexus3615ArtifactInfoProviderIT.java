package org.sonatype.nexus.integrationtests.nexus3615;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.ArtifactInfoResource;
import org.sonatype.nexus.rest.model.RepositoryUrlResource;

public class Nexus3615ArtifactInfoProviderIT
    extends AbstractNexusIntegrationTest
{

    @Override
    protected void deployArtifacts()
        throws Exception
    {
        super.deployArtifacts();

        File pom = getTestFile( "artifact.pom" );
        File jar = getTestFile( "artifact.jar" );
        getDeployUtils().deployUsingPomWithRest( REPO_TEST_HARNESS_REPO, jar, pom, null, null );
        getDeployUtils().deployUsingPomWithRest( REPO_TEST_HARNESS_REPO2, jar, pom, null, null );
        getDeployUtils().deployUsingPomWithRest( REPO_TEST_HARNESS_RELEASE_REPO, jar, pom, null, null );
    }

    @Test
    public void getInfo()
        throws Exception
    {
        ArtifactInfoResource info =
            getSearchMessageUtil().getInfo( REPO_TEST_HARNESS_REPO, "nexus3615/artifact/1.0/artifact-1.0.jar" );

        Assert.assertEquals( REPO_TEST_HARNESS_REPO, info.getRepositoryId() );
        Assert.assertEquals( "/nexus3615/artifact/1.0/artifact-1.0.jar", info.getRepositoryPath() );
        Assert.assertEquals( "b354a0022914a48daf90b5b203f90077f6852c68", info.getSha1Hash() );
        Assert.assertEquals( 3, info.getRepositories().size() );
        Assert.assertThat( getRepositoryId( info.getRepositories() ), IsCollectionContaining.hasItems(
            REPO_TEST_HARNESS_REPO, REPO_TEST_HARNESS_REPO2, REPO_TEST_HARNESS_RELEASE_REPO ) );
        Assert.assertEquals( "application/java-archive", info.getMimeType() );
        Assert.assertEquals( 1364, info.getSize() );
    }

    private Iterable<String> getRepositoryId( List<RepositoryUrlResource> repositories )
    {
        List<String> repoIds = new ArrayList<String>();
        for ( RepositoryUrlResource repositoryUrlResource : repositories )
        {
            repoIds.add( repositoryUrlResource.getRepositoryId() );
        }

        return repoIds;
    }
}
