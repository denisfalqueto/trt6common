package br.jus.trt.lib.common_tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.arquillian.ArquillianDataLoaderExtension;
import br.jus.trt.lib.common_tests.dataloader.DataLoader;
import br.jus.trt.lib.common_tests.dataloader.HibernateDataLoader;
import br.jus.trt.lib.common_tests.dataloader.LoadData;
import br.jus.trt.lib.common_tests.dataloader.LoadDatas;
import br.jus.trt.lib.common_tests.dataloader.SqlDataLoader;
import br.jus.trt.lib.common_tests.util.QuerierUtil;

/**
 * Teste executado em um ambiente JEE real, com todos os recursos disponíveis.
 * Ideal para testes de integração, testes de comportamento, etc.
 * 
 * @author augusto
 *
 */
@RunWith(Arquillian.class)
@Transactional(value = TransactionMode.ROLLBACK)
public abstract class DeployableTestBase extends TestBase {

	@Inject
	private QuerierUtil querier;

	/**
	 * Adiciona o arquivo de configuração de módulos do JBoss/Wildfly ao
	 * aterfato "deployable"
	 * 
	 * @param archive
	 *            "deployable" que receberá o arquivo de configuração de
	 *            módulos.
	 */
	protected static void addDefaultJbossDeploymentStructure(WebArchive archive) {

		// buscando o arquivo baseado no classloader
		URL resource = Thread.currentThread().getContextClassLoader()
				.getResource("test-jboss-deployment-structure.xml");

		archive.addAsWebInfResource(resource,
				"META-INF/jboss-deployment-structure.xml");
	}

	/**
	 * Adiciona o arquivo de configuração de módulos do JBoss/Wildfly ao
	 * aterfato "deployable"
	 * 
	 * @param archive
	 *            "deployable" que receberá o arquivo de configuração de
	 *            módulos.
	 */
	protected static void addDefaultJbossDeploymentStructure(
			EnterpriseArchive archive) {

		// buscando o arquivo baseado no classloader
		URL resource = Thread.currentThread().getContextClassLoader()
				.getResource("test-jboss-deployment-structure.xml");

		archive.addAsResource(resource, "jboss-deployment-structure.xml");
	}

	/**
	 * Pelo fato de o arquillian não conseguir importar pom.xml com o 
	 * packaging 'ejb', é necessário mudar para packaging 'jar' em runtime.
	 * 
	 * @param path 
	 * 			caminho para o pom ejb original
	 */
	protected static String createTempEJBPomWithJarPackaging(String path) {
		try {
			String pom = new String(Files.readAllBytes(Paths.get(path)));
			pom = pom.replaceAll("<packaging>ejb</packaging>", "<packaging>jar</packaging>");
			
			String fileName = path + "-tmp.xml";
			
			PrintWriter out = new PrintWriter(fileName);
			out.print(pom);
			out.close();
			
			return fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Adiciona ao artefato as bibliotecas configuradas no arquivo pom.xml
	 * encontrado na raíz do projeto.
	 * 
	 * @param container
	 *            Artefato que receberá as bibliotecas.
	 */
	protected static void addLibsFromPom(LibraryContainer<?> container) {
		File[] libs = loadLibsFromPom();
		container.addAsLibraries(libs);
	}

	/**
	 * Adiciona ao artefato as bibliotecas configuradas no arquivo pom.xml
	 * encontrado no diretório informado.
	 * 
	 * @param container
	 *            Artefato que receberá as bibliotecas.
	 */
	protected static void addLibsFromPom(LibraryContainer<?> container, String path) {
		File[] libs = loadLibsFromPom(path, ScopeType.COMPILE, ScopeType.TEST);
		container.addAsLibraries(libs);
	}

	/**
	 * Instala a extensão do Arquillian que permite o funcionamento das soluções
	 * de DataLoader.
	 * 
	 * @param archive
	 *            Arquivo que receberá a ativação da extensão de Data Loader
	 * 
	 * @see LoadData
	 * @see LoadDatas
	 * @see DataLoader
	 * @see HibernateDataLoader
	 * @see SqlDataLoader
	 */
	public static void installDataLoaderExtension(ManifestContainer<?> archive) {
		archive.addAsServiceProvider(RemoteLoadableExtension.class,
				ArquillianDataLoaderExtension.class);
	}

	/**
	 * Carrega todas as dependências de escopo "compile" a partir do arquivo
	 * "pom.xml" na raiz do projeto.
	 * 
	 * @return Array com as dependências configuradas no arquivo "pom.xml"
	 */
	public static File[] loadLibsFromPom() {
		return loadLibsFromPom("pom.xml", ScopeType.COMPILE, ScopeType.TEST);
	}

	/**
	 * Carrega todas as dependências de escopo "compile" a partir do arquivo
	 * "pom.xml" na raiz do projeto.
	 * 
	 * @param pomPath
	 *            Caminho para o arquivo pom.
	 * @param scopes
	 *            Escopos que deverão ser considerados (importados) no arquivo
	 *            POM.
	 * @return Array com as dependências configuradas no arquivo "pom.xml".
	 */
	public static File[] loadLibsFromPom(String pomPath, ScopeType... scopes) {
		// carregando configuração de dependências do pom
		Maven.configureResolver().workOffline();
		PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile(pomPath);

		// seleciona as dependências do projeto
		File[] libs = pom.importDependencies(scopes).resolve()
				.withTransitivity().asFile();
		return libs;
	}

	public QuerierUtil getQuerier() {
		return querier;
	}

	public void setQuerier(QuerierUtil querier) {
		this.querier = querier;
	}

}
