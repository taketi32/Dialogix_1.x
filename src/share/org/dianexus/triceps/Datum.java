import java.util.*;
import java.lang.*;
import java.io.*;
import java.text.*;

public class Datum implements Serializable {
	public static final int UNKNOWN = 0;		// haven't asked
	public static final int NA = 1;				// don't need to ask - not applicable
	public static final int INVALID = 2;		// if an exception occurs - so propagated
	public static final int DOUBLE = 3;
	public static final int STRING = 4;
	public static final int DATE = 5;
	public static final int MONTH = 6;
	public static final int TIME = 7;
	private static final Date SAMPLE_DATE = new Date(System.currentTimeMillis());
	private static final Double SAMPLE_NUMBER = new Double(123.456);
	public static final String TYPES[] = { "*UNKNOWN*", "*NOT APPLICABLE*", "*INVALID*", "Double", "String", "Date", "Month", "Time"};
//	public static final String TYPE_EG_STR[] = { null, null, null, null, null, "3/15/1991", "March", "16:32:49" };

	public static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat defaultMonthFormat = new SimpleDateFormat("MMMM");
	public static final SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("hh:mm:ss");
	public static final DecimalFormat defaultNumberFormat = new DecimalFormat();


	private int type = UNKNOWN;
	private String sVal = TYPES[type];
	private double dVal = Double.NaN;
	private boolean bVal = false;
	private Date timestamp = null;
	private Date date = null;
	private Format mask = null;
	private String error = null;

	public Datum(double d) {
		type = DOUBLE;
		dVal = d;
		bVal = (Double.isNaN(d) || (d == 0)) ? false : true;
		sVal = (bVal) ? Double.toString(d) : "";
		timestamp = new Date(System.currentTimeMillis());
	}
	public Datum(int i) {
		type = i;
		sVal = null;
		bVal = false;
		dVal = Double.NaN;
		date = null;
		timestamp = new Date(System.currentTimeMillis());

		switch (i) {
			case NA:
			case UNKNOWN:
				break;
			default:
				error = "Internal error:  expected one of INVALID, UNKNOWN or NA";
				/* fall through */
			case INVALID:
				type = INVALID;
				break;
		}
	}
	public Datum(long l) {
		type = DOUBLE;
		dVal = (double)l;
		bVal = (l == 0) ? false : true;
		sVal = Long.toString(l);
		timestamp = new Date(System.currentTimeMillis());
	}
	public Datum(Datum val) {
		dVal = val.doubleVal();
		bVal = val.booleanVal();
		sVal = val.stringVal();
		date = val.date;
		type = val.type();
		mask = val.getMask();
		timestamp = new Date(System.currentTimeMillis());
	}

	public Datum(Date d, int t) {
		this(d,t,Datum.getDefaultMask(t));
	}

	public Datum(Date d, int t, Format mask) {
		String s = Datum.format(d,t,mask);
		init(s,t,mask);
	}

	public Datum(String s, int t) {
		init(s,t,Datum.getDefaultMask(t));
	}

	public Datum(String s, int t, Format mask) {
		init(s,t,mask);
	}

	private void init(String s, int t, Format mask) {
		dVal = Double.NaN;
		bVal = false;
		date = null;
		sVal = null;
		type = INVALID;	// default is to indicate failure to create new Datum object
		this.mask = mask;	// what happens when convert data types?

		if (s == null || s.trim().equals("")) {
			t = INVALID;
		}

		switch (t) {
			case DOUBLE:
				try {
					if (mask != null && mask instanceof NumberFormat) {
						dVal = ((NumberFormat) mask).parse(s).doubleValue();
						sVal = ((NumberFormat) mask).format(dVal);
						type = DOUBLE;	// only if successfully parsed
					}
					else {
						type = INVALID;
					}
				}
				catch (java.text.ParseException e) {
					String ex = Datum.getExampleFormatStr(mask,t);
					if (ex.length() > 0)
						ex = "(e.g. " + ex + ")";
					error = "Please enter a <B>" + TYPES[t] + "</B>" + ex;
					sVal = "";
					dVal = Double.NaN;
				}
/*
				try {
					dVal = Double.valueOf(s).doubleValue();
					sVal = s;
					type = DOUBLE;	// only if successful
				}
				catch(NumberFormatException e) {
					error = "Please enter a <B>" + TYPES[t] + "</B>";
					sVal = "";
					dVal = Double.NaN;
				}
*/
				bVal = (Double.isNaN(dVal) || (dVal == 0)) ? false : true;
				break;
			case STRING:
				type = STRING;
				sVal = s;
				/* also check whether can be considered a number */
				try {
					dVal = Double.valueOf(s).doubleValue();
				}
				catch(NumberFormatException e) {
					dVal = Double.NaN;
				}
				finally {
					bVal = (Double.isNaN(dVal) || (dVal == 0)) ? false : true;
				}
				break;
			case MONTH:
			case DATE:
			case TIME:
				try {
					if (mask != null && mask instanceof DateFormat) {
						date = ((DateFormat) mask).parse(s);
						sVal = ((DateFormat) mask).format(date);
						type = t;	// only if successfully parsed
					}
					else {
						type = INVALID;
					}
				}
				catch (java.text.ParseException e) {
					String ex = Datum.getExampleFormatStr(mask,t);
					if (ex.length() > 0)
						ex = "(e.g. " + ex + ")";
					error = "Please enter a <B>" + TYPES[t] + "</B>" + ex;
					date = null;
				}
				break;
			case INVALID:
				type = INVALID;
				error = "Please answer this question";
				break;
			case NA:
				type = NA;
				/* this means can skip over the question */
				break;
			default:
				type = INVALID;
				error = "Internal error: Unexpected data format: " + type;
				break;
		}
		timestamp = new Date(System.currentTimeMillis());
	}

