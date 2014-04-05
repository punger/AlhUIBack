package name.pju.alhambra.uibe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import name.pju.alhambra.Card;
import name.pju.alhambra.CardSet;
import name.pju.alhambra.Currency;
import name.pju.alhambra.CurrencyColor;
import name.pju.alhambra.Game;
import name.pju.alhambra.Market;
import name.pju.alhambra.MarketColor;
import name.pju.alhambra.Payment;
import name.pju.alhambra.Player;
import name.pju.alhambra.PlayerColor;
import name.pju.alhambra.Point;
import name.pju.alhambra.Tile;
import name.pju.alhambra.resource.GameCards;
import name.pju.alhambra.resource.GameTile;
import name.pju.alhambra.resource.TileList;
import name.pju.alhambra.resource.TileListFromXml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;

@Controller
@RequestMapping("/")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class AlhController {
	/**
	 * Stupid bean to correctly map to json object
	 */
	private static class ColorBin {
		private String color;
		public ColorBin(String color) { setColor(color); }
		@SuppressWarnings("unused")
		public String getColor() {return color;}
		public void setColor(String color) { this.color = color; }
	}
	
	private static class TileJ {
		TileJ(Tile gtin) { gt = (GameTile) gtin; }
		public GameTile gt;
	}
	private static class MarketJ {
		public TileJ blue;
		public TileJ yellow;
		public TileJ orange;
		public TileJ green;
		public MarketJ (Market m) {
			blue 	= new TileJ(m.whatsOnOffer(MarketColor.blue));
			yellow 	= new TileJ(m.whatsOnOffer(MarketColor.yellow));
			orange 	= new TileJ(m.whatsOnOffer(MarketColor.orange));
			green 	= new TileJ(m.whatsOnOffer(MarketColor.green));
		}
	}
	
	private static class ScoreJ {
		public String player;
		public int score;
		public ScoreJ(String p, int s) { player = p; score = s; }
	}
	
	private static class CardJ {
		public String color;
		public int value;
		public CardJ(Card c) {
			color = c.getColor().toString();
			value = c.value();
		}
		public static CardJ[] fromCardset(CardSet cs) {
	    	ArrayList<CardJ> retCards = new ArrayList<CardJ>();
	    	for (Card c : cs.getCards()) {
	    		retCards.add(new CardJ(c));
	    	}
	    	CardJ[] retCardArray = new CardJ[retCards.size()];
	    	return retCards.toArray(retCardArray);
		}
		public static Card makeCard(CardJ cin) {
			try {
				return new Currency(CurrencyColor.valueOf(cin.color), cin.value);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}
	
	private static class PointJ {
		public int x = 0;
		public int y = 0;
		public PointJ( int x, int y) { this.x = x; this.y = y; }
		public PointJ(Point pos) { x = pos.getX(); y = pos.getY(); }
		public Point getPoint() { return new Point(x, y); }
	}
	
	private static class StatusJ {
		public StatusJ(boolean s, String p, String m) {
			success = s;
			player = p;
			message = m;
		}
		public boolean success;
		public String player;
		public String message;
		public static StatusJ fail(String m) {
			return new StatusJ(false, "", m);
		}
		public static StatusJ succeed(PlayerColor pc) {
			return new StatusJ(true, pc.name(), "");
		}
	}
	
    protected final Log logger = LogFactory.getLog(getClass());
    
    private Game game = null;
    
    private TileList allTiles = null;
    
    public AlhController() {
    	
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String hello(ModelMap model) {
    	logger.info("At welcome");
        return "welcome";
    }
    
    /**
     * Get the list of available player colors
     * @return an array of all the player colors
     */
    @RequestMapping(value="/fullplayerlist")
    public @ResponseBody ColorBin[] getPlayerColors() {
    	logger.info("At player list");
    	ArrayList<ColorBin> names = new ArrayList<ColorBin>();
    	for (PlayerColor p : PlayerColor.values()) {
    		names.add(new ColorBin(p.toString()));
    	}
    	ColorBin[] nameArray = new ColorBin[names.size()];
    	nameArray = names.toArray(nameArray);
    	return nameArray;
    }
    
    /**
     * Start the game with the named players
     * @param players who is playing
     * @return status object success with the first player or failure with a
     * message
     */
    @RequestMapping(value="/startgame")
    public @ResponseBody StatusJ startGame(@RequestParam String[] players) {
    	logger.info("At start game.");
    	if (players == null || players.length == 0)
    		return StatusJ.fail("empty player list");
    	logger.info("Players " + Arrays.toString(players));
		if (players.length < 3)
			return StatusJ.fail(
					"failure: need at least three players and provided only "
					+ players.length);
    	EnumSet<PlayerColor> roster = EnumSet.noneOf(PlayerColor.class);
    	for (String c : players) {
    		try {
				roster.add(PlayerColor.valueOf(c));
			} catch (Exception e) {
				return StatusJ.fail("failure: bad input player color " + c);
			}
    	}
    	allTiles = TileListFromXml.getFreshBag();
    	game = new Game(roster, new GameCards(), allTiles);
    	Player p = game.getCurPlayer();
    	return StatusJ.succeed(p.getMeeple());
    }
    
    /**
     * Retrieve tile information for the start garden tile
     * @return a tile that is the garden
     */
    @RequestMapping(value="/garden")
    public @ResponseBody TileJ getGarden() {
    	return new TileJ(allTiles.getGarden());
    }
    
    /**
     * 
     * @return an object that holds the current state of the market
     */
    @RequestMapping(value="/currentmarket")
    public @ResponseBody MarketJ getCurrentMarketState() {
    	if (!game.getMarket().refill()) return null;
    	return new MarketJ(game.getMarket());
    }
    /**
     * The current player wants to buy a tile from the market with these cards
     * @param paySet subset of his hand the player wants to pay with
     * @param slot the color of market stall that holds the tile he wants
     * @return the tile if his offer is accepted and a garden otherwise
     */
    @RequestMapping(value="/buytile")
    public @ResponseBody TileJ buyTile(
    		@RequestParam CardJ paySet[], 
    		@RequestParam String slot) 
    {
    	Payment offer = new Payment();
    	for (CardJ cin : paySet) {
    		offer.addCard(CardJ.makeCard(cin));
    	}
    	Tile t = game.getCurPlayer().buy(MarketColor.valueOf(slot), offer);
    	if (t == null) {
    		return new TileJ(game.getGarden());
    	}
    	return new TileJ(t);
    }
    
    @RequestMapping(value="/takecards")
    public @ResponseBody StatusJ take(@RequestParam CardJ[] cards) {
    	CardSet cs = new CardSet();
    	for (CardJ cin : cards) {
    		cs = game.getExchange().claim(CardJ.makeCard(cin), cs);
    	}
    	if (cs.getCards().size() == cards.length) {
    		Player curP = game.getCurPlayer();
    		curP.addFromExchange(cs);
    		return StatusJ.succeed(null);
    	} 
    	game.getExchange().restoreClaimedCards(cs);
    	return StatusJ.fail("Couldn't claim all cards from the exchange");
    }
    
    public TileJ[][] placeTile(String player, String tileId, int x, int y)  {
    	return null;
    }
    
    public PointJ[] candidateLocations(String player, String tileId) {
    	return null;
    }
    
    @RequestMapping(value="/exchange")
    public @ResponseBody CardJ[] getExchange() {
    	return CardJ.fromCardset(game.getExchange());
    }
    
    
    @RequestMapping(value="/playerhand")
    public @ResponseBody CardJ[] getHand(@RequestParam String player) {
    	logger.info("player hand for "+player);
    	PlayerColor pc = PlayerColor.valueOf(player);
    	Player p = game.getPlayer(pc);
    	return CardJ.fromCardset(p.getHand());
    }
    
    /**
     * Ends the current player's turn.
     * <br>
     * Success means that the next player will go.  Failure means that something
     * else happens, either an intermediate scoring round or the end of the 
     * game.  If a failure occurs, then the message is not one of the expected
     * ones.
     * @return a status object that contains success and the next player, or
     * failure and a scoring round or end of game message 
     */
    @RequestMapping(value="/endofturn")
    public @ResponseBody StatusJ endOfTurn() {
   		int round = game.replenish();
   		switch (round) {
   		case 1:
   		case 2:
   			game.triggerScoringRound(round);
   			return StatusJ.fail("Scoring round"+round);
   		case 3:
   			game.endgame();
   			return StatusJ.fail("End of game");
   		case 0: 
        default:
        	PlayerColor nextPlayer = game.endTurn();
        	return StatusJ.succeed(nextPlayer);
   		}
    }
    
    @RequestMapping(value="/score")
    public @ResponseBody ScoreJ[] getScores() {
    	List<Player> ps = game.getPlayers();
    	ScoreJ [] scores = new ScoreJ[ps.size()];
    	int index = 0;
    	for (Player p : ps){
    		scores[index++] = new ScoreJ(p.getMeeple().name(), p.getScore());
    	}
    	return scores;
    }

}
