package br.jus.trt.lib.common_core.integration.persistence;

import org.apache.deltaspike.data.api.Repository;

import br.jus.trt.lib.common_core.domain.UF;

/**
 * Classe para testes.
 * Repositório para a entidade UF.
 * @author Augusto
 */
@Repository(forEntity = UF.class)
public abstract class UFRepository extends CrudRepositoryBase<UF, Long> {
 
}
