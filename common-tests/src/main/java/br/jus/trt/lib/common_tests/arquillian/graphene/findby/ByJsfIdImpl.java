package br.jus.trt.lib.common_tests.arquillian.graphene.findby;

import java.util.List;

import org.jboss.arquillian.core.spi.Validate;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 * @author David Vieira
 */
public class ByJsfIdImpl extends By {

    private final String selector;

    public ByJsfIdImpl(String idJsf) {
        Validate.notNull(idJsf, "Não é possível procurar o elemento sem o id.");
        this.selector = idJsf;
    }

    @Override
    public String toString() {
        return "ByJsfId(\"" + selector + "\")";
    }

    @Override
    public List<WebElement> findElements(SearchContext searchContext) {
    	return searchContext.findElements(By.xpath("//*[substring(@id, string-length(@id) - " + (selector.length() - 1) + ") = '" + selector + "']"));
    }

    @Override
    public WebElement findElement(SearchContext context) {
        List<WebElement> elements = findElements(context);
        if (elements == null || elements.isEmpty()) {
            throw new NoSuchElementException("Cannot locate element using: " + selector);
        }
        return elements.get(0);
    }

}
