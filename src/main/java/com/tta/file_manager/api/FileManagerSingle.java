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

package com.tta.file_manager.api;

import org.apache.log4j.Logger;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.error.CallResult;
import com.tta.commons.model.TTModelST;
import com.tta.commons.model.TranslationAgreement;
import com.tta.file_manager.IFileManager;
import com.tta.file_manager.builder.TPLBuilder;
import com.tta.file_manager.builder.XMLBuilder;

public class FileManagerSingle implements IFileManager {

	Logger logger = Logger.getLogger (FileManagerSingle.class);
	
	/* (non-Javadoc)
	 * @see com.tta.file_manager.IFileManager#createTranslationFiles(com.tta.commons.model.TranslationAgreement)
	 */
	public CallResult createTranslationFiles(TranslationAgreement agr){

		String depositDir = Configuration.getConfiguration().getProperty(PropertyNames.PATH_FOR_FILE_SERVER);
		String signS = Configuration.getConfiguration().getProperty(PropertyNames.SIGN_TRANSLATION_FILES);
		boolean sign = Boolean.parseBoolean(signS);
		
		logger.debug ("create translation files init");

		String xmlFname = null;
		
		xmlFname = XMLBuilder.buildFile(depositDir, sign, agr, false);
		
		if (xmlFname == null){
			logger.error("error creating xml file");
			XMLBuilder.deleteFile(depositDir, sign, agr, false);
			return new CallResult(false,"error creating xml file. Contact the administrator.");
		}else {
			String signedCanonicalxmlFname = depositDir +"signed/"+xmlFname;
			TTModelST.getModel().getTTDFCont().addFile(xmlFname, signedCanonicalxmlFname);
		}

		String tplFname = null;
		tplFname = TPLBuilder.buildFile(depositDir, sign, agr,false);
		
		if (tplFname == null){
			logger.error("error creeating tpl file");
			XMLBuilder.deleteFile(depositDir, sign, agr, false);
			TPLBuilder.deleteFile(depositDir, sign,  agr, false);
			return new CallResult(false, "error creeating tpl file. Contact the administrator.");
		}else {
			String signedCanonicalTplFname = depositDir +"signed/"+tplFname;
			TTModelST.getModel().getTTDFCont().addFile(tplFname, signedCanonicalTplFname);
		}

		String answer = null;
		answer = "{\n"+
					"    \"agreename\":\""+agr.getName()+"\",\n" +
					"    \"xmlFile\":\""+xmlFname+"\",\n"+
					"    \"tplFile\":\""+tplFname+"\"\n"+
					"}";
		logger.debug("files created: " +  answer);
		return new CallResult(true,answer);

	}
	
	/* (non-Javadoc)
	 * @see com.tta.file_manager.IFileManager#deleteTranslationFiles(com.tta.commons.model.TranslationAgreement)
	 */
	public CallResult deleteTranslationFiles(TranslationAgreement agr){
		
		
		String depositDir = Configuration.getConfiguration().getProperty(PropertyNames.PATH_FOR_FILE_SERVER);
		String signS = Configuration.getConfiguration().getProperty(PropertyNames.SIGN_TRANSLATION_FILES);
		boolean sign = Boolean.parseBoolean(signS);


		
		String xmlFileName = XMLBuilder.deleteFile(depositDir, sign, agr, false);
		if (xmlFileName != null && !xmlFileName.isEmpty()) {
			TTModelST.getModel().getTTDFCont().deleteFile(xmlFileName);
		}
			
		String tplFileName = TPLBuilder.deleteFile(depositDir, sign, agr, false);
		if (tplFileName != null && !tplFileName.isEmpty()) {
			TTModelST.getModel().getTTDFCont().deleteFile(tplFileName);
		}
		String msg = "";
		boolean r = true;
		if (xmlFileName==null && tplFileName==null) {
			msg = "XML and/or TPL trust translation files could not be removed. ";
			r = false;
		}	
		
		return new CallResult ( r, msg);
	}

	
	public void addFilenameToContainer(TranslationAgreement agr) {
		String depositDir = Configuration.getConfiguration().getProperty(PropertyNames.PATH_FOR_FILE_SERVER);

		String xmlFname;
		String tplFname;
		String uriTarget = Configuration.getConfiguration().getProperty(PropertyNames.DNS_RECORD_URI_BUILD_WITH_TARGET);
	    boolean uriTargetBool = Boolean.parseBoolean(uriTarget);
		if (uriTargetBool) {
			xmlFname = agr.getTarget().getName() + ".xml";
			tplFname = agr.getTarget().getName() + ".tpl";
		}else {
			xmlFname = agr.getSource().getName() + ".xml";
			tplFname = agr.getSource().getName() + ".tpl";
		}
		
		String signS = Configuration.getConfiguration().getProperty(PropertyNames.SIGN_TRANSLATION_FILES);
		boolean sign = Boolean.parseBoolean(signS);
		if (sign)
			tplFname =  tplFname + ".p7s";
		
		
		String signedCanonicalxmlFname = depositDir +"signed/"+xmlFname;
		TTModelST.getModel().getTTDFCont().addFile(xmlFname, signedCanonicalxmlFname);
		
				String signedCanonicalTplFname = depositDir +"signed/"+tplFname;
		TTModelST.getModel().getTTDFCont().addFile(tplFname, signedCanonicalTplFname);
	}
}
