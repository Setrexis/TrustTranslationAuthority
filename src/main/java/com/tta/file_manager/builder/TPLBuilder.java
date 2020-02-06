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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

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
 * TPL files builder class
 */
public class TPLBuilder {

	static Logger logger = Logger.getLogger("TPLBuilder.class");

	private TPLBuilder() {}


	/**
	 * @param depositDir directory where the resulting file is saved. The value has to be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param agr Agreement for which the file will be created
	 * @param multi indicates whether tpl file has to be built per agreement (multi) or per Trust Scheme (single)
	 * @return name of the file or null if the file cannot be created
	 * 
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
	 * @param depositDir directory where the resulting file is saved. The value has to be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param agr Agreement for which the file will be created
	 * @return name of the file or null if the file cannot be created
	 */
	private static String buildFile(String depositDir, boolean sign, TranslationAgreement agr){

		String fileName = agr.getName() + ".tpl";
		
		String canonicalName = depositDir + "/" + fileName;
		String signedFileName = fileName + ".p7s";
		String signedCanonicalName = depositDir+ "/signed/" + signedFileName;
		String unsignedCanonicalName = depositDir+ "/signed/" + fileName;




		StringBuilder sb = new StringBuilder();
		String sourceName = agr.getSource().getName();
		String targetName = agr.getTarget().getName();
		//
		//sb.append("/* " + targetName + " --> translation */\n");
		//sb.append("/* " + fileName + "*/\n\n");

		//source data
		TrustScheme source = agr.getSource();
		TrustScheme target = agr.getTarget();
		genData(agr.getName(), sb,source,target);


		try (FileWriter fw = new FileWriter(canonicalName);
				BufferedWriter bw = new BufferedWriter(fw)){  


			bw.write(sb.toString());

			bw.flush();
			fw.flush();


		} catch (IOException e) {
			logger.error(e,e);
			deleteFile(depositDir, sign, agr, false);

			return null;
		}

		if (sign) {
			SigningService ss = new SigningService(canonicalName, SigningService.TPL);


			FileSigner fs = new FileSigner(ss.getRemoteDocumentSignatureService(), ss.getSigningProperties());
			DSSDocument document = fs.doSign();
			if (document == null)
				return null;
			agr.setTplFileFullName(signedFileName);
			
			if (Util.save(document, signedCanonicalName)) {
				return signedFileName;
			}else {
				return "";
			}

		}else {
			agr.setTplFileFullName(fileName);
			Util.save(canonicalName,  unsignedCanonicalName);
			return fileName;
		}
	}

	/**
	 * @param depositDir directory where the resulting file is saved. The value has to be the directory path where file server store static files.
	 * @param sign indicates if the resulting file has to be signed
	 * @param ts Trust Scheme for which the file will be created
	 * @return name of the file or null if the file cannot be created
	 */
	public static String buildFile(String depositDir, boolean sign, TrustScheme ts){


		String fileName;
		fileName = ts.getName() + ".tpl";


		String canonicalName = depositDir + "/" + fileName;
		String signedFileName = fileName + ".p7s"; 
		String signedCanonicalName = depositDir+ "/signed/" + signedFileName;
		String unsignedCanonicalName = depositDir+ "/signed/" + fileName;


		//if files already exists we take a backup on memory
		//file before signing
		StringBuilder tplCanonicalFileContent = null;
		File f = new File (canonicalName);
		if (f.exists() && f.isFile()){
			try(FileReader fr = new FileReader(f)){
				BufferedReader br = new BufferedReader(fr);
				tplCanonicalFileContent = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					tplCanonicalFileContent.append(line+"\n");
				}
			}catch(Exception e) {
				logger.error(e,e);
			}
		}
		
		//published file, it can be signed or unsigned
		StringBuilder tplSignedCanonicalFileContent = null;
		if (sign)
			f=new File(signedCanonicalName);
		else
			f = new File (unsignedCanonicalName);

