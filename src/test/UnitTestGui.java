package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.junit.jupiter.api.Test;


import main.Item;

public class UnitTestGui {
	Item mainItem = new Item();
	 
	/* createItemFromItemName(String itemName)
	 * Het invullen van een item object mag niet gebeuren als er een niet bestaand itemNaam mee wordt gegeven. 
	 */
	@Test
	public void createItemEqual() {
		Item item = new Item().createItemFromItemName("lamp");
		System.out.println(item);
		assertEquals(new Item(), item);
	}
	
	/* createItemFromItemName(String itemName)
	 * Het invullen van een item object mag wel gebeuren als er een bestaand itemNaam mee wordt gegeven.
	 */
	@Test
	public void createItemNotEqual() {
		Item item = new Item().createItemFromItemName("lamp_woonkamer");
		System.out.println(item);
		assertNotEquals(new Item(), item);
	}
	
	/* changeState(String itemName, String state)
	 * Bij een verkeerde state zal het item object de state niet aanpassen en nog steeds dezelfde oude state teruggeven.
	 */
	@Test
	public void invalidState_changeState() throws IOException {
		String testState = "OFN";
		Item testItem = new Item().createItemFromItemName("lamp_woonkamer");
		testItem.changeState(testItem.getName(), testState);
		System.out.println(testItem.getState());
		assertNotEquals(testItem.getState(), testState);
	}
	
	/* changeState(String itemName, String state)
	 * Bij een juiste state zal het item object de state aanpassen en de nieuwe state teruggeven.
	 */
	@Test
	public void validState_changeState() {
		String testState = "OFF";
		Item testItem = new Item().createItemFromItemName("lamp_woonkamer");
		testItem.changeState(testItem.getName(), testState);
		System.out.println(testItem.getState());
		assertEquals(testItem.getState(), testState);
		
	}
	
	/* changeState(String itemName, String state)
	 * Het testen van een itemnaam dat niet klopt geeft een response code van 404.
	 */
	@Test()
	public void invalidName_changeState() throws IOException {
		int responseCode = 404;
		mainItem.changeState("S", "OFF");
		assertTrue(mainItem.getConnection().getResponseCode() == responseCode);
	}
	
	/* changeState(String itemName, String state)
	 * Het testen van een itemnaam dat wel klopt geeft een response code van 200.
	 */
	@Test
	public void validName_changeState() throws IOException {
		int responseCode = 200;
		mainItem.changeState("lamp_woonkamer", "OFF");
		assertTrue(mainItem.getConnection().getResponseCode() == responseCode);
	}
	
	
}
