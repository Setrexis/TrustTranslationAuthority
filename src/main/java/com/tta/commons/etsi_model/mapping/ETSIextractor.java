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

import java.io.File;
import java.util.HashMap;

import javax.xml.soap.Node;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.tta.commons.etsi_model.Provider;
import com.tta.commons.etsi_model.Service;
import com.tta.commons.etsi_model.TrustServiceList;

/**
* parser for TSL files in ETSI.
*/
public class ETSIextractor {
 
 static Logger logger = Logger.getLogger("EIDAS_extractor.class");
 private ETSIextractor(){
   
 }
 
 /**
 * @param f file containing the TSL
 * @return TSL representation according to the test model
 */
public static TrustServiceList extract (File f){
//   TrustServiceList sch = null;
//   try {
//     DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//     DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//  
//     Document d =  dBuilder.parse(f);
//     sch = extract (d);
//  } catch (SAXException | IOException | ParserConfigurationException e) {
//    logger.error(e,e);
//  } 
//  
//   return sch;
  return null; 
 }


/**
 * @param d XML (DOM) Document of the TSL
 * @return TSL representation according to the test model
 */
public static TrustServiceList extract (Document d){
   
   String schemeName = null;
   String schemeDesc = null;
   Provider provider = null;
   
   XPath xpath = XPathFactory.newInstance().newXPath();
   NodeList nodeL;
   Node node;
   
   d.getDocumentElement().normalize();
   
   try{
     //extract scheme name
     node = (Node) xpath.evaluate(ETSI.SCHEME_NAME, d.getDocumentElement(), XPathConstants.NODE);
     if (node!= null)
       schemeName = node.getTextContent().trim();
     
   //extract scheme description
     node = (Node)xpath.evaluate(ETSI.SCHEME_INFO_URI, d.getDocumentElement(), XPathConstants.NODE);
     if (node!= null)
       schemeDesc = node.getTextContent().trim();
     
     //extract ServiceProviders
     nodeL = (NodeList)xpath.evaluate(ETSI.SERVICE_PROVIDER, d.getDocumentElement(), XPathConstants.NODESET);
     HashMap<String,Provider> providerHM = new HashMap<>();
     for (int i=0; i<nodeL.getLength(); i++){
       Node serviceProviderNode = (Node) nodeL.item(i);
       
       String serviceProviderName = ((Node)xpath.evaluate(ETSI.SERVICE_PROVIDER_NAME_CHILD, serviceProviderNode, XPathConstants.NODE)).getTextContent().trim();
       String serviceProviderURI =  ((Node)xpath.evaluate(ETSI.SERVICE_PROVIDER_URI_CHILD, serviceProviderNode, XPathConstants.NODE)).getTextContent().trim();
       
       //extract services
       NodeList serviceNodes = (NodeList)xpath.evaluate(ETSI.SERVICES, serviceProviderNode, XPathConstants.NODESET);
       HashMap<String, Service> serviceHM = new HashMap<>();
       for (int j=0; j<serviceNodes.getLength(); j++){
         Node serviceNode = (Node) serviceNodes.item(j);
         String serviceType = ((Node)xpath.evaluate(ETSI.SERVICE_TYPE, serviceNode, XPathConstants.NODE)).getTextContent().trim();
         String serviceStatus = ((Node)xpath.evaluate(ETSI.SERVICE_STATUS, serviceNode, XPathConstants.NODE)).getTextContent().trim();
         String serviceName = ((Node)xpath.evaluate(ETSI.SERVICE_NAME, serviceNode, XPathConstants.NODE)).getTextContent().trim();
         String serviceDigitalID = ((Node)xpath.evaluate(ETSI.SERVICE_DIGITAL_ID, serviceNode, XPathConstants.NODE)).getTextContent().trim();
         String serviceAdditionalInfo="";
         if (node != null)
           serviceAdditionalInfo = node.getTextContent().trim();
         
         
         
         HashMap<String, String> svcInfo = new HashMap<>();
         svcInfo.put("service-provider-name", serviceProviderName);
         svcInfo.put("service-provider-URI", serviceProviderURI);
         svcInfo.put("service-type-identifier", serviceType);
         svcInfo.put("service-status", serviceStatus);
         svcInfo.put("service-additional-info", serviceAdditionalInfo);
         svcInfo.put("service-digital-ID", serviceDigitalID);
         
         
          
         Service svc = new Service (serviceName, svcInfo, null);
         serviceHM.put(svc.getName(), svc);
         
         
       }
        provider = new Provider(serviceProviderName, serviceProviderURI, serviceHM);
        providerHM.put(provider.getProviderName(), provider);
     }
     
     return new TrustServiceList(schemeName, schemeDesc, providerHM);
     
   }catch(Exception e){
     logger.error (e,e);
     return null;
   }
 }
}
