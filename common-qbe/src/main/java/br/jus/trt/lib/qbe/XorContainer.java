package br.jus.trt.lib.qbe;

import br.jus.trt.lib.qbe.api.Operation;
import br.jus.trt.lib.qbe.api.OperationContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




/**
 * Container de operação que implementar um XOR.
 * @author augusto
 *
 */
@SuppressWarnings("serial")
public class XorContainer extends OperationContainer {

        private Logger log = LogManager.getLogger();
        
	/**
	 * Implementa uma variação do container aplicando uma operação XOR sobre
	 * as operações (não considerando subcontainers, por enquanto).
	 */
	@Override
	public OperationContainer transform() {
                log.entry();
		OperationContainer xor = new OperationContainer(ContainerType.OR);
		
		/*
		 * O Operador Lógico XOR, ou EXCLUSIVE OR, determina que apenas um dos operandos deve ser verdadeiro.
		 * Portanto, abaixo todos as operações são comparadas, de forma que em cada tupla, apenas uma delas é verdadeira.
		 * 
		 * Ex:
		 * (A and not (B or C)) OR (B and not (A or C)) OR (C and not (B or A))
		 */
		if (getOperations() != null && !getOperations().isEmpty()) {
                        log.debug("Possui operacoes");
			for (Operation operacao : getOperations()) {
                                log.trace("Operacao: " + operacao);
				OperationContainer and = xor.addAnd(operacao);
				
				OperationContainer or = and.addOr().negate();
				for (Operation outraOperacao : getOperations()) {
					if (!outraOperacao.equals(operacao)) {
						or.addOperation(outraOperacao);
					}
				}
			}
		}	
		
		return log.exit(xor);
	}
	
}
