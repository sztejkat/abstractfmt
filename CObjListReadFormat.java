package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.Arrays;
/**
		Primarily a test bed for {@link ASignalReadFormat} which
		reads signals and data from {@link CObjListFormat}.
		<p>
		Intended to be used in tests, but users may look at it as
		on a base, primitive implementation.
		<p>
		Absolutely not thread safe.
*/
public class CObjListReadFormat extends ASignalReadFormat
{
					/** A media to which this class writes. */
					public final CObjListFormat media;
					/** Used to implement array operations stitching.
					Carries pointer from which return data in currently
					processes block on {@link #media} */
					private int array_op_ptr;
					/** For use in {@link #readRegisterIndex}
					and {@link #readRegisterUse} which needs to 
					provide data from indicator */
					private CObjListFormat.INDICATOR last_indicator;
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalReadFormat#ASignalReadFormat(int,int,int,boolean)}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param strict_described_types --//--
		@param media non null media from which read data.
		*/
		public CObjListReadFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 boolean strict_described_types,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth,strict_described_types);
			assert(media!=null);
			this.media = media;
		};		
		/* *************************************************************
		
				Services required by superclass.		
		
		***************************************************************/
		/*............................................................		
				Indicators		
		............................................................*/
		@Override protected int readIndicator()throws IOException
		{
			//check if end-of file
			if (media.isEmpty()) return EOF_INDICATOR;
			//Poll content under cursor
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof CObjListFormat.INDICATOR)
			{
				//we have an indicator
				array_op_ptr = 0;		//each indicator clears array op.
				media.removeFirst();	//read it from media.
				CObjListFormat.INDICATOR i = (CObjListFormat.INDICATOR)at_cursor;
				last_indicator = i; //for name index reading services.
				return i.type;
			}else
			{
				//we don't have indicator but data instead.
				last_indicator = null;
				return NO_INDICATOR;
			}
		};
		@Override  protected void skip()throws IOException,EUnexpectedEof
		{
			array_op_ptr = 0;	//reset any array op.
			//loop
			for(;;)
			{
				if (media.isEmpty()) throw new EUnexpectedEof();
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof CObjListFormat.INDICATOR)
				{
						//indicator found in stream.
						return;
				}else
				{
					//remove from media.
					media.removeFirst();
				};
			}
		};
		/*............................................................		
				Signals		
		............................................................*/
		@Override protected void readSignalNameData(Appendable a, int limit)throws IOException
		{
			//We expect that in stream there is a String representing name,
			//as format specs say.			
			Object at_cursor = media.pollFirst();
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof String)
			{
				//Now we can just add it. A more complex system would fetch
				//data char-by-char and test limit, but since we do operate on
				//string form, we can just add it and rely on superclass sending
				//us Appendable which applies the limit.
				a.append((String)at_cursor);
			}else
				throw new EBrokenStream();
		};
		@Override protected int readRegisterIndex()throws IOException
		{
			//We expect that in stream there is a REGISTER_INDICATOR representing name,
			//as format specs say.			
			CObjListFormat.INDICATOR at_cursor = last_indicator;
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof CObjListFormat.REGISTER_INDICATOR)
			{
				last_indicator=null;
				return ((CObjListFormat.REGISTER_INDICATOR)at_cursor).name_index;
			}else
				throw new EBrokenStream("Expected REGISTER_INDICATOR but "+at_cursor+" is found");
		}
		@Override protected int readRegisterUse()throws IOException
		{
			//We expect that in stream there is a REGISTER_USE_INDICATOR representing name,
			//as format specs say.			
			CObjListFormat.INDICATOR at_cursor =last_indicator;
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof CObjListFormat.REGISTER_USE_INDICATOR)
			{
				last_indicator=null;
				return ((CObjListFormat.REGISTER_USE_INDICATOR)at_cursor).name_index;
			}else
				throw new EBrokenStream("Expected REGISTER_USE_INDICATOR but  "+at_cursor+" is found");
		}
		/*............................................................		
				low level I/O		
		............................................................*/
		/** Empty */
		@Override protected void closeImpl()throws IOException{};
		/*............................................................		
				elementary primitive reads.
				
			Note: Since reads are basically un-typed, but our 
			data are typed by objects we will do some test, try
			some casts, but in many cases throw EDataMissmatch.
			
		............................................................*/
		@Override protected boolean readBooleanImpl()throws IOException
		{
			//There is no need to check for physical or logic EOF
			//because it is handled by a caller. We have to be however
			//prepared to have incorrect type of data if stream is untyped.
			Object at_cursor = media.pollFirst();	//we do remove data, as the exact
													//behaviour on EDataMissmatch is
			if (at_cursor instanceof Boolean)
			{
				return ((Boolean)at_cursor).booleanValue();
			}else
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).intValue()!=0;
			}else
				throw new EDataMissmatch(at_cursor+" while expected Boolean/Number");
		};
		@Override protected byte readByteImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).byteValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Byte/Number");
		};
		@Override protected char readCharImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return (char)((Number)at_cursor).intValue();
			}else
			if (at_cursor instanceof Character)
			{
				return ((Character)at_cursor).charValue();
			}else
				throw new EDataMissmatch(at_cursor+" while expected Character/Number");
		};
		@Override protected short readShortImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).shortValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Short/Number");
		};
		@Override protected int readIntImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).intValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Int/Number");
		};
		@Override protected long readLongImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).longValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Long/Number");
		};
		@Override protected float readFloatImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).floatValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Float/Number");
		};
		@Override protected double readDoubleImpl()throws IOException
		{
			Object at_cursor = media.pollFirst();	
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).doubleValue();
			}
				throw new EDataMissmatch(at_cursor+" while expected Double/Number");
		};
		
		/*............................................................		
				block primitive reads.
				
				Note: All block reads do "stitch" adjacent blocks.
		..............................................................*/
		@Override protected int readBooleanBlockImpl(boolean [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_BOOLEAN_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof boolean [])
				{
					//Now serve data from array_op_ptr
					final boolean [] data = (boolean[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected boolean[]");
			};
			return read_count;
		};
		@Override protected int readByteBlockImpl(byte [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_BYTE_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof byte [])
				{
					//Now serve data from array_op_ptr
					final byte [] data = (byte[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected byte[]");
			};
			return read_count;
		};
		@Override protected int readByteBlockImpl()throws IOException
		{
			//Since after transfer we may be at the end of block we basically have to
			//implement the same code as for block reads.
			for(;;)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_BYTE_BLOCK_END)
				   )
				{
					return -1;	//we have nothing read.
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof byte [])
				{
					//Now serve data from array_op_ptr
					final byte [] data = (byte[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						byte v = data[ptr];
						ptr++;
						this.array_op_ptr=ptr;
						return ((int)v)&0xff;
					}
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected byte[]");
			}
		};
		
		@Override protected int readCharBlockImpl(char [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_CHAR_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof char [])
				{
					//Now serve data from array_op_ptr
					final char [] data = (char[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected char[]");
			};
			return read_count;
		};
		@Override protected int readCharBlockImpl(Appendable a, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_CHAR_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof char [])
				{
					//Now serve data from array_op_ptr
					final char [] data = (char[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						for(int i = 0;i<to_transfer;i++)
						{
							a.append(data[ptr+i]);
						};
						ptr+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected char[]");
			};
			return read_count;
		};
		@Override protected int readShortBlockImpl(short [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_SHORT_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof short [])
				{
					//Now serve data from array_op_ptr
					final short [] data = (short[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected short[]");
			};
			return read_count;
		};
		@Override protected int readIntBlockImpl(int [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_INT_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof int [])
				{
					//Now serve data from array_op_ptr
					final int [] data = (int[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected int[]");
			};
			return read_count;
		};
		@Override protected int readLongBlockImpl(long [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_LONG_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof long [])
				{
					//Now serve data from array_op_ptr
					final long [] data = (long[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected long[]");
			};
			return read_count;
		};
		@Override protected int readFloatBlockImpl(float [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_FLOAT_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof float [])
				{
					//Now serve data from array_op_ptr
					final float [] data = (float[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected float[]");
			};
			return read_count;
		};
		@Override protected int readDoubleBlockImpl(double [] buffer, int offset, int length)throws IOException
		{
			//sever through blocks
			int read_count =0;
			while(length!=0)
			{
				//check what is under a cursor, but do not remove it.
				if (media.isEmpty()) throw new EUnexpectedEof();	//this is unexpected
				Object at_cursor = media.getFirst();
				if (
				    (at_cursor == CObjListFormat.BEGIN_INDICATOR)
					 ||
				    (at_cursor == CObjListFormat.END_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.END_BEGIN_INDICATOR)
				     ||
				    (at_cursor == CObjListFormat.TYPE_DOUBLE_BLOCK_END)
				   )
				{
					//the allowed condition when we reach block terminator
					//after we fully read previous data block.
					//We are never called when it is a first request, so we just
					//return how much we read up to now in this operation.
					break;
				};
				//now we have non-terminating data, so it must be compatible block array.
				if (at_cursor instanceof double [])
				{
					//Now serve data from array_op_ptr
					final double [] data = (double[])at_cursor;
					final int L = data.length;
					int ptr = this.array_op_ptr;		//pick to local to avoid field fetches.
					final int available = L -  ptr;
					if (available<=0)
					{
						//move to next element, by taking current element from queue
						media.removeFirst();
						this.array_op_ptr = 0;	//reset array pointer.
						continue;
					}else
					{
						//provide a data block and track transfer size.
						final int to_transfer =   available< length ? available : length;
						System.arraycopy( data, ptr, buffer, offset, to_transfer);
						ptr+=to_transfer;
						offset+=to_transfer;
						read_count+=to_transfer;
						length-=to_transfer;
						this.array_op_ptr=ptr;
					};
				}else
					throw new EDataMissmatch(at_cursor.getClass()+" while expected double[]");
			};
			return read_count;
		};
	
};