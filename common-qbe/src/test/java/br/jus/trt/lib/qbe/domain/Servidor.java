package br.jus.trt.lib.qbe.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name="ID")
public class Servidor extends Pessoa {

	public enum SITUACAO {
		ATIVO ("A", "Ativo"), INATIVO("I", "Inativo");
		
		private String id;
		private String descricao;
		
		private SITUACAO(String id, String descricao) {
			this.id = id;
			this.descricao = descricao;
		}

		public String getId() {
			return id;
		}
		
		public String getDescricao() {
			return descricao;
		}
	}

	public Servidor() {
	}
	
	public Servidor(Long id, String nome) {
		setId(id);
		setNome(nome);
	}

	@Column(name="MATRICULA", length=10, nullable=false)
	private String matricula;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="servidor", orphanRemoval=true, cascade=CascadeType.ALL)
	private List<ProjetoServidor> projetos;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="servidor", orphanRemoval=true, cascade=CascadeType.ALL)
	private List<Dependente> dependentes;
	
	@Enumerated(EnumType.STRING)
	private SITUACAO situacao;
	
	public void removerProjeto(ProjetoServidor projeto) {
		if (projetos != null) {
			projetos.remove(projeto);
		}
	}
	
	public void removerDependente(Dependente dependente) {
		if (dependentes != null) {
			dependentes.remove(dependente);
		}
	}
	
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public List<ProjetoServidor> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<ProjetoServidor> projetos) {
		this.projetos = projetos;
	}

	public SITUACAO getSituacao() {
		return situacao;
	}

	public void setSituacao(SITUACAO situacao) {
		this.situacao = situacao;
	}

	public List<Dependente> getDependentes() {
		return dependentes;
	}

	public void setDependentes(List<Dependente> dependentes) {
		this.dependentes = dependentes;
	}
}
