package org.sonatype.plugin.maven;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.License;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.plugin.ExtensionPoint;
import org.sonatype.plexus.plugin.Managed;
import org.sonatype.plugin.metadata.PluginMetadataGenerationRequest;
import org.sonatype.plugin.metadata.PluginMetadataGenerator;
import org.sonatype.plugin.metadata.gleaner.GleanerException;

/**
 * Generates a plugin's <tt>plugin.xml</tt> descriptor file based on the project's pom and class annotations.
 * 
 * @goal generate-metadata
 * @phase process-classes
 * @requiresDependencyResolution compile
 */
public class PluginDescriptorMojo
    extends AbstractMojo
{

    /**
     * The output location for the generated plugin descriptor.
     * 
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plugin.xml"
     * @required
     */
    private File generatedPluginMetadata;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * The ID of the target application. For example if this plugin was for the Nexus Repository Manager, the ID would
     * be, 'nexus'.
     * 
     * @parameter
     * @required
     */
    private String applicationId;

    /**
     * The edition of the target application. Some applications come in multiple flavors, OSS, PRO, Free, light, etc.
     * 
     * @parameter
     */
    private String applicationEdition;

    /**
     * The minimum product version of the target application.
     * 
     * @parameter
     */
    private String applicationMinVersion;

    /**
     * The maximum product version of the target application.
     * 
     * @parameter
     */
    private String applicationMaxVersion;

    /**
     * The list of other plugins this plugin depends on
     * 
     * @parameter
     */
    private List<String> pluginDependencies;

    /** @component */
    private PluginMetadataGenerator metadataGenerator;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        PluginMetadataGenerationRequest request = new PluginMetadataGenerationRequest();
        request.setGroupId( this.mavenProject.getGroupId() );
        request.setArtifactId( this.mavenProject.getArtifactId() );
        request.setVersion( this.mavenProject.getVersion() );
        request.setName( this.mavenProject.getName() );
        request.setDescription( this.mavenProject.getDescription() );
        request.setPluginSiteURL( this.mavenProject.getUrl() );

        request.setApplicationId( this.applicationId );
        request.setApplicationEdition( this.applicationEdition );
        request.setApplicationMinVersion( this.applicationMinVersion );
        request.setApplicationMaxVersion( this.applicationMaxVersion );

        // licenses
        if ( this.mavenProject.getLicenses() != null )
        {
            for ( License mavenLicenseModel : (List<License>) this.mavenProject.getLicenses() )
            {
                request.addLicense( mavenLicenseModel.getName(), mavenLicenseModel.getUrl() );
            }
        }

        // dependencies
        if ( this.mavenProject.getDependencies() != null )
        {
            for ( Dependency mavenDependency : (List<Dependency>) this.mavenProject.getDependencies() )
            {
                if ( mavenDependency.getScope().equals( "compile" ) || mavenDependency.getScope().equals( "runtime" ) )
                {
                    request.addClasspathDependency( mavenDependency.getGroupId(), mavenDependency.getArtifactId(),
                                                    mavenDependency.getVersion() );
                }
            }
        }

        request.setOutputFile( this.generatedPluginMetadata );
        request.setClassesDirectory( new File( mavenProject.getBuild().getOutputDirectory() ) );
        try
        {
            if ( mavenProject.getCompileClasspathElements() != null )
            {
                for ( String classpathElement : (List<String>) mavenProject.getCompileClasspathElements() )
                {
                    request.getClasspath().add( new File( classpathElement ) );
                }
            }
        }
        catch ( DependencyResolutionRequiredException e )
        {
            throw new MojoFailureException( "Plugin failed to resolve dependencies: " + e.getMessage(), e );
        }

        if ( this.pluginDependencies != null )
        {
            for ( String gavString : this.pluginDependencies )
            {
                String[] split = gavString.split( ":" );
                if ( split.length != 3 )
                {
                    throw new MojoFailureException( "Invalid entry in pluginDependencies: " + gavString
                        + ", the string must be in the format of 'groupId:artifactId:version'" );
                }

                String groupId = split[0];
                String artifactId = split[1];
                String version = split[2];

                // make sure it is a real dependency
                Dependency otherPlugin = null;
                for ( Dependency mavenDependency : (List<Dependency>) this.mavenProject.getDependencies() )
                {
                    if ( mavenDependency.getGroupId().equals( groupId )
                        && mavenDependency.getArtifactId().equals( artifactId )
                        && mavenDependency.getVersion().equals( version ) )
                    {
                        // this dep should be marked 'provied'
                        otherPlugin = mavenDependency;
                    }
                }

                if ( otherPlugin == null )
                {
                    throw new MojoFailureException( "GAV: " + gavString + ", must be defined as a dependency." );
                }

                // the dep needs to be provided
                if ( !otherPlugin.getScope().equals( "provided" ) )
                {
                    throw new MojoFailureException( "Dependency: " + gavString + ", must have scope 'provided'" );
                }

                // finally now just set the plugin dependency on the request
                request.addPluginDependency( otherPlugin.getGroupId() );
            }
        }

        request.getAnnotationClasses().add( ExtensionPoint.class );
        request.getAnnotationClasses().add( Managed.class );

        // do the work
        try
        {
            this.metadataGenerator.generatePluginDescriptor( request );
        }
        catch ( GleanerException e )
        {
            throw new MojoFailureException( "Failed to generante plugin xml file: " + e.getMessage(), e );
        }
    }
}
