package br.jus.trt6.lib.common_web.util;

import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Oferece suporte à camada WEB para operações relacionadas ao JSF.
 * 
 * fonte: http://stackoverflow.com/questions/4602950/auto-generated-ids-for-facelets-components
 * @author augusto
 */
@Named(value="jsfSupport")
public class JsfSupport {

	/**
	 * @return Um ID único junto ao container JSF.
	 */
	public String generateId() {
		String uniqueId = FacesContext.getCurrentInstance().getViewRoot().createUniqueId();
		System.out.println(uniqueId);
		return	uniqueId;
	}
}