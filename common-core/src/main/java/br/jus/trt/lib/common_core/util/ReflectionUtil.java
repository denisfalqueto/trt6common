package br.jus.trt.lib.common_core.util;

import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import br.jus.trt.lib.common_core.exception.AppException;

/**
 * Classe utilitária com operacoes baseadas em reflection.
 */
public class ReflectionUtil {
	
	protected final static Logger log = Logger.getLogger(ReflectionUtil.class.getName());
	
	/**
	 * Verifica se este objeto é um proxy, devolvendo a respectiva instância concreta.
	 * Inicialmente compatível com {@link HibernateProxy}
	 * @param entity Objeto passível de ser um proxy.
	 * @return O objeto envolvido pelo proxy, ou o próprio objeto caso o parâmetro não seja um proxy.
	 */
	@SuppressWarnings("unchecked")
	public static <TYPE extends Object> TYPE unProxy(TYPE entity) {
		if (entity instanceof HibernateProxy) {
			if (!Hibernate.isInitialized(entity)) {
				throw new AppException("Objeto " + entity + " não inicializado. Não é possível realizar o unproxy.");
			}
			HibernateProxy proxy = (HibernateProxy) entity;
			entity = (TYPE) proxy.writeReplace();
		}
		return entity;
	}

	/**
	 * Recupera o valor de uma propriedade atraves de reflexao.
	 * @param entidade Objeto a ter a propriedade extraída.
	 * @param propriedade Propriedade a ser extraída.
	 * @return Valor da propriedade.
	 */
	public static Object getValue(Object entidade, Field propriedade) {
		try {
			boolean accessible = propriedade.isAccessible();
			propriedade.setAccessible(true);
			Object valor = propriedade.get(entidade);
			propriedade.setAccessible(accessible);
			return valor;
		} catch (Exception e) {
			throw new AppException("Falha ao recuperar valor da propriedade: " + entidade + " " + propriedade, e);
		}
	}
	
	/**
	 * Recupera o valor de uma propriedade atraves de reflexao. Faz uso do método "Get".
	 * @param object Objeto a ter a propriedade extraída.
	 * @param propertyName Nome da propriedade a ser extraída.
	 * @return Valor da propriedade ou  null.
	 */
	public static Object getValue(Object object, String propertyName) {
		boolean done = false;
		
		Object valor = null;
		if (object != null) {

			// primeiro tenta pelas vias normais: get
			try {
				valor = PropertyUtils.getProperty(object, propertyName.trim());
				done= true;
			} catch (NestedNullException e) {
				valor = null; // caso a propriedade seja null
			} catch (Exception e) {
				log.info("Não foi possível alterar o valor da propriedade via getter. Tentando a alteração direta do atributo: " 
						+ object.getClass().getSimpleName() + "." + propertyName);
			}
			
			if (!done) {
				// tenta recuperar o valor diretamente do field			
				Field field = getField(object.getClass(), propertyName);
				
				if (field == null) {
					throw new AppException("A propriedade " + object.getClass().getSimpleName() + "." + propertyName + " não existe.");
				}
		
				Object fieldOwner;
				if (!propertyName.contains(".")) {
					// se a propriedade for primária, quem contém o field é o próprio objeto
					fieldOwner = object;
				} else {
					// se a propriedade for aninhada, quem contém o field é o atributo imediatamente anterior à propriedade
					fieldOwner = getValue(object, propertyName.substring(0, propertyName.lastIndexOf(".")));
				}
				
				valor = getValue(fieldOwner, field);				
			}
			
		}	
		
		return valor; 
	}

	/**
	 * Altera o valor de uma propriedade atraves de reflexao. Faz uso do método "Set".
	 * @param object Objeto a ter a propriedade alterada.
	 * @param nomePropriedade Nome da propriedade a ser alterada.
	 */	
	public static void setValue(Object object, Field attribute, Object value) {
		try {
			boolean accessible = attribute.isAccessible();
			attribute.setAccessible(true);
			attribute.set(object, value);
			attribute.setAccessible(accessible);
		} catch (Exception e) {
			new AppException("Não foi possível alterar o valor do atributo: " + attribute, e);
		}
	}
	
