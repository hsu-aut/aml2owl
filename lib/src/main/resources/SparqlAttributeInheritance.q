PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX : <https://w3id.org/i40/aml#>

INSERT { ?rcChild :hasAttribute ?parentAttr . }
WHERE { 
    ?rcChild rdfs:subClassOf :RoleClass.
    ?rcParent rdfs:subClassOf :RoleClass.
	?rcChild :hasRefBaseClass ?rcParent.
	?rcParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.
};

INSERT { ?icChild :hasAttribute ?parentAttr . }
WHERE { 
    ?icChild rdfs:subClassOf :InterfaceClass.
    ?icParent rdfs:subClassOf :InterfaceClass.
	?icChild :hasRefBaseClass ?icParent.
	?icParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.
}