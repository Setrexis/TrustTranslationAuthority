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

package com.tta.file_manager;

import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;
import com.tta.file_manager.api.FileManagerMulti;
import com.tta.file_manager.api.FileManagerSingle;

/**
 * @author a101866
 * Translation declaration files factory
 *
 */
public class FileManagerFactory {
	
	private FileManagerFactory() {}

	/**
	 * @return an implementation fo IFileManager depending on how TTA is set to create declaration files. this is set by means 
	 * of the configuration param one_file_per_scheme
	 * if one_file_per_scheme = true it return a FileManagerSingle object
	 * if one_file_per_scheme = false it return a FileManagerMulti object
	 * 
	 */
	public static IFileManager getFileManager() {
		String oneFileS = Configuration.getConfiguration().getProperty(PropertyNames.ONE_FILE_PER_SCHEME);
		boolean oneFile = Boolean.parseBoolean(oneFileS);
		
		if (oneFile)
			return new FileManagerSingle();
		else
			return new FileManagerMulti(); 
	}
}
