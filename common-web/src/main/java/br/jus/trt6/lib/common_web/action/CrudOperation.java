package br.jus.trt6.lib.common_web.action;

/** Constantes para controle da operação corrente */
public enum CrudOperation {
	SEARCH("S"), EDIT("E");
	
	private String operation;
	
	CrudOperation(String operation) {
		this.operation = operation;
	}
	
	/**
	 * Verifica se a operação informada (String) é equivalente a este enum (this).
	 * @param anOperation Representação em String de uma operação para comparação.
	 * @return true se esta representação em String for equivalente ao nome ou à operação deste enum (this)
	 */
	public boolean equals(String anOperation) {
		return this.operation.equalsIgnoreCase(anOperation)
				|| this.name().equalsIgnoreCase(anOperation);
	}
}