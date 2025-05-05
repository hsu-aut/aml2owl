package aml2owl.core;

import java.io.StringWriter;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class ModelStringWriter {

	public static String convertModelToString(Model model) {
		StringWriter writer = new StringWriter();
		try {
			RDFDataMgr.write(writer, model, Lang.TURTLE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resultString = writer.toString();
		return resultString;
	}
}
