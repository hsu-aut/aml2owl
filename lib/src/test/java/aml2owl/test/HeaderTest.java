package aml2owl.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import aml2owl.mapping.AmlOwlMapper;

public class HeaderTest {

	@Test
	void testBaseHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "base-header.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "header", "base-header.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
	@Test
	void testAdditionalHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "additional-header.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "header", "additional-header.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
	@Test
	void testExternalReference() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "external-reference.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "header", "external-reference.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
	
	@Test
	void testOriginInfo() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "header", "origin-info.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "header", "origin-info.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
}
