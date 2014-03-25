package name.pju.alhambra.resource;

import java.util.HashSet;
import java.util.Set;

import name.pju.alhambra.Direction;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="tile")
public class TileFromXml {
	@Attribute public String id;
	@Attribute public String color;
	@Attribute public int cost;
	@Attribute public int west;
	@Attribute public int north;
	@Attribute public int east;
	@Attribute public int south;
	
	public String toString() {
//		return ""+color;
		return "id="+id+", c="+color+"("+cost+") "+
				(west!=0?"w":"") +
				(north!=0?"n":"") +
				(east!=0?"e":"") +
				(south!=0?"s":"");
	}
	
	public Set<Direction> getWalls() {
		Set<Direction> ws = new HashSet<Direction>();
		if (west != 0) ws.add(Direction.west);
		if (north != 0) ws.add(Direction.north);
		if (east!= 0) ws.add(Direction.east);
		if (south != 0) ws.add(Direction.south);
		return ws;
	}
}
