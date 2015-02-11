package br.jus.trt.lib.qbe.api;

import java.io.Serializable;

import br.jus.trt.lib.qbe.util.StringUtil;

import com.mysema.query.types.Path;


/**
 * Representa as possibilidades de configuração de carregamento das dependências de uma entidade durante 
 * uma consulta.
 * @author augusto
 */
@SuppressWarnings("serial")
public class FetchMode implements Serializable, Cloneable {

	/** Propriedade de referência para modo de carregamento */
	private String property;
	
	/** Para carregamento instantâneo, determina que tipo de join será realizado */
	private JoinType joinType;

	public FetchMode() {
		super();
	}

	/**
	 * Utiliza o tipo de join "LEFT" por padrão.
	 * @param propriedade Propriedade a ter sua forma de carregamento configurada.
	 */
	@SuppressWarnings("rawtypes")
	public FetchMode(Path propriedade) {
		this(propriedade, JoinType.LEFT);
	}

	@SuppressWarnings("rawtypes")
	public FetchMode(Path property, JoinType joinType) {
		super();
		this.property = StringUtil.getStringPath(property);
		this.joinType = joinType;
	}
	
	FetchMode(String property, JoinType joinType) {
		super();
		this.property = property;
		this.joinType = joinType;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String propriedade) {
		this.property = propriedade;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType tipoIntantaneo) {
		this.joinType = tipoIntantaneo;
	}	
	
	@Override
	public String toString() {
		return joinType + " " + property;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((joinType == null) ? 0 : joinType.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FetchMode other = (FetchMode) obj;
		if (joinType != other.joinType)
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}
	
	@Override
	public FetchMode clone() {
		FetchMode fetchModeClone = new FetchMode();
		fetchModeClone.setProperty(getProperty());
		fetchModeClone.setJoinType(getJoinType());
		return fetchModeClone;
	}
	
}
