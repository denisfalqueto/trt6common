package br.jus.trt.lib.common_tests;

import java.io.File;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.jus.trt.lib.common_tests.arquillian.ArquillianDataLoaderExtension;

/**
 * Classe base (específica deste projeto) com configurações para rodar testes no Arquillian
 * @author Augusto
 *
 */
@ArquillianSuiteDeployment // garante a reutilização do @Deployment entre as classes de teste
public abstract class CommonTestsArquillianTestBase extends DeployableTestBase {

	@Deployment
	public static Archive<?> createDeployment() {

		File[] libs = loadLibsFromPom();

		WebArchive war = ShrinkWrap
				.create(WebArchive.class, "test.war")
//				.addClasses(ArquillianCommonRunner.class,
//						DeployableTestBase.class, TestBase.class,
//						ArquillianDataLoaderExtension.class,
//						AlternativeEntityManagerProducer.class,
//						LoggerProducer.class, QuerierUtil.class, UF.class,
//						DomainBase.class)
//				.addPackage(DataLoader.class.getPackage())
//				.addPackage(BaseSuite.class.getPackage())
//				.addPackage(ClassPathFilter.class.getPackage())
				.addPackages(true, TestBase.class.getPackage())
				.addAsLibraries(libs)
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsResource("dataloader/uf_aa.sql")
				.addAsResource("dataloader/uf_bb.sql")
				.addAsResource("dataloader/uf_cc.sql")
				.addAsWebInfResource("test-beans.xml", "beans.xml")
				.addAsWebInfResource("test-jboss-deployment-structure.xml",
						"META-INF/jboss-deployment-structure.xml")
				.addAsServiceProvider(RemoteLoadableExtension.class, ArquillianDataLoaderExtension.class);		;

		System.out.println(war.toString(true));

		return war;
	}	
	
}
