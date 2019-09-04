package org.javamagazine.arquillianexample;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Example of universal deployment method for the testing of back-end  classes testing within
 * the ArquillianExampleUI project.
 */
public class Deployments {
    public static final String WEBAPP_SRC = "src/main/webapp";

    public static WebArchive getTestDeployment() {
        return ShrinkWrap.create(WebArchive.class, "ArquillianExampleUI-Test.war")
                .addPackage("org.javamagazine.arquillianexample")
                .addPackage("org.javamagazine.arquillianexample.cdi")
                .addPackage("org.javamagazine.arquillianexample.cdi.util")
                .addPackage("org.javamagazine.arquillianexample.entity")
                .addPackage("org.javamagazine.arquillianexample.session")
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
                        .importDirectory(WEBAPP_SRC + "/customer").as(GenericArchive.class),
                        "/", Filters.include(".*\\.xhtml$"))
                .addAsManifestResource(EmptyAsset.INSTANCE,"beans.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("mock-web.xml", "web.xml");
    }
}
