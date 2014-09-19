package br.jus.trt.lib.qbe.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class ProjetoServidorPK implements Serializable {

	@Column(name="ID_PROJETO")
	private Long idProjeto;
	
	@Column(name="ID_SERVIDOR")
	private Long idServidor;

	public Long getIdProjeto() {
		return idProjeto;
	}

	public void setIdProjeto(Long idProjeto) {
		this.idProjeto = idProjeto;
	}

	public Long getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(Long idservidor) {
		this.idServidor = idservidor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idProjeto == null) ? 0 : idProjeto.hashCode());
		result = prime * result
				+ ((idServidor == null) ? 0 : idServidor.hashCode());
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
		ProjetoServidorPK other = (ProjetoServidorPK) obj;
		if (idProjeto == null) {
			if (other.idProjeto != null)
				return false;
		} else if (!idProjeto.equals(other.idProjeto))
			return false;
		if (idServidor == null) {
			if (other.idServidor != null)
				return false;
		} else if (!idServidor.equals(other.idServidor))
			return false;
		return true;
	}
}
