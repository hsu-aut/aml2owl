[![Java CI with Maven](https://github.com/hsu-aut/aml2owl/actions/workflows/mvn-test.yml/badge.svg?branch=master)](https://github.com/hsu-aut/aml2owl/actions/workflows/mvn-test.yml)
# Aml2Owl

## Motivation
AutomationML cannot represent the semantics that many Best Practice Recommendations (BPR) and Application Recommendations (AR) implicitly require. In order to explicitly represent these semantics, we created a mapping from AutomationML to OWL.

## Installation & How to Use
Go to the releases section on the right and download the latest version. You can then simply run the mapper from a shell using:
```
java -jar Aml2Owl-<version>.jar -f "<path to your AML file>"
```

Make sure to replace `<version>` and `<path to your AML file>` using the version you downloaded (e.g. 1.0.0) and a path to the AML file you want to map.

## Tests
We're extending our test collection to make sure our mapper can robustly handle all subtleties of AutomationML
