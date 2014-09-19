package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "size". Verifica se uma coleção contém um número de elementos menor que um determinado valor.
 * A propriedade deve representar uma coleção.
 * O valor informado deve ser um número inteiro.
 */
@SuppressWarnings("serial")
public class SizeLessThan extends OperatorBase<Integer> {

	/** Configura a operação para funcionar como "menor igual" */
	private boolean lessEqual;
	
	/**
	 * Construtor default. Configura menorIgual=false;
	 */
	public SizeLessThan() {
		this(false);
	}
	
	/**
	 * @param lessEqual caso true, ativa a operação para trabalhar como "menor igual"
	 */
	public SizeLessThan(boolean lessEqual) {
		this.lessEqual = lessEqual;
	}	
	
	public boolean isLessEqual() {
		return lessEqual;
	}

	public void setLessEqual(boolean lessEqual) {
		this.lessEqual = lessEqual;
	}

}
