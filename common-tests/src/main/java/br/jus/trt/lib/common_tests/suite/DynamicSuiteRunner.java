package br.jus.trt.lib.common_tests.suite;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import br.jus.trt.lib.common_tests.util.GenericComparator;
import br.jus.trt.lib.common_tests.util.classpath.ClassPathFilter;
import br.jus.trt.lib.common_tests.util.classpath.ClassPathScanner;
import br.jus.trt.lib.common_tests.util.classpath.ClassPathScannerException;

/**
 * Test Runner que descobre automaticamente as classes de teste dentro do package desta classe, adicionando à suite de testes dinamicamente.
 */
public class DynamicSuiteRunner extends ParentRunner<Runner> {

	private final List<Runner> fRunners;

	/**
	 * Called reflectively on classes annotated with <code>@RunWith(Suite.class)</code>
	 * 
	 * @param klass the root class
	 * @param builder builds runners for classes in the suite
	 * @throws InitializationError
	 * @throws ClassPathScannerException 
	 */
	public DynamicSuiteRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError, ClassPathScannerException {
		this(builder, klass, searchForTests(klass));
	}

	/**
	 * Call this when there is no single root class (for example, multiple class names
	 * passed on the command line to {@link org.junit.runner.JUnitCore}
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param classes the classes in the suite
	 * @throws InitializationError 
	 */
	public DynamicSuiteRunner(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
		this(null, builder.runners(null, classes));
	}
	
	/**
	 * Call this when the default builder is good enough. Left in for compatibility with JUnit 4.4.
	 * @param klass the root of the suite
	 * @param suiteClasses the classes in the suite
	 * @throws InitializationError
	 */
	protected DynamicSuiteRunner(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(new AllDefaultPossibilitiesBuilder(true), klass, suiteClasses);
	}
	
	/**
	 * Called by this class and subclasses once the classes making up the suite have been determined
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param klass the root of the suite
	 * @param suiteClasses the classes in the suite
	 * @throws InitializationError
	 */
	protected DynamicSuiteRunner(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(klass, builder.runners(klass, suiteClasses));
	}
	
	/**
	 * Called by this class and subclasses once the runners making up the suite have been determined
	 * 
	 * @param klass root of the suite
	 * @param runners for each class in the suite, a {@link Runner}
	 * @throws InitializationError 
	 */
	protected DynamicSuiteRunner(Class<?> klass, List<Runner> runners) throws InitializationError {
		super(klass);
		fRunners = runners;
	}	
	
	/**
	 * @return Uma suite vazia.
	 */
	public static Runner emptySuite() {
		try {
			return new DynamicSuiteRunner((Class<?>)null, new Class<?>[0]);
		} catch (InitializationError e) {
			throw new RuntimeException("Este erro não deve ocorrer.");
		}
	}
	
	/**
	 * Procura por classes de testes a partir do package da suíte em execução.
	 * @param suiteClass Suíte em execução.
	 * @return Todas as classes de teste identificadas a partir do diretório da suíte.
	 * @throws ClassPathScannerException 
	 */
	private static Class<?>[] searchForTests(Class<?> suiteClass) throws ClassPathScannerException {
		// procurando pelas classes existentes a partir do pacote da suíte
		String packageName = suiteClass.getPackage().getName();
		Collection<Class<?>> classes = ClassPathScanner.scanForClasses(packageName, getFilter());
		
		// identificando as classes e suítes de teste
		List<Class<?>> testClasses = new ArrayList<Class<?>>();
		List<Class<?>> suiteClasses = new ArrayList<Class<?>>();
		for (Class<?> candidateClass : classes) {
			if (containsTestCases(candidateClass)) {
				testClasses.add(candidateClass);
			}
			if (!suiteClass.equals(candidateClass) && isDynamicSuiteClass(candidateClass)) {
				suiteClasses.add(candidateClass);
			}			
		}
		
		//ordenando classes de forma crescente
		GenericComparator.sort(testClasses, "name");
		
		/*
		 * As suites de testes serão pivot para o agrupamento das classes de teste. Se uma classe
		 * de teste está contida em um package que contém uma suíte, apenas a classe suíte será acrescentada 
		 * ao grupo de testes a ser executado. 
		 */
		// ordenando as suítes de teste de forma decrescente
		GenericComparator.sort(false, suiteClasses, "package.name");
		
		// extraindo apenas as suítes que não são cobertas por outras suítes.
		suiteClasses = filterNotCoveredBySuite(suiteClasses, suiteClasses);
		
		// extraindo apenas as classes de testes não cobertas por nenhuma suíte de teste.
		List<Class<?>> definitiveTestClasses = filterNotCoveredBySuite(testClasses, suiteClasses);
		
		// ordenando as classes de testes para organização do painel dos testes
		GenericComparator.sort(false, definitiveTestClasses, "package.name");
		
		definitiveTestClasses.addAll(suiteClasses);
		
		return definitiveTestClasses.toArray(new Class<?>[definitiveTestClasses.size()]); 
	}

	/**
	 * @return Filtro para identificação dos diretórios e classes de teste
	 */
	protected static ClassPathFilter getFilter() {
		return new SourceFolderFilter();
	}

	/**
	 * Verifica quais classes são cobertas por uma suite de testes, retornando apenas aquelas sem cobertura.
	 * @param testClasses Classes para checagem se são cobertas por uma suite
	 * @param suiteClasses Lista de suites de testes.
	 * @return Classes não cobertas por nenhuma as suites informadas.
	 */
	private static List<Class<?>> filterNotCoveredBySuite(List<Class<?>> testClasses, List<Class<?>> suiteClasses) {
		List<Class<?>> definitiveTestClasses = new ArrayList<Class<?>>();
		for (Class<?> testClass : testClasses) {
			boolean coveredBySuite = false;
			for (Class<?> suite : suiteClasses) {
				/*
				 * Uma classe é coberta por uma suite de testes quando esta contida no mesmo package ou em um sub-package
				 * da suíte
				 */
				if (!testClass.equals(suite)
						&& testClass.getPackage().getName().startsWith(suite.getPackage().getName())) {
					coveredBySuite = true;
					break;
				}
			}
			
			if (!coveredBySuite) {
				definitiveTestClasses.add(testClass);
			}
		}
		return definitiveTestClasses;
	}

	/**
	 * @return true se a classe for uma suíte de testes dinâmica.
	 */
	private static boolean isDynamicSuiteClass(Class<?> candidateClass) {
		RunWith runWith = candidateClass.getAnnotation(RunWith.class);
		return !isAbstract(candidateClass) && runWith != null && runWith.value().equals(DynamicSuiteRunner.class);
	}

	private static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * Verifica se a classe informada possui ao menos um método público com a anotação @{@link Test} ,
	 * identificado-a como um caso de teste.
	 */
	private static boolean containsTestCases(Class<?> candidateClass) {
		boolean containsTestCase = false;
		
		if (!isAbstract(candidateClass)) {
			Method[] methods = candidateClass.getMethods();
			for (Method method : methods) {
				if (method.isAnnotationPresent(Test.class)) {
					containsTestCase = true;
					break;
				}
			}
		}	
		return containsTestCase;
	}

	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}
	
	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner runner, final RunNotifier notifier) {
		runner.run(notifier);
	}
}
