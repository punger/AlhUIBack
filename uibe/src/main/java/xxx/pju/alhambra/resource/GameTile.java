package xxx.pju.alhambra.resource;

import java.util.Set;

import name.pju.alhambra.Direction;
import name.pju.alhambra.Tile;

public class GameTile extends Tile {
	
	private final String resourceId;
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * This constructor only works when instantiating the garden tile.  Note 
	 * that even though you don't need to say it's a garden tile, you still 
	 * have to provide the resource id for the garden tile.  We can't find it 
	 * automatically.
	 * @param rid resource id of the tile being initialized
	 */
	public GameTile(String rid) {
		super();
		resourceId = rid;
	}
	
	public GameTile(String rid, Family color, int cost, Set<Direction> w) {
		super(color, cost, w);
		resourceId = rid;
	}

}
