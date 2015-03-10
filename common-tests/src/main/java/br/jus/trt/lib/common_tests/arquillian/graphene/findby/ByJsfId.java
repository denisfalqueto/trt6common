/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package br.jus.trt.lib.common_tests.arquillian.graphene.findby;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;

import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.graphene.spi.ImplementedBy;
import org.jboss.arquillian.graphene.spi.TypeResolver;
import org.jboss.arquillian.graphene.spi.findby.LocationStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * Permite procurar elementos usando o id do components JSF.
 *
 * @author David Vieira
 */
@ImplementedBy(className = "br.jus.trt.lib.common_tests.arquillian.graphene.findby.ByJsfIdImpl")
public class ByJsfId extends By {

    private By implementation;

    public ByJsfId(String idJsf) {
        Validate.notNull(idJsf, "Id do Jsf não pode ser nulo!");
        this.implementation = instantiate(idJsf);
    }

    public static ByJsfId selector(String idJsf) {
        return new ByJsfId(idJsf);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.By#findElement(org.openqa.selenium.SearchContext)
     */
    @Override
    public WebElement findElement(SearchContext context) {
        return implementation.findElement(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.By#findElements(org.openqa.selenium.SearchContext)
     */
    @Override
    public List<WebElement> findElements(SearchContext context) {
        return implementation.findElements(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openqa.selenium.By#toString()
     */
    @Override
    public String toString() {
        return implementation.toString();
    }

    private static By instantiate(String selector) {
        try {
            Class<? extends By> clazz = (Class<? extends By>) TypeResolver.resolveType(ByJsfId.class);

            Constructor<? extends By> constructor = clazz.getConstructor(String.class);

            return constructor.newInstance(selector);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot instantiate ByJQuery", e);
        }
    }

    /**
     * Location Strategy para buscar o id JSF.
     *
     * @author David Vieira
     */
    public static class JsfIdLocationStrategy implements LocationStrategy {

        @Override
        public ByJsfId fromAnnotation(Annotation annotation) {
            FindByJsfId findBy = (FindByJsfId) annotation;
            return new ByJsfId(findBy.value());
        }
    }
}
