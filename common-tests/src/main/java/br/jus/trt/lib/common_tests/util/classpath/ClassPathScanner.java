package br.jus.trt.lib.common_tests.util.classpath;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Busca por aquivos no classpath
 * @author augusto
 *
 */
public class ClassPathScanner {

	private static final Logger logger = LogManager.getLogger(ClassPathScanner.class);
	
	/** cache para as buscas do scanner */
	private static Map<String, Object> scannerCache = new HashMap<String, Object>();

	/**
	 * Busca por arquivos no classpath/META-INF, baseado em um padrão de nome.
	 * @param namePattern Regex para identificar nome dos arquivos.
	 * @return Lista de arquivos compatíveis.
	 */
	@SuppressWarnings("unchecked")
	public static List<File> scanForFile(final String namePattern) {
		String scanSufix = "_scfile";
		
		List<File> files = (ArrayList<File>) scannerCache.get(namePattern + scanSufix);
		if (files == null) {
			files = doScanForFile(namePattern);
			scannerCache.put(namePattern + scanSufix, files);
		}
		
		return files;
	}
	
	/**
	 * Busca por arquivos no classpath/META-INF, baseado em um padrão de nome.
	 * @param namePattern Regex para identificar nome dos arquivos.
	 * @return Lista de arquivos compatíveis.
	 */
	private static List<File> doScanForFile(final String namePattern) {
		ArrayList<File> files = new ArrayList<File>();
		
		// recupera o caminho de META-INF
		URL resource = Thread.currentThread().getContextClassLoader().getResource("./META-INF");
		String file = resource.getPath();
		
		File metaInf = new File(file);
		if (metaInf.exists()) {
			
			File[] matchFiles = metaInf.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(namePattern);
				}
			});
			
			for (File matchFile : matchFiles) {
				files.add(matchFile);
			}
			
		} else {
			logger.info("Não foi encontrado o diretório META-INF. Nenhum data source foi carregado.");
		}
		return files;
	}

	/**
	 * Porcura por todas as classes acessíveis no class loader que pertencem ao pacote especificado.
	 * 
	 * @param packageName Nome do pacote para busca.
	 * @return As classes encontradas 
	 * @throws ClassPathScannerException 
	 */
	public static Collection<Class<?>> scanForClasses(final String packageName) throws ClassPathScannerException {
		
		// filtro default que aceita todos os arquivos e diretórios
		ClassPathFilter filter = new ClassPathFilter() {
			
			public boolean isFileScanneable(File file) {
				return true;
			}
			
		};
		
		return scanForClasses(packageName, filter);
	}
	
	/**
	 * Porcura por todas as classes acessíveis no class loader que pertencem ao pacote especificado.
	 * 
	 * @param packageName Nome do pacote para busca.
	 * @param filter Filtro para decidir sobre diretórios e arquivos escaneáveis
	 * @return As classes encontradas 
	 * @throws ClassPathScannerException 
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Class<?>> scanForClasses(final String packageName, ClassPathFilter filter) throws ClassPathScannerException {
		try {
			String scanSufix = "_scclasses" + filter.getClass().getSimpleName();
			Collection<Class<?>> classes = (Collection<Class<?>>) scannerCache.get(packageName + scanSufix);
			if (classes == null) {
				classes = doScanForClasses(packageName, filter);
				scannerCache.put(packageName + scanSufix, classes);
			}
			
			return classes;
		} catch (Exception e) {
			throw new ClassPathScannerException("Não foi possível scanear o classpath em busca de classes mapeadas", e);
		}
	}

	/**
	 * Porcura por todas as classes acessíveis no class loader que pertencem ao pacote especificado.
	 * 
	 * @param packageName Nome do pacote para busca.
	 * @param filter Filtro para decidir sobre diretórios e arquivos escaneáveis
	 * @return As classes encontradas 
	 */
	private static Collection<Class<?>> doScanForClasses(String packageName, ClassPathFilter filter) throws ClassNotFoundException, IOException {

		// carrega os recursos encontrados no classloader
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			
			File dir = new File(resource.getFile());
			dirs.add(dir);
		}
		
		// descobre as classes existentes no diretório do package informado
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName, filter));
		}
		return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @param filter Filtro para decidir sobre diretórios e arquivos escaneáveis
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName, ClassPathFilter filter) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			String packagePrefix = (packageName == null || packageName.trim().isEmpty()) ? "" : packageName + '.'; 
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packagePrefix + file.getName(), filter));
			} else if (file.getName().endsWith(".class")) {
				if (filter.isFileScanneable(file)) {
					//quando encontrar um arquivo .class, carrega a respectiva classe
					String className = packagePrefix + file.getName().substring(0, file.getName().length() - 6);
					className = removeKnownSourceFolderPrefix(className);
					classes.add(Class.forName(className));
				}	
			}
		}
		
		return classes;
	}

	/**
	 * Remove prefixos conhecidos.
	 */
	private static String removeKnownSourceFolderPrefix(String className) {
		
		String[] prefixs = {"bin.", "WEB-INF.", "classes."};
		
		for (String prefix : prefixs) {
			if (className.startsWith(prefix)) {
				className = className.replaceFirst(prefix, "");
			}
		}
		return className;
	}

}
