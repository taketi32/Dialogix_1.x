import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Schedule holds a collection of nodes.
*/
public class Schedule  {
	private Vector nodes = new Vector();
	private Vector comments = new Vector();
	private Vector reserved = new Vector();

	public Schedule() {
	}

	public boolean load(BufferedReader br, String filename) {
		if (br == null)
			return false;

		boolean err = false;

		try {
			int line = 0;
			int count=0;
			String fileLine;

			while ((fileLine = br.readLine()) != null) {
				++line;
				fileLine = fileLine.trim();
				if (fileLine.equals(""))
					continue;
					
				if (fileLine.startsWith("COMMENT")) {
					comments.addElement(fileLine);
					continue;
				}
				if (fileLine.startsWith("RESERVED")) {
					reserved.addElement(fileLine);
					continue;
				}

				Node node = new Node(line, filename, fileLine);
				++count;
				nodes.addElement(node);
			}
			System.out.println("Read " + count + " nodes from " + filename);
			return (!err);
		}
		catch(IOException e) {
			System.out.println("Unable to access " + filename);
			return false;
		}
		finally {
			if (br != null) {
				try { br.close(); } catch (Exception e) {}
			}
		}
	}

	public Node getNode(int index) {
		if (index < 0) {
			System.out.println("Node[" + index + "] does't exist");
			return null;
		}
		if (index > size()) {
			System.out.println("Node[" + index + "/" + size() + "] doesn't exist");
			return null;
		}
		return (Node)nodes.elementAt(index);
	}
	
	public Vector getComments() { return comments; }
	public Vector getReserved() { return reserved; }

	public int size() {
		return nodes.size();
	}
}
