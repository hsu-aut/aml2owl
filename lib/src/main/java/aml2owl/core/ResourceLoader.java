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

	public static Model loadResourceAsModel(Path modelPath) throws IOException {
		Model model = JenaUtil.createMemoryModel();
		InputStream input = loadResource(modelPath);
		model.read(input, "urn:dummy", FileUtils.langTurtle);
		return model;
	}
	
	public static InputStream loadResource(Path modelPath) throws IOException {
	    // First try: Load via classpath (for tests or bundled resources)
	    String resourceName = "/" + modelPath.toString().replace(File.separatorChar, '/');
	    InputStream in = ResourceLoader.class.getResourceAsStream(resourceName);
	    if (in != null) {
	        return in;
	    }

	    // Second try. Fallback: file system (for all files, e.g., via CLI)
	    if (Files.exists(modelPath)) {
	        return Files.newInputStream(modelPath);
	    }

	    // Last resort: Throw error, if both options don't work
	    throw new FileNotFoundException("Resource not found on classpath or filesystem: " + modelPath);
	}
	
	public static InputStream loadResource(String modelPathString) throws IOException {
		Path modelPath = Paths.get(modelPathString);
		return loadResource(modelPath);
	}
	
}
