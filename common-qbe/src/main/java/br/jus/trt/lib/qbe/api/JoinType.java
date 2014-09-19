package br.jus.trt.lib.qbe.api;


/** Para fetches que utilizam join, detemrina que tipo de join deve ser realizado
 * @author augusto
 */
public enum JoinType {

	/** Para o caso de associação com coleções, por exemplo, A 0-* B, retorna todos os objetos do tipo A, mesmo quando não possuem nenhum
	 * B associado.*/
	LEFT,
	/** Para o caso de associação com coleções, por exemplo, A 0-* B, retorna apenas os objetos do tipo A que possuem algum
	 * B associado.*/		
	INNER;
	
}
