package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.AStructWriteFormatBase0;
import sztejkat.abstractfmt.IFormatLimits;
import sztejkat.abstractfmt.logging.SLogging;
import sztejkat.abstractfmt.utils.IAddable;
import sztejkat.abstractfmt.utils.CAddablePollableArrayList;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
/**
	Format writing {@link IObjStructFormat0} stream,
	the variant which writes objects which do refuse
	conversion to other types than their own.
	<p>
	Use this format to check if scenarios which 
	are using struct format are not abusing it even
	if there is no type information to be expected
	in there. Notice, in 99% of cases wrapping the
	stream in typed wraper would be enough but there
	are subtle differences which may escape in such
	case due to the fact that typed writer will inject
	some internal signals.
	<p>
	Used strict forms will throw {@link EAbusedFormat}
	exception but in such a way, that reading stream won't
	be able to recover from it.
	
	@see CObjStructReadFormat1
*/
public class CStrictObjStructWriteFormat1 extends CObjStructWriteFormat1 
{			

	/** Creates	
	@param end_begin_enabled if true the {@link SIG_END_BEGIN} is used
		to implement {@link #endBeginImpl}. If false default implementation
		is left and a pair of signals {@link SIG_END}+{@link SIG_BEGIN} is used.
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
	@param name_registry_capacity capactity of name registry used	
			to support {@link #optimizeBeginName}. Zero to disable optimization.
	@param stream a stream to add data to.
    */
	public CStrictObjStructWriteFormat1(boolean end_begin_enabled,
								  int max_supported_recursion_depth,
								  int max_supported_name_length,
								  int name_registry_capacity,
								  IAddable<IObjStructFormat0> stream
								  )
	{
		super(end_begin_enabled,max_supported_recursion_depth,max_supported_name_length,name_registry_capacity,stream);
	};
	/** Creates, using {@link CAddablePollableArrayList} as a stream back-end  
		and sets it to {@link #stream}.
	@param end_begin_enabled if true the {@link SIG_END_BEGIN} is used
		to implement {@link #endBeginImpl}. If false default implementation
		is left and a pair of signals {@link SIG_END}+{@link SIG_BEGIN} is used.
	@param max_supported_recursion_depth see {@link IFormatLimits#getMaxSupportedStructRecursionDepth}
	@param name_registry_capacity capactity of name registry used	
			to support {@link #optimizeBeginName}. Zero to disable optimization.
	@param max_supported_name_length see {@link IFormatLimits#getMaxSupportedSignalNameLength}
    */
	public CStrictObjStructWriteFormat1(boolean end_begin_enabled,
								  int max_supported_recursion_depth,
								  int max_supported_name_length,
								  int name_registry_capacity
								  )
	{
		this(
			end_begin_enabled,
			max_supported_recursion_depth,
			max_supported_name_length,
			name_registry_capacity,
			new CAddablePollableArrayList<IObjStructFormat0>()
			);
			
	};
	/* ***********************************************************************
		
				AStructWriteFormatBase0
				
		
	************************************************************************/
	
	/** Adds to a stream the instance of {@link ELMT_BOOLEAN} */
	@Override protected void writeBooleanImpl(boolean v)throws IOException
	{
		stream.add(Strict_ELMT_BOOLEAN.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_BYTE} */
	@Override protected void writeByteImpl(byte v)throws IOException
	{
		stream.add(Strict_ELMT_BYTE.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_CHAR} */
	@Override protected void writeCharImpl(char v)throws IOException
	{
		stream.add(Strict_ELMT_CHAR.valueOf(v));
	};
	/** Adds to a stream the instance of {@link ELMT_SHORT} */
	@Override protected void writeShortImpl(short v)throws IOException
	{
		stream.add(new Strict_ELMT_SHORT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_INT} */
	@Override protected void writeIntImpl(int v)throws IOException
	{
		stream.add(new Strict_ELMT_INT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_LONG} */
	@Override protected void writeLongImpl(long v)throws IOException
	{
		stream.add(new Strict_ELMT_LONG(v));
	};
	/** Adds to a stream the instance of {@link ELMT_FLOAT} */
	@Override protected void writeFloatImpl(float v)throws IOException
	{
		stream.add(new Strict_ELMT_FLOAT(v));
	};
	/** Adds to a stream the instance of {@link ELMT_DOUBLE} */
	@Override protected void writeDoubleImpl(double v)throws IOException
	{
		stream.add(new Strict_ELMT_DOUBLE(v));
	};
	/** Adds to a stream the instance of {@link BLK_BOOLEAN}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeBooleanBlockImpl(boolean v)throws IOException
	{
		 stream.add(Strict_BLK_BOOLEAN.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_BYTE}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeByteBlockImpl(byte v)throws IOException
	{
		 stream.add(Strict_BLK_BYTE.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_CHAR}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeCharBlockImpl(char v)throws IOException
	{
		 stream.add(Strict_BLK_CHAR.valueOf(v));
	};
	/** Adds to a stream the instance of {@link BLK_SHORT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeShortBlockImpl(short v)throws IOException
	{
		 stream.add(new Strict_BLK_SHORT(v));
	};
	/** Adds to a stream the instance of {@link BLK_INT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeIntBlockImpl(int v)throws IOException
	{
		 stream.add(new Strict_BLK_INT(v));
	};
	/** Adds to a stream the instance of {@link BLK_LONG}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeLongBlockImpl(long v)throws IOException
	{
		 stream.add(new Strict_BLK_LONG(v));
	};
	/** Adds to a stream the instance of {@link BLK_FLOAT}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeFloatBlockImpl(float v)throws IOException
	{
		 stream.add(new Strict_BLK_FLOAT(v));
	};
	/** Adds to a stream the instance of {@link BLK_DOUBLE}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeDoubleBlockImpl(double v)throws IOException
	{
		 stream.add(new Strict_BLK_DOUBLE(v));
	};
	/** Adds to a stream the instance of {@link BLK_STRING}.
	Note: Array paremeterized block write is implement by a sequence
	of item writes. */
	@Override protected void writeStringImpl(char c)throws IOException
	{
		 stream.add(Strict_BLK_STRING.valueOf(c));
	};
	
};