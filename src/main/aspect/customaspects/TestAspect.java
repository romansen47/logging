package customaspects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;

import customaspects.impl.AbstractProfilingAspect;

public interface TestAspect extends OutputToFile {

	default CustomAspect getConcreteProfilingAspect() {
		for (CustomAspect aspect : getRelevantAspects()) {
			if (aspect instanceof AbstractProfilingAspect) {
				return aspect;
			}
		}
		return null;
	}

	List<OutputToFile> getRelevantAspects();

	default void syncBeforeTest(JoinPoint jp) throws Throwable {
		if (getConcreteProfilingAspect() != null) {
			((AbstractProfilingAspect) getConcreteProfilingAspect()).setEnabled(true);
			log("\rProfiling active in " + jp.toShortString().split(Pattern.quote("("))[1]);
		}
		this.syncBefore(jp);
		getTests().putIfAbsent(Thread.currentThread(), jp.toShortString());
	}

	/**
	 * Sometimes debugger will not halt in advise but we can export the
	 * corresponding code to non-advising method
	 *
	 * @param jp #com.fja.ipl.customer.lp.referencetests.ReferenceTest4DebuggingDialog.testRunAndCompare
	 */
	@SuppressWarnings("unchecked")
	default void syncBefore(final JoinPoint jp) {
		final String tcn = jp.toShortString().split(Pattern.quote("@"))[0].split(Pattern.quote("("))[1]
				.replace(Pattern.quote("."), Pattern.quote("/"));
		final Thread thread = Thread.currentThread();
		AbstractProfilingAspect deepSearch = (AbstractProfilingAspect) getConcreteProfilingAspect();
		if (deepSearch != null) {
			try {
				((Map<Thread, List<String>>) deepSearch.getThreadToOutputMap()).putIfAbsent(thread, new ArrayList<>());
			} catch (ClassCastException e) {
				log("wrong type, not a map. Check abstract inheritage dependency");
			}
		}
		for (OutputToFile aspect : this.getRelevantAspects()) {
			aspect.getTests().put(thread, tcn);
		}
	}

	/**
	 * Create folders and output for relevant aspects
	 *
	 * @param jp the join point
	 * @throws IOException in case of problems accessing file system
	 */
	default void syncAfter(final JoinPoint jp) throws IOException {
		final Thread thread = Thread.currentThread();
		synchronized (thread) {
			String tcn = "target/aspectj/test/";
			new File(tcn).mkdirs();
			if (getConcreteProfilingAspect() != null) {
				((AbstractProfilingAspect) getConcreteProfilingAspect()).setRecording(false);
			}
			for (OutputToFile aspect : getRelevantAspects()) {
				log("writing " + aspect.getGenericName() + "-output");
				aspect.print(tcn);
			}
			log("done! \r");
		}
	}
}
