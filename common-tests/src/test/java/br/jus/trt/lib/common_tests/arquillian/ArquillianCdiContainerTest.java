package br.jus.trt.lib.common_tests.arquillian;

import java.io.File;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import br.jus.trt.lib.common_tests.DeployableTestBase;
import br.jus.trt.lib.common_tests.TestBase;
import br.jus.trt.lib.common_tests.alternatives.AlternativeContainerEntityManagerProducer;
import br.jus.trt.lib.common_tests.cdi.CDI;
import br.jus.trt.lib.common_tests.util.EmptyBean;
import br.jus.trt.lib.common_tests.util.EmptyBeanA;
import br.jus.trt.lib.common_tests.util.EmptyBeanB;
import br.jus.trt.lib.common_tests.util.QualifierB;
import br.jus.trt.lib.common_tests.util.QuerierUtil;

/**
 * Tenta garantir que o container de injeção de dependências via CDI está
 * devidamente funcional.
 * 
 * @author augusto
 *
 */
public class ArquillianCdiContainerTest extends DeployableTestBase {

	@Inject
	private EmptyBeanA emptyBeanA;

	@Deployment
	public static Archive<?> createDeployment() {

		File[] libs = loadLibsFromPom();

		WebArchive war = ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(EmptyBean.class, EmptyBeanA.class,
						EmptyBeanB.class, CDI.class, QualifierB.class)
				.addClasses(ArquillianCommonRunner.class,
						DeployableTestBase.class, TestBase.class,
						ArquillianDataLoaderExtension.class, QuerierUtil.class,
						AlternativeContainerEntityManagerProducer.class)
				.addAsLibraries(libs)
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource("test-beans.xml", "beans.xml");

		System.out.println("YES");
		System.out.println(war.toString(true));

		return war;
	}

	/**
	 * Verifica se a injeção de dependência está sendo habilitada para a classe
	 * de teste que executa com {@link ArquillianCommonRunner}
	 */
	@Test
	public void cdiBeanInjectionTest() {
		Assert.assertNotNull(emptyBeanA);
	}

	/**
	 * Verifica se a injeção de pripriedades aninhadas está funcionando para a
	 * classe de teste que executa com {@link ArquillianCommonRunner}
	 */
	@Test
	public void cdiBeanNestedInjectionTest() {
		Assert.assertNotNull(emptyBeanA.getEmptyBeanB());
	}

	/**
	 * Teste de lookup de bean sem qualifiers
	 */
	@Test
	public void simpleLookupTest() {
		EmptyBeanA emptyBeanA = BeanProvider
				.getContextualReference(EmptyBeanA.class);
		Assert.assertNotNull(emptyBeanA);
	}

	/**
	 * Teste de lookup de bean com qualifiers.
	 */
	@Test
	public void withQualifierLookupTest() {

		/*
		 * Busca um bean pela interface + um qualifier que especifica o tipo
		 * concreto.
		 */

		@SuppressWarnings("serial")
		EmptyBean emptyBean = BeanProvider.getContextualReference(
				EmptyBean.class, new AnnotationLiteral<QualifierB>() {
				});
		Assert.assertNotNull(emptyBean);
		Assert.assertTrue(EmptyBeanB.class.equals(emptyBean.getClass()));
		;
	}

}
