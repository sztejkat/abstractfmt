package sztejkat.abstractfmt;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
/**
	A set of interoperatibilty tests focused on exchanging primitive
	data. Tests in this class are agnostic to whether streams
	are described or not.
*/
public abstract class ATestISignalFormat_Primitives extends ATestISignalFormatBase
{
		/* ********************************************************************
		
		
				"Positive" tests which check how stream reacts
				when all data are present and in correct place, type
				and amount.
		
		
		
		*********************************************************************/
		/* -----------------------------------------------------------------
		
				Just elementary tests.				
		
		-----------------------------------------------------------------*/
		@Test  public void testByte_1()throws IOException
		{
			enter();
			/*
				In this test we just write single byte and read it back
			*/			
			Pair p = create();
			try{
				p.write.open();
				p.write.writeByte((byte)-45);
				p.write.close();
							
				p.read.open();
				Assert.assertTrue(p.read.readByte()==(byte)-45);
				}finally{ p.close(); };
		};
		/* -----------------------------------------------------------------
		
		
				Test capability of carrying large subset
				of possible numbers through elementary primitives.
				
		
		-----------------------------------------------------------------*/
		@Test public void testBytes()throws IOException
		{
			enter();
			/*
				In this test we just write all bytes and read them back
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(int i=-100;i<100;i+=10)
					{
							p.write.writeByte((byte)(i));
					};
					p.write.close();
					p.read.open();			
					for(int i=-100;i<100;i+=10)
					{
							Assert.assertTrue( p.read.readByte()==(byte)(i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test public void testChars()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(int i=0;i<65536;i+=37)
					{
							p.write.writeChar((char)i);
					};
					p.write.close();
					p.read.open();					
					for(int i=0;i<65536;i+=37)
					{
							Assert.assertTrue( p.read.readChar()==(char)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		@Test public void testShorts()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(int i=-32767;i<32768;i+=97)
					{
							p.write.writeShort((short)i);
					};
					p.write.close();
					p.read.open();					
					for(int i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readShort()==(short)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test public void testInts()throws IOException
		{
			enter();
			/*
				In this test we just write some ints
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(int i=-32767;i<32768;i+=97)
					{
							p.write.writeInt(i + 37*i);
					};
					p.write.close();
					p.read.open();					
					for(int i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readInt()==i + 37*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test  public void testLongs()throws IOException
		{
			enter();
			/*
				In this test we just write some longs
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeLong(i + 37*i + 997*i);
					};
					p.write.close();	
					p.read.open();				
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readLong()==i + 37*i+ 997*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@Test   public void testFloats()throws IOException
		{
			enter();
			/*
				In this test we just write some floats
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeFloat((1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.close();	
					p.read.open();				
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readFloat()==(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testDouble()throws IOException
		{
			enter();
			/*
				In this test we just write some doubles
			*/			
			Pair p = create();
			try{
					p.write.open();
					for(long i=-32767;i<32768;i+=97)
					{
							p.write.writeDouble((double)(1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.flush();
					p.read.open();
					for(long i=-32767;i<32768;i+=97)
					{
							Assert.assertTrue( p.read.readDouble()==(double)(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		
		/* -----------------------------------------------------------------
		
		
		
			Signals, as necessary to ensure block data reads.
				
				
		
		-----------------------------------------------------------------*/
		@Test public void testBeginEnd()throws IOException
		{
			enter();
			/*
				In this test we write a signal and test 
				if it is readable.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.begin("pershing");
					p.write.end();
					p.write.close();
				
					p.read.open();
				System.out.println("fetching begin...");	
					Assert.assertTrue("pershing".equals(p.read.next()));
				System.out.println("fetching end...");
					Assert.assertTrue(null==p.read.next());
				System.out.println("polling for eof...");
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		
		
		
		/* -----------------------------------------------------------------
		
		
		
				Blocks of data
				
				
		
		-----------------------------------------------------------------*/
		
		
		/* ................................................................
					Boolean
		................................................................*/
		private static boolean [] prepareBooleanBlock(int size)
		{	
			boolean [] a = new boolean[size];
			for(int i=0;i<size;i++){ a[i] = ((i &0x01)!=0); }
			return a;
		};
		private void testBooleanBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					boolean [] blk = prepareBooleanBlock(woffset+wlength);
					p.write.writeBooleanBlock(blk,woffset,wL);
					p.write.writeBooleanBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					boolean [] res = new boolean[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readBooleanBlock(res, roffset, rL);
						r += p.read.readBooleanBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.readBooleanBlock(res,0,1)==0);	//subseqent read shuld be stuck.
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_BOOLEAN_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test public void testBooleanBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testBooleanBlock(0,0,16,16);
				testBooleanBlock(0,5,16,16);
				testBooleanBlock(5,0,16,16);
			leave();
		};
		@Test public void testBooleanBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testBooleanBlock(0,0,16,19);
				testBooleanBlock(0,5,16,19);
				testBooleanBlock(5,0,16,19);
			leave();
		};
		@Test public void testBooleanBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testBooleanBlock(0,0,16,10);
				testBooleanBlock(0,5,16,10);
				testBooleanBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Byte
		................................................................*/
		private static byte [] prepareByteBlock(int size)
		{	
			byte [] a = new byte[size];
			for(int i=0;i<size;i++){ a[i] = (byte)(i*31-i); }
			return a;
		};
		private void testByteBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					byte [] blk = prepareByteBlock(woffset+wlength);
					p.write.writeByteBlock(blk,woffset,wL);
					p.write.writeByteBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					byte [] res = new byte[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readByteBlock(res, roffset, rL);
						r += p.read.readByteBlock(res, roffset+rL, rH);						
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.readByteBlock(res,0,1)==0);	//subseqent read shuld be stuck.
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test public void testByteBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testByteBlock(0,0,16,16);
				testByteBlock(0,5,16,16);
				testByteBlock(5,0,16,16);
			leave();
		};
		@Test public void testByteBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testByteBlock(0,0,16,19);
				testByteBlock(0,5,16,19);
				testByteBlock(5,0,16,19);
			leave();
		};
		@Test public void testByteBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testByteBlock(0,0,16,10);
				testByteBlock(0,5,16,10);
				testByteBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Byte, single element
		................................................................*/
		private void testByteBlockS(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					byte [] blk = prepareByteBlock(woffset+wlength);
					for(int i=0;i<wlength;i++)
						p.write.writeByteBlock(blk[woffset+i]);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					byte [] res = new byte[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r=0;
					for( int i=0;i<rlength;i++)
					{
						int x = p.read.readByteBlock();
						if (x==-1) break;
						res[roffset+i]=(byte)x;
						r++;
					};
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_BYTE_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testByteBlockS_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testByteBlockS(0,0,16,16);
			leave();
		};
		@Test  public void testByteBlockS_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testByteBlockS(0,0,16,19);
			leave();
		};
		@Test  public void testByteBlockS_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testByteBlockS(0,0,16,10);
			leave();
		};
		
		
		
		
		
		
		/* ................................................................
					Char
		................................................................*/
		private static char [] prepareCharBlock(int size)
		{	
			char [] a = new char[size];
			for(int i=0;i<size;i++){ a[i] = (char)(i*1940); }
			return a;
		};
		private void testCharBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					char [] blk = prepareCharBlock(woffset+wlength);
					p.write.writeCharBlock(blk,woffset,wL);
					p.write.writeCharBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					char [] res = new char[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readCharBlock(res, roffset, rL);
						r += p.read.readCharBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_CHAR_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testCharBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testCharBlock(0,0,16,16);
				testCharBlock(0,5,16,16);
				testCharBlock(5,0,16,16);
			leave();
		};
		@Test  public void testCharBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testCharBlock(0,0,16,19);
				testCharBlock(0,5,16,19);
				testCharBlock(5,0,16,19);
			leave();
		};
		@Test  public void testCharBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testCharBlock(0,0,16,10);
				testCharBlock(0,5,16,10);
				testCharBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Short
		................................................................*/
		private static short [] prepareShortBlock(int size)
		{	
			short [] a = new short[size];
			for(int i=0;i<size;i++){ a[i] = (short)(i ^ 0x55AA); }
			return a;
		};
		private void testShortBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					short [] blk = prepareShortBlock(woffset+wlength);
					p.write.writeShortBlock(blk,woffset,wL);
					p.write.writeShortBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					short [] res = new short[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readShortBlock(res, roffset, rL);
						r += p.read.readShortBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_SHORT_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testShortBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testShortBlock(0,0,16,16);
				testShortBlock(0,5,16,16);
				testShortBlock(5,0,16,16);
			leave();
		};
		@Test  public void testShortBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testShortBlock(0,0,16,19);
				testShortBlock(0,5,16,19);
				testShortBlock(5,0,16,19);
			leave();
		};
		@Test  public void testShortBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testShortBlock(0,0,16,10);
				testShortBlock(0,5,16,10);
				testShortBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Int
		................................................................*/
		private static int [] prepareIntBlock(int size)
		{	
			int [] a = new int[size];
			for(int i=0;i<size;i++){ a[i] = i*102; }
			return a;
		};
		private void testIntBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					int [] blk = prepareIntBlock(woffset+wlength);
					p.write.writeIntBlock(blk,woffset,wL);
					p.write.writeIntBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					int [] res = new int[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readIntBlock(res, roffset, rL);
						r += p.read.readIntBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_INT_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testIntBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testIntBlock(0,0,16,16);
				testIntBlock(0,5,16,16);
				testIntBlock(5,0,16,16);
			leave();
		};
		@Test  public void testIntBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testIntBlock(0,0,16,19);
				testIntBlock(0,5,16,19);
				testIntBlock(5,0,16,19);
			leave();
		};
		@Test  public void testIntBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testIntBlock(0,0,16,10);
				testIntBlock(0,5,16,10);
				testIntBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Long
		................................................................*/
		private static long [] prepareLongBlock(int size)
		{	
			long [] a = new long[size];
			for(int i=0;i<size;i++){ a[i] = i*37 + (i-1)*81; }
			return a;
		};
		private void testLongBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					long [] blk = prepareLongBlock(woffset+wlength);
					p.write.writeLongBlock(blk,woffset,wL);
					p.write.writeLongBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					long [] res = new long[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readLongBlock(res, roffset, rL);
						r += p.read.readLongBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_LONG_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testLongBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testLongBlock(0,0,16,16);
				testLongBlock(0,5,16,16);
				testLongBlock(5,0,16,16);
			leave();
		};
		@Test  public void testLongBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testLongBlock(0,0,16,19);
				testLongBlock(0,5,16,19);
				testLongBlock(5,0,16,19);
			leave();
		};
		@Test  public void testLongBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testLongBlock(0,0,16,10);
				testLongBlock(0,5,16,10);
				testLongBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Float
		................................................................*/
		private static float [] prepareFloatBlock(int size)
		{	
			float [] a = new float[size];
			for(int i=0;i<size;i++){ a[i] = i*999f; }
			return a;
		};
		private void testFloatBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					float [] blk = prepareFloatBlock(woffset+wlength);
					p.write.writeFloatBlock(blk,woffset,wL);
					p.write.writeFloatBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					float [] res = new float[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readFloatBlock(res, roffset, rL);
						r += p.read.readFloatBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_FLOAT_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testFloatBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testFloatBlock(0,0,16,16);
				testFloatBlock(0,5,16,16);
				testFloatBlock(5,0,16,16);
			leave();
		};
		@Test  public void testFloatBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testFloatBlock(0,0,16,19);
				testFloatBlock(0,5,16,19);
				testFloatBlock(5,0,16,19);
			leave();
		};
		@Test  public void testFloatBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testFloatBlock(0,0,16,10);
				testFloatBlock(0,5,16,10);
				testFloatBlock(5,0,16,10);
			leave();
		};
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ................................................................
					Double
		................................................................*/
		private static double [] prepareDoubleBlock(int size)
		{	
			double [] a = new double[size];
			for(int i=0;i<size;i++){ a[i] = 47.5*(i ^ 0x55AA44); }
			return a;
		};
		private void testDoubleBlock(int woffset, int roffset, int wlength, int rlength)throws IOException
		{
			enter();
			
			/*
				In this test we just write some blocks of data using different offset
				in each pair and different cross-block scatter and we perform a complete
				read or partial or incomplete.
			*/	
			Pair p = create();
			try{
				int wL = wlength/2;
				int wH = wlength - wL;
				
				int rL = wlength/3;	//<-- intentionally wlength
				int rH = rlength - rL;
				
					p.write.open();
					p.write.begin("block");
					System.out.println("write??Block(..,"+woffset+","+wlength+")");
					double [] blk = prepareDoubleBlock(woffset+wlength);
					p.write.writeDoubleBlock(blk,woffset,wL);
					p.write.writeDoubleBlock(blk,woffset+wL,wH);
					p.write.end();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue("block".equals(p.read.next()));
					double [] res = new double[rlength+roffset];
					System.out.println("read??Block(..,"+roffset+","+rlength+")");
					int r = p.read.readDoubleBlock(res, roffset, rL);
						r += p.read.readDoubleBlock(res, roffset+rL, rH);
					System.out.println("r="+r);
					Assert.assertTrue(r==Math.min(wlength,rlength)); //<-- partial or incomplete
					for(int i=0;i<r;i++)
					{
							Assert.assertTrue(blk[i+woffset]==res[i+roffset]);
					};
					if (wlength<=rlength)
					{
						Assert.assertTrue(p.read.whatNext()==TContentType.SIGNAL);
					}else
					{
						Assert.assertTrue(
									p.read.isDescribed() ?
										(p.read.whatNext()==TContentType.PRMTV_DOUBLE_BLOCK)
										:
										(p.read.whatNext()==TContentType.PRMTV_UNTYPED)
									);
					};
					Assert.assertTrue(p.read.next()==null);
					Assert.assertTrue(p.read.whatNext()==TContentType.EOF);
			}finally{ p.close(); };
			leave();
		};
		@Test  public void testDoubleBlock_full()throws IOException
		{
			enter();
			/* Test complete reads */
				testDoubleBlock(0,0,16,16);
				testDoubleBlock(0,5,16,16);
				testDoubleBlock(5,0,16,16);
			leave();
		};
		@Test  public void testDoubleBlock_partial()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testDoubleBlock(0,0,16,19);
				testDoubleBlock(0,5,16,19);
				testDoubleBlock(5,0,16,19);
			leave();
		};
		@Test  public void testDoubleBlock_incomplete()throws IOException
		{
			enter();
			/* Test partial reads over the end of block*/
				testDoubleBlock(0,0,16,10);
				testDoubleBlock(0,5,16,10);
				testDoubleBlock(5,0,16,10);
			leave();
		};
		/* ********************************************************************
		
		
		
		
				Negative tests, checking how stream behaves when
				there is no data.
				
				
				
		
		*********************************************************************/
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in stream at all
				to initate operation (EUnexpectedEof)
		
		------------------------------------------------------------------*/		
		@Test  public void testUEof_Bool()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeBoolean(false);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readBoolean()==false);
					try{
							p.read.readBoolean();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Byte()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeByte((byte)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readByte()==(byte)34);
					try{
							p.read.readByte();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test   public void testUEof_Char()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeChar((char)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readChar()==(char)34);
					try{
							p.read.readChar();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Shrt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeShort((short)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readShort()==(short)34);
					try{
							p.read.readShort();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Int()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeInt(34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readInt()==34);
					try{
							p.read.readInt();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Lng()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeLong((long)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readLong()==(long)34);
					try{
							p.read.readLong();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Flt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeFloat((float)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readFloat()==(float)34);
					try{
							p.read.readFloat();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@Test  public void testUEof_Dbl()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.writeDouble((double)34);
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.readDouble()==(double)34);
					try{
							p.read.readDouble();
							Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in event
				to initate operation (ENoMoreData)
		
		------------------------------------------------------------------*/
		@Test public void testENoMoreData_elemntry()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBoolean();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByte();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readChar();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readInt();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLong();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloat();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDouble();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
		
		@Test public void testENoMoreData_block()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create();
			try{
					p.write.open();
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					p.write.close();
					
					p.read.open();
					Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBooleanBlock(new boolean[3],0,1);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock();
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock(new byte[3],0,1);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new char[3],0,1);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new StringBuilder(),3);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readIntBlock(new int[3],0,1);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLongBlock(new long[5],1,3);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloatBlock(new float[4],0,2);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDoubleBlock(new double[5],0,4);
							Assert.fail();
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
};