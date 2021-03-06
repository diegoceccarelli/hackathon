/**
 *  Copyright 2011 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.isti.cnr.hpc.europeana.hackthon.domain;

import java.util.List;

import com.google.gson.Gson;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

import javax.xml.bind.annotation.XmlElement;




/**
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "suggestions")
public class EuropeanaEntity implements Entity{
	
	@XmlElement
	private String name = null;
	@XmlElement
	private List<String> alias  = null;
	@XmlElement
	private String dbpediaLabel  = null;
	@XmlElement
	private String dbpediaUri  = null;
	@XmlElement
	private String dbpediaDescription  = null;
	@XmlElement
	private String freebaseGuid = null;
	@XmlElement
	private String freebaseId = null;
	@XmlElement
	private String freebaseImage = null;
	@XmlElement
	private List<String> europeanaImages = null;
	@XmlElement
	private List<String> types = null;
	@XmlElement
	private String explain = null;
	
	public EuropeanaEntity(){}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the dbpediaDescription
	 */
	public String getDbpediaDescription() {
		return dbpediaDescription;
	}
	/**
	 * @param dbpediaDescription the dbpediaDescription to set
	 */
	public void setDbpediaDescription(String dbpediaDescription) {
		this.dbpediaDescription = dbpediaDescription;
	}
	
	/**
	 * @return the alias
	 */
	public List<String> getAlias() {
		return alias;
	}
	/**
	 * @param alias the alias to set
	 */
	public void setAlias(List<String> alias) {
		this.alias = alias;
	}
	/**
	 * @return the dbpediaLabel
	 */
	public String getDbpediaLabel() {
		return dbpediaLabel;
	}
	/**
	 * @param dbpediaLabel the dbpediaLabel to set
	 */
	public void setDbpediaLabel(String dbpediaLabel) {
		this.dbpediaLabel = dbpediaLabel;
	}
	/**
	 * @return the dbpediaUri
	 */
	public String getDbpediaUri() {
		return dbpediaUri;
	}
	/**
	 * @param dbpediaUri the dbpediaUri to set
	 */
	public void setDbpediaUri(String dbpediaUri) {
		this.dbpediaUri = dbpediaUri;
	}
	/**
	 * @return the freebaseGuid
	 */
	public String getFreebaseGuid() {
		return freebaseGuid;
	}
	/**
	 * @param freebaseGuid the freebaseGuid to set
	 */
	public void setFreebaseGuid(String freebaseGuid) {
		freebaseImage = "http://api.freebase.com/api/trans/image_thumb/guid/"+freebaseGuid.substring(1);
		this.freebaseGuid = freebaseGuid;
	}
	/**
	 * @return the freebaseId
	 */
	public String getFreebaseId() {
		return freebaseId;
	}
	/**
	 * @param freebaseId the freebaseId to set
	 */
	public void setFreebaseId(String freebaseId) {
		this.freebaseId = freebaseId;
	}
	/**
	 * @return the freebaseImage
	 */
	public String getFreebaseImage() {
		return freebaseImage;
	}
	/**
	 * @param freebaseImage the freebaseImage to set
	 */
	public void setFreebaseImage(String freebaseImage) {
		this.freebaseImage = freebaseImage;
	}
	/**
	 * @return the europeanaImages
	 */
	public List<String> getEuropeanaImages() {
		return europeanaImages;
	}
	/**
	 * @param europeanaImages the europeanaImages to set
	 */
	public void setEuropeanaImages(List<String> europeanaImages) {
		this.europeanaImages = europeanaImages;
	}
	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}
	/**
	 * @param types the types to set
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	public String toJson(){
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}
	
	public static EuropeanaEntity fromJson(String json){
		Gson gson = new Gson();
		EuropeanaEntity ee = gson.fromJson(json, EuropeanaEntity.class);
		return ee;
	}
	/**
	 * @param explain
	 */
	public void setExplaination(String explain) {
		String[] terms = explain.split("...");
		explain = explain.replaceAll("..."+terms[1],"<a href='"+terms[1]+"'>link in wikipedia</a>");
		this.explain = explain;
		
	}
	
	public String getExplaination(){
		return explain;
	}
	
	

}
