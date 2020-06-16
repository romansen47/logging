package customaspects.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import customaspects.AbstractCustomAspect;
import customaspects.OutputToFile;
import customaspects.ProfilingAspect;
import customaspects.TestAspect;

/**
 * Preparations
 */
@Aspect
public class ConcreteTestAspect extends AbstractCustomAspect implements TestAspect {

	/**
	 * Singleton implementation
	 */
	private static volatile TestAspect instance = null;

	/**
	 * static instance
	 *
	 * @return the instance
	 */
	public static TestAspect getInstance() {
		return instance;
	}

	/**
	 * for thread safety purposes, maybe obsolete...
	 */
	private static volatile Object mutex = new Object();

	/**
	 * constructor is private since in singleton scope
	 */
	private ConcreteTestAspect() {
		synchronized (mutex) {
			aspects = new ArrayList<>();
			instance = this;
			setGenericName("TestAspect");
		}
	}

	/**
	 * List of managed aspects. An aspect not contained by this list will give no
	 * output (although it might be active at runtime)
	 */
	private final List<OutputToFile> aspects;

	/**
	 * @return the aspects
	 */
	@Override
	public List<OutputToFile> getRelevantAspects() {
		return aspects;
	}

	/**
	 * Advise to execute preparative steps when testRunAndCompare-method is entered
	 *
	 * @param jp #com.fja.ipl.customer.lp.referencetests.ReferenceTest4DebuggingDialog.testRunAndCompare
	 */
	@Override
	@Before("@annotation(org.junit.Test)")
	public void syncBeforeTest(final JoinPoint jp) throws Throwable {
		TestAspect.super.syncBeforeTest(jp);
	}

	/**
	 * Produces output
	 *
	 * @param jp com.fja.ipl.customer.lp.referencetest.testcase.RegressionTest.testRunAndCompare
	 *           or com.fja.ipl.core.lp.referencetest.testcase.ITestCaseLP.run
	 * @throws IOException if st goes wrong using file system
	 */
	@After("@annotation(org.junit.Test)")
	public void syncAfterTest(final JoinPoint jp) throws IOException {
		TestAspect.super.syncAfter(jp);
	}

	@Around("execution(void plotter.Plotter.plotCompare(..))")
	public synchronized Object suppressPloterProfiling(ProceedingJoinPoint pjp) throws Throwable {
		((ProfilingAspect) this.getConcreteProfilingAspect()).setRecording(false);
		pjp.proceed();
		((ProfilingAspect) this.getConcreteProfilingAspect()).setRecording(true);
		return null;
	}

}
