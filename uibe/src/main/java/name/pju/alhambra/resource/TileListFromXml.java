package name.pju.alhambra.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import name.pju.alhambra.Tile;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Root(name="tilelist")
public class TileListFromXml {
	@ElementList(entry="tile", inline=true)
	public List<TileFromXml> tiles;

	public static TileList getFreshBag() {

		Serializer serializer = new Persister();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream tileFile = cl.getResourceAsStream(
				"name/pju/alhambra/resource/tiledescriptions.xml");
		TileListFromXml tlx = null;
		try {
			tlx = serializer.read(TileListFromXml.class, tileFile);
		} catch (Exception e) {
			System.out.println("xml parsing threw an exception" + e);
			return null;
		}
		List<GameTile> ts = new ArrayList<GameTile>();
		for (TileFromXml t : tlx.tiles) {
			Tile.Family f = Tile.Family.valueOf(t.color);
			GameTile gt = new GameTile(t.id, f, t.cost, t.getWalls());
			ts.add(gt);
		}
		return new TileList(ts);
	}

}
