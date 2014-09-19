package br.jus.trt.lib.common_core.integration.persistence;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;

import br.jus.trt.lib.common_core.business.bobject.BObjectBase;
import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;

/**
 * Para produção de DAOs concretos inseridos em um contexto de injeção de dependência. 
 * @author augusto
 */
public class DaoFactory {

	@Produces
	public <ENTITY extends Entity<?>> Dao<ENTITY> create(InjectionPoint injectionPoint, final EntityManager entityManager) throws Exception {

		try {
			Bean<?> ownerBean = injectionPoint.getBean();
			List<Class<?>> typeArguments = JavaGenericsUtil.getGenericTypedArguments(BObjectBase.class, ownerBean.getBeanClass());
			
			@SuppressWarnings("unchecked")
			Class<ENTITY> entityType = (Class<ENTITY>) typeArguments.get(0);
			
			return new DefaultJpaDao<ENTITY>(entityManager, entityType);
		} catch (Exception e) {
			// se houver problemas, descarta a tentatica de descobrir generics automaticamente
			throw new Exception("Não foi possível determinar o tipo genérico da entidade associada ao DAO a ser criado", e);				
		}
		
	}
}
