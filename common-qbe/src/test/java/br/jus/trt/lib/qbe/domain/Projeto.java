package br.jus.trt.lib.qbe.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;


@SuppressWarnings("serial")
@Entity
public class Projeto  extends DomainBase<Long> { 

	@Column(name="NOME", length=100, nullable=false)
	@NotNull
	@Length(min=5, max=100)
	private String nome;
	
	@Column(name="SIGLA", length=10, nullable=false)
	@NotNull
	@Length(min=3, max=10)
	private String sigla;	
	
	@Column(name="CONCLUIDO")
	@Type(type="yes_no")
	private Boolean concluido;

	@OneToMany(mappedBy="projeto", fetch=FetchType.LAZY, orphanRemoval=true, cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE})
	private List<ProjetoServidor> servidores;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="PROJETO_FERRAMENTA",
			joinColumns=@JoinColumn(name="ID_PROJETO"),
			inverseJoinColumns=@JoinColumn(name="ID_FERRAMENTA"))
	private List<Ferramenta> ferramentas;
	
	public Projeto() {
		super();
	}
	
	public Projeto(Long id, String nome) {
		super();
		setId(id);
		this.nome = nome;
	}

	public Projeto(String nome, String sigla, Boolean concluido) {
		super();
		this.nome = nome;
		this.sigla = sigla;
		this.concluido = concluido;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Boolean getConcluido() {
		return concluido;
	}

	public void setConcluido(Boolean concluido) {
		this.concluido = concluido;
	}

	public List<ProjetoServidor> getServidores() {
		return servidores;
	}

	public void setServidores(List<ProjetoServidor> servidores) {
		this.servidores = servidores;
	}

	public List<Ferramenta> getFerramentas() {
		return ferramentas;
	}

	public void setFerramentas(List<Ferramenta> ferramentas) {
		this.ferramentas = ferramentas;
	}

}
