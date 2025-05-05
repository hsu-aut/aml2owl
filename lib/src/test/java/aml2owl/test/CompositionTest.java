package aml2owl.test;

import aml2owl.core.ResourceLoader;
import aml2owl.mapping.AmlOwlMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertTrue;

public class CompositionTest {

	@Test
	void testSucComposition() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "system-unit-classes", "suc-composition.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		Model mappedModel= mapper.executeMapping(mappingPath, null);
		
		Path expectedModelPath = Paths.get("system-unit-classes", "suc-composition.ttl");
		Model expectedModel = ResourceLoader.loadResourceAsModel(expectedModelPath);
		
		assertTrue(mappedModel.isIsomorphicWith(expectedModel));
	}
	
}

