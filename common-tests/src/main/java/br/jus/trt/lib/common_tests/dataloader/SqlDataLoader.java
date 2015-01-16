package br.jus.trt.lib.common_tests.dataloader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.deltaspike.core.util.StringUtils;
import org.hibernate.jdbc.Work;

/**
 * Realiza uma carga de dados baseado na execução de scripts. O arquivo com o script informado será procurado
 * baseado em seu nome relativo, caso não seja encontrado será procurado dentro do diretório "src/test/resources",
 * que é o caminho raíz default esperado.
 * @author augusto
 *
 */
public class SqlDataLoader extends HibernateDataLoader {
	/** Caminho para o Script SQL para execução */
	private String scriptPath;

	public SqlDataLoader() {
		// Default
	}
	
	/**
	 * @param scriptPath Caminho para o Script SQL para execução.
	 */
	public SqlDataLoader(String scriptPath) {
		super();
		this.scriptPath = scriptPath;
	}

	@Override
	public void load() throws Exception {
		try {
			getLogger().info("Executando script de carga de dados: " + getScriptPath());
			executeDMLScript(getScriptPath());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Não foi possível executar scripts de carga para testes: " + getScriptPath(), e);
		}	
	}


	/**
	 * Executa um script que modifica os dados na base de dados (insert, update, delete).
	 * 
	 * @param scriptClearPath
	 *            Caminho para o arquivo.
	 * @throws Exception 
	 */
	protected void executeDMLScript(String scriptClearPath) throws Exception {
		final List<String> commands = readScriptComands(scriptClearPath);
		
		if (!commands.isEmpty()) {
			
			// executando como uma unidade de comando
			getSession().doWork(new Work() {
			    public void execute(Connection connection) throws SQLException {
			    	Statement st = connection.createStatement();
			        for (String command : commands) {
			        	if (isValid(command)) {
			        		st.addBatch(removeEndOfFile(command));
			        	}
			        }
			        st.executeBatch();
			    }

			    /**
			     * Verifica se é um comando válido. Descarta comentários iniciados com -- 
			     */
			    private boolean isValid(String command) {
					return !StringUtils.isEmpty(command) && !command.trim().startsWith("--");
				}

				/**
			     * Remove ";" do final do comando, já que causa problemas quando executado em batch. 
			     */
				private String removeEndOfFile(String command) {
					String removed = command.trim();
					int lastIndexOf = removed.lastIndexOf(";");
					if (lastIndexOf + 1 == removed.length()) {
						removed = removed.substring(0, lastIndexOf);
					}
					return removed;
				}
			});
			
		}
		
	}

	private static List<String> readScriptComands(String scriptPath) throws Exception {
		try {
			InputStream scriptInput = null;
			Scanner scanner = null;
			
			try {
				scriptInput = Thread.currentThread().getContextClassLoader().getResourceAsStream(scriptPath);
				scanner = new Scanner(scriptInput, "UTF8");
				
				List<String> comandos = new ArrayList<>();
				while (scanner.hasNext()) {
					String linha = scanner.nextLine();
					comandos.add(linha);
				}
				
				return comandos;
			} finally {
				if (scanner != null) {
					scanner.close();
				}
				if (scriptInput != null) {
					scriptInput.close();
				}	
			}
			
		} catch (Exception e) {
			throw new Exception("Não foi possível ler o Script para carga de dados: " + scriptPath, e);
		}
				
		
	}

	// getter and setters
	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String script) {
		this.scriptPath = script;
	}
	
	@Override
	public String toString() {
		return "Script to load:" + getScriptPath();
	}

}
