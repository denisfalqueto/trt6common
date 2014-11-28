package br.jus.trt6.lib.common_web.action;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;

import org.apache.logging.log4j.Logger;

import br.jus.trt6.lib.common_web.util.MessageUtil;

/**
 * Classe base para controle da camada de apresentação.
 * @author augusto
 *
 */
@SuppressWarnings("serial")
@ActionExceptionHandler
public class ActionBase implements Serializable {
	
    @Inject
    protected Logger log;
    
	@Inject
	private MessageUtil messageUtil; 
	
	/**
	 * Método invocado no momento  em que o componente é instanciado pelo próprio Seam.
	 */
	@PostConstruct
	public void init() {
            log.entry();
	}	
	
	/**
	 * Exibe mensagem de informação na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem.
	 */
	protected void showInfoMessage(String mensagem, Object...parametros) {
		messageUtil.showMessage(FacesMessage.SEVERITY_INFO, mensagem, parametros);
	}

	/**
	 * Exibe mensagem de Advertencia na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem.
	 */
	protected void showWarnMessage(String mensagem, Object...parametros) {
		messageUtil.showMessage(FacesMessage.SEVERITY_WARN, mensagem, parametros);
	}	
	
	/**
	 * Exibe mensagem de erro na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem 
	 */
	protected void showErrorMessage(String mensagem, Object...parametros) {
		messageUtil.showMessage(FacesMessage.SEVERITY_ERROR, mensagem, parametros);
	}
	
}
