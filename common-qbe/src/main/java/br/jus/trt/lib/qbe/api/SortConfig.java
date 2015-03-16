package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

/**
 * Classe que representa as formas de ordenação de uma consulta JPA
 * @author augusto
 */
@SuppressWarnings("serial")
public class SortConfig implements Serializable {

	public enum SortType {ASCENDING, DESCENDING}
	
	/**
	 * Método de fábrica para obter uma configuração para ordenação ascendente.
	 * @param propriedade Propriedade para ordenação.
	 * @return Configuração de ordenação ascendente.
	 */
	public static SortConfig ASC(String propriedade) {
		return new SortConfig(propriedade, SortType.ASCENDING);
	}
	
	/**
	 * Método de fábrica para obter uma configuração para ordenação descendente.
	 * @param propriedade Propriedade para ordenação.
	 * @return Configuração de ordenação descendente.
	 */
	public static SortConfig DESC(String propriedade) {
		return new SortConfig(propriedade, SortType.DESCENDING);
	}
	
	/** Propriedade de referência para ordenação */
	private String property;
	
	/** Determina que tipo de join será realizado para ordenação em propriedades de associações */
	private JoinType joinType;
	
	private SortType type;

	/**
	 * Default
	 */
	public SortConfig() {
	}

	/**
	 * Utiliza tipo {@link SortType#ASCENDING} como default.
	 * @param propriedade Propriedade para ordenação.
	 */
	public SortConfig(String propriedade) {
		this(propriedade, SortType.ASCENDING);
	}

	/**
	 * @param propriedade Propriedade para ordenação.
	 * @param tipo Tipo de ordenação.
	 */
	public SortConfig(String propriedade, SortType tipo) {
		this(propriedade, tipo, JoinType.LEFT);
	}
	
	/**
	 * @param propriedade Propriedade para ordenação.
	 * @param tipo Tipo de ordenação.
	 * @param joinType Tipo de join utilizado caso a propriedade pertença a uma associação
	 */
	public SortConfig(String propriedade, SortType tipo, JoinType joinType) {
		super();
		this.property = propriedade;
		this.joinType = joinType;
		this.type = tipo;
	}

	/**
	 * @return true se a ordenação está configurada como ascendente.
	 */
	public boolean isAscendant() {
		return SortType.ASCENDING.equals(this.type);
	}
	
	/**
	 * @return true se a ordenação está configurada como descendente.
	 */
	public boolean isDescendant() {
		return SortType.DESCENDING.equals(this.type);
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String propriedade) {
		this.property = propriedade;
	}

	public SortType getType() {
		return type;
	}

	public void setType(SortType tipo) {
		this.type = tipo;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
	
}
