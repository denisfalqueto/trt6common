package br.jus.trt.lib.qbe.repository.criteria;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

import br.jus.trt.lib.qbe.Property;
import br.jus.trt.lib.qbe.api.Filter;
import br.jus.trt.lib.qbe.api.Identifiable;
import br.jus.trt.lib.qbe.util.ReflectionUtil;
import br.jus.trt.lib.qbe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Inspeciona as propriedades de uma entidade mapeada para descobrir quais
 * são de interesse do mecanismo de QBE
 * @author augusto
 *
 */
public class PropertyInspector {

	/** Logger */
	private static Logger log;
	
	/**
	 * Extrai as propriedades preenchidas do objeto. Por padrão verifica as propriedades
	 * pertencente é entidade de consulta, mas o nível de profundidade pode ser configurado, caso
	 * seja desejado a checagem de associações.
	 * @param filter preenchida com dados de consulta.
	 * @return Lista de propriedades preenchids.
	 */
	public static <TIPO extends Identifiable> List<Property> extractFilledProperties(Filter<TIPO> filter) throws Exception {
		return extractFilledProperties(new ArrayList<Property>(), filter.getExample(), null, filter.getProspectionLevel(), 1);
	}
	
	/**
	 * Para recursividade. 
	 * @param filledProperties 
	 * @param nestedProperty Propriedade pai, de onde esta chamada foi originada 
	 */
	private static List<Property> extractFilledProperties(ArrayList<Property> filledProperties, 
			Object entity, Property nestedProperty, int maxNivel, int currentNivel) throws Exception {
		
		if (entity != null) {
			
			// verifica se a entidade é um Hibernate proxy, e extrai a verdadeira entidade
			entity = ReflectionUtil.unProxy(entity); 
			
			List<Field> propriedades = extractMainProperties(entity, currentNivel);
			for (Field field : propriedades) {
				
				if (isValidProperty(field)) {
					
					Property novaPropriedade = new Property(field, nestedProperty, ReflectionUtil.getValue(entity, field));
					if (!isCollection(novaPropriedade.getValue())) {
						
						// se é um relacionamento, e ainda há nivel para descer, ou é chave composta
						if ((isRelationship(field) && isInitialized(novaPropriedade.getValue()))) {
							
							// analisa o relacionamento apenas se o nível permitir
							if (currentNivel <= maxNivel) {
								extractFilledProperties(filledProperties, novaPropriedade.getValue(), novaPropriedade, maxNivel, currentNivel + 1);
							} else {
								// informa que ainda seria possível avançar mais na análise das propriedades
								logWarn("O relacionamento " + novaPropriedade.generateDotNotation() + " não foi analisado porque a propriedade 'nivel' " +
										"do FiltroQBE está limitando a profundidade das propriedades aninhadas. Para incluir esta propriedade," +
										"deve-se incrementar  valor do 'nvel'.");
							}
							
						// se é chave composta	
						} else if (isCompositeKey(field)) {
							extractFilledProperties(filledProperties, novaPropriedade.getValue(), novaPropriedade, maxNivel, currentNivel + 1);
							
							// se é outra propriedade qualquer
						} else if (isFilled(novaPropriedade)) {
							filledProperties.add(novaPropriedade);
						}
						
					}	
				}
			}
			
		}	
		
		return filledProperties;  
	}	
	
	
	
	private static void logWarn(String msg) {
		if (getLogger() != null) {
			getLogger().warn(msg);
		}
	}

	/**
	 * Inicializa o logger
	 */
	private static Logger getLogger() {
		if (log == null) {
			log = LogManager.getLogger(PropertyInspector.class);
		}
		return log;
	}

	/**
	 * Este método inclui certa inteligencia para perceber quando o id da entidade foi informado. Neste caso,  
	 * se o id for diferente de null, e não se trata do objeto base da consulta (nível = 1), não verifica as demais propriedades, 
	 * visto que o id é o sufiente para identificar um registro.  
	 */
	private static List<Field> extractMainProperties(Object entidade, int currentNivel) throws Exception {
		
	      List<Field> fields = new ArrayList<Field>();

	      // iterando sobre a estrutura da classe e das superclasses
	      for (Class<?> clazz = entidade.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
	    	  
	    	  // para cada classe, itera sobre os atributos
	         for (Field field : clazz.getDeclaredFields()) {
	        	 
	        	 // descarta campos considerados padroes da api
	        	 if (!isFieldDisposable(field)) {
	        		 
	        		 // se for uma associacao verifica primeiro se o id foi informado
	        		 if (currentNivel > 1 && field.isAnnotationPresent(Id.class) && (ReflectionUtil.getValue(entidade, field) != null) ) {
	        			 
	        			 fields = new ArrayList<Field>();
	        			 fields.add(field);
	        			 return fields;         
	        			 
	        		 } else {
	        			 fields.add(field);
	        		 }
	        		 
	        	 }
	        	 
	         }
	         
	      }
	      
	      return fields;
	}
	
