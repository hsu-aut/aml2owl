package aml2owl.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aml2owl.checking.ShaclValidator;
import aml2owl.core.ModelStringWriter;
import aml2owl.mapping.AmlOwlMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
	name = "AML2OWL CLI", 
	mixinStandardHelpOptions = true,
	subcommands = {CommandLine.HelpCommand.class},
	description = "Map AutomationML files to OWL ontologies and validate these ontologies against SHACL shapes"
)
public class AmlOwlCli {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	String mappingOutputFilename = "MappingOutput.ttl";
	String validationOutputFilename = "ValidationOutput.ttl";

	@Command(name="map", description = "Maps an AutomationML file to an ontology")
	public Model map(
		@Parameters(arity = "1..*", paramLabel = "<amlFile>", description = "File name of the AML file that should be mapped") String amlFile,
		@Parameters(arity = "0..1", paramLabel = "<baseIri>", description = "Explicit baseIri definition for all individuals that are created. Optional, a default one is used if none is given") String baseIri
	) throws Exception {
		// fileName is required, rest is optional
		if (amlFile.isBlank()) {
			logger.error("Missing parameter -aF. Make sure to pass a file name");
			throw new Exception("Missing parameter -aF. Make sure to pass a file name");
		}
		
		logger.info("Started Mapping Process...");
		Path amlFilePath = Path.of(amlFile);
		logger.info("fileName: " + amlFilePath);
		AmlOwlMapper mapper = new AmlOwlMapper();

		Model mappedOntology = null;
		try {
			mappedOntology = mapper.executeMapping(amlFilePath, baseIri);
			String result = ModelStringWriter.convertModelToString(mappedOntology);
			writeFile(result, mappingOutputFilename);
			logger.info("Completed Mapping and wrote result to " + mappingOutputFilename);
			return mappedOntology;
		} catch (Exception e) {
			logger.error("Could not complete mapping. " + e.toString());
			e.printStackTrace();
		}
		return mappedOntology;
	}
	
	
	@Command(name="map-and-validate", description = "Maps an AutomationML file to an ontology and validates the ontology against a SHACL file.")
	public void mapAndValidate(
		@Parameters(arity = "1", paramLabel = "<amlFile>", description = "File path of the AML file that should be mapped") String amlFile,
		@Parameters(arity = "1", paramLabel = "<shaclFile>", description = "File path of the SHACL file with constraints against the AML model") String shaclFile,
		@Parameters(arity = "0..1", paramLabel = "<baseIri>", description = "Explicit baseIri definition for all individuals that are created. Optional, a default one is used if none is given") String baseIri
	) throws Exception {
		// First step: Map the AML file
		Model mappedOntology = this.map(amlFile, baseIri);
		
		// Second step: Get a SHACL validator and run the validation 
		logger.info("Running SHACL validation...");
		ShaclValidator checker = new ShaclValidator();
		Path shaclPath = Paths.get(shaclFile);
		Model validationResultModel = checker.checkConformance(mappedOntology, shaclPath);
		
		String validationResultString = ModelStringWriter.convertModelToString(validationResultModel); 
		writeFile(validationResultString, validationOutputFilename);
		logger.info("Validation completed. Wrote results to " + validationOutputFilename);
	}
	
	
	public static void main(String[] args) {
		int exitCode = new CommandLine(new AmlOwlCli()).execute(args);
		System.exit(exitCode);
	}

	
	/**
	 * Utility function to write the mapped model to a file
	 * 
	 * @param mappedModel String containing the complete AML ontology
	 * @param filePath    Path to the file that will be created
	 */
	private void writeFile(String mappedModel, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			writer.write(mappedModel);
			writer.close();
		} catch (IOException e) {
			logger.error("Error while writing the file");
			e.printStackTrace();
		}
	}

}