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
import com.tta.commons.cte.PropertyNames;
import com.tta.commons.etsi_model.TrustServiceList;


/**
 * 
 * Implementation of a connector for MongoDB. Specific for Testing feeding api of dao module
 * @see //https://www.mkyong.com/mongodb/java-mongodb-query-document/
 * 
 * @param mongoClient_ single tone mongoClient object
 * 
 */

public class TestMongoConnector {

  static Logger logger = Logger.getLogger("TestMongoConnector.class");

  
   
    
  /**
   * @return mongoClient object
   */
 
  
  /**
   * @param scheme Scheme description in JSON format
   * @return true if the Scheme has been stored, false in case of error (see log file)
   */
  public boolean addScheme (JSONObject scheme){
    return addScheme(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME), Configuration.getConfiguration().getProperty(PropertyNames.MONGO_SCHEME_COLL), scheme);
  }
  
  /**
   * @param dbName Name of the Date Base
   * @param collName Name of the Collection
   * @param scheme Scheme description in JSON format
   * @return true if the Scheme has been stored, false in case of error (see log file)
   */
  public boolean addScheme (String dbName, String collName, JSONObject scheme){
    
    MongoDatabase db = MCST.getMC().getDatabase(dbName);
    MongoCollection<Document> mColl = null;
    boolean r = false;
    
    if (db != null){
      mColl = db.getCollection(collName);
      
      Document schemeDoc = new Document();
      schemeDoc.append("schemeName", scheme.getString("name"));
      schemeDoc.append("scheme", scheme.toString());
      
      mColl.insertOne(schemeDoc);
      r = true;
    }
    return r;
  }
  
  
  /**
   * Retrieve Schemes whose name contains the value in val paremeter
   * @param field Field to look for
   * @param val Value to filter names
   * @return List of agreements
   */
  public  List<Document> getSchemesWithValcontainedInValue (String field, String val){
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    
    ArrayList<Document> docAL = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_SCHEME_COLL));
      if (mColl != null){
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put(field, "^.*" + val + ".*$"); 
        
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
   * @param field Field to look for
   * @param val Value to filter name
   * @return List of agreements
   */
  public  List<Document> getSchemeByValEqualsTo (String field, String val){
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    
    ArrayList<Document> docAL = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_SCHEME_COLL));
      if (mColl != null){
        BasicDBObject regexQuery = new BasicDBObject();
        regexQuery.put(field, val); 
        
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
   * @return A MAP object containing all schemes stored into the DataBase
   */
  public  Map<String,TrustServiceList> getAllSchemes (){
    MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
    MongoCollection<Document> mColl = null;
    HashMap<String, TrustServiceList> schemeHM = null;
    
    if (db != null){
      mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_SCHEME_COLL));
      if (mColl != null){
        
        
        FindIterable<Document> docIt = mColl.find();
        Iterator<Document> it = docIt.iterator();
        if (it.hasNext()){
          schemeHM = new HashMap<>();
          while (it.hasNext()){
             Document doc = it.next();
             JSONObject jo =  new JSONObject(doc.toJson());
             TrustServiceList sch = new TrustServiceList(jo);
             schemeHM.put(sch.getName(), sch);
          }
        }
      }
      
    }
    
    if (mColl != null)
      mColl.drop();
    return schemeHM; 
  }
  
  /**
   * @param name Name of the scheme to be removed from data base
   * @return true if the scheme was removed, false in case of error
   */
  public boolean deleteSchemeByName(String name){
    boolean r = true;
    
    try{
      BasicDBObject query = new BasicDBObject();
      query.append("name", name);
      
      MongoDatabase db = MCST.getMC().getDatabase(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_DB_NAME));
      MongoCollection<Document> mColl = db.getCollection(Configuration.getConfiguration().getProperty(PropertyNames.MONGO_SCHEME_COLL));
      DeleteResult dr = mColl.deleteOne(eq("name", name));
      
      if (dr.getDeletedCount() < 1)
        r = false;
      
      return r;
    }catch (Exception e){
      logger.error(e,e);
      return false;
    }
  }
}
