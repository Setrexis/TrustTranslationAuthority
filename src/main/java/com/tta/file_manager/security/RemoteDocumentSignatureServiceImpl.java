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



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.ASiCContainerType;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.RemoteConverter;
import eu.europa.esig.dss.RemoteDocument;
import eu.europa.esig.dss.RemoteSignatureParameters;
import eu.europa.esig.dss.SignatureForm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.asic.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.signature.AbstractRemoteSignatureServiceImpl;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.signature.RemoteDocumentSignatureService;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;

/**
 * 
 * class RemoteDocumentSignatureServiceImpl
 * 
 *  This class is a modification of class eu.europa.esig.dss.signature.RemoteDocumentSignatureServiceImpl
 *  which you can find in 
 *  "https://github.com/esig/dss/blob/master/dss-remote-services/src/main/java/eu/europa/esig/dss/signature/RemoteDocumentSignatureServiceImpl.java"
 *  
 *  The present class result of a modification to include class constructor and do not use Bean initialization.
 *  
 *  */


@SuppressWarnings("serial")
public class RemoteDocumentSignatureServiceImpl extends AbstractRemoteSignatureServiceImpl
		implements RemoteDocumentSignatureService<RemoteDocument, RemoteSignatureParameters> {

	private static final Logger LOG = LoggerFactory.getLogger(RemoteDocumentSignatureServiceImpl.class);

	private DocumentSignatureService<XAdESSignatureParameters> xadesService;

	private DocumentSignatureService<CAdESSignatureParameters> cadesService;

	private DocumentSignatureService<PAdESSignatureParameters> padesService;

	private DocumentSignatureService<ASiCWithXAdESSignatureParameters> asicWithXAdESService;

	private DocumentSignatureService<ASiCWithCAdESSignatureParameters> asicWithCAdESService;
	
	/**
	 * @param xadesService
	 * @param cadesService
	 * @param padesService
	 * @param asicWithXAdESService
	 * @param asicWithCAdESService
	 */
	public RemoteDocumentSignatureServiceImpl (DocumentSignatureService<XAdESSignatureParameters> xadesService,
                                               DocumentSignatureService<CAdESSignatureParameters> cadesService,
                                               DocumentSignatureService<PAdESSignatureParameters> padesService,
                                               DocumentSignatureService<ASiCWithXAdESSignatureParameters> asicWithXAdESService,
                                               DocumentSignatureService<ASiCWithCAdESSignatureParameters> asicWithCAdESService) {
		
		this.xadesService = xadesService;
		this.cadesService = cadesService;
		this.padesService = padesService;
		this.asicWithXAdESService = asicWithXAdESService;
		this.asicWithCAdESService = asicWithCAdESService;
	}

	/**
	 * @param xadesService
	 */
	public void setXadesService(DocumentSignatureService<XAdESSignatureParameters> xadesService) {
		this.xadesService = xadesService;
	}

	/**
	 * @param cadesService
	 */
	public void setCadesService(DocumentSignatureService<CAdESSignatureParameters> cadesService) {
		this.cadesService = cadesService;
	}

	/**
	 * @param padesService
	 */
	public void setPadesService(DocumentSignatureService<PAdESSignatureParameters> padesService) {
		this.padesService = padesService;
	}

	/**
	 * @param asicWithXAdESService
	 */
	public void setAsicWithXAdESService(DocumentSignatureService<ASiCWithXAdESSignatureParameters> asicWithXAdESService) {
		this.asicWithXAdESService = asicWithXAdESService;
	}

	/**
	 * @param asicWithCAdESService
	 */
	public void setAsicWithCAdESService(DocumentSignatureService<ASiCWithCAdESSignatureParameters> asicWithCAdESService) {
		this.asicWithCAdESService = asicWithCAdESService;
	}

	/**
	 * @param parameters
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private DocumentSignatureService getServiceForSignature(RemoteSignatureParameters parameters) {
		ASiCContainerType asicContainerType = parameters.getAsicContainerType();
		SignatureLevel signatureLevel = parameters.getSignatureLevel();
		SignatureForm signatureForm = signatureLevel.getSignatureForm();
		if (asicContainerType != null) {
			switch (signatureForm) {
			case XAdES:
				return asicWithXAdESService;
			case CAdES:
				return asicWithCAdESService;
			default:
				throw new DSSException("Unrecognized format (XAdES or CAdES are allowed with ASiC) : " + signatureForm);
			}
		} else {
			switch (signatureForm) {
			case XAdES:
				return xadesService;
			case CAdES:
				return cadesService;
			case PAdES:
				return padesService;
			default:
				throw new DSSException("Unrecognized format " + signatureLevel);
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.europa.esig.dss.signature.RemoteDocumentSignatureService#getDataToSign(java.lang.Object, eu.europa.esig.dss.AbstractSerializableSignatureParameters)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ToBeSigned getDataToSign(RemoteDocument remoteDocument, RemoteSignatureParameters remoteParameters) throws DSSException {
		LOG.info("GetDataToSign in process...");
		AbstractSignatureParameters parameters = createParameters(remoteParameters);
		DocumentSignatureService service = getServiceForSignature(remoteParameters);
		DSSDocument dssDocument = RemoteConverter.toDSSDocument(remoteDocument);
		ToBeSigned dataToSign = service.getDataToSign(dssDocument, parameters);
		LOG.info("GetDataToSign is finished");
		return dataToSign;
	}

	/* (non-Javadoc)
	 * @see eu.europa.esig.dss.signature.RemoteDocumentSignatureService#signDocument(java.lang.Object, eu.europa.esig.dss.AbstractSerializableSignatureParameters, eu.europa.esig.dss.SignatureValue)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DSSDocument signDocument(RemoteDocument remoteDocument, RemoteSignatureParameters remoteParameters, SignatureValue signatureValue)
			throws DSSException {
		LOG.info("SignDocument in process...");
		AbstractSignatureParameters parameters = createParameters(remoteParameters);
		DocumentSignatureService service = getServiceForSignature(remoteParameters);
		DSSDocument dssDocument = RemoteConverter.toDSSDocument(remoteDocument);
		DSSDocument signDocument = service.signDocument(dssDocument, parameters, signatureValue);
		LOG.info("SignDocument is finished");
		return signDocument;
	}

	/* (non-Javadoc)
	 * @see eu.europa.esig.dss.signature.RemoteDocumentSignatureService#extendDocument(java.lang.Object, eu.europa.esig.dss.AbstractSerializableSignatureParameters)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DSSDocument extendDocument(RemoteDocument remoteDocument, RemoteSignatureParameters remoteParameters) throws DSSException {
		LOG.info("ExtendDocument in process...");
		AbstractSignatureParameters parameters = createParameters(remoteParameters);
		DocumentSignatureService service = getServiceForSignature(remoteParameters);
		DSSDocument dssDocument = RemoteConverter.toDSSDocument(remoteDocument);
		DSSDocument extendDocument = service.extendDocument(dssDocument, parameters);
		LOG.info("ExtendDocument is finished");
		return extendDocument;
	}

}

