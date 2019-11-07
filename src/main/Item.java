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

/*
 * Dit is een item object. Via openhab wordt een item opgehaald en als json ontvangen deze class kan dat json object omzetten naar een item object in java
 */
public class Item {
	private String link;
	private String name;
	private String label;
	private String type;
	private String category;
	private String state;
	private boolean editable;
	private static final Logger LOG = Logger.getLogger(Item.class.getName());
	private static HttpURLConnection connection;
	
	public Item(String link, String name, String label, String type, String category, String state, boolean editable) {
		this.link = link;
		this.name = name;
		this.label = label;
		this.type = type;
		this.category = category;
		this.state = state;
		this.editable = editable;
		
	}
	
	public Item(String itemName) {
		createItemFromItemName(itemName);
	}
	
	public Item() {
		//Creates a empty Item object
	}

	/*
	 * This method creates a connection to the rest API of openHAB
	 */
	public HttpURLConnection initURLConnection(String item, String requestMethod, boolean getOneItem) {
		HttpURLConnection temporaryConnection;
		try {
			URL url;
			if(!getOneItem) {
				url = new URL("http://localhost:8080/rest/items/");
			}else {
				url = new URL("http://localhost:8080/rest/items/" + item);
			}
			
			temporaryConnection = (HttpURLConnection) url.openConnection();
			temporaryConnection.setRequestMethod(requestMethod.toUpperCase(Locale.getDefault()));
			
			this.setConnection(temporaryConnection);
		}catch(IOException e) {
			LOG.log(Level.SEVERE, e.getMessage()+ " bestaat niet of je heb de verkeerde requestMethod gebruikt");
		}
		return connection;
	}
	
	/*
	 * Maak een item object aan waarbij de itemnaam is meegegeven van een item in openHab.
	 */
	public Item createItemFromItemName(String itemName) {
		BufferedReader bufferedReader;
		String inputLine;
		//Item item = new Item();
		try {
			Item.connection = this.initURLConnection(itemName, "GET", true);
			
			bufferedReader = new BufferedReader(
					  new InputStreamReader(connection.getInputStream()));
			final StringBuffer content = new StringBuffer();
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				content.append(inputLine);
			}
			bufferedReader.close();
			
			//Parse to json
			final JSONObject json = new JSONObject(content.toString());
			
			this.setLink(json.getString("link"));
			this.setName(json.getString("name"));
			this.setLabel(json.getString("label"));
			this.setType(json.getString("type"));
			this.setCategory(json.getString("category"));
			this.setState(json.getString("state"));
			this.setEditable( json.getBoolean("editable"));

			connection.disconnect();
		}catch(IOException e) {
			LOG.log(Level.SEVERE, "getOutputStream throws IOException. Check of de opgegeven itemNaam bestaat.");
		}
		return this;
		
	}

	/*
	 * Met deze methode kun je de status van een item aanpassen in openHab. bijvoorbeeld: het licht aan of uit zetten.
	 */
	public void changeState(String itemName, String state){
		BufferedWriter writer;
		String stateOn = "ON";
		String stateOff = "OFF";
		
		if((stateOn.equals(state) || stateOff.equals(state))) {
			if(this.getState() != null) {
				this.setState(state);
			}
			try{
				Item.connection = this.initURLConnection(itemName, "POST", true);
				connection.setRequestProperty("mode", "no-cors");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "text/plain");
				
				writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "ascii"));
				writer.write(state);
				writer.flush();
				
				//BufferedReader is noodzakelijk voor de response bericht. 
				final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				bufferedReader.close();
				
				if(LOG.isLoggable(Level.INFO)) {
					LOG.log(Level.INFO,itemName + ":"+state);
				}
				
				writer.close();
				connection.disconnect();
			}catch(IOException e) {
				LOG.log(Level.SEVERE, "IOException: Geen outputstream gevonden. Check of de itemNaam klopt");
			}
			
		}else {
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
	    try {
	    	connection = this.initURLConnection(null, "GET", false);
	    	StringBuilder result = new StringBuilder();
	    	final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    String line;
	    	while ((line = bufferedReader.readLine()) != null) {
	    		result.append(line);
	    	}
	    	bufferedReader.close();
	    	
	    	//VERWIJDER brackets[]
	    	String newItemsString;
	    	newItemsString = result.toString().substring(1, result.toString().length() -1);
	    	
	    	//SPLIT STRING
	    	String [] parts = newItemsString.split("},");
	    	
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
	    	connection.disconnect();
	      }catch(IOException e) {
	    	  LOG.log(Level.SEVERE , "getInputStream() throws IOException");
	      }
	    return items;
	}
	
	/*
	 * Gebruik alleen voor Unit testen!
	 */
	public HttpURLConnection getConnection() {
		return Item.connection;
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
	
	public void setLink(String link) {
		this.link = link;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setConnection(HttpURLConnection connection) {
		Item.connection = connection;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean status = false;
		Item item = (Item)obj;
		if(this.getLink() == item.getLink()){
			status = true;
		}
		return status;
   }

	@Override
	public String toString() {
		return "{link: "+this.link+", name: "+name+", label: "+label+", type: "+type+", category: "+category+", state: "+state+", editable: "+editable+"}";
	}
}
