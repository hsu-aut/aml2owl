PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX : <https://w3id.org/hsu-aut/AutomationML#>

INSERT { ?rcChild :hasInterface ?parentInterface . }
WHERE { 
    ?rcChild rdf:type :RoleClass.
    ?rcParent rdf:type :RoleClass.
	#Inheritance over multiple Levels
	?rcChild :hasRefBaseClass* ?rcParent. 
	?rcParent :hasInterface ?parentInterface.
	?parentInterface a :ExternalInterface.

    # Ensure ?rcChild does not already have an Interface with the same name as ?parentInterface
	# catches Interfaces that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?rcChild :hasInterface ?existingInterface .
        ?existingInterface :hasName ?interFaceName .
        ?parentInterface :hasName ?interFaceName .
    }	
};

INSERT { ?icChild :hasInterface ?parentInterface . }
WHERE { 
    ?icChild rdf:type :InterfaceClass.
    ?icParent rdf:type :InterfaceClass.
	#Inheritance over multiple Levels    
	?icChild :hasRefBaseClass* ?icParent.
	?icParent :hasInterface ?parentInterface.
	?parentInterface a :ExternalInterface.

    # Ensure ?icChild does not already have an Interface with the same name as ?parentInterface
	# catches Interfaces that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?icChild :hasInterface ?existingInterface .
        ?existingInterface :hasName ?interFaceName .
        ?parentInterface :hasName ?interFaceName .
    }	    
};

INSERT { ?sucChild :hasInterface ?parentInterface . }
WHERE { 
    ?sucChild rdf:type :SystemUnitClass.
    ?sucParent rdf:type :SystemUnitClass.
	#Inheritance over multiple Levels    
	?sucChild :hasRefBaseClass* ?sucParent.
	?sucParent :hasInterface ?parentInterface.
	?parentInterface a :Interface.

    # Ensure ?icChild does not already have an interface with the same name as ?parentInterface
	# catches Interfaces that were inherited, but later overwritten 
    FILTER NOT EXISTS {
        ?sucChild :hasInterface ?existingInterface .
        ?existingInterface :hasName ?interFaceName .
        ?parentInterface :hasName ?interFaceName .
    }	    
}