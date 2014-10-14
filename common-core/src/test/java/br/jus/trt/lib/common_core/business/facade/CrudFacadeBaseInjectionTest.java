package br.jus.trt.lib.common_core.business.facade;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;

import br.jus.trt.lib.common_core.CoreTestBase;
import br.jus.trt.lib.common_core.integration.persistence.UFRepository;

/**
 * Testes de injeção de dependência.
 * @author Augusto
 */
public class CrudFacadeBaseInjectionTest extends CoreTestBase {
	
	@Inject
	private UFFacade ufFacade;
	
	@Test
	public void repositoryInjectionTest() {
		assertNotNull(ufFacade.getRepository());
	}
	
	@Test
	public void repositoryTypeInjectionTest() {
		assertTrue(UFRepository.class.isAssignableFrom(ufFacade.getRepository().getClass()));
	}
}
