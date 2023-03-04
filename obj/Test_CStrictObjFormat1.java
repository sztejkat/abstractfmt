package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.test.*;
import sztejkat.abstractfmt.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
		A test checking if {@link CStrictObjStructWriteFormat1} 
		paired with {@link CObjStructReadFormat1} do barf when expected.
		<p>
		This test suite dumps test files content to text files on writer close
		using struct level indentation. 
*/
public class Test_CStrictObjFormat1 extends AInterOpTestCase<CObjStructReadFormat1,CStrictObjStructWriteFormat1>
{
	
	@BeforeClass public static void armImplementation()
	{
		TestSuite_StrictObjFormat1.armImplementation();
	};
	@AfterClass public static void disarmImplementation()
	{
		TestSuite_StrictObjFormat1.disarmImplementation();
	};
	
	//Note: Surprise! Junit won't run @Test annotated classes in a
	//		@RunWith(Suite.class)
	//		This is reasonable, but surprising.
	
	/* ***********************************************************************
	
	
				Test if they barf.
				
			Notice we are NOT testing the type contract, even tough
			the effect is very alike. 
			
			We just test if code can DETECT abusive use behavior
	
	
	************************************************************************/
	
	
	/* -------------------------------------------------------------------
				elementary boolean abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnBoolean_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnBoolean_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBoolean(false);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary byte abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnByte_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnByte_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByte((byte)0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary short abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnShort_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnShort_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShort((short)0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary char abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnChar_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnChar_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeChar((char)0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary int abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnInt_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnInt_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeInt(0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary long abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnLong_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnLong_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLong((long)0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary float abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnFloat_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnFloat_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloat((float)0);
		w.close();
		r.open();
		try{
				r.readDouble();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				elementary double abuse
	-------------------------------------------------------------------*/
	@Test public void testBarfsOnDouble_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readBoolean();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readChar();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readShort();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readInt();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readLong();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readFloat();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBarfsOnDouble_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDouble((double)0);
		w.close();
		r.open();
		try{
				r.readByte();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	/* *********************************************************************
	
	
			blocks abuse
	
	
	
	
	********************************************************************** */
	
	
	
	
	
		/* -------------------------------------------------------------------
				block boolean abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnBoolean_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnBoolean_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeBooleanBlock(false);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block byte abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnByte_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnByte_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeByteBlock((byte)0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block short abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnShort_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnShort_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeShortBlock((short)0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block char abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnChar_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnChar_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeCharBlock((char)0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block int abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnInt_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnInt_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeIntBlock(0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block long abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnLong_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnLong_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeLongBlock((long)0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block float abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnFloat_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnFloat_toDouble()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeFloatBlock((float)0);
		w.close();
		r.open();
		try{
				r.readDoubleBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block double abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnDouble_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnDouble_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeDoubleBlock((double)0);
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* -------------------------------------------------------------------
				block string abuse
	-------------------------------------------------------------------*/
	@Test public void testBlockBarfsOnString_toBoolean()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readBooleanBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toChar()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readCharBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toShort()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readShortBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toInt()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readIntBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toLong()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readLongBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toFloat()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readFloatBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	@Test public void testBlockBarfsOnString_toByte()throws IOException
	{
		enter();
		CPair<?,?> p = AInterOpTestCase.factory.createTestDevice(this.getClass());
		final IStructWriteFormat w= p.writer;
		final IStructReadFormat  r= p.reader;
		w.open();
			w.writeString('d');
		w.close();
		r.open();
		try{
				r.readByteBlock();
				Assert.fail();
			}catch(EAbusedFormat ex){ System.out.println(ex); };
		r.close();
		leave();
	};
	
	
	
	
	
	
	
};
