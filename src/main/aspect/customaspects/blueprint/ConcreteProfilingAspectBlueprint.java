package customaspects.blueprint;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclarePrecedence;

import customaspects.impl.AbstractProfilingAspect; 

@Aspect
@DeclarePrecedence(value = "ConcreteTestAspectBlueprint")
public class ConcreteProfilingAspectBlueprint extends AbstractProfilingAspect {

	/**
	 * Der Pointcut der Methoden von Interesse
	 */
	private final String included = "execution(* testpackage..*.*(..))";

	/**
	 * Der resultierende Pointcut
	 */
	private final String pointcut = included;

	/**
	 * Der Eintritts-Joinpoint, hier durch die org.junit.Test-Annotation markiert.
	 */
	private final String initialJoinPoint = "@annotation(org.junit.Test)";

	/**
	 * Vor dem Betreten des Eintritts-Joinpoint wird der Schalter "running" umgelegt.
	 *
	 * @param jp der Eintritts-Joinpoint
	 */
	@Override
	@Before(initialJoinPoint)
	public void beforeInitialJoinPoint(JoinPoint jp) {
		super.beforeInitialJoinPoint(jp);
	}

	@Override
	@Around(pointcut)
	public Object recordMethodExecutionAdvise(final ProceedingJoinPoint pjp) throws Throwable {
		return super.recordMethodExecutionAdvise(pjp);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void register() {
		ConcreteTestAspectBlueprint.getInstance().getRelevantAspects().add(this);
	}


}
