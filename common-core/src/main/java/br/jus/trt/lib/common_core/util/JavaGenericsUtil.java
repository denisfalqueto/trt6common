package br.jus.trt.lib.common_core.util;  

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Classe que implementa operações para extrair informações de tipos genéricos
 * (GENERICS) que são informados em classes que estendem de superclasses
 * genéricas ou implementam interfaces genéricas.</p>
 * 
 * <p>Clase baseada em solução compartilhada por 
 * <a href="http://www.artima.com/weblogs/viewpost.jsp?thread=208860">
 * Ian Robertson</a></p>
 * 
 */
public class JavaGenericsUtil {

	/**
	 * Recupera o primeiro tipo genérico de uma classe. Faz uso do método 
	 * {@link JavaGenericsUtil#getGenericTypedArguments(Class, Class)} para listar
	 * os tipos genéricos, mas recupera apenas aquele no índice 0, ou retorna
	 * <b>null</b> se não encontrar.
	 * @param classeBase	Classe base (superclasse) que determina os tipos genéricos.
	 * 						É necessário porque uma classe concreta	pode definir tipos
	 * 						abstratos de diversas interfaces.
	 * @param tipoConcreto 	Classe concreta que informa os tipos concretos e estende ou
	 * 						implementa a classe ou interface genérica.
	 * @return 				Primeiro elemento dos tipos genéricos informados na classe
	 * 						concreta.
	 */
	@SuppressWarnings("unchecked")
	public static <TIPO> Class<?> getFirstGenericTypedArgument(Class<TIPO> classeBase, Class<? extends TIPO> tipoConcreto) {
		Class<?> retorno = null;
		try {
			List<Class<?>> typeArguments = JavaGenericsUtil.getGenericTypedArguments(classeBase, tipoConcreto);
			retorno = !typeArguments.isEmpty() ? (Class<TIPO>) typeArguments.get(0) : null;
		} catch (Exception e) {
			// se houver problemas, descarta a tentativa de descobrir genérics automaticamente
		}
		return retorno;
	}
	
	/**
	 * <p>Recupera a lista de tipos concretos que foram informados para preecherem os tipos genéricos
	 * de uma classe.</p>
	 * 
	 * <p>Ex:<br /> 
	 * Para a classe com definição <i>public class ServidorDAO extends DAOGenerico<Servidor, Long></i>,
	 * a chamada ao método getTypeArguments(DAOGenerico.class, ServidorDAO.class) retornaria uma lista
	 * com os tipos Servidor e Long.</p>
	 * 
	 * @param classeBase 	Classe base (superclasse) que determina os tipos genéricos. 
	 * @param tipoConcreto 	Classe concreta que informa os tipos concretos e estende ou implementa a
	 * 						classe ou interface genérica.
	 * @return 				Lista de tipos concretos informados pela classe filha.
	 */
	@SuppressWarnings("rawtypes")
	public static List<Class<?>> getGenericTypedArguments(Class classeBase, Class tipoConcreto) {
		
		// TODO o algoritmo abaixo considera apenas superclasses genéricas. Deve-se acrescentar tambémas as superinterfaces genéricas. Ex: IEntity
		
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = tipoConcreto;
		
		/* ascende na hierarquia de classes até encontrar a classe base informada */
		while (type != null				 
				&& !getClass(type).equals(classeBase)) {
			
			if (type instanceof Class) {
				
				/* Não há informações úteis em nem genérics... continua a busca */
				type = ((Class) type).getGenericSuperclass();
			} else {
				/* encontrou um tipo parametrizado */
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(classeBase)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}
		
		/*
		 * Finalmente, para cada parametro gererico informado na classe base, tenta determinar
		 * o tipo concreto para o argumento
		 */
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		
		// resolvendo tipos
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * Determina o tipo de uma classe. Se é um tipo simples (bruto) ou parametrizável.
	 * @param type 	Classe.
	 * @return 		Tipo da classe.
	 */
	@SuppressWarnings("rawtypes")
	private static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}