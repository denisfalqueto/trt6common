package br.jus.trt.lib.common_core.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import br.jus.trt.lib.common_core.business.domain.EntityBase;

@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "sigla"))
public class UF extends EntityBase<Long> { 

	@NotNull
	@Size(min = 2, max = 2, message = "Deve ter exatamente 2 caracteres.")
	private String sigla;
	
	@OneToMany(mappedBy="uf", fetch=FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private List<Cidade> cidades;
	
	public UF() {
	}
	
	public UF(String sigla) {
		setSigla(sigla);
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public List<Cidade> getCidades() {
		return cidades;
	}

	public void setCidades(List<Cidade> cidades) {
		this.cidades = cidades;
	}

}