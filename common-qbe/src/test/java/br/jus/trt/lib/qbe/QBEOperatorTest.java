package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.Pessoa;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;


/**
 * Classe de teste que avalia as op��es de operadores das consultas QBE 
 * @author augusto
 */
public class QBEOperatorTest extends QbeTestBase {

	/**
	 * Testa consulta utilizando uma propriedade simples como filtro
	 */
	@Test
	public void consultarEntidadeOperadorDefault() {
		// busca a primeira cidade na base de dados
		Cidade cidadeBD = getQuerier().findAny(Cidade.class);
		assertNotNull("Não foi encontrada nenhuma cidade para prosseguir com o teste", cidadeBD);
		
		// cria um exemplo com par�metros compat�veis com a cidade encontrada
		Cidade exemplo = new Cidade();
		exemplo.setNome(cidadeBD.getNome());
		
		// executa uma consulta com HQL filtrando pelo nome do exemplo
		String hql = "from " + Cidade.class.getSimpleName() + " cid where cid.nome = ?";
		List<Cidade> cidadesHQL = getQuerier().searchAndValidateNotEmpty(hql, exemplo.getNome());		
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas
		assertContentEqual(cidadesHQL, cidadesQbe);		
	}
	
	@Test
	public void consultarOperadorEqual() {
		
		// busca a primeira cidade na base de dados
		Cidade cidadeBD = getQuerier().findAny(Cidade.class);
		assertNotNull("Não foi encontrada nenhuma cidade para prosseguir com o teste", cidadeBD);
		
		// cria um exemplo com par�metros compat�veis com a cidade encontrada
		Cidade exemplo = new Cidade();
		exemplo.setNome(cidadeBD.getNome());
		
		// executa uma consulta com HQL filtrando pelo nome do exemplo
		String hql = "from " + Cidade.class.getSimpleName() + " cid where cid.nome = ?";
		List<Cidade> cidadesHQL = getQuerier().searchAndValidateNotEmpty(hql, exemplo.getNome());		
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("nome", Operators.equal());
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas
		assertContentEqual(cidadesHQL, cidadesQbe);
		
	}
	
	@Test
	public void consultarOperadorNotEqual() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria um exemplo com par�metros compat�veis com a cidade encontrada
		UF exemplo = new UF();
		exemplo.setSigla(ufBD.getSigla());
		
		// executa uma consulta com HQL filtrando pelo nome do exemplo
		String hql = "from " + UF.class.getSimpleName() + " uf where uf.sigla != ?";
		List<UF> ufHQL = getQuerier().searchAndValidateNotEmpty(hql, exemplo.getSigla());		
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<UF> filtro = new QBEFilter<UF>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("sigla", Operators.notEqual());
		
		List<UF> ufsQbe = qbe.search(filtro);
		
