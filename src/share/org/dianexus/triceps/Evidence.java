import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Contains data generated at each node.  Such data are produced either
 * by the person running the interview in response to
 * questions, or by the system evaluating previously stored evidence
 */
public class Evidence  {
	private Hashtable aliases = new Hashtable();
	private Vector values = new Vector();
	private int	numReserved = 0;

	public Evidence(Schedule schedule) {
		if (schedule == null) {
			return;
		}
		
		int size = schedule.size();

		numReserved = Schedule.RESERVED_WORDS.length;	// these are always added at the beginning

		Node node;
		Value value;
		int idx=0;

		/* first assign the reserved words */
		for (idx=0;idx<Schedule.RESERVED_WORDS.length;++idx) {
			value = new Value(Schedule.RESERVED_WORDS[idx],new Datum(schedule.getReserved(idx),Datum.STRING),idx,schedule);
			values.addElement(value);
			addAlias(null,Schedule.RESERVED_WORDS[idx],new Integer(idx));
		}


		/* then assign the user-defined words */
		for (int i = 0; i < size; ++i, ++idx) {
			node = schedule.getNode(i);
			value = new Value(node, Datum.getInstance(Datum.UNASKED),node.getAnswerTimeStampStr());

			values.addElement(value);

			Integer j = new Integer(idx);

			addAlias(node,node.getConcept(),j);
			addAlias(node,node.getLocalName(),j);
			addAlias(node,node.getExternalName(),j);
			aliases.put(node,j);
		}
	}

	private void addAlias(Node n, String alias, Integer index) {
		if (alias == null || alias.equals(""))
			return;	// ignore invalid aliases

		Object o = aliases.put(alias,index);
		if (o != null) {
			try {
				int pastIndex = ((Integer) o).intValue();

				if (pastIndex != index.intValue()) {
					/* Allow a single node to try to set the same alias for itself multiple times.
					However, each node must have non-overlapping aliases with other nodes */
					aliases.put(alias,o);	// restore overwritten alias?
					Node prevNode = ((Value) values.elementAt(pastIndex)).getNode();
					n.setParseError("Duplicate alias <B>" + Node.encodeHTML(alias) + "</B> previously used for node <B>" + Node.encodeHTML(prevNode.getLocalName()) + "</B> on line " + prevNode.getSourceLine());
				}
			} catch (Throwable t) {
				System.err.println("Unexpected error: " + t.getMessage());
			}
		}
	}

	public boolean containsKey(Object val) {
		if (val == null)
			return false;
		return aliases.containsKey(val);
	}

	public Datum getDatum(Object val) {
		int i = getNodeIndex(val);
		if (i == -1) {
			return null;
		}
		return ((Value) values.elementAt(i)).getDatum();
	}

	public Node getNode(Object val) {
		int i = getNodeIndex(val);
		if (i == -1) {
//			System.err.println("Node not found: " + val);
			return null;
		}
		return ((Value) values.elementAt(i)).getNode();
	}

	public int getStep(Object n) {
		if (n == null)
			return -1;
		int step = getNodeIndex(n);
		if (step == -1)
			return -1;
		else
			return (step - numReserved);
	}

	private int getNodeIndex(Object n) {
		if (n == null)
			return -1;
		Object o = aliases.get(n);
		if (o != null || !(n instanceof Node))
			return ((Integer) o).intValue();
			
		Node node = (Node) n;
		o = aliases.get(node.getConcept());
		if (o != null)
			return ((Integer) o).intValue();
		
		o = aliases.get(node.getLocalName());
		if (o != null)
			return ((Integer) o).intValue();
			
		return -1;
	}

	public void set(Node node, Datum val, String time) {
		if (node == null) {
			System.err.println("null Node");
			return;
		}
		if (val == null) {
			System.err.println("null Datum");
			return;
		}
		int i;

		i = getNodeIndex(node);
		if (i == -1) {
			System.err.println("Node does not exist");
			return;
		}

		((Value) values.elementAt(i)).setDatum(val,time);
	}

	public void set(Node node, Datum val) {
		set(node,val,null);
	}

	public void set(String name, Datum val) {
		if (name == null) {
			System.err.println("null Node name");
			return;
		}
		if (val == null) {
			System.err.println("null Datum");
			return;
		}

		int i = getNodeIndex(name);
		if (i == -1) {
			i = size();	// append to end
			Value value = new Value(name,val);
			values.addElement(value);
			aliases.put(name, new Integer(i));
		}
		else {
			((Value) values.elementAt(i)).setDatum(val,null);
		}
	}

	public int size() {
		return values.size();
	}

	public String toXML() {
		StringBuffer sb = new StringBuffer("<Evidence>\n");
		Enumeration e = aliases.keys();

		while (e.hasMoreElements()) {
			String s = (String)e.nextElement();
			sb.append("	<datum name='" + s + "' value='" + toString(s) + "'/>\n");
		}
		sb.append("</Evidence>");
		return sb.toString();
	}

	public String toString(Object val) {
		Datum d = getDatum(val);
		if (d == null)
			return "null";
		else
			return d.stringVal();
	}

	public void unset(Node node) {
		if (node == null)
			return;
		Integer i = (Integer)aliases.remove(node);

		if (i != null) {
			aliases.remove(node.getConcept());
			aliases.remove(node.getLocalName());
			aliases.remove(node.getExternalName());

			values.setElementAt(new Value(), i.intValue());
		}
	}

	public void unset(String name) {
		Integer i = (Integer)aliases.remove(name);
		if (i != null) {
			values.setElementAt(new Value(), i.intValue());
		}
	}
}
