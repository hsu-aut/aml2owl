PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX : <https://w3id.org/hsu-aut/AutomationML#>

INSERT { ?rcChild :hasAttribute ?parentAttr . }
WHERE { 
    ?rcChild rdf:type :RoleClass.
    ?rcParent rdf:type :RoleClass.
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
    ?icChild rdf:type :InterfaceClass.
    ?icParent rdf:type :InterfaceClass.
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
    ?atChild rdf:type :AttributeType.
    ?atParent rdf:type :AttributeType.
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
    ?sucChild rdf:type :SystemUnitClass.
    ?sucParent rdf:type :SystemUnitClass.
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