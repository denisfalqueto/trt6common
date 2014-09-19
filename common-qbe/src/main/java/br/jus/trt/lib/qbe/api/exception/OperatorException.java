package br.jus.trt.lib.qbe.api.exception;


/**
 * Exceção para alidações do comportamento de Operadores. 
 * @author augusto
 */
@SuppressWarnings("serial")
public class OperatorException extends QbeException {

	public OperatorException() {
		super();
	}

	public OperatorException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public OperatorException(String msg) {
		super(msg);
	}

	
}
