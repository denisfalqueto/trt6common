package br.jus.trt.lib.common_tests.cdi;

import javax.enterprise.util.AnnotationLiteral;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.util.EmptyBean;
import br.jus.trt.lib.common_tests.util.EmptyBeanA;
import br.jus.trt.lib.common_tests.util.EmptyBeanB;
import br.jus.trt.lib.common_tests.util.QualifierB;

@RunWith(CdiJUnitRunner.class)
public class CDITest {

	/**
	 * Teste de lookup de bean sem qualifiers
	 */
	@Test
	public void simpleLookupTest() {
		EmptyBeanA emptyBeanA = CDI.lookup(EmptyBeanA.class);
		Assert.assertNotNull(emptyBeanA);
	}
	
	/**
	 * Teste de lookup de bean com qualifiers.
	 */
	@Test
	public void withQualifierLookupTest() {
		
		/*
		 * Busca um bean pela interface + um qualifier que especifica o tipo concreto.
		 */
		
		@SuppressWarnings("serial")
		EmptyBean emptyBean = CDI.lookup(EmptyBean.class,  new AnnotationLiteral<QualifierB>() {});
		Assert.assertNotNull(emptyBean);
		Assert.assertTrue(EmptyBeanB.class.equals(emptyBean.getClass()));;
	}
	
}
