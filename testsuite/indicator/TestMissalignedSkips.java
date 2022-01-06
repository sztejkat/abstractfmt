package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.IIndicatorReadFormat;
import sztejkat.abstractfmt.TIndicator;
import sztejkat.abstractfmt.ECorruptedFormat;
import sztejkat.abstractfmt.EBrokenFormat;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Assume;
import java.io.IOException;
/**
	A test which check if an un-describe indicator
	format can correctly skip to the end of data block
	if reads are performed using invalid read methods.
	<p>
	This test class is a bit picky, because the behaviour
	in this
*/
public class TestMissalignedSkips extends AIndicatorTest
{
	@Test public void testMissalignedByteVsLong()throws IOException
	{
		/* 
			Check if a sequence of primitive
			writes in an event can be skipped
			if we miss-read first of them.
			
			We do use byte vs long because
			we may suspect, that fetching long in many formats
			may fetch boolean and interprete it without breaking
			but consume more than expected.
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());		
		p.write.open();			
		p.write.writeBeginDirect("A");
		
			for(int i=0;i<10;i++)
			{
				p.write.writeType(TIndicator.TYPE_BYTE);
				p.write.writeByte((byte)0);
				p.write.writeFlush(TIndicator.FLUSH_BYTE);
			};
			
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.DATA);
		try{
			//Now perform an incorrect read.
			//This read _may_  fail with an exception, but does not have to.
			p.read.readLong();
		}catch(EBrokenFormat ex)
		{
			throw ex;	//broken are NOT allowed.
		}catch(ECorruptedFormat ex)
		{
			System.out.println(ex);
		};
		//Now we still have to be at data.
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.next();
		assertReadIndicator(p.read,TIndicator.END);
		p.read.close();
		leave();
	}
	
	
	
	@Test public void testMissalignedCharVsByteBlock()throws IOException
	{
		/* 
			Check if a sequence of primitive
			writes in an event can be skipped
			if we miss-read first of them.
			
			We do test byte block vs char becase some formats
			may be very alike.
		*/
		enter();
		Pair p = create();
		Assume.assumeFalse(p.write.isDescribed());		
		p.write.open();			
		p.write.writeBeginDirect("A");
		
				p.write.writeType(TIndicator.TYPE_BYTE_BLOCK);
				p.write.writeByteBlock(new byte[64]);
				p.write.writeFlush(TIndicator.FLUSH_BYTE_BLOCK);
			
		p.write.writeEnd();		
		p.write.flush();
		p.write.close();
		
		p.read.open();
		assertReadIndicator(p.read,TIndicator.BEGIN_DIRECT);
		assertGetIndicator(p.read,TIndicator.DATA);
		try{
			//Now perform an incorrect read.
			//This read _may_  fail with an exception, but does not have to.
			p.read.readChar();
		}catch(EBrokenFormat ex)
		{
			throw ex;	//broken are NOT allowed.
		}catch(ECorruptedFormat ex)
		{
			System.out.println(ex);
		};
		//Now we still have to be at data.
		assertGetIndicator(p.read,TIndicator.DATA);
		p.read.next();
		assertReadIndicator(p.read,TIndicator.END);
		p.read.close();
		leave();
	}
		
};