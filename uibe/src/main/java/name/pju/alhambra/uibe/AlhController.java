package name.pju.alhambra.uibe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import name.pju.alhambra.Alhambra;
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

	// static {
	// ToStringStyle stts = StandardToStringStyle.DEFAULT_STYLE;
	// stts.
	// }
	/**
	 * Stupid bean to correctly map to json object
	 */
	private static class ColorBin {
		private String color;

		public ColorBin(String color) {
			setColor(color);
		}

		@SuppressWarnings("unused")
		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}
	}

	private static class ProfferJ {
		public ProfferJ() {
		}

		public String slot;
		public CardJ[] offer;
	}

	/**
	 * Represents a tile for transport.
	 * 
	 * null tiles will be represented as tile backs
	 * @author paul
	 *
	 */
	private static class TileJ {
		TileJ(Tile gtin) {
			gt = (GameTile) gtin;
			if (gt == null) {
				gt = TileList.getTileById("tileback");
			}
		}
		public GameTile gt;
	}

	private static class MarketJ {
		public TileJ blue;
		public TileJ yellow;
		public TileJ orange;
		public TileJ green;

		public MarketJ(Market m) {
			blue = new TileJ(m.whatsOnOffer(MarketColor.blue));
			yellow = new TileJ(m.whatsOnOffer(MarketColor.yellow));
			orange = new TileJ(m.whatsOnOffer(MarketColor.orange));
			green = new TileJ(m.whatsOnOffer(MarketColor.green));
		}
	}

	private static class ScoreJ {
		public String player;
		public int score;

		public ScoreJ(String p, int s) {
			player = p;
			score = s;
		}
	}

	private static class CardJ {
		public String color;
		public int value;

		@SuppressWarnings("unused")
		public CardJ() {
		}

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

		public String toString() {
			return "(color: " + color + ", value: " + value + ")";
		}
	}

	private static class PointJ {
		public int x = 0;
		public int y = 0;

		public PointJ(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public PointJ(Point pos) {
			x = pos.getX();
			y = pos.getY();
		}

		public Point getPoint() {
			return new Point(x, y);
		}
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

		public static StatusJ succeed(String m) {
			return new StatusJ(true, "", m);
		}
	}

	private static class BoardJ {
		public int minX;
		public int maxX;
		public int minY;
		public int maxY;

		public static class TilePosJ {
			public TilePosJ(int x, int y, TileJ t) {
				this.x = x;
				this.y = y;
				this.t = t;
			}

			public int x;
			public int y;
			public TileJ t;
		}

		public TilePosJ[] board;

		public static BoardJ fromBoard(Alhambra alh) {
			BoardJ b = new BoardJ();
			b.minX = alh.getMins().getX();
			b.minY = alh.getMins().getY();
			b.maxX = alh.getMaxs().getX();
			b.maxY = alh.getMaxs().getY();
			Tile[][] tMatrix = alh.getTileArray();
			ArrayList<TilePosJ> outTilejs = new ArrayList<TilePosJ>();
			for (int x = b.minX + 1; x < b.maxX; x++) {
				for (int y = b.minY + 1; y < b.maxY; y++) {
					Tile t = tMatrix[x - b.minX - 1][y - b.minY - 1];
					if (t != null) {
						TileJ tj = new TileJ(t);
						TilePosJ tpj = new TilePosJ(x, y, tj);
						outTilejs.add(tpj);
					}
				}
			}
			b.board = outTilejs.toArray(new TilePosJ[0]);
			return b;
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
		// logger.info("WHere is wuava?\n"+
		// Iterables.class.getProtectionDomain().getCodeSource().getLocation()
		// );
		return "welcome";
	}

	/**
	 * Get the list of available player colors
	 * 
	 * @return an array of all the player colors
	 */
	@RequestMapping(value = "/fullplayerlist")
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
	 * 
	 * @param players
	 *            who is playing
	 * @return status object success with the first player or failure with a
	 *         message
	 */
	@RequestMapping(value = "/startgame")
	public @ResponseBody StatusJ startGame(@RequestParam String[] players) {
		logger.info("At start game.");
		if (players == null || players.length == 0) {
			logger.info("Empty player list provided.  Defaulting");
			String [] defltPlayers = {"black", "red", "orange"};
			players = defltPlayers;
		}
		logger.info("Players " + Arrays.toString(players));
		if (players.length < 3)
			return StatusJ
					.fail("failure: need at least three players and provided only "
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
	 * 
	 * @return a tile that is the garden
	 */
	@RequestMapping(value = "/garden")
	public @ResponseBody TileJ getGarden() {
		return new TileJ(allTiles.getGarden());
	}

	/**
	 * 
	 * @return an object that holds the current state of the market
	 */
	@RequestMapping(value = "/currentmarket")
	public @ResponseBody MarketJ getCurrentMarketState() {
		// if (!game.getMarket().refill()) return null;
		return new MarketJ(game.getMarket());
	}

	/**
	 * The current player wants to buy a tile from the market with these cards
	 * 
	 * @param proffer
	 * @return the tile if his offer is accepted and a garden otherwise
	 */
	@RequestMapping(value = "/buytile")
	public @ResponseBody StatusJ buyTile(@RequestBody ProfferJ proffer) {
		logger.info("Offer to buy tile at slot " + proffer.slot
				+ " with offer " + Arrays.toString(proffer.offer));
		if (!game.getCurPlayer().hasActions()) {
			logger.error("Player " + game.getCurPlayer().getMeeple().name()
					+ "cannot buy tiles because he has no actions left.");
			return StatusJ.fail("No more actions.");
		}
		Payment offer = new Payment();
		for (CardJ cin : proffer.offer) {
			offer.addCard(CardJ.makeCard(cin));
		}
		MarketColor desiredSlot;
		try {
			desiredSlot = MarketColor.valueOf(proffer.slot);
		} catch (Exception e) {
			return StatusJ.fail("Invalid slot "+proffer.slot);
		}
		Tile t = game.getCurPlayer().buy(desiredSlot, offer);
		if (t == null) {
			logger.error("Couldn't buy tile "
					+ game.getMarket().whatsOnOffer(desiredSlot).toString()
					+ " at slot " + proffer.slot + " from "
					+ game.getMarket().toString() + " using proffer "
					+ offer.toString());
			logger.error("Current player "+game.getCurPlayer().getMeeple().name());
			logger.error("Player's hand "+game.getCurPlayer().getHand().toString());
			return StatusJ.fail("Couldn't buy tile");
		}
		logger.info("bought tile " + t.toString());
		if (game.getCurPlayer().hasActions())
			return StatusJ.succeed(game.getCurPlayer().getMeeple());
		return StatusJ.succeed("next");
	}

	@RequestMapping(value = "/takecards")
	public @ResponseBody StatusJ take(@RequestBody CardJ[] cards) {
		logger.info("takecards arg " + Arrays.toString(cards));
		if (!game.getCurPlayer().hasActions()) {
			logger.error("Player "+game.getCurPlayer().getMeeple().name()+"cannot take cards because he has no actions left.");
			return StatusJ.fail("No more actions.");
		}
		if (cards.length == 0)
			return StatusJ.fail("Omitted to ask for any cards.  Now who's a silly person?");
		CardSet cs = new CardSet();
		for (CardJ cin : cards) {
			cs = game.getExchange().claim(CardJ.makeCard(cin), cs);
		}
		if (cs.getCards().size() == cards.length) {
			Player curP = game.getCurPlayer();
			curP.addFromExchange(cs);
			return StatusJ.succeed(game.getCurPlayer().getMeeple());
		}
		logger.error("Failed taking cards " + cs.toString() + " from "
				+ game.getExchange().toString());
		game.getExchange().restoreClaimedCards(cs);
		return StatusJ.fail("Couldn't claim all cards from the exchange");
	}

	private static class PlacementRequestJ {
		public PlacementRequestJ() {
		}

		public String tileId;
		public int x;
		public int y;
	}

	@RequestMapping(value = "/placetile")
	public @ResponseBody StatusJ placeTile(@RequestBody PlacementRequestJ pr)
	// @RequestParam String tileId,
	// @RequestParam int x,
	// @RequestParam int y)
	{
		logger.info("Request to place tile " + pr.tileId + " at (" + pr.x
				+ ", " + pr.y + ")");
		GameTile t = TileList.getTileById(pr.tileId);
		Player curP = game.getCurPlayer();
		if (curP.getReserveBoard().contains(t)) {
			if (!curP.hasActions()) {
				return StatusJ.fail("Player needs an available action to move tiles from his reserve");
			}
			if (curP.getAlh().placeTile(pr.x, pr.y, t)){
				curP.getReserveBoard().remove(t);
				curP.consumeAction();
				return StatusJ.succeed("next");
			} 
			return StatusJ.fail("Could not place tile " + pr.tileId
					+ " at location (" + pr.x + "," + pr.y + ")");
		}
		if (curP.getUnattached().contains(t)) {
			if (curP.getAlh().placeTile(pr.x, pr.y, t)){
				curP.getUnattached().remove(t);
				return StatusJ.succeed(curP.getMeeple());
			}
			return StatusJ.fail("Could not place tile " + pr.tileId
					+ " at (" + pr.x + "," + pr.y + ")");
		}
		return StatusJ.fail("Player doesn't have tile "+ pr.tileId);
//		if (curP.getAlh().placeTile(pr.x, pr.y, t)){
//			if (curP.getReserveBoard().contains(t)) {
//				curP.getReserveBoard().remove(t);
//				curP.consumeAction();
//			} else if (curP.getUnattached().contains(t)) {
//				curP.getUnattached().remove(t);
//			} else {
//				return StatusJ.fail("Player doesn't have tile "+ pr.tileId);
//			}
//			return StatusJ.succeed("next");
//		}
//		return StatusJ.fail("Failed to place tile "+ pr.tileId);
	}

	@RequestMapping(value = "/toreserve")
	public @ResponseBody StatusJ toReserve(@RequestParam String tile) {
		logger.info("Move tile " + tile+" to reserve");
		GameTile t = TileList.getTileById(tile);
		if (t == null) {
			return StatusJ.fail("Tile not found");
		}
		List<Tile> free = game.getCurPlayer().getUnattached();
		if (free.contains(t)) {
			List<Tile> reserve = game.getCurPlayer().getReserveBoard();
			free.remove(t);
			reserve.add(t);
			return StatusJ.succeed("Tile "+tile+" moved to reserve.");
		}
		return StatusJ.fail("Tile "+tile+" not found in unattached tiles list.");
	}
	
	@RequestMapping(value = "/possibleLocations")
	public @ResponseBody PointJ[] candidateLocations(@RequestParam String tile) {
		logger.info("Possible locations for tile " + tile);
		GameTile t = TileList.getTileById(tile);
		if (t == null)
			return null;
		List<Point> locs = game.getCurPlayer().getAlh().getValidLocations(t);
		if (locs == null || locs.size() == 0)
			return null;
		PointJ pLocsJ[] = new PointJ[locs.size()];
		int i = 0;
		for (Point p : locs) {
			pLocsJ[i++] = new PointJ(p);
		}
		return pLocsJ;
	}

	@RequestMapping(value = "/exchange")
	public @ResponseBody CardJ[] getExchange() {
		return CardJ.fromCardset(game.getExchange());
	}

	@RequestMapping(value = "/playerhand")
	public @ResponseBody CardJ[] getHand(@RequestParam String player) {
		logger.info("player hand for " + player+": ");
		PlayerColor pc = PlayerColor.valueOf(player);
		Player p = game.getPlayer(pc);
		logger.info(p.getHand().toString());
		return CardJ.fromCardset(p.getHand());
	}

	@RequestMapping(value = "/playerboard")
	public @ResponseBody BoardJ getBoard(@RequestParam String player) {
		logger.info("player board for " + player);
		PlayerColor pc = PlayerColor.valueOf(player);
		Player p = game.getPlayer(pc);
		return BoardJ.fromBoard(p.getAlh());
	}

	/**
	 * Ends the current player's turn. 
	 * <p>
	 * Success means that the next player will go. Failure means that something
	 * else happens, either an intermediate scoring round or the end of the
	 * game. If a failure occurs, then the message is not one of the expected
	 * ones.
	 * 
	 * @return a status object that contains success and the next player, or
	 *         failure and a scoring round or end of game message
	 */
	@RequestMapping(value = "/endofturn")
	public @ResponseBody StatusJ endOfTurn() {
		if (game.getCurPlayer().hasActions()) {
			StatusJ s = StatusJ.fail("same player");
			s.player = game.getCurPlayer().getMeeple().name();
			return s;
		}
		int round = game.replenish();
		switch (round) {
		case 1:
		case 2:
			game.triggerScoringRound(round);
			StatusJ s = StatusJ.fail("Scoring round" + round);
			s.player = game.endTurn().name();
			return s;
		case 3:
			game.endgame();
			StatusJ.fail("End of game");
		case 0:
		default:
			PlayerColor nextPlayer = game.endTurn();
			game.getCurPlayer().startTurn();
			return StatusJ.succeed(nextPlayer);
		}
	}

	@RequestMapping(value = "curplayer")
	public @ResponseBody String getCurrentPlayerColor() {
		return game.getCurPlayer().getMeeple().name();
	}

	private static class PlayerInfoJ {
		public PlayerInfoJ() {
		}

		public String color = "invalid";
		public int score = 0;
		public boolean hasActions = false;
		public boolean current = false;

		public PlayerInfoJ(Player p, PlayerColor whosup) {
			color = p.getMeeple().name();
			current = p.getMeeple() == whosup;
			score = p.getScore();
			hasActions = p.hasActions();
		}
	}

	@RequestMapping(value = "playerinfo")
	public @ResponseBody PlayerInfoJ getPlayerInfo(@RequestParam String player) {
		try {
			PlayerColor pc = PlayerColor.valueOf(player);
			return new PlayerInfoJ(game.getPlayer(pc), game.getCurPlayer()
					.getMeeple());
		} catch (RuntimeException e) {
			return new PlayerInfoJ();
		}
	}

	@RequestMapping(value = "/score")
	public @ResponseBody ScoreJ[] getScores() {
		List<Player> ps = game.getPlayers();
		ScoreJ[] scores = new ScoreJ[ps.size()];
		int index = 0;
		for (Player p : ps) {
			scores[index++] = new ScoreJ(p.getMeeple().name(), p.getScore());
		}
		return scores;
	}

}
