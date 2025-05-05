package aml2owl.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import aml2owl.checking.ShaclValidator;
import aml2owl.core.ModelStringWriter;
import aml2owl.core.ResourceLoader;
import aml2owl.core.ShaclReportUtil;
import aml2owl.mapping.AmlOwlMapper;

public class BaseStructureTest {

	// File path to the AML file used for all tests
	Path amlFilePath = Paths.get("src", "test", "resources", "base-structure", "base-structure.aml").toAbsolutePath();

	/**
	 * Test the mapper, convert the result with an expected model
	 * @throws Exception
	 */
	@Test
	void testBaseStructureMapping() throws Exception {
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model actualModel = mapper.executeMapping(amlFilePath, null);
		
		Path expectedModelPath = Paths.get("base-structure", "base-structure.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}
	
	
	/**
	 * Test a more or less useless shape just to see that errors can be detected and correct reports are generated
	 * @throws Exception
	 */
	@Test
	void testBaseStructureNegativeValidation() throws Exception {
		ShaclValidator validator = new ShaclValidator();
		Path shapePath = Paths.get("base-structure", "base-structure_negative shape.ttl");
		Model report = validator.mapAndCheckConformance(amlFilePath, shapePath);

		Boolean conforms = ShaclReportUtil.isConforming(report);
		assertFalse(conforms);
		
		Path expectedReportPath = Paths.get("base-structure", "base-structure_negative result.ttl");
		Model expectedReport = ResourceLoader.loadResourceAsModel(expectedReportPath);
		
		assertTrue(report.isIsomorphicWith(expectedReport));
	}
	
	
	/**
	 * Simply check one class' individual for its hasName property value
	 * @throws Exception
	 */
	@Test
	void testBaseStructurePositiveValidation() throws Exception {
		ShaclValidator validator = new ShaclValidator();
		Path shapePath = Paths.get("base-structure", "base-structure_positive shape.ttl");
		Model report = validator.mapAndCheckConformance(amlFilePath, shapePath);

		Boolean conforms = ShaclReportUtil.isConforming(report);
		assertTrue(conforms);
	}
	
}
