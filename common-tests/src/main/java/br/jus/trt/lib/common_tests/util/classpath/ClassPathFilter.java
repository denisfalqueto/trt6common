package br.jus.trt.lib.common_tests.util.classpath;

import java.io.File;

/**
 * Permite adicionar ao {@link ClassPathScanner} o poder de decisão sobre quais diretórios
 * e arquivos deve ser considerados na busca.
 * @author augusto
 *
 */
public interface ClassPathFilter {

	/**
	 * 
	 * @param file Arquivo encontrado pelo scanner.
	 * @return Retornar true se o arquivo deve ser considerado. Retorna false para deconsiderá-lo.
	 */
	public boolean isFileScanneable(File file);
	
	
}
