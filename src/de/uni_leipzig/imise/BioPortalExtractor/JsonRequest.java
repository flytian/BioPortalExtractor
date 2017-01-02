package de.uni_leipzig.imise.BioPortalExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRequest {
	
	private static final int WAITING_TIME = 5000;
	private static ObjectMapper mapper = new ObjectMapper();
	
	
	/**
	 * Sends a get request to the specified URL and returns the response body as JsonNode.
	 * 
	 * @param url request URL
	 * @return response body as JsonNode
	 */
	public static JsonNode get(String url) {
		return get(url, null);
	}
	
	
	/**
	 * Sends a get request to the specified URL with specified api key and returns the response body as JsonNode.
	 * If the provided api key is null, it is ignored.
	 * 
	 * @param url request URL
	 * @param api_key personal api key
	 * @return response body as JsonNode
	 */
	public static JsonNode get(String url, String api_key) {
        HttpURLConnection conn;
        BufferedReader rd = null;
        String line;
        String result = "";
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            if (api_key != null)
            	conn.setRequestProperty("Authorization", "apikey token=" + api_key);
            conn.setRequestProperty("Accept", "application/json");
            
            boolean retry = true;
            
            do {
	            try {
	            	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            	retry = false;
	            } catch (Exception e) {
	            	System.err.println(e.getLocalizedMessage() + " - " + conn.getResponseMessage());
	            	System.out.println("Waiting 5 seconds to try again...");
	            	Thread.sleep(WAITING_TIME);
	            }
            } while (retry);
	            
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringToJsonNode(result);
    }
	
	/**
	 * Transforms a String in JSON format to JsonNode
	 * 
	 * @param string JSON formated string
	 * @return resulting JsonNode
	 */
	private static JsonNode stringToJsonNode(String string) {
        JsonNode root = null;
        try {
            root = mapper.readTree(string);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }
}
