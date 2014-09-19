package br.jus.trt.lib.qbe.api.operator;



/**
 * Operador "like": [valor]%.
 */
@SuppressWarnings("serial")
public class LikePrefix extends Like {

	public LikePrefix() {
		super();
	}

	/**
	 * @see Like#Like(boolean)
	 */
	public LikePrefix(boolean caseSensitive) {
		super(caseSensitive);
	}

}
