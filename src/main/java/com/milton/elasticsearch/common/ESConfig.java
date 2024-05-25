 
package com.milton.elasticsearch.common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MILTON
 */
@Slf4j
public class ESConfig {
	
	
	/**
	 * 
	 */
	private ESConfig() {
		log.debug("ESConfig");
	}
    
	private static String getEnvOrProVar(String key, String defaultValue) {

		if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
			return System.getenv(key);
		}

		return defaultValue;

	}
   
   public static final String ELASTIC_IP_ADDRESS = getEnvOrProVar("ELASTIC_IP_ADDRESS", "127.0.0.1");
   public static final int ELASTIC_PORT = Integer.parseInt(getEnvOrProVar("ELASTIC_PORT", "9200"));
   
   public static final boolean USE_AUTHENTICATION = getEnvOrProVar("USE_AUTHENTICATION", "no").equalsIgnoreCase("yes") ? true : false;
   public static final String ELASTIC_USER_NAME = getEnvOrProVar("ELASTIC_USER_NAME", "admin");
   public static final String ELASTIC_USER_PASSWORD = getEnvOrProVar("ELASTIC_USER_PASSWORD", "admin");
   
   public static final boolean USE_ELASTIC_URL = getEnvOrProVar("USE_ELASTIC_URL", "yes").equalsIgnoreCase("yes") ? true : false;
   public static final String ELASTIC_URL = getEnvOrProVar("ELASTIC_URL", "https://localhost:9200");
   
   public static final int NUMBER_OF_SHARDS = Integer.parseInt(getEnvOrProVar("NUMBER_OF_SHARDS", "3"));
   public static final int NUMBER_OF_REPLICAS = Integer.parseInt(getEnvOrProVar("NUMBER_OF_REPLICAS", "2"));
   
   public static final int RETRY_ON_CONFLICT_COUNT = Integer.parseInt(getEnvOrProVar("RETRY_ON_CONFLICT_COUNT", "3"));
   public static final int ELASTIC_MAX_RETRY_COUNT = Integer.parseInt(getEnvOrProVar("ELASTIC_MAX_RETRY_COUNT", "5"));
   public static final String ELASTIC_REQUEST_TIMEOUT_MIN = getEnvOrProVar("ELASTIC_REQUEST_TIMEOUT", "1m");
   public static final int ELASTIC_MATCH_PHRASE_MAX_EXPANSIONS = Integer.parseInt(getEnvOrProVar("ELASTIC_MATCH_PHRASE_MAX_EXPANSIONS", "5000"));
}
