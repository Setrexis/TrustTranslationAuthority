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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Scanner;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.error.CallResult;
import com.tta.commons.model.TTModelST;
import com.tta.commons.model.TranslationAgreement;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * class to manage the connection with DBS Server
 * 
 * class properties:
 *        pk_: public key of the certificate used to sign xml and tpl documents
 *
 */
public class DnsConnector {

	private String dnsServiceSubDom = "/translation";
	private String dnsSecurityToken = "Bearer ";
	private String dnsSecurityHeader = "Authorization";

	private Logger logger = Logger.getLogger(DnsConnector.class);
	private static String pk_;



	/**
	 * @return public key of the certificate in BASE16
	 */
	public static String getPK () {
		return pk_;
	}

	/**
	 * @param pk public key of the certificate
	 */
	public static void setPK(String pk) {
		pk_ = pk;
	}

	/**
	 * @param agr
	 * @return DNSResponse object with the result of the query to DNS.
	 */
	public CallResult publishTranslation (TranslationAgreement agr) {
		String oneFileS = Configuration.getConfiguration().getProperty(PropertyNames.ONE_FILE_PER_SCHEME);
		boolean oneFile = Boolean.parseBoolean(oneFileS);

		try {

			String uriSchemeName = getURISchemeName(agr);
			if (oneFile && agr.getTarget().getListwhereIamTarget().length > 1)
				return new CallResult(true,"dns record already created");

			String endPoint = Configuration.getConfiguration().getProperty(PropertyNames.DNS_URL)+"/"+uriSchemeName + dnsServiceSubDom;

			DNSRecordBean record = getRecordBean(agr);

			RequestBody body = RequestBody.create(MediaType.parse("Application/Json"), record.getJson());
			logger.debug("seeting dns record for tplfile: " + record.toString());
			Request request =  new Request.Builder()
					.url(endPoint)
					.addHeader(dnsSecurityHeader, dnsSecurityToken + getToken())
					.put(body)
					.build();
			Response response;

			logger.debug("Request: " + request);

			OkHttpClient mClient = new OkHttpClient();
			response = mClient.newCall(request).execute();
			logger.debug("Response: " + response.code());
			logger.debug(response);

			if (response.code() != 200 && response.code() != 204) {
				logger.error("error while creating tpl file record.");
				logger.debug("response.body().charStream():" + response.body().charStream());
				logger.debug("response.body().string():" + response.body().string());
				return new CallResult(false, "Error while creating DNS record, DNS answer: " + response.code()); 
			}

			return new CallResult (true,"");

		} catch (Exception e) {
			logger.error(e,e);
			return new CallResult(false, "Internal error while creating DNS record.");
		}
	}


	/**
	 * @param agr
	 * @return DNSResponse object with the result of the query to DNS.
	 */
	public CallResult deleteRecords (TranslationAgreement agr) {
		String oneFileS = Configuration.getConfiguration().getProperty(PropertyNames.ONE_FILE_PER_SCHEME);
		boolean oneFile = Boolean.parseBoolean(oneFileS);

		int i = TTModelST.getModel().getAgreementsbyTrustScheme(agr.getTarget().getName()).size();
		try{
			if (oneFile && TTModelST.getModel().getAgreementsbyTrustScheme(agr.getTarget().getName()).size() > 1)
				return new CallResult (true, "dns record no deleted, there are more agr for the target trust scheme");

			OkHttpClient mClient = new OkHttpClient();

			String uriSchemeName = getURISchemeName(agr);
			String endPoint = Configuration.getConfiguration().getProperty(PropertyNames.DNS_URL)+"/"+uriSchemeName+dnsServiceSubDom;

			logger.debug("deleting dns record for scheme: " + agr.getTarget().getName());
			Request request =  new Request.Builder()
					.delete()
					.url(endPoint)
					.addHeader(dnsSecurityHeader, dnsSecurityToken + getToken())
					.build();
			Response response;

			logger.debug("Request: " + request);

			response = mClient.newCall(request).execute();
			logger.debug("Response: " + response.code());
			logger.debug(response);

			if (response.code() != 200 && response.code() != 204) {
				logger.error("error while creating tpl file record.");
				logger.debug("response.body().charStream():" + response.body().charStream());
				logger.debug("response.body().string():" + response.body().string());
				return new CallResult(false, "Error while deleting DNS records for : " + agr.getTarget().getName()); 
			}

			return new CallResult(true,"");
		}catch (Exception e) {
			logger.error(e,e);
			return new CallResult(false, "Undefined error while deleting DNS records for: " + agr.getTarget().getName());
		}

	}



	/**
	 * @return String with the token to grant access to DNS
	 */
	private String getToken() {
		StringBuilder str = new StringBuilder();
		String fileN = Configuration.getConfiguration().getProperty(PropertyNames.TOKEN_FILE_NAME);
		File f = new File (fileN);
		Scanner sc=null;
		try {
			sc = new Scanner(f);

			str.append(sc.nextLine());
			while (sc.hasNextLine()) {
				str.append(sc.nextLine());
			}

		} catch (FileNotFoundException e) {
			logger.error (e,e);
			str = new StringBuilder("");
		}
		if (sc != null)
			sc.close();
		return str.toString();
	}



	/**
	 * @param agr
	 * @return The name of the scheme to be used in the construction of the URL to query to DNS
	 */
	private String getURISchemeName(TranslationAgreement agr) {

		String schemeName = getDeclarationFileRecordAim(agr);
		if (Configuration.getConfiguration().getProperty(PropertyNames.DNS_DOMAIN) == null)
			return schemeName;
		return schemeName + Configuration.getConfiguration().getProperty(PropertyNames.DNS_DOMAIN);
	}



	
	/**
	 * @param agr translation agreement object
	 * @return The record bean that will be used to build the body of the query to DNS
	 */
	private DNSRecordBean getRecordBean(TranslationAgreement agr) {

		String signS = Configuration.getConfiguration().getProperty(PropertyNames.SIGN_TRANSLATION_FILES);
		boolean sing = Boolean.parseBoolean(signS);
		String fileServer = Configuration.getConfiguration().getProperty(PropertyNames.FILE_SERVER_BASE_URL);
		String fileUrl;

		String oneFileS = Configuration.getConfiguration().getProperty(PropertyNames.ONE_FILE_PER_SCHEME);
		boolean oneFile = Boolean.parseBoolean(oneFileS);

		if (oneFile) {
			fileUrl = fileServer + "/" + getDeclarationFileRecordAim(agr);
		}else {
			fileUrl = fileServer+"/"+ agr.getName();
		}


		DNSRecordBean record;
		if (sing) {
//			byte[] encodedPublicKey = pk_.getEncoded();
//			String pkHex = Hex.encodeHexString(encodedPublicKey);
//			pkHex = pkHex.toUpperCase();

			record = new DNSRecordBean(fileUrl, pk_);

		}else {
			record = new DNSRecordBean(fileUrl, null);
		}
		return record;
	}

	/**
	 * @param agr Translation agreement object
	 * @return the name for the file of translation declaration
	 */
	private String getDeclarationFileRecordAim (TranslationAgreement agr) {

		String uriTarget = Configuration.getConfiguration().getProperty(PropertyNames.DNS_RECORD_URI_BUILD_WITH_TARGET);
		boolean uriTargetBool = Boolean.parseBoolean(uriTarget);

		String schemeName;

		if (uriTargetBool) {
			schemeName = agr.getTarget().getName();
		}else {
			schemeName = agr.getSource().getName();
		}

		return schemeName;
	}
}
