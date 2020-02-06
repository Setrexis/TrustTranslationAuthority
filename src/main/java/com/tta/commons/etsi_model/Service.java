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
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * Service class of model for testing feeding api of dao module
 * 
 * @param name_ name of the Service
 * @param additionalInfo_ additional information of the service
 * @param serviceInfo_ MAP object containing service information
 * @param levelHM_ MAP object containing all levels associated to the service
 * @param selfContaindedName_ indicates if the name of the service is contained in the name of the scheme
 */
public class Service {

  // it could happen that the name of the service is self-contained in the
  // scheme' name
  // so, to manage this case the "name" property will take the value "--"
  String name;
  HashMap <String, String>serviceInfo = null;
  
  boolean selfContaindedName = false;
  HashMap<String, Level> levelHM;

  /**
   * Constructor
   * @param name name of the Service
   * @param info information of the Service
   * @param levelHM levels associated to the Service
   */
  public Service(String name, Map<String, String> info, Map<String, Level> levelHM) {
    if (name != null && !name.isEmpty())
      this.name = name;
    else {
      // service name is self contained in Scheme' name
      this.selfContaindedName = true;
      this.name = "--";
    }
    this.levelHM = (HashMap<String, Level>) levelHM;
    this.serviceInfo = (HashMap<String, String>) info;
  }
  
  /**
   * Constructor
   * @param jo Description of the service in JSON format
   */
  public Service (JSONObject jo){
    String srvName = jo.getString("name");
    if (srvName != null && !srvName.isEmpty())
      this.name = srvName;
    else{
      // service name is self contained in Scheme' name
    	this.selfContaindedName = true;
      this.name = "--";
    }
    
    JSONObject infoJo = jo.getJSONObject("info");
    String[] keys= JSONObject.getNames(infoJo);
    for (int i=0; i < keys.length; i++){
      this.serviceInfo.put(keys[i], infoJo.getString(keys[i]));
    }
    
    this.levelHM = new HashMap<>();
    JSONArray joa = jo.getJSONArray("levels");
    for (int i=0; i<joa.length(); i++){
      Level level = new Level(joa.getString(i));
      this.levelHM.put(level.getName(), level);
    
    }
  }
  
  /**
   * @return description of the service in JSON format
   */
  public JSONObject toJSON (){
    JSONObject jo = new JSONObject();
    JSONObject jos = new JSONObject();
    if (this.selfContaindedName)
      jo.put("name", "");
    else
      jo.put("name", this.name);
    
    Iterator<Entry<String, String>> it = this.serviceInfo.entrySet().iterator();
    JSONObject infoJo = new JSONObject();
    while (it.hasNext()){
      Entry<String, String> entry = it.next();
      infoJo.put(entry.getKey(), entry.getValue());
    }
    jo.put("info", infoJo);
    
    if (this.levelHM != null){
      Iterator<Entry<String, Level>> itl = this.levelHM.entrySet().iterator();
      JSONArray joa = new JSONArray();
      while (itl.hasNext()){
        joa.put(itl.next().getValue().toJSON());
      }
      jo.put("levels", joa);
    }
    jos.put("service", jo);
    return jos;
  }



  /**
   * @return name
   */
  public String getName() {
    if (this.name.compareTo("--") == 0)
      return "";
    else
      return this.name;
  }

  /**
   * @return MAP object containing all levels associated to the service
   */
  public Map<String,Level> getLevel() {
    return this.levelHM;
  }

  /**
   * @param lv level
   */
  public void setLevel(Map<String,Level> lv) {
    this.levelHM = (HashMap<String, Level>) lv;
  }

  /**
   * @return MAP object containing service information
   */
  public Map<String, String> getServiceInfo() {
    return this.serviceInfo;
  }

  /**
   * @param serviceInfo
   */
  public void setServiceInfo(Map<String, String> serviceInfo) {
    this.serviceInfo = (HashMap<String, String>) serviceInfo;
  }

}
