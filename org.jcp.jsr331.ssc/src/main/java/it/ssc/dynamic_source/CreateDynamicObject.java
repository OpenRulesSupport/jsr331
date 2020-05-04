package it.ssc.dynamic_source;
import it.ssc.context.Config;
import it.ssc.dynamic_source.exception.JavaCompilerError;
import it.ssc.log.SscLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.Diagnostic; 
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask; 

public class CreateDynamicObject {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private DynamicSourceInterface dyn_source;
	private String path_compiler;
	private StringBuffer name_class;
	private static final String nl=Config.NL; 

	public CreateDynamicObject(DynamicSourceInterface dyn_source,String path_compiler) {
		this.dyn_source = dyn_source;
		this.path_compiler=path_compiler;
		this.name_class=new StringBuffer();
	}

	public Object createObject() throws IOException, ClassNotFoundException, InstantiationException, 
	                                                 IllegalAccessException, JavaCompilerError {
		//File file_java = createFileJava();
		File file_java = createFileJava2(name_class,dyn_source, path_compiler);
		return createObjectClass(file_java);
	}

	//public DynamicClassInterface createObjectClass(File java_file)
	public Object createObjectClass(File java_file)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, JavaCompilerError {

		JavaCompiler jc = ToolProvider.getSystemJavaCompiler();

		if (jc == null) throw new JavaCompilerError("ERRORE. JavaCompiler non trovato.");
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager sjfm = jc.getStandardFileManager(diagnostics, null, null);

		Iterable<? extends JavaFileObject> file_objects = sjfm.getJavaFileObjects(java_file);
		String[] options = new String[] { "-d", path_compiler};

		CompilationTask task = jc.getTask(null, sjfm, diagnostics,	Arrays.asList(options), null, file_objects);
		
		boolean without_errors = task.call();
		if (!without_errors) {
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				logger.log(Level.SEVERE,"Alla  linea " + diagnostic.getLineNumber() +
						   nl +" in " + diagnostic.toString());
			}
			throw new JavaCompilerError("ERRORE. Compilazione del codice java non riuscita");
		}
		else {
			if(!dyn_source.getUserSource().equals("")) logger.log(Level.INFO,"Compiling source:"+dyn_source.getUserSource()+nl+"Compilazione eseguita con successo "+nl);
			
		}
		sjfm.close();	
		File file = new File(path_compiler);
		URL[] urls = new URL[] { file.toURI().toURL() };

		ClassLoader cl_old = Thread.currentThread().getContextClassLoader();
		@SuppressWarnings("resource")
		ClassLoader ucl = new URLClassLoader(urls, cl_old);
	
		Class<?> clazz = ucl.loadClass(this.name_class.toString());
		Object object =  clazz.newInstance();
		
		//return (DynamicClassInterface) object;
		return object;
	}

	/*
	private File createFileJava() throws IOException {
		
		Random ra=new Random(new Date().getTime());
		this.name_class="source_"+Math.abs(ra.nextInt());
		String java_source=dyn_source.createCompleteJavaClassSource(name_class);
		String name_with_path=path_compiler+this.name_class+".java";
		File javaFile = new File(name_with_path);
		PrintWriter pw = new PrintWriter(new FileWriter(javaFile));
		pw.println(java_source);
		pw.close();
		return javaFile;
	}
	*/
	private synchronized static File createFileJava2(StringBuffer name_classe,
			DynamicSourceInterface dyn_source, String path_compiler)
			throws IOException {

		File javaFile = null;
		String java_source = null;
		String name_cla =null;
		do {
			Random ra = new Random(new Date().getTime());
			name_cla = "source_" + Math.abs(ra.nextInt());
			String name_with_path = path_compiler + name_cla + ".java";
			javaFile = new File(name_with_path);
		} 
		while (javaFile.exists());
		PrintWriter pw = new PrintWriter(new FileWriter(javaFile));
		java_source = dyn_source.createCompleteJavaClassSource(name_cla);
		pw.println(java_source);
		pw.close();
		name_classe.append(name_cla);
		return javaFile;

	}
}
