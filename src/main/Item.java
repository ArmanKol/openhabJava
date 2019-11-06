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
	private HttpURLConnection connection;
	
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

	/*
	 * This method creates a connection to the rest API of openHAB
	 */
	public HttpURLConnection initURLConnection(String item, String requestMethod, boolean getOneItem) {
		try {
			URL url;
			if(!getOneItem) {
				url = new URL("http://localhost:8080/rest/items/");
			}else {
				url = new URL("http://localhost:8080/rest/items/" + item);
			}
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod.toUpperCase(Locale.getDefault()));
		}catch(IOException e) {
			LOG.log(Level.SEVERE, e.getStackTrace().toString());
		}
		return connection;
	}
	
	/*
	 * Maak een item object aan waarbij de itemnaam is meegegeven van een item in openHab.
	 */
	public Item createItemFromItemName(String itemName) {
		BufferedReader bufferedReader;
		String inputLine;
		Item item = null;
		try {
			this.connection = this.initURLConnection(itemName, "GET", true);
			
			if(connection.getResponseCode() == 404) {
				throw new NullPointerException();
			}
			
			bufferedReader = new BufferedReader(
					  new InputStreamReader(this.connection.getInputStream()));
			StringBuffer content = new StringBuffer();
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				content.append(inputLine);
			}
			bufferedReader.close();
			
			//Parse to json
			final JSONObject json = new JSONObject(content.toString());
			
			item = new Item(json.getString("link"), json.getString("name"), json.getString("label"), 
					json.getString("type"), json.getString("category"), json.getString("state"), json.getBoolean("editable"));
			
			
			connection.disconnect();
		}catch(IOException e) {
			LOG.log(Level.SEVERE, "getOutputStream throws IOException. Check of de opgegeven itemNaam bestaat.");
		}catch(NullPointerException e) {
			LOG.log(Level.SEVERE, "NullPointerException. Check of de opgegeven itemNaam bestaat.");
		}
		return item;
		
	}

	/*
	 * Met deze methode kun je de status van een item aanpassen in openHab. bijvoorbeeld: het licht aan of uit zetten.
	 */
	public void changeState(String itemName, String state){
		BufferedWriter writer;
		if(state.equals("ON") || state.equals("OFF")) {
			if(this.getState() != null) {
				this.setState(state);
			}
			try{
				this.connection = this.initURLConnection(itemName, "POST", true);
				this.connection.setRequestProperty("mode", "no-cors");
				this.connection.setDoOutput(true);
				this.connection.setRequestProperty("Content-Type", "text/plain");
				
				writer = new BufferedWriter(new OutputStreamWriter(this.connection.getOutputStream(), "ascii"));
				writer.write(state);
				writer.flush();
				
				if(this.connection.getResponseCode() == 404) {
					throw new NullPointerException();
				}
				
				//BufferedReader is noodzakelijk voor de response bericht. 
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
				bufferedReader.close();
				
				LOG.log(Level.INFO,itemName + ":"+state);
				
				writer.close();
				this.connection.disconnect();
			}catch(IOException e) {
				LOG.log(Level.SEVERE, "IOException: Geen outputstream gevonden. Check of de itemNaam klopt");
			}catch(NullPointerException e) {
				LOG.log(Level.SEVERE, "NullPointerException: Check of de opgegeven itemNaam klopt");
			}
			
		}else {
			System.out.println(this.connection);
			LOG.log(Level.SEVERE, "Een State moet ON of OFF zijn");
		}
	}
	
	/*
	 * Deze methode returned alle items in een lijst die geconfigureerd zijn in openHab.
	 */
	public List<Item> getAllItems(){
		ArrayList<Item>  items = new ArrayList<>();
		JSONObject json;
		Item item;
		String[] parts;
	    try {
	    	this.connection = this.initURLConnection(null, "GET", false);
	    	StringBuilder result = new StringBuilder();
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
		    String line;
	    	while ((line = bufferedReader.readLine()) != null) {
	    		result.append(line);
	    	}
	    	bufferedReader.close();
	    	
	    	//VERWIJDER brackets[]
	    	String newItemsString;
	    	newItemsString = result.toString().substring(1, result.toString().length() -1);
	    	
	    	//SPLIT STRING
	    	parts = newItemsString.split("},");
	    	
	    	//VOEG CURLY BRACKETS TOE{
	    	for(int x=0; x < parts.length; x++) {
	    		if(x < parts.length -1) {
	    			parts[x]+="}";
	    		}
	    		json = new JSONObject(parts[x]);
	    		item = new Item(json.getString("link"), json.getString("name"), json.getString("label"), 
						json.getString("type"), json.getString("category"), json.getString("state"), json.getBoolean("editable"));
	    		items.add(item);
	    	}
	    	this.connection.disconnect();
	      }catch(IOException e) {
	    	  LOG.log(Level.SEVERE , "getInputStream() throws IOException");
	      }
	    return items;
	}
	
	/*
	 * Gebruik alleen voor Unit testen!
	 */
	public HttpURLConnection getConnection() {
		return this.connection;
	}
	
	public String getLink() {
		return this.link;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public String getState() {
		return this.state;
	}
	
	public boolean isEditable() {
		return this.editable;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "{link: "+this.link+", name: "+name+", label: "+label+", type: "+type+", category: "+category+", state: "+state+", editable: "+editable+"}";
	}
}
