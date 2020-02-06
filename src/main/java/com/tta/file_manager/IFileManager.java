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

import com.tta.commons.error.CallResult;
import com.tta.commons.model.TranslationAgreement;

public interface IFileManager {

	/**
	 * This method creates the trust translation declaration or list files depending on how TTA is set, it can create a file per translation agreement
	 * or a file containing all translation agreements in which a trust scheme participates.
	 * @param agr
	 * @return CallResult object
	 */
	public CallResult createTranslationFiles(TranslationAgreement agr);
	/**
	 * this method deletes the Trust Translation declaration files according to how they where created, per agreement or per Trust scheme.
	 * @param agr
	 * @return
	 */
	public CallResult deleteTranslationFiles(TranslationAgreement agr);
	
	/**
	 * this method recreates the reference to translation files when TTA is restarted.
	 * @param agr
	 * @return
	 */
	public void addFilenameToContainer(TranslationAgreement agr);

}
