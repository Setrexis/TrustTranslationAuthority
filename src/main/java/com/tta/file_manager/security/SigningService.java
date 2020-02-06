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

import java.io.File;
import java.security.Security;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.SignaturePackaging;
import eu.europa.esig.dss.SignatureTokenType;
import eu.europa.esig.dss.asic.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.asic.signature.ASiCWithCAdESService;
import eu.europa.esig.dss.asic.signature.ASiCWithXAdESService;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.client.crl.OnlineCRLSource;
import eu.europa.esig.dss.client.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.client.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.client.tsp.OnlineTSPSource;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;

/**
 * 
 * class SigningService
 * 
 *  This class is used to initialize RemoteDocumentSignatureServiceImpl implemented in this project which avoid the initialization with Beans.
 *    
 *  @Param sProperties_
 *  @Param RemoteDocumentSignatureServiceImpl
 *  
 */
public class SigningService {
	
	public static final String XML = "xml";
	public static final String TPL = "tpl";
	
	private SigningProperties sProperties;
	private RemoteDocumentSignatureServiceImpl rdssl;
	
	Logger logger = Logger.getLogger(SigningService.class);
	
	static{
		BouncyCastleProvider obj = new BouncyCastleProvider();
		Security.addProvider(obj);
	}
	
	/**
	 * @param canonicalName
	 * @param fileType
	 */
	public SigningService(String canonicalName, String fileType){
	
		try {
		sProperties = new SigningProperties();
		sProperties.setFileToSign(new File(canonicalName));
		sProperties.setPkcsFile(new File (Configuration.getConfiguration().getProperty(PropertyNames.KEY_STORE_FILE)));
		sProperties.setPassword(Configuration.getConfiguration().getProperty("pkcspwd"));
		
		SignatureLevel sl;
		SignatureTokenType stt;
		SignaturePackaging sp;
		DigestAlgorithm da;
		
		Configuration conf = Configuration.getConfiguration();
		if (fileType.equalsIgnoreCase(XML)) {
			
			sl = SignatureLevel.valueByName(conf.getProperty(PropertyNames.XML_SIG_LEVEL));
			stt = SignatureTokenType.valueOf(conf.getProperty(PropertyNames.XML_TOKEN_TYPE));
			sp = SignaturePackaging.valueOf(conf.getProperty(PropertyNames.XML_SIG_PACK));
			da = DigestAlgorithm.forName(conf.getProperty(PropertyNames.XML_DIGEST_ALG));
		}else {
			sl = SignatureLevel.valueByName(conf.getProperty(PropertyNames.CAD_SIG_LEVEL));
			stt = SignatureTokenType.valueOf(conf.getProperty(PropertyNames.CAD_TOKEN_TYPE));
			sp = SignaturePackaging.valueOf(conf.getProperty(PropertyNames.CAD_SIG_PACK));
			da = DigestAlgorithm.forName(conf.getProperty(PropertyNames.CAD_DIGEST_ALG));
		}
		
		sProperties.setSignatureLevel(sl);
		sProperties.setTokenType(stt);
		sProperties.setSignaturePackaging(sp);
		sProperties.setDigestAlgorithm(da);
		
		BouncyCastleProvider obj = new BouncyCastleProvider();
		Security.addProvider(obj);
		  
		String tspServer = "http://tsa.belgium.be/connect";
		OnlineTSPSource tsp = new OnlineTSPSource(tspServer);
		CommonsDataLoader dataLoader = new CommonsDataLoader();
		tsp.setDataLoader(dataLoader);
		
		CommonCertificateVerifier ccv = new CommonCertificateVerifier();
		OnlineOCSPSource oocsps = new OnlineOCSPSource();
		oocsps.setDataLoader(dataLoader);
		ccv.setOcspSource(oocsps);
		  
		OnlineCRLSource crlSource = new OnlineCRLSource();
		crlSource.setDataLoader(dataLoader);
		ccv.setCrlSource(crlSource);
		
		DocumentSignatureService<XAdESSignatureParameters> xadesService;
		DocumentSignatureService<CAdESSignatureParameters> cadesService;
		DocumentSignatureService<PAdESSignatureParameters> padesService;
		DocumentSignatureService<ASiCWithXAdESSignatureParameters> asicWithXAdESService;
		DocumentSignatureService<ASiCWithCAdESSignatureParameters> asicWithCAdESService;
		
		cadesService = new CAdESService(ccv);
		cadesService.setTspSource(tsp);
		xadesService = new XAdESService(ccv);
		xadesService.setTspSource(tsp);
		padesService = new PAdESService(ccv);
		padesService.setTspSource(tsp);
		asicWithXAdESService = new ASiCWithXAdESService (ccv);
		asicWithXAdESService.setTspSource(tsp);
		asicWithCAdESService = new ASiCWithCAdESService (ccv);
		asicWithCAdESService.setTspSource(tsp);
		
		rdssl = new RemoteDocumentSignatureServiceImpl(xadesService, cadesService, padesService, asicWithXAdESService, asicWithCAdESService);
		  
		
		}catch(Exception e) {
			logger.error(e,e);
		}
		
		
	}
	
	/**
	 * @return
	 */
	public SigningProperties getSigningProperties() {
		return sProperties;
	}
	
	/**
	 * @return
	 */
	public RemoteDocumentSignatureServiceImpl getRemoteDocumentSignatureService() {
		return rdssl;
	}
	
	
	
}
