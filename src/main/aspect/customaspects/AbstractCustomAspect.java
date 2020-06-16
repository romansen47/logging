package customaspects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; 
import org.aspectj.lang.annotation.Aspect;

@Aspect
public abstract class AbstractCustomAspect implements OutputToFile {

	private Logger logger;

	@Override
	public Logger getLogger() {
		if (logger == null) {
			logger = LogManager.getLogger(getGenericName());  
		}
		return logger;
	}

	/**
	 * the generic name
	 */
	private String genericName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getGenericName() {
		return genericName;
	}

	/**
	 * @param genericName the genericName to set
	 */
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}

	protected final String path = "target/aspectj/test/";

	/**
	 * @return the path
	 */
	@Override
	public String getPath() {
		return path;
	}

	private volatile Map<Thread, String> tests = new ConcurrentHashMap<>();

	/**
	 * @return the tests
	 */
	@Override
	public Map<Thread, String> getTests() {
		return tests;
	}

	/**
	 * file writer
	 */
	private FileWriter w;

	/**
	 * @return the file writer
	 */
	@Override
	public FileWriter getFileWriter() {
		return w;
	}

	/**
	 * @param w the file writer to set
	 */
	@Override
	public void setFileWriter(FileWriter w) {
		this.w = w;
	}

	/**
	 * buffered writer
	 */
	private BufferedWriter bw;

	/**
	 * @return the buffered writer
	 */
	@Override
	public BufferedWriter getBufferedWriter() {
		return bw;
	}

	/**
	 * @param bw the buffered writer to set
	 */
	@Override
	public void setBufferedWriter(BufferedWriter bw) {
		this.bw = bw;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String path) throws IOException {
		synchronized (path) {
			print(Thread.currentThread());
		}
	}

	private volatile Map<?, ?> threadToOutputMap = new ConcurrentHashMap<>();

	/**
	 * @return the threadToOutputMap
	 */
	@Override
	public Map<?, ?> getThreadToOutputMap() {
		return threadToOutputMap;
	}

	/**
	 * @param threadToOutputMap the threadToOutputMap to set
	 */
	public void setThreadToOutputMap(Map<?, ?> threadToOutputMap) {
		this.threadToOutputMap = threadToOutputMap;
	}

}
