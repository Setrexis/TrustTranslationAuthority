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

package com.tta.file_manager.builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.model.TTModelST;
import com.tta.commons.model.TranslationAgreement;
import com.tta.commons.model.TrustScheme;
import com.tta.commons.utils.Util;
import com.tta.file_manager.security.FileSigner;
import com.tta.file_manager.security.SigningService;

import eu.europa.esig.dss.DSSDocument;



/**
 * XML files builder class
 */
public class XMLBuilder {
	static Logger logger = Logger.getLogger("XMLBuilder.class");

	private XMLBuilder () {}

	/**
	 * @param depositDir directory where the resulting file is saved. The value has to   be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param agr Agreement for which the file will be created
	 * @param multi indicates whether tpl file has to be built per agreement (multi) or per Trust Scheme (single)
	 * @return name of the file or null if the file cannot be created
	 */
	public static String buildFile(String depositDir, boolean sign, TranslationAgreement agr, boolean multi){
		if (multi)
			return buildFile(depositDir, sign, agr);
		else {
			String uriTarget = Configuration.getConfiguration().getProperty(PropertyNames.DNS_RECORD_URI_BUILD_WITH_TARGET);
		    boolean uriTargetBool = Boolean.parseBoolean(uriTarget);
			if (uriTargetBool)
				return buildFile(depositDir, sign, agr.getTarget());
			else
				return buildFile(depositDir,sign, agr.getSource());
			
			
		}
	}
	
	/**
	 * @param depositDir directory where the resulting file is saved. The value has to   be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param agr Agreement for which the file will be created
	 * @return name of the file or null if the file cannot be created
	 */
	private static String buildFile(String depositDir, boolean sign, TranslationAgreement agr){
		String fileName = agr.getName() + ".xml";
		
		String canonicalName = depositDir + fileName;
		String signedCanonicalName = depositDir +"signed/"+fileName;
		
		try {
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			Document doc = db.newDocument();

			Element root = buildXMLDeclaration(doc, agr);

			if (root != null) {
				doc.appendChild(root);
				
				agr.setXmlFileFullName(fileName);

				TransformerFactory tf = TransformerFactory.newInstance();
				tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

				Transformer t = tf.newTransformer();
				t.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource ds = new DOMSource(root);
				File f = new File(canonicalName);

				StreamResult sr = new StreamResult (f);
				t.transform(ds, sr);

				if (sign) {
					SigningService ss = new SigningService(canonicalName, SigningService.XML);


					FileSigner fs = new FileSigner(ss.getRemoteDocumentSignatureService(), ss.getSigningProperties());
					DSSDocument document = fs.doSign();
					if (document == null)
						return null;


					if (Util.save(document, signedCanonicalName)) {
						return fileName;
					}else {
						return null;
					}

				}else {
					Util.save(canonicalName,  signedCanonicalName);
					return fileName;
				}

			}  
			return null;
		} catch (Exception e) {
			logger.error(e,e);
			deleteFile(depositDir, sign, agr, false);

			return null;
		}
	}


	/**
	 * @param depositDir directory where the resulting file is saved. The value has to   be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param ts Trust Scheme for which the file will be created
	 * @return name of the file or null if the file cannot be created
	 */
	private static String buildFile(String depositDir, boolean sign, TrustScheme ts){

		String fileName;
		fileName = ts.getName() + ".xml";

		String canonicalName = depositDir + fileName;
		String signedCanonicalName = depositDir +"signed/"+fileName;
		
		
		//first it save a backup of file if they already exists
		//unsigned file
		StringBuilder xmlCanonicalFileContent = null;
		File f = new File (canonicalName);
		if (f.exists() && f.isFile()){
			try(FileReader fr = new FileReader(f)){
				BufferedReader br = new BufferedReader(fr);
				xmlCanonicalFileContent = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					xmlCanonicalFileContent.append(line+"\n");
				}
			}catch(Exception e) {
				logger.error(e,e);
			}
		}
		
