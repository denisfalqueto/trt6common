package br.jus.trt.lib.qbe;

import java.lang.reflect.Field;

import br.jus.trt.lib.qbe.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe auxiliar para representar as propriedades a serem consideradas durante a construção de uma consulta baseada
 * na solução QBE. Pode armazenar o seu caminho a partir da classe pai, possibilitando a construção de uma notação "dot notation".
 */
public class Property {

        private Logger log = LogManager.getLogger();
	private Field name;
	private Object value;
	private Property nestedName;
	private String dotNotationCache;

	/**
	 * Construtor.
	 * @param field Campo que representa a propria propriedade.
	 * @param nestedProperty Campo que representa o campo pai de onde este (this.campo) derivou.
	 * @param value Valor da propriedade.
	 */
	public Property(Field field, Property nestedProperty, Object value) {
		this.name = field;
		this.nestedName = nestedProperty;
		this.value = value;
	}
	
	/**
	 * Cria a notação em "dot notation" para representar a propriedade. 
	 * @return notacao.
	 */
	public String generateDotNotation() {
                log.entry();
		if (StringUtil.isStringEmpty(dotNotationCache)) {
                        log.debug("Gerar dotNotationCache");
			dotNotationCache = generate(name, nestedName);
		}
		return log.exit(dotNotationCache);
	}
	
	private String generate(Field prop, Property nested) {
                log.entry(prop, nested);
		String s = "";
		if (nested != null) {
			s += generate(nested.getName(), nested.getNestedName());
			s += ".";
		}
		s += prop.getName();
		
		return log.exit(s);
	}

	public Field getName() {
		return name;
	}

	public Property getNestedName() {
		return nestedName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object valor) {
		this.value = valor;
	}

}
