PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX : <https://w3id.org/i40/aml#>

INSERT { ?rcChild :hasAttribute ?parentAttr . }
WHERE { 
    ?rcChild rdfs:subClassOf :RoleClass.
    ?rcParent rdfs:subClassOf :RoleClass.
	#Inheritance over multiple Levels
	?rcChild :hasRefBaseClass* ?rcParent. 
	?rcParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.

    # Ensure ?rcChild does not already have an attribute with the same name as ?parentAttr
	# catches Attributes that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?rcChild :hasAttribute ?existingAttr .
        ?existingAttr :hasName ?attrName .
        ?parentAttr :hasName ?attrName .
    }	
};

INSERT { ?icChild :hasAttribute ?parentAttr . }
WHERE { 
    ?icChild rdfs:subClassOf :InterfaceClass.
    ?icParent rdfs:subClassOf :InterfaceClass.
	#Inheritance over multiple Levels    
	?icChild :hasRefBaseClass* ?icParent.
	?icParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.

    # Ensure ?icChild does not already have an attribute with the same name as ?parentAttr
	# catches Attributes that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?icChild :hasAttribute ?existingAttr .
        ?existingAttr :hasName ?attrName .
        ?parentAttr :hasName ?attrName .
    }	    
};

INSERT { ?atChild :hasAttribute ?parentAttr . }
WHERE { 
    ?atChild rdfs:subClassOf :AttributeType.
    ?atParent rdfs:subClassOf :AttributeType.
	#Inheritance over multiple Levels    
	?atChild :hasRefAttributeType* ?atParent.
	?atParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.

    # Ensure ?atChild does not already have an attribute with the same name as ?parentAttr
	# catches Attributes that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?atChild :hasAttribute ?existingAttr .
        ?existingAttr :hasName ?attrName .
        ?parentAttr :hasName ?attrName .
    }	    
};

INSERT { ?sucChild :hasAttribute ?parentAttr . }
WHERE { 
    ?sucChild a :SystemUnitClass.
    ?sucParent a :SystemUnitClass.
	#Inheritance over multiple Levels    
	?sucChild :hasRefBaseClass* ?sucParent.
	?sucParent :hasAttribute ?parentAttr.
	?parentAttr a :Attribute.

    # Ensure ?icChild does not already have an attribute with the same name as ?parentAttr
	# catches Attributes that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?sucChild :hasAttribute ?existingAttr .
        ?existingAttr :hasName ?attrName .
        ?parentAttr :hasName ?attrName .
    }	    
}