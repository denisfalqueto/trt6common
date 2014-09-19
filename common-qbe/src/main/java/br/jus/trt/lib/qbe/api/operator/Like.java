package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "like": %[valor]%.
 */
@SuppressWarnings("serial")
public class Like extends OperatorBase<String> {

	private boolean caseSensitive;
	
	/**
	 * Construtor default. Configura caseSensitive=false;
	 */
	public Like() {
		this(false);
	}
	
	/**
	 * @param caseSensitive true caso o like deva considerar caseSensitive na operação. false caso contrário.
	 */
	public Like(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}
