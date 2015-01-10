package br.jus.trt.lib.common_tests.dataloader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.deltaspike.core.util.StringUtils;
import org.hibernate.jdbc.Work;

/**
 * Realiza uma carga de dados baseado na execução de scripts. O arquivo com o script informado será procurado
 * baseado em seu nome relativo, caso não seja encontrado será procurado dentro do diretório "src/test/resources",
 * que é o caminho raíz default esperado.
 * @author augusto
 *
 */
public class DataLoaderSQL extends HibernateDataLoader {

	/** Caminho para o diretório src/test/resources*/
	public static final String TEST_RESOURCES_PATH = "src" + File.separator + "test" + File.separator + "resources"; 
	
	/** Caminho para o Script SQL para execução */
	private String scriptPath;

	public DataLoaderSQL() {
		// Default
	}
	
	/**
	 * @param scriptPath Caminho para o Script SQL para execução.
	 */
	public DataLoaderSQL(String scriptPath) {
		super();
		this.scriptPath = scriptPath;
	}

	@Override
	public void load() throws Exception {
		try {
			getLogger().info("Executando script de carga de dados: " + getScriptPath());
			executeDMLScript(getScriptPath());
		} catch (Exception e) {
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
		String resolvedScriptPath = resolveScriptPath(scriptPath);

		try {
			return Files.readAllLines(Paths.get(resolvedScriptPath), StandardCharsets.UTF_8);
		} catch (NoSuchFileException e) {
			throw new Exception("Script para carga de dados não encontrado: " + scriptPath);
		}
				
		
	}

	private static String resolveScriptPath(String scriptPath) {
		File file = new File(scriptPath);
		if (!file.exists()) {
			file = new File(TEST_RESOURCES_PATH, scriptPath);
		}
		return file.getAbsolutePath(); 
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
