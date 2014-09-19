package br.jus.trt.lib.common_core.util;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;

public class StringUtil {

	/** 
	 * Caracteres Especiais
	 */
	public static final String SPECIAL_CHARACTERS = "\\.|\\?|\\!|\\#|\\@|\\$|\\%|\\&|\\*|\\(|\\)|\\+|\\=|\\-|/";
	
	/**
	 * <p>Método responsável por adicionar quantidades de "0" à esquerda do valor.</p>
	 * 
	 * <p>Ex:<ul>
	 * 	<li>getValorPorDigitos("22", 5) -> "00022"</li>
	 * 	<li>getValorPorDigitos("5422", 3) -> "5422"</li>
	 * </ul></p>
	 * 
	 * @param valor 	O valor a ser completado com "0".
	 * @param digitos 	A quantidade de dígitos que o valor necessita.
	 * @return 			O valor formatado pela quantidade de dígitos.
	 */
	public static String getDigitValues(String valor, int digitos) {
		String retorno = null;
		if (valor != null) {
			retorno = leftPad(valor.trim(), digitos, "0");
		}
		return retorno;
	}
	
	/**
	 * <p>Método responsável por adicionar quantidades de <i>token</i> à esquerda do valor.</p>
	 * 
	 * <p>Ex:<ul>
	 * 	<li>leftPad("22", 5, "0") -> "00022"</li>
	 * 	<li>leftPad("5422", 7, "F") -> "FFF5422"</li>
	 * </ul></p>
	 * 
	 * @param valor 	O valor a ser completado com <i>token</i>.
	 * @param digitos 	A quantidade de <i>token</i> que o valor necessita.
	 * @param token		O token que será utilizado para formatar.
	 * @return 			O valor formatado pela quantidade de <i>token</i>.
	 */
	public static String leftPad(String valor, int digitos, String token) {
		String retorno = null;
		if (!isStringEmpty(valor)) {
			retorno = new String(valor.trim());
			while (retorno.length() < digitos) {
				retorno = token + retorno;
			}
		}
		return retorno;
	}
	
	/**
	 * Converte um Array de Objetos em uma String separada por um
	 * delimitador.
	 * @param lista	Array de Objetos a ser convertida.
	 * @param delim	Delimitador utilizado.
	 * @return		String separada pelo delimitador.
	 */
	public static String listToString(Object[] lista, String delim) {
		String retorno = new String();
		if (lista != null && lista.length > 0) {
			retorno = Arrays.toString(lista);
			retorno = retorno.replaceAll("[\\[\\]]", "");
			if (!delim.trim().equals(",")) {
				retorno = retorno.replaceAll(",", delim);
			}
		}
		return retorno;
	}
	
	/**
	 * Converte uma Lista de Objetos em uma String separada por vírgula.
	 * @param lista	Lista de Objetos a ser convertida.
	 * @return		String separada por vírgulas.
	 */
	public static String listToString(Collection<?> lista) {
		return listToString(lista.toArray(), ",");
	}
	
	/**
	 * Converte um Array de Objetos em uma String separada por vírgula.
	 * @param lista	Array de Objetos a ser convertida.
	 * @return		String separada por vírgulas.
	 */
	public static String listToString(Object[] lista) {
		return listToString(lista, ",");
	}
	
	/**
	 * Método responsável por formatar uma String a partir de um MaskFormatter
	 * pré-definido.
	 * 
	 * @param texto		O texto a ser formatado.
	 * @param mf		O Maskformatter utilizado na formatação.
	 * @return			O texto formatado ou o texto inalterado,
	 * 					caso a máscara seja incompatível ou inválida.
	 * @see MaskFormatter
	 */
	public static String formatString(String texto, MaskFormatter mf) {  
	    String result = null;
	    
		if (mf != null) {
			try {
				result = mf.valueToString(texto);
			} catch (ParseException e) {
				result = new String(texto);
			}
		} else {
			result = new String(texto);
		}
	    
	    return result;
	}
	
