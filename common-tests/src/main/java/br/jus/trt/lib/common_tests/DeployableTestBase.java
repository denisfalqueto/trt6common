package br.jus.trt.lib.common_tests;

import java.io.File;

import javax.ejb.TransactionAttribute;

import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.arquillian.ArquillianCommonRunner;

/**
 * Teste executado em um ambiente JEE real, com todos os recursos disponíveis.
 * Ideal para testes de integração, testes de comportamento, etc.
 * @author augusto
 *
 */
@RunWith(ArquillianCommonRunner.class)
@Transactional(value=TransactionMode.ROLLBACK)
public abstract class DeployableTestBase extends TestBase {

	/**
	 * Carrega todas as dependências de escopo "compile" a partir do arquivo "pom.xml"  na raiz do projeto.
	 * @return Array com as dependências configuradas no arquivo "pom.xml"
	 */
	protected static File[] loadLibsFromPom() {
		return loadLibsFromPom("pom.xml", ScopeType.COMPILE);
	}
	
	/**
	 * Carrega todas as dependências de escopo "compile" a partir do arquivo "pom.xml"  na raiz do projeto.
	 * 
	 * @param pomPath Caminho para o arquivo pom.
	 * @param scopes Escopos que deverão ser considerados (importados) no arquivo POM.
	 * @return Array com as dependências configuradas no arquivo "pom.xml".
	 */
	protected static File[] loadLibsFromPom(String pomPath, ScopeType...scopes) {
		// carregando configuração de dependências do pom
    	PomEquippedResolveStage pom = Maven.resolver().loadPomFromFile(pomPath);
    	
    	// seleciona as dependências do projeto
        File[] libs = pom.importDependencies(scopes).resolve().withTransitivity().asFile();
		return libs;
	}
	
	
}
