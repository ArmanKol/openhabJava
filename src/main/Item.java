package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class Item {
	private String link;
	private String name;
	private String label;
	private String type;
	private String category;
	private String state;
	
	private boolean editable;
	private static final Logger LOG = Logger.getLogger(Item.class.getName());
	
	public Item(String link, String name, String label, String type, String category, String state, boolean editable) {
		this.link = link;
		this.name = name;
		this.label = label;
		this.type = type;
		this.category = category;
		this.state = state;
		this.editable = editable;
		
	}
	
	public Item() {
		//Creates a Item object
	}
	
	
	public Item(String itemName) {
		createItem(itemName);
	}
	
	/*
	 * This method creates a connection to the rest API of openHAB
	 */
	private HttpURLConnection initURLConnection(String item, String requestMethod) {
		try {
			URL url;
			if(item == null) {
				url = new URL("http://localhost:8080/rest/items/");
			}else {
				url = new URL("http://localhost:8080/rest/items/" + item);
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(requestMethod.toUpperCase(Locale.getDefault()));

			return con;
		}catch(Exception e) {
			return null;
		}
	}
	
	/*
	 * Create an Item object with the specified item name
	 */
	public Item createItem(String itemName) {
		BufferedReader bufferedReader;
		String inputLine;
		Item item = null;
		try {
			bufferedReader = new BufferedReader(
					  new InputStreamReader(this.initURLConnection(itemName, "GET").getInputStream()));
			StringBuffer content = new StringBuffer();
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				content.append(inputLine);
			}
			bufferedReader.close();
			
			//Parse to json
			final JSONObject json = new JSONObject(content.toString());
			
			item = new Item(json.getString("link"), json.getString("name"), json.getString("label"), 
					json.getString("type"), json.getString("category"), json.getString("state"), json.getBoolean("editable"));
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		return item;
		
	}
	
	/*
	 * This method lets you change the state of an item. example: turn a light on or off
	 */
	public void changeState(String itemName, String state){
		BufferedWriter writer;
		HttpURLConnection con;
		if(state.equals("ON") || state.equals("OFF")) {
			
			con = this.initURLConnection(itemName, "POST");
			con.setRequestProperty("mode", "no-cors");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "text/plain");
			
			try{
				writer = new BufferedWriter(
				        new OutputStreamWriter(con.getOutputStream(), "ascii"));
				writer.write(state);
				writer.flush();
				
				//BufferedReader is needed to get the response message but it somehow returns null
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				bufferedReader.close();
				
				LOG.log(Level.INFO,itemName + ":"+state);
				writer.close();
				con.disconnect();
			}catch(IOException e) {
				LOG.log(Level.INFO, "IOException");
			}
			
		}else {
			LOG.log(Level.INFO, "State is not valid use ON or OFF");
		}
	}
	
	/*
	 * This method returns all items that's configured in OpenHAB
	 */
	public List<Item> getAllItems(){
		ArrayList<Item>  items = new ArrayList<>();
		JSONObject json;
		Item item;
		String[] parts;
		final HttpURLConnection con = this.initURLConnection(null, "GET");
	    try {
	    	StringBuilder result = new StringBuilder();
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line;
	    	while ((line = bufferedReader.readLine()) != null) {
	    		result.append(line);
	    	}
	    	bufferedReader.close();
	    	
	    	//REMOVE brackets[]
	    	String newItemsString;
	    	newItemsString = result.toString().substring(1, result.toString().length() -1);
	    	
	    	//SPLIT STRING
	    	parts = newItemsString.split("},");
	    	
	    	//ADD CURLY BRACKETS {
	    	for(int x=0; x < parts.length; x++) {
	    		if(x < parts.length -1) {
	    			parts[x]+="}";
	    		}
	    		json = new JSONObject(parts[x]);
	    		item = new Item(json.getString("link"), json.getString("name"), json.getString("label"), 
						json.getString("type"), json.getString("category"), json.getString("state"), json.getBoolean("editable"));
	    		items.add(item);
	    	}
	    	
	      }catch(IOException e) {
	    	  LOG.log(Level.INFO ,e.getMessage());
	      }
	    return items;
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "{link: "+this.link+", name: "+name+", label: "+label+", type: "+type+", category: "+category+", state: "+state+", editable: "+editable+"}";
	}
}
