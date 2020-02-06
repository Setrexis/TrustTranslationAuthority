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
 * TrustServiceList class of model for testing feeding api of dao module
 * 
 * @param name_ name of the TrustServiceList
 * @param desc_ description of the TrustServiceList
 * @param providerHM_ MAP of Providers provided by a Trust Service List
 */
public class TrustServiceList {

  String name;
  String desc;
  HashMap<String, Provider> providerHM;
  
  

  /**
   * Constructor
   * @param name Name of the TSL
   * @param desc Description of the TSL
   * @param provider MAP object containing all Providers into the TSL
   */
  public TrustServiceList(String name, String desc,  Map<String, Provider>  provider){ 
    this.name = name;
    this.desc = desc;
    this.providerHM = (HashMap<String, Provider>) provider;
  }
  
  /**
   * Constructor
   * @param jo Trust Service List described in JSON format
   */
  public TrustServiceList (JSONObject jo){
    this.name = jo.getString("name");
    this.desc = jo.getString("description");
    this.providerHM = new HashMap<>();
    
    JSONArray joa = jo.getJSONArray("providers");
    for (int i=0; i<joa.length(); i++){
      Provider prv = new Provider (joa.getJSONObject(i));
      this.providerHM.put(prv.getProviderName(), prv);
    }
        
  }
  
  /**
   * @return Trust Service List into JSON format
   */
  public JSONObject toJSON (){
    JSONObject jo = new JSONObject();
    jo.put("name", this.name);
    jo.put("description", this.desc);
    
    Iterator<Entry <String, Provider>> it = this.providerHM.entrySet().iterator();
    JSONArray joa = new JSONArray();
    while(it.hasNext()){
      Entry<String, Provider> entry = it.next();
      joa.put(entry.getValue().toJSON());
    }
    
    jo.put("providers",joa);
    return jo;
    

    
  }



  /**
   * @return name of the TSL
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return MAP object containing all Providers into the TSL
   */
  public Map<String,Provider> getProvider() {
    return this.providerHM;
  }

  /**
   * @param providerHM_
   */
  public void setProvider(Map<String,Provider> providerHM) {
    this.providerHM = (HashMap<String, Provider>) providerHM;
  }

}
