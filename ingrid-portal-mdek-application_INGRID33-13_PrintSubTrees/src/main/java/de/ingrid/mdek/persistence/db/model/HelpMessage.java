package de.ingrid.mdek.persistence.db.model;

import de.ingrid.mdek.services.persistence.db.IEntity;

public class HelpMessage implements IEntity {

	private Long id;
	private int version;
	private Integer guiId;
	private Integer entityClass;
	private String language;
	private String name;
	private String helpText;
	private String sample;


	public HelpMessage() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getGuiId() {
		return guiId;
	}

	public void setGuiId(Integer guiId) {
		this.guiId = guiId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public String getSample() {
		return sample;
	}

	public void setSample(String sample) {
		this.sample = sample;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Integer getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Integer entityClass) {
		this.entityClass = entityClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}