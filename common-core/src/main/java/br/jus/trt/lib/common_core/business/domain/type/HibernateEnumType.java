package br.jus.trt.lib.common_core.business.domain.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import br.jus.trt.lib.common_core.util.ReflectionUtil;


/**
 * Classe que implementa um tipo customizado do Hibernate que permite o mapeamento de Java Enum de forma customizada. 
 * O mapeamento é realizado nos dois sentidos, ou seja, na persistência e na leitura.
 * 
 * Por padrão, O JPA/Hibernate permite o mapemaneto de enuns através da anotação &#064;Enumerated. No entanto, o valor persistido
 * na base de dados está associado ao nome dos itens de domínio do Enum criado pelo desenvolvedor.
 * 
 * Esta classe permite ao desenvolvedo criar Enuns com qualquer nome, e determinar o valor que deverá ser persistido na base
 * de dados.
 * 
 * Para a utilização desta solução é necessário seguir alguns passos: incluir na entidade, sobre o mapemaneto da propriedade booleana,
 * 
 * <p/> Passo 1: Criar o Enum, e definir uma propriedade com os valores que se deseja persistir na base de dados (no caso, id):
 * 
 * public enum Operacao {
 * 	INCLUSAO("I", "Incluir"), EXCLUSAO("E", "Excluir");
 * 	
 * 	private String id;
 * 	private String descricao;
 * 
 * 	// construtor e getters
 * }
 * 
 * <p/> Passo 2: Mapear o tipo customizado em package-info.java
 * 
 * 	&#064;TypeDef(name = "operacao", typeClass = br.jwf.entidade.HibernateEnumType.class, 
 * 	         parameters = {
 * 		        &#064;Parameter(name  = "enumClass", 	  value = "br.jwf.entidade.Operacao"),
 * 				&#064;Parameter(name  = "identifierField", value = "id") // se não informado, considera "id" por padrão		
 * 			 }
 * 	)	
 * 
 * <p/> Passo 3: Na entidade mapeada, no atributo do tipo "Enum", utilizar a anotação &#064;Type
 * 	<i>"&#064;Type(type="operacao")"</i>.
 *  
 * <p>Contribuição inicial do appfuse: http://appfuse.org/display/APF/Java+5+Enums+Persistence+with+Hibernate </p>
 * @author augustobreno
 *
 */
public class HibernateEnumType implements UserType, ParameterizedType {

	/** nome padrão para propriedade de identificação de um enum */
	private static final String DEFAULT_IDENTIFIER = "id";

	/** para armazenar o tipo do enum com mapeamento customizado */
    @SuppressWarnings("rawtypes")
	private Class<? extends Enum> enumClass;
    
    /** para armazenar o tipo da propriedade de identificação */
    private Class<?> identifierType;
    
    
    private String idProperty;
    private AbstractSingleColumnStandardBasicType type;
    private int[] sqlTypes;
	private Field idFiled;

    public void setParameterValues(Properties parameters) {
    	
    	// recuperando o tipo do enum
        String enumClassName = parameters.getProperty("enumClass");
        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
        } catch (ClassNotFoundException cfne) {
            throw new HibernateException("Enum class not found", cfne);
        }

        // recuperando o atributo identificador
        idProperty = parameters.getProperty("identifierField", DEFAULT_IDENTIFIER);
        try {
        	idFiled = enumClass.getDeclaredField(idProperty);
        	identifierType = idFiled.getType();
        } catch (Exception e) {
            throw new HibernateException("Failed to obtain identifier method", e);
        }        
        
        // Mapeando para tipo conhecido pelo hibernate
        if (String.class.equals(identifierType)) {
        	type = StringType.INSTANCE;
        } else if (Integer.class.equals(identifierType)) {
        	type = IntegerType.INSTANCE;
        } else if (Long.class.equals(identifierType)) {
        	type = LongType.INSTANCE;
        }

        sqlTypes = new int[] { type.sqlType() };

    }

    @SuppressWarnings("rawtypes")
	public Class returnedClass() {
        return enumClass;
    }

    public Object nullSafeGet(ResultSet rs, String[] names,
    		SessionImplementor session, Object owner)
    		throws HibernateException, SQLException {
    	 Object identifier = type.get(rs, names[0], session);
         if (identifier == null) {
             return null;
         }
         
         try {
         	
         	// extrai o domínio do Enum associado
         	Enum[] enumConstants = enumClass.getEnumConstants();
         	
         	// Varre a lista de domínio do enum buscando aquele que bate com o identificador recebido, comparando pela propriedade de identificação
         	Enum itemEnum = null;
         	for (Enum e : enumConstants) {
         		if (identifier.equals(ReflectionUtil.getValue(e, idFiled))) {
         			itemEnum = e;
         			break;
         		}
     		}        	
         	
             return itemEnum;
         } catch (Exception e) {
             throw new HibernateException("Exception while invoking valueOf method '" + idFiled.getName() + "' of " +
                     "enumeration class '" + enumClass + "'", e);
         }
    }
    @SuppressWarnings("rawtypes")

    public void nullSafeSet(PreparedStatement st, Object value, int index,
    		SessionImplementor session) throws HibernateException, SQLException {
    	try {
    		if (value == null) {
    			st.setNull(index, type.sqlType());
    		} else {
    			// recupera o valor do atributo no enum 
    			Object identifier = ReflectionUtil.getValue(value, idFiled);
    			
    			// atualiza o tipo do hibernate com o valor da propriedade de identificação
    			type.set(st, identifier, index, session);
    		}
    	} catch (Exception e) {
    		throw new HibernateException("Exception while invoking identifierMethod '" + idFiled.getName() + "' of " +
    				"enumeration class '" + enumClass + "'", e);
    	}
    	
    }

    public int[] sqlTypes() {
        return sqlTypes;
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
    
}
