package br.jus.trt6.lib.common_web.action;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import br.jus.trt.lib.common_core.business.domain.Entity;
import br.jus.trt.lib.common_core.business.facade.CrudFacade;
import br.jus.trt.lib.common_core.util.DIContainerUtil;
import br.jus.trt.lib.common_core.util.JavaGenericsUtil;
import br.jus.trt.lib.qbe.QBEFilter;
import br.jus.trt.lib.qbe.api.FetchMode;

/**
 * Classe base para Actions REST do tipo CRUD, associados a uma entidade de domínio
 * específica. Implementa o comportamento padrão determinado para CRUDs,
 * permitindo a customização de todas as operações.
 * 
 * @author david vieira
 * 
 * @param <ENTITY>
 *            Entidade de domínio associada a este Action Crud.
 * @param <PK>
 *            Tipo da chave primária da entidade
 * @param <FACADE>
 *            Manager compatível com a entidade de negócio a ser utilizado.
 */
public abstract class CrudRestBase<ENTITY extends Entity<PK>, PK extends Serializable, FACADE extends CrudFacade<ENTITY, PK>> {

	/**
	 * Fachada de serviços para impleentação de um fluxo CRUD.
	 */
	private FACADE facade;

	/**
	 * Tipo do facade associado, para lookup do serviço no container de DI.
	 */
	private Class<FACADE> facadeType;

	private Class<ENTITY> entityType;

	@PostConstruct
	public void init() {
		initGenericTypes();
		initObjects();
	}

	/**
	 * Inicializa os objetos de controle desclarados.
	 */
	protected void initObjects() {
		initFacade();
	}
	
	@SuppressWarnings("unchecked")
	protected void initGenericTypes() {
		List<Class<?>> genericsTypedArguments = JavaGenericsUtil
				.getGenericTypedArguments(CrudRestBase.class, this.getClass());

		this.entityType = (Class<ENTITY>) genericsTypedArguments.get(0);

		this.facadeType = (Class<FACADE>) genericsTypedArguments.get(2);
	}

	/**
	 * Inicializa o objeto concreto que representa a Facade.
	 */
	protected void initFacade() {
		this.facade = new DIContainerUtil().lookup(getFacadeType());
	}

	protected FACADE getFacade() {
		return facade;
	}

	protected Class<FACADE> getFacadeType() {
		return facadeType;
	}
	
	public Class<ENTITY> getEntityType() {
		return this.entityType;
	}

	// REST

	@POST
	@Consumes("application/json")
	public Response create(@Valid ENTITY entity) {
		getFacade().save(entity);
		return Response.created(
				UriBuilder.fromPath(getPath())
						.path(String.valueOf(entity.getId())).build()).build();
	}

	@DELETE
	@Path("/{id:[0-9][0-9]*}")
	public Response deleteById(@PathParam("id") PK id) {
		ENTITY entity = getFacade().findBy(id);
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		getFacade().remove(entity);
		return Response.noContent().build();
	}

	@GET
	@Path("/{id:[0-9][0-9]*}")
	@Produces("application/json")
	public Response findById(@PathParam("id") PK id) {
		ENTITY entity;
		try {
			QBEFilter<ENTITY> filter = new QBEFilter<ENTITY>(getEntityType());
			configureFilterFindById(filter);
			entity = getFacade().findBy(id, toStringArray(filter.getFetches()));
		} catch (NoResultException nre) {
			entity = null;
		}
		if (entity == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(entity).build();
	}

	private String[] toStringArray(List<FetchMode> fetches) {
		String[] result = new String[fetches.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = fetches.get(i).getProperty();
		}
		return result;
	}

	protected void configureFilterFindById(QBEFilter<ENTITY> filter) {}

	@GET
	@Produces("application/json")
	public List<ENTITY> listAll(@QueryParam("start") Integer startPosition,
			@QueryParam("max") Integer maxResult) {
		QBEFilter<ENTITY> filter = new QBEFilter<ENTITY>(getEntityType());
		configureFilterListAll(filter);
		filter.paginate(startPosition, maxResult);
		return getFacade().findAllBy(filter);
	}

	protected void configureFilterListAll(QBEFilter<ENTITY> filter) {}

	@PUT
	@Path("/{id:[0-9][0-9]*}")
	@Consumes("application/json")
	public Response update(@Valid ENTITY entity) {
		try {
			entity = getFacade().save(entity);
		} catch (OptimisticLockException e) {
			return Response.status(Response.Status.CONFLICT)
					.entity(e.getEntity()).build();
		}

		return Response.noContent().build();
	}

	public abstract String getPath();

}
