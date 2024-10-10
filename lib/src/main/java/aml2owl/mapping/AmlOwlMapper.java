package aml2owl.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.eclipse.rdf4j.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.Quad;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.AbstractTerm;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;

public class AmlOwlMapper {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	static String mappingDefinition = "/aml2rdf.ttl";

	/**
	 * Maps an AML document into an OWL ontology
	 * 
	 * @param xmlSourceDocument Absolute path to a source document
	 * @return
	 * @throws Exception
	 */
	public Model executeMapping(Path amlSourcePath, String baseIri) throws Exception {

		try {
			InputStream mappingStream = this.getClass().getResourceAsStream(mappingDefinition);

			// Load the mapping in a QuadStore
			QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

			// Get all the triples which contain the rml:source and placeholder as predicate and object, respectively
			Term predicate = new NamedNode("http://semweb.mmlab.be/ns/rml#source");
			Term object = new be.ugent.rml.term.Literal("${AMLFileToMap}");

			List<Quad> sourceQuads = rmlStore.getQuads(null, predicate, object);
			List<Term> subjects = new ArrayList<Term>();
			for (Quad quad : sourceQuads) {
				subjects.add(quad.getSubject());
			}

			rmlStore.removeQuads(null, predicate, object);

			Term newObject = new be.ugent.rml.term.Literal(amlSourcePath.toString());
			for (Term subject : subjects) {
				rmlStore.addQuad(subject, predicate, newObject);
			}

			// Set up the basepath for the records factory, i.e., the basepath for the
			// (local file) data sources
			String parent = amlSourcePath.getParent().toString();
			RecordsFactory factory = new RecordsFactory(parent);

			// Set up the outputstore (needed when you want to output something else than nquads)
			QuadStore outputStore = new RDF4JStore();

			// Create the Executor
			Executor executor = new Executor(rmlStore, factory, outputStore, baseIri, null);

			// Execute the mapping
			QuadStore mappedQuads = executor.execute(null).get(new NamedNode("rmlmapper://default.store"));
			Model extendedModel = extendModelWithSparqlUpdates(mappedQuads);

			// Return updated model
			return extendedModel;
		} catch (Exception e) {
			logger.error("An error happend while executing the RML mapping: " + e.toString());
			throw new Exception("Error while mapping Aml2OWL mapping rules when trying to map " + amlSourcePath.toString() + ".\n" + e.toString()); 
		}
	}
	
	public String executeMappingAndReturnString(Path amlSourcePath, String baseIri) throws Exception {
		Model model = this.executeMapping(amlSourcePath, baseIri);
		String result = this.convertResultToString(model);
		return result;
	}

	
	public Model extendModelWithSparqlUpdates(QuadStore quadStore) {
	    // We need a Jena model for querying. Create default one
	    Model model = ModelFactory.createDefaultModel();

	    // Add all quads from the quadstore (= mapping result) to the Jena model
	    for (Quad quad : quadStore.getQuads(null, null, null)) {
	        // Get the quad subject (from RML) and create a Jena subject
	    	String subject = quad.getSubject().getValue();
	        Resource subjectResource = model.createResource(subject);

	        // Get the quad predicate (from RML) and create a Jena predicate
	        String predicate = quad.getPredicate().getValue();
	        Property predicateProperty = model.createProperty(predicate);
	        
	        // Get the quad (from RML) predicate and create a Jena predicate
	        Term object = quad.getObject();
	        RDFNode newObject;
	        if (object instanceof be.ugent.rml.term.Literal) {
	        	newObject = model.createLiteral(((Term) object).getValue());
	        }
	        else {
	        	newObject = model.createResource(((Term) object).getValue());
	        }

	        model.add(subjectResource, predicateProperty, newObject);
	    }

	    // Load all queries contained in the resources folder
		List<String> queries = new ArrayList<String>();
		try {
			queries = loadAllQueries();
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Execute queries against the Jena model
		for (String query : queries) {
			UpdateRequest updateRequest = UpdateFactory.create(query);
			UpdateAction.execute(updateRequest, model);
		}

       return model;
	}
	
	
	/**
	 * Read content from an input stream as a string
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
    private String readFileAsString(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    
    /**
     * Loads all queries from the queries directory inside the main/src/resources folder
     * @return A list of strings corresponding to the file contents
     * @throws URISyntaxException
     * @throws IOException
     */
    public List<String> loadAllQueries() throws URISyntaxException, IOException {
        final String folderPath = "queries";
        
    	URL directoryUrl = getClass().getClassLoader().getResource("queries");
        if (directoryUrl == null) {
            throw new IllegalArgumentException("Query folder not found: ");
        }

        List<String> queries = new ArrayList<String>();
        
        // There is a difference in loading resource files from the src files (e.g. when testing) and from loading them from the jar
        // The following code handles both cases
        if (directoryUrl != null && directoryUrl.getProtocol().equals("jar")) {
            // If the project was bundled and is executed as a jar
            String jarPath = directoryUrl.getPath().substring(5, directoryUrl.getPath().indexOf("!")); // Get directory path within jar
            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries(); // Iterate over all entries 
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(folderPath + "/") && !entry.isDirectory()) {
                        // Load every file inside the directory
                        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(entryName)) {
                            String content = readFileAsString(inputStream);
                            queries.add(content); // Add file content to the list of query strings
                        }
                    }
                }
            }
        } else if (directoryUrl != null) {
        	// If the resources can be loaded from the file system
            try (Stream<Path> walk = Files.walk(Paths.get(directoryUrl.toURI()))) {
                walk.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            String content = readFileAsString(inputStream);
                            queries.add(content);
                        } catch (IOException e) {
                            throw new RuntimeException("Error reading file: " + path.getFileName(), e);
                        }
                    });
            }
        }
        return queries;
    }
	
	

	
	private String convertResultToString(Model model) {
		Writer sW = new StringWriter();
		try {
			model.write(sW, "turtle");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resultString = sW.toString();
		return resultString;
	}
}
