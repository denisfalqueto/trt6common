package br.jus.trt.lib.common_core.util;

import org.apache.deltaspike.core.api.provider.BeanProvider;


/**
 * Classe utilitária com operações relacionadas ao conteiner de injeção de
 * dependências.
 * 
 * @author augusto
 * 
 */
public class DIContainerUtil {

	/**
	 * Realiza lookup de um Bean a partir do container de injeção de dependência ativo.
	 * @param clazz Tipo do bean para lookup.
	 * @return Bean instanciado e com suas dependências injetadas.
	 */
	public <T> T lookup(Class<T> clazz) {
		return BeanProvider.getContextualReference(clazz, false);
	}
	
}