		// compara as listas e verifica o resultado
		assertContentEqual(ufHQL, ufsQbe);
		assertFalse("A UF não deveria estar na lista. " + ufBD.getSigla(), ufsQbe.contains(ufBD));
		
	}
	
	@Test
	public void consultarOperadorMaior() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria algumas cidades para teste
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("cidade1", ufBD);
		Cidade cid2 = new Cidade("cidade2", ufBD);
		Cidade cid3 = new Cidade("cidade3", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		
		// cria um exemplo para filtro
		Cidade exemplo = new Cidade();
		exemplo.setNome(cid2.getNome());
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("nome", Operators.greater());
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("Numero de cidades retornada não confere.",1, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.contains(cid3));
		
	}
	
	@Test
	public void consultarOperadorMaiorIgual() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria algumas cidades para teste
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("cidade1", ufBD);
		Cidade cid2 = new Cidade("cidade2", ufBD);
		Cidade cid3 = new Cidade("cidade3", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		
		// cria um exemplo para filtro com o nome da cidade 2
		Cidade exemplo = new Cidade();
		exemplo.setNome(cid2.getNome());
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("nome", Operators.greateEqual());
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("Número de cidades retornada não confere.",2, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid2, cid3)));
		
	}
	
	
	@Test
	public void consultarOperadorMenor() {
		
		// cria uma nova UF para teste
		UF ufBD = new UF("ZZ");
		getJpa().save(ufBD);
		
		// cria algumas cidades para teste
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("cidade1", ufBD);
		Cidade cid2 = new Cidade("cidade2", ufBD);
		Cidade cid3 = new Cidade("cidade3", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		
		// cria um exemplo para filtro baseado na cidade 2
		Cidade exemplo = new Cidade();
		exemplo.setNome(cid2.getNome());
		exemplo.setUf(ufBD);
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("nome", Operators.less());
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",1, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.contains(cid1));
		
	}
	
	@Test
	public void consultarOperadorMenorIgual() {
		
		// cria uma nova UF para teste
		UF ufBD = new UF("ZZ");
		getJpa().save(ufBD);
		
		// cria algumas cidades para teste
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("cidade1", ufBD);
		Cidade cid2 = new Cidade("cidade2", ufBD);
		Cidade cid3 = new Cidade("cidade3", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		
		// cria um exemplo para filtro com o nome da cidade 2
		Cidade exemplo = new Cidade();
		exemplo.setNome(cid2.getNome());
		exemplo.setUf(ufBD);
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("nome", Operators.lessEqual());
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",2, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid2)));
		
	}
	
	@Test
	public void consultarOperadorLike() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("Cidade QWER teste", ufBD);
		Cidade cid2 = new Cidade("Cidade QweR teste", ufBD);
		Cidade cid3 = new Cidade("Cidade QWER teste", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// cria um exemplo com parte do nome comum entre algumas cidades
		Cidade exemplo = new Cidade();
		exemplo.setNome("QWER");
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador like para a propriedade de filtro, com case sensitive
		filtro.filterBy("nome", Operators.like(true));
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",2, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid3)));
		
		// configura o operador para ser case INsensitive e espera valores diferentes
		filtro = new QBEFilter<Cidade>(exemplo);
		filtro.filterBy("nome", Operators.like(false));
		
		cidadesQbe = qbe.search(filtro);
		
		// deve encontrar todas as cidades cadastradas
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",3, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid2, cid3)));
	}
	
	@Test
	public void consultarOperadorLikeInicio() {
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("QWER Cidade teste", ufBD);
		Cidade cid2 = new Cidade("QweR Cidade teste", ufBD);
		Cidade cid3 = new Cidade("QWER Cidade teste", ufBD);
		Cidade cid4 = new Cidade("Cidade QWER teste", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().save(cid4);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// cria um exemplo com parte do nome comum entre algumas cidades
		Cidade exemplo = new Cidade();
		exemplo.setNome("QWER");
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador like para a propriedade de filtro, com case sensitive
		filtro.filterBy("nome", Operators.likePrefix(true));
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",2, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid3)));
		
		// configura o operador para ser case INsensitive e espera valores diferentes
		filtro = new QBEFilter<Cidade>(exemplo);
		filtro.filterBy("nome", Operators.likePrefix(false));
		
		cidadesQbe = qbe.search(filtro);
		
		// deve encontrar todas as cidades cadastradas
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",3, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid2, cid3)));
	}
	
	@Test
	public void consultarOperadorLikeFinal() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria cidades com parte do nome comum para busca
		Cidade cid1 = new Cidade("Cidade teste QWER", ufBD);
		Cidade cid2 = new Cidade("Cidade teste QweR", ufBD);
		Cidade cid3 = new Cidade("Cidade teste QWER", ufBD);
		Cidade cid4 = new Cidade("Cidade QWER teste", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().save(cid4);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// cria um exemplo com parte do nome comum entre algumas cidades
		Cidade exemplo = new Cidade();
		exemplo.setNome("QWER");
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		
		// configura o operador like para a propriedade de filtro, com case sensitive
		filtro.filterBy("nome", Operators.likeSufix(true));
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// verifica o resultado
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",2, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid3)));
		
		// configura o operador para ser case INsensitive e espera valores diferentes
		filtro = new QBEFilter<Cidade>(exemplo);
		filtro.filterBy("nome", Operators.likeSufix(false));
		
		cidadesQbe = qbe.search(filtro);
		
		// deve encontrar todas as cidades cadastradas
		assertNotNull("Deveria ter retornado uma lista de cidades", cidadesQbe);
		assertEquals("N�mero de cidades retornada não confere.",3, cidadesQbe.size());
		assertTrue("As cidades esperadas não fazem parte da lista.",cidadesQbe.containsAll(Arrays.asList(cid1, cid2, cid3)));
	}
	
	@Test
	public void consultarOperadorIn() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria algumas cidades
		Cidade cid1 = new Cidade("cid1", ufBD);
		Cidade cid2 = new Cidade("cid2", ufBD);
		Cidade cid3 = new Cidade("cid3", ufBD);
		Cidade cid4 = new Cidade("cid4", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().save(cid4);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// cria uma cole��o com os ids das cidades selecionadas
		List<Long> idCidades = Arrays.asList(cid1.getId(), cid2.getId(), cid3.getId(), cid4.getId());
		
		
		// executa a consulta utilizando QBE para recuperar as cidades criadas
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(Cidade.class);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("id", Operators.in(), idCidades);
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", cidadesQbe.isEmpty());
		assertEquals("A quantide de cidades encontradas não confere.", idCidades.size(), cidadesQbe.size());
		assertTrue("A consulta não cont�m as cidades esperadas", 
				cidadesQbe.containsAll(Arrays.asList(cid1, cid2, cid3, cid4)));
		
	}
	
	@Test
	public void consultarOperadorNotIn() {
		
		// busca a primeira UF na base de dados
		UF ufBD = getQuerier().findAny(UF.class);
		assertNotNull("Não foi encontrada nenhuma UF para prosseguir com o teste", ufBD);
		
		// cria algumas cidades
		Cidade cid1 = new Cidade("cid1", ufBD);
		Cidade cid2 = new Cidade("cid2", ufBD);
		Cidade cid3 = new Cidade("cid3", ufBD);
		Cidade cid4 = new Cidade("cid4", ufBD);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().save(cid4);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// cria uma coleção com os ids das cidades selecionadas
		List<Long> idCidades = Arrays.asList(cid1.getId(), cid2.getId());		
		
		// executa a consulta utilizando QBE para recuperar as cidades criadas
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(Cidade.class);
		
		// configura o operador para a propriedade de filtro
		filtro.filterBy("id", Operators.notIn(), idCidades);
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", cidadesQbe.isEmpty());
		
		assertTrue("A consulta não contêm as cidades esperadas", 
				cidadesQbe.containsAll(Arrays.asList(cid3, cid4)));
		
		assertFalse("A consulta contêm as cidades que não eram esperadas", 
				cidadesQbe.containsAll(Arrays.asList(cid1, cid2)));
	}
	
	@Test
	public void consultarOperadorBetween() throws Exception {
		
		// busca a primeira UF na base de dados
		Cidade cidadeBD = getQuerier().findAny(Cidade.class);
		assertNotNull("Não foi encontrada nenhuma Cidade para prosseguir com o teste", cidadeBD);
		
		// cria algumas pessoas
		Pessoa p1 = new Pessoa("Pessoa 1", cidadeBD, formatDate("01/01/2010"), "11111111111", "p1@p.com");
		Pessoa p2 = new Pessoa("Pessoa 2", cidadeBD, formatDate("01/06/2010"), "22222222222", "p2@p.com");
		Pessoa p3 = new Pessoa("Pessoa 3", cidadeBD, formatDate("01/01/2011"), "33333333333", "p3@p.com");
		Pessoa p4 = new Pessoa("Pessoa 4", cidadeBD, formatDate("01/06/2011"), "44444444444", "p4@p.com");
		Pessoa p5 = new Pessoa("Pessoa 5", cidadeBD, formatDate("01/01/2012"), "55555555555", "p5@p.com");
		
		getJpa().save(p1);
		getJpa().save(p2);
		getJpa().save(p3);
		getJpa().save(p4);
		getJpa().save(p5);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		
		// executa a consulta utilizando QBE para recuperar as pessoas baseado na data de nascimento
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		// configura o operador para a propriedade de filtro com DATE
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.filterBy("dataNascimento", Operators.between(), formatDate("02/01/2010"), formatDate("31/12/2011"));
		
		List<Pessoa> pessoas = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", pessoas.isEmpty());
		assertEquals("A quantide de pessoas encontradas não confere.", pessoas.size(), 3);
		assertTrue("A consulta não cont�m as pessoas esperadas", 
				pessoas.containsAll(Arrays.asList(p2, p3, p4)));		
		
		// configura o operador para a propriedade de filtro com NUMBER
		filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.filterBy("id", Operators.between(), p2.getId(), p4.getId());
		
		pessoas = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", pessoas.isEmpty());
		assertEquals("A quantide de pessoas encontradas não confere.", pessoas.size(), 3);
		assertTrue("A consulta não cont�m as pessoas esperadas", 
				pessoas.containsAll(Arrays.asList(p2, p3, p4)));		
		
	}
	
	@Test
	public void consultarOperadorIsNull() throws Exception {
		
		criarPessoasTestIsNullIsNotNull();
		
		// executa a consulta utilizando QBE para recuperar projetos sem situa��o de conclus�o
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.filterBy("email", Operators.isNull());
		
		List<Pessoa> pessoas = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", pessoas.isEmpty());
		
		// varre a lista verificando se todos os projeto estáo sem a propriedade
		// "concluido" configurada
		for (Pessoa pessoa : pessoas) {
			assertNull("O projeto não deveria ter a propriedade 'email' preenchida", pessoa.getEmail());
		}
		
	}
	
	@Test
	public void consultarOperadorIsNotNull() throws Exception {
		
		// cria algumas pessoas
		criarPessoasTestIsNullIsNotNull();
		
		// executa a consulta utilizando QBE para recuperar projetos sem situa��o de conclus�o
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.filterBy("email", Operators.isNotNull());
		
		List<Pessoa> pessoas = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", pessoas.isEmpty());
		
		// varre a lista verificando se todos os projeto estáo sem a propriedade
		// "concluido" configurada
		for (Pessoa pessoa : pessoas) {
			assertNotNull("A pessoa não deveria ter a propriedade 'email' preenchida: " + pessoa, pessoa.getEmail());
		}
		
	}
	
	@Test
	public void consultarOperadorIsEmpty() throws Exception {
		criarUFsIsEmptyIsNotEmpty();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.isEmpty());
		
		List<UF> ufs = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", ufs.isEmpty());
		
		// varre a lista verificando se todas as UFs encontradas não possuem cidades
		for (UF uf : ufs) {
			assertTrue("A UF não deveria ter cidades associadas: " + uf, uf.getCidades().isEmpty());
		}		
		
	}
	
	@Test
	public void consultarOperadorSizeIgual() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeEqual(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// compara as listas e verifica o resultado
		assertFalse("A lista não deveria estar vazia", ufs.isEmpty());
		
		// garante que a uf criada está na lista
		assertTrue("A UF criada deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas possuem apenas 3 cidades
		for (UF uf : ufs) {
			assertTrue("A UF deveria ter apenas 3 cidades associadas: " + uf, uf.getCidades().size() == 3);
		}		
	}
	
	@Test
	public void consultarOperadorSizeDiferente() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeNotEqual(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// garante que a uf criada não está na lista
		assertFalse("A UF criada não deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas não possuem apenas 3 cidades
		for (UF uf : ufs) {
			assertTrue("A UF não deveria ter apenas 3 cidades associadas: " + uf, uf.getCidades().size() != 3);
		}		
	}
	
	@Test
	public void consultarOperadorSizeMaior() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeGreater(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// garante que a uf criada não está na lista
		assertFalse("A UF criada não deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas possuem mais que 3 cidades
		for (UF uf : ufs) {
			assertTrue("A UF deveria ter mais que 3 cidades associadas: " + uf, uf.getCidades().size() > 3);
		}		
	}
	
	@Test
	public void consultarOperadorSizeMaiorIgual() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeGreaterEqual(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// garante que a uf criada não está na lista
		assertTrue("A UF criada deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas possuem 3 ou mais cidades
		for (UF uf : ufs) {
			assertTrue("A UF deveria ter 3 ou mais cidades associadas: " + uf, uf.getCidades().size() >= 3);
		}		
	}
	
	@Test
	public void consultarOperadorSizeMenor() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeLess(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// garante que a uf criada não está na lista
		assertFalse("A UF criada não deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas possuem menos que 3 cidades
		for (UF uf : ufs) {
			assertTrue("A UF deveria ter menos que 3 cidades associadas: " + uf, uf.getCidades().size() < 3);
		}		
	}
	
	@Test
	public void consultarOperadorSizeMenorIgual() throws Exception {
		UF uf1 = criarUFOperadoresSize();
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());

		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.filterBy("cidades", Operators.sizeLessEquals(), 3);
		
		List<UF> ufs = qbe.search(filtro);

		// garante que a uf criada não está na lista
		assertTrue("A UF criada deveria estar na lista", ufs.contains(uf1));
		
		// varre a lista verificando se todas as UFs encontradas possuem 3 ou menos cidades
		for (UF uf : ufs) {
			assertTrue("A UF deveria ter 3 ou menos cidades associadas: " + uf, uf.getCidades().size() <= 3);
		}		
	}

	private UF criarUFOperadoresSize() {
		// cria uma UF 
		UF uf1 = new UF("T1");
		getJpa().save(uf1);
		
		// cadastra algumas cidades
		getJpa().save(new Cidade("cidade1", uf1));
		getJpa().save(new Cidade("cidade2", uf1));
		getJpa().save(new Cidade("cidade3", uf1));
		getJpa().getEm().flush();
		getJpa().getEm().clear();
		return uf1;
	}

	private void criarUFsIsEmptyIsNotEmpty() {
		// cria duas novas UF para testes
		UF uf1 = new UF("T1");
		UF uf2 = new UF("T2");
		getJpa().save(uf1);
		getJpa().save(uf2);
		
		// cadastra todas as cidades na uf1
		Cidade cid1 = new Cidade("cidade1", uf1);
		Cidade cid2 = new Cidade("cidade2", uf1);
		Cidade cid3 = new Cidade("cidade3", uf1);
		getJpa().save(cid1);
		getJpa().save(cid2);
		getJpa().save(cid3);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
	}

	private void criarPessoasTestIsNullIsNotNull() {
		Pessoa p1 = new Pessoa("Pessoa 1", null, null, null, null);
		Pessoa p2 = new Pessoa("Pessoa 2", null, null, null, "p2@p.com");
		Pessoa p3 = new Pessoa("Pessoa 3", null, null, null, "p3@p.com");
		Pessoa p4 = new Pessoa("Pessoa 4", null, null, null, "p4@p.com");
		Pessoa p5 = new Pessoa("Pessoa 5", null, null, null, null);
		
		getJpa().save(p1);
		getJpa().save(p2);
		getJpa().save(p3);
		getJpa().save(p4);
		getJpa().save(p5);
		getJpa().getEm().flush();
		getJpa().getEm().clear();
	}
	
}
