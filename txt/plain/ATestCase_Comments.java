package sztejkat.abstractfmt.txt.plain;
import sztejkat.abstractfmt.test.*;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case checking how class deals with comments.
*/
public class ATestCase_Comments extends AInterOpTestCase<IStructReadFormat,CPlainTxtWriteFormat>
{	

	@SuppressWarnings("unchecked")
	@Test public void testWithComment()throws IOException
	{
			enter();
			CPair<?,CPlainTxtWriteFormat> p = createTestDevice();
			final CPlainTxtWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
					w.writeComment("Comment");
				w.writeInt(44);
					w.writeComment("\nComment\n\n of some length");
				w.writeInt(35);
					w.writeComment("Comment");
			w.end();
				w.writeComment("Comment");
				w.writeInt(0x3F499900);
				w.writeComment("Comment");
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				Assert.assertTrue(33==r.readInt());
				Assert.assertTrue(44==r.readInt());
				//make an attempt to skip 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
};