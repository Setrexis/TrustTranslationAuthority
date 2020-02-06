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

package com.tta.commons.connectors.istorage.db;

import java.util.Arrays;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.tta.commons.conf.Configuration;
import com.tta.commons.cte.PropertyNames;

/**
 * 
 * Class to manage the connection witht the Mongo DB
 * 
 * class properties:
 *		mc_: single tone of Mongo Connector
 *
 */
public final class MCST{
	  private static MongoClient mc_ = null;
	  
	  private MCST() {}
	  
	  
	  /**
	   * @return mongoClient object
	   */
	  public static MongoClient getMC(){
	    
	    if (mc_ == null){
	      String dbuser = Configuration.getConfiguration().getProperty (PropertyNames.MONGO_USER);
	      String dbname = Configuration.getConfiguration().getProperty (PropertyNames.MONGO_DB_NAME);
	      char[] dbpwd = Configuration.getConfiguration().getProperty (PropertyNames.MONGO_PW).toCharArray();
	      
	      MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(dbuser, dbname, dbpwd);
	      
	      String dburl = Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_URL);
	      String dbport = Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_PORT);
	      
	      mc_ = new MongoClient (new ServerAddress(dburl, Integer.parseInt(dbport)), Arrays.asList(mongoCredential));
	    }
	    return mc_;
	    
	  }
}
