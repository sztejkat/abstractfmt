package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.io.IOException;

/**
	An indicator format implementation over {@link CObjListFormat} media.
*/
public class CObjIndicatorReadFormat implements IIndicatorReadFormat
{
			private final CObjListFormat media;
			private final int max_signal_name_length;
			/** Name registration number, -1 for invalid */
			private int register_number;
			/** Signal name data */
			private String signal_name;
			/** Used to implement array operations stitching.
			Carries pointer from which return data in currently
			processes block on {@link #media} */
			private int array_op_ptr;
			
		/** Creates
		@param media non null media to read from
		@param max_signal_name_length boundary of signal name length. 
		*/
		public CObjIndicatorReadFormat(CObjListFormat media, int max_signal_name_length)
		{ 
			assert(max_signal_name_length>=0);
			assert(media!=null);
			
			this.media = media; 
			this.max_signal_name_length=max_signal_name_length;			
			this.register_number =-1;
		};
		/* *******************************************************
		
				IIndicatorReadFormat
				
		
		********************************************************/
		public int getMaxRegistrations(){ return Integer.MAX_VALUE; };
		public TIndicator readIndicator()throws IOException
		{
			//Any call invalidates store name and number
		 	register_number = -1;
		 	signal_name=null; 
		 	//poll data
			if (media.isEmpty()) return TIndicator.EOF;
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof TIndicator)
			{
				 media.removeFirst();
				 TIndicator indicator = (TIndicator)at_cursor;
				 //Handler name and registration pick-ups.
				 //Note: Writer always writes number first, then
				 //text so we can use flags to pick it.				 
				 if ((indicator.FLAGS & TIndicator.REGISTER)!=0)
				 {
				 	if (media.isEmpty()) throw new EUnexpectedEof("No registration number?");
				 	register_number = ((Integer)(media.removeFirst())).intValue();	
				 	if (register_number<0) throw new ECorruptedFormat("Negative registration number");		 	
				 };
				 if ((indicator.FLAGS & TIndicator.NAME)!=0)
				 {
				 	if (media.isEmpty()) throw new EUnexpectedEof("No signal name?");
				 	String n = (String)(media.removeFirst());
				 	if (n.length()>	max_signal_name_length) throw new EFormatBoundaryExceeded("Name length "+n.length()+" is more than "+max_signal_name_length);
				 	this.signal_name=n;	
				 };
				 return indicator;
			}else
			{
				return TIndicator.DATA;
			}			
		};
		public String getSignalName()
		{
			String n =this.signal_name;
			if (n==null) throw new IllegalStateException("Signal name can't be read.");
			this.signal_name = null;
			return n;
		};
		public int getSignalNumber()
		{
			int n=this.register_number;
			if (n==-1) throw new IllegalStateException("Signal number can't be read.");
			this.register_number = -1;
			return n;
		};
		public void skip()throws IOException
		{			
			for(;;)
			{
				if (media.isEmpty()) throw new EUnexpectedEof();
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break;
				media.removeFirst();
			}
		};
		/* *******************************************************
		
				IPrimitiveReadFormat
				
		
		********************************************************/
		private void startPrimitiveRead()throws IOException
		{
			if (media.isEmpty()) throw new EUnexpectedEof();			
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof TIndicator) throw new ENoMoreData();
		};
		public boolean readBoolean()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Boolean)
			{
				media.removeFirst();
				return ((Boolean)at_cursor).booleanValue();
			}else
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).intValue()!=0;
			}else
				throw new ECorruptedFormat(at_cursor+" while expected Boolean/Number");
		};
		public byte readByte()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).byteValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		public char readChar()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return (char)(((Number)at_cursor).intValue());
			}else
			if (at_cursor instanceof Character)
			{
				media.removeFirst();
				return ((Character)at_cursor).charValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Char/Number");
		};
		
		
		public short readShort()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).shortValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		public int readInt()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).intValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		public long readLong()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).longValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		public float readFloat()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).floatValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		public double readDouble()throws IOException
		{
			startPrimitiveRead();
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof Number)
			{
				media.removeFirst();
				return ((Number)at_cursor).doubleValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Number");
		};
		
		/* --------------------------------------------------------
				Blocks
		---------------------------------------------------------*/
		@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof boolean [])
				{
					//Now serve data from array_op_ptr
					final boolean [] data = (boolean[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected boolean[]");
			};
			return read_count;
		};
		
		
		@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof byte [])
				{
					//Now serve data from array_op_ptr
					final byte [] data = (byte[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected byte[]");
			};
			return read_count;
		};
		
		
		@Override public int readByteBlock()throws IOException
		{		
			byte v = 0;
			boolean read = false;
			while(!read)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) return -1; //we do not test any kind of consistency.				
				if (at_cursor instanceof byte [])
				{
					//Now serve data from array_op_ptr
					final byte [] data = (byte[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					if (ptr<L)
					{
						v = data[ptr];
						ptr++;
						this.array_op_ptr=ptr;
						read = true;
					}; 
					if (ptr>=L)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected byte[]");
			};
			return 0xFF & v; 
		};
		
		
		@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof char [])
				{
					//Now serve data from array_op_ptr
					final char [] data = (char[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected char[]");
			};
			return read_count;
		};
		
		
		@Override public int readCharBlock(Appendable characters,  int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof char [])
				{
					//Now serve data from array_op_ptr
					final char [] data = (char[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						for(int i = to_transfer; --i>=0;)
						{
							characters.append(data[ptr++]);
						};
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected char[]");
			};
			return read_count;
		};
		
		@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof short [])
				{
					//Now serve data from array_op_ptr
					final short [] data = (short[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected short[]");
			};
			return read_count;
		};
		
		
		@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof int [])
				{
					//Now serve data from array_op_ptr
					final int [] data = (int[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected int[]");
			};
			return read_count;
		};
		
		
		@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof long [])
				{
					//Now serve data from array_op_ptr
					final long [] data = (long[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected long[]");
			};
			return read_count;
		};
		
		
		@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof float [])
				{
					//Now serve data from array_op_ptr
					final float [] data = (float[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected float[]");
			};
			return read_count;
		};
		
		
		@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{		
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof TIndicator) break; //we do not test any kind of consistency.				
				if (at_cursor instanceof double [])
				{
					//Now serve data from array_op_ptr
					final double [] data = (double[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					int available = L -  ptr;
					final int to_transfer =   available< length ? available : length;
					if (to_transfer!=0)
					{
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					}; 
					//Now purge fully used up blocks.
					available = L -  ptr;
					if (available<=0)
					{
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" ("+at_cursor+") while expected double[]");
			};
			return read_count;
		};
		
		
		/* *******************************************************
		
				Closeable
				
		
		********************************************************/
		public void close(){};
};