package br.jus.trt.lib.common_core.exception;

import br.jus.trt.lib.common_core.util.StringUtil;


/**
 * Representa uma mensagem a ser associada a uma exceção. Permite a configuração da mensagem através de parâmetros.
 */
public class ExceptionMessage {

	/** Toda mensagem exceção pode ter um código associado. Geralmente, para identificação de uma mensagem específica. */
	private String code;
	
	/** Para preenchimento de mensagens parametrizadas. */
	private Object[] parameters;
	
	/** Texto da mensagem. */
	private String message;
	
	/** Define se o código da mensagem será exibido para o usuário **/
	private Boolean showCode;
	
	/**
	 * Construtor padrão que determina a mensagem e seus parâmetros.
	 * @param codigo Toda mensagem exceção pode ter um código associado. Geralmente, para identificação de uma mensagem específica.
	 * @param mensagem Texto explicativo a ser exibido, geralmente na ocorrência de exceções.
	 * @param parametros Permite a parametrização da mensagem, associando os elementos deste array a indicadores
	 * encontrados na mensagem no formato "{indice}". 
	 * Ex: No exemplo abaixo o indicador "{0}" representa o valor na posição 0 no array de parâmetros recebido.
	 * 
	 *   Mensagem: "O cpf {0} não está cadastrado."
	 * 
	 */
	public ExceptionMessage(String codigo, String mensagem, Object...parametros) {
		super();
		this.setCode(codigo);
		this.setMessage(mensagem);
		this.setShowCode(false);
		this.setParameters(parametros);
	}
	
	/**
	 * Construtor que determina a mensagem, seus parâmetros e se o código deve ser exibido junto com a mensagem.
	 * @param codigo Toda mensagem exceção pode ter um código associado. Geralmente, para identificação de uma mensagem específica.
	 * @param mensagem Texto explicativo a ser exibido, geralmente na ocorrência de exceções.
	 * @param showCode Define se o código da mensagem será exibido para o usuário
	 * @param parametros Permite a parametrização da mensagem, associando os elementos deste array a indicadores
	 * encontrados na mensagem no formato "{indice}". 
	 * Ex: No exemplo abaixo o indicador "{0}" representa o valor na posição 0 no array de parâmetros recebido.
	 * 
	 *   Mensagem: "O cpf {0} não está cadastrado."
	 * 
	 */
	public ExceptionMessage(String codigo, String mensagem, Boolean showCode, Object...parametros) {
		super();
		this.setCode(codigo);
		this.setMessage(mensagem);
		this.setShowCode(showCode);
		this.setParameters(parametros);
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parametros) {
		this.parameters = parametros;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String mensagem) {
		this.message = mensagem;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String codigo) {
		this.code = codigo;
	}
	
	private String getFormattedMessage() {
		return StringUtil.getFormattedMessage(message, parameters);
	}

	@Override
	public boolean equals(Object obj) {
		String m1 = null, m2 = null;
		boolean retorno = false;
		if (obj == this) {
            retorno = true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            retorno = false;
        } else {
        	ExceptionMessage e = (ExceptionMessage) obj;
        	m1 = this.getFormattedMessage(); m2 = e.getFormattedMessage();
        	retorno = (code == e.getCode() || (code != null && code.equals(e.getCode())))
        			&& (m1 == m2 || (m1 != null && m1.equals(m2)));
        }
		return retorno;
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

	public Boolean getShowCode() {
		return showCode;
	}

	public void setShowCode(Boolean showCode) {
		this.showCode = showCode;
	}
}
