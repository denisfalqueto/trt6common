package br.jus.trt.lib.qbe;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.jus.trt.lib.qbe.api.QBERepository;
import br.jus.trt.lib.qbe.api.OperationContainer.ContainerType;
import br.jus.trt.lib.qbe.api.operator.Operators;
import br.jus.trt.lib.qbe.domain.Cidade;
import br.jus.trt.lib.qbe.domain.Dependente;
import br.jus.trt.lib.qbe.domain.Pessoa;
import br.jus.trt.lib.qbe.domain.Servidor;
import br.jus.trt.lib.qbe.domain.Servidor.SITUACAO;
import br.jus.trt.lib.qbe.domain.UF;
import br.jus.trt.lib.qbe.repository.criteria.CriteriaQbeRepository;
import br.jus.trt.lib.qbe.repository.criteria.OperatorProcessorRepositoryFactory;

public class QBEFilterTest extends QbeTestBase {

	/**
	 * Testa consulta utilizando uma propriedade aninhada como filtro
	 */
	@Test
	public void consultaEntidadeFiltroPropAninhado() {
		
		Cidade cid = new Cidade();
		cid.setNome("tESTE");
		cid.setUf(getEntityManager().find(UF.class, 116L));
		getJpa().save(cid);
		getJpa().clearCache();
		
		// cria um exemplo 
		Cidade exemplo = new Cidade();
		exemplo.setUf(new UF());
		exemplo.getUf().setSigla("PE");
		
		// executa uma consulta com HQL filtrando pelo nome do exemplo
		String hql = "from " + Cidade.class.getSimpleName() + " cid where cid.uf.sigla = ?";
		List<Cidade> cidadesHQL = getQuerier().searchAndValidateNotEmpty(hql, exemplo.getUf().getSigla());		
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(exemplo);
		filtro.setProspectionLevel(0);
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara as listas, o resultado esperado deveria ser diferente porque
		// a propriedade utilizada como filtro est� um n�vel de hierarquia abaixo
		// da entidade principal
		assertContentNotEqual(cidadesHQL, cidadesQbe);		
		
		// ajustando a consulta para considerar o segundo n�vel de propriedades
		filtro.incrementProspectionLevel();
		cidadesQbe = qbe.search(filtro);
		
		// compara a lista, esperando resultados id�nticos
		assertContentEqual(cidadesHQL, cidadesQbe);
	}
	
	/**
	 * Testa consulta utilizando uma propriedade aninhada como filtro
	 */
	@Test
	public void consultaEntidadeFiltroPropRepeticaoAlias() {
		
		// executa uma consulta com HQL filtrando pelo nome do exemplo
		String hql = "from " + Cidade.class.getSimpleName() + " cid where cid.uf.sigla = ? or cid.uf.sigla = ?";
		List<Cidade> cidadesHQL = getQuerier().searchAndValidateNotEmpty(hql, "PE", "PB");		
		
		// executa a consulta utilizando QBE
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		QBEFilter<Cidade> filtro = new QBEFilter<Cidade>(Cidade.class);
		
		filtro.setRootContainerType(ContainerType.OR);
		filtro.filterBy("uf.sigla", Operators.equal(), "PE");
		filtro.filterBy("uf.sigla", Operators.equal(), "PB");
		
		List<Cidade> cidadesQbe = qbe.search(filtro);
		
		// compara a lista, esperando resultados id�nticos
		assertContentEqual(cidadesHQL, cidadesQbe);
	}
	
	/**
	 * Testa uma consulta polim�rfica para entidades mapeadas com heran�a
	 */
	@Test
	public void consultaPolimorficaTipoClasse() {
		
		// cria um filtro para Pessoa, mas realiza a consulta com um exemplo de Servidor
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Servidor.class);
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Pessoa> servidores = qbe.search(filtro);
		
