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


/**
 * 
 * class SigningProperties
 * 
 *  This class is a modification of class eu.europa.esig.dss.standalone.model 
 *  which you can find in 
 *  "https://github.com/esig/dss-demonstrations/blob/master/dss-standalone-app/src/main/java/eu/europa/esig/dss/standalone/model/SignatureModel.java"
 *  
 *  The present class result of a modification to avoid dependencies with javafx related to initialization and Beans.
 *  
 *  */
package com.tta.file_manager.security;

import java.io.File;

import eu.europa.esig.dss.ASiCContainerType;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureForm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignaturePackaging;
import eu.europa.esig.dss.SignatureTokenType;


public class SigningProperties {

	
	private ASiCContainerType asicContainerType = null;
	private SignatureForm signatureForm = null;
	private SignaturePackaging signaturePackaging = null;
	private SignatureLevel signatureLevel = null;
	private DigestAlgorithm digestAlgorithm = null;
	private SignatureTokenType tokenType = null;
	
	

	private File pkcsFile = null;
	private String password = null;
	
	private File fileToSign = null;
	public File getFileToSign() {
		return fileToSign;
	}
	public void setFileToSign(File fileToSign) {
		this.fileToSign = fileToSign;
	}
	
	public ASiCContainerType getAsicContainerType() {
		return asicContainerType;
	}
	public void setAsicContainerType(ASiCContainerType asicContainerType) {
		this.asicContainerType = asicContainerType;
	}
	public SignatureForm getSignatureForm() {
		return signatureForm;
	}
	public void setSignatureForm(SignatureForm signatureForm) {
		this.signatureForm = signatureForm;
	}
	public SignaturePackaging getSignaturePackaging() {
		return signaturePackaging;
	}
	public void setSignaturePackaging(SignaturePackaging signaturePackaging) {
		this.signaturePackaging = signaturePackaging;
	}
	public SignatureLevel getSignatureLevel() {
		return signatureLevel;
	}
	public void setSignatureLevel(SignatureLevel signatureLevel) {
		this.signatureLevel = signatureLevel;
	}
	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}
	public void setDigestAlgorithm(DigestAlgorithm digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
	}
	public SignatureTokenType getTokenType() {
		return tokenType;
	}
	public void setTokenType(SignatureTokenType tokenType) {
		this.tokenType = tokenType;
	}
	public File getPkcsFile() {
		return pkcsFile;
	}
	public void setPkcsFile(File pkcsFile) {
		this.pkcsFile = pkcsFile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
