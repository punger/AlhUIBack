package xxx.pju.alhambra.resource;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import name.pju.alhambra.Card;
import name.pju.alhambra.CardSet;

public class CardList {
	private static Set<Card> cards = new HashSet<Card>();
	static {
		
	}
	public static CardSet getDeck() {
		return new CardSet(cards);
	}
	

}