		for (Pessoa pessoa : servidores) {
			assertTrue("Deveria ser do tipo Servidor", pessoa instanceof Servidor);
		}
	}
	
	/**
	 * Testa uma consulta polim�rfica para entidades mapeadas com heran�a
	 */
	@Test
	public void consultaPolimorficoTipoExemplo() {
		
		// cadastra um servidor ativo e outro inativo para garantir variedade de situacao
		Servidor servidor1 = new Servidor();
		servidor1.setCidade(getQuerier().findAt(Cidade.class, 0));
		servidor1.setCpf("99999999999");
		servidor1.setDataNascimento(new Date());
		servidor1.setEmail("serv@email.com");
		servidor1.setMatricula("99999");
		servidor1.setNome("Serv1");
		servidor1.setSituacao(SITUACAO.ATIVO);

		Servidor servidor2 = new Servidor();
		servidor2.setCidade(getQuerier().findAt(Cidade.class, 0));
		servidor2.setCpf("88888888888");
		servidor2.setDataNascimento(new Date());
		servidor2.setEmail("serv2@email.com");
		servidor2.setMatricula("89999");
		servidor2.setNome("Serv2");
		servidor2.setSituacao(SITUACAO.INATIVO);		
		
		getJpa().save(servidor1);
		getJpa().save(servidor2);
		getJpa().getEm().clear();
		
		// cria um filtro para Pessoa, mas realiza a consulta com um exemplo de Servidor
		Servidor exemplo = new Servidor();
		exemplo.setSituacao(SITUACAO.ATIVO);
		
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(exemplo);
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Pessoa> servidores = qbe.search(filtro);
		
		for (Pessoa pessoa : servidores) {
			assertTrue("Deveria ser do tipo Servidor", pessoa instanceof Servidor);
			
			Servidor serv = (Servidor) pessoa;
			assertEquals("Deveria ser uma servidor ATIVO", SITUACAO.ATIVO, serv.getSituacao());
		}
	}
	
	/**
	 * Testa uma consulta polim�rfica para entidades mapeadas com heran�a
	 */
	@Test
	public void consultaPolimorficoSetandoExemploDinamicamente() {
		
		// cadastra dependente, servidor ativo e outro inativo para garantir variedade de situacao
		Servidor servidor1 = new Servidor();
		servidor1.setCidade(getQuerier().findAt(Cidade.class, 0));
		servidor1.setCpf("99999999999");
		servidor1.setDataNascimento(new Date());
		servidor1.setEmail("serv@email.com");
		servidor1.setMatricula("99999");
		servidor1.setNome("Serv1");
		servidor1.setSituacao(SITUACAO.ATIVO);

		Servidor servidor2 = new Servidor();
		servidor2.setCidade(getQuerier().findAt(Cidade.class, 0));
		servidor2.setCpf("88888888888");
		servidor2.setDataNascimento(new Date());
		servidor2.setEmail("serv2@email.com");
		servidor2.setMatricula("89999");
		servidor2.setNome("Serv2");
		servidor2.setSituacao(SITUACAO.INATIVO);
		
		Dependente dependente = new Dependente();
		dependente.setCidade(getQuerier().findAt(Cidade.class, 0));
		dependente.setCpf("88888888888");
		dependente.setDataNascimento(new Date());
		dependente.setEmail("serv2@email.com");
		dependente.setServidor(getQuerier().findAt(Servidor.class, 0));
		dependente.setNome("Dependente");
		dependente.setDataInicio(new Date());
		
		getJpa().save(servidor1);
		getJpa().save(servidor2);
		
		getJpa().save(dependente);
		
		// cria um filtro para Pessoa, mas realiza a consulta com um exemplo de Servidor
		Servidor exemplo = new Servidor();
		exemplo.setSituacao(SITUACAO.ATIVO);
		
		QBEFilter<Pessoa> filtro = new QBEFilter<Pessoa>(Pessoa.class);
		filtro.setExample(exemplo);
		
		QBERepository qbe = new CriteriaQbeRepository(getJpa().getEm(), OperatorProcessorRepositoryFactory.create());
		List<Pessoa> servidores = qbe.search(filtro);
		
		for (Pessoa pessoa : servidores) {
			assertTrue("Deveria ser do tipo Servidor", pessoa instanceof Servidor);
			
			Servidor serv = (Servidor) pessoa;
			assertEquals("Deveria ser uma servidor ATIVO", SITUACAO.ATIVO, serv.getSituacao());
		}
	}

}
