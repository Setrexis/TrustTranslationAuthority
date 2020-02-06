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

package com.tta.commons.connectors.istorage.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;

import com.tta.commons.conf.Configuration;
import com.tta.commons.connectors.istorage.IConnector;
import com.tta.commons.cte.OutputMsgs;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.error.CallResult;
import com.tta.commons.model.ModelParser;
import com.tta.commons.model.TranslationAgreement;
import com.tta.manager.Manager;



/**
 * 
 * Implementation of a connector to use file system as storage of agreements. Specific for TTA module
 * the data stored are agreements in json format as they are received in createAgreement method of the API.
 * The aim of storing this info is to be able to restore it on shut-downs or crashes.
 * 
 */
public class FileS implements IConnector{
	Logger logger = Logger.getLogger(IConnector.class.getName());
	
	
	
	 /**
	   * Initialize the tta compoment by loading into memory all stored agreements
	   */
	@Override
	public boolean initialize() {
		boolean r = false;
		
		String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
		File f = new File (dir);
		File[] fileArray = f.listFiles();
		
		for (int i=0; i<fileArray.length; i++) {
			if (fileArray[i].isFile() && fileArray[i].getName().endsWith("json")) {
				try(FileReader fr = new FileReader(fileArray[i])){
					BufferedReader br = new BufferedReader(fr);
					StringBuilder sb = new StringBuilder();
					String line;
					while((line = br.readLine()) != null) {
						sb.append(line+"\n");
					}
					
					
					JSONObject jo = new JSONObject(sb.toString());
					
					Manager manager = new Manager();
				    manager.createAgreement(jo, true);
					
				}catch (Exception e) {
					logger.error("error trying to read file:"+ fileArray[i].getAbsolutePath());
					logger.error(e,e);
				}
				
				
			}
		}
		
		return r;
	}
	
	/**
	   * Retrieve agreements whose name contains the value in val paremeter
	   * @param val Value to filter names
	   * @return List of agreements
	   */
	@Override
	public List<Document> getAgreementsWithValcontainedInName( String val) {
		String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
		File f = new File (dir);
		ArrayList<Document> docArray = new ArrayList<>();
		
		File[] fileArray = f.listFiles();
		for (int i=0; i<fileArray.length ; i++) {
			if (fileArray[i].getName().toLowerCase().endsWith("json") &&
				fileArray[i].getName().toLowerCase().contains(val)) {
					Document doc = Document.parse(readFile(fileArray[i]));
					docArray.add(doc);
					
				
			}
		}
		
		return docArray;
	}

	
	  /**
	   * Retrieve an agreement whose name is equal to val paremeter
	   * @param val Value to filter name
	   * @return List of agreements
	   */
	@Override
	public JSONObject getAgreementByNameEqualsTo( String val) {
		String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
		File f = new File (dir);
		Document doc = null;
		
		File[] fileArray = f.listFiles();
		for (int i=0; i<fileArray.length ; i++) {
			doc = Document.parse(readFile(fileArray[i]));
			Document agrDoc = (Document) doc.get("agreement");
			JSONObject jo = new JSONObject (agrDoc.toJson());
			
		    String name = jo.getString("name");
			if (name.equalsIgnoreCase(val)) {
				return jo;
			}
		}
		
		JSONObject jo = new JSONObject();
		return jo;
	}

	 /**
	  *  add an agreement
	   * @param agr Agreement object
	   * @param agreement Agreement description in JSON format
	   * @return true if the agreement has been stored, false in case of error (see log file)
	   */
	@Override
	public CallResult addAgreement(TranslationAgreement agr, JSONObject agreement) {
		String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
		
		
		try (FileWriter file = new FileWriter(dir+"/"+agr.getName()+".json")) {
			
			file.write(agreement.toString());

			logger.debug("Successfully Copied JSON Object to File...");
			logger.debug("\nJSON Object: " + agreement);
			return new CallResult(true,"");
			
		}catch(Exception e) {
			logger.error(OutputMsgs.CREATE_T_ERRER_INTERNAL_REPO);
			logger.error(e,e);
			//return new CallResult(false,e.getMessage());
			return new CallResult(false,OutputMsgs.CREATE_T_ERRER_INTERNAL_REPO);
		}
		
	}

	

	/**
	 *   delete an agreement
	   * @param name Name of the agreement to be deleted from DB
	   * @return true if OK, true in case of error
	   */
	@Override
	public CallResult deleteAgreementByName(String name) {
		try {
			String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
			File f = new File (dir);
			
			File[] fileArray = f.listFiles();
			for (int i=0; i<fileArray.length ; i++) {
				if (fileArray[i].getName().toLowerCase().replace(".json", "").equalsIgnoreCase(name)) {
					
					Files.delete(fileArray[i].toPath());
					
				}
			}
			return new CallResult(true,"");
		}catch(Exception e) {
			logger.error(OutputMsgs.DELETE_T_ERRER_INTERNAL_REPO);
			logger.error(e,e);
			//return new CallResult(false,e.getMessage());
			return new CallResult(false,OutputMsgs.DELETE_T_ERRER_INTERNAL_REPO);
		}
		
	}

	
	/**
	 * retrieve all agreements
	   * @return A MAP object containing all agreements stored into de DataBase
	   */
	@Override
	public Map<String, TranslationAgreement> getAllAgreements() {
		String dir = Configuration.getConfiguration().getProperty(PropertyNames.FILE_STORAGE_PATH);
		File f = new File (dir);
		
		HashMap<String,TranslationAgreement> agrHM = new HashMap<>();
		
		File[] fileArray = f.listFiles();
		for (int i=0; i<fileArray.length ; i++) {
			 String jsonS = readFile (fileArray[i]);
			 TranslationAgreement agr = ModelParser.parseAgreement(new JSONObject (jsonS));
			 if (agr != null)
				 agrHM.put(agr.getName(), agr);
				
		}
		
		
		return agrHM;
	}
	
	
	/**
	 * read a file to String
	   * @return an string with the content of the file
	   */
	private String readFile (File f) {
		try (FileReader fr = new FileReader(f);
			 BufferedReader reader = new BufferedReader(fr)	){
			
			
		    String         line = null;
		    StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");

		        while((line = reader.readLine()) != null) {
		            stringBuilder.append(line);
		            stringBuilder.append(ls);
		        }
		        
		        return stringBuilder.toString();
		}catch(Exception e) {
			logger.error(e,e);
		}
		return null;
	}

	@Override
	public CallResult addAgreement(String dbName, String collName, TranslationAgreement agr, JSONObject agreement) {
		return null;
	}

	

}
