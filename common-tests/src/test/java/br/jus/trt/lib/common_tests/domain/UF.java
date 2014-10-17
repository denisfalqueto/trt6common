package br.jus.trt.lib.common_tests.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "sigla"))
public class UF extends DomainBase<Long> { 

	@NotNull
	@Size(min = 2, max = 2, message = "Deve ter exatamente 2 caracteres.")
	private String sigla;
	
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

}