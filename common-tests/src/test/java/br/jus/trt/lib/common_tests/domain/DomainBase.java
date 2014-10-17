package br.jus.trt.lib.common_tests.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.proxy.HibernateProxyHelper;

/**
 * Definições básicas para as entidades de domínio criadas para testes.
 */
@SuppressWarnings({ "serial"})
@MappedSuperclass
public abstract class DomainBase<PK> implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private PK id;

	public PK getId() {
		return id;
	}

	public void setId(PK id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		Class objClass = HibernateProxyHelper.getClassWithoutInitializingProxy(obj);
		if (getClass() != objClass)
			return false;
		DomainBase other = (DomainBase) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
	
}