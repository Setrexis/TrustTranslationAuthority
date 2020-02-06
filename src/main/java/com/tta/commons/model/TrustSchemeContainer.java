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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;



/**
 * TrustObjectContainer class of model for TTA api of dao module, it represent to a source or a target of Trust.
 * 
 * @param stCont_ MAP object to host all Subjects of Trust
 */
public class TrustSchemeContainer {

	private HashMap<String,ArrayList<TrustScheme>> tsCont;


	/**
	 * Constructor
	 */
	public TrustSchemeContainer(){
		tsCont = new HashMap<>();
	}

	/**
	 * @param name of a trust scheme
	 * @return true if it contains ane instance of a trust scheme with this name, false any other case
	 */
	public boolean containsTrustScheme (String name){
		return tsCont.containsKey(name);
	}

	/**
	 * @param subject of trust
	 * @return true if it contains a trust scheme with all properties equals to the ones of the parameter
	 */
	public boolean containsTrustSchemeInstance (TrustScheme ts){

		ArrayList<TrustScheme> al = tsCont.get(ts.getName());
		if (al != null){
			for (TrustScheme myts : al) {
				if (myts.isEqualTo(ts)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param trust scheme
	 */
	public void addSubjectOfTrust (TrustScheme ts){
		ArrayList<TrustScheme> al;
		if (tsCont.containsKey(ts.getName())){
			al = tsCont.get(ts.getName());
		}else {
			al = new ArrayList<>();
			tsCont.put(ts.getName(), al);
		}

		boolean found = false;
		for (int i=0; i<al.size(); i++) {
			if (al.get(i).isEqualTo(ts)) {
				found = true;
				break;
			}
		}

		if (!found) {
			al.add(ts);
		}
	}

	/**
	 * it removes an instance of a trust scheme from the container if it has all his properties with the same value that the one passed as parameter
	 * @param trust scheme
	 */
	public boolean removeSubjectOfTrust(TrustScheme ts){
		ArrayList<TrustScheme> al;
		if (tsCont.containsKey(ts.getName())){
			al = tsCont.get(ts.getName());
		}else {
			return false;
		}

		for (int i=0; i<al.size(); i++) {
			if (al.get(i).isEqualTo(ts)) {
				al.remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * it removes all instances of a trust scheme (warning it can affect to existing translation agreements
	 * @param string
	 */
	public boolean removeSubjectOfTrust(String schemeName){

		if (tsCont.containsKey(schemeName)){
			tsCont.remove(schemeName);
			return true;
		}else {
			return false;
		}
	}

	/**
	 * @param name
	 * @return all instances of a Trust Scheme
	 */
	public List<TrustScheme> getTrustScheme (String schemeName){
		return tsCont.get(schemeName);
	}


	/**
	 * @param name
	 * @return the instances of a Trust Scheme equal to the trust scheme received as parameter
	 */
	public TrustScheme getTrustSchemeInstance (TrustScheme ts){
		ArrayList<TrustScheme> al = tsCont.get(ts.getName());

		for (TrustScheme myts : al) {
			if (myts.isEqualTo(ts)) {
				return myts;
			}
		}

		return null;
	}

	/**
	 * @return String array with the name of all Trust Schemes added to the TTA
	 */
	public String[] getTrustSchemeNames() {
		Set<String > s= tsCont.keySet();
		int n = s.size();
		String[] strA = new String[n];
		strA = s.toArray(strA);

		return strA;

	}

	/**
	 * @param name
	 * @return JSONObject with the details of a trust scheme
	 */
	public JSONObject getSchemeDetails (String name) {
		ArrayList<TrustScheme>tsAL = (ArrayList<TrustScheme>) getTrustScheme (name);
		if (tsAL != null) {
			if (!tsAL.isEmpty()) {
				JSONObject jo = new JSONObject ();
				JSONArray ja = new JSONArray();
	
				for (TrustScheme ts : tsAL) {
					ja.put(ts.toJSON());
				}
				jo.put("trust-scheme-list", ja);
				return jo;
			}
		}
		return null;
	}
}
