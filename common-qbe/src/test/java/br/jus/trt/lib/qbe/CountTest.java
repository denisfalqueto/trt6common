package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.text.ParseException;

import javax.persistence.Query;

import org.junit.Test;

import br.jus.trt.lib.qbe.api.Operation;
import br.jus.trt.lib.qbe.api.OperationContainer;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Pessoa;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class CountTest extends QbeTestBase {

	@Test
	public void contarSimples() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "select count(f.id) from " + UF.class.getSimpleName() + " f";
		Long UfsHQL = getQuerier().executeCountQuery(getEntityManager(), hql);
		
		// deve haver pelo menos uma Uf encontrada
		if (UfsHQL <= 0) {
			fail("Nao ha Ufs cadastradas, nao e possivel proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		Long UfsQbe = qbe.count(new QBEFilter<UF>(UF.class));
		
		// compara os resultados
		assertEquals("O numero de registros encontrados deveria ser igual.", UfsHQL, UfsQbe);
		
	}
	
	@Test
	public void contarComOrdenacao() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "select count(f.id) from " + UF.class.getSimpleName() + " f"; // hql n�o aceitar count + order by
		Long UfsHQL = getQuerier().executeCountQuery(getEntityManager(), hql);
		
		// deve haver pelo menos uma Uf encontrada
		if (UfsHQL <= 0) {
			fail("Nao ha Ufs cadastradas, nao e possivel proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());;
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.sortAscBy("sigla"); 
		
		Long UfsQbe = qbe.count(filtro); // espera-se que a ordenacao seja desconsiderada
		
		// compara os resultados
		assertEquals("O numero de registros encontrados deveria ser igual.", UfsHQL, UfsQbe);
		
	}	
	
	@Test
	public void contarComPaginacao() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "select count(f.id) from " + UF.class.getSimpleName() + " f";
		Query query = getQuerier().createQuery(getEntityManager(), hql); // hql nao permite paginacao com count
		Long UfsHQL = (Long) query.getSingleResult();
		
		// deve haver pelo menos uma Uf encontrada
		if (UfsHQL <= 0) {
			fail("Nao ha Ufs cadastradas, nao e possivel proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());;
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.paginate(5, 10); // espera-se que a pagina��o seja desconsiderada
		
		Long UfsQbe = qbe.count(filtro); 
		
		// compara os resultados
		assertEquals("O numero de registros encontrados deveria ser igual.", UfsHQL, UfsQbe);
		
	}		
	
	@Test
	public void contarComFiltros() {
		
		// executa a consulta com HQL correto equivalente
		String hql = "select count(f.id) from " + UF.class.getSimpleName() + " f";
		Query query = getQuerier().createQuery(getEntityManager(), hql); // hql n�o permite pagina��o com count
		Long UfsHQL = (Long) query.getSingleResult();
		
		// deve haver pelo menos uma Uf encontrada
		if (UfsHQL <= 0) {
			fail("Nao ha Ufs cadastradas, nao e possivel proceder com o teste");
		}
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());;
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.paginate(5, 10); // espera-se que a pagina��o seja desconsiderada
		
		Long UfsQbe = qbe.count(filtro); 
		
		// compara os resultados
		assertEquals("O n�mero de registros encontrados deveria ser igual.", UfsHQL, UfsQbe);
		
	}		
	
	@Test
	public void testContainerAninhadoOR_AND() throws ParseException {
		// Cria algumas pessoas pra consulta
		Pessoa p1 = new Pessoa("p1", null, formatDate("01/01/2010"), "11111111111", "email@email.com");
		Pessoa p2 = new Pessoa("p2", null, formatDate("01/01/2012"), "22222222222", "email@email.com");
		Pessoa p3 = new Pessoa("p3", null,                     null, "22222222222", "email@email.com");
		getJpa().save(p1);
		getJpa().save(p2);
		getJpa().save(p3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// utiliza uma consulta hql para compara��o
		String hql = "select count(p.id) from Pessoa p where p.email=? and ( p.cpf=? or (p.dataNascimento is null  or p.dataNascimento > ?) ) ";
		Long pessoasHQL = getQuerier().executeCountQuery(getEntityManager(), hql, p2.getEmail(), p1.getCpf(), p1.getDataNascimento());
		
		// Configura o filtro
		Pessoa exemplo = new Pessoa(null, null, p2.getDataNascimento(), p2.getCpf(), p2.getEmail());
		
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(exemplo);
		filtro.setStringDefaultOperator(Operators.equal());
		
		OperationContainer containerOR = filtro.addOr(new Operation("cpf", Operators.equal()));
		containerOR.addOr(new Operation("dataNascimento", Operators.isNotNull()),
				 	      new Operation("dataNascimento", Operators.greater()));
		
		// realiza a mesma consulta com qbe
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		Long pessoasQbe = qbe.count(filtro);
		
		// compara os resultados
		assertEquals("O numero de registros encontrados deveria ser igual.", pessoasHQL, pessoasQbe);
	}

}
