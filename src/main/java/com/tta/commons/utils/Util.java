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

package com.tta.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.google.common.io.ByteStreams;

import eu.europa.esig.dss.DSSDocument;


/**
 * 
 * class for general purpose methods
 *
 */
public class Util {
	
	static Logger logger = Logger.getLogger(Util.class);
	
	private Util() {}
	
	 /**
	   * method to save a signed document into a new file. This method is required because method save of class Document do not close properly the
	   * file descriptor of the original file when you chose a different name of the original, it cause unavailability of deleting files and memory leak.
	   */
	public static boolean save(DSSDocument signedDocument, String oFname) {
		
		File fileToSave = new File (oFname);

		
		try (FileOutputStream fos = new FileOutputStream(fileToSave)){
			
			InputStream is = signedDocument.openStream();
			ByteStreams.copy(is, fos);
			fos.flush();
			is.close();
			
			
			return true;
			
		} catch (Exception e) {
			logger.error("Unable to save file : " + e.getMessage());
			logger.error(e,e);
			
			return false;
			
		}
		
	}
	
	/**
	   * method to save a NON signed document into a new file. 
	   */
	public static boolean save (String doc, String oFname) {
		File origFile = new File (doc);
		File fileToSave = new File (oFname);
		
		try (FileOutputStream fos = new FileOutputStream(fileToSave)){
			
			InputStream is = new FileInputStream(origFile);
			
			ByteStreams.copy(is, fos);
			fos.flush();
			is.close();
			
			return true;
		}catch(Exception e) {
			logger.error("Unable to save file : " + e.getMessage());
			logger.error(e,e);
			return false;
		}
	}

}
