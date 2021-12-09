package sztejkat.abstractfmt;
import java.io.IOException;
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
		
		
				Test capability of carrying large subset
				of possible numbers throug elementary primitives.
				
		
		-----------------------------------------------------------------*/
		@org.junit.Test public void testBytes()throws IOException
		{
			enter();
			/*
				In this test we just write all bytes and read them back
			*/			
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
		
		
		
			Signals, as necessary to ensure block data reads.
				
				
		
		-----------------------------------------------------------------*/
		@org.junit.Test public void testBeginEnd()throws IOException
		{
			enter();
			/*
				In this test we write a signal and test 
				if it is readable.
			*/			
			Pair p = create(8,8);
			try{
					p.write.begin("pershing");
					p.write.end();
					p.write.flush();
					
					
					org.junit.Assert.assertTrue("pershing".equals(p.read.next()));
					org.junit.Assert.assertTrue(null==p.read.next());
					
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
			Pair p = create(8,8);
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/* ********************************************************************
		
		
		
		
				Negative tests, checking how stream behaves when
				there is no data.
				
				
				
		
		*********************************************************************/
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in stream at all
				to initate operation (EUnexpectedEof)
		
		------------------------------------------------------------------*/		
		@org.junit.Test public void testUEof_Bool()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeBoolean(false);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readBoolean()==false);
					try{
							p.read.readBoolean();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Byte()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeByte((byte)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readByte()==(byte)34);
					try{
							p.read.readByte();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Char()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeChar((char)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readChar()==(char)34);
					try{
							p.read.readChar();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Shrt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeShort((short)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readShort()==(short)34);
					try{
							p.read.readShort();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Int()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeInt(34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readInt()==34);
					try{
							p.read.readInt();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Lng()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeLong((long)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readLong()==(long)34);
					try{
							p.read.readLong();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Flt()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeFloat((float)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readFloat()==(float)34);
					try{
							p.read.readFloat();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		@org.junit.Test public void testUEof_Dbl()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
			*/			
			Pair p = create(8,8);
			try{
					p.write.writeDouble((double)34);
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.readDouble()==(double)34);
					try{
							p.read.readDouble();
							org.junit.Assert.fail("");
					}catch(EUnexpectedEof ex){ System.out.println(ex); };
			}finally{ p.close(); };
			
			leave();
		};
		/* ------------------------------------------------------------------
		
				If there is no elementary primitive data in event
				to initate operation (ENoMoreData)
		
		------------------------------------------------------------------*/
		@org.junit.Test public void testENoMoreData_elemntry()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create(8,8);
			try{
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBoolean();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByte();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readChar();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readInt();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLong();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloat();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDouble();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					org.junit.Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
		
		@org.junit.Test public void testENoMoreData_block()throws IOException
		{
			enter();
			/*
				In this test we just check if we read past end of stream.
				We do run single test to check if all possible subsequent
				attempts do fail and if signal is then read correctly.
			*/			
			Pair p = create(8,8);
			try{
					p.write.begin("paris");
					p.write.end();
					p.write.flush();
					
					org.junit.Assert.assertTrue( p.read.next()!=null);
					try{
							p.read.readBooleanBlock(new boolean[3],0,1);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock();
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readByteBlock(new byte[3],0,1);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new char[3],0,1);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readCharBlock(new StringBuilder(),3);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readIntBlock(new int[3],0,1);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readLongBlock(new long[5],1,3);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readFloatBlock(new float[4],0,2);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					try{
							p.read.readDoubleBlock(new double[5],0,4);
							org.junit.Assert.fail("");
					}catch(ENoMoreData ex){ System.out.println(ex); };
					
					org.junit.Assert.assertTrue( p.read.next()==null);
					
			}finally{ p.close(); };
			
			leave();
		};	
};