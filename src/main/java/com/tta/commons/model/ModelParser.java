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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tta.commons.cte.Status;


/**
 * Parser for the  TTA model 
*/
public class ModelParser {

	 private static final String PARAM = "params";
	 
	 private ModelParser () {}
	 
  /**
   * @param jo Agreement description in JSON format
   * @return agreement object
   */
  public static TranslationAgreement parseAgreement (JSONObject jos){
	  
	 
    
    Logger logger = Logger.getLogger("ModelParser.class");
    String name;
    TrustScheme source;
    TrustScheme target;
    Date creationDate=null;
    Date leavingDate=null;
    Date activationDate=null;
    int duration = 0; 
    Status status;
    String dateNote = null;
    
    JSONObject jo = jos.getJSONObject("agreement");
    try {
    	name = jo.getString("name");
    }catch(Exception e) {
    	logger.error("name param not found");
    	logger.error(e,e);
    	return null;
    }
    status = Status.valueOf(jo.getString("status").toUpperCase());
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    try {
      creationDate = format.parse(jo.getString("creation-date"));
      leavingDate = format.parse(jo.getString("leaving-date"));
      activationDate = format.parse(jo.getString("activation-date"));
    } catch (JSONException | ParseException e) {
      logger.error (e,e);
      return null;
    }
    
    if (creationDate.compareTo(leavingDate) >=0) {
    	logger.error("Creation date must be sonner than Leaving date");
    	dateNote = "Creation date must be sonner than Leaving date";
    	
    }
    if (activationDate.compareTo(creationDate)<0) {
    	logger.error("activation date must later than or equal to creation date");
    	dateNote = "activation date must later than or equal to creation date";
    }
    if (activationDate.compareTo(leavingDate)>=0) {
    	logger.error("activationdate must be sonner than leaving date");
    	dateNote = "activationdate must be sonner than leaving date";
    }
    
    String level;
    String provider;
    
    JSONObject sourceJO;
    String oName;
    try {
    sourceJO = jo.getJSONObject("source");
    oName = sourceJO.getString("name");
    }catch(Exception e) {
    	logger.error(e,e);
    	return null;
    }
    try{
      level = sourceJO.getString("level");
    }catch (Exception e){
      level = "";
    }
    
    try {
      provider = sourceJO.getString("provider");
    }catch (Exception e){
      provider = "";
    }
    
    HashMap<String,String> pH=null;
    if (sourceJO.has(PARAM)) {
	    JSONArray ja = sourceJO.getJSONArray(PARAM);
	    pH = new HashMap<>();
	    for (int i =0; i<ja.length(); i++){
	      JSONObject jop = ja.getJSONObject(i);
	      String pname = jop.getString("name");
	      String pval = jop.getString("value");
	      pH.put(pname, pval);
	    }
    }
    
    source = new TrustScheme(oName, level, provider, pH);
    
    JSONObject targetJO;
    String tName;
    try {
    	targetJO = jo.getJSONObject("target");
    	tName = targetJO.getString("name");
    }catch(Exception e) {
    	logger.error(e,e);
    	return null;
    }
    try{
      level = targetJO.getString("level");
    }catch (Exception e){
      level = "";
    }
    
    try {
      provider = targetJO.getString("provider");
    }catch (Exception e){
      provider = "";
    }
    
    
    if (targetJO.has(PARAM)) {
	    JSONArray ja = targetJO.getJSONArray(PARAM);
	    pH = new HashMap<>();
	    for (int i =0; i<ja.length(); i++){
	      JSONObject jop = ja.getJSONObject(i);
	      String pname = jop.getString("name");
	      String pval = jop.getString("value");
	      pH.put(pname, pval);
	    }
    }else
    	pH=null;
    
    target = new TrustScheme(tName, level, provider, pH);
    
    TranslationAgreement agr = new TranslationAgreement(name, creationDate, leavingDate, activationDate, duration, status,dateNote);
    agr.setSource(source);
    agr.setTarget(target);
    
    return agr;
  }
}
