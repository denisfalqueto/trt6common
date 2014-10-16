package br.jus.trt.lib.common_tests.suite;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;

/**
 * Classe base para Suítes de testes. Configura um {@link Runner} do Junit que procura
 * as classes de testes automaticamente, a partir do package da classe Suíte em execução.
 * @see DynamicSuiteRunner 
 * @author augusto
 */
@RunWith(DynamicSuiteRunner.class)
public abstract class BaseSuite {

}
