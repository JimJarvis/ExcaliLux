package utils;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Printer Pro <br>
 * for debugging and toString, pretty prints various data structures recursively <br>
 * Settings can be changed by static setter methods.
 * @author Jim Fan	(c) 2014
 */
public class PP
{
	public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public static PrintStream out = System.out;
    public static PrintStream err = System.err;

    private static String sep = " ";
    private static String mapSep = ",\n";
    private static String mapper = " => ";
    private static int doublePrec = -1;

    /**
     * Set the separator for printing var args <br>
     * Without any argument to reset <br>
     * Default: " "
     */
    public static void setSep(String sep) { PP.sep = sep; }
    public static void setSep() { PP.sep = " "; }

    /**
     * Set the dictionary item separator <br>
     * Default: ",\n"
     */
    public static void setMapSep(String mapSep) { PP.mapSep = mapSep; }
    public static void setMapSep() { PP.mapSep = "\n"; }

    /**
     * Set the HashMap mapping symbol. <br>
     * Default: " => "
     */
    public static void setMapper(String mapper) {	PP.mapper = mapper;	}
    public static void setMapper() {	PP.mapper = " => ";	}

    /**
     * Set the precision of float/double types <br>
     * Default: -1, uses the system default precision
     */
    public static void setDoublePrec(int doublePrec)	{	PP.doublePrec = doublePrec;	}
    public static void setDoublePrec()	{	PP.doublePrec = -1;	}


    /**
     * Pretty prints any recognized object types with new line <br>
     * Supported types: array, ArrayList, Set, Map
     */
    public static void p(Object ... A) { out.println(all2str(A)); }
    public static void p()	{	out.println();	}
    // Print a single Object: don't invoke varargs
    public static void po(Object A)	{	out.println(o2str(A)); }

    /**
     * Pretty prints any recognized object types without new line
     */
    public static void p_(Object ... A) { out.print(all2str(A)); }
    // Print a single Object: don't invoke varargs
    public static void po_(Object A)	{	out.print(o2str(A)); }

    /**
     * Variable args to string
     */
    public static String all2str(Object ... args)
    {
    	String s = "";
    	for (Object A : args)
    		s += o2str(A) + sep;
    	return makeEnding(s, sep, "");
    }

    /**
     * Converts any recognized object types to String <br>
     * Supported types: array, ArrayList, Set, Map
     */
    public static String o2str(Object A)
    {
    	if (A == null)
    		return "null";
    	else if (A instanceof Map)
    		return dict2str((Map)A);
    	else if (A instanceof Set)
    		return C2str((Set)A, "{}");
    	else if (A instanceof ArrayList)
    		return C2str((ArrayList)A, "[]");
    	else if (A instanceof Collection) // handles all other generic collection
    		return C2str((Collection)A, "<>");
    	else if (A.getClass().isArray())  // all arrays
    		return arr2str(A);
    	else if ((A instanceof Float || A instanceof Double)
    				&& doublePrec >= 0) // If -1, we don't use any specific precision
    		return String.format("%1$."+doublePrec+"f", (double) A);
    	else // call the most generic method
    		return A.toString();
    }

    /**
     * Joins an iterable object by a separator into a string
     * @param C must be either a Collection or an Object[]
     */
    public static String join(Object C, String sep)
    {
    	if (C instanceof Collection)
    		return C2str((Collection) C, "", "", sep);
    	else // all other array types
    		return arr2str(C, "", "", sep);
    }

    /**
     * Pad a string with space
     * @param space how many space in total
     * @param justified 1 for right and -1 for left justification. Default: right
     */
    public static String pad(Object s, int space, int justified)
    {
    	return String.format("%1$" + (justified  > 0 ? "" : "-") + space + "s", o2str(s));
    }
    public static String pad(Object s, int space)	{	return pad(s, space, 1);	}


    /**
     * Converts an array to string. <br>
     * Recursive pretty print
     * @param left left enclosing parenthesis
     * @param right right enclosing parenthesis
     * @param sep separator
     */
    public static String arr2str(Object A, String left, String right, String sep)
    {
		String s = left;

    	if ((A instanceof double[] || A instanceof float[] ||
    				A instanceof Double[] || A instanceof Float[])
    				&& doublePrec >= 0)
    		for (int i = 0; i < Array.getLength(A); ++i)
    			s += String.format("%1$."+doublePrec+"f", Array.get(A, i)) + sep;

    	else if (A instanceof int[] || A instanceof byte[] ||
			A instanceof long[] || A instanceof short[] ||
			A instanceof char[] || A instanceof boolean[] ||
			A instanceof double[] || A instanceof float[])
    		for (int i = 0; i < Array.getLength(A); ++i)
    			s += Array.get(A, i) + sep;

    	else // Object array: the last resort
    		for (Object ele : (Object []) A)
    			s += o2str(ele) + sep;

		return makeEnding(s, sep, right);
    }
    private static String arr2str(Object A) {	return arr2str(A, "[", "]", ", ");	}

    /**
     * Converts a (hash)map to string. <br>
     * Recursive pretty print { => }
     */
    private static String dict2str(Map map)
    {
    	String s = "{";
    	Iterator it = map.entrySet().iterator();
    	while (it.hasNext())
    	{
    		Map.Entry pairs = (Map.Entry) it.next();
    		s += o2str(pairs.getKey()) + mapper + o2str(pairs.getValue()) + mapSep;
    	}
    	return makeEnding(s, mapSep, "}");
    }

