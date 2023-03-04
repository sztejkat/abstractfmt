package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.test.ATest;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.*;
import sztejkat.abstractfmt.obj.*;
import java.io.IOException;
import java.io.EOFException;
import org.junit.Test;
import org.junit.Assert;


/**
	An elementary test for {@link CStructDataInput} 
	during which we write data using {@link IStructWriteFormat}
	and read using {@link CStructDataInput}.
*/
public class Test_CStructDataInput extends ATest
{
	@Test public void testReadFully()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				byte [] b = new byte[10];
				i.readFully(b,1,3);
				Assert.assertTrue(b[1]==(byte)3);
				Assert.assertTrue(b[2]==(byte)99);
				Assert.assertTrue(b[3]==(byte)100);
			};
		leave();
	};
	
	@Test public void testReadFullyFailsOnEof()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				byte [] b = new byte[10];
				try{
					i.readFully(b,1,4);
					Assert.fail();
				}catch(EEof ex){};
			};
		leave();
	};
	
	
	@Test public void testReadFullyFailsOnSignal()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				byte [] b = new byte[10];
				try{
					i.readFully(b,1,4);
					Assert.fail();
				}catch(EEof ex){ Assert.fail(); }
				 catch(EOFException ex){ };
			};
		leave();
	};
	
	
	@Test public void testSkipBytesSkips()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(i.skipBytes(2)==2);
				Assert.assertTrue(i.readByte()==(byte)100);
			};
		leave();
	};
	
	
	@Test public void testSkipBytesSkipsTouchesSignal()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(i.skipBytes(100)==3);
				Assert.assertTrue(i.skipBytes(100)==0);
			};
		leave();
	};
	
	@Test public void testSkipBytesSkipsTouchesEof()throws IOException
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
			fo.writeByte((byte)3);
			fo.writeByte((byte)99);
			fo.writeByte((byte)100);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(i.skipBytes(100)==3);
				Assert.assertTrue(i.skipBytes(100)==0);
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadBoolean()throws IOException
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
			fo.writeBoolean(true);
			fo.writeBoolean(false);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(true==i.readBoolean());
				Assert.assertTrue(false==i.readBoolean());
				try{
					i.readBoolean();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadBooleanInSignal()throws IOException
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
			fo.writeBoolean(true);
			fo.writeBoolean(false);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(true==i.readBoolean());
				Assert.assertTrue(false==i.readBoolean());
				try{
					i.readBoolean();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadByte()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//byte end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//byte use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeByte((byte)0);
			fo.writeByte((byte)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((byte)0==i.readByte());
				Assert.assertTrue((byte)-1==i.readByte());
				try{
					i.readByte();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadByteInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//byte end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//byte use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeByte((byte)0);
			fo.writeByte((byte)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((byte)0==i.readByte());
				Assert.assertTrue((byte)-1==i.readByte());
				try{
					i.readByte();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	@Test public void testReadUnsignedByte()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//byte end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//byte use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeByte((byte)0x7F);
			fo.writeByte((byte)0xFE);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0x7F==i.readUnsignedByte());
				Assert.assertTrue(0xFE==i.readUnsignedByte());
				try{
					i.readUnsignedByte();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadUnsignedByteInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//byte end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//byte use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeByte((byte)0x7F);
			fo.writeByte((byte)0xFE);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0x7F==i.readUnsignedByte());
				Assert.assertTrue(0xFE==i.readUnsignedByte());
				try{
					i.readUnsignedByte();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadShort()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//short end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//short use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeShort((short)0);
			fo.writeShort((short)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((short)0==i.readShort());
				Assert.assertTrue((short)-1==i.readShort());
				try{
					i.readShort();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadShortInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//short end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//short use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeShort((short)0);
			fo.writeShort((short)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((short)0==i.readShort());
				Assert.assertTrue((short)-1==i.readShort());
				try{
					i.readShort();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	@Test public void testReadUnsignedShort()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//short end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//short use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeShort((short)0x337F);
			fo.writeShort((short)0xFFFE);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0x337F==i.readUnsignedShort());
				Assert.assertTrue(0xFFFE==i.readUnsignedShort());
				try{
					i.readUnsignedShort();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadUnsignedShortInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//short end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//short use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeShort((short)0x347F);
			fo.writeShort((short)0x80FE);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0x347F==i.readUnsignedShort());
				Assert.assertTrue(0x80FE==i.readUnsignedShort());
				try{
					i.readUnsignedShort();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadChar()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//char end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//char use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeChar((char)0);
			fo.writeChar((char)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((char)0==i.readChar());
				Assert.assertTrue((char)-1==i.readChar());
				try{
					i.readChar();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadCharInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//char end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//char use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeChar((char)0);
			fo.writeChar((char)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((char)0==i.readChar());
				Assert.assertTrue((char)-1==i.readChar());
				try{
					i.readChar();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadInt()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//int end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//int use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeInt(0);
			fo.writeInt(-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0==i.readInt());
				Assert.assertTrue(-1==i.readInt());
				try{
					i.readInt();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadIntInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//int end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//int use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeInt(0);
			fo.writeInt(-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue(0==i.readInt());
				Assert.assertTrue(-1==i.readInt());
				try{
					i.readInt();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadLong()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//long end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//long use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeLong((long)0);
			fo.writeLong((long)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((long)0==i.readLong());
				Assert.assertTrue((long)-1==i.readLong());
				try{
					i.readLong();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadLongInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//long end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//long use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeLong((long)0);
			fo.writeLong((long)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((long)0==i.readLong());
				Assert.assertTrue((long)-1==i.readLong());
				try{
					i.readLong();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadFloat()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//float end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//float use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeFloat((float)0);
			fo.writeFloat((float)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((float)0==i.readFloat());
				Assert.assertTrue((float)-1==i.readFloat());
				try{
					i.readFloat();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadFloatInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//float end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//float use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeFloat((float)0);
			fo.writeFloat((float)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((float)0==i.readFloat());
				Assert.assertTrue((float)-1==i.readFloat());
				try{
					i.readFloat();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testReadDouble()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.writeDouble((double)0);
			fo.writeDouble((double)-1);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((double)0==i.readDouble());
				Assert.assertTrue((double)-1==i.readDouble());
				try{
					i.readDouble();
				}catch(EEof ex){}
			};
		leave();
	};
	@Test public void testReadDoubleInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			fo.writeDouble((double)0);
			fo.writeDouble((double)-1);
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue((double)0==i.readDouble());
				Assert.assertTrue((double)-1==i.readDouble());
				try{
					i.readDouble();
				}catch(EEof ex){ Assert.fail();}
				 catch(EOFException ex){};
			};
		leave();
	};
	
	
	
	
	
	
	/*
		Note:
			We don't test readLine in details. This function is pretty
			useless in any real scenario, so it must "just" work.
	*/
	@Test public void testReadLine()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
				fo.writeByte((byte)'a');
				fo.writeByte((byte)'r');
				fo.writeByte((byte)'\n');
				fo.writeByte((byte)'o');
				fo.writeByte((byte)'t');
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue( "ar".equals(i.readLine()));
				Assert.assertTrue( "ot".equals(i.readLine()));
			};
		leave();
	};
	
	
	@Test public void testReadLineInSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
				fo.writeByte((byte)'a');
				fo.writeByte((byte)'r');
				fo.writeByte((byte)'\r');
				fo.writeByte((byte)'o');
				fo.writeByte((byte)'t');
			fo.end();
			fo.close();
			
			fi.open();
			fi.next();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue( "ar".equals(i.readLine()));
				Assert.assertTrue( "ot".equals(i.readLine()));
			};
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testReadUTF()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
				fo.writeString("alloa");
			fo.end();
			fo.writeInt(33);
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				Assert.assertTrue( "alloa".equals(i.readUTF()));
				Assert.assertTrue(fi.readInt()==33);	//if end was consumed.
			};
		leave();
	};
	
	
	
	@Test public void testReadUTFMissingSignal()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
				fo.writeString("alloa");
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				try{
					i.readUTF();
					Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
			};
		leave();
	};
	
	
	@Test public void testReadUTFTooLongString()throws IOException
	{
		enter();
			CAddablePollableArrayList<IObjStructFormat0> stream 
				= new CAddablePollableArrayList<IObjStructFormat0>();
				
			CStrictObjStructWriteFormat1 fo = 
				new CStrictObjStructWriteFormat1(
								   false,//double end_begin_enabled,
								   -1,//int max_supported_recursion_depth,
								   1024,//int max_supported_name_length,
								   0, // int name_registry_capacity
								   stream
								   );
			CObjStructReadFormat1 fi = new CObjStructReadFormat1(
								  stream, //IRollbackPollable<IObjStructFormat0> stream,
								  -1,//int max_supported_recursion_depth,
								  1024,//int max_supported_name_length,
								  false,//double use_index_instead_of_order,
								  0 //int name_registry_capacity
								  );
			
			fo.open();
			fo.begin("");
			for(int i = 0;i<65536+1;i++)
							fo.writeString('a');
			fo.end();
			fo.close();
			
			fi.open();
			{
				CStructDataInput i = new CStructDataInput(fi);
				try{
					i.readUTF();
					Assert.fail();
				}catch(EBrokenFormat ex){ System.out.println(ex); };
			};
		leave();
	};
}