package br.jus.trt.lib.common_tests.arquillian;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifica se o arquillian está devidamente configurado para se conectar ao servidor de 
 * aplicação.
 * @author augusto
 */
@RunWith(ArquillianCommonRunning.class)
public class ArquillianContainerConnectionTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
        	.addClass(ArquillianCommonRunning.class)	
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void ContainerUpAndRunnigTest() {
		/* 
		 * Este teste pode ser vazio, visto que apenas o fato de utilizar 
		 * @RunWith(Arquillian) já deverá executar a tentativa de conexão ao 
		 * container (servidor de aplicação).
		 * 
		 * O método createDeployment deverá provocar uma tentativa de deploy deste teste
		 * no servidor de aplicação.
		 */
	}

}
