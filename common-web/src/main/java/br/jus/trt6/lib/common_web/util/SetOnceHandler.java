package br.jus.trt6.lib.common_web.util;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 * <c:set> in Facelets means aliasing, not an assignment like in JSP. So every use of #{id} is translated into a 
 * separate call to #{jsfSupport.generateId(id)}, which results in problems you describe. 
 * You could write your own version of <c:set> tag which would evaluate the passed expression only once, 
 * and save the returned value.
 * 
 * fonte: http://stackoverflow.com/questions/4602950/auto-generated-ids-for-facelets-components
 * @author augusto
 *
 */
public class SetOnceHandler extends TagHandler {
	private TagAttribute var;
	private TagAttribute value;

	public SetOnceHandler(TagConfig cfg) {
		super(cfg);
		value = getRequiredAttribute("value");
		var = getRequiredAttribute("var");
	}

	public void apply(FaceletContext ctx, UIComponent parent) {
		ctx.setAttribute(var.getValue(ctx), value.getObject(ctx));
	}
}
