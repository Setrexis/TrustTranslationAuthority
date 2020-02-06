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

package com.tta.commons.etsi_model;


import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;



/**
 * Level class of model for testing feeding api of dao module
 * 
 * @param name_ name of the level
 * @param attMap_ MAP object containing the attributes associated to the value
 */
public class Level {

  Logger logger = Logger.getLogger(this.getClass());
  
  String name;
  HashMap<String, Attribute> attMap;

  /**
   * Constructor
   * @param name
   */
  public Level(String name) {
    this.name = name;
    attMap = new HashMap<>();
  }

  /**
   * Constructor
   * @param jo Level description in JSON format
   */
  public Level (JSONObject jo){
		try{
			name = jo.getString("name");
			
			JSONArray joa = jo.getJSONArray("atributes");
			if (joa != null && joa.length()>0){
				
				attMap = new HashMap<>();
				
				for (int i=0; i<joa.length(); i++){
					JSONObject attJO = joa.getJSONObject(i);
					Attribute at = Attribute.createAttribute(attJO);
					if (at != null)
					  attMap.put(at.name, at);
				}
			}
		}catch (Exception e){
		  logger.error(e,e);
		}
  }
  
  /**
   * @return Level in JSON format
   */
  public JSONObject toJSON (){
    JSONObject jos = new JSONObject();
    JSONObject jo = new JSONObject();
    jo.put("name", name);
    
    if (attMap.size()>0){
      JSONArray ja = new JSONArray();
      Iterator<String> it = attMap.keySet().iterator();
      while (it.hasNext()){
        JSONObject aJo = attMap.get(it.next()).toJSON();
        ja.put(aJo);
      }
      jo.put("attributes", ja);
    }
    jos.put("level", jo);
    return jos;
  }
	

  /**
   * Compares this level with the one passed as parameter
   * @param lv
   * @return true if both levels has same names and attributes, false in any other case
   */
  public boolean compare(Level lv) {
    boolean r = true;
    if (lv.name.compareTo(this.name) == 0) {
      Iterator<String> it = attMap.keySet().iterator();

      while (it.hasNext() && r) {
        String n = it.next();
        Attribute at = lv.getAtribute(n);
        if (at != null) {
          if (!at.compare(attMap.get(n))) {
            r = false;
          }
        } else {
          r = false;
        }
      }
    } else {
      r = false;
    }
    return r;
  }

  /**
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return level name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Add an attribute to the level
   * @param att
   */
  public void setAtribute(Attribute att) {
    attMap.put(att.name, att);
  }

  /**
   * @param name
   * @return Attribute object with the name equals to the one passed as parameter
   */
  public Attribute getAtribute(String name) {
    return attMap.get(name);
  }
}
