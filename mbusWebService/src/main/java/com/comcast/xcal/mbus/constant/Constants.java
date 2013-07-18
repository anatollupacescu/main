package com.comcast.xcal.mbus.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface, describing possible errors, thrown from web service.
 * 
 * To avoid copy\paste and misunderstanding, possible returned 
 * errors are designed to be Interface constants.
 *
 */
public interface Constants {

	public final String ERR_CODE_CONFLICT_PARAM = "ConflictingQueryParameter";
	public final String ERR_TEXT_CONFLICT_PARAM = "The query parameter <parameter> is invalid. Its structure conflicts with that of another parameter.";
	
	public final String ERR_CODE_INTERNAL_ERROR = "InternalError";
	public final String ERR_TEXT_INTERNAL_ERROR = "There is an internal problem with webservice, which you cannot resolve. Retry the request. If the problem persists, contact us through the Comcast helpline.";
	
	public final String ERR_CODE_INVALID_ADDR = "InvalidAddress";
	public final String ERR_TEXT_INVALID_ADDR = "The address <address> is not valid for this web service.";
	
	public final String ERR_CODE_INVALID_HTTP = "InvalidHttpRequest";
	public final String ERR_TEXT_INVALID_HTTP = "Invalid HTTP request.";
	
	public final String ERR_CODE_INVALID_PARAM_COMB = "InvalidParameterCombination";
	public final String ERR_TEXT_INVALID_PARAM_COMB = "Two parameters were specified that cannot be used together";
	
	public final String ERR_CODE_INVALID_PARAM_VAL = "InvalidParameterValue";
	public final String ERR_TEXT_INVALID_PARAM_VAL = "One or more parameters cannot be validated: %s";
	
	public final String ERR_CODE_INVALID_PARAM = "InvalidQueryParameter";
	public final String ERR_TEXT_INVALID_PARAM = "The query parameter is invalid. Please see service documentation for correct syntax.";
	
	public final String ERR_CODE_INVALID_REQ = "InvalidRequest";
	public final String ERR_TEXT_INVALID_REQ = "The request is invalid. Please see service documentation for correct syntax.";
	
	public final String ERR_CODE_MALFORMED_VER = "MalformedVersion";
	public final String ERR_TEXT_MALFORMED_VER = "Version not well formed";
	
	public final String ERR_CODE_MISSING_PARAM = "MissingParameter";
	public final String ERR_TEXT_MISSING_PARAM = "A required parameter is missing: %s";
	
	public final String ERR_CODE_INCORRECT_VER = "NoSuchVersion";
	public final String ERR_TEXT_INCORRECT_VER = "An incorrect version was specified in the request.";
	
	public final String ERR_CODE_NO_MSG = "NoMessage";
	public final String ERR_TEXT_NO_MSG = "There is no message in the queue";
	
	public final String ERR_CODE_UNKNOWN = "UnknownError";
	public final String ERR_TEXT_UNKNOWN = "This is an unknown Error";
	
	public final Map <String, String> KNOWN_ERRORS = Collections.unmodifiableMap(new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1687162929218055773L;

	{ 
        put(ERR_CODE_CONFLICT_PARAM, ERR_TEXT_CONFLICT_PARAM);
        put(ERR_CODE_INTERNAL_ERROR, ERR_TEXT_INTERNAL_ERROR);
        put(ERR_CODE_INVALID_ADDR, ERR_TEXT_INVALID_ADDR);
        put(ERR_CODE_INVALID_HTTP, ERR_TEXT_INVALID_HTTP);
        put(ERR_CODE_INVALID_PARAM_COMB, ERR_TEXT_INVALID_PARAM_COMB);
        put(ERR_CODE_INVALID_PARAM_VAL, ERR_TEXT_INVALID_PARAM_VAL);
        put(ERR_CODE_INVALID_PARAM, ERR_TEXT_INVALID_PARAM);
        put(ERR_CODE_INVALID_REQ, ERR_TEXT_INVALID_REQ);
        put(ERR_CODE_MALFORMED_VER, ERR_TEXT_MALFORMED_VER);
        put(ERR_CODE_MISSING_PARAM, ERR_TEXT_MISSING_PARAM);
        put(ERR_CODE_INCORRECT_VER, ERR_TEXT_INCORRECT_VER);
        put(ERR_CODE_NO_MSG, ERR_TEXT_NO_MSG);
    }});

}
