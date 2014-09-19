package br.jus.trt.lib.common_core.util;

import java.util.Iterator;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Classe utilitária com operações relacionadas ao conteiner de injeção de
 * dependências.
 * 
 * @author augusto
 * 
 */
public class DIContainerUtil {

	public BeanManager getBeanManager() {
		try {
			InitialContext initialContext = new InitialContext();
			return (BeanManager) initialContext.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			// TODO Log!
			System.out.println("Couldn't get BeanManager through JNDI");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz, BeanManager bm) {
		Iterator<Bean<?>> iter = bm.getBeans(clazz).iterator();
		if (!iter.hasNext()) {
			throw new IllegalStateException(
					"CDI BeanManager cannot find an instance of requested type "
							+ clazz.getName());
		}
		Bean<T> bean = (Bean<T>) iter.next();
		CreationalContext<T> ctx = bm.createCreationalContext(bean);
		T dao = (T) bm.getReference(bean, clazz, ctx);
		return dao;
	}

	public <T> T lookup(Class<T> clazz) {
		BeanManager bm = getBeanManager();
		return lookup(clazz, bm);
	}
}
