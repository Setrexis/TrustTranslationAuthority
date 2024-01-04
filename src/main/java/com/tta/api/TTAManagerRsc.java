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
package com.tta.api;





import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;

import com.tta.commons.connectors.istorage.IConnector;
import com.tta.commons.connectors.istorage.InternalStorageConnectorFactory;
import com.tta.commons.error.CallResult;
import com.tta.manager.Manager;

/**
 * 
 * class TTAManagerRsc
 * 
 *  Implementation of the REST API for TTA component 
 *  
 *  */

@Path("/rsc")
public class TTAManagerRsc {

	static boolean initialized_ = false;
	private Logger logger = Logger.getLogger(TTAManagerRsc.class);


	/**
	 * Initialization of the TTA component, this method load  in memory all Translation declarations from DB or File system
	 * 
	 */
	static void initialize() {
		PropertyConfigurator.configure("/usr/local/tomcat/conf/rest_log4j.properties");

		IConnector conn = InternalStorageConnectorFactory.getConnector();
		conn.initialize();

		initialized_ = true;
	}

	/**
	 * general test method
	 * 
	 */
	@GET
	@Path("/{name}")
	public Response getMsg(@PathParam("name") String name) {

		String output = "Welcome   : " + name;

		if (!initialized_) {
			initialize();
		}

		return Response.status(200).entity(output).build();

	}

	/**
	 * general test method
	 * 
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response test(){
		if (!initialized_) {
			initialize();
		}

		return Response.status(Response.Status.OK).entity("tta component").build();
	}

	/**
	 * Method to retrieve the name of all existing Translation declarations
	 * @return http response with JSON doc containing all translation declarations
	 */
	@GET
	@Path("translation")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTranslations() {
		if (!initialized_) {
			initialize();
		}

		Manager manager = new Manager();
		JSONObject jo = manager.getAgreementNames();

		return Response.status(Response.Status.OK).entity(jo.toString()).build();
	}

	/**
	 * Method to retrieve the details of a Translation declaration
	 * @param commandDetails JSON doc whit the name of the agreement of translation declaration
	 * @return http response with JSON doc containing description of the agreement of translation declaration
	 */
	@GET
	@Path("translation/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTranslationDetails(@PathParam("name") String name) {
		if (!initialized_) {
			initialize();
		}

		Manager manager = new Manager();
		JSONObject jo = manager.getTranslationByName(name);
		if (jo != null)
			return Response.status(Response.Status.OK).entity(jo.toString()).build();
		else
			return Response.status(Response.Status.NOT_FOUND).build();

	}
	
	
	/**
	 * @return a JSON list containing all the trust schemes defined into Translations
	 */
	@GET
	@Path("trustScheme")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTrustSchemes() {
		if (!initialized_) {
			initialize();
		}

		Manager manager = new Manager();
		JSONObject jo = manager.getTrustSchemeNames();

		return Response.status(Response.Status.OK).entity(jo.toString()).build();
	}
	
	/**
	 * 
	 * @param name of trust scheme
	 * @return a JSON list with all translation agreements which contains the named trust scheme
	 */
	@GET
	@Path("getAgreementsRelatedToATrustScheme/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAgremenstOfTS(@PathParam("name") String name) {
		
		Manager manager = new Manager();
		JSONObject joresult = manager.getAgreemetsOfATrustScheme(name);
		
		return Response.status(Response.Status.OK).entity(joresult.toString()).build();
	}

	/**
	 * Method to delete a Translation declaration
	 * @param commandDetails JSON doc whit the name of the agreement to delete
	 * @return http result code
	 */

	/**
	 * @param name of the translation agreement to delete
	 * @return HTTP result code
	 */
	@DELETE
	@Path("translation/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteTranslation(@PathParam("name") String name) {
		if (!initialized_) {
			initialize();
		}
		
		logger.debug ("deleteTranslation method details: " + name);
		
		Manager manager = new Manager();
		CallResult cr = manager.removeAgreement(name);
		
		if (!cr.isOK()) {
			return Response.status(Response.Status.NOT_FOUND).entity(cr.getTxt()).build();
		}else {
			return Response.status(Response.Status.OK).entity("").build();
		}
		
	}
		
		
	/**
	 * Method to create a Translation declaration
	 * @param commandDetails JSON doc whit the details of the agreement for this translation declaration
	 * @return http result code with the description of the result. In 200Ok case it contains the name of the created xml and tpl files.
	 */

	
	//REVISED
	@POST
	@Path("translation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response createTranslation(String commandDetails){

		if (!initialized_) {
			initialize();
		}

		logger.debug ("createTraslation method details: " + commandDetails);
		
		//creates the agreement data structure
		Manager manager = new Manager();
		CallResult cr = manager.createAgreement(commandDetails, false);
		
		if (!cr.isOK()) {
			return Response.status(Response.Status.CONFLICT).entity(cr.getTxt()).build();
		}else {
			return Response.status(Response.Status.OK).entity(cr.getTxt()).build();
		}

	}

	/**
	 * 
	 * @param trust scheme name
	 * @return details of the named trust scheme in json format, in hte case of ordinal and tupple base schemes the answer is a list of all deffinitions
	 * available for the trust scheme.
	 */
	    @GET
	    @Path("trustScheme/{name}")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response getChemeLevelDetails(@PathParam("name") String name){
	     
	    	Manager manager = new Manager();
	    	CallResult cr = manager.getSchemeDetails(name);
	    	if (!cr.isOK()) {
				return Response.status(Response.Status.NOT_FOUND).entity(cr.getTxt()).build();
			}else {
				return Response.status(Response.Status.OK).entity(cr.getTxt()).build();
			}
	    }
	  


}

