package br.jus.trt.lib.common_tests.dataloader;


/**
 * Interface para definição dos métodos de testes. 
 * Criada para permitir o compartilhamento dos métodos de testes em ambientes
 * CDI e Arquillian.
 * @author Augusto
 *
 */
public interface DataLoaderSQLTestDef {

	/**
	 * Tenta executar um script existente na raiz do projeto, considerando seu
	 * caminho relativo.
	 */
	public abstract void loadScriptTest() throws Exception;

}