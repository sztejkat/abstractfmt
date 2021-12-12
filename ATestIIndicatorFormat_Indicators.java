package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
		A test framework focused on indicator related operations
*/
public abstract class ATestIIndicatorFormat_Indicators extends ATestIIndicatorFormatBase
{

	@Test public void testEmpty()throws IOException
	{
		enter();
		/*	Check if empty gives eof.*/
		Pair p = create(16,0);
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};
	
	
	
	@Test public void testDirectIndicators_basic()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create(16,0);
		p.write.writeBeginDirect("arabica");
		p.write.writeBeginDirect("jamaica");
		p.write.writeEnd();
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		try{
			p.read.getSignalName(); //if invalidated?
			Assert.fail();
		}catch(IllegalStateException ex){};
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF); //intentionally double
		leave();
	};
	
	@Test public void testDirectIndicators_sequenced()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create(16,0);
		p.write.writeBeginDirect("arabica");
		p.write.writeEnd();
		p.write.writeBeginDirect("jamaica");		
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};
	
	@Test public void testDirectIndicators_sequenced_opt()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create(16,0);
		p.write.writeBeginDirect("arabica");
		p.write.writeEndBeginDirect("jamaica");		
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END_BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};       
	
	
	
	
	
	
	
	
	@Test public void testRegistered()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with registered names. */
		Pair p = create(16,2);
		p.write.writeBeginRegister("arabica",0);
		p.write.writeEndBeginRegister("jamaica",1);		
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_REGISTER);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		try{
			p.read.getSignalNumber(); //if invalidated?
			Assert.fail();
		}catch(IllegalStateException ex){};
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END_BEGIN_REGISTER);
		Assert.assertTrue(p.read.getSignalNumber()==1);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));				
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};
	
	@Test public void testRegistered_use()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with registered names. */
		Pair p = create(16,2);
		p.write.writeBeginRegister("arabica",0);
		p.write.writeEndBeginRegister("jamaica",1);	
			p.write.writeBeginUse(1);
			p.write.writeEndBeginUse(0);	
			p.write.writeEnd();
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_REGISTER);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END_BEGIN_REGISTER);
		Assert.assertTrue(p.read.getSignalNumber()==1);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		
			Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==1);
			Assert.assertTrue(p.read.readIndicator()==TIndicator.END_BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==0);
			Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
				
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testDataSkip()throws IOException
	{
		enter();
		/*	Check if we can skip un-read data and get to signal	*/
		Pair p = create(16,0);
		p.write.writeInt(77);
		p.write.writeChar('c');
		p.write.writeBeginDirect("arabica");
		p.write.writeEnd();
		p.write.flush();
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		
		p.read.skip();
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
			
		leave();
	};	
	@Test public void testDataSkip_Eof()throws IOException
	{
		enter();
		/*	Check if we can skip un-read and will fail if no closing signal.*/
		Pair p = create(16,0);
		p.write.writeInt(77);
		p.write.writeChar('c');
		p.write.flush();
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		try{
			p.read.skip();
			Assert.fail();
			}catch(EUnexpectedEof ex){};
			
		leave();
	};
	
	
	
	
	
	@Test public void testTypesAndFlushes()throws IOException
	{
		enter();
		/*	Check if we can put type and flush indicators.
		
		Note: Theoretically we could just dump a sequence
		of writeType / writeFlush not carrying about proper
		data order. However not every format may be able
		to handle it, so instead do a proper thing. Some may
		also not allow type without flush, so we always do
		a pair.		
		*/
		Pair p = create(16,0);
		p.write.writeType(TIndicator.TYPE_BOOLEAN);
		p.write.writeBoolean(false);
		p.write.writeFlush(TIndicator.FLUSH_BOOLEAN);
		
		p.write.writeType(TIndicator.TYPE_BYTE);
		p.write.writeByte((byte)1);
		p.write.writeFlush(TIndicator.FLUSH_BYTE);
		
		p.write.writeType(TIndicator.TYPE_CHAR);
		p.write.writeChar('c');
		p.write.writeFlush(TIndicator.FLUSH_CHAR);
		
		
		p.write.writeType(TIndicator.TYPE_SHORT);
		p.write.writeShort((short)8888);
		p.write.writeFlush(TIndicator.FLUSH_SHORT);
		
		p.write.writeType(TIndicator.TYPE_INT);
		p.write.writeInt(3444);
		p.write.writeFlush(TIndicator.FLUSH_INT);
		
		p.write.writeType(TIndicator.TYPE_LONG);
		p.write.writeLong(134);
		p.write.writeFlush(TIndicator.FLUSH_LONG);
		
		p.write.writeType(TIndicator.TYPE_FLOAT);
		p.write.writeFloat(1);
		p.write.writeFlush(TIndicator.FLUSH_FLOAT);
		
		p.write.writeType(TIndicator.TYPE_DOUBLE);
		p.write.writeDouble(2);
		p.write.writeFlush(TIndicator.FLUSH_DOUBLE);
		
		p.write.flush();		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BYTE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_CHAR);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_SHORT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_INT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_LONG);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_FLOAT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_DOUBLE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
			
		leave();
	};	
};