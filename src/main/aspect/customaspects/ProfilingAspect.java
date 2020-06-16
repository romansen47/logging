package customaspects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public interface ProfilingAspect extends OutputToFile {

	@Override
	default String stringOf(Object o) {
		setRecording(false);
		String ans = OutputToFile.super.stringOf(o);
		setRecording(true);
		return ans;
	}

	@Override
	default String addStringsOf(String a, String b) {
		setRecording(false);
		String ans = a + b;
		setRecording(true);
		return ans;
	}

	@SuppressWarnings("unchecked")
	default void createEntries(final JoinPoint pjp) {
		List<String> list = ((Map<Thread, List<String>>) getThreadToOutputMap()).getOrDefault(Thread.currentThread(),
				new ArrayList<>());
		if (list == null) {
			list = new ArrayList<>();
		}
		((Map<Thread, List<String>>) getThreadToOutputMap()).put(Thread.currentThread(), list);
		String invocator = getTests().get(Thread.currentThread());
		if (invocator == null) {
			invocator = pjp.toLongString();
			if (invocator.startsWith("execution(")) {
				invocator = invocator.replace("execution(", Pattern.quote("(")).split(Pattern.quote("("))[1];
			} else {
				invocator = pjp.getSignature().toLongString().split(Pattern.quote(" "))[2].split(Pattern.quote("@"))[0];
				invocator = invocator.split(Pattern.quote("("))[1];
			}
			getTests().put(Thread.currentThread(), invocator);
		}
	}

	@SuppressWarnings("unchecked")
	default Object createXmlEntry(final ProceedingJoinPoint pjp) throws Throwable {
		List<String> list = ((Map<Thread, List<String>>) getThreadToOutputMap()).getOrDefault(Thread.currentThread(),
				new ArrayList<String>());
		String str = ""; 
		try {
			str = customFullyQualifiedName(pjp);
		} 
		catch (Exception e) { ; 
			return pjp.proceed();
		} 
		if (str.equals("") || str.contains("$")) {
			str = pjp.getSourceLocation().getWithinType().getSimpleName();
			str = str.replace("$", ":").split(":")[0];
		}
		String ans = "<" + str + ">\r";
		if (getCurrentDepth() != 0) {
			final Object[] args = pjp.getArgs();
			if (args.length > 0) {
				ans += "<arguments>\r";
				for (final Object arg : args) {
					ans += this.xmlString(arg);
				}
				ans += "</arguments>\r";
			}
			ans += "<executions>\r";
			list.add(ans);
			((Map<Thread, List<String>>) getThreadToOutputMap()).put(Thread.currentThread(), list);
		}
		setRecording(true);

		Object o;
		o = pjp.proceed();

		list = ((Map<Thread, List<String>>) getThreadToOutputMap()).getOrDefault(Thread.currentThread(),
				new ArrayList<String>());
		setRecording(false);
		if (getCurrentDepth() != 0) {
			String ans2 = "";
			ans2 += "</executions>\r";
			if (!stringOf(pjp.getSignature()).startsWith("void")) {
				ans2 += "<return>\r";
				ans2 += this.xmlString(o);
				ans2 += "</return>\r";
			}
			ans2 += "</" + str + ">\r";
			list.add(ans2);
		}
		((Map<Thread, List<String>>) getThreadToOutputMap()).put(Thread.currentThread(), list);
		setRecording(true);
		return o;
	}

	default void beforeInitialJoinPoint(JoinPoint jp) {
		if (isEnabled()) {
			initialize(jp);
		}
	}

	public int getCurrentDepth();

	/**
	 * @param depth the depth to set
	 */
	public void setCurrentDepth(int depth);

	void setEnabled(Boolean enabled);

	Boolean isEnabled();

	/**
	 * Legt den Schalter "recording" um
	 * @param jp 
	 */
	default void initialize(JoinPoint jp) {
		if (!isRecording()) {
			setRecording(true);
			createEntries(jp);
		}
	}

	void setRecording(boolean recording);

	boolean isRecording();

	default Object recordMethodExecutionAdvise(ProceedingJoinPoint pjp) throws Throwable {
		if (isEnabled() && isRecording()) {
			if (getCurrentDepth() + 1 < getMaximalDepth()) {
				setCurrentDepth(getCurrentDepth() + 1);
				Object ans = this.recordMethodExecution(pjp);
				setCurrentDepth(getCurrentDepth() - 1);
				if (getCurrentDepth() == 0) {
					setRecording(false);
					if (isRunOnlyOnce()) {
						setEnabled(false);
					}
				}
				return ans;
			}
		}
		return pjp.proceed();
	}

	default Object recordMethodExecution(ProceedingJoinPoint pjp) throws Throwable {
		if (this.isEnabled() && !this.isRecording()) {
			this.setRecording(true);
		}
		if (this.isLoggingToConsole()) {
			log(pjp.getThis().getClass().getSimpleName() + ": " + pjp.toShortString());
		}
		return this.createXmlEntry(pjp);

	}

	@Override
	default String xmlString(final Object o) {
		boolean a = isRecording();
		this.setRecording(false);
		String ans="";
		if (o instanceof List) {
			for (Object e:(List)o) {
				ans+=OutputToFile.super.xmlString(e);
			}
		}
		if (o instanceof Map) {
			for (Entry<?,?> e:((Map<?,?>)o).entrySet()) {
				ans+="<key>\r"+OutputToFile.super.xmlString(e.getKey())+"\r</key>\r";
				ans+="<value>\r"+OutputToFile.super.xmlString(e.getValue())+"\r</value>\r";
			}
		}
		else {
		ans = OutputToFile.super.xmlString(o);}
		this.setRecording(a);
		return ans;
	}

	int getMaximalDepth();

	boolean isRunOnlyOnce();

	void setRunOnlyOnce(boolean runOnlyOnce);

	boolean isLoggingToConsole();

	void setIsLoggingToConsole(boolean logging);

}
