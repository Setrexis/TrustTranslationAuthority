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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Params class of model for TTA api of dao module
 * 
 * @param paramsMap MAP object containing all the parameter of a Trust Scheme with their values. 
 *
 */
public class Params {
	Logger logger = Logger.getLogger(Params.class);
	
	private HashMap<String,String> paramMap =null;
	
	/**
	 * Constructor
	 */
	public Params(){
		paramMap = new HashMap<>();
	}
	
	/**
	 * 
	 * Constructor
	 * @param pmap
	 */
	public Params(Map<String,String>pmap) {
		this.paramMap = (HashMap<String, String>) pmap;
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public void addParam (String name, String value) {
		paramMap.put(name, value);
	}
	
	/**
	 * @param name
	 * @return value
	 */
	public String getParamValue (String name) {
		return paramMap.get(name);
	}
	
	/**
	 * @return MAP containing all parameters of the Trust Scheme
	 */
	public Map<String,String> getParamMap(){
		return paramMap;
	}
	
	/**
	 * Compares two Params object
	 * @param pm
	 * @return True if both Maps contains the same parameters with the same value. False any other case.
	 */
	public boolean compare (Params pm) {
		boolean r= false;
		int i = paramMap.size();
		int j = pm.paramMap.size();
		
		if ((paramMap.size() == pm.paramMap.size())&& paramMap.size() == 0) {
			r = true;
		}else if (paramMap.size() == pm.paramMap.size()){
			try {
				Set<Entry<String,String>> plset = paramMap.entrySet();
				Iterator<Entry<String, String>> it = plset.iterator();
				do{
					Entry<String, String> et = it.next();
					String k = et.getKey();
					String v = et.getValue();
					String fv = pm.getParamValue(k);
					if (fv == null || !v.equalsIgnoreCase(fv)) {
						r = false;
						break;
					}else {
						r = true;
					}

				}while (it.hasNext());
			}catch (Exception e) {
				logger.error(e,e);
				r = false;
			}
		}else {
			r=false;
		}
		return r;
	}
	
	/**
	 * @return a JSON object with the representation of the params
	 */
	public JSONArray toJSON() {
		JSONArray ja = null;
		
		if (paramMap!= null && paramMap.size()>0) {
			ja = new JSONArray();
			
			Iterator<String> it = paramMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = paramMap.get(key);
				JSONObject jo = new JSONObject();
				jo.put(key, value);
				ja.put(jo);
			}
		}
		return ja;
	}
	

}
