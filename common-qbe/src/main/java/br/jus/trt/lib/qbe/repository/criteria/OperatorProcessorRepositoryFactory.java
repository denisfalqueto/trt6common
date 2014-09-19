package br.jus.trt.lib.qbe.repository.criteria;

import br.jus.trt.lib.qbe.OperatorProcessorRepositoryBase;
import br.jus.trt.lib.qbe.api.OperatorProcessorRepository;
import br.jus.trt.lib.qbe.api.operator.Between;
import br.jus.trt.lib.qbe.api.operator.Equal;
import br.jus.trt.lib.qbe.api.operator.GreaterThan;
import br.jus.trt.lib.qbe.api.operator.In;
import br.jus.trt.lib.qbe.api.operator.IsEmpty;
import br.jus.trt.lib.qbe.api.operator.IsNotEmpty;
import br.jus.trt.lib.qbe.api.operator.IsNotNull;
import br.jus.trt.lib.qbe.api.operator.IsNull;
import br.jus.trt.lib.qbe.api.operator.LessThan;
import br.jus.trt.lib.qbe.api.operator.Like;
import br.jus.trt.lib.qbe.api.operator.LikePrefix;
import br.jus.trt.lib.qbe.api.operator.LikeSufix;
import br.jus.trt.lib.qbe.api.operator.NotEqual;
import br.jus.trt.lib.qbe.api.operator.NotIn;
import br.jus.trt.lib.qbe.api.operator.SizeEqual;
import br.jus.trt.lib.qbe.api.operator.SizeGreaterThan;
import br.jus.trt.lib.qbe.api.operator.SizeLessThan;
import br.jus.trt.lib.qbe.api.operator.SizeNotEqual;
import br.jus.trt.lib.qbe.repository.criteria.operator.BetweenProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.EqualProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.GreaterThanProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.InProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.IsEmptyProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.IsNotEmptyProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.IsNotNullProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.IsNullProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.LessThanProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.LikePrefixProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.LikeProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.LikeSufixProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.NotEqualProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.NotInProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.SizeEqualProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.SizeGreaterThanProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.SizeLessThanProcessor;
import br.jus.trt.lib.qbe.repository.criteria.operator.SizeNotEqualProcessor;

/**
 * Para produção de DAOs concretos inseridos em um contexto de injeção de dependência. 
 * @author augusto
 */
public class OperatorProcessorRepositoryFactory {

	/**
	 * @return Um repositório com todos os processadores conhecidos pré-cadastrados.
	 * @throws Exception
	 */
	public static OperatorProcessorRepository create() {

		OperatorProcessorRepositoryBase operatorProcessorRepositoryBase = 
				new OperatorProcessorRepositoryBase();
		
		operatorProcessorRepositoryBase.register(Between.class, BetweenProcessor.class);
		operatorProcessorRepositoryBase.register(Equal.class, EqualProcessor.class);
		operatorProcessorRepositoryBase.register(GreaterThan.class, GreaterThanProcessor.class);
		operatorProcessorRepositoryBase.register(In.class, InProcessor.class);
		operatorProcessorRepositoryBase.register(IsEmpty.class, IsEmptyProcessor.class);
		operatorProcessorRepositoryBase.register(IsNotEmpty.class, IsNotEmptyProcessor.class);
		operatorProcessorRepositoryBase.register(IsNotNull.class, IsNotNullProcessor.class);
		operatorProcessorRepositoryBase.register(IsNull.class, IsNullProcessor.class);
		operatorProcessorRepositoryBase.register(LessThan.class, LessThanProcessor.class);
		operatorProcessorRepositoryBase.register(Like.class, LikeProcessor.class);
		operatorProcessorRepositoryBase.register(LikePrefix.class, LikePrefixProcessor.class);
		operatorProcessorRepositoryBase.register(LikeSufix.class, LikeSufixProcessor.class);
		operatorProcessorRepositoryBase.register(NotEqual.class, NotEqualProcessor.class);
		operatorProcessorRepositoryBase.register(NotIn.class, NotInProcessor.class);
		operatorProcessorRepositoryBase.register(SizeEqual.class, SizeEqualProcessor.class);
		operatorProcessorRepositoryBase.register(SizeGreaterThan.class, SizeGreaterThanProcessor.class);
		operatorProcessorRepositoryBase.register(SizeLessThan.class, SizeLessThanProcessor.class);
		operatorProcessorRepositoryBase.register(SizeNotEqual.class, SizeNotEqualProcessor.class);
		
		return operatorProcessorRepositoryBase;
		
	}
}
