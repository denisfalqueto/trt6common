package br.jus.trt.lib.common_core.exception;

import javax.ejb.ApplicationException;


/**
 * Implementação abstrata das exceções.
 * @author Fabiano Lúcio de Souza Rolim
 */
@ApplicationException(rollback=true)
public class AppException extends RuntimeException {
		
	private static final long serialVersionUID = 8352236423259075706L;

	/**
	 * Construtor padrão.
	 */
	public AppException() {
		super();
	}

	/**
	 * Construtor.
	 * @param mensagem Mensagem explicativa do erro ocorrido, a ser exibida para o usuário ou logada em um arquivo, 
	 * caso necessário.
	 */
	public AppException(String mensagem) {
		super(mensagem);
	}
	
	/**
	 * Construtor.
	 * @param e Exceção original gerada pelo problema ocorrido. Este construtor permite o encapsulamento da exceção
	 * para ser, possivelmente, tratada por soluções genéricas de tratamento de exceções ou redirecionamento para 
	 * página de erro.
	 */
	public AppException(Throwable e) {
		super(e);
	}
	
	/**
	 * Construtor.
	 * @param e Exceção original gerada pelo problema ocorrido. Este construtor permite o encapsulamento da exceção
	 * para ser, possivelmente, tratada por soluções genéricas de tratamento de exceções ou redirecionamento para 
	 * página de erro.
	 * @param messagem Mensagem de erro.
	 */
	public AppException(String messagem, Throwable e) {
		super(messagem, e);
	}
	
}
