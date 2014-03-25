package xxx.pju.alhambra.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import name.pju.alhambra.BagOfTiles;
import name.pju.alhambra.Tile;

public class TileList implements BagOfTiles {
	private static Collection<GameTile> allTiles = null;
	private Stack<GameTile> gameBag = null;
	private static GameTile garden = null;
	@Override
	public GameTile getNext() {
		if (empty()) return null;
		return gameBag.pop();
	}
	@Override
	public boolean empty() {
		if (gameBag == null) return true;
		return gameBag.isEmpty();
	}
	public TileList() {
		this(null);
	}
	private void assignAllTiles(Collection<GameTile> ts) {
		if (ts == null) return;
		for (GameTile t : ts) {
			if (t.getColor() == Tile.Family.garden) {
				garden = t;
				break;
			}
		}
		ts.remove(garden);
		allTiles = ts;
	}
	public TileList(Collection<GameTile> ts) {
		assignAllTiles(ts);
		gameBag = new Stack<GameTile>();
		if (allTiles == null) return;
		gameBag.addAll(allTiles);
		Collections.shuffle(gameBag);
	}
	public int getNumCurrentlyAvailableTiles() { return gameBag.size(); }
	@Override
	public GameTile getGarden() {
		return garden;
	}

}
