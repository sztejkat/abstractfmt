package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.util.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormat1}.
*/
public class Test_ATxtReadFormat1 extends ATest
{
		protected static class CStreamItem 
		{
				public final char character;
				public final ATxtReadFormat1.TIntermediateSyntax syntax;
				
			public CStreamItem(char c, ATxtReadFormat1.TIntermediateSyntax s)
			{
				this.character = c;
				this.syntax = s;
			};
			public String toString(){ return "{"+character+","+syntax+"}"; };
		};
		
		protected static class CStream extends LinkedList<CStreamItem>
		{	
				private static final long serialVersionUID = 1L; //for -xlint only.
			public void add(char c, ATxtReadFormat1.TIntermediateSyntax s)
			{
				add(new CStreamItem(c,s));
			};
			public String toString()
			{
				StringBuilder sb = new StringBuilder();
				int i=0;
				for(CStreamItem I: this)
				{
					sb.append(i+":"+I.toString()+"\n");
				};
				return sb.toString();
			};
		};
		
		/** A device under test. Very limited implementation */
		private static final class DUT extends ATxtReadFormat1<ATxtReadFormat1.TIntermediateSyntax>
		{
					private final Iterator<CStreamItem> stream;
					private CStreamItem current = null;
			/** Creates
			@param stream tokens to read
			*/
			protected DUT(Iterator<CStreamItem> stream)
			{
				super(100,65);
				this.stream=stream;
			};
			/* ******************************************************
				
				ATxtReadFormat1
			
			*******************************************************/
			@Override protected void toNextChar()throws IOException
			{
				if (stream.hasNext()) current = stream.next();
				else
					current = null;
			};
			@Override protected ATxtReadFormat1.TIntermediateSyntax getNextSyntaxElement()
			{
				return current==null ? null : current.syntax;
			};
			@Override protected int getNextChar()
			{
				return current==null ? -1 : current.character;
			};
			/* ****************************************************
	
				AStructReadFormatBase0, strongly faked.
	
	
			* ***************************************************/
			@Override protected void closeImpl(){};
			@Override protected void openImpl(){};
			@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
			@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
		
		
	@Test public void test_NotEnclosed_Sequence_of_elementary()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('0',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add(',',ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('\n',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue(d.readInt()==0);
			Assert.assertTrue(d.readInt()==33);
			Assert.assertTrue(d.readLong()==44);
			d.close();
		leave();
	};
	
	
	@Test public void test_signal_begin_direct()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			d.close();
		leave();
	};
	
	@Test public void test_signal_begin_direct_with_enclosed_name()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			d.close();
		leave();
	};
	@Test public void test_signal_begin_direct_empty_name()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			d.close();
		leave();
	};
	@Test public void test_signal_begin_register_index()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('!',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			input.add('5',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			d.close();
		leave();
	};
	
	@Test public void test_signal_begin_register_index_reuse()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('!',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			input.add('5',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			
			input.add('&',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);			
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			input.add('5',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue("mama".equals(d.next()));
			d.close();
		leave();
	};
	
	
	
	@Test public void test_signal_begin_register_order()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('!',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.SIG_ORDER);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			d.close();
		leave();
	};
	
	@Test public void test_signal_begin_register_index_order()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('!',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('-',ATxtReadFormat1.TIntermediateSyntax.SIG_ORDER);
			
			input.add('!',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add('t',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('t',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('\"',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME_VOID);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('-',ATxtReadFormat1.TIntermediateSyntax.SIG_ORDER);
			
			input.add('&',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);			
			input.add('1',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			
			input.add('&',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);			
			input.add('0',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue("tata".equals(d.next()));
			Assert.assertTrue("tata".equals(d.next()));
			Assert.assertTrue("mama".equals(d.next()));
			d.close();
		leave();
	};
	
	
	
	@Test public void test_signal_begin_direct_end()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			Assert.assertTrue(!d.hasElementaryData());
			Assert.assertTrue(null==d.next());
			d.close();
		leave();
	};
	
	@Test public void test_signal_begin_direct_end_begin()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END_BEGIN);
			input.add('x',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(',',ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('7',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("x".equals(d.next()));
			Assert.assertTrue(d.readInt()==7);
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(!d.hasElementaryData());			
			d.close();
		leave();
	};
	
	@Test public void test_signal_begin_direct_end_begin_register()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('m',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('a',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('x',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_ORDER);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(',',ATxtReadFormat1.TIntermediateSyntax.NEXT_TOKEN);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.VOID);
			input.add('7',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('0',ATxtReadFormat1.TIntermediateSyntax.SIG_INDEX);
			input.add('3',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("mama".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("x".equals(d.next()));
			Assert.assertTrue(d.readInt()==7);
			Assert.assertTrue(null==d.next());
			Assert.assertTrue("x".equals(d.next()));
			Assert.assertTrue(d.readInt()==3);
			Assert.assertTrue(!d.hasElementaryData());			
			d.close();
		leave();
	};
	
	@Test public void test_enclosed_ENoMoreData()throws IOException
	{
		enter();
			CStream input = new CStream();
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('x',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			input.add('c',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.open();
			Assert.assertTrue("x".equals(d.next()));
			Assert.assertTrue(d.readInt()==44);
			try{
					d.readChar();
					Assert.fail();
			}catch(ENoMoreData ex){};
			try{
					d.readLong();
					Assert.fail();
			}catch(ENoMoreData ex){};
			Assert.assertTrue(null==d.next());
			Assert.assertTrue(d.readChar()=='c');
			d.close();
		leave();
	};
	
	
	@Test public void test_nameLimit()throws IOException
	{
		enter();
			CStream input = new CStream();
			
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('x',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('y',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('z',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			input.add('c',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.setMaxSignalNameLength(2);
			d.open();
			try{
				d.next();
				Assert.fail();
			}catch(EFormatBoundaryExceeded ex){};
			d.close();
		leave();
	};
	
	@Test public void test_nameLimitAccuracy()throws IOException
	{
		enter();
			CStream input = new CStream();
			
			input.add('*',ATxtReadFormat1.TIntermediateSyntax.SIG_BEGIN);
			input.add('x',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('y',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add('z',ATxtReadFormat1.TIntermediateSyntax.SIG_NAME);
			input.add(' ',ATxtReadFormat1.TIntermediateSyntax.SEPARATOR);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('4',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			input.add('.',ATxtReadFormat1.TIntermediateSyntax.SIG_END);
			input.add('c',ATxtReadFormat1.TIntermediateSyntax.TOKEN);
			
			System.out.println(input);
			DUT d = new DUT(input.iterator());
			d.setMaxSignalNameLength(3);
			d.open();
			Assert.assertTrue("xyz".equals(d.next()));
			d.close();
		leave();
	};
};