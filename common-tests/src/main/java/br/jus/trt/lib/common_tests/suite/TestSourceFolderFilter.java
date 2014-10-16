package br.jus.trt.lib.common_tests.suite;

import java.io.File;
import java.io.IOException;

import br.jus.trt.lib.common_tests.util.classpath.ClassPathFilter;

/**
 * Filtro que identifica o output-folder das classes de teste das aplicações.
 * Por default, as aplicações têm este tipo de source folder configurado para "test-classes"
 * 
 * @author augusto
 *
 */
public class TestSourceFolderFilter implements ClassPathFilter {

	/** Diretório padronizado para output folder das classes de teste*/
	public static final String TEST_OUTPUT_FOLDER = "test-classes";

	public boolean isFileScanneable(File file) {
		boolean scanneable = false;
		try {
			scanneable =  file.getCanonicalPath().contains(TEST_OUTPUT_FOLDER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scanneable;
	}
	
	
	
}
