package br.jus.trt.lib.common_core.business.facade;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.integration.persistence.NonUniqueEntityException;
import br.jus.trt.lib.qbe.api.Filter;
import java.io.Serializable;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;

public interface CrudFacade<E extends Entity<PK>, PK extends Serializable> extends Facade {

    /*
     * Redefinir métodos das interfaces herdadas. O objetivo é isolar a implementação real
     * dos pontos de uso acima da camada de facade.
     *
     * A primeira parte são os métodos da interface EntityRepository. A segunda parte é
     * de CrudRepository.
     */
    /**
     * Persist (new entity) or merge the given entity. The distinction on
     * calling either method is done based on the primary key field being null
     * or not. If this results in wrong behavior for a specific case, consider
     * using the {@link org.apache.deltaspike.data.api.EntityManagerDelegate}
     * which offers both {@code persist} and {@code merge}.
     *
     * @param entity Entity to save.
     * @return Returns the modified entity.
     */
    E save(E entity);

    /**
     * Convenience access to
     * {@link javax.persistence.EntityManager#remove(Object)}.
     *
     * @param entity Entity to remove.
     */
    void remove(E entity);

    /**
     * Entity lookup by primary key. Convenicence method around
     * {@link javax.persistence.EntityManager#find(Class, Object)}.
     *
     * @param primaryKey DB primary key.
     * @return Entity identified by primary or null if it does not exist.
     */
    E findBy(PK primaryKey);

    /**
     * Lookup all existing entities of entity class {@code <E extends Entity>}.
     *
     * @return List of entities, empty if none found.
     */
    List<E> findAll();

    /**
     * Lookup a range of existing entities of entity class {@code <E extends Entity>} with
     * support for pagination.
     *
     * @param start The starting position.
     * @param max The maximum number of results to return
     * @return List of entities, empty if none found.
     */
    List<E> findAll(int start, int max);

    /**
     * Query by example - for a given object and a specific set of properties.
     *
     * @param example Sample entity. Query all like.
     * @param attributes Which attributes to consider for the query.
     * @return List of entities matching the example, or empty if none found.
     */
    List<E> findBy(E example, SingularAttribute<E, ?>... attributes);

    /**
     * Query by example - for a given object and a specific set of properties
     * with support for pagination.
     *
     * @param example Sample entity. Query all like.
     * @param start The starting position.
     * @param max The maximum number of results to return
     * @param attributes Which attributes to consider for the query.
     * @return List of entities matching the example, or empty if none found.
     */
    List<E> findBy(E example, int start, int max, SingularAttribute<E, ?>... attributes);

    /**
     * Query by example - for a given object and a specific set of properties
     * using a like operator for Strings.
     *
     * @param example Sample entity. Query all like.
     * @param attributes Which attributes to consider for the query.
     * @return List of entities matching the example, or empty if none found.
     */
    List<E> findByLike(E example, SingularAttribute<E, ?>... attributes);

    /**
     * Query by example - for a given object and a specific set of properties
     * using a like operator for Strings with support for pagination.
     *
     * @param example Sample entity. Query all like.
     * @param start The starting position.
     * @param max The maximum number of results to return
     * @param attributes Which attributes to consider for the query.
     * @return List of entities matching the example, or empty if none found.
     */
    List<E> findByLike(E example, int start, int max, SingularAttribute<E, ?>... attributes);

    /**
     * Count all existing entities of entity class {@code <E extends Entity>}.
     *
     * @return Counter.
     */
    Long count();

    /**
     * Count existing entities of entity class {@code <E extends Entity>} with for a given
     * object and a specific set of properties..
     *
     * @param example Sample entity. Query all like.
     * @param attributes Which attributes to consider for the query.
     *
     * @return Counter.
     */
    Long count(E example, SingularAttribute<E, ?>... attributes);

    /**
     * Count existing entities of entity class using the like operator for
     * String attributes {@code <E extends Entity>} with for a given object and a specific set
     * of properties..
     *
     * @param example Sample entity. Query all like.
     * @param attributes Which attributes to consider for the query.
     *
     * @return Counter.
     */
    Long countLike(E example, SingularAttribute<E, ?>... attributes);

    /**
     * Busca uma entidade por ID, permitindo provocar o pré-carregamento
     * otimizado de associações relacionadas (fetch).
     *
     * @param id Identificador da entidade para busca.
     * @param fetch Lista de atributos para pré-carregamento.
     * @return A entidade identificada pelo id informado, ou null caso não seja
     * encontrada.
     */
    public E findBy(PK id, String... fetch);

    /**
     * Realiza uma consulta com filtro, retornando apenas um elemento. O filtro
     * utilizado deverá garantir que a consulta resultará em apenas um registro,
     * caso mais de um registro seja encontrado uma exceção será lançada.
     *
     * @param filter Classe que encapsula os parâmetros de consulta.
     * @return Elemento resultante da consulta, ou null caso não encontre.
     * @throws NonUniqueEntityException Exceção lançada quando a consulta
     * retorna mais de um objeto.
     */
    public E findBy(Filter<? extends E> filter) throws NonUniqueEntityException;

    /**
     * Lista todos os registros da entidade associada de forma ordenada.
     *
     * @param ascedant true para listagem ascendente, false para listagem
     * descendente
     *
     * @param orderBy Lista de campos que serão utilizados na ordenação. A ordem
     * dos parametros indicará a prioridade da ordenação.
     *
     * @return Lista de registros ordenados.
     */
    public abstract List<E> findAll(boolean ascedant, String... orderBy);

    /**
     * Conta a quantidade de registros da entidade.
     *
     * @param filter Encapsula os parâmetros e configurações das restrições da
     * consulta.
     * @return a quantidade de registros da entidade.
     */
    public Long count(Filter<? extends E> filter);

    /**
     * Realiza uma consulta baseada nas configurações do filtro.
     *
     * @param filter Encapsula os parâmetros e configurações das restrições da
     * consulta.
     * @return Resultado da consulta.
     */
    public List<E> findAllBy(Filter<? extends E> filter);
}
