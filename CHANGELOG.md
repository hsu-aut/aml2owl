# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2026-01-05

### Breaking Changes

#### Human-Readable URIs
**BREAKING:** All generated URIs now use human-readable `/` characters instead of URL-encoded `%2F`.

**Impact:**
- Existing SPARQL queries that reference URIs with `%2F` will no longer match
- Triple stores containing data from version 2.x will have different URIs than data from version 3.x
- String-based URI comparisons and filtering will break
- Applications with hardcoded URI references need to be updated

**Migration:**
- Update SPARQL queries: Replace `%2F` with `/` in all URI patterns
- For existing triple stores, you can migrate data using SPARQL UPDATE:
  ```sparql
  DELETE { ?s ?p ?o }
  INSERT { ?s_new ?p_new ?o_new }
  WHERE {
    ?s ?p ?o
    BIND(IRI(REPLACE(STR(?s), "%2F", "/")) AS ?s_new)
    BIND(IRI(REPLACE(STR(?p), "%2F", "/")) AS ?p_new)
    BIND(IF(ISIRI(?o), IRI(REPLACE(STR(?o), "%2F", "/")), ?o) AS ?o_new)
  }
  ```

**Example:**
- Old: `<https://w3id.org/hsu-aut/AutomationML#AutomationMLBaseRoleClassLib%2FAutomationMLBaseRole%2FProcess>`
- New: `<https://w3id.org/hsu-aut/AutomationML#AutomationMLBaseRoleClassLib/AutomationMLBaseRole/Process>`

### Fixed

#### Conditional RML Mappings for Missing Attributes
- Fixed missing `rdf:type` assignments when optional XML attributes are absent
- Added conditional mappings using RML filters for `@RefAttributeType`, `@RefBaseSystemUnitPath`, `@RefBaseClassPath`
- Ensures proper type assignments only when attributes exist and are non-empty
- Prevents RML mapper errors when processing AML files with missing optional attributes

### Changed

#### Build System
- Updated to Java 11 compatibility (previously used Java 17+ features)
- Fixed Maven coordinates for proper deployment

## [2.0.0] - Previous Release

(Earlier version history not documented)
