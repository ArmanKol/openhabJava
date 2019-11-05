package main;

public class Main {
	public static void main(String[] args){
		Gui gui = new Gui();
		Item item = new Item();

		for(Item item1 : item.getAllItems()) {
			System.out.println(item.getName());
		}
	}
}
