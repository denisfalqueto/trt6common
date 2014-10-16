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
	
	/** Script SQL para execução */
	private String script;

	public DataLoaderSQL() {
		// Default
	}
	
	/**
	 * @param script Script SQL para execução.
	 */
	public DataLoaderSQL(String script) {
		super();
		this.script = script;
	}

	@Override
	public void load() throws Exception {
		try {
			getLogger().info("Executando script de carga de dados: " + getScript());
			executeDMLScript(getScript());
		} catch (Exception e) {
			throw new Exception("Não foi possível executar scripts de carga para testes: " + getScript(), e);
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
			            st.addBatch(command);
			        }
			        st.executeBatch();
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
	
	protected String getScript() {
		return script;
	}

	protected void setScript(String script) {
		this.script = script;
	}
	
	@Override
	public String toString() {
		return "Script to load:" + getScript();
	}
}
