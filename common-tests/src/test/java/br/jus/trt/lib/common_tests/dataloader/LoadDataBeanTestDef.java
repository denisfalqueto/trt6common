package br.jus.trt.lib.common_tests.dataloader;


public interface LoadDataBeanTestDef {

	/**
	 * Verifica o funcionamento da anotação sobre a classe.
	 */
	public abstract void loadDataInTypeTest();

	/**
	 * Verifica o funcionamento da anotação sobre um método.
	 * Neste caso, deve considerar o que há sobre a classe e sobre o método.
	 */
	public abstract void loadDataInMethodTest();

	/**
	 * Teste de precendência de execução do {@link DataLoader}
	 */
	public abstract void precedenceTest();

}