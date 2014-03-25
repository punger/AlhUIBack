package xxx.pju.alhambra.resource;

import name.pju.alhambra.CardSet;
import name.pju.alhambra.Currency;
import name.pju.alhambra.CurrencyColor;

public class GameCards extends CardSet {

	private static CardSet origCards = new CardSet();
	static {
		for (CurrencyColor cc : CurrencyColor.values()) {
			for (int cardVal = 1; cardVal <= 9; cardVal++) {
				for (int setNum = 0; setNum < 3; setNum++)
					origCards.addCard(new Currency(cc, cardVal));
			}
		}
	}
	public GameCards() {
		super(origCards);
	}

}
