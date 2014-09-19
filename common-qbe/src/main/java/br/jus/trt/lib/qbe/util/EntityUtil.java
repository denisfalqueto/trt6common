package br.jus.trt.lib.qbe.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.jus.trt.lib.qbe.api.Identifiable;

public class EntityUtil {
	/**
	 * Monta uma lista de ID's a partir de uma lidata de entidades.
	 * @param <TIPO> Tipo do ID das entidades.
	 * @param entities Lista de entidades.
	 * @return Lista de ID's.
	 */
	public static List<Object> getIds(List<? extends Identifiable> entities) {
		List<Object> ids = new ArrayList<Object>();
		for (Identifiable entidade : entities) {
			ids.add(entidade.getId());
		}
		return ids;
	}
	
	/**
	 * Monta uma lista de ID's a partir de uma lidata de entidades.
	 * @param <TIPO> Tipo do ID das entidades.
	 * @param entities Array de entidades.
	 * @return Lista de ID's.
	 */
	public static List<?> getIds(Identifiable...entities) {
		return getIds(Arrays.asList(entities));
	}
}
