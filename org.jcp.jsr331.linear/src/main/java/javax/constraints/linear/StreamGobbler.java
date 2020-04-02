package javax.constraints.linear;

/*
 * Created By Michael C. Daconta, JavaWorld.com, 12/29/00 
 * http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
 * JF added timeout 3/1/2016
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;

public class StreamGobbler extends Thread {
	InputStream is;
	String type;
	OutputStream os;

	StreamGobbler(InputStream is, String type) {
		this(is, type, null);
	}

	StreamGobbler(InputStream is, String type, OutputStream redirect) {
		this.is = is;
		this.type = type;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null)
				pw = new PrintWriter(os);

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null)
					pw.println(line);
				// System.out.println(type + ">" + line);
			}
			if (pw != null)
				pw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// Added by JF
	static public void execute(String command, String outputfile) {
		execute(command, null, outputfile,-1);
	}
	
	static public void execute(String command, String outputfile, int timeoutMilliSeconds) {
		execute(command, null, outputfile, timeoutMilliSeconds);
	}

	static public boolean execute(String command, String inputfile,
			String outputfile, int timeoutMilliSeconds) {
		long startTime = System.currentTimeMillis();
		System.out.println("Start time: " + Calendar.getInstance().getTime());
		System.out.println("Execute command: " + command);
		try {
			FileOutputStream fos = new FileOutputStream(outputfile);
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(command);

			if (inputfile != null) {
				try {
					System.out.println("using command file: " + inputfile);
					InputStream dataFromFile = new FileInputStream(inputfile);
					OutputStream standardInputOfChildProcess = proc
							.getOutputStream();
					byte[] buff = new byte[4096];
					for (int count = -1; (count = dataFromFile.read(buff)) != -1;) {
						standardInputOfChildProcess.write(buff, 0, count);
					}
					dataFromFile.close();
					standardInputOfChildProcess.close();
				} catch (Exception e) {
					String msg = "Error in StreamGobbler: "
							+ "Cannot copy input stream from " + inputfile;
					System.out.println(msg);
					throw new RuntimeException(msg);
				}
			}
			// any error message?
			StreamGobbler errorGobbler = new StreamGobbler(
					proc.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new StreamGobbler(
					proc.getInputStream(), "OUTPUT", fos);

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			
			// Java 8 only?
			boolean result = true;
			if (timeoutMilliSeconds > 0) {
				boolean exitVal = proc.waitFor(timeoutMilliSeconds, TimeUnit.MILLISECONDS); 
				if (exitVal == false) {
				    //timeout - kill the process. 
					System.out.println("Process interrupted by timeout " + timeoutMilliSeconds + " milliseconds");
				    proc.destroyForcibly();
				    result = false;
				}
			}
			else {
				// any error???
				int exitVal = proc.waitFor();
				System.out.println("Exit value: " + exitVal);
				if (exitVal != 0)
					result = false;
			}
			System.out.println("End time: " + Calendar.getInstance().getTime());
			fos.flush();
			fos.close();
			if (result)
				System.out.println("Output is redirected to: " + outputfile);
			long executionTime = System.currentTimeMillis() - startTime;
			System.out.println("Execution time: " + executionTime + " msec");
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			//throw new RuntimeException(t);
			return false;
		}
	}

	
// ADDING TIMEOUT
//	public static int executeCommandLine(final String commandLine,
//			final boolean printOutput, final boolean printError,
//			final long timeout) throws IOException, InterruptedException,
//			TimeoutException {
//		Runtime runtime = Runtime.getRuntime();
//		Process process = runtime.exec(commandLine);
//		/* Set up process I/O. */
//		// ...
//		Worker worker = new Worker(process);
//		worker.start();
//		try {
//			worker.join(timeout);
//			if (worker.exit != null)
//				return worker.exit;
//			else
//				throw new TimeoutException();
//		} catch (InterruptedException ex) {
//			worker.interrupt();
//			Thread.currentThread().interrupt();
//			throw ex;
//		} finally {
//			process.destroy();
//		}
//	}
//
//	private static class Worker extends Thread {
//		private final Process process;
//		private volatile Integer exit;
//
//		private Worker(Process process) {
//			this.process = process;
//		}
//
//		public void run() {
//			try {
//				exit = process.waitFor();
//			} catch (InterruptedException ignore) {
//				return;
//			}
//		}
//	}

}
