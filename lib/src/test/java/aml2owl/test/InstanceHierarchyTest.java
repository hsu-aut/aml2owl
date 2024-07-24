package aml2owl.test;

import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import aml2owl.mapping.AmlOwlMapper;

public class InstanceHierarchyTest {
	@Test
	void testBaseHeader() throws Exception {
		Path mappingPath = Paths.get("src", "test", "resources", "instance-hierarchy", "single-IE.aml").toAbsolutePath();
		AmlOwlMapper mapper = new AmlOwlMapper();
		String actualResult = mapper.executeMappingAndReturnString(mappingPath, null);
		
		byte[] fileFromBytes = Files.readAllBytes(Paths.get("src", "test", "resources", "instance-hierarchy", "single-IE.ttl"));
		String expectedResult = new String(fileFromBytes, StandardCharsets.UTF_8);
		
		assertThat(actualResult).isEqualToIgnoringWhitespace(expectedResult);
	}
}
