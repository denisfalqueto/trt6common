package br.jus.trt.lib.common_tests.dataloader.arquillian;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.DeployableTestBase;
import br.jus.trt.lib.common_tests.LocalTransactionTestBase;
import br.jus.trt.lib.common_tests.TestBase;
import br.jus.trt.lib.common_tests.arquillian.ArquillianCommonRunner;
import br.jus.trt.lib.common_tests.arquillian.ArquillianDataLoaderExtension;
import br.jus.trt.lib.common_tests.cdi.CDI;
import br.jus.trt.lib.common_tests.dataloader.DataLoader;
import br.jus.trt.lib.common_tests.dataloader.DataLoaderSQL;
import br.jus.trt.lib.common_tests.util.EmptyBean;
import br.jus.trt.lib.common_tests.util.EmptyBeanA;
import br.jus.trt.lib.common_tests.util.EmptyBeanB;
import br.jus.trt.lib.common_tests.util.QualifierB;


/**
 * Testes da classe {@link DataLoaderSQL}
 * @author augusto
 */
public class DataLoaderSQLTest extends DeployableTestBase {

	@Inject
	private DataLoaderSQL loaderSQL;
	
	@Deployment
	public static Archive<?> createDeployment() {

		File[] libs = loadLibsFromPom();

		WebArchive war = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(ArquillianCommonRunner.class,
						DeployableTestBase.class, TestBase.class, ArquillianDataLoaderExtension.class)
				.addPackage(DataLoader.class.getPackage())		
				.addAsLibraries(libs)
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource("test-beans.xml", "beans.xml");

		System.out.println(war.toString(true));

		return war;
	}	
	
	/**
	 * Tenta executar um script existente na raiz do projeto, considerando seu caminho relativo.
	 */
	@Test
	public void loadScriptTest() throws Exception {
		// garante que não há nenhuma uf com sigla "aa"
		long count = getQuerier().executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(0, count);
		
		loaderSQL.setScriptPath("dataloader/uf_aa.sql");
		loaderSQL.load();
		
		// buscando o registro para confirmação
		count = getQuerier().executeCountQuery("select count(uf) from UF uf where uf.sigla=?", "aa");
		Assert.assertEquals(1, count);
	}
	
}
