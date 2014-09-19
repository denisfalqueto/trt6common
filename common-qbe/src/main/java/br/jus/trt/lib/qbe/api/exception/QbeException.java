package br.jus.trt.lib.qbe.api.exception;


/**
 * Exceção para alidações do comportamento de Operadores. 
 * @author augusto
 */
@SuppressWarnings("serial")
public class QbeException extends RuntimeException {

	public QbeException() {
		super();
	}

	public QbeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public QbeException(String msg) {
		super(msg);
	}

	
}
