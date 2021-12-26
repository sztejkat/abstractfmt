package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.IIndicatorWriteFormat;
import sztejkat.abstractfmt.TIndicator;
import java.io.*;
import java.io.OutputStream;

/**
	A chunk-based {@link IIndicatorWriteFormat}, undescribed.
*/
public class CBinIndicatorWriteFormat extends ABinIndicatorWriteFormat1
{			
	protected CBinIndicatorWriteFormat(
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
			default: throw new AssertionError();
		}
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_DATA_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive used payload size and decide on chunk type
		int s = getPayloadSize();
		if (s==0) return 0;	//zero sized data chunks are not written at all.
		s--;
		if (s<=15)
		{
			//short data
			header_buffer[0] = (byte)( TBinUndescribed.DATA_SHORT | (s<<4));
			return 1;
		}else
		if(s<=4095)
		{
			//medium data
			header_buffer[0] = (byte)( TBinUndescribed.DATA_MEDIUM | (s<<4));
			header_buffer[1] = (byte)(s>>>4);
			return 2;
		}else
		{
			assert(s<=65535);
			//long data.
			header_buffer[0] = TBinUndescribed.DATA_MEDIUM;
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
	@Override public final boolean isDescribed(){ return false;};
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
					15+3					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_BEGIN_DIRECT_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=3)&&(s<=15+3));	//header type limits.
		s = s-3;
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.BEGIN_DIRECT | (s<<4));
		return 1;
	};
	
	@Override public void writeEndBeginDirect(String signal_name)throws IOException		
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_DIRECT,//TIndicator header_indicator,
					15+3					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_END_BEGIN_DIRECT_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=3)&&(s<=15+3));	//header type limits.
		s = s-3;
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.END_BEGIN_DIRECT | (s<<4));
		return 1;
	};
	
	
	@Override public void writeBeginRegister(String signal_name, int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.BEGIN_REGISTER,//TIndicator header_indicator,
					15+3					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_BEGIN_REGISTER_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=3)&&(s<=15+3));	//header type limits.
		s = s-3;
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.BEGIN_REGISTER | (s<<4));
		return 1;
	};   
	
	
	@Override public void writeEndBeginRegister(String signal_name, int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_REGISTER,//TIndicator header_indicator,
					15+3					//int chunk_payload_capacity
						      );
		//and follow it with name which may, by itself, generate other chunks.
		writeBeginDirectPayload(signal_name);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_END_BEGIN_REGISTER_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=3)&&(s<=15+3));	//header type limits.
		s = s-3;
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.END_BEGIN_REGISTER | (s<<4));
		return 1;
	};
				/** Stored for {@link #writeBeginUse}/{@link #writeEndBeginUse} */
				private byte use_number;
	@Override public void writeBeginUse(int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.BEGIN_USE,//TIndicator header_indicator,
					15					//int chunk_payload_capacity
					);
		assert(number>=0);
		assert(number<=255);
		this.use_number =(byte)number;
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_BEGIN_USE_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=15));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.BEGIN_USE | (s<<4));
		header_buffer[1]=use_number;
		return 2;
	};
	
	
	@Override public void writeEndBeginUse(int number)throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END_BEGIN_USE,//TIndicator header_indicator,
					15					//int chunk_payload_capacity
					);
		assert(number>=0);
		assert(number<=255);
		this.use_number =(byte)number;
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_END_BEGIN_USE_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=15));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.END_BEGIN_USE | (s<<4));
		header_buffer[1]=use_number;
		return 2;
	};
	
	@Override public void writeEnd()throws IOException
	{
		//We just start the chunk
		startChunk(
					TIndicator.END,//TIndicator header_indicator,
					15					//int chunk_payload_capacity
					);
	};
	/** Header specific handler for {@link #prepareChunkHeaderForFlushing}
	@param header_buffer as specified there
	@return --//--
	*/
	private int prepare_END_HeaderForFlushing( byte [] header_buffer )throws IOException
	{
		//retrive size
		int s = getPayloadSize();
		assert((s>=0)||(s<=15));	//header type limits.
		//encode type
		header_buffer[0]=(byte)(TBinUndescribed.END | (s<<4));
		return 1;
	};
	
	/* --------------------------------------------------
	
			Type related indicators
	
	-------------------------------------------------- */
	@Override final public void writeType(TIndicator type)throws IOException
	{
		//Do nothing operation in un-described format.
	};
	/* --------------------------------------------------
	
			IPrimitiveWriteFormat
	
	-------------------------------------------------- */
	@Override public void writeBoolean(boolean v)throws IOException
	{
		writePayload(v ? (byte)0x01 : (byte)0x00);
	};
	/* --------------------------------------------------
	
			State, Closeable, Flushable	
	
	-------------------------------------------------- */
	/** Do nothing operation */
	public void open()throws IOException{};
};