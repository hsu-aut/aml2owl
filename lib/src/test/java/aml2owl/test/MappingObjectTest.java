package aml2owl.test;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import aml2owl.core.ResourceLoader;
import aml2owl.mapping.AmlOwlMapper;

public class MappingObjectTest {
	
	@Test
	void testMappingObject() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "mapping-object", "mapping-object.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel= mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("mapping-object", "mapping-object.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
	
}
