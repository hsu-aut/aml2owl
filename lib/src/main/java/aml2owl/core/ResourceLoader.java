package aml2owl.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.util.JenaUtil;

public class ResourceLoader {

	/**
	 * Loads an Apache Jena {@link Model} from a file
	 * @param modelPath {@link Path} to the model to load
	 * @return A {@link Model} instance 
	 * @throws IOException In cases the file cannot be found
	 */
	public static Model loadResourceAsModel(Path modelPath) throws IOException {
		Model model = JenaUtil.createMemoryModel();
		InputStream input = loadResource(modelPath);
		model.read(input, "urn:dummy", FileUtils.langTurtle);
		return model;
	}
	
	/**
	 * Loads the content of a file as an {@link InputStream}
	 * @param path {@link Path} to the file to load
	 * @return An {@link InputStream} instance
	 * @throws IOException In cases the file cannot be found
	 */
	public static InputStream loadResource(Path path) throws IOException {
	    // First try: Load via classpath (for tests or bundled resources)
	    String resourceName = "/" + path.toString().replace(File.separatorChar, '/');
	    InputStream in = ResourceLoader.class.getResourceAsStream(resourceName);
	    if (in != null) {
	        return in;
	    }

	    // Second try. Fallback: file system (for all files, e.g., via CLI)
	    if (Files.exists(path)) {
	        return Files.newInputStream(path);
	    }

	    // Last resort: Throw error, if both options don't work
	    throw new FileNotFoundException("Resource not found on classpath or filesystem: " + path);
	}
	
	/**
	 * Loads the content of a file as an {@link InputStream}
	 * @param pathString {@link String} to the file to load
	 * @return An {@link InputStream} instance
	 * @throws IOException In cases the file cannot be found
	 */
	public static InputStream loadResource(String pathString) throws IOException {
		Path modelPath = Paths.get(pathString);
		return loadResource(modelPath);
	}
	
}
