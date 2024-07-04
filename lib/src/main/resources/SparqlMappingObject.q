PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX : <https://w3id.org/i40/aml#>

INSERT {
  ?ieAttr :hasMappingObject ?rrAttr .
} 
WHERE { 
  ?ie a :InternalElement .
  ?ieAttr a :Attribute .
  ?rrAttr a :Attribute .
  ?rr rdfs:subClassOf :RoleClass .
  ?ie :hasAttribute ?ieAttr .
  ?ieAttr :hasName ?ieAttrName .
  ?ie :hasRoleRequirement ?rr .
  ?rr :hasAttribute ?rrAttr .
  ?rrAttr :hasName ?rrAttrName .
  FILTER(?ieAttrName = ?rrAttrName)
};

INSERT {
  ?ieIF :hasMappingObject ?rrIF .
} 
WHERE { 
  ?ie a :InternalElement .
  ?ieIF a :ExternalInterface .
  ?rrIF a :ExternalInterface .
  ?rr rdfs:subClassOf :RoleClass .
  ?ie :hasInterface ?ieIF .
  ?ieIF :hasName ?ieIFName .
  ?ie :hasRoleRequirement ?rr .
  ?rr :hasInterface ?rrIF .
  ?rrIF :hasName ?rrIFName .
  FILTER(?ieIFName = ?rrIFName)
}