	/**
	 * Verifica se o campo faz parte de alguma obrigação padrão da API ou é desnecessário no contexto de busca
	 * através de QBE. Ex: serialVersionUID, class
	 * @param campo Campo a ser verificado.
	 * @return true se o campo faz parte do conjunto de "descartáveis" para fins de QBE
	 */
	protected static boolean isFieldDisposable(Field campo) {
		return campo.getName().matches("serialVersionUID|class");
	}	
	
	/**
	 * Verifica se a propriedade possuir valor preenchido na entidade.
	 * @param propriedade Propriedade devidamente preenchida
	 * @return true se for preenchida.
	 * @throws Exception 
	 */
	private static boolean isFilled(Property propriedade) throws Exception {
		Object valor = propriedade.getValue();
		
		boolean preenchido = true;
		if (valor == null 
				|| (valor instanceof String && StringUtil.isStringEmpty((String) valor))
				|| (isRelationship(propriedade.getName()) && !isIdFilled(propriedade.getValue()))) { //se é um relacionamento, mas o id não está preenchido, nao considera
			preenchido = false;
		}
		return preenchido;
	}

	/**
	 * Verifica se a propriedade representa uma chave composta.
	 * @param propriedade a ser avaliada.
	 * @return true se representa uma chave composta
	 */
	private static boolean isCompositeKey(Field propriedade) {
		return propriedade.isAnnotationPresent(EmbeddedId.class);
	}

	/**
	 * Verifica se uma propriedade possui anotacao de relacionamento com outra entidade
	 * @param propriedade Propriedade a ser vefificada.
	 * @return true se representar uma relacionamento.
	 */
	private static boolean isRelationship(Field propriedade) {
		return propriedade.isAnnotationPresent(OneToMany.class) 
			|| propriedade.isAnnotationPresent(OneToOne.class)
			|| propriedade.isAnnotationPresent(ManyToMany.class)
			|| propriedade.isAnnotationPresent(ManyToOne.class);
	}
	
	/**
	 * Verifica se o bean está inicializado ou é um Hibernate Proxy.
	 * @param objeto 
	 * @return true se estiver inicializado.
	 */
	private static boolean isInitialized(Object objeto) {
		return Hibernate.isInitialized(objeto); 
	}
	
	/**
	 * Verifica se uma propriedade é uma coleção
	 */
	private static boolean isCollection(Object valor) {
		return valor instanceof Collection<?>;
	}

	/**
	 * Verifica se a propriedade é transient. Como estamos utilizando anotações em propriedades,
	 * apenas as que são anotadas com @Transient é que não sao mapeadas.
	 * @param propriedade propriedade a ser checada.
	 * @return true se for transiente.
	 */
	private static boolean isTransient(Field propriedade) {
		return propriedade.isAnnotationPresent(Transient.class);
	}
	
	/**
	 * Verifica se a propriedade é static. Como estamos utilizando o padrão QBE,
	 * apenas as que não são static devem ser consideradas.
	 * @param propriedade propriedade a ser checada.
	 * @return true se for static.
	 */
	private static boolean isStatic(Field propriedade) {
		return Modifier.isStatic(propriedade.getModifiers());
	}
	
	/**
	 * Verifica se a propriedade deve ser considerada pelo QBE
	 * @param propriedade propriedade a ser checada.
	 * @return true se for considerada.
	 */
	private static boolean isValidProperty(Field propriedade) {
		if (isTransient(propriedade)) {
			return false;
		}
		if (isStatic(propriedade)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Verifica se o id está preenchido
	 */
	private static boolean isIdFilled(Object entidade) throws Exception {
		return entidade != null && ReflectionUtil.getValue(entidade, "id") != null;
	}
	
}
