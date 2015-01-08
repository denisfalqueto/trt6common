package br.jus.trt.lib.common_tests;

import org.junit.runner.RunWith;

import br.jus.trt.lib.common_tests.arquillian.ArquillianCommonRunner;

/**
 * Teste executado em um ambiente JEE real, com todos os recursos disponíveis.
 * Ideal para testes de integração, testes de comportamento, etc.
 * @author augusto
 *
 */
@RunWith(ArquillianCommonRunner.class)
public class InServerTestBase extends TestBase {

}
