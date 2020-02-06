/**
 *
 *LIGHTest Trust Translation Authority
 *Copyright © 2018 Atos Spain SA
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.tta.commons.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;


/**
 * TrustScheme class of model for TTA api
 * 
 * @param name_
 *            Name
 * @param level_
 *            Level
 * @param provider_
 *            Provider
 * 
 * @param pList_
 *            MAP object to host all parameters associated
 * @param trustR_
 *            relations of trust of this subject with others
 */
public class TrustScheme extends TrustRelations{

	private String name;
	private String level;
	private String provider;

	private Params params;
	
	private String tplFileFullName;
	private String xmlFileFullName;

	
	/**
	 * @param name
	 * @param level
	 * @param provider
	 * @param pList
	 */
	public TrustScheme(String name, String level, String provider, Map<String, String> pList) {
		super();
		
		this.name = name;
		this.level = level;
		this.provider = provider;
		this.params = new Params(pList);
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name_
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return level
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * @param level_
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return provider
	 */
	public String getProvider() {
		return this.provider;
	}

	/**
	 * @param provider
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * @param name
	 * @return value associated to the param name
	 */
	public String getParamValue(String name) {
		return params.getParamValue(name);
	}

	/**
	 * @return Params
	 */
	public Set<Entry<String, String>> getParamList() {
		if (params.getParamMap() != null)
			return params.getParamMap().entrySet();
		else
			return null;
	}


	/**
	 * @param sot
	 * @return true if both subjects have all their properties with the same values
	 */
	public boolean isEqualTo(TrustScheme ts) {
		
		try {
			/*
			 * check main features of a subject of trust
			 */
			if (!this.name.equals(ts.getName()) ||
				!this.level.equals(ts.getLevel()) ||
				!this.provider.equals(ts.getProvider())){
				
				return false;
			}

			if ((params.getParamMap() == null) && (ts.params.getParamMap() == null)) {
				return true;
			}else {
				return params.compare(ts.params);
			}
		} catch (Exception e) {
			return false;
		}
		
	}

	/**
	 * @return name of the file when TTA is set to produce a file containing all the translations agreements in which the trust scheme participates
	 */
	public String getTplFileFullName() {
		return tplFileFullName;
	}

	/**
	 * @param tplFileFullName name of the file when TTA is set to produce a file containing all the translations agreements in which the trust scheme participates
	 */
	public void setTplFileFullName(String tplFileFullName) {
		this.tplFileFullName = tplFileFullName;
	}

	/**
	 * @return name of the file when TTA is set to produce a file containing all the translations agreements in which the trust scheme participates
	 */
	public String getXmlFileFullName() {
		return xmlFileFullName;
	}

	/**
	 * @param xmlFileFullName name of the file when TTA is set to produce a file containing all the translations agreements in which the trust scheme participates
	 */
	public void setXmlFileFullName(String xmlFileFullName) {
		this.xmlFileFullName = xmlFileFullName;
	}
	
	/**
	 * @return JSONobject with the description of the trust scheme
	 */
	public JSONObject toJSON() {
		JSONObject jo = new JSONObject();
		jo.put("name", name);
		if (level != null && !level.isEmpty())
			jo.put("level", level);
		if (provider != null && !provider.isEmpty())
			jo.put("provider", provider);
		if (params.getParamMap() != null)
			jo.put("params", params.toJSON());
		
		return jo;
	}
}
