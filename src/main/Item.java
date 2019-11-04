package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class Item {
	private String link, name, label, type, category, state;
	private boolean editable;
	private Logger log = Logger.getLogger(Item.class.getName());
	
	public Item(String link, String name, String label, String type, String category, String state, boolean editable) {
		this.link = link;
		this.name = name;
		this.label = label;
		this.type = type;
		this.category = category;
		this.state = state;
		this.editable = editable;
		
	}
	
	public Item() {};
	
	private HttpURLConnection initURLConnection(String item, String requestMethod) {
		try {
			URL url;
			if(item == null) {
				url = new URL("http://localhost:8080/rest/items/");
			}else {
				url = new URL("http://localhost:8080/rest/items/" + item);
			}
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(requestMethod.toUpperCase());

			return con;
		}catch(Exception e) {
			return null;
		}
	}
	
	public Item createItem(String itemName) {
		try {
			BufferedReader in = new BufferedReader(
					  new InputStreamReader(this.initURLConnection(itemName, "GET").getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			
			//Parse to json
			JSONObject json = new JSONObject(content.toString());
			
			return new Item(json.getString("link"), json.getString("name"), json.getString("label"), 
					json.getString("type"), json.getString("category"), json.getString("state"), json.getBoolean("editable"));
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void changeState(String itemName, String state) throws IOException{
		if(state.equals("ON") || state.equals("OFF")) {
			HttpURLConnection con = this.initURLConnection(itemName, "POST");
			con.setRequestProperty("mode", "no-cors");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "text/plain");
			
			try{
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(con.getOutputStream(), "ascii"));
				writer.write(state);
				writer.flush();
				writer.close();
				
				log.log(Level.INFO, "Response Body : ");
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
				    response.append(inputLine);
				}
				in.close();
				con.disconnect();
				log.log(Level.INFO, response.toString());
			}catch(IOException e) {
				log.log(Level.INFO,con.getResponseCode() + " CATCH");
			}
			
		}else {
			log.log(Level.INFO, "State is not valid use ON or OFF");
		}
	}
	
	public ArrayList<Item> getAllItems(){
		ArrayList<Item> items = new ArrayList<>();
		JSONObject json;
		Item item;
		HttpURLConnection con = this.initURLConnection(null, "GET");
	    try {
	    	StringBuilder result = new StringBuilder();
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		    String line;
	    	while ((line = rd.readLine()) != null) {
	    		result.append(line);
	    	}
	    	rd.close();
	    	
	    	//REMOVE brackets[]
	    	String newItemsString;
	    	newItemsString = result.toString().substring(1, result.toString().length() -1);
	    	
	    	//SPLIT STRING
	    	String[] parts = newItemsString.split("},");
	    	
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
	    	return items;
	    	
	      }catch(Exception e) {
	    	  e.printStackTrace();
	    	  return null;
	      }
	}
	
	public String toString() {
		return "{link: "+this.link+", name: "+name+", label: "+label+", type: "+type+", category: "+category+", state: "+state+", editable: "+editable+"}";
	}
}
