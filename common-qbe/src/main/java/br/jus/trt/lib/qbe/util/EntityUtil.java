package br.jus.trt.lib.qbe.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.jus.trt.lib.qbe.api.Identifiable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityUtil {

    private static Logger log = LogManager.getLogger();

    /**
     * Monta uma lista de ID's a partir de uma lidata de entidades.
     *
     * @param <TIPO> Tipo do ID das entidades.
     * @param entities Lista de entidades.
     * @return Lista de ID's.
     */
    public static List<Object> getIds(List<? extends Identifiable> entities) {
        log.entry(entities);
        List<Object> ids = new ArrayList<Object>();
        for (Identifiable entidade : entities) {
            ids.add(entidade.getId());
        }
        return log.exit(ids);
    }

    /**
     * Monta uma lista de ID's a partir de uma lidata de entidades.
     *
     * @param <TIPO> Tipo do ID das entidades.
     * @param entities Array de entidades.
     * @return Lista de ID's.
     */
    public static List<?> getIds(Identifiable... entities) {
        log.entry(entities);
        return log.exit(getIds(Arrays.asList(entities)));
    }
}
