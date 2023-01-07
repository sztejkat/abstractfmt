package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case which check how stream behaves on read-type abuse.
	<p>
	Specifically this test case assumes that implementation is 
	not typed and that it does not defend by <u>absolutely any means</u>
	against incorrect type reads. Thous it is ideal for binary formats,
	but may not be best suited for text formats.
	<p>
	To not restrict the use for text format this test case validates
	only abuse across <code>byte,short,int,long</code> and their sequence 
	variants.
*/
public class ATestCase_AbuseOfReads_IntNumeric extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{	
	/**
		Check how byte abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveByteReadRecovery()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using byte, abusive. Abusive ops are allowed to throw.
				try{
					r.readByte();
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how byte abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveByteReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using byte till the boundary, abusive. Abusive ops are allowed to throw.
				try{
					for(int i=0;i<100;i++)
					{
						r.readByte();
					};
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how byte abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveByteArrayReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using byte till the boundary, abusive. Abusive ops are allowed to throw.
				try{
						r.readByteBlock(new byte[1024]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	
	
	
	
	
	
	
	
	/**
		Check how short abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveShortReadRecovery()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using short, abusive. Abusive ops are allowed to throw.
				try{
					r.readShort();
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how short abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveShortReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using short, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					for(int i=0;i<100;i++)
					{
						r.readShort();
					};
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how short abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveShortBlockReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using short, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					r.readShortBlock(new short[100]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how short abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveShortBlockReadRecovery2_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeByte((byte)33);	//intentionally number which should trigger boundary in sequence problems.
				w.writeByte((byte)33);
				w.writeByte((byte)33);
				w.writeByte((byte)33);
				w.writeByte((byte)33);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using short, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					r.readShortBlock(new short[100]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	
	
	
	
	
	
	
	/**
		Check how long abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveLongReadRecovery()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using long, abusive. Abusive ops are allowed to throw.
				try{
					r.readLong();
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how long abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveLongReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(43);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using long, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					for(int i=0;i<100;i++)
					{
						r.readLong();
					};
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how long abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveLongBlockReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeInt(33);
				w.writeInt(44);
				w.writeInt(35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using long, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					r.readLongBlock(new long[100]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how long abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveLongBlockReadRecovery2_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeByte((byte)33);	//intentionally number which should trigger boundary in sequence problems.
				w.writeByte((byte)33);
				w.writeByte((byte)33);
				w.writeByte((byte)33);
				w.writeByte((byte)33);
				w.writeInt(344);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using long, abusive, till boundary. Abusive ops are allowed to throw.
				try{
					r.readLongBlock(new long[100]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	
	
	
	
	
	
	
	
	
	/**
		Check how int abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveIntReadRecovery()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeLong(33);
				w.writeLong(33);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using int, abusive. Abusive ops are allowed to throw.
				try{
					r.readInt();
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how int abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveIntReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeLong(33);
				w.writeShort((short)44);
				w.writeByte((byte)35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using int till the boundary, abusive. Abusive ops are allowed to throw.
				try{
					for(int i=0;i<100;i++)
					{
						r.readInt();
					};
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	/**
		Check how int abuses reading of other types and if we recover
		after getting signal.
	@throws IOException .
	*/
	@Test public void testAbusiveIntArrayReadRecovery_boundary()throws IOException
	{
			enter();
			CPair<?,?> p = createTestDevice();
			final IStructWriteFormat w= p.writer;
			final IStructReadFormat  r= p.reader;
			w.open();
			w.begin("larkis");
				w.writeLong(33);
				w.writeShort((short)44);
				w.writeByte((byte)35);
			w.end();
				w.writeInt(0x3F499900);
			w.close();
			
			r.open();
			Assert.assertTrue("larkis".equals(r.next()));
			
				//Read using int till the boundary, abusive. Abusive ops are allowed to throw.
				try{
						r.readIntBlock(new int[1024]);
				}catch(IOException ex){ System.out.println(ex); };
			
				//make an attempt to revover 
			Assert.assertTrue(null==r.next());
			//Validate if recovered fine.
			Assert.assertTrue(r.readInt()==0x3F499900);
			
			r.close();
			leave();
	};
	
	
	
	
	
};



