package aml2owl.test;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;

import aml2owl.core.ResourceLoader;
import aml2owl.mapping.AmlOwlMapper;

public class InstanceHierarchyTest {
	@Test
	void testBaseHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "instance-hierarchy", "single-IE.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel= mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("instance-hierarchy", "single-IE.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
}
