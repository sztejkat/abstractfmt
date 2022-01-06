package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.*;
import java.io.OutputStream;

/**
	A chunk-based {@link IIndicatorWriteFormat}, described.
*/
public class CBinDescIndicatorWriteFormat extends ABinIndicatorWriteFormat1
{			
				/** Cache for {@link #writeBoolean(boolean)}
				since this value is encoded in chunk header */
				private boolean boolean_cache;
	/** Creates
	@param output see {@link ABinIndicatorWriteFormat1#ABinIndicatorWriteFormat1}
	*/
	protected CBinDescIndicatorWriteFormat(
							OutputStream output
							)
	{
		super(output, 
				3,//max_header_size,
				65535 // max_chunk_size);
				);
	};
	/* ********************************************************************
	
	
			Services required by superclass
	
	
	* *********************************************************************/
	@Override protected int prepareChunkHeaderForFlushing(
													 TIndicator header_indicator,
													 byte [] header_buffer
													 )throws IOException
	{
		switch(header_indicator)
		{
			case DATA:
					return prepare_DATA_HeaderForFlushing(header_buffer);
			case BEGIN_DIRECT:
					return prepare_BEGIN_DIRECT_HeaderForFlushing(header_buffer);
			case BEGIN_REGISTER:
					return prepare_BEGIN_REGISTER_HeaderForFlushing(header_buffer);
			case BEGIN_USE:
					return prepare_BEGIN_USE_HeaderForFlushing(header_buffer);
			case END:
					return prepare_END_HeaderForFlushing(header_buffer);
			case END_BEGIN_DIRECT:
					return prepare_END_BEGIN_DIRECT_HeaderForFlushing(header_buffer);			
			case END_BEGIN_REGISTER:
					return prepare_END_BEGIN_REGISTER_HeaderForFlushing(header_buffer);			
			case END_BEGIN_USE:
					return prepare_END_BEGIN_USE_HeaderForFlushing(header_buffer);
			case TYPE_BOOLEAN: 			
					return prepare_TYPE_BOOLEAN_HeaderForFlushing(header_buffer);
			case TYPE_BYTE: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_BYTE);
			case TYPE_CHAR: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_CHAR);
			case TYPE_SHORT: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_SHORT);
			case TYPE_INT: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_INT);
			case TYPE_LONG: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_LONG);	
			case TYPE_FLOAT: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_FLOAT);	
			case TYPE_DOUBLE: 			
					return prepare_TYPE_x_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_DOUBLE);	
			case TYPE_BOOLEAN_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_BOOLEAN_BLOCK);		
			case TYPE_BYTE_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_BYTE_BLOCK);
			case TYPE_CHAR_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_CHAR_BLOCK);
			case TYPE_SHORT_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_SHORT_BLOCK);
			case TYPE_INT_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_INT_BLOCK);
			case TYPE_LONG_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_LONG_BLOCK);	
			case TYPE_FLOAT_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_FLOAT_BLOCK);	
			case TYPE_DOUBLE_BLOCK: 			
					return prepare_TYPE_x_BLOCK_HeaderForFlushing(header_buffer, header_indicator, TBinDescribed.TYPE_DOUBLE_BLOCK);		
			default: throw new AssertionError();
		}
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_DATA_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive used payload size and decide on chunk type
		int s = getPayloadSize();
		if (s==0) return 0;	//zero sized data chunks are not written at all.
		s--;
		if (s<=7)
		{
			//short data
			header_buffer[0] = (byte)( TBinDescribed.DATA_SHORT | (s<<5));
			return 1;
		}else
		if(s<=2047)
		{
			//medium data
			header_buffer[0] = (byte)( TBinDescribed.DATA_MEDIUM | (s<<5));
			header_buffer[1] = (byte)(s>>>3);
			return 2;
		}else
		{
			assert(s<=65535);
			//long data.
			header_buffer[0] = TBinDescribed.DATA_LONG;
			header_buffer[1] = (byte)(s);
			header_buffer[2] = (byte)(s>>>8);
			return 3;
		}
	};
	
	@Override protected void startDataChunk()throws IOException
	{
		startChunk(
					TIndicator.DATA,//TIndicator header_indicator,
					65535			//int chunk_payload_capacity, max kind of data chunk.
						      );
	};
	
	/* ***********************************************************************
	
	
			IIndicatorWriteFormat
	
	
	* ***********************************************************************/
	/* --------------------------------------------------
	
			Information and settings.
	
	-------------------------------------------------- */
	/** Always false */
	@Override public final boolean isDescribed(){ return true;};
	/* --------------------------------------------------
	
			Signals related indicators..
	
	-------------------------------------------------- */
	/** Encodes signal specified in <a href="doc-files/chunk-syntax-described.html#BEGIN_DIRECT">BEGIN_DIRECT</a>
	*/
	@Override public void writeBeginDirect(String signal_name)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.BEGIN_DIRECT,//TIndicator header_indicator,
					7+1					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_BEGIN_DIRECT_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=1)&&(s<=7+1)):"s="+s;	//header type limits.
		s = s-1;
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.BEGIN_DIRECT | (s<<5));
		return 1;
	};
	
	@Override public void writeEndBeginDirect(String signal_name)throws IOException		
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_DIRECT,//TIndicator header_indicator,
					7+1					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_END_BEGIN_DIRECT_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=1)&&(s<=7+1)):"s="+s;	//header type limits.
		s = s-1;
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.END_BEGIN_DIRECT | (s<<5));
		return 1;
	};
	
	
	@Override public void writeBeginRegister(String signal_name, int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.BEGIN_REGISTER,//TIndicator header_indicator,
					7+1					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_BEGIN_REGISTER_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=1)&&(s<=7+1));	//header type limits.
		s = s-1;
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.BEGIN_REGISTER | (s<<5));
		return 1;
	};   
	
	
	@Override public void writeEndBeginRegister(String signal_name, int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_REGISTER,//TIndicator header_indicator,
					7+1					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_END_BEGIN_REGISTER_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=1)&&(s<=7+1));	//header type limits.
		s = s-1;
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.END_BEGIN_REGISTER | (s<<5));
		return 1;
	};
				/** Stored for {@link #writeBeginUse}/{@link #writeEndBeginUse} */
				private byte use_number;
	@Override public void writeBeginUse(int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.BEGIN_USE,//TIndicator header_indicator,
					7					//int chunk_payload_capacity
					);
		assert(number>=0);
		assert(number<=255);
		this.use_number =(byte)number;
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_BEGIN_USE_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=7));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.BEGIN_USE | (s<<5));
		header_buffer[1]=use_number;
		return 2;
	};
	
	
	@Override public void writeEndBeginUse(int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_USE,//TIndicator header_indicator,
					7					//int chunk_payload_capacity
					);
		assert(number>=0);
		assert(number<=255);
		this.use_number =(byte)number;
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_END_BEGIN_USE_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=7));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.END_BEGIN_USE | (s<<5));
		header_buffer[1]=use_number;
		return 2;
	};
	
	@Override public void writeEnd()throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END,//TIndicator header_indicator,
					7					//int chunk_payload_capacity
					);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	@throws IOException if failed.
	*/
	private int prepare_END_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=7));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinDescribed.END | (s<<5));
		return 1;
	};
	
	/* --------------------------------------------------
	
			Type related indicators
	
	-------------------------------------------------- */
	@Override final public void writeType(TIndicator type)throws IOException
	{
		//Do nothing operation in un-described format.
		startChunk( type,
			TBinDescribed.chunkPayloadForType(type)
			);
	};
	private int prepare_TYPE_BOOLEAN_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert(s==0);	//header type limits.
		//encode type
		byte v = TBinDescribed.TYPE_BOOLEAN;
		if (boolean_cache)
			v |= (byte)(1<<5);
		header_buffer[0]=v;
		return 1;
	};
	private int prepare_TYPE_x_HeaderForFlushing( byte [] header_buffer, TIndicator header_indicator, byte header_type )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert(s==TBinDescribed.chunkPayloadForType(header_indicator)):"s="+s+" for "+header_indicator;	//header specific type limits.
		assert((s>=0)||(s<=7));	//header generic type limits.
		//encode type
		header_buffer[0]=(byte)(header_type | (s<<5));
		return 1;
	};
	private int prepare_TYPE_x_BLOCK_HeaderForFlushing( byte [] header_buffer, TIndicator header_indicator, byte header_type )throws IOException
	{
		//retrive size
		int s = getPayloadSize();	//this size is expressed in bytes.
		assert((s>=0)||(s<=TBinDescribed.chunkPayloadForType(header_indicator))); //type specific limits.	
		int items = s / TBinDescribed.chunkPayloadUnitForBlockType(header_indicator);
		assert(items *TBinDescribed.chunkPayloadUnitForBlockType(header_indicator)==s):
				"getPayloadSize()="+s+" is not multiple of "+TBinDescribed.chunkPayloadUnitForBlockType(header_indicator)+" for "+header_indicator;
		assert(items<=7);
		//encode type
		header_buffer[0]=(byte)(header_type | (items<<5));
		return 1;
	};
	/* --------------------------------------------------
	
			IPrimitiveWriteFormat
	
	-------------------------------------------------- */
	@Override public void writeBoolean(boolean v)throws IOException
	{
		//encoded in header, so we cache it.
		boolean_cache = v;
	};
	/* --------------------------------------------------
	
			State, Closeable, Flushable	
	
	-------------------------------------------------- */
	/** Do nothing operation */
	public void open()throws IOException{};
};