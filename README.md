[![Java CI with Maven](https://github.com/hsu-aut/aml2owl/actions/workflows/mvn-test.yml/badge.svg?branch=master)](https://github.com/hsu-aut/aml2owl/actions/workflows/mvn-test.yml)
# Aml2Owl - Mapping & Validation

## Motivation
AutomationML cannot represent the semantics that many Best Practice Recommendations (BPR) and Application Recommendations (AR) implicitly require. In order to explicitly represent these semantics, AML2OWL provides an automated mapping from AutomationML to OWL using the [RML mapping language](https://rml.io/specs/rml/).
Based on this generated ontology of an AML file, formal constraint validations can be made using [SHACL](https://www.w3.org/TR/shacl/).

## How to Use

### As a Command-Line Tool
Go to the releases section on the right and download the jar in the latest version. You can then simply run the application from a shell using:
```
java -jar Aml2Owl-<version>.jar <command> <arguments>
```

Make sure to replace `<version>` and `<command>` and `<arguments>` with the version you downloaded (e.g. 1.0.0) one of the two commands below and the corresponding arguments, respectively.

The following two commands are provided:
* `map`: Automatically maps a given AML file into an ontology. The `map` command requires one parameter: a path to an AutomationML file to be mapped. A complete command for v2.0.0 looks like this: `java -jar .\Aml2Owl-2.0.0.jar map ".\test-file.aml"`. If successfully executed, this command will produce an ontology stored as a Turtle file with file name `MappingOutput.ttl` located right next to the jar.    
* `map-and-validate`: Automatically maps a given AML file into an ontology and validates the result against a given SHACL file. The `map-and-validate` command requires one parameter: a path to an AutomationML file to be mapped. A complete command for v2.0.0 looks like this: `java -jar .\Aml2Owl-2.0.0.jar map-and-validate ".\test-file.aml" ".\test-shapes.ttl"`. In this case, `test-file.aml` is mapped and validated against all SHACL shapes contained in `test-shapes.tll`. If successfully executed, this command will produce an SHACL validation report stored as a Turtle file with file name `ValidationOutput.ttl` located right next to the jar.

### As a dependency
With Maven, it's very easy to use this library in your own projects. Releases are published to the Maven Central Repo. Just add this dependency to your pom.xml:

```xml
<dependency>
	<groupId>io.github.aljoshakoecher</groupId>
	<artifactId>io.github.aljoshakoecher.aml2owl-lib</artifactId>
	<version>2.0.0</version>
</dependency>
```

With this dependency, you can use the mapper and validator in your own code:

```java
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.jena.rdf.model.Model;
import aml2owl.checking.ShaclValidator;

// Get a mapper instance
AmlOwlMapper mapper = new AmlOwlMapper();
Path amlFilePath = Paths.get("amlFile.aml");

// Map an AML file and get an ontology instance (Apache Jena Model)
Model mappedModel = mapper.executeMapping(amlFilePath, null);
```

The resulting `mappedModel` is an ontology conforming to the mapping specification.

```java
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.jena.rdf.model.Model;
import aml2owl.checking.ShaclValidator;

// Get a validator instance
ShaclValidator validator = new ShaclValidator();
Path amlFilePath = Paths.get("amlFile.aml");
Path shapePath = Paths.get("shaclShapeFile.ttl");

// Map an AML file and validate the resulting ontology instance against a file containing SHACL shapes
Model report = validator.mapAndCheckConformance(amlFilePath, shapePath);
```
The resulting `report` is a SHACL report object. If you want to quickly test whether the AML model conforms with all shapes, you can use the utility function `isConforming(model)` of the `ShaclReportUtil` class. 

```java`
Boolean conforms = ShaclReportUtil.isConforming(report);
```

## Tests
Please have a look at the tests located [here](https://github.com/hsu-aut/aml2owl/tree/master/lib/src/test) if you need more examples. We're extending our test collection to make sure our mapper can robustly handle all subtleties of AutomationML. So please contact us if you have interesting test cases you want to include.

