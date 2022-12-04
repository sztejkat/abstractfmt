package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.AStructWriteFormatBase0;
import sztejkat.abstractfmt.IFormatLimits;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
/**
	Stream producing {@link Collection} of {@link IObjStructFormat0}.
	
	@see CObjStructReadFormat0
*/
public class CObjStructWriteFormat0 extends AStructWriteFormatBase0 
{			
			
				/** A collection to which objects representing
				stream operations are added. */
				public final Collection<IObjStructFormat0> stream;
				/** Controls end-begin optimization */
				private final boolean end_begin_enabled;
				/** A bounadry of format */
				private final int max_supported_recursion_depth;
				/** A bounadry of format */
				private final int max_supported_name_length; 

	/** Creates	
	@param end_begin_enabled if true the {@link SIG_END_BEGIN} is used
		to implement {@link #endBeginImpl}. If false default implementation
		is left and a pair of signals {@link SIG_END}+{@link SIG_BEGIN} is used.
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
    */
	public CObjStructWriteFormat0(boolean end_begin_enabled,
								  int max_supported_recursion_depth,
								  int max_supported_name_length
								  )
	{
		assert(max_supported_name_length>0);
		assert(max_supported_recursion_depth>=-1);
		
		this.end_begin_enabled = end_begin_enabled;
		this.max_supported_recursion_depth=max_supported_recursion_depth;
		this.max_supported_name_length=max_supported_name_length;
		
		stream = new ArrayList<IObjStructFormat0>();
	};
	/* ***********************************************************************
		
				AStructWriteFormatBase0
				
		
	************************************************************************/
	/** Overriden to add to {@link #stream} the instance of {@link SIG_END} */
	@Override protected void endImpl()throws IOException
	{
		stream.add(SIG_END.INSTANCE);			
	};
	/** Overriden to add to {@link #stream} the instance of {@link SIG_BEGIN} */
	@Override protected void beginImpl(String name)throws IOException
	{
		stream.add(new SIG_BEGIN(name));		
	};
	/** Overriden to add to {@link #stream} the instance of {@link SIG_END_BEGIN} */
	@Override protected void endBeginImpl(String name)throws IOException
	{
		if (end_begin_enabled)
			stream.add(new SIG_END_BEGIN(name));
		else
			super.endBeginImpl(name);
	};
	/** Doesn't do anything */
	@Override protected void openImpl()throws IOException{};
	/** Doesn't do anything */
	@Override protected void closeImpl()throws IOException{};
	/** Doesn't do anything */
	@Override protected void flushImpl()throws IOException{};
	/** Adds to a stream the instance of {@link ELMT_BOOLEAN} */
	@Override protected void writeBooleanImpl(boolean v)throws IOException
	{
		stream.add(ELMT_BOOLEAN.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_BYTE} */
	@Override protected void writeByteImpl(byte v)throws IOException
	{
		stream.add(ELMT_BYTE.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_CHAR} */
	@Override protected void writeCharImpl(char v)throws IOException
	{
		stream.add(ELMT_CHAR.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_SHORT} */
	@Override protected void writeShortImpl(short v)throws IOException
	{
		stream.add(new ELMT_SHORT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_INT} */
	@Override protected void writeIntImpl(int v)throws IOException
	{
		stream.add(new ELMT_INT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_LONG} */
	@Override protected void writeLongImpl(long v)throws IOException
	{
		stream.add(new ELMT_LONG(v));
	};
	/** Adds to a stream the instance of {@link ELMT_FLOAT} */
	@Override protected void writeFloatImpl(float v)throws IOException
	{
		stream.add(new ELMT_FLOAT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_DOUBLE} */
	@Override protected void writeDoubleImpl(double v)throws IOException
	{
		stream.add(new ELMT_DOUBLE(v));
	};
	/** Adds to a stream the instance of {@link BLK_BOOLEAN}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeBooleanBlockImpl(boolean v)throws IOException
	{
		 stream.add(BLK_BOOLEAN.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_BYTE}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeByteBlockImpl(byte v)throws IOException
	{
		 stream.add(BLK_BYTE.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_CHAR}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeCharBlockImpl(char v)throws IOException
	{
		 stream.add(BLK_CHAR.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_SHORT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeShortBlockImpl(short v)throws IOException
	{
		 stream.add(new BLK_SHORT(v));
	};
	/** Adds to a stream the instance of {@link BLK_INT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeIntBlockImpl(int v)throws IOException
	{
		 stream.add(new BLK_INT(v));
	};
	/** Adds to a stream the instance of {@link BLK_LONG}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeLongBlockImpl(long v)throws IOException
	{
		 stream.add(new BLK_LONG(v));
	};
	/** Adds to a stream the instance of {@link BLK_FLOAT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeFloatBlockImpl(float v)throws IOException
	{
		 stream.add(new BLK_FLOAT(v));
	};
	/** Adds to a stream the instance of {@link BLK_DOUBLE}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeDoubleBlockImpl(double v)throws IOException
	{
		 stream.add(new BLK_DOUBLE(v));
	};
	/** Adds to a stream the instance of {@link BLK_STRING}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeStringImpl(char c)throws IOException
	{
		 stream.add(BLK_STRING.valueOf(c));
	};
	/* ***********************************************************************
		
				IFormatLimits
				
		
	************************************************************************/
	@Override public final int getMaxSupportedSignalNameLength(){ return max_supported_name_length; };
	@Override public final int getMaxSupportedStructRecursionDepth(){ return max_supported_recursion_depth; };
};