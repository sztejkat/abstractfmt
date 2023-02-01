package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.test.ATest;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import org.junit.Test;
import org.junit.Assert;

/**
	A test engine for {@link AUnescapingEngine}.
*/
public class Test_AUnescapingEngine extends ATest
{
				/*
					A class which is recognizing a \u0000
					JAVA style escape sequence. Only upper case HEX is recognized.
				*/
				private static class DUT_JVA extends AUnescapingEngine
				{
								private final StringReader in;
							
						public DUT_JVA(String in)
						{
							this.in = new StringReader(in);
						};
						
						@Override protected int readImpl()throws IOException
						{
							return in.read();
						};
						@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequece_index)throws IOException
						{
							switch(sequece_index)
							{
								case 0:
										return c=='\\' ? TEscapeCharType.ESCAPE_BODY_VOID : TEscapeCharType.REGULAR_CHAR;
								case 1:
										if (c!='u') throw new IOException("Invalid escape");
										return TEscapeCharType.ESCAPE_BODY_VOID;
								case 2:
								case 3:
								case 4:
										return TEscapeCharType.ESCAPE_BODY;
								case 5:
										return TEscapeCharType.ESCAPE_LAST_BODY;
								default:
										throw new AssertionError();
							}
						};
						@Override protected char unescape(StringBuilder collection_buffer)throws IOException
						{
							System.out.println("unescape "+collection_buffer);
							Assert.assertTrue(collection_buffer.length()==4);
							try{
								return (char)(Integer.parseInt(collection_buffer.toString(),16));
							}catch(NumberFormatException ex){ throw new IOException(ex);}
						};
				};
				
				
				/*
					A class which is recognizing an XML &0;
					JAVA style escape of variable length.
					
					The & is remembered but ; is NOT.
				*/
				private static class DUT_XML extends AUnescapingEngine
				{
								private final StringReader in;
							
						public DUT_XML(String in)
						{
							this.in = new StringReader(in);
						};
						
						@Override protected int readImpl()throws IOException
						{
							return in.read();
						};
						@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequece_index)throws IOException
						{
							switch(sequece_index)
							{
								case 0:
										return c=='&' ? TEscapeCharType.ESCAPE_BODY : TEscapeCharType.REGULAR_CHAR;
								default:
										if (escape_sequence_length>=6) throw new IOException("Too long");
										return (c==';') ?  TEscapeCharType.ESCAPE_LAST_BODY_VOID :TEscapeCharType.ESCAPE_BODY ;
							}
						};
						@Override protected char unescape(StringBuilder collection_buffer)throws IOException
						{
							System.out.println("unescape "+collection_buffer);
							Assert.assertTrue(collection_buffer.length()<=6);
							Assert.assertTrue(collection_buffer.length()>=1);
							try{
								Assert.assertTrue(collection_buffer.charAt(0)=='&');
								return (char)(Integer.parseInt(collection_buffer.toString().substring(1),16));
							}catch(NumberFormatException ex){ throw new IOException(ex);}
						};
				};
				
				
	private static String collect(AUnescapingEngine e, int char_limit)throws IOException
	{
		StringBuilder sb = new StringBuilder();
		while(char_limit-->0)
		{
			int c=  e.read();
			if (c==-1) break;
			assert((c>=-1)&&(c<=0xFFFF));
			System.out.println("collected \'"+(char)c+"\'(0x"+Integer.toHexString(c)+")");
			sb.append((char)c);
		};
		System.out.println("\""+sb.toString()+"\"");
		return sb.toString();
	};
	
	/* ********************************************************************
	
			The JAVA like model
	
	
	**********************************************************************/
	@Test public void not_escape()throws IOException
	{
		enter();
			Assert.assertTrue(
					"marryane".equals(collect(new DUT_JVA("marryane"),1000))
					);
		leave();
	};
	@Test public void with_escape_interlaved()throws IOException
	{
		enter();
			Assert.assertTrue(
					"a\u3344ane\u0456x".equals(collect(new DUT_JVA("a\\u3344ane\\u0456x"),1000))
					);
		leave();
	};
	@Test public void with_escape_at_start()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\uAFECane".equals(collect(new DUT_JVA("\\uAFECane"),1000))
					);
		leave();
	};
	@Test public void with_escape_at_end()throws IOException
	{
		enter();
			Assert.assertTrue(
					"ororo\uAFEC".equals(collect(new DUT_JVA("ororo\\uAFEC"),1000))
					);
		leave();
	};
	@Test public void with_escape_after_escape()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\uAFEC\uAFDE".equals(collect(new DUT_JVA("\\uAFEC\\uAFDE"),1000))
					);
		leave();
	};
	@Test public void with_bad_escape_code_recovery()throws IOException
	{
		enter();
		DUT_JVA d = new DUT_JVA("\\xAFEC\\uAFDE");
		try{
			//this is a failure
			collect(d,1000);
			Assert.fail();
		}catch(IOException ex){ System.out.println(ex); };
		//but we should continue without no problem.
		Assert.assertTrue(
					"AFEC\uAFDE".equals(collect(d,1000))
					);
		leave();
	};
	
	@Test public void with_bad_value_recovery()throws IOException
	{
		enter();
		DUT_JVA d = new DUT_JVA("\\u0X94marry\\uAFDE");
		try{
			//this is a failure
			collect(d,1000);
			Assert.fail();
		}catch(IOException ex){ System.out.println(ex); };
		//but we should continue without no problem.
		Assert.assertTrue(
					"marry\uAFDE".equals(collect(d,1000))
					);
		leave();
	};
	
	@Test public void missing_jva()throws IOException
	{
		enter();
		try{
			//this is a failure
			collect(new DUT_JVA("\\uAFD"),1000);
			Assert.fail();
		}catch(IOException ex){ System.out.println(ex); };
		
		leave();
	};
	
	
	/* ********************************************************************
	
			The XML like model
	
	
	**********************************************************************/
	@Test public void not_escape_xml()throws IOException
	{
		enter();
			Assert.assertTrue(
					"marryane".equals(collect(new DUT_XML("marryane"),1000))
					);
		leave();
	};
	@Test public void at_start_xml()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\u0000marryane".equals(collect(new DUT_XML("&0;marryane"),1000))
					);
		leave();
	};
	@Test public void chained_xml()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\u0000\u3344marryane".equals(collect(new DUT_XML("&0;&3344;marryane"),1000))
					);
		leave();
	};
	@Test public void interlaved_xml()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\u0000z\u3344marryane".equals(collect(new DUT_XML("&0;z&3344;marryane"),1000))
					);
		leave();
	};
	@Test public void trailing_xml()throws IOException
	{
		enter();
			Assert.assertTrue(
					"marryane\u8888".equals(collect(new DUT_XML("marryane&8888;"),1000))
					);
		leave();
	};
	@Test public void missing_xml()throws IOException
	{
		enter();
		try{
			collect(new DUT_XML("marryane&888"),1000);
			Assert.fail();
		}catch(IOException ex){ System.out.println(ex);};
		leave();
	};
};