		//published file, it can be signed or no
		StringBuilder xmlSignedCanonicalFileContent = null;
		f = new File (signedCanonicalName);
		
		if (f.exists() && f.isFile()){
			try(FileReader fr = new FileReader(f)){
				BufferedReader br = new BufferedReader(fr);
				xmlSignedCanonicalFileContent = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					xmlSignedCanonicalFileContent.append(line+"\n");
				}
			}catch(Exception e) {
				logger.error(e,e);
			}
		}
		
		//generate xml content
		try {		
			ts.setXmlFileFullName(fileName);
			
			DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = df.newDocumentBuilder();
			Document doc = db.newDocument();
			Element main = doc.createElement("trustlevel-translation-agreements");

			ArrayList<String> al = (ArrayList<String>) TTModelST.getModel().getAgreementsbyTrustScheme(ts.getName());
			String[] agrList = new String [al.size()];
			agrList = al.toArray(agrList);
			
			
			for (String agrName : agrList) {
				TranslationAgreement agr = TTModelST.getModel().getAgreement(agrName);
				Element root = buildXMLDeclaration(doc, agr);
				if (root != null) {
					main.appendChild(root);
				}
			}

			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource ds = new DOMSource(main);
			f = new File(canonicalName);

			StreamResult sr = new StreamResult (f);
			t.transform(ds, sr);

			//publish generated file, it is to copy the file to the file service allocaton
			//if the file has to be signed
			if (sign) {
				SigningService ss = new SigningService(canonicalName, SigningService.XML);


				FileSigner fs = new FileSigner(ss.getRemoteDocumentSignatureService(), ss.getSigningProperties());
				DSSDocument document = fs.doSign();
				if (document == null)
					return null;


				if (Util.save(document, signedCanonicalName)) {
					return fileName;
				}else {
					return "";
				}

				//if the file does not have to be signed
			}else {
				Util.save(canonicalName,  signedCanonicalName);
				return fileName;
			}



		} catch (Exception e) {
			logger.error(e,e);
			// in case of error, it replaces new files by the backup ones
			 try (BufferedWriter bwr = new BufferedWriter(new FileWriter(f))){
				 f = new File(canonicalName);
				 bwr.write(xmlCanonicalFileContent.toString());
				 bwr.flush();
				 
			 }catch(Exception e1) {
				 logger.error(e1,e1);
			 }	 
			 try (BufferedWriter bwr = new BufferedWriter(new FileWriter(f))){
				 f = new File(signedCanonicalName);
				 bwr.write(xmlSignedCanonicalFileContent.toString());
				 bwr.flush();
				
			 }catch(Exception e1) {
				 logger.error(e1,e1);
			 }
			return null;
		}
	}

	
	
	/**
	 * @param depositDir Directory where the resulting file is saved. The value has to be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param agr Agreement for which the file will be removed
	 * @param multi indicates whether tpl file was to be built per agreement (multi) or per Trust Scheme (single)
	 * @return
	 */
	public static String deleteFile(String depositDir, boolean sign, TranslationAgreement agr, boolean multi){
		String fileName;
		
		
		if (multi) {
			
			fileName = agr.getName() + ".xml";
			
		}else {
			TrustScheme ts = agr.getTarget();
			fileName = ts.getName() + ".xml";
		}
		
		String canonicalName = depositDir + fileName;
		String signedCanonicalName = depositDir +"signed/"+fileName;
		
		try {
			File f = new File (canonicalName);
			logger.debug("Trying to delete file: " + canonicalName);
			if (f.exists() && f.isFile()){
				Files.delete(f.toPath());
			}
			f = new File (signedCanonicalName);
			logger.debug("Trying to delete file: " + signedCanonicalName);
			if (f.exists() && f.isFile()){
				
				Files.delete(f.toPath());
			}
			if (!multi && !(TTModelST.getModel().getAgreementsbyTrustScheme(agr.getTarget().getName()).isEmpty())) {
				buildFile(depositDir, sign, agr, multi);
				return "";
				
			}
		}catch (Exception e) {
			logger.error(e,e);
			return null;
		}

		return fileName;
	}




	/**
	 * @param doc
	 * @param agr
	 * @return
	 */
	public static Element buildXMLDeclaration(Document doc, TranslationAgreement agr) {
		try {


			Element root = doc.createElement("trustlevel-translation-agreement");


			Element name = doc.createElement("name");
			name.appendChild(doc.createTextNode(agr.getName()));
			root.appendChild(name);

			Element actDate = doc.createElement("activation-date");
			actDate.appendChild(doc.createTextNode(agr.getActivationDate().toString()));
			root.appendChild(actDate);

			Element crtDate = doc.createElement("creation-date");
			crtDate.appendChild(doc.createTextNode(agr.getCreationDate().toString()));
			root.appendChild(crtDate);

			Element lvDate = doc.createElement("leaving-date");
			lvDate.appendChild(doc.createTextNode(agr.getLeavingDate().toString()));
			root.appendChild(lvDate);

			Element duration = doc.createElement("duration");
			duration.appendChild(doc.createTextNode(""+agr.getDuration()));
			root.appendChild(duration);

			Element status = doc.createElement("status");
			status.appendChild(doc.createTextNode(""+agr.getSource()));
			root.appendChild(status);

			Element source = doc.createElement("source");
			root.appendChild(source);

			Element sName = doc.createElement("scheme-name");
			sName.appendChild(doc.createTextNode(agr.getSource().getName()));
			source.appendChild(sName);

			Element slevel = doc.createElement("level");
			if (agr.getSource().getLevel() != null)
				slevel.appendChild(doc.createTextNode(agr.getSource().getLevel()));
			source.appendChild(slevel);

			Element sProvider = doc.createElement("provider");
			if (agr.getSource().getProvider() != null)
				sProvider.appendChild(doc.createTextNode(agr.getSource().getProvider()));
			source.appendChild(sProvider);

			Element params = doc.createElement("params");
			source.appendChild(params);

			Set<Entry<String,String>> pList = agr.getSource().getParamList();
			if (pList!=null) {
				Iterator<Entry<String, String>> it = pList.iterator();
				while (it.hasNext()){
					Entry<String, String> n = it.next();
					Element param = doc.createElement("param");
					params.appendChild(param);
					Element nam = doc.createElement("name");
					nam.appendChild(doc.createTextNode((String) n.getKey()));
					param.appendChild(nam);
					Element val = doc.createElement("value");
					val.appendChild(doc.createTextNode((String) n.getValue()));
					param.appendChild(val);
				}
			}

			Element target = doc.createElement("target");
			root.appendChild(target);

			Element tName = doc.createElement("scheme-name");
			tName.appendChild(doc.createTextNode(agr.getTarget().getName()));
			target.appendChild(tName);

			Element tlevel = doc.createElement("level");
			if (agr.getTarget().getLevel() != null)
				tlevel.appendChild(doc.createTextNode(agr.getTarget().getLevel()));
			target.appendChild(tlevel);

			Element tProvider = doc.createElement("provider");
			if (agr.getTarget().getProvider() != null)
				tProvider.appendChild(doc.createTextNode(agr.getTarget().getProvider()));
			target.appendChild(tProvider);

			Element paramt = doc.createElement("params");
			target.appendChild(paramt);

			Set<Entry<String,String>> pList2 = agr.getTarget().getParamList();
			if (pList2!=null) {
				Iterator<Entry<String,String>> it2 = pList2.iterator();
				while (it2.hasNext()){
					Entry<String, String> n = it2.next();
					Element param = doc.createElement("param");
					paramt.appendChild(param);
					Element nam = doc.createElement("name");
					nam.appendChild(doc.createTextNode((String) n.getKey()));
					param.appendChild(nam);
					Element val = doc.createElement("value");
					val.appendChild(doc.createTextNode((String) n.getValue()));
					param.appendChild(val);
				}
			}

			return root;
		}catch (Exception e) {
			logger.error(e,e);
			return null;
		}
	}

}
