package customaspects.blueprint;

import java.io.IOException;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import customaspects.TestAspect;
import customaspects.impl.AbstractTestAspect;

@Aspect
public class ConcreteTestAspectBlueprint extends AbstractTestAspect {
	
	ConcreteTestAspectBlueprint() {
		super();
		super.setInstance(this);
	}

	/**
	 * static instance
	 *
	 * @return the instance
	 */
	public static TestAspect getInstance() {
		return instance;
	}

	/**
	 * Advise to execute preparative steps when testRunAndCompare-method is entered
	 *
	 * @param jp #com.fja.ipl.customer.lp.referencetests.ReferenceTest4DebuggingDialog.testRunAndCompare
	 */
	@Override
	@Before("@annotation(org.junit.Test)")
	public void syncBeforeTest(final JoinPoint jp) throws Throwable {
		super.syncBeforeTest(jp);
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
		super.syncAfter(jp);
	}
}
