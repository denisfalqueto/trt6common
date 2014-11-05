package br.jus.trt6.lib.common_web.action.pagination;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt6.lib.common_web.action.ResultData;

/**
 * Define as operações mínimas necessárias para que o {@link LazyEntityDataModel} tenha acesso
 * à infraestrutura do portador utilizada na construção e execução das consultas realizadas.
 * @author augusto
 */
public interface LazyEntityProvider<ENTITY extends Entity<?>> {

	/**
	 * @return Tipo da entidade associada a este provider.
	 */
	public Class<ENTITY> getEntityType();
	
	/**
	 * Realiza a consulta de objetos. Naturalmente, esta consulta deve ser baseada nos mínimos
	 * configurados no {@link Filter} informado.
	 * 
	 * @param minimalFilter
	 *            Filtro com pré-configuração mínima inicial para consulta.
	 */
	public ResultData<ENTITY> search(Filter<ENTITY> minimalFilter);	
	
}