	/**
	 * Remove caracteres especiais e acentos de uma string.
	 * @param s 	String a ser manipulada.
	 * @return 		String sem caracateres especiais ou espaços.
	 * 
	 * @see StringUtil#SPECIAL_CHARACTERS
	 */
	public static String removeSpecialCharacters(String s) {
		String retorno = null;
		try {
			retorno = new String(s.getBytes("US-ASCII"));
			retorno = retorno.replaceAll(SPECIAL_CHARACTERS, "");
		} catch (UnsupportedEncodingException e) {
			retorno = new String(s);
		}
		return retorno;
	}
	
	/**
	 * Concatenar a representação String de dois ou mais objetos.
	 * @param o		Lista de Objetos a serem concatenados
	 * @return 		Valores concatenados.
	 */
	public static String concat(Object... o) {
		String concat = "";
		for (Object object : o) {
			concat += (object != null) ? object.toString() : "";
		}
		return concat;
	}
	
	/**
	 * Verifica se uma string é vazia ou null.
	 * @param s String a ser verificada.
	 * @return true se a string for null, vazia ou composta por espaços. false caso contrário.
	 */
	public static boolean isStringEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}
	
	/**
	 * Extrai os valores numéricos de uma string.
	 * @param valor String a ser analisada. 
	 * @return String apenas com os valores numéricos, ou null caso o parâmetro informado seja null.
	 */
	public static String extractNumbers(String valor) {
		return valor != null ? valor.trim().replaceAll("\\D", "") : null;
	}
	
	/**
	 * Remove os valores numéricos de uma string.
	 * @param valor String a ser analisada. 
	 * @return String sem valores numéricos.
	 */
	public static String removeNumbers(String valor) {
		return valor.replaceAll("\\d", "");
	}
	
	/**
	 * Capitaliza um texto
	 * @param texto	String a ser capitalizada
	 * @return String capitalizada
	 */
	public static String capitalize(String texto){
        String []palavras = texto.split("\\s+");
        StringBuilder textoFormatado = new StringBuilder();
        
        for(String palavra : palavras) {
            textoFormatado.append(palavra.substring(0,1).toUpperCase() + 
        	    					palavra.substring(1).toLowerCase() + " ");
        }
        
        return textoFormatado.toString();
    }
	
	/**
	 * Remove espaços duplos de uma String.
	 * @param texto	String a ser manipulada.
	 * @return String sem espaços duplos.
	 */
	public static String removeDoubleSpaces(String texto) {
		return (texto != null ? texto.replaceAll("\\s+", " ") : null);
	}
	
	/**
	 * Remove espaços de uma String.
	 * @param texto	String a ser manipulada.
	 * @return String sem espaços.
	 */
	public static String removeSpaces(String texto) {
		return (texto != null ? texto.replaceAll("\\s+", "") : null);
	}
	
	/**
	 * Verifica se uma String é formada pela repetição do mesmo caracter.
	 * Ex: aaaa, bbbbbbb, 1111111
	 * @param str	String a ser analizada.
	 * @return		Se a String é formada pela repetição do mesmo caracter. 
	 */
	public static boolean checkCharsEquals(String str) {
		boolean retorno = false;
		String pStr = removeSpaces(str);
		
		if (!isStringEmpty(pStr)) {
			Pattern p = Pattern.compile(pStr.charAt(0) + "{" + pStr.length() + "}");
			Matcher m = p.matcher(pStr);
			retorno = m.matches();
		}
		
		return retorno;
	}
	
	/**
	 * Recupera uma String formatada, substituindo os parâmetros correspondentes.
	 * @param mensagem Mensagem a ser formatada.
	 * @param parametros Parâmetros da mensagem.
	 * @return A String formatada com os parâmetros.
	 */
	public static String getFormattedMessage(String mensagem, Object...parametros) {
		StringBuffer b = new StringBuffer();
		if (mensagem != null && parametros != null && parametros.length != 0) {
			MessageFormat mf = new MessageFormat(mensagem, new Locale("pt", "BR"));
			b.append(mf.format(parametros));
		}
		return b.length() == 0 ? mensagem : b.toString();
	}
}