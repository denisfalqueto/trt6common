package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.List;

import org.hibernate.Hibernate;
import org.junit.Test;

import br.jus.trt.lib.qbe.api.FetchMode;
import br.jus.trt.lib.qbe.api.JoinType;
import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.Dependente;
import br.jus.trt.lib.qbe.domain.Pessoa;
import br.jus.trt.lib.qbe.domain.ProjetoServidor;
import br.jus.trt.lib.qbe.domain.Servidor;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class FetchMultipleCollectionsTest extends QbeTestBase {

	
	@Test
	public void consultarFetch2Colecoes() {

		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch("projetos");
		filtro.addFetch("dependentes");
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (Servidor servidor : servidoresQbe) {
			assertTrue("Os dependentes deveriam estar inicializados", Hibernate.isInitialized(servidor.getDependentes()));
			assertTrue("Os projetos deveriam estar inicializados", Hibernate.isInitialized(servidor.getProjetos()));
			
			for (Dependente dependente : servidor.getDependentes()) {
				assertTrue("O dependente deveria estar inicializado: " + dependente.getNome(), Hibernate.isInitialized(dependente));
			}
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				assertTrue("O projetoServidor deveria estar inicializado: " + projetoServidor.getId(), Hibernate.isInitialized(projetoServidor));
			}
		}
	}
	
	@Test
	public void consultarFetchCollectionsPropAninhada_1() {

		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch("dependentes");
		filtro.addFetch("projetos.projeto"); // propriedade aninhada na coleção
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (Servidor servidor : servidoresQbe) {
			assertTrue("Os dependentes deveriam estar inicializados", Hibernate.isInitialized(servidor.getDependentes()));
			assertTrue("Os projetos deveriam estar inicializados", Hibernate.isInitialized(servidor.getProjetos()));
			
			for (Dependente dependente : servidor.getDependentes()) {
				assertTrue("O dependente deveria estar inicializado", Hibernate.isInitialized(dependente));
			}
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				assertTrue("O projetoServidor deveria estar inicializado", Hibernate.isInitialized(projetoServidor));
				assertTrue("O projeto deveria estar inicializado", Hibernate.isInitialized(projetoServidor.getProjeto()));
			}
		}
	}
	
	@Test
	public void consultarFetchCollectionsPropAninhada_2() {

		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch("projetos.projeto");
		filtro.addFetch("dependentes.cidade.uf"); // propriedade aninhada na coleção em 2 níveis
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (Servidor servidor : servidoresQbe) {
			assertTrue("Os dependentes deveriam estar inicializados", Hibernate.isInitialized(servidor.getDependentes()));
			assertTrue("Os projetos deveriam estar inicializados", Hibernate.isInitialized(servidor.getProjetos()));
			
			for (Dependente dependente : servidor.getDependentes()) {
				assertTrue("O dependente deveria estar inicializado", Hibernate.isInitialized(dependente));
				assertTrue("A cidade do dependente deveria estar inicializada", Hibernate.isInitialized(dependente.getCidade()));
				assertTrue("A UF do dependente deveria estar inicializada", Hibernate.isInitialized(dependente.getCidade().getUf()));
			}
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				assertTrue("O projetoServidor deveria estar inicializado", Hibernate.isInitialized(projetoServidor));
				assertTrue("O projeto deveria estar inicializado", Hibernate.isInitialized(projetoServidor.getProjeto()));
			}
		}
	}
	
	@Test
	public void consultarInnerFetchCollections() {

		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<Servidor> filtro = new QBEFilter<Servidor>(Servidor.class);
		filtro.addFetch("dependentes");
		filtro.addFetch(new FetchMode("projetos", JoinType.INNER)); // inner collection em fetch manual
		
		List<Servidor> servidoresQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontrados Servidores.", servidoresQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (Servidor servidor : servidoresQbe) {
			assertTrue("Os dependentes deveriam estar inicializados", Hibernate.isInitialized(servidor.getDependentes()));
			assertTrue("Os projetos deveriam estar inicializados", Hibernate.isInitialized(servidor.getProjetos()));
			assertFalse("Eram esperados apenas servidores com projetos associados", servidor.getProjetos().isEmpty());
			
			for (Dependente dependente : servidor.getDependentes()) {
				assertTrue("O dependente deveria estar inicializado", Hibernate.isInitialized(dependente));
			}
			for (ProjetoServidor projetoServidor : servidor.getProjetos()) {
				assertTrue("O projetoServidor deveria estar inicializado", Hibernate.isInitialized(projetoServidor));
			}
		}
		
	}
	
	/**
	 * Consulta com fetch do tipo: uf.cidades.pessoas
	 */
	@Test
	public void consultarEntidadeComFetchColecoesAninhadas() {
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		QBEFilter<UF> filtro = new QBEFilter<UF>(UF.class);
		filtro.addFetch("cidades.pessoas"); // colecoes aninhadas
		
		List<UF> ufsQbe = qbe.search(filtro);
		
		assertFalse("Não foram encontradas UFs.", ufsQbe.isEmpty());
		
		// verifica se as entidades carregadas via fetch estão inicializadas
		for (UF uf : ufsQbe) {
			assertTrue("A lista de cidades deveria estar inicializada", Hibernate.isInitialized(uf.getCidades()));
			
			for (Cidade cidade : uf.getCidades()) {
				assertTrue("A cidade deveria estar inicializado", Hibernate.isInitialized(cidade));
				assertTrue("A lista de pessoas deveria estar inicializada", Hibernate.isInitialized(cidade.getPessoas()));
				
				for (Pessoa pessoa : cidade.getPessoas()) {
					assertTrue("A pessoa deveria estar inicializada", Hibernate.isInitialized(pessoa));
				}
			}
		}		
		
	}
	
	/**
	 * Nativamente o Hibernate não realiza a paginação corretamente quando associado
	 * com fetch de coleçoes. Vamos testar se o QBE resolve o problema.
	 */
	@Test
	public void consultarEntidadeComFetchColecaoEPaginacao() {
		
		/*
		 * Vamos consultar todas as UFs, mas com paginação tamanho 5. Esperamos receber 5 UFs.
		 * Em seguida consultamos todas as UFs, com paginação tamanho 5, mas realizando fetch das cidades. Esperamos receber também 5 UFs.
		 */
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		
		
		// sem fetch
		QBEFilter<UF> filter = new QBEFilter<UF>(UF.class);
		filter.paginate(0, 5);
		List<UF> ufs = qbe.search(filter);
		
		// com fetch
		filter = new QBEFilter<UF>(UF.class);
		filter.paginate(0,  5);
		filter.addFetch("cidades");
		List<UF> ufsFetch = qbe.search(filter);
		
		assertContentEqual(ufs, ufsFetch);
	}
	
}
