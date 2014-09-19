package br.jus.trt.lib.qbe.api.operator;


/**
 * Operador "size". Verifica se uma coleção contém um número de elementos maior que um determinado valor.
 * A propriedade deve representar uma coleção.
 * O valor informado deve ser um número inteiro.
 */
@SuppressWarnings("serial")
public class SizeGreaterThan extends OperatorBase<Integer> {

	/** Configura a operação para funcionar como "maior igual" */
	private boolean greaterEqual;

	/**
	 * Construtor default. Configura maiorIgual=false;
	 */
	public SizeGreaterThan() {
		this(false);
	}

	/**
	 * @param greaterEqual caso true, ativa a operação para trabalhar como "maior igual"
	 */
	public SizeGreaterThan(boolean greaterEqual) {
		this.greaterEqual = greaterEqual;
	}	
	
	public boolean isGreaterEqual() {
		return greaterEqual;
	}

	public void setGreaterEqual(boolean greaterEqual) {
		this.greaterEqual = greaterEqual;
	}

}
