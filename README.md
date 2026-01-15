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
	<version>3.0.0</version>
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


## Mapping Specification

### RML Mapping Rules
The complete mapping specification from AutomationML to OWL is defined using [RML (RDF Mapping Language)](https://rml.io/specs/rml/). The mapping rules can be found at:

[`lib/src/main/resources/aml2rdf.ttl`](lib/src/main/resources/aml2rdf.ttl)

This file defines how AutomationML XML elements are transformed into OWL 2 DL RDF triples. The initial RML mapping is supplemented with SPARQL UPDATE queries that perform additional transformations (e.g., inheritance expansion). These SPARQL queries are located at:

[`lib/src/main/resources/queries/`](lib/src/main/resources/queries/)

### Semantic Model

The mapping follows these semantic conventions:

**Classes (TBox)**:
- `SystemUnitClass`, `RoleClass`, `InterfaceClass`, `AttributeType` → Mapped to OWL Classes
- Class hierarchies are preserved using `rdfs:subClassOf`
- Base class references (e.g., `@RefBaseClassPath`) create subclass relationships

**Individuals (ABox)**:
- `InternalElement` → Mapped to OWL Individuals (instances)
- `ExternalInterface` → Mapped to OWL Individuals
- `Attribute` instances → Mapped to OWL Individuals
- Instances are typed according to their class references (e.g., `@RefBaseSystemUnitPath`)

**URIs**:
- All generated URIs use human-readable `/` separators instead of URL-encoded `%2F`
- URI construction varies by element type:
  - **Instance elements** (`InternalElement`, `ExternalInterface`): URIs use the `@ID` attribute, mimicking AutomationML's reference mechanism
    - Example: `https://w3id.org/hsu-aut/AutomationML#{@ID}`
  - **Class definitions** (`RoleClass`, `SystemUnitClass`, `InterfaceClass`, `AttributeType`): URIs use hierarchical paths constructed from `@Name` attributes
    - Example: `https://w3id.org/hsu-aut/AutomationML#AutomationMLBaseRoleClassLib/AutomationMLBaseRole/Process`
  - **Attribute instances**: URIs use hierarchical paths constructed from `@Name` attributes
    - Example: `https://w3id.org/hsu-aut/AutomationML#ParentElement/ChildAttribute/NestedAttribute`

**Ontology Import**:
- Each mapped AML file imports the latest AutomationML ontology: `https://w3id.org/hsu-aut/AutomationML`

### OWL 2 DL Profile
The generated RDF conforms to the **OWL 2 DL** profile, enabling use with DL reasoners such as HermiT, Pellet, and FaCT++. Users can expect:
- Type inference based on class hierarchies
- Automatic classification of individuals
- Consistency checking
- Standard OWL 2 DL reasoning capabilities

**Note**: Some AutomationML constructs use punning (same URI as both class and individual), which is valid in OWL 2 DL.

### Mapping Coverage

The mapping covers the core AutomationML specification elements:

**Fully Supported**:
- Core structure: `CAEXFile`, `InstanceHierarchy`, `InternalElement`
- Composition and nesting: Nested `InternalElement` hierarchies via `aml:hasPart`, nested `Attribute` hierarchies, nested `ExternalInterface`
- Header information: `SourceDocumentInformation`, `ExternalReference`, project metadata
- All class libraries: `SystemUnitClassLib`, `RoleClassLib`, `InterfaceClassLib`, `AttributeTypeLib`
- Class hierarchies: Class definitions with `@RefBaseClassPath` creating `rdfs:subClassOf` relationships
- Role requirements: `RoleRequirements` with `@RefBaseRoleClassPath` mapped via `aml:hasRoleRequirement`
- Interfaces: `ExternalInterface` with optional `@RefBaseClassPath` typing
- Attributes: Full attribute support including nested attributes, data types, units, values, constraints
- Attribute constraints: `NominalScaledType`, `OrdinalScaledType`, `UnknownType`
- InternalLinks: `InternalLink` with partner references and direct `aml:isLinkedTo` relationships
- Special patterns: Mirror/Master objects, Groups, Facets
- Mapping objects: `AttributeNameMapping`, `InterfaceIDMapping` within `RoleRequirements/MappingObject`
- Conditional mappings: `@RefBaseSystemUnitPath`, `@RefAttributeType`, `SupportedRoleClass`

### SPARQL Post-Processing

After the initial RML mapping, three SPARQL UPDATE queries expand the model with derived information:

1. **Attribute Inheritance** (`SparqlAttributeInheritance.q`):
   - Propagates attributes from parent classes to child classes across multi-level hierarchies
   - Applies to `RoleClass`, `InterfaceClass`, `AttributeType`, and `SystemUnitClass`
   - Respects attribute overriding: inherited attributes are only added if not already defined with the same name
   - Uses transitive closure (`hasRefBaseClass*`) to handle inheritance chains

2. **Interface Inheritance** (`SparqlInterfaceInheritance.q`):
   - Propagates interfaces from parent classes to child classes across multi-level hierarchies
   - Applies to `RoleClass`, `InterfaceClass`, and `SystemUnitClass`
   - Respects interface overriding: inherited interfaces are only added if not already defined with the same name
   - Uses transitive closure (`hasRefBaseClass*`) to handle inheritance chains

3. **Mapping Object Resolution** (`SparqlMappingObject.q`):
   - Creates direct `hasMappingObject` links between `InternalElement` attributes/interfaces and their corresponding `RoleClass` attributes/interfaces
   - Matches based on name equality between the internal element's features and the role requirement's features
   - Complements the explicit mapping objects defined in `RoleRequirements/MappingObject`

### Known Limitations

1. **External File References**: References to external AML files (via `ExternalReference/@Path`) are not currently resolved or imported. The external reference is recorded, but the mapper does not follow the reference to include content from external files in the generated ontology.
2. **Revision History**: Change tracking and revision history elements are not currently mapped. Future versions could use PROV-O for provenance tracking.

## Tests
Please have a look at the tests located [here](https://github.com/hsu-aut/aml2owl/tree/master/lib/src/test) if you need more examples. We're extending our test collection to make sure our mapper can robustly handle all subtleties of AutomationML. Please contact us if you have interesting test cases you want to include.

