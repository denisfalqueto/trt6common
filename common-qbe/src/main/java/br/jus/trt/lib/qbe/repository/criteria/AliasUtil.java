package br.jus.trt.lib.qbe.repository.criteria;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;

import br.jus.trt.lib.qbe.api.JoinType;
import br.jus.trt.lib.qbe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe com alguns métodos utilitários para lidar com Alias do Hibernate Criteria.
 * @author augusto
 */
public class AliasUtil {
    
        private static final Logger log = LogManager.getLogger();

	/**
	 * Cria alias específico para fetches.
	 * 
	 * @param aliasCache Mapa para armazenamento cache dos alias criados. 
	 * @param property Propriedade assim como no filtro.
	 * @param joinType Tipo de Join determina pela classe {@link CriteriaSpecification}
	 * @return Nome da propriedade atualizado com o alias criado.
	 */
	public static void createFetchAlias(Map<String, String> aliasCache, Criteria criteria, String property, org.hibernate.sql.JoinType joinType) {
                log.entry(aliasCache, criteria, property, joinType);
		
		/*
		 * A propriedadeFiltro por representar uma propriedade aninhada. Para o caso de fetch, não é possível configurar
		 * um EAGER em uma propriedade aninhada antes de configurar para a propriedade anterior. Portanto, deve-se identificar
		 * propriedades aninhadas e aplicar EAGER em todas as propriedades intermediárias também.
		 * Ex: pessoa.cidade.uf
		 * 
		 * Deve-se configurar fetch para pessoa, pessoa.cidade e pessoa.cidade.uf
		 */
		
		if (aliasCache == null) {
                        log.debug("Criando aliasCache");
			aliasCache = new HashMap<String, String>();
		}
		
		String alias = aliasCache.get(property); 
		if (alias == null) {
                        log.debug("Alias não encontrado: " + alias);
			alias = property.replaceAll("\\.", "");
			criteria.createAlias(property, alias, joinType);
			criteria.setFetchMode(alias, FetchMode.JOIN); // determina que o modo de fetch será "JOIN" (equivalente a EAGER)
                        log.debug("Adicionando alias no cache");
			aliasCache.put(property, alias);
		}
		
		// se a propriedade for aninhada, aplica o mesmo procedimento para as propriedades intermediárias
		if (property.contains(".")) {
                        log.debug("Propriedade possui caracter ponto");
			int ultimoToken = property.lastIndexOf(".");
			String propIntermediaria = property.substring(0, ultimoToken);
			createFetchAlias(aliasCache, criteria, propIntermediaria, joinType);
		}
		
	}	

	/**
	 * Cria alias considerando um cache de itens criandos anteriormente. Utiliza {@link JoinType#LEFT} como default.
	 * @param aliasCache Mapa para armazenamento cache dos alias criados.
	 * @param criteria Criteria para configuração do alias. 
	 * @param property Propriedade assim como no filtro.
	 * @return Nome da propriedade atualizado com o alias criado.
	 */
	public static String createPropertyAlias(Map<String, String> aliasCache, Criteria criteria, String property) {
                log.entry(aliasCache, criteria, property);
		return log.exit(createPropertyAlias(aliasCache, criteria, property, JoinType.LEFT));
	}
	
	/**
	 * Cria alias considerando um cache de itens criandos anteriormento.
	 * @see AliasUtil#createAlias(Criteria, String)
	 * 
	 * @param aliasCache Mapa para armazenamento cache dos alias criados.
	 * @param criteria Criteria para configuração do alias. 
	 * @param property Propriedade assim como no filtro.
	 * @param joinType Tipo de join a ser realizado sobre a operação.
	 * @return Nome da propriedade atualizado com o alias criado.
	 */
	public static String createPropertyAlias(Map<String, String> aliasCache, Criteria criteria, String property, JoinType joinType) {
                log.entry(aliasCache, criteria, property, joinType);

		// extraindo tokens. O último token representa propriedade, os primeiros representam a associação
		String[] tokens = property.split("\\.");
		
		// identifica a associação
		int indexUltimoToken = property.lastIndexOf(".");
		String associacao = indexUltimoToken > 0 ? property.substring(0, indexUltimoToken) : property;
                log.trace("associacao: " + associacao);
		
		// cadastra um alias para a associacao
		String alias = associacao;
		if (tokens.length > 1) {
                        log.debug("Existe mais de uma token");
			alias = createAlias(aliasCache, criteria, associacao, joinType);
			
			// se a propriedade for aninhada, ajusta o valor adaptando para utilizar o alias
			String prop = tokens[tokens.length - 1];			
			property = alias + "." + prop;
                        log.trace("property: " + property);
		}	
		
		return log.exit(property);
	}
	
	/**
	 * No Critera, propriedades aninhadas exigem a criação de um alias que representa a associação para, 
	 * então, realização da restrição ou operação desejada. Este método cria um alias baseado no nome da 
	 * propriedade e altera o caminho (dot notation) da propriedade original para um novo caminho baseado no alias.
	 * <br/>
	 * Ex: Ordenar por "cidade.uf.sigla" de forma tradional
	 * <br/>
	 * {@code
	 *  c.createAlias("cidade", "cidade");
	 *  c.createAlias("cidade.uf", "cidadeuf");
	 *  c.addOrder(Order.asc("cidadeuf.sigla"));	  
	 * }
	 * <br/><br/>
	 * Ex: Ordenar por "cidade.uf.sigla" utilizando o método
	 * <br/>
	 * {@code
	 *  String prop = createAliasProperty(criteria. propOriginal);
	 *  c.addOrder(Order.asc(prop));	  
	 * }
	 * @param criteria 
	 * @param property Propriedade assim como no filtro.
	 * @param joinType Tipo de join a ser configurado na associação. Caso seja informado null, será utilizado {@link JoinType#LEFT} como default.
	 * @return Nome da propriedade atualizado com o alias criado.
	 */
	private static String createAlias(Map<String, String> aliasCache, Criteria criteria, String property, JoinType joinType) {
                log.entry(aliasCache, criteria, property, joinType);
		String alias = null;
		
		if (!StringUtil.isStringEmpty(property)) {
                        log.debug("property é string vazia");
			
			alias = aliasCache.get(property);
			if (alias == null) {
                                log.debug("alias não foi encontrado no cache");
				
				// cria o alias 
				alias = property.replaceAll("\\.", "");
				criteria.createAlias(property, alias, joinType == null ? getJoinFetch(JoinType.LEFT) : getJoinFetch(joinType));
                                log.debug("Adicionando alias no cache");
				aliasCache.put(property, alias);

				// se a propriedade era aninhada, cria alias para os demais níveis
				String[] tokens = property.split("\\.");
				if (tokens.length > 1) {
                                        log.debug("Propriedade aninhada");
					createPropertyAlias(aliasCache, criteria, property, joinType);
				}				
				
			}

		}
		return log.exit(alias);
	}	
	
	private static org.hibernate.sql.JoinType getJoinFetch(JoinType joinType) {
		return new JoinTypeParser().parse(joinType);
	}
	
}
