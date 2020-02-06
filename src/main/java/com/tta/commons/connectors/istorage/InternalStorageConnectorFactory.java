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

package com.tta.commons.connectors.istorage;

import com.tta.commons.conf.Configuration;
import com.tta.commons.connectors.istorage.db.MongoConnector;
import com.tta.commons.connectors.istorage.file.FileS;
import com.tta.commons.cte.PropertyNames;


/**
 * 
 * class ConnectorFactory.
 * 
 * Factory for storage of translation declarations in json format
 * 
 */
public class InternalStorageConnectorFactory {
	
	private InternalStorageConnectorFactory() {}
	
	/**
	 * @return an implementation of IConnector depending on the internal storage, this is indicated by "db_installed" configuration param
	 * if db_installed = true it returns a MongoConnector object
	 * if db_installed = false it returns a FileS object
	 */
	public static IConnector getConnector () {
		if (Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_INSTALLED).equalsIgnoreCase("false")) {
			return new FileS();
		}else {
			return new MongoConnector();
		}
	}
}