	/**
	 * Altera o valor de uma propriedade atraves de reflexao. Tenta fazer uso do método "set", caso não exista ou não seja acessível, 
	 * altera diretamente o valor do atributo.
	 * @param object Objeto a ter a propriedade alterada.
	 * @param propertyName Nome da propriedade a ser alterada.
	 */	
	public static void setValue(Object object, String propertyName, Object value)  {
		boolean done = false;
		// primeiro tenta pelas vias normais: setter
		try {
			PropertyUtils.setProperty(object, propertyName, value);
			done = true;
		} catch (Exception e) {
			log.info("Não foi possível alterar o valor da propriedade via setter. Tentando a alteração direta do atributo: " 
						+ object.getClass().getSimpleName() + "." + propertyName);
			
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			log.info(sw.toString());
		}
		
		if (!done) {
			// tenta atualizar o valor do field			
			Field field = getField(object.getClass(), propertyName);
			
			if (field == null) {
				throw new AppException("A propriedade " + object.getClass().getSimpleName() + "." + propertyName + " não existe.");
			}
	
			Object fieldOwner;
			if (!propertyName.contains(".")) {
				// se a propriedade for primária, quem contém o field é o próprio objeto
				fieldOwner = object;
			} else {
				// se a propriedade for aninhada, quem contém o field é o atributo imediatamente anterior à propriedade
				fieldOwner = getValue(object, propertyName.substring(0, propertyName.lastIndexOf(".")));
			}
			
			setValue(fieldOwner, field, value);
		}	
	}
	
	/**
	 * Retorna uma referência para um atributo. O atributo pode ser da classe ou da superclasse, em qualquer nível.
	 * @param classe Class para extrair o atributo.
	 * @param nomeAtributo Nome do atributo. Suporta atributos aninhados.
	 * @return Field extraído, ou null se o atributo não existe na classe
	 */
	public static Field getField(Class<?> type, String propertyName) {
		
		if (type == null)
			throw new IllegalArgumentException("type não deve ser null.");
		if (propertyName == null)
			throw new IllegalArgumentException("propertyName não deve ser null.");
		
		Field attribute = null;
		Class<?> attHost = type;
		String[] path = propertyName.split("\\.");
		int i = 0;
		
		do {
			String property = path[i];
			attribute = getClassField(attHost, property);
			attHost = attribute != null ? attribute.getType() : null;
			i++;
		} while (i < path.length && attribute != null);
			
		return attribute;
	}
	
	/**
	 * Retorna uma referência para um método. O mpetodo pode ser da classe ou da superclasse, em qualquer nível.
	 * @param type Class para extrair o atributo.
	 * @param methodName Nome do atributo. Suporta atributos aninhados.
	 * @return Method extraído, ou null se o atributo não existe na classe
	 */
	public static Method getMethod(Class<?> type, String methodName) {
		
		if (type == null)
			throw new IllegalArgumentException("type não deve ser null.");
		if (methodName == null)
			throw new IllegalArgumentException("methodName não deve ser null.");
		
		Method method = null;
		
		// iterando sobre a estrutura da classe e das superclasses
		for (Class<?> clazz = type; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				method = clazz.getDeclaredMethod(methodName);
				break;
			} catch (Exception e) {
				// metodo nao encontrado
				// FIXME AUDIT WARN está simplesmente abafando a exceção.
			}
		}

