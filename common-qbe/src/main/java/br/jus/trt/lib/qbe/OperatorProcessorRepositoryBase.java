package br.jus.trt.lib.qbe;

import java.util.HashMap;
import java.util.Map;

import br.jus.trt.lib.qbe.api.Operator;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessor;

/**
 * Implementação concreta de um DAO JPA, criada para minimizar a necessidade de criação
 * de DAOs concretos para cada entidade de domínio.
 * @author augusto
 *
 */
@SuppressWarnings("serial")
public class OperatorProcessorRepositoryBase implements OperatorProcessorRepository { 

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Operator>, Class<? extends OperatorProcessor>> processors;
	
	@SuppressWarnings("rawtypes")
	public OperatorProcessorRepositoryBase() {
		processors = new HashMap<Class<? extends Operator>, Class<? extends OperatorProcessor>>();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void register(Class<? extends Operator> operatorType, Class<? extends OperatorProcessor> processorType) {
		processors.put(operatorType, processorType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends OperatorProcessor> getProcessor(Class<? extends Operator> operatorType) {
		return processors.get(operatorType);
	}

}
