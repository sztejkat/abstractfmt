package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CStructDataOutput} 
	during which we write data using {@link CStructDataOutput}
	and read using {@link IStructReadFormat}.
*/
public class Test_CStructDataOutput extends ATest
{
	@Test public void testWriteBlockMixed()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.write(0xFA);
				o.write(new byte[]{(byte)33,(byte)34,(byte)0x81,(byte)36},1,2);
			}
			fo.close();
			
			fi.open();
			{
				//This is NOT a byte block!
				Assert.assertTrue(fi.readByte()==(byte)0xFA);
				Assert.assertTrue(fi.readByte()==(byte)34);
				Assert.assertTrue(fi.readByte()==(byte)0x81);
				try{
					fi.readByte();
					Assert.fail();
				}catch(EEof ex){};
			};
		leave();
	};
	
	@Test public void testWriteBlockMixedInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.write(0xFA);
				o.write(new byte[]{(byte)33,(byte)34,(byte)0x81,(byte)36},1,2);
			}
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				//This is NOT a byte block!
				Assert.assertTrue(fi.readByte()==(byte)0xFA);
				Assert.assertTrue(fi.readByte()==(byte)34);
				Assert.assertTrue(fi.readByte()==(byte)0x81);
				try{
					fi.readByte();
					Assert.fail();
				}catch(ENoMoreData ex){};
			};
		leave();
	};
	
	
	
	@Test public void testWriteElementary()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.writeBoolean(false);
				o.writeByte(0xFA);
				o.writeShort(0x3aFA);
				o.writeChar('c');
				o.writeInt(0x33445566);
				o.writeLong(0x33445566_11223344L);
				o.writeFloat(-1e31f);
				o.writeDouble(21.0E-4);
			}
			fo.close();
			
			fi.open();
			{
				Assert.assertTrue(fi.readBoolean()==false);
				Assert.assertTrue(fi.readByte()==(byte)0xFA);
				Assert.assertTrue(fi.readShort()==(short)0x3aFA);
				Assert.assertTrue(fi.readChar()=='c');
				Assert.assertTrue(fi.readInt()==0x33445566);
				Assert.assertTrue(fi.readLong()==0x33445566_11223344L);
				Assert.assertTrue(fi.readFloat()==-1e31f);
				Assert.assertTrue(fi.readDouble()==21.0E-4);
				try{					
					fi.readByte();
					Assert.fail();
				}catch(EEof ex){};
			};
		leave();
	};
	
	
	
	@Test public void testWriteBytes()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.writeBytes("012");
				o.writeBytes("3\u4404");
			}
			fo.close();
			
			fi.open();
			{
				//This is NOT a byte block!
				Assert.assertTrue(fi.readByte()==(byte)'0');
				Assert.assertTrue(fi.readByte()==(byte)'1');
				Assert.assertTrue(fi.readByte()==(byte)'2');
				Assert.assertTrue(fi.readByte()==(byte)'3');
				Assert.assertTrue(fi.readByte()==(byte)4);
				try{					
					fi.readByte();
					Assert.fail();
				}catch(EEof ex){};
			};
		leave();
	};
	
	
	@Test public void testWriteChars()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.writeChars("012");
				o.writeChars("3\u4404");
			}
			fo.close();
			
			fi.open();
			{
				//This is NOT a char block!
				Assert.assertTrue(fi.readChar()=='0');
				Assert.assertTrue(fi.readChar()=='1');
				Assert.assertTrue(fi.readChar()=='2');
				Assert.assertTrue(fi.readChar()=='3');
				Assert.assertTrue(fi.readChar()=='\u4404');
				try{					
					fi.readByte();
					Assert.fail();
				}catch(EEof ex){};
			};
		leave();
	};
	
	
	
	@Test public void testWriteUTF()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				CStructDataOutput o = new CStructDataOutput(fo);
				o.writeUTF("012");
				o.writeUTF("abc");
			}
			fo.close();
			
			fi.open();
			{
				Assert.assertTrue("".equals(fi.next()));
				Assert.assertTrue("012".equals(fi.readString(1000)));
				Assert.assertTrue(null==fi.next());
				Assert.assertTrue("".equals(fi.next()));
				Assert.assertTrue("abc".equals(fi.readString(1000)));
				Assert.assertTrue(null==fi.next());
			};
		leave();
	};
	
	@Test public void testWriteUTFTooLong()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//boolean end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//boolean use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			{
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<65536+1; i++) sb.append('a');
				
				CStructDataOutput o = new CStructDataOutput(fo);
				try{
					o.writeUTF(sb.toString());
					Assert.fail();
				}catch(java.io.UTFDataFormatException ex){};
			}
			fo.close();
			
		leave();
	};
};
	