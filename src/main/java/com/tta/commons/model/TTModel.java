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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;



/**
 * TTModel class host all trust agreements and data of their participants.
 * 
 * @param trustSubjCont_
 *            container of subject of trust
 * @param agreementCont_
 *            container of agreements
 */
public class TTModel {
	private TrustSchemeContainer trustSchemeCont;
	private TranslationAgreementContainer agreementCont;
	private TrustTranslationDeclarationFileContainer ttdfContainer;

	/**
	 * Constructor
	 */
	public TTModel() {
		this.trustSchemeCont = new TrustSchemeContainer();
		this.agreementCont = new TranslationAgreementContainer();
		this.ttdfContainer = new TrustTranslationDeclarationFileContainer();
	}

	/**
	 * @param agr
	 * @return true if the agreement does not exist into the container and is added
	 */
	public boolean addAgreement(TranslationAgreement agr) {
		boolean r = false;

		if (!agreementCont.containsAgr(agr)) {
			agreementCont.addAgreement(agr);

			// update relations
			TrustScheme src = agr.getSource();
			TrustScheme trg = agr.getTarget();

			// if objects does not exist in the model they are added, otherwise they are
			// retrieved to work with
			if (!trustSchemeCont.containsTrustSchemeInstance(src))
				trustSchemeCont.addSubjectOfTrust(src);
			else {
				src = trustSchemeCont.getTrustSchemeInstance(src);
				agr.setSource(src);
			}
			if (!trustSchemeCont.containsTrustSchemeInstance(trg))
				trustSchemeCont.addSubjectOfTrust(trg);
			else {
				trg = trustSchemeCont.getTrustSchemeInstance(trg);
				agr.setTarget(trg);
			}
			src.addmeAsOrigin(agr.getName());
			trg.addmeAsTarget(agr.getName());

			r = true;
		}

		return r;

	}

	/**
	 * if there is an agreement with the same properties value as the one given, it
	 * is removed from the container
	 * 
	 * @param agr
	 */
	public void removeAgreement(TranslationAgreement agr) {
		agreementCont.removeAgreement(agr);
	}

	/**
	 * if there is an agreement with the same name as the one given, it is removed
	 * from the container
	 * 
	 * @param name
	 */
	public TranslationAgreement removeAgreement(String name) {
		return agreementCont.removeAgreement(name);
	}
	
	
	/**
	 * @param name
	 * @return TranslationAgreement object
	 */
	public TranslationAgreement getAgreement (String name) {
		return agreementCont.getAgreement(name);
	}
	
	/**
	 * @return TrustTranslationDeclarationFileContainer object of the model
	 */
	public TrustTranslationDeclarationFileContainer getTTDFCont() {
		return ttdfContainer;
	}
	
	/**
	 * @param name of a trust scheme
	 * @return List of all Translation Agreements where the name TrustScheme participates
	 */
	public List<String> getAgreementsbyTrustScheme(String name) {
		String uriTarget = Configuration.getConfiguration().getProperty(PropertyNames.DNS_RECORD_URI_BUILD_WITH_TARGET);
	    boolean uriTargetBool = Boolean.parseBoolean(uriTarget);
		
		ArrayList<TrustScheme> al = (ArrayList<TrustScheme>) trustSchemeCont.getTrustScheme(name);
		ArrayList<String> agrNames = new ArrayList<>();
		for (TrustScheme ts : al) {
			String [] sa;
			if (uriTargetBool) {
				sa = ts.getListwhereIamTarget();
			}else {
				sa = ts.getListwhereIamOrigin();
			}
			for (String s : sa) {
				agrNames.add(s);
			}
		}
		return agrNames;
	}
	
	/**
	 * @return JSONObject with a list of all Trust Schemes defined in TTA
	 */
	public JSONObject getTrustSchemeList () {
		String [] tsa = trustSchemeCont.getTrustSchemeNames();
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (String s : tsa) {
			ja.put(s);
		}
		jo.put("trustSchemeNames", ja);
		return jo;
	}
	
	/**
	 * @param trustSchemeName
	 * @return Return a JSON object with a list of all name of Translation Agreements where the given TrustScheme participates
	 */
	public JSONObject getAgreementsOfATS(String trustSchemeName) {
		ArrayList<TrustScheme> tsAL = (ArrayList<TrustScheme>) trustSchemeCont.getTrustScheme(trustSchemeName);
		JSONObject jo = new JSONObject();
		JSONArray jaOrigin = new JSONArray();
		JSONArray jaTarget = new JSONArray();
		String [] or;
		String [] tr;
		if (tsAL != null) {
			for (TrustScheme ts : tsAL) {
				or = ts.getListwhereIamOrigin();
				tr = ts.getListwhereIamTarget();
				for (String s : or)
					jaOrigin.put(s);
				for (String s: tr)
					jaTarget.put(s);
			}
		}
		jo.put("trustSchemeName", trustSchemeName);
		jo.put("OriginOfTrustIn", jaOrigin);
		jo.put("TargetOfTrustIn", jaTarget);
		
		return jo;
	}
	
	/**
	 * @param name
	 * @return JSON object with the description of a Trust Scheme
	 */
	public JSONObject getTrustSchemeDetail (String name) {
		return  trustSchemeCont.getSchemeDetails(name);
	}
	
}
