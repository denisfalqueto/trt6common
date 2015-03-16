package br.jus.trt.lib.qbe.util;


public class StringUtil {

	/**
	 * Verifica se uma string é vazia ou null.
	 * @param s String a ser verificada.
	 * @return true se a string for null, vazia ou composta por espaços. false caso contrário.
	 */
	public static boolean isStringEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
	

}