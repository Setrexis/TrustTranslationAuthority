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

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.tta.commons.conf.Configuration;
import com.tta.commons.connectors.istorage.IConnector;
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.error.CallResult;
import com.tta.commons.model.ModelParser;
import com.tta.commons.model.TranslationAgreement;


/**
 * 
 * Implementation of a connector for MongoDB. Specific for TTA module
 * @see //https://www.mkyong.com/mongodb/java-mongodb-query-document/
 * 
 * @param mongoClient single tone mongoClient object
 * 
 */
public class MongoConnector implements IConnector{
  
  private static Logger logger = Logger.getLogger("MongoConnector.class");

  private static final String AGR_NAME="agreementName";
  
  @Override
	public boolean initialize() {
		return false;
  }

  
  /**
   * Retrieve agreements whose name contains the value in val paremeter
   * @param val Value to filter names
   * @return List of agreements
   */
  public List<Document> getAgreementsWithValcontainedInName (String val){
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    
    ArrayList<Document> docAL = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_AGR_COLL));
      if (mColl != null){
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put(AGR_NAME, "^.*" + val + ".*$"); 
        
        FindIterable<Document> docIt = mColl.find(regexQuery);
        Iterator<Document> it = docIt.iterator();
        if (it.hasNext()){
          docAL = new ArrayList<>();
          while (it.hasNext()){
            docAL.add(it.next());
          }
        }
      }
    }
    
    if (mColl != null)
      mColl.drop();
    return docAL; 
  }
  
  /**
   * Retrieve an agreement whose name is equal to val paremeter
   * @param val Value to filter name
   * @return List of agreements
   */
  public  JSONObject getAgreementByNameEqualsTo (String val){
    
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    
    Document doc = null;
    JSONObject jo = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_AGR_COLL));
      if (mColl != null){
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put(AGR_NAME, val); 
        
        FindIterable<Document> docIt = mColl.find(regexQuery);
        Iterator<Document> it = docIt.iterator();
        if (it.hasNext()){
        	doc = it.next();
        	
        	String s = (String) doc.get("agreement");
		    jo = new JSONObject(s);
		    
		    String name = jo.getString("name");
			if (name.equalsIgnoreCase(val)) {
				return jo;
			}
        }
      }
    }
      
    if (mColl != null)
      mColl.drop();
    
    return jo; 
  }
  
  /**
   * @param agr Agreement object
   * @param agreement Agreement description in JSON format
   * @return true if the agreement has been stored, false in case of error (see log file)
   */
  public CallResult addAgreement (TranslationAgreement agr, JSONObject agreement){
    return addAgreement (Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME), Configuration.getConfiguration().getProperty(PropertyNames.MONGO_AGR_COLL), agr, agreement);
  }
  
  
  /**
   * @param dbName Name of the Date Base
   * @param collName Name of the Collection
   * @param agr Agreement object
   * @param agreement Agreement description in JSON format
   * @return true if the agreement has been stored, false in case of error (see log file)
   */
  public CallResult addAgreement (String dbName, String collName, TranslationAgreement agr, JSONObject agreement){
    
    try{
      
      MongoDatabase db = MCST.getMC().getDatabase(dbName);
      MongoCollection<Document> mColl = null;
      boolean r = false;
      
      if (db != null){
        mColl = db.getCollection(collName);
        
        Document doc = new Document();
        doc.put(AGR_NAME, agr.getName());
        doc.put("origin", agr.getSource().getName());
        doc.put("target", agr.getTarget().getName());
        doc.put("agreement", agreement.toString());
        
        mColl.insertOne(doc);
        r = true;
      }
      return new CallResult(r,"");
    
    }catch(Exception e){
      logger.error(e,e);
      return new CallResult (false,e.getMessage());
    }
  }
  
  
  /**
   * @param name Name of the agreement to be deleted from DB
   * @return true if OK, true in case of error
   */
  public CallResult deleteAgreementByName(String name){
    boolean r = true;
    
    try{
      BasicDBObject query = new BasicDBObject();
    
      query.append("name", name);
      
      MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
      MongoCollection<Document> mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_AGR_COLL));
      DeleteResult dr = mColl.deleteOne(eq(AGR_NAME, name));
      
      if (dr.getDeletedCount() < 1)
        r = false;
    
      return new CallResult (r, "");
    }catch(Exception e){
      logger.error(e,e);
      return new CallResult (false, e.getMessage());
    }
  }
  
  
  
  /**
   * @return A MAP object containing all agreements stored into de DataBase
   */
  public  Map<String,TranslationAgreement> getAllAgreements (){
    
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    HashMap<String, TranslationAgreement> agreementHM = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_AGR_COLL));
      if (mColl != null){
        
        
        FindIterable<Document> docIt = mColl.find();
        Iterator<Document> it = docIt.iterator();
        if (it.hasNext()){
          agreementHM = new HashMap<>();
          while (it.hasNext()){
             Document doc = it.next();
             JSONObject jo =  new JSONObject(doc.toJson());
             TranslationAgreement agr = ModelParser.parseAgreement(jo);
             agreementHM.put(agr.getName(), agr);
          }
        }
      }
      
    }
    
    if (mColl != null)
      mColl.drop();
    return agreementHM; 
  }



  
 
  
}
