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

package com.tta.manager;


import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tta.commons.conf.Configuration;
import com.tta.commons.connectors.dns.DnsConnector;
import com.tta.commons.connectors.istorage.IConnector;
import com.tta.commons.connectors.istorage.InternalStorageConnectorFactory;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.error.CallResult;
import com.tta.commons.model.ModelParser;
import com.tta.commons.model.TTModel;
import com.tta.commons.model.TTModelST;
import com.tta.commons.model.TranslationAgreement;
import com.tta.file_manager.FileManagerFactory;
import com.tta.file_manager.IFileManager;


/**
 * REST API manage data of dao
 */

public class Manager {

	Logger logger = Logger.getLogger(Manager.class);




	/**
	 * create a new agreement or declaration of translation
	 * @param agreementDetails description of the translation in JSON format as String
	 * @param recovery false value indicates the agreement comes from the interface and it has to be loaded into memory and files created
	 *                 true indicates the agreement comes from recovery file and only has to be loaded into memory 
	 * @return boolean
	 */

	public CallResult createAgreement (String agreementDetails, boolean recovery)
	{
		JSONObject jo = new JSONObject(agreementDetails);
		return createAgreement (jo,recovery);
	}

	/**
	 * create a new agreement or declaration of translation
	 * @param jo description of the translation in JSON format 
	 * @param recovery false value indicates the agreement comes from the interface and it has to be loaded into memory and files created
	 *                 true indicates the agreement comes from recovery file and only has to be loaded into memory 
	 * @return CallResult object
	 */

	public CallResult createAgreement (JSONObject jo, boolean recovery) {

		TranslationAgreement agr = null;
		
		
		//parse agreement details
		agr = ModelParser.parseAgreement(jo);
		if (agr == null) {
			return new CallResult(false, "Error parsing agreement details, some of the data is not present or has incorrect format.");
		}else if (agr.getDateNote() != null) {
			return new CallResult (false, agr.getDateNote());
		}
		
			

		//create internal agreement strucutre
		TTModel ttModel = TTModelST.getModel();
		if (!ttModel.addAgreement(agr)){
			return new CallResult(false, "agreement already exists");
		}
		
		//if it is not recovering, this is, it is a new translation received. In this case
		//tta manager has to create all related stuff; xml and tpl files, dnsRecords, etc.
		CallResult cr = null;
		if (!recovery) {
			
			//store agreement detail in the internal repo
				IConnector conn = InternalStorageConnectorFactory.getConnector();
				cr = conn.addAgreement(agr, jo);
				if (!cr.isOK()) {
					ttModel.removeAgreement(agr);
					return cr;
				}
				//create xml and tpl files
				IFileManager fm = FileManagerFactory.getFileManager();
				cr = fm.createTranslationFiles(agr);
				if (!cr.isOK()) {
					conn.deleteAgreementByName(agr.getName());
					ttModel.removeAgreement(agr);
					return cr;
				}
				
				//create dnsRecords
				if (Configuration.getConfiguration().getProperty(PropertyNames.DNS_SET).equalsIgnoreCase("true")) {
					DnsConnector dnsC = new DnsConnector();
					CallResult cr2 = dnsC.publishTranslation(agr);
					if (!cr2.isOK()) {
						fm.deleteTranslationFiles(agr);
						conn.deleteAgreementByName(agr.getName());
						ttModel.removeAgreement(agr);
						return cr2;
					}
				}
				
		}else {
			IFileManager fm = FileManagerFactory.getFileManager();
			fm.addFilenameToContainer(agr);
		}
		

		return cr;

	}



	/**
	 * remove an agreement of translation 
	 * @param commandDetails in json with the name of the agreement
	 * @return CallResult object
	 * 
	 */

	public CallResult removeAgreement(String agreementName){
		
		//get agreement
		TranslationAgreement agr = TTModelST.getModel().getAgreement(agreementName);
		if (agr == null) {
			return new CallResult (false, "Agreement name not found");
		}
		CallResult cr;
		
		//remove DNS records
		if (Configuration.getConfiguration().getProperty(PropertyNames.DNS_SET).equalsIgnoreCase("true")) {
			DnsConnector dnsC = new DnsConnector();
			cr = dnsC.deleteRecords(agr);
			if (!cr.isOK())
				return cr;
		}
		//remove agreeement from model
		agr = TTModelST.getModel().removeAgreement(agreementName);
		if (agr == null) {
			return new CallResult (false, "Agreement cannot be removed from model, contact the administrator");
		}
		
		//remove agreement from declaration files
		IFileManager fm = FileManagerFactory.getFileManager();
		cr = fm.deleteTranslationFiles(agr);
		if (!cr.isOK()) {
			return cr;
		}
		
		IConnector conn = InternalStorageConnectorFactory.getConnector();
		cr = conn.deleteAgreementByName(agr.getName());
		return cr;


	}



	/**
	 * @return a list of names of stored translation agreements in JSON format
	 */
	public JSONObject getAgreementNames(){
		IConnector conn = InternalStorageConnectorFactory.getConnector();
		HashMap<String, TranslationAgreement> agrHM = (HashMap<String, TranslationAgreement>) conn.getAllAgreements();
		Iterator<String> it = agrHM.keySet().iterator();
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();

		while (it.hasNext()){
			ja.put(it.next());

		}
		jo.put("agreementNames", ja);


		return jo;


	}
	
	/**
	 * @return a list of names of stored Trust Schemes in JSON format
	 */
	public JSONObject getTrustSchemeNames(){
		
		TTModel model = TTModelST.getModel();
		return model.getTrustSchemeList();
	}


	/**
	 * @return a list of agreements where the given trust scheme participates in JSON format
	 */
	public JSONObject getAgreemetsOfATrustScheme (String tsname) {
		
		TTModel model = TTModelST.getModel();
		return model.getAgreementsOfATS(tsname);

	}

	
	/**
	 * @param name of the agreement
	 * 
	 * @return description of the translation agreement in JSON format
	 */
	public JSONObject getTranslationByName(String name){
	
		IConnector conn = InternalStorageConnectorFactory.getConnector();
		return conn.getAgreementByNameEqualsTo( name);

	}


	/**
	 * @param name of a trust scheme
	 * @return Trust Scheme definition as it was provided to TTA in JSON format.
	 */
	public CallResult getSchemeDetails(String name){

		TTModel model = TTModelST.getModel();
		JSONObject jo = model.getTrustSchemeDetail(name);
		if (jo != null)
			return new CallResult (true,jo.toString());
		else
			return new CallResult (false,"Trust Scheme: "+name+" not found");

	}

	
}
