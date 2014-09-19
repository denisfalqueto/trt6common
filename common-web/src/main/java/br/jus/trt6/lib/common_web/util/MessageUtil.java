package br.jus.trt6.lib.common_web.util;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

@SuppressWarnings("serial")
public class MessageUtil implements Serializable {

	public void showMessage(Severity severity, String mensagem, Object...parametros) {
		FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(severity,  mensagem, null) );
	}

	/**
	 * Exibe mensagem de informação na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem.
	 */
	public void showInfoMessage(String mensagem, Object...parametros) {
		showMessage(FacesMessage.SEVERITY_INFO, mensagem, parametros);
	}

	/**
	 * Exibe mensagem de Advertencia na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem.
	 */
	public void showWarn(String mensagem, Object...parametros) {
		showMessage(FacesMessage.SEVERITY_WARN, mensagem, parametros);
	}	
	
	/**
	 * Exibe mensagem de erro na tela.
	 * @param mensagem Mensagem ou chave.
	 * @param parametros para montagem da mensagem 
	 */
	public void showError(String mensagem, Object...parametros) {
		showMessage(FacesMessage.SEVERITY_ERROR, mensagem, parametros);
	}
	
}
