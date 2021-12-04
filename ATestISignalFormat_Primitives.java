package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A set of interoperatibilty tests focused on exchanging primitive
	data.
*/
public abstract class ATestISignalFormat_Primitives extends ATestISignalFormatBase
{
		/* -----------------------------------------------------------------
		
				Just test capability of carrying large subset
				of numbers.
		
		-----------------------------------------------------------------*/
		@org.junit.Test public void testBytes()throws IOException
		{
			enter();
			/*
				In this test we just write all bytes and read them back
			*/			
			Pair p = create();
			try{
			
					for(int i=0;i<255;i++)
					{
							p.write.writeByte((byte)i);
					};
					p.write.flush();
					
					for(int i=0;i<255;i++)
					{
							org.junit.Assert.assertTrue( p.read.readByte()==(byte)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testChars()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create();
			try{
					for(int i=0;i<65535;i++)
					{
							p.write.writeChar((char)i);
					};
					p.write.flush();
					
					for(int i=0;i<65535;i++)
					{
							org.junit.Assert.assertTrue( p.read.readChar()==(char)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testShorts()throws IOException
		{
			enter();
			/*
				In this test we just write all chars
			*/			
			Pair p = create();
			try{
					for(int i=0;i<65535;i++)
					{
							p.write.writeShort((short)i);
					};
					p.write.flush();
					
					for(int i=0;i<65535;i++)
					{
							org.junit.Assert.assertTrue( p.read.readShort()==(short)i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testInts()throws IOException
		{
			enter();
			/*
				In this test we just write some ints
			*/			
			Pair p = create();
			try{
					for(int i=-32767;i<32768;i+=7)
					{
							p.write.writeInt(i + 37*i);
					};
					p.write.flush();
					
					for(int i=-32767;i<32768;i+=7)
					{
							org.junit.Assert.assertTrue( p.read.readInt()==i + 37*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testLongs()throws IOException
		{
			enter();
			/*
				In this test we just write some longs
			*/			
			Pair p = create();
			try{
					for(long i=-32767;i<32768;i+=7)
					{
							p.write.writeLong(i + 37*i + 997*i);
					};
					p.write.flush();
					
					for(long i=-32767;i<32768;i+=7)
					{
							org.junit.Assert.assertTrue( p.read.readLong()==i + 37*i+ 997*i);
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testFloats()throws IOException
		{
			enter();
			/*
				In this test we just write some floats
			*/			
			Pair p = create();
			try{
					for(long i=-32767;i<32768;i+=7)
					{
							p.write.writeFloat((1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.flush();
					
					for(long i=-32767;i<32768;i+=7)
					{
							org.junit.Assert.assertTrue( p.read.readFloat()==(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testDouble()throws IOException
		{
			enter();
			/*
				In this test we just write some doubles
			*/			
			Pair p = create();
			try{
					for(long i=-32767;i<32768;i+=7)
					{
							p.write.writeDouble((double)(1e-2f*(i + 37*i + 997*i)-i));
					};
					p.write.flush();
					
					for(long i=-32767;i<32768;i+=7)
					{
							org.junit.Assert.assertTrue( p.read.readDouble()==(double)(1e-2f*(i + 37*i + 997*i)-i));
					};
			}finally{ p.close(); };
			
			leave();
		};
		
		
		/* -----------------------------------------------------------------
		
				Blocks of data
		
		-----------------------------------------------------------------*/
		@org.junit.Test public void testBooleanBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of boolena data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						boolean [] buff = new boolean[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=((written & 0x01)!=0);
								written++;
							};
						p.write.writeBooleanBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						boolean [] buff = new boolean[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readBooleanBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==((read & 0x01)!=0));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testByteBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						byte [] buff = new byte[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(byte)(written-written*7);
								written++;
							};
						p.write.writeByteBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						byte [] buff = new byte[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readByteBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(byte)(read-read*7));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		@org.junit.Test public void testCharBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						char [] buff = new char[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(char)(written-written*37);
								written++;
							};
						p.write.writeCharBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						char [] buff = new char[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readCharBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(char)(read-read*37));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		@org.junit.Test public void testShortBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						short [] buff = new short[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(short)(written-written*37);
								written++;
							};
						p.write.writeShortBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						short [] buff = new short[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readShortBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(short)(read-read*37));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		@org.junit.Test public void testIntBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						int [] buff = new int[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(written-written*37+written*997);
								written++;
							};
						p.write.writeIntBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						int [] buff = new int[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readIntBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(read-read*37+read*997));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testLongBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						long [] buff = new long[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(written-written*37L+written*997L);
								written++;
							};
						p.write.writeLongBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						long [] buff = new long[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readLongBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(read-read*37L+read*997L));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		@org.junit.Test public void testFloatBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						float [] buff = new float[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(float)(written-written*37L+written*997L);
								written++;
							};
						p.write.writeFloatBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						float [] buff = new float[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readFloatBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(float)(read-read*37L+read*997L));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		@org.junit.Test public void testDoubleBlock()throws IOException
		{
			enter();
			/*
				In this test we just write some blocks of data.
				The scattering of write is different than scattering of reads
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					int written=0;
					for(int sec = 0; sec<16; sec+=9)
					{
						double [] buff = new double[sec];
							for(int i=0;i<sec;i++)
							{
								buff[i]=(double)(written-written*37L+written*997L);
								written++;
							};
						p.write.writeDoubleBlock(buff);
					};
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					int read = 0;
					while(read<written)
					{
						//a transfer size
						final int L = 32;
						//buffer, larger to test varying offset
						final int o = read & 0x7;
						double [] buff = new double[L+o];
						//how much we are expected to read?
						final int t = L < (written-read) ? L : written-read;
						int r = p.read.readDoubleBlock(buff,o,L);
						org.junit.Assert.assertTrue( r == t );
						for(int i=0;i<t;i++)
						{
							org.junit.Assert.assertTrue(buff[i+o]==(double)(read-read*37L+read*997L));
							read++;
						};
					};
					org.junit.Assert.assertTrue(p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};
		
		
		/* -----------------------------------------------------------------
		
				Mixtures of data.
		
		-----------------------------------------------------------------*/
		@org.junit.Test public void testMixture()throws IOException
		{
			enter();
			/*
				In this test we just write mixture of primitives
			*/			
			Pair p = create();
			try{
					p.write.begin("block");
					p.write.writeInt(-3403490);
					p.write.writeLong(-88904903444L);
					final byte [] Y = new byte[267];
					for(int i=0;i<Y.length;i++){ Y[i]=(byte)(i ^0x87); };
					p.write.writeByteBlock(Y,1,100);
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue("block".equals(p.read.next()));
					org.junit.Assert.assertTrue(p.read.readInt()==-3403490);
					org.junit.Assert.assertTrue(p.read.readLong()==-88904903444L);
					final byte [] X = new byte[100];
					org.junit.Assert.assertTrue(p.read.readByteBlock(X,0,100)==100);
					for(int i=0;i<100;i++){ org.junit.Assert.assertTrue(X[i]==Y[i+1]); };
					org.junit.Assert.assertTrue(p.read.next()==null);
					
					try{
						p.read.next();
						org.junit.Assert.fail();
					}catch(EUnexpectedEof ex){};
					
				}finally{ p.close(); };
			
			leave();
		};
};