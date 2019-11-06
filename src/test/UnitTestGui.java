package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;


import main.Item;

public class UnitTestGui {
	Item item = new Item();
	 
	/* createItem(String itemName)
	 * Het aanmaken van een item object mag niet gebeuren als er een niet bestaand itemNaame mee wordt gegeven. 
	 */
	@Test
	public void createItemNull() {
		assertNull("Niet bestaande itemName", item.createItemFromItemName("lamp"));
	}
	
	/* createItem(String itemName)
	 * Het aanmaken van een item object mag wel gebeuren als er een bestaand itemNaame mee wordt gegeven.
	 */
	@Test
	public void createItemNotNull() {
		assertNotNull("Juiste itemName", item.createItemFromItemName("lamp_woonkamer"));
	}
	
	/* changeState(String itemName, String state)
	 * Bij een verkeerde state zal er geen connectie gemaakt worden en dus zal getConnection() null teruggeven.
	 */
	@Test
	public void invalidState_changeState() {
		Item testItem = new Item().createItemFromItemName("lamp_woonkamer");
		testItem.changeState(testItem.getName(), "OFN");
		assertEquals(testItem.getConnection(), null);
	}
	
	/* changeState(String itemName, String state)
	 * Bij een juiste state zal er een connectie gemaakt worden en dus zal getConnection() een HTTPURLConnection object teruggeven.
	 */
	@Test
	public void validState_changeState() {
		Item testItem = new Item().createItemFromItemName("lamp_woonkamer");
		testItem.changeState(testItem.getName(), "ON");
		
		HttpURLConnection connection = null;
		connection = this.item.initURLConnection(testItem.getName(), "POST", true);
		connection.setRequestProperty("mode", "no-cors");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "text/plain");
		
		assertNotEquals(testItem.getConnection(), connection);
		
	}
	
	/* changeState(String itemName, String state)
	 * Het testen van een itemnaam dat niet klopt moet een foutmelding geven.
	 */
	@Test
	public void invalidName_changeState() {
		item.changeState("S", "ON");
		assertNull(this.item.getConnection());
		
	}
	
	/* changeState(String itemName, String state)
	 * Het testen van een itemnaam dat wel klopt.
	 */
	@Test
	public void validName_changeState() {
		item.changeState("lamp_woonkamer", "OFF");
		assertNotNull(this.item.getConnection());
		
	}
	
	
}
