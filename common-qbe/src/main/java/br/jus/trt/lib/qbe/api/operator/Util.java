package br.jus.trt.lib.qbe.api.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {
	/**
	 * realiza algumas transformações nos valores para permitir receber uma lista com um dos valores.
	 */
	public static List<Object> extrairValores(Object... valores) {
		List<Object> list = new ArrayList<Object>();
		for (Object valor : valores) {
			if (isColecao(valor)) {
				// inclui cada item da coleção como um valor
				Collection<?> itens = (Collection<?>) valor;
				list.addAll(itens);
			} else {
				list.add(valor);
			}
		}
		return list;
	}

	/**
	 * Verifica se uma propriedade é uma coleção
	 */
	private static boolean isColecao(Object valor) {
		return valor instanceof Collection<?>;
	}
}
