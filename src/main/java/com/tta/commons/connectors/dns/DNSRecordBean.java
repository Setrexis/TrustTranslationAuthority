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

package com.tta.commons.connectors.dns;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * RecordBean for the body of the queries to DNS in JSON format
 *
 */
@XmlRootElement(name = "scheme-name")
public class DNSRecordBean {

	Logger logger_ = Logger.getLogger(DNSRecordBean.class);
	
	private String url_ = null;
	private String pkey_ = null;
	
	/**
	 * @param url of the translation declaration file on the file server
	 * @param pkey public key of the certificate used to sign the file
	 */
	public DNSRecordBean(String url, String pkey) {
		super();
		this.url_ = url;
		this.pkey_ = pkey;
	}

	
	
	/**
	 * @return the url field pointing to Translation agreement files
	 */
	public String getUrl() {
		return url_;
	}
	
	
	/**
	 * @param url of translation agreement files
	 */
	public void setUrl(String url) {
		this.url_ = url;
	}
	
	
	/**
	 * @return String with the certificate used to sigh the document
	 */
	public String getCertificate() {
		return pkey_;
	}
	
	
	/**
	 * @param certificate used to sign the document
	 */
	public void setCertificate(String certificate) {
		this.pkey_ = certificate;
	}
	
	@Override
    public String toString() {
		if (pkey_ != null) {
			return "scheme-name [url=" + url_ + ", certificate=" + pkey_ + "]";
		}else {
			return "scheme-name [url=" + url_ + "]";
		}
		
    }
	

	
	
	/**
	 * @return JSON document to be added in the body of the request to DNS
	 */
	public String getJson() {
		JSONObject jo = new JSONObject();
		jo.put("url", url_);
		JSONObject joCertificate = new JSONObject();
		JSONArray ja = new JSONArray();
		
		if (pkey_ != null) {
			joCertificate.put("data", pkey_);
			joCertificate.put("usage", "dane-ee");
			joCertificate.put("selector", "spki");
			joCertificate.put("matching", "full");
			ja.put(joCertificate);
		}
		
		jo.put("certificate", ja);
		
		logger_.debug("set DNS record. JSON: ");
		logger_.debug(jo.toString());
		
		return jo.toString();
		
	}
}
