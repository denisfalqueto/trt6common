package br.jus.trt.lib.common_core.exception;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.ApplicationException;

/**
 * Classe para definições de exceções de negócio e validações.
 * Exceções deste tipo são consideradas controladas pela aplicação. Realiza rollback da transação mas não 
 * compromete a conversação (não causa erro 500).
 * @author augusto
 *
 */
@ApplicationException(rollback=true)
@SuppressWarnings("serial")
public class BusinessException extends AppException {
	
	public interface KEY {
		/**
		 * Indica o valor padrão para o atributo showCode.
		 */
		public static final String SHOW_CODE = "showCodes";
	}

	private List<ExceptionMessage> messages = new ArrayList<ExceptionMessage>();
	
	/**
	 * Construtor.
	 * @param codigo Toda mensagem exceção pode ter um código associado. Geralmente, para identificação de uma mensagem específica.
	 * @param mensagem Mensagem a ser exibida. Permite parametrização.
	 * @param parametros Parâmetros da mensagem.
	 */
	public BusinessException(String codigo, String mensagem, Object ... parametros) {
		super(mensagem);
		messages.add(new ExceptionMessage(codigo, mensagem, parametros));
	}
	
	/**
	 * Construtor.
	 * @param codigo Toda mensagem exceção pode ter um código associado. Geralmente, para identificação de uma mensagem específica.
	 * @param mensagem Mensagem a ser exibida. Permite parametrização.
	 * @param showCode Define se o código da mensagem será exibido para o usuário.
	 * @param parametros Parâmetros da mensagem.
	 */
	public BusinessException(String codigo, String mensagem, Boolean showCode, Object ... parametros) {
		super(mensagem);
		messages.add(new ExceptionMessage(codigo, mensagem, showCode, parametros));
	}
	
	/**
	 * Construtor.
	 * @param mensagem Mensagem a ser exibida. Permite parametrização.
	 * @param parametros Parâmetros da mensagem.
	 * @param e Exceção original gerada pelo problema ocorrido. Este construtor permite o encapsulamento da exceção
	 * para ser, possivelmente, tratada por soluções genéricas de tratamento de exceções ou redirecionamento para 
	 * página de erro.
	 */
	public BusinessException(String codigo, String mensagem, Throwable e, Object ... parametros) {
		super(e);
		messages.add(new ExceptionMessage(codigo, mensagem, parametros));
	}
	
	/**
	 * Verifica se esta exceção contém pelo menos uma mensagem com o código informado.
	 * @param codigo Código para identificação da mensagem.
	 * @return true se o código for encontrado.
	 */
	public boolean hasCode(String codigo) {
		if (this.messages == null) {
			return false;
		}
		
		for (ExceptionMessage mensagem : this.messages) {
			if (codigo.equals(mensagem.getCode())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Define uma exceção com diversas mensagens associadas.
	 * @param mensagens		Lista de mensagens que serão associadas a exceção.
	 * @param allowRepeat	Se poderá ou não haver mensagens repetidas.
	 */
	public BusinessException(Collection<ExceptionMessage> mensagens, boolean allowRepeat) {
		super();
		if (allowRepeat) {
			createList(mensagens);
		} else {
			createSet(mensagens);
		}
	}
	
	/**
	 * Define uma exceção com diversas mensagens associadas. Mensagens repetidas
	 * serão ignoradas.
	 * @param mensagens		Lista de mensagens que serão associadas a exceção.
	 */
	public BusinessException(Collection<ExceptionMessage> mensagens) {
		super();
		createSet(mensagens);
	}
	
	private void createList(Collection<ExceptionMessage> mensagens) {
		this.messages.addAll(mensagens);
	}
	
	private void createSet(Collection<ExceptionMessage> mensagens) {
		Set<ExceptionMessage> set = new HashSet<ExceptionMessage>();
		set.addAll(mensagens);
		this.messages.addAll(set);
	}
	
	public List<ExceptionMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ExceptionMessage> mensagens) {
		this.messages = mensagens;
	}

}
