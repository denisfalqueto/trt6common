package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "<";
 */
@SuppressWarnings("serial")
public class LessThan extends OperatorBase<Object> {

	/** Configura a operação para funcionar como "menor igual" */
	private boolean lessThen;
	
	/**
	 * Construtor default. Configura menorIgual=false;
	 */
	public LessThan() {
		this(false);
	}
	
	/**
	 * @param lessThan caso true, ativa a operação para trabalhar como "menor igual"
	 */
	public LessThan(boolean lessThan) {
		this.lessThen = lessThan;
	}	
	
	public boolean isLessThen() {
		return lessThen;
	}

	public void setLessThen(boolean lessThen) {
		this.lessThen = lessThen;
	}


}
