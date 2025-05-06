package aml2owl.core;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class ModelStringWriter {

	/**
	 * Converts an RDF model to a Turtle string
	 * @param model The Apache Jena {@link Model} object to convert
	 * @return a Turtle-serialized string of the model
	 */
	public static String convertModelToString(Model model) {
		StringWriter writer = new StringWriter();
		try {
			RDFDataMgr.write(writer, model, Lang.TURTLE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String resultString = writer.toString();
		return resultString;
	}
}
