package name.pju.alhambra.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

import name.pju.alhambra.BagOfTiles;
import name.pju.alhambra.Tile;

public class TileList implements BagOfTiles {
	private static Collection<GameTile> allTiles = null;
	private static Collection<GameTile> unplayable = null;
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
		unplayable = new ArrayList<GameTile>();
		for (GameTile t : ts) {
			if (t.getColor() == Tile.Family.garden) {
				garden = t;
				unplayable.add(garden);
//				break;
			} else if (t.getColor() == Tile.Family.other) {
				unplayable.add(t);
			}
		}
		ts.removeAll(unplayable);
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
	/**
	 * Find a concrete tile from the list of all known tiles that matches by
	 * resource id.
	 * @param ridProbe indicates the name of a tile
	 * @return a GameTile with the given resource id
	 */
	public static GameTile getTileById(String ridProbe) {
		for (GameTile gt : allTiles) {
			if (gt.getResourceId().equals(ridProbe)) return gt;
		}
		for (GameTile gt : unplayable) {
			if (gt.getResourceId().equals(ridProbe)) return gt;
		}
		return null;
	}

}
