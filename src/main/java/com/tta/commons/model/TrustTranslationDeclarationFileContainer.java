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

package com.tta.commons.model;

import java.util.HashMap;

/**
 * @author a101866
 *
 * This class is used to store the relation of each trasnalation declaration and the file where it is published.
 */
public class TrustTranslationDeclarationFileContainer {
	private HashMap<String,String>ttdHM;
	
	/**
	 * Constructor
	 */
	public TrustTranslationDeclarationFileContainer (){
		ttdHM = new HashMap<>();
	}
	
	/**
	 * @param file file name
	 * @param fullPath path + file
	 * @return
	 */
	public boolean addFile (String file, String fullPath) {
		if (!ttdHM.containsKey(file)) {
			ttdHM.put(file, fullPath);
			return true;
		}
		return false;
			
	}
	
	/**
	 * @param file name
	 * @return
	 */
	public boolean deleteFile (String file) {
		return (ttdHM.remove(file)!=null);
		
	}
	
	/**
	 * @param file
	 * @return the path + file of the file
	 */
	public String getFile(String file) {
		return ttdHM.get(file);
	}

}
