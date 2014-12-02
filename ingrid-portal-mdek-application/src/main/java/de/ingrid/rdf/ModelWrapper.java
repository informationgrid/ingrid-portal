package de.ingrid.rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelWrapper {
    private Model model;
    
    private String id;
    
    public ModelWrapper(Model model, String id) {
        this.setModel(model);
        this.setId(id);
    }
    
    public Resource getResource() {
        return this.model.getResource(this.id);
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
