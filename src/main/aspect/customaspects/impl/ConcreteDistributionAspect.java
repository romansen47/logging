package customaspects.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import customaspects.AbstractCustomAspect;
import customaspects.DistributionAspect;

@Aspect
public class ConcreteDistributionAspect extends AbstractCustomAspect implements DistributionAspect {
 
	public ConcreteDistributionAspect() {
		register();
		setGenericName("DistributionAspect");
		this.setThreadToOutputMap(new ConcurrentHashMap<Thread, Map<String, Integer>>());
	} 
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(Thread thread) throws IOException {
		final String testcase = getTests().get(thread);
		final Map<String, Integer> stats = (Map<String, Integer>) getThreadToOutputMap().get(Thread.currentThread());
		if (stats.isEmpty()) {
			log("list empty");
		} else {
			for (final String str : stats.keySet()) {
				final Integer times = stats.get(str);
				if (times != 0) { 
					String s="<" + str + ">" + times + "</" + str + ">\r";
					getBufferedWriter().write(s);
					getBufferedWriter().flush();
				}
			}
		}
		getBufferedWriter().write("</" + testcase + ">");
		getBufferedWriter().flush();
		getBufferedWriter().close();
		getFileWriter().close();
	}

	@SuppressWarnings("unchecked")
	@Before("execution(* definitions..*.*(..)) && !execution(* definitions.cache..*.*(..))")
	public void getStats(final JoinPoint jp) {
		final String key = jp.toShortString().split(Pattern.quote("@"))[0].split(Pattern.quote("("))[1]
				.replace(Pattern.quote("."), Pattern.quote("/"));
		final Map<String, Integer> stats = ((Map<Thread, Map<String, Integer>>) getThreadToOutputMap())
				.getOrDefault(Thread.currentThread(), new ConcurrentHashMap<String, Integer>());
		Integer ans = stats.get(key);
		if (ans == null) {
			ans = new Integer(1);
		} else {
			ans += 1;
		}
		stats.put(key, ans);
		((Map<Thread, Map<String, Integer>>) getThreadToOutputMap()).put(Thread.currentThread(), stats);
	}

}
