package br.jus.trt.lib.qbe.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtils;

import br.jus.trt.lib.qbe.api.exception.QbeException;

/**
 * Implementação genérica de um {@link PropertyComparator} para comparações simples entre campos.
 * 
 * @param <TIPO> Tipo do objeto a ser comparado.
 */
public class PropertyComparator implements Comparator<Object> {
	
	private Collator collator;
	
	/**
	 * Obtém uma instância de {@link Collator} para ordenação ignorando acentos e case
	 * @return Uma instância de {@link Collator} que ignora acentos e case nas ordenações.
	 */
	private Collator getCollator() {
		if (collator == null) {
			collator = Collator.getInstance(new Locale("pt", "BR"));
			collator.setStrength(Collator.PRIMARY);
		}
		
		return collator;
	}

	/** Lista de configuração dos atributos para comparação */
	private List<PropertyConfig> configs = new ArrayList<PropertyConfig>();

	/**
	 * Comparador genérico que realiza a comparação entre objetos através de
	 * atributos informados.
	 * 
	 * @param asc
	 *            Determina se a comparação deverá proceder de forma ascendente
	 *            ou descendente. true para ascendente e false para descendente.
	 * @param properties
	 *            Atributos na ordem em que devem ser considerados na comparação. Estes atributos devem implementar a interface
	 *            {@link Comparable} (nativamente implementadas para tipos primitivos. Aceita "atributos aninhados", ex: id.propriedade. 
	 *            Atributos com valor null "perdem" na comparação com atributos valorados.
	 */
	public PropertyComparator(boolean asc, String... properties) {
		if (properties != null) {
			for (String atributo : properties) {
				configs.add(new PropertyConfig(asc, atributo));
			}
		}
	}
	
	/**
	 * Adiciona uma configuração de comparação de atributo.
	 * @param propertyConfig Configuração para adicionar ao comparador.
	 */
	public void addPropertyConfig(PropertyConfig propertyConfig) {
		configs.add(propertyConfig);
	}
	
	/**
	 * Comparador genérico que realiza a comparação entre objetos através de
	 * atributos informados.
	 * @param configs Lista de configuração de atributos para comparação.
	 */
	public PropertyComparator(List<PropertyConfig> configs) {
		this.configs = configs;
	}



	/**
	 * Realiza ordenação da lista baseado nos atributos informados. Utiliza o modo ascendente.
	 * @param list Lista para ordenação.
	 * @param properties Atributos dos objetos para ordenação.
	 */
	public static void sort(List<?> list, String...properties) {
		sort(true, list, properties);
	}
	
	/**
	 * Realiza ordenação da lista baseado nos atributos informados.
	 * @param ascendent true para ordenação ascendente, false para ordenação descendente.
	 * @param list Lista para ordenação.
	 * @param properties Atributos dos objetos para ordenação.
	 */
	public static void sort(boolean ascendent, List<?> list, String...properties) {
		sort(list, new PropertyComparator(ascendent, properties));
	}
	
	/**
	 * Realiza ordenação da lista baseado nos atributos informados.
	 * @param list Lista para ordenação.
	 * @param properties Configuração individual dos atributos para ordenação.
	 */
	public static void sort(List<?> list, PropertyConfig...properties) {
		sort(list, new PropertyComparator(Arrays.asList(properties)));
	}
	
	private static void sort(List<?> list, PropertyComparator comparator) {
		if (list != null) {
			Collections.sort(list, comparator);
		}	
	}
	
	/**
	 * Realiza a comparação entre objetos através de atributos informados no construtor
	 * @param o1 Objeto a ser comparado
	 * @param o2 Objeto a ser comparado
	 * @return int 
	 */
	@SuppressWarnings("unchecked")
	public int compare(Object o1, Object o2) {
		try {

			// armazena o resultado da comparação
			int comparacao = 0;
			
			// enquanto o resultado for zero (0), significa que os objeto são iguais, então
			// continua varrendo os atributos para comparação
			for (int i = 0; i < configs.size() && comparacao == 0; i++) {

				// recupera a configuração para o atributo informado
				PropertyConfig configAtributo = configs.get(i);
				
				// recupera o valor a propriedade em cada objeto para comparação
				Comparable<Object> prop1 = (Comparable<Object>) PropertyUtils.getNestedProperty(o1, configAtributo.getProperty());
				Comparable<Object> prop2 = (Comparable<Object>) PropertyUtils.getNestedProperty(o2, configAtributo.getProperty());

				// realiza a comparação das propriedades, considerando null
				if (prop1 != null && prop2 != null) {
					if (prop1.getClass().equals(String.class) && prop2.getClass().equals(String.class)) {
						comparacao = getCollator().compare(prop1, prop2);
					} else {
						comparacao = prop1.compareTo(prop2);	
					}
				} else if (prop1 != null) {
					comparacao = -1;
				} else if (prop2 != null) {
					comparacao = 1;
				}
				
				// ajusta o resultado da comparação de acordo com o modo ascendente x descendente
				comparacao = configAtributo.isAscendent() ? comparacao : -comparacao; 
			}

			return comparacao;
		} catch (Exception e) {
			throw new QbeException("Falha ao executar comparador genérico", e);
		}
	}

	/**
	 * Representa um atributo para configuração de um Comparador Genérico.
	 * @author augusto
	 */
	class PropertyConfig {

		/** true para comparação ascendente, false para comparação descendente */
		private boolean ascendent;
		
		/** Nome do atributo a ser comparado*/
		private String property;

		/**
		 * @param asc true para comparação ascendente, false para comparação descendente
		 * @param atributo Path do atributo a ser comparado.
		 */
		public PropertyConfig(boolean asc, String atributo) {
			super();
			this.ascendent = asc;
			this.property = atributo;
		}

		public boolean isAscendent() {
			return ascendent;
		}

		public void setAscendent(boolean asc) {
			this.ascendent = asc;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String atributo) {
			this.property = atributo;
		}

	}

}
