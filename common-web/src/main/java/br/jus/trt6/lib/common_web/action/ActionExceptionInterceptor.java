package br.jus.trt6.lib.common_web.action;

import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import br.jus.trt.lib.common_core.exception.AppException;
import br.jus.trt.lib.common_core.exception.BusinessException;
import br.jus.trt.lib.common_core.exception.ExceptionMessage;
import br.jus.trt.lib.common_core.util.StringUtil;
import br.jus.trt6.lib.common_web.util.MessageUtil;
import org.apache.logging.log4j.Logger;

/**
 * Responsável pela interceptação das exceções e realização de tratamentos genéricos. Qualquer exceção capturada
 * terá sua mensagem transformada em uma mensagem JSF, para ser exibida na interface com o usuário, caso a tag 
 * "message" esteja declarada no xhtml. Também é realizado um tratamento específico para casos de validação do hibernate,
 * buscando exibir mensagens apropriadas.
 * 
 * Este intercepator é ativado através da utilização da anotação "@ExceptionHandler" em componentes Seam
 * da camada de controle.
 */

@SuppressWarnings("serial")
@Interceptor
@ActionExceptionHandler
public class ActionExceptionInterceptor implements Serializable {

	@Inject
	private Logger log;

	@Inject
	private MessageUtil messageUtil;
	
	/* para identificar chamadas recorrentes dentro do mesmo objeto */
	private boolean recurrent;

	/**
	 * Método interceptador que envolve a chamada ao componente
	 */
	@AroundInvoke
	public Object aroundInvoke(InvocationContext invocation) throws Exception {

		if (recurrent) {
			return invocation.proceed();
		} else {
			recurrent = true;
			Object result = null;
			
			try {
				// executa o método interceptado
				result = invocation.proceed();
				return result;
				
			} catch (BusinessException ne) {
				// trata mensagens de validação de negócio
				checkBusinessException(ne);
				return result;
				
			} catch (AppException e) {
                                log.error(e);
				throw e;
			} catch (Throwable ne) {
				log.error("Erro não esperado pela aplicação.", ne); 
				throw new Exception(ne);
				
			} finally {
				recurrent = false;
			}
		}
	}
	
	/**
	 * Indica ao Seam se o intercepator está abilitado.
	 * Veja mais em {@link #isInterceptorEnabled()} 
	 */
	public boolean isInterceptorEnabled() {
		return true;
	}

	/**
	 * Cria mensagem do tipo ERROR a ser exibida para o usuário
	 */
	protected void checkBusinessException(BusinessException e) {

		for (ExceptionMessage em : e.getMessages()) {
			if (!StringUtil.isStringEmpty(em.getMessage())) {
				String mensagem = em.getShowCode() ? em.getCode()  + " - " + em.getMessage() : em.getMessage(); 
				messageUtil.showError(mensagem, em.getParameters());
				log.debug("Adicionando mensagem para o usuário da validação de negócio : " + mensagem);
			}
		}
	}

}