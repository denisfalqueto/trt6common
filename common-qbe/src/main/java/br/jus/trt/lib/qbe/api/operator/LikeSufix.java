package br.jus.trt.lib.qbe.api.operator;



/**
 * Operador "like": %[valor].
 */
@SuppressWarnings("serial")
public class LikeSufix extends Like {

	public LikeSufix() {
		super();
	}

	/**
	 * @see Like#Like(boolean)
	 */
	public LikeSufix(boolean caseSensitive) {
		super(caseSensitive);
	}

}
