package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.io.IOException;

/**
	An indicator format implementation over {@link CObjListFormat} media.
*/
public class CObjIndicatorReadFormat implements IIndicatorReadFormat
{
			private final CObjListFormat media;
			private final int max_registrations;
			private final boolean is_described;
			private final boolean is_flushing;
			
			/** Active name length limit */
			private int max_signal_name_length = 1024;
			
			/** Name and number cache for indicators */
			private String signal_name;
			private int signal_number;
			/** Controls if above have to be re-read */
			private boolean signal_data_valid;
			
			/** Used to validate registrations contract */
			private int registration_count;
			
			/** Used to implement array operations stitching.
			Carries pointer from which return data in currently
			processes block on {@link #media} */
			private int array_op_ptr;			
			
			
		/** Creates
		@param media non null media to read from
		@param max_registrations number returned from {@link #getMaxRegistrations}
		@param is_described returned from {@link #isDescribed}. If false
			type writes are non-op
		@param is_flushing returned from {@link #isFlushing}. If false
			type writes are non-op
		@throws AssertionError is something is wrong. 
		*/
		public CObjIndicatorReadFormat(CObjListFormat media, 
										final int max_registrations,
										final boolean is_described,
										final boolean is_flushing
										)
		{ 
			assert(media!=null);
			assert(max_registrations>=0);
			assert( !is_flushing || (is_flushing && is_described)):"invalid is_described/is_flushing combination";
			this.media = media; 
			this.max_registrations=max_registrations;
			this.is_described=is_described;
			this.is_flushing=is_flushing;
		};
		/* *******************************************************
		
				IIndicatorReadFormat
				
		
		********************************************************/
		/* ------------------------------------------------------
		
				Information and settings
		
		------------------------------------------------------*/
		@Override public final int getMaxRegistrations(){ return max_registrations; };
		@Override public final boolean isDescribed(){return is_described; };
		@Override public final boolean isFlushing(){return is_flushing; };
		@Override public void setMaxSignalNameLength(int characters)
		{
			assert(characters>=0);
			this.max_signal_name_length=max_signal_name_length;
		};
		/* ------------------------------------------------------
		
				Signals
		
		------------------------------------------------------*/
		@Override public TIndicator getIndicator()throws IOException
		{
			//poll data
			if (media.isEmpty()) 
			{
				//have to invalidate signal info
				this.signal_name=null;	
				this.signal_number=-1;
				return TIndicator.EOF;
			};
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof TIndicator)
			{
				TIndicator indicator = (TIndicator)at_cursor;
				//pick name and numbers to cache
				int offset = 0;
				boolean signal_data_valid = this.signal_data_valid;
				if ((indicator.FLAGS & TIndicator.REGISTER)!=0)
				{
					offset++;
					if (!signal_data_valid) //to avoid re-reading if already read.
					{
						if (media.size()<offset+1) throw new EUnexpectedEof();					
						int n = ((Number)media.get(offset)).intValue();
						if (n<0) throw new ECorruptedFormat("signal number="+n);
						if ((indicator.FLAGS & TIndicator.USE_REGISTER)==0)
						{
							if (n!=registration_count) throw new ECorruptedFormat("signal number="+n+" out of sequence "+registration_count);
							this.registration_count++;
						}else
						{
							if (n>=registration_count) throw new ECorruptedFormat("signal number="+n+" out of sequence "+registration_count);
						};						
						this.signal_number = n;
						this.signal_data_valid=true;
					};
				}else
				{
					//have to invalidate signal info
					this.signal_number=-1;
				};
				if ((indicator.FLAGS & TIndicator.NAME)!=0)
				{
					offset++;
					if (!signal_data_valid) //to avoid re-reading if already read.
					{
						if (media.size()<offset+1) throw new EUnexpectedEof();					
						String n = (String)media.get(offset);
						if (n.length()>max_signal_name_length) throw new EFormatBoundaryExceeded("Name too long");
						this.signal_name = n;
						this.signal_data_valid=true;
					};	
				}else
				{
					//have to invalidate signal info
					this.signal_name=null;
				};
				return indicator;
			}else
			{
				//have to invalidate signal info
				this.signal_name=null;
				this.signal_number=-1;
				return TIndicator.DATA;
			}			
		};
		@Override public void next()throws IOException
		{	
			this.signal_data_valid=false;	//<-- do not invalidate cache,
										//but make sure it is refreshed from
										//signal.
			if (media.isEmpty()) throw new EUnexpectedEof();
			//check what are we skipping?
			Object at_cursor = media.removeFirst();
			if (at_cursor instanceof TIndicator)
			{
				TIndicator indicator = (TIndicator)at_cursor;
				if ((indicator.FLAGS & TIndicator.REGISTER)!=0)
				{
					if (media.isEmpty()) throw new EUnexpectedEof();
					media.removeFirst();
				};
				if ((indicator.FLAGS & TIndicator.NAME)!=0)
				{
					if (media.isEmpty()) throw new EUnexpectedEof();
					media.removeFirst();
				};
			}else
			{
				//we can have more than one data object, all have to
				//be skipped.
				for(;;)
				{
				 	if (media.isEmpty()) throw new EUnexpectedEof();
					at_cursor = media.getFirst();
					if (at_cursor instanceof TIndicator) break;
					media.removeFirst();
				};
			};
		};
		@Override public String getSignalName()
		{
			String n=this.signal_name;
			if (n==null) throw new IllegalStateException("Signal number can't be read.");
			return n;
		};
		@Override public int getSignalNumber()
		{
			int n=this.signal_number;
			if (n==-1) throw new IllegalStateException("Signal number can't be read.");
			return n;
		};
		
		/* *******************************************************
		
				IPrimitiveReadFormat
				
		
		********************************************************/
		private void startPrimitiveRead()throws IOException
		{
			if (media.isEmpty()) throw new EUnexpectedEof();			
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof TIndicator) throw new AssertionError("at_cursor="+at_cursor);
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
			startPrimitiveRead();
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
			startPrimitiveRead();
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
			startPrimitiveRead();
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
			startPrimitiveRead();
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
			startPrimitiveRead();	
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
			startPrimitiveRead();
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
			startPrimitiveRead();		
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
			startPrimitiveRead();	
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
			startPrimitiveRead();
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
			startPrimitiveRead();
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