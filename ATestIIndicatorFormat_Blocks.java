package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
		A test framework focused on block of primitives related operations
*/
public abstract class ATestIIndicatorFormat_Blocks extends ATestIIndicatorFormatBase
{
	@Test public void testBlockBoolean_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockBoolean_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockBoolean_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockBoolean_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeBooleanBlock(new boolean[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readBooleanBlock(new boolean[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	
	@Test public void testBlockBoolean_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		
		Note: We especially test char block value passing because
		char blocks are usually specially tightly packed.
		 */
		java.util.Random r = new java.util.Random(); 
		boolean [] DAT = new boolean[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= r.nextBoolean(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BOOLEAN_BLOCK);
		p.write.writeBooleanBlock(DAT,0,1);
		p.write.writeBooleanBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		boolean [] R = new boolean[DAT.length];
		Assert.assertTrue(p.read.readBooleanBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockByte_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readByteBlock(new byte[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readByteBlock(new byte[100])==32+32-16);		
	//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockByte_1a()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		for(int i=0;i<16;i++)
		{   
			//Note: API requires that cursor is tested before each call.   
			expect(p.read.readIndicator(),TIndicator.DATA);
			Assert.assertTrue(p.read.readByteBlock()!=-1);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		for(int i=0;i<64-16;i++)
		{
			//Note: API requires that cursor is tested before each call.
			expect(p.read.readIndicator(),TIndicator.DATA);
			Assert.assertTrue(p.read.readByteBlock()!=-1);
		};
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockByte_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readByteBlock(new byte[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readByteBlock(new byte[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	@Test public void testBlockByte_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readByteBlock(new byte[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockByte_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeByteBlock(new byte[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readByteBlock(new byte[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.skip();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockByte_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value 
		
		Note: We especially test char block value passing because
		char blocks are usually specially encoded.
		*/
		java.util.Random r = new java.util.Random(); 
		byte [] DAT = new byte[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= (byte)r.nextInt(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
		p.write.writeByteBlock(DAT,0,1);
		p.write.writeByteBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_BYTE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		byte [] R = new byte[DAT.length];
		Assert.assertTrue(p.read.readByteBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockChar_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();		
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readCharBlock(new char[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readCharBlock(new char[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	@Test public void testBlockChar_1a()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock("12345678901234567890123456789012",0,32);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();	
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readCharBlock(new StringBuilder(),16)==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readCharBlock(new StringBuilder(),100)==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockChar_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();			
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readCharBlock(new char[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readCharBlock(new char[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockChar_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();			
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readCharBlock(new char[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};	
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockChar_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeCharBlock(new char[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();		
		
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readCharBlock(new char[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.skip();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};	
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockChar_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value.
		
		Note: We especially test char block value passing because
		char blocks are usually specially encoded.
		*/
		java.util.Random r = new java.util.Random(); 
		char [] DAT = new char[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= (char)r.nextInt(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_CHAR_BLOCK);
		p.write.writeCharBlock(DAT,0,1);
		p.write.writeCharBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_CHAR_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_CHAR_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		char [] R = new char[DAT.length];
		Assert.assertTrue(p.read.readCharBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockShort_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_SHORT_BLOCK);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_SHORT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readShortBlock(new short[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readShortBlock(new short[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockShort_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_SHORT_BLOCK);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_SHORT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readShortBlock(new short[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readShortBlock(new short[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockShort_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_SHORT_BLOCK);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_SHORT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readShortBlock(new short[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockShort_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_SHORT_BLOCK);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeShortBlock(new short[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_SHORT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readShortBlock(new short[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	@Test public void testBlockShort_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		 */
		java.util.Random r = new java.util.Random(); 
		short [] DAT = new short[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= (short)r.nextInt(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_SHORT_BLOCK);
		p.write.writeShortBlock(DAT,0,1);
		p.write.writeShortBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_SHORT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_SHORT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		short [] R = new short[DAT.length];
		Assert.assertTrue(p.read.readShortBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockInt_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_INT_BLOCK);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_INT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_INT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readIntBlock(new int[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readIntBlock(new int[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockInt_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_INT_BLOCK);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_INT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_INT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readIntBlock(new int[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readIntBlock(new int[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockInt_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_INT_BLOCK);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_INT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_INT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readIntBlock(new int[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockInt_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_INT_BLOCK);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeIntBlock(new int[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_INT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_INT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readIntBlock(new int[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	@Test public void testBlockInt_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		 */
		java.util.Random r = new java.util.Random(); 
		int [] DAT = new int[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= r.nextInt(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_INT_BLOCK);
		p.write.writeIntBlock(DAT,0,1);
		p.write.writeIntBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_INT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_INT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		int [] R = new int[DAT.length];
		Assert.assertTrue(p.read.readIntBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockLong_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_LONG_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readLongBlock(new long[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readLongBlock(new long[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockLong_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_LONG_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readLongBlock(new long[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readLongBlock(new long[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockLong_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_LONG_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readLongBlock(new long[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockLong_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeLongBlock(new long[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_LONG_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readLongBlock(new long[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockLong_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		 */
		java.util.Random r = new java.util.Random(); 
		long [] DAT = new long[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= r.nextLong(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_LONG_BLOCK);
		p.write.writeLongBlock(DAT,0,1);
		p.write.writeLongBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_LONG_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_LONG_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		long [] R = new long[DAT.length];
		Assert.assertTrue(p.read.readLongBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockFloat_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_FLOAT_BLOCK);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readFloatBlock(new float[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readFloatBlock(new float[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockFloat_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_FLOAT_BLOCK);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readFloatBlock(new float[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readFloatBlock(new float[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockFloat_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_FLOAT_BLOCK);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readFloatBlock(new float[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockFloat_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_FLOAT_BLOCK);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFloatBlock(new float[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readFloatBlock(new float[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	@Test public void testBlockFloat_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		 */
		java.util.Random r = new java.util.Random(); 
		float [] DAT = new float[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= r.nextFloat(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_FLOAT_BLOCK);
		p.write.writeFloatBlock(DAT,0,1);
		p.write.writeFloatBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		float [] R = new float[DAT.length];
		Assert.assertTrue(p.read.readFloatBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testBlockDouble_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readDoubleBlock(new double[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, trigger partial read.
		Assert.assertTrue(p.read.readDoubleBlock(new double[100])==32+32-16);		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockDouble_2()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to signaling
		when we use a completion read */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readDoubleBlock(new double[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//run all, NOT trigger partial read.
		Assert.assertTrue(p.read.readDoubleBlock(new double[32+16])==32+16);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testBlockDouble_3()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to full read again */
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readDoubleBlock(new double[64])==64);
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	@Test public void testBlockDouble_Skip_1()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes when it comes to skipping
		when we use a partial read.*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeDoubleBlock(new double[32],0,32);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//pick a piece of block
		Assert.assertTrue(p.read.readDoubleBlock(new double[16])==16);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//skip rest.
		p.read.next();
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	@Test public void testBlockDouble_Values()throws IOException
	{
		enter();
		/* Test if layer properly handles block writes and read backs
		when it comes to value
		 */
		java.util.Random r = new java.util.Random(); 
		double [] DAT = new double[297];
		for(int i=0;i<DAT.length;i++){ DAT[i]= r.nextDouble(); };
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("A");
		p.write.writeType(TIndicator.TYPE_DOUBLE_BLOCK);
		p.write.writeDoubleBlock(DAT,0,1);
		p.write.writeDoubleBlock(DAT,1,DAT.length-1);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE_BLOCK);
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		if (p.read.isDescribed())
		{
			expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE_BLOCK);
		};
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Pick it in one operation
		double [] R = new double[DAT.length];
		Assert.assertTrue(p.read.readDoubleBlock(R)==R.length);
		
		for(int i=0;i<DAT.length;i++)
		{
			 if (DAT[i]!=R[i]) Assert.fail("Differs at "+i);
		}; 
		
		//Should be getting end indicator and calls should be not allowed, but 
		//calls are allowed to not throw directly because it is up to us to check it.
		if (p.read.isFlushing())
		{
			Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0);
		};
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
}