	public Datum(boolean b) {
		type = DOUBLE;
		dVal = (b ? 1 : 0);
		bVal = b;
		sVal = (b ? "1" : "0");
		timestamp = new Date(System.currentTimeMillis());
	}

	public String stringVal() {
		return stringVal(false);
	}

	public String stringVal(boolean showReserved) {
		if (isType(Datum.STRING)) {
			return sVal;
		}
		else {
			if (showReserved)
				return TYPES[type];
			else
				return "";
		}
	}

	public boolean booleanVal() { return bVal; }
	public double doubleVal() { return dVal; }
	public Date dateVal() { return date; }
	public String monthVal() { if (date == null) return ""; return Datum.format(date,Datum.MONTH); }
	public String timeVal() { if (date == null) return ""; return Datum.format(date,Datum.TIME); }
	public long longVal() { return (long)dVal; }
	public int type() { return type; }
	public Date getTimeStamp() { return timestamp; }
	public Format getMask() { return mask; }
	public void setMask(Format mask) { this.mask = mask; }

	public boolean isValid() {
		return (isType(type) && type != INVALID);
	}

	public boolean exists() {
		return (type != UNKNOWN && isValid());
	}

	public boolean isType(int t) {
		switch(t) {
			case TIME:
			case MONTH:
			case DATE:
				return (date != null);
			case DOUBLE:
				return (dVal != Double.NaN);
			case STRING:
				return (sVal != null);
			case INVALID:
				return (type == INVALID);
			case NA:
				return (type == NA);
			case UNKNOWN:
				return (type == UNKNOWN);
			default:
				return false;
		}
	}

	public String getError() {
		if (error == null)
			return "";

		String temp = error;
		error = null;
		return temp;
	}

	static public Format buildMask(String maskStr, int t) {
		if (maskStr == null || maskStr.trim().equals(""))
			return getDefaultMask(t);

		switch (t) {
			case TIME:
			case MONTH:
			case DATE:
				try {
					return new SimpleDateFormat(maskStr);
				}
				catch (IllegalArgumentException e) {
					return null;
				}
			default:
			case DOUBLE:
				try {
					return new DecimalFormat(maskStr);
				}
				catch (IllegalArgumentException e) {
					return null;
				}
			case INVALID:
			case STRING:
			case NA:
			case UNKNOWN:
				break;
		}
		return null;
	}

	static public Format getDefaultMask(int t) {
		switch (t) {
			case MONTH:
				return defaultMonthFormat;
			case DATE:
				return defaultDateFormat;
			case TIME:
				return defaultTimeFormat;
			case DOUBLE:
				return defaultNumberFormat;
			default:
			case INVALID:
			case NA:
			case UNKNOWN:
			case STRING:
				break;
		}
		return null;
	}

	static public String format(Datum d, Format mask) {
		if (mask == null)
			return d.stringVal();

		String s;

		switch (d.type()) {
			case MONTH:
			case DATE:
			case TIME:
				try {
					s = mask.format(d.dateVal());
					if (s == null)
						return TYPES[INVALID];
					else
						return s;
				}
				catch (IllegalArgumentException e) {
					return TYPES[INVALID];
				}
			case DOUBLE:
				try {
					s = mask.format(new Double(d.doubleVal()));
					if (s == null)
						return TYPES[INVALID];
					else
						return s;
				}
				catch (IllegalArgumentException e) {
					return TYPES[INVALID];
				}
			default:
			case INVALID:
			case NA:
				return TYPES[d.type()];
			case UNKNOWN:
				return "";	// empty string to indicate that has not been assessed yet.
			case STRING:
				return d.stringVal();
		}
	}

	static public String format(Object o, int t) {
		return Datum.format(o,t,Datum.getDefaultMask(t));
	}

	static public String format (Object o, int t, String maskStr) {
		return Datum.format(o,t,Datum.buildMask(maskStr,t));
	}

	static public String format(Object o, int t, Format mask) {
		switch (t) {
			case MONTH:
			case DATE:
			case TIME:
			case DOUBLE:
				if (mask == null)
					return o.toString();

				String s;
				try {
					s = mask.format(o);
					if (s == null)
						return TYPES[INVALID];
					else
						return s;
				}
				catch (IllegalArgumentException e) {
					return TYPES[INVALID];
				}
			default:
			case INVALID:
			case NA:
				return TYPES[t];
			case UNKNOWN:
				return "";	// empty string to indicate that has not been assessed yet.
			case STRING:
				return o.toString();
		}
	}

	static public String getExampleFormatStr(Format mask, int t) {
		if (mask == null)
			return "";

		switch (t) {
			case MONTH:
			case DATE:
			case TIME:
				return format(SAMPLE_DATE, t, mask);
			case DOUBLE:
				if (mask == defaultNumberFormat)
					return "";
				else
					return format(SAMPLE_NUMBER, t, mask);
			default:
			case INVALID:
			case NA:
			case UNKNOWN:
			case STRING:
				return "";	// no formatting string to contrain input
		}
	}
}
