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
 * AgreementContainer class of model for TTA api of dao module
 * 
 * @param agrCont_ MAP object to store all agreements
 */
public class TranslationAgreementContainer {
  private HashMap<String,TranslationAgreement> agrCont;
  
  /**
   * Constructor
   */
  public TranslationAgreementContainer(){
    this.agrCont = new HashMap<>();
  }
  
  /**
   * @param name of an agreement
   * @return true if the container store an agreement with the name passed as parameter
   */
  public boolean containsKey (String name){
    return agrCont.containsKey(name);
  }
  
  /**
   * @param agr agreement Object
   * @return true if the container store an agreement with all properties with the same value
   */
  public boolean containsAgr (TranslationAgreement agr){
    boolean r = false;
    
    TranslationAgreement myAgr = agrCont.get(agr.getName());
    if ((myAgr != null) && (myAgr.isEqualTo(agr))) {
        r = true;
    }
    return r;
  }
  
  /**
   * @param agr Agreement to store
   */
  public void addAgreement (TranslationAgreement agr){
    if (!this.containsAgr(agr)){
      agrCont.put(agr.getName(), agr);
    }
    
    
  }
  
  /**
   * remove an agreement from the storage 
   * @param agr 
   */
  public void removeAgreement(TranslationAgreement agr){
    if (this.containsAgr(agr)){
      TranslationAgreement myAgr = agrCont.get(agr.getName());
      myAgr.getSource().removemeAsOrigin(myAgr.getTarget().getName());
      myAgr.getTarget().removemeAsTarget(myAgr.getSource().getName());
      agrCont.remove(agr.getName());
      
    }
  }
  
  /**
   * remove an agreement from the container which has the name passed as parameter
   * @param name
   */
  public TranslationAgreement removeAgreement(String name){
    if (this.containsKey(name)){
      TranslationAgreement myAgr = agrCont.get(name);
      myAgr.getSource().removemeAsOrigin(myAgr.getName());
      myAgr.getTarget().removemeAsTarget(myAgr.getName());
      return agrCont.remove(name);
      
    }
    return null;
  }
  
  /**
 * @param name
 * @return translation agreement object
 */
public TranslationAgreement getAgreement(String name) {
	  return  agrCont.get(name);
  }
}
