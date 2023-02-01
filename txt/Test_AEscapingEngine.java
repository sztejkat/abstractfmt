package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.test.ATest;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
	A test engine for {@link AEscapingEngine}.
*/
public class Test_AEscapingEngine extends ATest
{
			/** Used to represent escaped character */
			private static class Escape
			{
					public final char escaped;
					
					Escape(char c){ this.escaped = c; };
					public int hashCode(){ return escaped; };
					public boolean equals(Object o)
					{
						return (o!=null)
								&&
								(o instanceof Escape)
								&&
								(((Escape)o).escaped==this.escaped);
					};
					public String toString(){ return "Escape:"+escaped+"(0x"+Integer.toHexString(escaped)+")";};
			};
			private static final char [] MUST_ESCAPE = 
										new char[]{ '\"', '*' };
			private static final int [] MUST_ESCAPE_CODEPOINT =
										new int []
										{
											0x0,	//intentionally overlapping MUST_ESCAPE
											0x1,
											0x2,
											0x0010_7341
										};
			/** An engine which collects all invocations
			of {@link #out} and {@link #escape}
			using {@link Character} or {@link Escape}
			objects */
			private static class DUT extends AEscapingEngine
			{
					/** A collected stream of {@link Character}
					for {@link #out} or {@link Escape} for {@link #escape} */
					public final LinkedList<Object> stream;
					
				DUT(){ this.stream = new LinkedList<Object>(); };
				
				@Override protected boolean mustEscape(char c)
				{
					for(int i=MUST_ESCAPE.length;--i>=0;)
					{
						if (c==MUST_ESCAPE[i]) return true;
					};
					return false;
				};
				@Override protected boolean mustEscapeCodepoint(int code_point)
				{
					for(int i=MUST_ESCAPE_CODEPOINT.length;--i>=0;)
					{
						if (code_point==MUST_ESCAPE_CODEPOINT[i]) return true;
					};
					return false;
				};
				@Override protected void escape(char c)throws IOException
				{
					//Note: We don't use out to tell apart escapes in log
					stream.add(new Escape(c));
				};
				@Override protected void out(char c)throws IOException
				{
					stream.add(Character.valueOf(c));
				};
			}
	
	private static void compare(DUT d,Object [] expected)
	{
		System.out.println("escaped:"+d.stream);
		Iterator<Object> I = d.stream.iterator();
		for( int i = 0, n=expected.length;i<n;i++)
		{
			Assert.assertTrue(I.hasNext());
			Object o = I.next();
			Object e = expected[i];
			System.out.println("\t "+o+" vs "+e);
			Assert.assertTrue(e.equals(o));
		};
		Assert.assertTrue(!I.hasNext());
	}
	private void testString_Char_by_char(String to_escape, Object [] expected)throws IOException
	{
		enter();
		System.out.println("Escaping:\""+to_escape+"\"");
		DUT d = new DUT();
			for(int i=0,n=to_escape.length(); i<n; i++)
			{
				d.append(to_escape.charAt(i));
			};
			d.flush();
			compare(d,expected);
		leave();
	};
	private void testString_bySequence(String to_escape, Object [] expected)throws IOException
	{
		enter();
		System.out.println("Escaping:\""+to_escape+"\"");
		DUT d = new DUT();
			d.append(to_escape);
			d.flush();
			compare(d,expected);
		leave();
	};
	private void testString_byArray(String to_escape, Object [] expected)throws IOException
	{
		enter();
		System.out.println("Escaping:\""+to_escape+"\"");
		DUT d = new DUT();
			d.append(to_escape.toCharArray());
			d.flush();
			compare(d,expected);
		leave();
	};
	@Test public void testJustPlainString()throws IOException
	{
		enter();
				final String s = "ammy";
				final Object [] exp = new Object[]
							{
								'a','m','m','y' 
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	
	@Test public void testCharsEscape()throws IOException
	{
		enter();
				final String s = "a\"m*";
				final Object [] exp = new Object[]
							{
								'a',new Escape('\"'),'m', new Escape('*') 
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	
	@Test public void testCodePointEscape()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").
						appendCodePoint(0x0010_7341).
						append('y').toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape(
										(char)(((0x0010_7341-0x1_0000)>>10)+0xD800)
										),
								new Escape(
										(char)(((0x0010_7341-0x1_0000) & 0x3FF)+0xDC00)
										),
								'y'
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	
	@Test public void testLowerCodePointEscape()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").
						appendCodePoint(0).appendCodePoint(1).appendCodePoint(2).
						append('y').toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0),
								new Escape((char)1),
								new Escape((char)2),
								'y'
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	
	@Test public void testDanglingUpperSurogate()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xD800).toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xD800)
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	@Test public void testDanglingUpperSurogate2()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xDBFF).toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xDBFF)
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	@Test public void testStandAloneUpperSurogate()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xD933).append('c').toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xD933),
								'c'
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	@Test public void testStandAloneLowerSurogate()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xDC33).append('c').toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xDC33),
								'c'
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	@Test public void testReverseSurogates()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xDC33).append((char)0xD800).append('c').toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xDC33),
								new Escape((char)0xD800),
								'c'
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	
	@Test public void testDanglingLowerSurogate()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xDC00).toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xDC00)
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
	@Test public void testDanglingLowerSurogate2()throws IOException
	{
		enter();
				final String s = 
					new StringBuilder().append("am").append((char)0xDFFF).toString();
				final Object [] exp = new Object[]
							{
								'a',
								'm',
								new Escape((char)0xDFFF)
							};
				testString_Char_by_char(s,exp);
				testString_bySequence(s,exp);
				testString_byArray(s,exp);
		leave();
	};
};