package aml2owl.core;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;

public class ShaclReportUtil {

	private static final String SHACL_NS = "http://www.w3.org/ns/shacl#";
	private static final Property SH_CONFORMS = ResourceFactory.createProperty(SHACL_NS, "conforms");

	/**
	 * Returns true if the given SHACL validation report model indicates conformance.
	 *
	 * @param reportModel the Jena Model containing a SHACL validation report
	 * @return true if sh:conforms is true on the report resource, false otherwise
	 */
	public static boolean isConforming(Model reportModel) {
		 // Create a typed literal “true” in the SHACL namespace
	    Literal trueLiteral = ResourceFactory.createTypedLiteral(true);

	    // Check directly if there exists any triple (?s sh:conforms true)
	    return reportModel.contains(null, SH_CONFORMS, trueLiteral);
	}

}
