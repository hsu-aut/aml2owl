package aml2owl.mapping;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.Quad;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.Literal;
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
	public QuadStore executeMapping(Path amlSourcePath, String baseIri) throws Exception {

		try {
			InputStream mappingStream = this.getClass().getResourceAsStream(mappingDefinition);

			// Load the mapping in a QuadStore
			QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

			// Get all the triples which contain the rml:source and placeholder as predicate and object, respectively
			Term predicate = new NamedNode("http://semweb.mmlab.be/ns/rml#source");
			Term object = new Literal("${AMLFileToMap}");

			List<Quad> sourceQuads = rmlStore.getQuads(null, predicate, object);
			List<Term> subjects = new ArrayList<Term>();
			for (Quad quad : sourceQuads) {
				subjects.add(quad.getSubject());
			}

			rmlStore.removeQuads(null, predicate, object);

			Term newObject = new Literal(amlSourcePath.toString());
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
			
			QuadStore updatedQuads = extendMappingResult(mappedQuads);

			// Return the quads
			return updatedQuads;
		} catch (Exception e) {
			logger.error("An error happend while executing the RML mapping: " + e.toString());
			throw new Exception("Error while mapping Aml2OWL mapping rules.\n" + e.toString()); 
		}
	}
	
	public String executeMappingAndReturnString(Path amlSourcePath, String baseIri) throws Exception {
		QuadStore quads = this.executeMapping(amlSourcePath, baseIri);
		String result = this.convertResultToString(quads);
		return result;
	}


	private QuadStore extendMappingResult(QuadStore mappedQuads) {
		// Create a Jena Model from the mappedQuads
        Model model = ModelFactory.createDefaultModel();
        DatasetGraph datasetGraph = DatasetGraphFactory.create();
        for (be.ugent.rml.store.Quad quad : mappedQuads.getQuads(null, null, null)) {
        	Node subjectNode = NodeFactory.createURI(quad.getSubject().getValue());
        	Node predicateNode = NodeFactory.createURI(quad.getPredicate().getValue());
        	Node objectNode = NodeFactory.createURI(quad.getObject().getValue());
        	
        	datasetGraph.add(null, subjectNode, predicateNode, objectNode);
        }
        
        Dataset dataset = DatasetFactory.wrap(datasetGraph);

        // Apply SPARQL INSERT Queries
        String sparqlUpdate = "INSERT DATA { GRAPH <http://example.org> { <http://example.org/subject> <http://example.org/predicate> <http://example.org/object> . } }";
        UpdateRequest updateRequest = UpdateFactory.create(sparqlUpdate);

        dataset.begin(ReadWrite.WRITE);
        try {
            UpdateAction.execute(updateRequest, dataset);
            dataset.commit();
        } finally {
            dataset.end();
        }

        // Convert back to QuadStore if needed
        QuadStore updatedQuads = new RDF4JStore();

        // Iterate over the quads in the DatasetGraph and add them to the QuadStore
        datasetGraph.find().forEachRemaining(quad -> {
        		NamedNode s = new NamedNode(quad.getSubject().getURI());
        		NamedNode p = new NamedNode(quad.getPredicate().getURI());
        		NamedNode o = new NamedNode(quad.getObject().getURI());
            				
        		updatedQuads.addQuad(
                    new be.ugent.rml.store.Quad(
                            null,
                            s,
                            p,
                            o
                    )
            );
        });
        return updatedQuads;
	}
	
	private String convertResultToString(QuadStore resultQuads) {
		Writer sW = new StringWriter();
		try {
			resultQuads.write(sW, "turtle");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resultString = sW.toString();
		return resultString;
	}
}
