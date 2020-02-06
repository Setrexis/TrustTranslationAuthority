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


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tta.commons.connectors.istorage.db.TestMongoConnector;




/**
 * Test class to test the model developed to parse  Trusted Sevice List files from https://webgate.ec.europa.eu/tl-browser/#/
 * according to ETSI TS 102 231
 *
 */

public class TestProviderFeedingApi {

  Logger logger = Logger.getLogger(TestProviderFeedingApi.class);
  
  
  
  
  /**
   * load and parse TSL files contained in:
   * 
   * @param commandDetails:
   *            path: path where files are hosted
   *            schemePatter: set this param to "etsi"
   * @return boolean
   */

  public boolean loadSchemes(String commandDetails){
    JSONObject jo = new JSONObject(commandDetails);
    String path = jo.getString("path");
    String type = jo.getString("schemePattern");
    boolean r = false;
    
    File file = new File (path);
    File[] files = file.listFiles();
    for (int i=0; i<files.length; i++){
      if ((files[i].isFile()) && (files[i].getPath().endsWith(".xml"))){
          TrustServiceList sch = SchemeFactory.getScheme(files[i],type);
          if (sch != null){
            TestMongoConnector mc = new TestMongoConnector();
            mc.addScheme(sch.toJSON());
            r = true;
          }else{
            break;
          }
        }
      }
    
    
    return r;
  }
  
  /**
   * @return list of names of TSLs stored
   */
 
  public String getSchemeNames(){
    TestMongoConnector mc = new TestMongoConnector();
    HashMap<String, TrustServiceList> schHM = (HashMap<String, TrustServiceList>) mc.getAllSchemes();
    Iterator<String> it = schHM.keySet().iterator();
    JSONObject jo = new JSONObject();
    JSONArray ja = new JSONArray();
    
    while (it.hasNext()){
      ja.put(it.next());
      
    }
    jo.put("schemeNames", ja);
    return jo.toString();
    
  }
  
  
  /**
   * 
   * @param commandDetails:
   *                  name: name of TSL
   * @return all the details of a TSL
   */
 public String getSchemeByName(String commandDetails){
    JSONObject jo = new JSONObject(commandDetails);
    String name = jo.getString("name");
    
    TestMongoConnector mc = new TestMongoConnector();
    ArrayList<Document> ald = (ArrayList<Document>) mc.getSchemeByValEqualsTo("name", name);
    
    Document doc = ald.get(0);
    String s = (String) doc.get("scheme");
    jo = new JSONObject(s);
    
       
    return jo.toString();
    
    
  }
  
  /**
   * @param commandDetails:
   *                 name: name of the TSL to remove
   * @return http result code
   */
  public boolean removeScheme(String commandDetails){
    JSONObject jo = new JSONObject(commandDetails);
    String schemeName = jo.getString("schemeName");
    
    boolean r = false;
    
    
    TestMongoConnector mc = new TestMongoConnector();
    
    if ( mc.deleteSchemeByName(schemeName))
      r = true;
    
    return r;
      
    
  }
    

}
