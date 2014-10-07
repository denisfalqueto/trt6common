package br.jus.trt.lib.qbe;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.jus.trt.lib.common_tests.jpa.BaseTest;

/**
 * Classe base para os casos de testes do módulo QBE.
 * Gerencia o ciclo de vida do JUnit e dos recursos necessários para testes.
 * @author augusto
 */
public class QbeBaseTest extends BaseTest {

	public QbeBaseTest() {
		super("qbe_pu");
	}
	
	/**
	 * Converte uma String em uma Data Formatada utilizando o formato padrão de data.
	 * @param data	String a ser formatada.
	 * @return		Data Formatada no formato padrão.
	 * @throws ParseException 	Se a String for incompatível com o formato padrão.
	 */
	public static Date formatDate(String data) throws ParseException {
		return formatDate(data, "dd/MM/yyyy");
	}
	
	/**
	 * Converte uma String em uma Data Formatada utilizando um padrão qualquer.
	 * @param data		String a ser formatada.
	 * @param pattern	Padrão utilizado na formatação.
	 * @return			Data Formatada.
	 * @throws ParseException	Se a String ou o Padrão forem incompatíveis.
	 */
	public static Date formatDate(String data, String pattern) throws ParseException {
		Date retorno = null;
		if (data != null && !data.isEmpty()) {
			DateFormat formatter = new SimpleDateFormat(pattern);
			retorno = (Date) formatter.parse(data);
		}
		return retorno;
	}

}