    /**
     * Converts any generic Collection to string
     * @param C either a Collection or an Object[] <br>
     * Recursive pretty print
     */
    public static String C2str(Collection C, String left, String right, String sep)
    {
    	String s = left;
    	for (Object obj : (Collection) C)
    		s += o2str(obj) + sep;
    	return makeEnding(s, sep, right);
    }
    /**
     * Default separator = ", " <br>
     * encloser: "<>"
     */
    public static String C2str(Collection C, String encloser)
    {
    	return C2str(C, encloser.substring(0, 1), encloser.substring(1), ", ");
    }

    // Helper method: for the last closing parenthesis
    private static String makeEnding(String s, String sep, String end)
    {
    	if (s.length() < sep.length()) // this 'Collection' is empty
    		return s + " " + end;
		return s.substring(0, s.length() - sep.length()) + end;
    }

    /**
     * Generate a line that separates different sections
     * @param sep separator pattern
     * @param length repeat how many times the separator
     */
    public static String getSectionLine(String sep, int length)
    {
    	String line = "";
    	for (int i = 0; i < length; i++)
			line += sep;
    	return line + "\n";
    }
    /**
     * Default to separator is asterisk "*"
     */
    public static String getSectionLine(int length) {	return getSectionLine("*", length);	}
    /**
     * Default separator asterisk and length 33
     */
    public static String getSectionLine() {	return getSectionLine("*", 33);	}

    /**
     * Print a line that separates different sections
     * @param sep separator pattern
     * @param length repeat how many times the separator
     */
    public static void pSectionLine(String sep, int length) {	po(getSectionLine(sep, length));	}
    /**
     * Default to separator is asterisk "*"
     */
    public static void pSectionLine(int length) {	po(getSectionLine(length));	}
    /**
     * Default separator asterisk and length 33
     */
    public static void pSectionLine() {	po(getSectionLine());	}

    /**
     * Prints a formatted string with new line
     * @param fmstr format string that complies with the String.format() JDK std
     */
    public static void pFormat(String fmtstr, Object... objs) {	po(String.format(fmtstr, objs)); }
    /**
     * Prints a formatted string without new line
     * @param fmstr format string that complies with the String.format() JDK std
     */
    public static void pFormat_(String fmtstr, Object... objs) {	po_(String.format(fmtstr, objs)); }


    // For testing and debugging only
    private static void main(String[] args)
    {
    	setSep(";\n\n");
    	setMapper(": ");
    	setDoublePrec(3);

    	HashSet<ArrayList> hs = new HashSet<>();

    	ArrayList<String> al1 = new ArrayList<>();
    	al1.add("hsAL7"); al1.add("hsAL2"); al1.add("hsAL5");
    	ArrayList<Integer> al2 = new ArrayList<>();
    	al2.add(100); al2.add(300); al2.add(400); al2.add(900);
    	ArrayList<Double> al3 = new ArrayList<>();
    	al3.add(0.8); al3.add(54.9998);
    	hs.add(al1); hs.add(al2); hs.add(al3);

    	HashSet al4 = new HashSet<>();
    	HashSet hstmp = new HashSet<>();
    	hstmp.add(3); hstmp.add("hmHS2");
    	al4.add("hmAL7"); al4.add(hstmp); al4.add(al2);
    	ArrayList<byte[]> al5 = new ArrayList<>();
    	al5.add(new byte[] {11, 22, 33});
    	al5.add(new byte[] {44, 55});
    	al5.add(new byte[] {77, 88, 99});
    	HashSet<boolean[][]> al6 = new HashSet<>();
    	al6.add(new boolean[][] {{true}, {false, true}, {}});
    	al6.add(new boolean[][] {{}, {false, true, true, true}, {false, false}});

    	HashMap hm = new HashMap<>();
    	hm.put(new char[] {'#', '@', 'W'}, al4);
    	hm.put(new Object[] {new boolean[] {true, false}, "next", new float[]{-13.5008f}}, al5);
    	HashSet<Character> hstmp2 = new HashSet<>();
    	hstmp2.add('&'); hstmp2.add('$');
    	hm.put(hstmp2, al6);

    	HashMap hm2 = new HashMap<>();
    	hm2.put(new Object[]{"hello!", new HashSet<>(), al3}, new HashMap<>());
    	hm2.put(new ArrayList<>(), new HashSet<>());
    	HashMap hm2cp = new HashMap(hm2);
    	hm2.put(-999, hm2cp);
    	LinkedList<Integer> ll = new LinkedList<>(al2);
    	hm2.put("LinkedList", ll);

    	p("Debugging", hs, new int[] {-10, -23, -8}, 2031.6666666,
    			new double[] {Math.PI, Math.E, Math.log(5), Math.sin(1)},
    			hm, new String[][] {{"str00", "str01", "str03"}, {"str11", "str12"}, {"str21"}}, hm2);
    	setDoublePrec(-1);
    	p_("\nJoin:  ");
		p(join(new float[] {89.2387428f, -14f, 3.2f, 10.0058f, 27f}, " * "));
    }
}
