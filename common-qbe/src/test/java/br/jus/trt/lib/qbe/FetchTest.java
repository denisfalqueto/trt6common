package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.junit.Test;

import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.JoinType;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.Dependente;
import br.jus.trt.lib.qbe.domain.Pessoa;
import br.jus.trt.lib.qbe.domain.ProjetoServidor;
import br.jus.trt.lib.qbe.domain.Servidor;
import br.jus.trt.lib.qbe.domain.Servidor.SITUACAO;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class FetchTest extends QbeTestBase {

	@Test
	public void consultarEntidadeComFetchAssociacao() {

		// consulta algumas cidades, carregando tamb�m as UFs
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(Cidade.class);
		filtro.paginate(0, 5);
		filtro.addFetch("uf");
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontradas Cidades.", cidadesQbe.isEmpty());
		
		// verifica se a UD est� inicializada
		for (Cidade cidade : cidadesQbe) {
			assertTrue("A UF deveria estar inicializada. Cidade: " + cidade.getNome(), Hibernate.isInitialized(cidade.getUf()));
		}
	}
	
	
	@Test
	public void consultarEntidadeComFetchColecao() {

		// consulta todas as UFs, carregando tamb�m as cidades
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.addFetch("cidades");
		
		List<UF> ufsQbe = qbe.search(filtro);
		assertFalse("Não foram encontradas UFs.", ufsQbe.isEmpty());
		
		// verifica se a lista de cidades est�o inicializadas
		for (UF uf : ufsQbe) {
			assertTrue("As cidades deveriam estar inicializadas. UF: " + uf.getSigla(), Hibernate.isInitialized(uf.getCidades()));
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		assertEquals("search e count estão retornando resultados incompatíveis.", ufsQbe.size(), count);		
	}

	@Test
	public void consultarEntidadeComFetchAssociacaoAninhada() {

		// consulta algumas pessoas, solicitando fetch da UF, propriedade aninhada atrav�s de cidade
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.addFetch("cidade.uf");
		
		List<Pessoa> pessoasQbe = qbe.search(filtro);
		assertFalse("Não foram encontradas Pessoas.", pessoasQbe.isEmpty());
		
		// verifica se as cidades e UFs est�o inicializadas
		for (Pessoa pessoa : pessoasQbe) {
			assertTrue("A Cidade deveria estar inicializada. Pessoa: " + pessoa.getNome(), Hibernate.isInitialized(pessoa.getCidade()));
			assertTrue("A UF deveria estar inicializada. Pessoa: " + pessoa.getNome(), Hibernate.isInitialized(pessoa.getCidade().getUf()));
		}
	}
	
	@Test
	public void consultarEntidadeComFetchAssociacaoAninhadaEmColecao() {

		// consulta alguns servidores, realizando fetch de seus projetos
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);

		// busca apenas servidores envolvidos em projetos
		filtro.filterBy("projetos", Operators.isNotEmpty());
		
		// carrega as relacoes "projetos" (cole��o), inicialozando tamb�m o atributo "projeto" para cada ProjetoServidor encontrado
		filtro.addFetch("projetos.projeto"); 
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se a cole��o "projetos" est� inicializada e
		// verifica se o projeto de cada item da cole��o tamb�m est� inicializado
		for (Servidor servidor : servidoresQbe) {
			assertTrue("A coleção 'projetos' deveria estar inicializada. Servidor: " + servidor.getNome(), Hibernate.isInitialized(servidor.getProjetos()));
			
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				//verifica se o projeto tamb�m est� inicializado
				assertTrue("O Projeto deveria estar inicializado. Servidor: " + servidor.getNome(), Hibernate.isInitialized(projetoServidor.getProjeto()));
			}
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		assertEquals("search e count estão retornando resultados incompatíveis.", servidoresQbe.size(), count);
	}
	
	@Test
	public void consultarEntidadeComFetchColecaoAninhadaEmAssociacao_1() {

		// Consulta projetos, carregando os dependentes do servidor associado ao projeto
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<ProjetoServidor> filtro = new QBEFilter<ProjetoServidor>(ProjetoServidor.class);

		filtro.addFetch("servidor.dependentes"); 
		filtro.filterBy("servidor.dependentes", Operators.isNotEmpty()); // para auxiliar na validação
		
		List<ProjetoServidor> servidoresQbe = qbe.search(filtro);
		assertFalse("Não foram encontrados ProjetoServidor.", servidoresQbe.isEmpty());
		
		// verifica se o servidor e seus dependentes estão inicializados
		for (ProjetoServidor projetoServidor : servidoresQbe) {
			assertTrue("O servidor deveria estar inicializado", Hibernate.isInitialized(projetoServidor.getServidor()));
			assertTrue("Os dependentes do servidor deveriam estar inicializados", Hibernate.isInitialized(projetoServidor.getServidor().getDependentes()));
			assertFalse("A lista de dependentes não deveria estar vazia.", projetoServidor.getServidor().getDependentes().isEmpty());
			
			for (Dependente dependente : projetoServidor.getServidor().getDependentes()) {
				assertTrue("O dependente deveria estar inicializado.", Hibernate.isInitialized(dependente));
			}
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		assertEquals("search e count estão retornando resultados incompatíveis.", servidoresQbe.size(), count);
	}
	
	@Test
	public void consultarEntidadeComFetchColecaoAninhadaEmAssociacao_2() {

		// Consulta projetos, carregando os dependentes do servidor associado ao projeto
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<ProjetoServidor> filtro = new QBEFilter<ProjetoServidor>(ProjetoServidor.class);

		filtro.addFetch("servidor.dependentes.cidade"); 
		filtro.filterBy("servidor.dependentes", Operators.isNotEmpty()); // para auxiliar na validação
		
		List<ProjetoServidor> servidoresQbe = qbe.search(filtro);
		assertFalse("Não foram encontrados ProjetoServidor.", servidoresQbe.isEmpty());
		
		// verifica se o servidor e seus dependentes estão inicializados
		for (ProjetoServidor projetoServidor : servidoresQbe) {
			assertTrue("O servidor deveria estar inicializado", Hibernate.isInitialized(projetoServidor.getServidor()));
			assertTrue("Os dependentes do servidor deveriam estar inicializados", Hibernate.isInitialized(projetoServidor.getServidor().getDependentes()));
			assertFalse("A lista de dependentes não deveria estar vazia.", projetoServidor.getServidor().getDependentes().isEmpty());
			
			for (Dependente dependente : projetoServidor.getServidor().getDependentes()) {
				assertTrue("O dependente deveria estar inicializado.", Hibernate.isInitialized(dependente));
				assertTrue("A cidade deveria estar inicializada.", Hibernate.isInitialized(dependente.getCidade()));
			}
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		assertEquals("search e count estão retornando resultados incompatíveis.", servidoresQbe.size(), count);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void consultarEntidadeComFetchInnerJoin() {

		// criando um servidor sem projeto para garantir que existe este caso
		Servidor servidor = new Servidor();
		servidor.setCidade(getQuerier().findAny(Cidade.class));
		servidor.setCpf("12554789631");
		servidor.setDataNascimento(new Date());
		servidor.setEmail("serv@serv.com");
		servidor.setMatricula("321654");
		servidor.setNome("Servidor");
		servidor.setSituacao(SITUACAO.ATIVO);
		
		getJpa().save(servidor);
		getJpa().getEm().flush();
		
		// criando hql com fetch que faz inner join: apenas de servidores com projeto
		String hql = "from Servidor s inner join fetch s.projetos";
		List<Servidor> servidoresHQL = getQuerier().executeQuery(getEntityManager(), hql);
		
		assertFalse("Não foram encontrados Servidores.", servidoresHQL.isEmpty());
		assertFalse("O servidor criado nao deveria estar na lista", servidoresHQL.contains(servidor));
		
		// realiza mesma consulta com QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch(new FetchMode("projetos", JoinType.INNER)); 
		
		List<Servidor> servidoresQbe = qbe.search(filtro);

		// compara as listas
		assertContentEqual(servidoresHQL, servidoresQbe);		
		
	}
	
	/**
	 * Valida o comportamento de filtros configurados com INNER JOIN, comparando os resultados
	 * da consulta x count
	 */
	@Test
	public void consultarEntidadeFetchValidarCount() {
		
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch("projetos");
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (Servidor servidor : servidoresQbe) {
			assertTrue("Os projetos deveriam estar inicializados", Hibernate.isInitialized(servidor.getProjetos()));
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				assertTrue("O projetoServidor deveria estar inicializado: " + projetoServidor.getId(), Hibernate.isInitialized(projetoServidor));
			}
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		
		assertEquals("search e count estão retornando resultados incompatíveis.", servidoresQbe.size(), count);
	}
	
	/**
	 * No mapeamento de coleções é mais comum o uso de List, mas também é permito
	 * Set. Testando fetch com o uso de Set
	 */
	@Test
	public void fetchColecaoTipoSet() {
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.addFetch("cidadesSet");
		
		List<UF> ufsQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados UFs.", ufsQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (UF uf : ufsQbe) {
			assertTrue("As cidades deveriam estar inicializadas", Hibernate.isInitialized(uf.getCidadesSet()));
			for (Cidade cidade : uf.getCidadesSet()) {
				assertTrue("A cidade deveria estar inicializada: " + cidade.getId(), Hibernate.isInitialized(cidade));
			}
		}
		
		// utiliza o mesmo filtro no count e compara os resultados
		long count = qbe.count(filtro);
		
		assertEquals("search e count estão retornando resultados incompatíveis.", ufsQbe.size(), count);
		
		
	}
	
}
