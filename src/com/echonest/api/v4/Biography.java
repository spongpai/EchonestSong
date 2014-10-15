package com.echonest.api.v4;

import java.util.Map;

import org.json.simple.JSONObject;

public class Biography extends WebDocument {

	@SuppressWarnings("unchecked")
	Biography(Map map)  {
		super("biography", map);
	}

	public JSONObject toJSON(){
		JSONObject jo = new JSONObject();
		jo.put("site", this.getSite());
		jo.put("text", this.getText());
		jo.put("url", this.getURL());
		
		JSONObject li = new JSONObject();
		li.put("type", this.getLicenseType());
		li.put("attribution", this.getLicenseAttribution());
		
		jo.put("license", li);
		
		return jo;
	}
	
	/**
	 * Gets the name of the site that is the source of the biography
	 * 
	 * @return the site
	 */
	public String getSite() {
		return getString("site");
	}

	/**
	 * Gets the bio text
	 * 
	 * @return the text of the biography
	 */
	public String getText() {
		return getString("text");
	}

	/**
	 * gets the URL for the source of the biography
	 * 
	 * @return the url
	 */
	public String getURL() {
		return getString("url");
	}

	/**
	 * Gets the license type for the bio
	 * 
	 * @return the license type
	 */
	public String getLicenseType() {
		return getString("license.type");
	}

	/**
	 * Gets the attribution for the bio
	 * 
	 * @return the attribution
	 */
	public String getLicenseAttribution() {
		return getString("license.attribution");
	}
}
