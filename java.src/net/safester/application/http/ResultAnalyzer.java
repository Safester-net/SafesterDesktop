/*
 * This file is part of AceQL Client SDK.
 * AceQL Client SDK: Remote JDBC access over HTTP with AceQL HTTP.                                 
 * Copyright (C) 2017,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.                                
 *                                                                               
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.safester.application.http;

import java.io.StringReader;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;

/**
 * 
 * Analyses the JSON result sent by server
 * 
 * @author Nicolas de Pomereu
 *
 */
public class ResultAnalyzer {

    public static boolean DEBUG = false;

    private String jsonResult = null;

    /**
     * Constructor
     * 
     * @param jsonResult
     */
    public ResultAnalyzer(String jsonResult) {

	if (jsonResult == null) {
	    throw new NullPointerException("jsonResult is null!");
	}

	jsonResult = jsonResult.trim();
	this.jsonResult = jsonResult;
    }

    /**
     * Says if status is OK
     * 
     * @return true if status is OK
     */
    public boolean isStatusOk() {

	if (jsonResult == null || jsonResult.isEmpty()) {
	    return false;
	}

	JsonReader reader = Json.createReader(new StringReader(jsonResult));
	JsonStructure jsonst = reader.read();

	JsonObject object = (JsonObject) jsonst;
	JsonString status = (JsonString) object.get("status");

	if (status != null && status.getString().equals("OK")) {
	    return true;
	} else {
	    return false;
	}

    }

    /**
     * Returns the error_message in case of failure
     * 
     * @return the error_message in case of failure, null if no error
     */
    public String getExceptionName() {

	JsonReader reader = Json.createReader(new StringReader(jsonResult));
	JsonStructure jsonst = reader.read();

	JsonObject object = (JsonObject) jsonst;
	JsonString status = (JsonString) object.get("status");

	if (status == null) {
	    return null;
	}

	JsonString exceptionName = (JsonString) object.get("exceptionName");
	if (exceptionName == null) {
	    return null;
	} else {
	    return exceptionName.getString();
	}

    }

    /**
     * Returns the error_message in case of failure
     * 
     * @return the error_message in case of failure, null if no error
     */
    public String getErrorMessage() {

	JsonReader reader = Json.createReader(new StringReader(jsonResult));
	JsonStructure jsonst = reader.read();

	JsonObject object = (JsonObject) jsonst;
	JsonString status = (JsonString) object.get("status");

	if (status == null) {
	    return null;
	}

	JsonString errorMessage = (JsonString) object.get("errorMessage");
	if (errorMessage == null) {
	    return null;
	} else {
	    return errorMessage.getString();
	}

    }

    /**
     * Returns the stack_trace in case of failure
     * 
     * @return the stack_trace in case of failure, null if no stack_trace
     */
    public String getExceptionStackTrace() {

	JsonReader reader = Json.createReader(new StringReader(jsonResult));
	JsonStructure jsonst = reader.read();

	JsonObject object = (JsonObject) jsonst;
	JsonString status = (JsonString) object.get("status");

	if (status == null) {
	    return null;
	}

	JsonString exceptionStackTrace = (JsonString) object.get("exceptionStackTrace");
	if (exceptionStackTrace == null) {
	    return null;
	} else {
	    return exceptionStackTrace.getString();
	}
    }

    /**
     * @param s
     */

    protected static void debug(String s) {
	if (DEBUG) {
	    System.out.println(new Date() + " " + s);
	}
    }

}
