package br.jus.trt.lib.common_core.util;

import java.io.Serializable;

/**
 * Classe de uso geral para representar um objeto com nome e valor 
 */
@SuppressWarnings("serial")
public class Parameter implements Serializable {

	private String name;
	private Object value;
	
	/**
	 * Construtor.
	 * @param nome Nome do parâmetro.
	 * @param valor Valor do parâmetro. De qualquer tipo
	 */
	public Parameter(String nome, Object valor) {
		super();
		this.name = nome;
		this.value = valor;
	}

	public String getName() {
		return name;
	}
	public void setName(String nome) {
		this.name = nome;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object valor) {
		this.value = valor;
	}

	@Override
	public String toString() {
		String param = "";
		if (name != null && value != null) {
			param = name + ": " + value;
		}
		return param;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Parameter other = (Parameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
