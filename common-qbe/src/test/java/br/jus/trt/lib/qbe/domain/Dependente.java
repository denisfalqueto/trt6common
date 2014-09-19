package br.jus.trt.lib.qbe.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@SuppressWarnings("serial")
@Entity
@PrimaryKeyJoinColumn(name="ID")
public class Dependente extends Pessoa {

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ID_SERVIDOR", nullable=false)
	@NotNull
	private Servidor servidor;
	
	@Column(name="DT_INICIO")
	@Temporal(TemporalType.DATE)
	private Date dataInicio;

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

}
