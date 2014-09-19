package br.jus.trt.lib.qbe.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Pessoa  extends DomainBase<Long> { 

	@Column(nullable=false, length=100)
	private String nome;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_CIDADE", nullable=true)
	private Cidade cidade;	
	
	@Column(name="DT_NASCIMENTO", nullable=true)
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	@Column(name="CPF", length=11, nullable=true)
	private String cpf;
	
	@Column(name="EMAIL", length=30, nullable=true)
	private String email;

	
	public Pessoa() {
		super();
	}
	
	public Pessoa(String nome, Cidade cidade, Date dataNascimento,
			String cpf, String email) {
		this.nome = nome;
		this.cidade = cidade;
		this.dataNascimento = dataNascimento;
		this.cpf = cpf;
		this.email = email;
	}
	
	public Pessoa(Long id, String nome, Cidade cidade, Date dataNascimento,
			String cpf, String email) {
		super();
		setId(id);
		this.nome = nome;
		this.cidade = cidade;
		this.dataNascimento = dataNascimento;
		this.cpf = cpf;
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
