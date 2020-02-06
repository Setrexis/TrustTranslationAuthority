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
 * Provider class of model for testing feeding api of dao module
 * 
 * @param providerName_ name of the Provider
 * @param providerURI_ URI of the Provider
 * @param serviceHM_ MAP of services provided by this Provider
 */
public class Provider {

  private String providerName;
  private String providerURI;
  
  private HashMap<String, Service> serviceHM;
  
  public Provider(String name, String uri, Map<String, Service> serviceHM){
    this.providerName = name;
    this.providerURI = uri;
    this.serviceHM = (HashMap<String, Service>) serviceHM;
  }
  
  /**
   * Constructor
   * @param jo JSON object describing the provider
   */
  public Provider (JSONObject jo){
    providerName = jo.getString("service-provider-name");
    providerURI = jo.getString("service-provider-URI");
    
    JSONArray joa = jo.getJSONArray("services");
    for (int i=0; i<joa.length(); i++){
      Service srv = new Service (joa.getJSONObject(i));
      serviceHM.put(srv.getName(), srv);
    }
  }
  
  
  /**
   * @return JSON object describing the Provider
   */
  public JSONObject toJSON (){
    JSONObject jo = new JSONObject();
    
    jo.put("service-provider-name", providerName);
    jo.put("service-provider-URI", providerURI);
    
    Iterator<Entry <String, Service>> it = serviceHM.entrySet().iterator();
    JSONArray joa = new JSONArray();
    while(it.hasNext()){
      Entry<String, Service> entry = it.next();
      joa.put(entry.getValue().toJSON());
    }
   
    jo.put("services",joa);
    JSONObject jop = new JSONObject();
    jop.append("provider", jo);
    return jop;
  }

  /**
   * @return name of the Provider
   */
  public String getProviderName() {
    return providerName;
  }
  
  /**
   * @return MAP object containing all services provided by this Provider
   */
  public  Map<String, Service> getServiceHM(){
    return serviceHM;
  }
  
}
