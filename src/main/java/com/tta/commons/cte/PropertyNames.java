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
package com.tta.commons.cte;


/**
 * 
 * class to map the names of the configuraion properties
 *
 */
public class PropertyNames {

	private PropertyNames() {}
	
	public static final String MONGO_USER = "mongo-db-user";
	public static final String MONGO_PW = "mongo-db-pwd";
	public static final String MONGO_DB_NAME = "mongo-db-name";
	public static final String MONGO_DB_URL = "db_url";
	public static final String MONGO_DB_PORT = "db_port";
	
	public static final String MONGO_AGR_COLL = "agreement_collection";
	public static final String MONGO_SCHEME_COLL = "scheme-collection";
	
	public static final String FILE_STORAGE_PATH = "file-storage-dir";
	
	public static final String MONGO_DB_INSTALLED = "db_installed";
	
	public static final String ONE_FILE_PER_SCHEME = "one_file_per_scheme";
	public static final String PATH_FOR_FILE_SERVER = "file-server-dir";
	public static final String FILE_SERVER_URL = "fileServerURL";
	
	public static final String KEY_STORE_FILE = "pkcsfile";
	public static final String KEY_STORE_PD = "pkcspwd";
	
	public static final String XML_SIG_LEVEL = "xml_signature_level";
	public static final String XML_TOKEN_TYPE = "xml_token_type";
	public static final String XML_SIG_PACK = "xml_signature_packaging";
	public static final String XML_DIGEST_ALG = "xml_digest_algorithm";
	public static final String CAD_SIG_LEVEL = "tpl_signature_level";
	public static final String CAD_TOKEN_TYPE = "tpl_token_type";
	public static final String CAD_SIG_PACK = "tpl_signature_packaging";
	public static final String CAD_DIGEST_ALG = "tpl_digest_algorithm";
	
	public static final String TOKEN_FILE_NAME = "token_file";
    public static final String DNS_USR = "dns-user";
	public static final String DNS_PD = "dns-pwd";
	public static final String DNS_SET = "dnsConfigured";
	public static final String DNS_URL = "dnsURL";
	public static final String DNS_DOMAIN = "dns-domain";
	public static final String DNS_RECORD_URI_BUILD_WITH_TARGET = "dns_uri_record_target";
													 //certificate from keystore
	public static final String FILE_SERVER_BASE_URL = "fileServerURL";
	
	public static final String SIGN_TRANSLATION_FILES = "sign";
	
}