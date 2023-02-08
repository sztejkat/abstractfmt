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
				private static class DUT_JVA extends AUnescapingEngine2
				{
								private final StringReader in;
							
						public DUT_JVA(String in)
						{
							this.in = new StringReader(in);
						};
						
						@Override protected int readImpl2()throws IOException
						{
							return in.read();
						};
						@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
						{
							switch(sequence_index)
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
						@Override protected int unescape(StringBuilder collection_buffer)throws IOException
						{
							System.out.println("unescape "+collection_buffer);
							Assert.assertTrue(collection_buffer.length()==4);
							try{
								return (char)(Integer.parseInt(collection_buffer.toString(),16));
							}catch(NumberFormatException ex){ throw new IOException(ex);}
						};
				};
				
				
				/*
					A class which is recognizing an XML &0; (hex only)
					JAVA style escape of variable length.
					
					The & is remembered but ; is NOT.
				*/
				private static class DUT_XML extends AUnescapingEngine2
				{
								private final StringReader in;
							
						public DUT_XML(String in)
						{
							this.in = new StringReader(in);
						};
						
						@Override protected int readImpl2()throws IOException
						{
							return in.read();
						};
						@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
						{
							switch(sequence_index)
							{
								case 0:
										return c=='&' ? TEscapeCharType.ESCAPE_BODY : TEscapeCharType.REGULAR_CHAR;
								default:
										if (escape_sequence_length>=10) throw new IOException("Too long escape_sequence_length="+escape_sequence_length);
										return (c==';') ?  TEscapeCharType.ESCAPE_LAST_BODY_VOID :TEscapeCharType.ESCAPE_BODY ;
							}
						};
						@Override protected int unescape(StringBuilder collection_buffer)throws IOException
						{
							System.out.println("unescape "+collection_buffer);
							Assert.assertTrue(collection_buffer.length()<=9);
							Assert.assertTrue(collection_buffer.length()>=1);
							
							if (collection_buffer.length()==1) return -1; // void.
							try{
								Assert.assertTrue(collection_buffer.charAt(0)=='&');
								return (Integer.parseInt(collection_buffer.toString().substring(1),16));
							}catch(NumberFormatException ex){ throw new IOException(ex);}
						};
				};
				
				
				
				/*
					A class which is recognizing a silly
					escapes:
						\r
						\n
						but \? where ? is neither n nor r 
						produces just \
						
					I call it "silly" because it won't work correctly
					in case of eof since can't tell apart "missing data"
					from "stand-alone escape". And obviously can't
					encode \ followed by letter n.
				*/
				private static class DUT_Silly extends AUnescapingEngine2
				{
								private final StringReader in;
							
						public DUT_Silly(String in)
						{
							this.in = new StringReader(in);
						};
						
						@Override protected int readImpl2()throws IOException
						{
							return in.read();
						};
						@Override protected TEscapeCharType isEscape(char c, int escape_sequence_length, int sequence_index)throws IOException
						{
							switch(sequence_index)
							{
								case 0:
										return c=='\\' ? TEscapeCharType.ESCAPE_BODY : TEscapeCharType.REGULAR_CHAR;
								default:
										switch(c)
										{
											case 'n':
											case 'r': 
													return TEscapeCharType.ESCAPE_LAST_BODY;
											default:
													return TEscapeCharType.REGULAR_CHAR;
										}
							}
						};
						@Override protected int unescape(StringBuilder collection_buffer)throws IOException
						{
							//In this mode we have either 1 or 2 char buffer so:
							System.out.println("collection_buffer="+collection_buffer);
							char c = collection_buffer.charAt(collection_buffer.length()-1);
							switch(c)
							{
								case 'n': return '\n';
								case 'r': return '\r';
								case '\\': return '\\';
								default: throw new AssertionError("found:"+c);
							}
						}
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
	
	@Test public void test_reports_escaped()throws IOException
	{
		enter();
			DUT_JVA  d = new DUT_JVA("a\\u3344a\\u0456");
			Assert.assertTrue(d.read()=='a');
			Assert.assertTrue(!d.isEscaped());
			
			Assert.assertTrue(d.read()=='\u3344');
			Assert.assertTrue(d.isEscaped());
			
			Assert.assertTrue(d.read()=='a');
			Assert.assertTrue(!d.isEscaped());
			
			Assert.assertTrue(d.read()=='\u0456');
			Assert.assertTrue(d.isEscaped());
			
			Assert.assertTrue(d.read()==-1);
			Assert.assertTrue(!d.isEscaped());
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
	@Test public void test_with_upper_codepoint()throws IOException
	{
		enter();
			Assert.assertTrue("m\uDBFC\uDC00".equals(collect(new DUT_XML("m&10F000;"),1000)));
		
		leave();
	};
	
	@Test public void test_with_void()throws IOException
	{
		enter();
			Assert.assertTrue("m 3".equals(collect(new DUT_XML("m&;&20;3"),1000)));
		
		leave();
	};
	
	/* ***************************************************************
	
			DUT_Silly
			
	
	*****************************************************************/
	@Test public void not_escape_silly()throws IOException
	{
		enter();
			Assert.assertTrue(
					"marryane".equals(collect(new DUT_Silly("marryane"),1000))
					);
		leave();
	};
	@Test public void at_start_silly()throws IOException
	{
		enter();
			Assert.assertTrue(
					"\nmarryane".equals(collect(new DUT_Silly("\\nmarryane"),1000))
					);
		leave();
	};
	@Test public void at_end_silly()throws IOException
	{
		enter();
			Assert.assertTrue(
					"marryane\r".equals(collect(new DUT_Silly("marryane\\r"),1000))
					);
		leave();
	};
	@Test public void chained_silly()throws IOException
	{
		enter();
			Assert.assertTrue(
					"m\n\narryane".equals(collect(new DUT_Silly("m\\n\\narryane"),1000))
					);
		leave();
	};
	@Test public void silly_with_other_char()throws IOException
	{
		enter();
			Assert.assertTrue(
					"m\\xarryane".equals(collect(new DUT_Silly("m\\xarryane"),1000))
					);
		leave();
	};
	@Test public void silly_with_other_char_was_escaped()throws IOException
	{
		enter();
			DUT_Silly d = new DUT_Silly("m\\xarryane");
			Assert.assertTrue(d.read()=='m');
			Assert.assertTrue(!d.isEscaped());
			Assert.assertTrue(d.read()=='\\');
			Assert.assertTrue(d.isEscaped());
			Assert.assertTrue(d.read()=='x');
			Assert.assertTrue(!d.isEscaped());
		leave();
	};
};