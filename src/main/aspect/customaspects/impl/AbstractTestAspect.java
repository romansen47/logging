package customaspects.impl;

import java.util.ArrayList;
import java.util.List;

import customaspects.AbstractCustomAspect;
import customaspects.OutputToFile;
import customaspects.TestAspect;

/**
 * Preparations
 */
//@Aspect
public class AbstractTestAspect extends AbstractCustomAspect implements TestAspect {

	/**
	 * Singleton implementation
	 */
	protected static volatile TestAspect instance = null;
	
	protected static void setInstance(TestAspect testAspect) {
		instance=testAspect;
	}

	/**
	 * for thread safety purposes, maybe obsolete...
	 */
	private static volatile Object mutex = new Object();

	/**
	 * constructor is private since in singleton scope
	 */
	protected AbstractTestAspect() {
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

	@Override
	public void register() { 
		// do nothing
	}

//	/**
//	 * Advise to execute preparative steps when testRunAndCompare-method is entered
//	 *
//	 * @param jp #com.fja.ipl.customer.lp.referencetests.ReferenceTest4DebuggingDialog.testRunAndCompare
//	 */
//	@Override
//	@Before("@annotation(org.junit.Test)")
//	public void syncBeforeTest(final JoinPoint jp) throws Throwable {
//		TestAspect.super.syncBeforeTest(jp);
//	}
//
//	/**
//	 * Produces output
//	 *
//	 * @param jp com.fja.ipl.customer.lp.referencetest.testcase.RegressionTest.testRunAndCompare
//	 *           or com.fja.ipl.core.lp.referencetest.testcase.ITestCaseLP.run
//	 * @throws IOException if st goes wrong using file system
//	 */
//	@After("@annotation(org.junit.Test)")
//	public void syncAfterTest(final JoinPoint jp) throws IOException {
//		TestAspect.super.syncAfter(jp);
//	}

//	@Around("execution(void plotter.Plotter.plotCompare(..))")
//	public synchronized Object suppressPloterProfiling(ProceedingJoinPoint pjp) throws Throwable {
//		((ProfilingAspect) this.getConcreteProfilingAspect()).setRecording(false);
//		pjp.proceed();
//		((ProfilingAspect) this.getConcreteProfilingAspect()).setRecording(true);
//		return null;
//	}

}
