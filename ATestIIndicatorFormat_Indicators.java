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
		p.write.open();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.EOF);
		leave();
	};
	
	
	
	@Test public void testDirectIndicators_basic()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("arabica");
		p.write.writeBeginDirect("jamaica");
		p.write.writeEnd();
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));	//valid till different indicator.
		
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));
		expect(p.read.readIndicator(),TIndicator.END);
		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		expect(p.read.readIndicator(),TIndicator.EOF); //intentionally double
		p.read.close();
		leave();
	};
	
	@Test public void testDirectIndicators_sequenced()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("arabica");
		p.write.writeEnd();
		p.write.writeBeginDirect("jamaica");		
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		expect(p.read.readIndicator(),TIndicator.END);		
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testDirectIndicators_sequenced_opt()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with direct names.	*/
		Pair p = create();
		p.write.open();
		p.write.writeBeginDirect("arabica");
		p.write.writeEndBeginDirect("jamaica");		
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		expect(p.read.readIndicator(),TIndicator.END_BEGIN_DIRECT);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};       
	
	
	
	
	
	
	
	
	@Test public void testRegistered()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with registered names. */
		Pair p = create();
		Assume.assumeTrue(p.write.getMaxRegistrations()>=2);
		p.write.open();
		p.write.writeBeginRegister("arabica",0);
		p.write.writeEndBeginRegister("jamaica",1);		
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_REGISTER);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		p.read.getSignalNumber(); //if can do twice.			
		expect(p.read.readIndicator(),TIndicator.END_BEGIN_REGISTER);
		Assert.assertTrue(p.read.getSignalNumber()==1);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));				
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	@Test public void testRegistered_use()throws IOException
	{
		enter();
		/*	Check basic begin-end sequence with registered names. */
		Pair p = create();
		Assume.assumeTrue(p.write.getMaxRegistrations()>=2);
		p.write.open();
		p.write.writeBeginRegister("arabica",0);
		p.write.writeEndBeginRegister("jamaica",1);	
			p.write.writeBeginUse(1);
			p.write.writeEndBeginUse(0);	
			p.write.writeEnd();
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.BEGIN_REGISTER);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		Assert.assertTrue(p.read.getSignalNumber()==0);
		expect(p.read.readIndicator(),TIndicator.END_BEGIN_REGISTER);
		Assert.assertTrue(p.read.getSignalNumber()==1);
		Assert.assertTrue("jamaica".equals(p.read.getSignalName()));		
		
			expect(p.read.readIndicator(),TIndicator.BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==1);
			expect(p.read.readIndicator(),TIndicator.END_BEGIN_USE);
			Assert.assertTrue(p.read.getSignalNumber()==0);
			expect(p.read.readIndicator(),TIndicator.END);
				
		expect(p.read.readIndicator(),TIndicator.END);
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testDataSkip()throws IOException
	{
		enter();
		/*	Check if we can skip un-read data and get to signal	*/
		Pair p = create();
		
		Assume.assumeTrue(p.read.isDescribed()==false);
		Assume.assumeTrue(p.read.isFlushing()==false);		
		
		p.write.open();
		p.write.writeType(TIndicator.TYPE_INT);
		p.write.writeInt(77);
		p.write.writeFlush(TIndicator.FLUSH_INT);
		p.write.writeType(TIndicator.TYPE_CHAR);
		p.write.writeChar('c');
		p.write.writeFlush(TIndicator.FLUSH_CHAR);
		p.write.writeBeginDirect("arabica");
		p.write.writeEnd();
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.DATA);
		expect(p.read.readIndicator(),TIndicator.DATA);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		expect(p.read.readIndicator(),TIndicator.BEGIN_DIRECT);
		Assert.assertTrue("arabica".equals(p.read.getSignalName()));
		expect(p.read.readIndicator(),TIndicator.END);		
		expect(p.read.readIndicator(),TIndicator.EOF);
		p.read.close();
		leave();
	};	
	@Test public void testDataSkip_Eof()throws IOException
	{
		enter();
		/*	Check if we can skip and will fail if no closing signal.*/
		Pair p = create();
		
		Assume.assumeTrue(p.read.isDescribed()==false);
		Assume.assumeTrue(p.read.isFlushing()==false);
		
		p.write.open();
		p.write.writeType(TIndicator.TYPE_INT);
		p.write.writeInt(77);
		p.write.writeFlush(TIndicator.FLUSH_INT);
		p.write.writeType(TIndicator.TYPE_CHAR);
		p.write.writeChar('c');
		p.write.writeFlush(TIndicator.FLUSH_CHAR);
		p.write.flush();
		p.write.close();
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.DATA);
		expect(p.read.readIndicator(),TIndicator.DATA);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Now if stream was NOT flushing we have some data to skip, because
		//we did not read indicator. If stream WAS fli
		try{
				p.read.skip();
				Assert.fail();
				}catch(EUnexpectedEof ex){};
		p.read.close();
		leave();
	};
	
	
	
	
	private void writeTypesAndFlushes(IIndicatorWriteFormat w)throws IOException
	{
		w.open();
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
		w.flush();
		w.close();
	};
	@Test public void testTypesAndFlushes_Df()throws IOException
	{
		enter();
		/*	Check if we can put type and flush indicators and skip content	*/
		Pair p = create();
		Assume.assumeTrue("must be described",p.write.isDescribed());
		Assume.assumeTrue("must be flushing",p.write.isFlushing());		
		writeTypesAndFlushes(p.write);
		
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_BYTE);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_CHAR);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		
		expect(p.read.readIndicator(),TIndicator.TYPE_SHORT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_INT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_LONG);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		
		expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		Assert.assertTrue((p.read.readIndicator().FLAGS & TIndicator.FLUSH)!=0); //because any flush may be read.
		p.read.close();
			
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
			
		p.read.open();
		expect(p.read.readIndicator(),TIndicator.TYPE_BOOLEAN);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_BYTE);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_CHAR);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_SHORT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_INT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_LONG);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_FLOAT);
		expect(p.read.readIndicator(),TIndicator.DATA);
		p.read.skip();
		
		expect(p.read.readIndicator(),TIndicator.TYPE_DOUBLE);
		expect(p.read.readIndicator(),TIndicator.DATA);
		//Now if stream is described but not flushing there is no 
		//indicator after data, so skipping data SHOULD fail.
		try{
				p.read.skip();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
		p.read.close();
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
		
		p.read.open();
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_BOOLEAN);
		Assert.assertTrue(p.read.getIndicator()==TIndicator.TYPE_BOOLEAN);
		p.read.next();
		expect(p.read.readIndicator(),TIndicator.DATA);
		expect(p.read.readIndicator(),TIndicator.DATA);
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
		//Now if stream is described but not flushing there is no 
		//indicator after data, so going to next() indicator SHOULD fail.
		try{
				p.read.next();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
		p.read.close();
		leave();
	};	
};