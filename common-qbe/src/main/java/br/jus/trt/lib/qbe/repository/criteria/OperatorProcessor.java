package br.jus.trt.lib.qbe.repository.criteria;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;

import br.jus.trt.lib.qbe.api.Operator;

/**
 * Determina o comportamento esperado de um processador de operador.
 * Todo operador deve ser capaz de executar segundo a tecnologia de consulta na base de dados utilizada, 
 * esta interface permite desacoplar o processo de definição do processo de execução. Isto permite que 
 * um mesmo operador esteja disponível para uso em diferentes tecnologias ou estruturas de persistência 
 * de dados.
 * 
 * @author augusto
 *
 * @param <VALOR> Para definição do tipo de dados suportados como valores para este operador.
 */
public interface OperatorProcessor<VALOR> extends Serializable { 

	/**
	 * Realiza a validação e processamento da operação.
	 * @param criteria Criteria acesso, caso seja necessária uma configuração mais particular desta operação. Para operações
	 * simples, basta adicionar a restrição na junção do container recebida.
	 * @param containerJunction Juncao para todas as propriedades do container onde a propriedade foi configurada. Adicionando a restrição
	 * a esta junção, irá garantir que a propriedade utilizará o mesmo escopo e conector (AND, OR) utilizado pelas outras propriedades do mesmo container.
	 * @param property Propriedade que receberá a restrição
	 * @param operator Operador que está sendo processado.
	 * @param values Valor a ser aplicado sobre a restrição
	 */
	public void process(/*Criteria criteria, Junction containerJunction, */String property, Operator<VALOR> operator, VALOR...values);

}
