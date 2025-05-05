package aml2owl.checking;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.validation.ValidationUtil;

import aml2owl.core.ResourceLoader;
import aml2owl.mapping.AmlOwlMapper;

public class ShaclValidator {

	
	 /**
     * Validates the RDF data model located at the given path against the SHACL shapes model at the specified path.
     *
     * @param modelPath the filesystem path to the data model (in Turtle format)
     * @param shapePath the filesystem path to the SHACL shapes model (in Turtle format)
     * @return a String representation of the SHACL validation report
     * @throws IOException if either model cannot be loaded from the provided paths
     */
	public Model checkConformance(Path modelPath, Path shapePath) throws IOException {
		// Load the main data model and the model with the SHACL shapes
	    Model dataModel = ResourceLoader.loadResourceAsModel(modelPath);
	    Model shapeModel = ResourceLoader.loadResourceAsModel(shapePath);
	    
	    Model result = this.checkConformance(dataModel, shapeModel);
	    return result;
	}
	
	
	 /**
     * Validates the provided RDF data model against the SHACL shapes model located at the specified path.
     *
     * @param dataModel the Jena Model representing the data to validate
     * @param shapePath the filesystem path to the SHACL shapes model (in Turtle format)
     * @return a String representation of the SHACL validation report
     * @throws IOException if the shapes model cannot be loaded from the provided path
     */
	public Model checkConformance(Model dataModel, Path shapePath) throws IOException {
		// Load the model with the SHACL shapes from a path
	    Model shapeModel = ResourceLoader.loadResourceAsModel(shapePath);
	    
	    Model result = this.checkConformance(dataModel, shapeModel);
	    return result;
	}
	
	 /**
     * Validates the provided RDF data model against the provided SHACL shapes model.
     *
     * @param dataModel the Jena Model representing the data to validate
     * @param shapeModel the Jena Model containing the SHACL shapes definitions
     * @return a String representation of the SHACL validation report
     */
	public Model checkConformance(Model dataModel, Model shapeModel) {
		// Perform the validation of everything, using the data model (= mapped from AML) and the SHACL shapes model
        Resource result = ValidationUtil.validateModel(dataModel, shapeModel, true);
        
	    // Get and return report
        Model resultModel = result.getModel();
	    return resultModel;
	}
	
	public Model mapAndCheckConformance(Path amlFilePath, Path shapePath) throws Exception {
		// Load the main data model and the model with the SHACL shapes
	    AmlOwlMapper mapper = new AmlOwlMapper();
	    Model mappingResult = mapper.executeMapping(amlFilePath, null);
	    Model shapeModel = ResourceLoader.loadResourceAsModel(shapePath);
	    
	    Model result = this.checkConformance(mappingResult, shapeModel);
	    return result;
	}

}
