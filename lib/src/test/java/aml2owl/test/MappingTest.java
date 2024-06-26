package aml2owl.test;

import aml2owl.mapping.AmlOwlMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class MappingTest {

	@Test
	void testSucComposition() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "system-unit-classes", "suc-composition.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "system-unit-classes", "suc-composition.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
	
	@Test
	void testMappingObject() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "mapping-object", "mapping-object.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "mapping-object", "mapping-object.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
	
	@Test
	void testMirror() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "mirror", "mirror.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "mirror", "mirror.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
}

