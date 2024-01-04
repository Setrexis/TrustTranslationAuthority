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

package com.tta.commons.conf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;



/**
 * 
 * class to manage the configuration of TTA modules
 * 
 * class properties:
 *        name_: name of the configuration file
 *        conf_: single tone configuration object
 *        prop_: properties container
 *
 */
public class Configuration {
  
  static Logger logger_= Logger.getLogger(Configuration.class);
  
  
  static Configuration conf_= null;
  Properties prop_ = null;
  
  /**
   * private constructor
 * @throws IOException 
   */
  private Configuration(){
	  
    try (InputStream input = Files.newInputStream(Paths.get("/usr/local/tomcat/conf/ttaFM.properties"))){
      
      prop_ = new Properties();
      prop_.load(input);
      
    } catch (Exception e) {
      logger_.error (e,e);
      
    } 
    
  }
  
  
  /**
   * retrieve the configuration manager object
   */
  public static Configuration getConfiguration() {
	  if (conf_ == null)
		  conf_ = new Configuration();
	  
	  return conf_;
  }
  
  /**
   * reload configuration file
   */
  public static void reload(){
    conf_ = null;
    
    
    conf_ = new Configuration();
  }
  
  
  /**
   * @param name Name of the parameter to retrieve
   * @return value of the parameter
   */
  public String getProperty (String name){
    String v = null;
    
    
    try {
    	v = prop_.getProperty(name);
    }catch(Exception e) {
    	logger_.error("error fetching: "+ name);
    	logger_.error(e,e);
    }
    return v;
  }
  
  /**
 * @param name
 * @param value
 * @return true if the configuration parameter exists and have been updated, false in other cases.
 */
public boolean setProperty (String name, String value) {
	  if ((name != null) && (!name.isEmpty())&&(value!=null)&&(!value.isEmpty())) {
		  prop_.setProperty(name, value);
		  try (OutputStream out = Files.newOutputStream(Paths.get("/usr/local/tomcat/conf/ttaFM.properties"))){
			  prop_.store(out, null);
			out.flush();
			
		  }catch(Exception e) {
			  logger_.error(e,e);
			  return false;
		  }
		  return true;
	  }
	  return false;
  }

	/**
	 * @return list of all configuration paramenter with their values in JSON format
	 */
	public String getProperties () {
		Set<String>  set = prop_.stringPropertyNames();
		JSONArray ja = new JSONArray();
		for (String s : set) {
			String v = prop_.getProperty(s);
			if (v!=null) {
				JSONObject jo = new JSONObject();
				jo.put(s, v);
				ja.put(jo);
			}
		}
		JSONObject mainJO = new JSONObject();
		mainJO.put("tta-configuration", ja);
		return mainJO.toString();
	}

}
