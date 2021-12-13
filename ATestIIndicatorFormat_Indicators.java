package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
/**
		A test framework focused on indicator related operations
*/
public abstract class ATestIIndicatorFormat_Indicators extends ATestIIndicatorFormatBase
{

	@Test public void testEmpty()throws IOException
	{
		enter();
		/*	Check if empty gives eof.*/
		Pair p = create();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		leave();
	};
	
	
	
	@Test public void testDirectIndicators_basic()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create();
		p.write.writeBeginDirect("arabica");
		p.write.writeBeginDirect("jamaica");
		p.write.writeEnd();
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));	//valid till different indicator.
		try{
			p.read.getSignalNumber();	//<-- never should be valid.
			Assert.fail();
		}catch(IllegalStateException ex){};
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		try{
			p.read.getSignalName();	//<-- should be invalidated by end.
			Assert.fail();
		}catch(IllegalStateException ex){};
		Assert.assertTrue(p.read.readIndicator()==TIndicator.END);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.EOF); //intentionally double
		leave();
	};
	
	@Test public void testDirectIndicators_sequenced()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create();
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
		Pair p = create();
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
		Pair p = create();
		p.write.writeBeginRegister("arabica",0);
		p.write.writeEndBeginRegister("jamaica",1);		
		p.write.writeEnd();
		p.write.flush();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.BEGIN_REGISTER);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		p.read.getSignalNumber(); //if can do twice.			
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
		Pair p = create();
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
		Pair p = create();
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
		Pair p = create();
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
	
	
	
	
	private void writeTypesAndFlushes(IIndicatorWriteFormat w)throws IOException
	{
		w.writeType(TIndicator.TYPE_BOOLEAN);
		w.writeBoolean(false);
		w.writeFlush(TIndicator.FLUSH_BOOLEAN);
		
		w.writeType(TIndicator.TYPE_BYTE);
		w.writeByte((byte)1);
		w.writeFlush(TIndicator.FLUSH_BYTE);
		
		w.writeType(TIndicator.TYPE_CHAR);
		w.writeChar('c');
		w.writeFlush(TIndicator.FLUSH_CHAR);		
		
		w.writeType(TIndicator.TYPE_SHORT);
		w.writeShort((short)8888);
		w.writeFlush(TIndicator.FLUSH_SHORT);
		
		w.writeType(TIndicator.TYPE_INT);
		w.writeInt(34445544);
		w.writeFlush(TIndicator.FLUSH_INT);
		
		w.writeType(TIndicator.TYPE_LONG);
		w.writeLong(135245525252454L);
		w.writeFlush(TIndicator.FLUSH_LONG);
		
		w.writeType(TIndicator.TYPE_FLOAT);
		w.writeFloat(1.33f);
		w.writeFlush(TIndicator.FLUSH_FLOAT);
		
		w.writeType(TIndicator.TYPE_DOUBLE);
		w.writeDouble(-2.4090459E3);
		w.writeFlush(TIndicator.FLUSH_DOUBLE);
	};
	@Test public void testTypesAndFlushes_Df()throws IOException
	{
		enter();
		/*	Check if we can put type and flush indicators and skip content	*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be flushing",p.write.isFlushing());		
		writeTypesAndFlushes(p.write);
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
	
	
	@Test public void testTypesAndFlushes_Dnf()throws IOException
	{
		enter();
		/*	Check if we can put type and flush indicators and skip content	*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be not flushing",!p.write.isFlushing());		
		writeTypesAndFlushes(p.write);
		p.write.flush();		
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_BYTE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_CHAR);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_SHORT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_INT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_LONG);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_FLOAT);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.readIndicator()==TIndicator.TYPE_DOUBLE);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.skip();
					
		leave();
	};	
	
	@Test public void testTypesAndFlushesGetNext_Dnf()throws IOException
	{
		enter();
		/*	Check if we can put type and flush indicators and skip content,
		but this time using get/next paradign	*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be not flushing",!p.write.isFlushing());		
		writeTypesAndFlushes(p.write);
		p.write.flush();		
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_BOOLEAN);
		p.read.next();
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		Assert.assertTrue(p.read.readIndicator()==TIndicator.DATA);
		p.read.next();
				
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_BYTE);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_CHAR);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_SHORT);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.skip();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_INT);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_LONG);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_FLOAT);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
		
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_DOUBLE);
		p.read.next();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.DATA);
		p.read.next();
					
		leave();
	};	
};