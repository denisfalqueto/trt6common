package br.jus.trt.lib.qbe.domain;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
@SuppressWarnings("serial")
public class Ferramenta  extends DomainBase<Long> { 

	@Column(name="DESCRICAO", length=50)
	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
