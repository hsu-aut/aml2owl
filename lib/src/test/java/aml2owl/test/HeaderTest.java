package aml2owl.test;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import aml2owl.core.ResourceLoader;
import aml2owl.mapping.AmlOwlMapper;

public class HeaderTest {

	@Test
	void testBaseHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "base-header.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel = mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("header", "base-header.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
	
	@Test
	void testAdditionalHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "additional-header.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel = mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("header", "additional-header.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
	
	@Test
	void testExternalReference() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "external-reference.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel= mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("header", "external-reference.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
	
	@Test
	void testOriginInfo() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "origin-info.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel= mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("header", "origin-info.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
}
