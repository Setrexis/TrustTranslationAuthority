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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.tta.commons.conf.Configuration;

@Path("/cfg")
public class ConfRsc {
	Logger logger_ = Logger.getLogger(ConfRsc.class);
	
	/**
	 * @return set of configuration parameters and values in json format
	 */
	@GET
	@Path("/conf")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConf() {
		Configuration cfg = Configuration.getConfiguration();
		String cfgS = cfg.getProperties();
		return Response.status(Response.Status.OK).entity(cfgS).build();
		
	}
	
	/**
	 * @param commandDetails param name and value in json format {"name":"xxx", "value":"yyy"}
	 * @return HTTP result code
	 */
	@PUT
	@Path("/param")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setParam(String commandDetails) {
		JSONObject jo = new JSONObject (commandDetails);
		String name = jo.getString("name");
		String value = jo.getString("value");
		
		Configuration cfg = Configuration.getConfiguration();
		
		boolean r = cfg.setProperty(name, value);
		
		if (r) {
			return Response.status(Response.Status.OK).build();
		}else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	

}
