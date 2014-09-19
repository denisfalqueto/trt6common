package br.jus.trt.lib.qbe.api.exception;


@SuppressWarnings("serial")
public class CloneException extends QbeException {

	public CloneException() {
		super();
	}
	
	public CloneException(String msg, Throwable motivo) {
		super(msg, motivo);
	}
	
	public CloneException(Throwable motivo) {
		super("Falha ao realizar clone", motivo);
	}

	public CloneException(String msg) {
		super(msg);
	}

}
