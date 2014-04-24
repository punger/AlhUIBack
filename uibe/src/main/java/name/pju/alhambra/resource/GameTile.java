package name.pju.alhambra.resource;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import name.pju.alhambra.Direction;
import name.pju.alhambra.Tile;

public class GameTile extends Tile {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceId == null) ? 0 : resourceId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GameTile))
			return false;
		GameTile other = (GameTile) obj;
		if (resourceId == null) {
			if (other.resourceId != null)
				return false;
		} else if (!resourceId.equals(other.resourceId))
			return false;
		return true;
	}

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

	@Override
	public String toString() {
		return new ToStringBuilder(this).
				append("rid", resourceId).
				appendSuper(super.toString ()).
				toString();
	}

}
