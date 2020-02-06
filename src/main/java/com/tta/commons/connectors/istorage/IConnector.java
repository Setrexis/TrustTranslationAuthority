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

package com.tta.commons.connectors.istorage;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;

import com.tta.commons.error.CallResult;
import com.tta.commons.model.TranslationAgreement;

/**
 * 
 * Interface for a connector of storage of agreements. Specific for TTA module
 * the data stored are agreements in json format as they are received in createAgreement method of the API.
 * The aim of storing this info is to be able to restore it on shut-downs or crashes.
 * 
 */
public interface IConnector {

	public boolean initialize();
	public  List<Document> getAgreementsWithValcontainedInName (String val);
	public  JSONObject getAgreementByNameEqualsTo (String val);
	public CallResult deleteAgreementByName(String name);
	public Map<String,TranslationAgreement> getAllAgreements ();
	public CallResult addAgreement(TranslationAgreement agr, JSONObject agreement);
	public CallResult addAgreement (String dbName, String collName, TranslationAgreement agr, JSONObject agreement);
	
}
