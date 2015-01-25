package br.jus.trt.lib.common_tests.arquillian.graphene.findby;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.arquillian.graphene.spi.findby.ImplementsLocationStrategy;

/**
 * Anotação { link FindBy } alternativa para uso em testes, páginas e fragmentos de páginas definindo que determinado elemento deve ser
 * localizado pela estratégia JsfId (ou seja, no emaranhado de ids clients, irá buscar o final. Ex.: form1:jdid_12:input -> id jsf = input).
 *
 * @author David Vieira
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ImplementsLocationStrategy(ByJsfId.JsfIdLocationStrategy.class)
public @interface FindByJsfId {

    String value();
}