		return method;
	}

	/**
	 * Retorna a referência de um field dentro de uma classe especifica.
	 */
	private static Field getClassField(Class<?> type, String propertyName) {
		
		Field atributo = null;
		
		// iterando sobre a estrutura da classe e das superclasses
		for (Class<?> clazz = type; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				atributo = clazz.getDeclaredField(propertyName);
				break;
			} catch (Exception e) {
				// atributo nao encontrado
				// FIXME AUDIT WARN está simplesmente abafando a exceção.
			}
		}

		return atributo;
	}
	
	/**
	 * Identifica o tipo do atributo de um determinado objeto.
	 * Suporta propriedades aninhadas com dot notation
	 * 
	 * @param type Objeto que contém o atributo.
	 * @param propertyName Nome do atributo.
	 * 
	 * @return Tipo do atributo.
	 */
	public static Class<?> getFieldType(Class<?> type, String propertyName) {
		if (type == null)
			throw new IllegalArgumentException("type não deve ser null.");
		if (propertyName == null)
			throw new IllegalArgumentException("propertyName não deve ser null.");

		String[] path = propertyName.split("\\.");
		for (int i = 0; i < path.length; i++) {
			propertyName = path[i];
			PropertyDescriptor[] propDescs = PropertyUtils.getPropertyDescriptors(type);
			for (PropertyDescriptor propDesc : propDescs)
				if (propDesc.getName().equals(propertyName)) {
					type = propDesc.getPropertyType();
					if (i == path.length - 1)
						return type;
				}
		}

		return null;
	}
	
	/**
	 * Retorna uma lista de campos (atributos ou métodos) que possuem a anotação. O atributo pode ser da classe ou da superclasse, em qualquer nível.
	 * @param classe Class para extrair o atributo.
	 * @param annotation Annotation.
	 * @return Lista de Fields extraídos
	 */
	@SuppressWarnings("rawtypes")
	public static List<Field> getFields(Class<?> classe, Class<? extends Annotation> annotation) {
		List<Field> resultFields = new ArrayList<Field>();
		
		// iterando sobre a estrutura da classe e das superclasses
		for (Class clazz = classe; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				Field[] declaredFields = clazz.getDeclaredFields();
				if (declaredFields != null) {
					for (Field field : declaredFields) {
						if (field.isAnnotationPresent(annotation)) {
							resultFields.add(field);
						}
					}	
				}
			} catch (Exception e) {
				// atributo nao encontrado
			}
		}
		
		return resultFields;
	}
	
	/**
	 * Inicializa o respectivo atributo, com valor default (nova instância), no componente. 
	 * @param componente Objeto para inicialização do atributo.
	 * @param atributo Nome do atributo a ser inicializado com valor default. Utiliza o método "Set"
	 * @return O valor do atributo inicializado
	 */
	public static Object initializeProperty(Object componente, String atributo) {
		Field field = getField(componente.getClass(), atributo);
		try {
			Object valor = field.getType().newInstance();
			setValue(componente, field, valor);
			return valor;
		} catch (Exception e) {
			throw new AppException("Falha ao inicializar atributo", e);
		}
	}

	/**
	 * Inicializa o respectivo atributo, com valor default (nova instância), no componente. 
	 * @param componente Objeto para inicialização do atributo.
	 * @param atributo Atributo a ser inicializado com valor default.
	 * @return O valor do atributo inicializado
	 */	
	public static Object initializeProperty(Object componente, Field atributo) {
		try {
			Object valor = atributo.getType().newInstance();
			setValue(componente, atributo, valor);
			return valor;
		} catch (Exception e) {
			throw new AppException("Falha ao inicializar o atributo " + atributo.getName(), e);
		}
	}
	
	public static <TIPO extends Object> TIPO instantiate(Class<TIPO> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new AppException("Falha ao instanciar objeto: " + clazz, e);
		}
	}

	/**
	 * Copia os atributos de um objeto de origem para um de destino
	 * @param origem Objeto de origem
	 * @param destino Objeto de destino
	 */
	public static void copyProperties(Object origem, Object destino) throws Exception {
	    Class<?> clazz = origem.getClass();
	    for (Field field : clazz.getDeclaredFields()) {
	        Object valor = getValue(origem, field);
	        setValue(destino, field, valor);
	    }
	}
}
