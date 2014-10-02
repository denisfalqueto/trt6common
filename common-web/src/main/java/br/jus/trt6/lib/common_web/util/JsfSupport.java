package br.jus.trt6.lib.common_web.util;

import javax.inject.Named;

import br.jus.trt.lib.common_core.util.StringUtil;

/**
 * Oferece suporte à camada WEB para operações relacionadas ao JSF.
 * 
 * fonte: http://stackoverflow.com/questions/4602950/auto-generated-ids-for-facelets-components
 * @author augusto
 */
@Named(value="jsfSupport")
public class JsfSupport {

	/**
	 * Gera um ID para um componente JSF baseado no label informado. Este ID não garante unicidade (ainda).
	 * @return Um ID para componente JSF.
	 */
	public String generateId(String label) { 
//		TODO Pensar em uma forma de criar os IDs junto ao container do JSF, conforme o código abaixo. Porém,
//		deve-se realizar algum controle de cache, visto que este método é chamado várias vezes durante o ciclo
//		de vida do JSF.
//		String uniqueId = FacesContext.getCurrentInstance().getViewRoot().createUniqueId();
		
		return removeSpecialCharacters(label);
	}
	
	/**
	 * Remove caracteres especiais e acentos de uma string, além de substituir espaços por "_".
	 * @param s 	String a ser manipulada.
	 * @return 		String sem caracateres especiais ou espaços.
	 */
	private String removeSpecialCharacters(String s) {
		
		  String out = StringUtil.removeSpecialCharacters(s);
		  try {
		    out = new String(out.getBytes("US-ASCII"));
		    out = out.replaceAll(" ", "_");
		    
		    // Insere o caracter '_' no início do id se ele não iniciar com 
		    // uma letra ou por um '_' (sendo considerado inválido).
		    if (!out.isEmpty()) {
		        char c = out.charAt(0);
		        if ((!(Character.isLetter(c))) && (c != '_')) {
		            out = "_" + out;
		        }
		    }
		    
		  } catch (java.io.UnsupportedEncodingException e) {
			  e.printStackTrace();
		    return "";
		  }
		  return out;
	}
}