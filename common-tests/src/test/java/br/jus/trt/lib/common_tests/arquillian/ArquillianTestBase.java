package br.jus.trt.lib.common_tests.arquillian;

import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import br.jus.trt.lib.common_tests.DeployableTestBase;
import br.jus.trt.lib.common_tests.TestBase;
import br.jus.trt.lib.common_tests.alternatives.AlternativeContainerEntityManagerProducer;

/**
 * Classe base (específica deste projeto) com configurações para rodar testes no
 * Arquillian
 * 
 * @author Augusto
 *
 */
@ArquillianSuiteDeployment
// garante a reutilização do @Deployment entre as classes de teste
public abstract class ArquillianTestBase extends DeployableTestBase {

	/**
	 * Método que se integra ao ciclo de vida do Arquillian para realização do
	 * deploy dos arquivos necessários para execução dos testes no servidor de
	 * aplicação.
	 * 
	 * @return Arquivo elaborado para realização do deploy da aplicação + testes
	 *         no servidor de aplicação.
	 */
	@Deployment
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() {

		WebArchive war = ShrinkWrap
				.create(WebArchive.class, "test.war")

				/*
				 * Adicionando todas as classes do projeto ao WAR, com a
				 * intensão de dispobilizar todos os recursos para todos os
				 * testes arquillian.
				 */
				.addPackages(true, TestBase.class.getPackage())

				/*
				 * Removendo especificamente o package que contém os componentes
				 * alternativos, para que não sejam ativados "no bolo", pois
				 * pode causar um comportamente inesperado. Devem ser ativados
				 * intensionalmente, quando necessários.
				 * 
				 * No caso abaixo, mantendo apenas o Alternative que produz um
				 * Container EntityManager
				 */
				.deletePackages(
						true,
						Filters.exclude(AlternativeContainerEntityManagerProducer.class),
						AlternativeContainerEntityManagerProducer.class
								.getPackage())

				/*
				 * Adicionando recursos necessários para todos os testes
				 */
				.addAsResource("dataloader/uf_aa.sql")
				.addAsResource("dataloader/uf_bb.sql")
				.addAsResource("dataloader/uf_cc.sql")
				.addAsResource("test-arquillian-log4j2.xml", "log4j2.xml")
				.addAsResource("test-arquillian-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource("test-arquillian-beans.xml", "beans.xml");

		addLibsFromPom(war);
		installDataLoaderExtension(war);
		addDefaultJbossDeploymentStructure(war);

		System.out.println(war.toString(true));

		return war;
	}

}
