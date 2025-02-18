package aml2owl.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aml2owl.mapping.AmlOwlMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "AML2OWL CLI", mixinStandardHelpOptions = true)
public class AmlOwlCli implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	String outputFilename = "MappingOutput.ttl";

	@Option(names = { "-f", "--filename" }, description = "File name of the AML file that should be mapped")
	String fileName = "";

	@Option(names = { "-bI", "--baseIri" }, description = "Explicit baseIri definition for all individuals that are created. Optional, a default one is used if none is given")
	String baseIri = "";

	@Override
	public void run() {
		// fileName is required, rest is optional
		if (fileName.isBlank()) {
			logger.error("Missing parameter -f. Make sure to pass a file name");
			return;
		}
		
		logger.info("Started Mapping Process...");
		Path amlFilePath = Path.of(fileName);
		logger.info("fileName: " + amlFilePath);
		AmlOwlMapper mapper = new AmlOwlMapper();

		try {
			String result = mapper.executeMappingAndReturnString(amlFilePath, baseIri);
			writeFile(result, outputFilename);
			logger.info("Completed Mapping");
		} catch (Exception e) {
			logger.error("Could not complete mapping. " + e.toString());
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		int exitCode = new CommandLine(new AmlOwlCli()).execute(args);
		System.exit(exitCode);
	}

	/**
	 * Writes the mapped model to a file
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