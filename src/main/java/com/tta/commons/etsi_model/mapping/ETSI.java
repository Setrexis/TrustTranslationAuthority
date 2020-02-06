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

package com.tta.commons.etsi_model.mapping;



/**
 * XPATH of interest into a TSL in ETSI.
 */
public class ETSI {
	
	private ETSI() {}

  static final String SCHEME = "EIDAS";
  static final String SCHEME_NAME= "/TrustServiceStatusList/SchemeInformation/SchemeName/Name[@lang=\"en\"]";
  static final String SCHEME_INFO_URI = "/TrustServiceStatusList/SchemeInformation/SchemeInformationURI/URI[@lang=\"en\"]";
  static final String SERVICE_PROVIDER= "/TrustServiceStatusList/TrustServiceProviderList/TrustServiceProvider";
  static final String SERVICE_PROVIDER_NAME_CHILD = "./TSPInformation/TSPName/Name[@lang=\"en\"]";
  static final String SERVICE_PROVIDER_URI_CHILD = "./TSPInformation/TSPInformationURI/URI[@lang=\"en\"]";
  
  static final String SERVICES = "./TSPServices/TSPService";
  
  static final String SERVICE_TYPE = "./ServiceInformation/ServiceTypeIdentifier";
  static final String SERVICE_NAME = "./ServiceInformation/ServiceName/Name[@lang=\"en\"]";
  static final String SERVICE_DIGITAL_ID = "./ServiceInformation/ServiceDigitalIdentity/DigitalId/X509Certificate";
  static final String SERVICE_STATUS= "./ServiceInformation/ServiceStatus";
  static final String SERVICE_ADDITIONAL_INFO = "./ServiceInformation/ServiceInformationExtensions/Extension/AdditionalServiceInformation";
}