		if (f.exists() && f.isFile()){
			try(FileReader fr = new FileReader(f)){
				BufferedReader br = new BufferedReader(fr);
				tplSignedCanonicalFileContent = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					tplSignedCanonicalFileContent.append(line+"\n");
				}
			}catch(Exception e) {
				logger.error(e,e);
			}
		}



		//generate file content
		StringBuilder sb = new StringBuilder();

		ArrayList<String> al = (ArrayList<String>) TTModelST.getModel().getAgreementsbyTrustScheme(ts.getName());
		String[] agrList = new String [al.size()];
		agrList = al.toArray(agrList);
		
		for (String agrName : agrList) {
			TranslationAgreement agr = TTModelST.getModel().getAgreement(agrName);
			//sb.append("/*agreement name: " + agr.getName());

			TrustScheme source = agr.getSource();
			TrustScheme target = agr.getTarget();
			genData(agr.getName(), sb,source,target);

		}

		//write content to file
		try (FileWriter fw = new FileWriter(canonicalName);
				BufferedWriter bw = new BufferedWriter(fw)){  


			bw.write(sb.toString());

			bw.flush();
			fw.flush();


			//to move generated file to publishing service allocation
			//if it has to be signed
			if (sign) {
				SigningService ss = new SigningService(canonicalName, SigningService.TPL);


				FileSigner fs = new FileSigner(ss.getRemoteDocumentSignatureService(), ss.getSigningProperties());
				DSSDocument document = fs.doSign();
				if (document == null)
					return null;
				ts.setTplFileFullName(signedFileName);

				if (Util.save(document, signedCanonicalName)) {
					return signedFileName;
				}else {
					return "";
				}
			//if it has not to be signed
			}else {

				Util.save(canonicalName,  unsignedCanonicalName);
				ts.setTplFileFullName(fileName);
				return fileName;
			}
		} catch (Exception e) {
			
			//in case of error it restores backup files
			logger.error(e,e);
			f = new File(canonicalName);
			try (BufferedWriter bwr = new BufferedWriter(new FileWriter(f))){
				
				bwr.write(tplCanonicalFileContent.toString());
				bwr.flush();
				
			}catch (Exception e1) {
				logger.error(e1,e1);
			}
			if (sign)
				f = new File(signedCanonicalName);
			else
				f = new File (unsignedCanonicalName);
			try(BufferedWriter bwr = new BufferedWriter(new FileWriter(f))){
			
				bwr.write(tplSignedCanonicalFileContent.toString());
				bwr.flush();
				
			}catch (Exception e1) {
				logger.error(e1,e1);
			}
			return null;
			
		}

	}

	/**
	 * @param sb
	 * @param src
	 * @param trg
	 */
	private static void genData(String agrName, StringBuilder sb, TrustScheme src, TrustScheme trg) {
		String sourceName = src.getName();
		String targetName = trg.getName();

		sb.append("\ntranslate_conditional_" + agrName + "(" + sourceName + ","+ targetName + ") :-\n");

		//source data
		genParamData(sb,src, false);

		//target data
		genParamData(sb,trg, true);
	}
	
	/**
	 * @param sb object with the tpl code to be saved
	 * @param ts 
	 */
	private static void genParamData (StringBuilder sb, TrustScheme ts, boolean end ) {
		ArrayList<String> lines = new ArrayList<>();
		if (!ts.getLevel().isEmpty())
			lines.add("  extract(" + ts.getName() + ", level, " + ts.getLevel() + ")");
		if (!ts.getProvider().isEmpty())
			lines.add("  extract(" + ts.getName() + ", provider, " + ts.getProvider()+ ")");
		if (ts.getParamList() != null){
			Iterator<Entry<String, String>> it = ts.getParamList().iterator();
			while (it.hasNext()){
				Entry<String, String> param = it.next();
				lines.add("  extract(" + ts.getName() + ", " + param.getKey() + ", " + param.getValue() + ")");
			}
		}
		
		for (int i=0; i<lines.size(); i++) {
			if (!end)
				sb.append(lines.get(i)+",\n");
			else {
				if (i+1 == lines.size())
					sb.append(lines.get(i) + ".\n");
				else
					sb.append(lines.get(i)+",\n");
			}
		}
	}


	/**
	 * @param depositDir Directory where the resulting file is saved. The value has to be the directory path where file server store static files.
	 * @param agr Agreement for which the file will be removed
	 * @return
	 */
	public static String deleteFile(String depositDir, boolean sign, TranslationAgreement agr, boolean multi){
		String fileName;
		if (multi) {
			fileName = agr.getName() + ".tpl";
		}else {
			TrustScheme ts = agr.getTarget();
			fileName = ts.getName() + ".tpl";
		}

		String canonicalName = depositDir + "/" + fileName;
		String signedFileName = fileName + ".p7s"; 
		String signedCanonicalName = depositDir+ "/signed/" + signedFileName;
		String unsignedCanonicalName = depositDir+ "/signed/" + fileName;


		try {
			File f = new File (canonicalName);

			logger.debug("Trying to delete file: "+ canonicalName);
			if (f.exists() && f.isFile()){
				Files.delete(f.toPath());
			}
			String fileN;
			if (sign) {
				fileN = signedCanonicalName;
			}else {
				fileN = unsignedCanonicalName;
			}
			f = new File (fileN);
			logger.debug("Trying to delete file: "+ fileN);

			if (f.exists()&&f.isFile()){
				Files.delete(f.toPath());
			}

			if (!multi && !(TTModelST.getModel().getAgreementsbyTrustScheme(agr.getTarget().getName()).isEmpty())) {
				buildFile(depositDir,sign, agr, multi);
				return "";
			}
		}catch(Exception e) {
			logger.error(e,e);
			return null;
		}

		return fileName;
	}



}
