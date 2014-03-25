package name.pju.alhambra.uibe;

import java.util.ArrayList;
import java.util.EnumSet;

import name.pju.alhambra.Card;
import name.pju.alhambra.Game;
import name.pju.alhambra.Market;
import name.pju.alhambra.MarketColor;
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
import org.springframework.web.bind.annotation.RequestBody;
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
	
	private static class CardJ {
		public String color;
		public int value;
		public CardJ(Card c) {
			color = c.getColor().toString();
			value = c.value();
		}
	}
	
	private static class PointJ {
		public int x = 0;
		public int y = 0;
		public PointJ( int x, int y) { this.x = x; this.y = y; }
		public PointJ(Point pos) { x = pos.getX(); y = pos.getY(); }
		public Point getPoint() { return new Point(x, y); }
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
    
    @RequestMapping(value="/startgame")
    public @ResponseBody String startGame(@RequestParam String[] players) {
    	logger.info("At start game.");
    	if (players == null || players.length == 0)
    		return "failure: empty player list";
    	logger.info("Players " + players.toString());
    	if (players.length < 3)
    		return "failure: need at least three players and provided only " + players.length;
    	EnumSet<PlayerColor> roster = EnumSet.noneOf(PlayerColor.class);
    	for (String c : players) {
    		try {
				roster.add(PlayerColor.valueOf(c));
			} catch (Exception e) {
				return "failure: bad input player color " + c;
			}
    	}
    	allTiles = TileListFromXml.getFreshBag();
    	game = new Game(roster, new GameCards(), allTiles);
    	return "success";
    }
    
    @RequestMapping(value="/garden")
    public @ResponseBody GameTile getGarden() {
    	return allTiles.getGarden();
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
    
    public MarketJ buyTile(String player, CardJ paySet[], String tileId) {
    	return null;
    }
    
    public CardJ[] take(String player, int[] slot) {
    	return null;
    }
    
    public TileJ[][] place(String player, String tileId, int x, int y)  {
    	return null;
    }
    
    public PointJ[] candidateLocations(String player, String tileId) {
    	return null;
    }

}
