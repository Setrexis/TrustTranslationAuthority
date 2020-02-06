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
package com.tta.file_manager.security;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore.PasswordProtection;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.io.BaseEncoding;
import com.tta.commons.connectors.dns.DnsConnector;

import eu.europa.esig.dss.BLevelParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.RemoteCertificate;
import eu.europa.esig.dss.RemoteDocument;
import eu.europa.esig.dss.RemoteSignatureParameters;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.signature.RemoteDocumentSignatureService;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificateToken;



/**
 * 
 * class SigningProperties
 * 
 *  This class is a modification of class eu.europa.esig.dss.standalone.task.SigningTask 
 *  which you can find in 
 *  "https://github.com/esig/dss-demonstrations/blob/master/dss-standalone-app/src/main/java/eu/europa/esig/dss/standalone/task/SigningTask.java"
 *  
 *  The present class result of a modification to avoid dependencies with javafx.
 *  
 *
 */
public class FileSigner {
	Logger logger = Logger.getLogger(this.getClass());

	private RemoteDocumentSignatureServiceImpl service;
	private SigningProperties properties;

	/**
	 * @param service
	 * @param properties
	 */
	public FileSigner (RemoteDocumentSignatureService<RemoteDocument, RemoteSignatureParameters>service, SigningProperties properties) {
		this.service = (RemoteDocumentSignatureServiceImpl) service;
		this.properties = properties;
	}


	/**
	 * @return
	 */
	public DSSDocument doSign() {
		try {
			SignatureTokenConnection token = getToken(properties);
			if (token == null)
				return null;
			List<DSSPrivateKeyEntry> keys = token.getKeys();
			DSSPrivateKeyEntry signer = keys.get(0);
			//Olamide
			
			CertificateToken cert = signer.getCertificate();
			PublicKey pk = cert.getPublicKey();
			byte[] pkAssociationData = pk.getEncoded();
			byte[] certAssociationData = cert.getEncoded();
			
			logger.debug("----------------->format of the pk: "+pk.getFormat());
			logger.debug("----------------->algorithm of the pk: "+pk.getAlgorithm());
			logger.debug("----------------->encoded of the pk: " + new String(pk.getEncoded()));
			
			String data = BaseEncoding.base16().encode(pkAssociationData);
			DnsConnector.setPK(data);
			
			logger.debug("------------------>pk encoded in base16: " + data);
			//Olamide
			FileDocument fileToSign = new FileDocument(properties.getFileToSign());

			InputStream is = fileToSign.openStream();
			byte[] ba = Utils.toByteArray(is);
			RemoteDocument toSignDocument = new RemoteDocument(ba, fileToSign.getMimeType(), fileToSign.getName());
			RemoteSignatureParameters parameters = buildParameters(signer);
			ToBeSigned toBeSigned = getDataToSign(toSignDocument, parameters);
			SignatureValue signatureValue = signDigest(token, signer, toBeSigned);

			is.close();

			return signDocument(toSignDocument, parameters, signatureValue);


		} catch (IOException e) {
			logger.error(e,e);
			return null;
		}
	}


	/**
	 * @param toSignDocument
	 * @param parameters
	 * @param signatureValue
	 * @return
	 */
	private DSSDocument signDocument(RemoteDocument toSignDocument, RemoteSignatureParameters parameters, SignatureValue signatureValue) {
		DSSDocument signDocument = null;
		try {
			signDocument = service.signDocument(toSignDocument, parameters, signatureValue);
		} catch (Exception e) {
			logger.error(e,e);
			return null;
		}
		return signDocument;
	}



	/**
	 * @param model
	 * @return
	 * @throws IOException
	 */
	private SignatureTokenConnection getToken(SigningProperties model) throws IOException {
		try {
			switch (model.getTokenType()) {
			case PKCS11:
				return new Pkcs11SignatureToken(model.getPkcsFile().getAbsolutePath(), new PasswordProtection(model.getPassword().toCharArray()));
			case PKCS12:
				return new Pkcs12SignatureToken(model.getPkcsFile(), new PasswordProtection(model.getPassword().toCharArray()));
			case MSCAPI:
				return new MSCAPISignatureToken();
			default:
				throw new IllegalArgumentException("Unsupported token type " + model.getTokenType());
			}
		}catch(Exception e) {
			logger.error (e,e);
			return null;
		}
	}

	/**
	 * @param signer
	 * @return
	 */
	private RemoteSignatureParameters buildParameters(DSSPrivateKeyEntry signer) {
		RemoteSignatureParameters parameters = new RemoteSignatureParameters();
		parameters.setAsicContainerType(properties.getAsicContainerType());
		parameters.setDigestAlgorithm(properties.getDigestAlgorithm());
		parameters.setSignatureLevel(properties.getSignatureLevel());
		parameters.setSignaturePackaging(properties.getSignaturePackaging());
		BLevelParameters bLevelParams = new BLevelParameters();
		bLevelParams.setSigningDate(new Date());
		parameters.setBLevelParams(bLevelParams);
		parameters.setSigningCertificate(new RemoteCertificate(signer.getCertificate().getEncoded()));
		parameters.setEncryptionAlgorithm(signer.getEncryptionAlgorithm());
		CertificateToken[] certificateChain = signer.getCertificateChain();
		if (Utils.isArrayNotEmpty(certificateChain)) {
			List<RemoteCertificate> certificateChainList = new ArrayList<>();
			for (CertificateToken certificateToken : certificateChain) {
				certificateChainList.add(new RemoteCertificate(certificateToken.getEncoded()));
			}
			parameters.setCertificateChain(certificateChainList);
		}

		return parameters;
	}


	/**
	 * @param toSignDocument
	 * @param parameters
	 * @return
	 */
	private ToBeSigned getDataToSign(RemoteDocument toSignDocument, RemoteSignatureParameters parameters) {
		ToBeSigned toBeSigned = null;
		try {
			toBeSigned = service.getDataToSign(toSignDocument, parameters);
		} catch (Exception e) {
			logger.error("Unable to compute the digest to sign", e);
			return null;
		}
		return toBeSigned;
	}

	/**
	 * @param token
	 * @param signer
	 * @param toBeSigned
	 * @return
	 */
	private SignatureValue signDigest(SignatureTokenConnection token, DSSPrivateKeyEntry signer, ToBeSigned toBeSigned) {
		SignatureValue signatureValue = null;
		try {
			signatureValue = token.sign(toBeSigned, properties.getDigestAlgorithm(), signer);
		} catch (Exception e) {
			logger.error("Unable to sign the digest", e);
			return null;
		}
		return signatureValue;
	}


}