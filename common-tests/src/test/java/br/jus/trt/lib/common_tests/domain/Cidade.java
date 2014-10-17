package br.jus.trt.lib.common_tests.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
@SuppressWarnings("serial")
public class Cidade  extends DomainBase<Long> { 

	@Column(name="NOME", length=50, nullable=false)
	private String nome;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_UF", nullable=false)
	private UF uf;

	/**
	 * Default
	 */
	public Cidade() {	}

	public Cidade(String nome, UF uf) {
		this.nome = nome;
		this.uf = uf;
	}

	public Cidade(Long id, String nome, UF uf) {
		super();
		setId(id);
		this.nome = nome;
		this.uf = uf;
	}
	
	public Cidade(Long id, String nome) {
		super();
		setId(id);
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public UF getUf() {
		return uf;
	}

	public void setUf(UF uf) {
		this.uf = uf;
	}

}
