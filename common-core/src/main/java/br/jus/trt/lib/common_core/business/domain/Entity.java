package br.jus.trt.lib.common_core.business.domain;

import java.io.Serializable;

import br.jus.trt.lib.qbe.api.Identifiable;

/**
 * Interface para as entidades de domínio. 
 * @author augusto
 */
public interface Entity <PK> extends Serializable, Identifiable {

	public PK getId();
		
}
