package de.ingrid.mdek.beans;

import java.util.ArrayList;
import java.util.List;
public class ProfileBean {
    private String name; 
    private List<String> languages = new ArrayList<String>();
    private List<Rubric> rubrics = new ArrayList<Rubric>();
    private String namespace;
    private String version;
    
    public ProfileBean() {
        languages.add("en");
        languages.add("de");
    }
    
    public void setRubrics(List<Rubric> rubrics) {this.rubrics = rubrics;}
    public List<Rubric> getRubrics() {return this.rubrics;}
    
    public void setLanguages(List<String> languages) {this.languages = languages;}
    public List<String> getLanguages() {return this.languages;}

    public void setName(String name) {this.name = name;}
    public String getName() {return name;}

    public void setNamespace(String namespace) {this.namespace = namespace;}
    public String getNamespace() {return namespace;}

    public void setVersion(String version) {this.version = version;}
    public String getVersion() {return version;}
}