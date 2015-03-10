package br.jus.trt.lib.qbe.util;

import com.mysema.query.types.Path;
import com.mysema.query.types.path.StringPath;


public class StringUtil {

	/**
	 * Verifica se uma string é vazia ou null.
	 * @param s String a ser verificada.
	 * @return true se a string for null, vazia ou composta por espaços. false caso contrário.
	 */
	public static boolean isStringEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
	
	public static String getStringPath(@SuppressWarnings("rawtypes") Path path) {
		return path.toString().replaceFirst(".*?\\.", "");
	}
	
	@SuppressWarnings("rawtypes")
	public static Path getFakePath(String path) {
		return new StringPath("fake." + path);
	}

}