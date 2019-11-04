package main;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException{
		Item item = new Item();
		//item.changeState("lamp_woonkamer", "ON");
		for(Item itemx : item.getAllItems()) {
			System.out.println(itemx);
		}
	}
}
