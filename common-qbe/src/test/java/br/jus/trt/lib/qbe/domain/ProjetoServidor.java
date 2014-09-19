package br.jus.trt.lib.qbe.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.jus.trt.lib.qbe.api.Identifiable;

@SuppressWarnings("serial")
@Entity
@Table(name="PROJETO_SERVIDOR")
public class ProjetoServidor  implements Identifiable { 

	@EmbeddedId
	private ProjetoServidorPK id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_PROJETO", insertable=false, updatable=false)
	private Projeto projeto;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_SERVIDOR", insertable=false, updatable=false)
	private Servidor servidor;
	
	// TODO adicionar uma atributo nesta relação: Papel (do servidor no projeto)

	public ProjetoServidor() {
		setId(new ProjetoServidorPK());
	}
	
	public ProjetoServidor(Projeto projeto, Servidor servidor) {
		this();
		setProjeto(projeto);
		setServidor(servidor);
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		getId().setIdProjeto(projeto == null ? null : projeto.getId());
		this.projeto = projeto;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		getId().setIdServidor(servidor == null ? null : servidor.getId());
		this.servidor = servidor;
	}
	
	public ProjetoServidorPK getId() {
		return id;
	}

	public void setId(ProjetoServidorPK id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		return id == null || obj == null ? false : id.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return id == null ? 31 : id.hashCode();
	}
	
}