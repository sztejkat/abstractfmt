package sztejkat.abstractfmt;

/**
	Represents content type which can be found in 
	signal format stream.
*/
public enum TContentType
{
		/** Indicates that {@link ISignalReadFormat#next} would have thrown {@link EUnexpectedEof}
		if called right now. This condition may change if stream is
		a network connection or other produced-on-demand stream. This condition
		<u>must</u> appear if no data about next signal is present in stream, but
		is not expected to detect the possibility of an EOF inside a begin
		signal itself, ie. when reading a name of a signal.
		*/
		EOF(0),
		/** Indicates that {@link ISignalReadFormat#next} would not skip anything
		and would return a signal, either end or begin*/
		SIGNAL(TContentType.CONTENT_SIGNAL),
		/** Indicates that next element in stream is an elementary primitive
			which should be processed by {@link ISignalReadFormat#readBoolean} */
		PRMTV_BOOLEAN(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readByte} */
		PRMTV_BYTE(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readChar} */
		PRMTV_CHAR(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readShort} */
		PRMTV_SHORT(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readInt} */
		 PRMTV_INT(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readLong} */
		 PRMTV_LONG(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readFloat} */
		 PRMTV_FLOAT(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next element in stream is an elementary primitive
		which should be processed by {@link ISignalReadFormat#readDouble} */
		 PRMTV_DOUBLE(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_ELEMENTARY),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readBooleanBlock}.*/
		 PRMTV_BOOLEAN_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readByteBlock}. */
		 PRMTV_BYTE_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),				
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readCharBlock}. */
		 PRMTV_CHAR_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readShortBlock}. */
		 PRMTV_SHORT_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readIntBlock}. */
		 PRMTV_INT_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readLongBlock}.*/
		 PRMTV_LONG_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readFloatBlock}.*/
		 PRMTV_FLOAT_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates that next PRMTV in stream is a block operation
		which should be processed by {@link ISignalReadFormat#readDoubleBlock}.*/
		 PRMTV_DOUBLE_BLOCK(TContentType.CONTENT_DATA+TContentType.CONTENT_TYPED+TContentType.CONTENT_TYPED_BLOCK),
		/** Indicates stread carries no type information about what
		is under cursor, but surely this is not a signal. */
		PRMTV_UNTYPED(TContentType.CONTENT_DATA);
				
		/** Set to indicate that content is carrying a signal, either begin or end */
		public static final int CONTENT_SIGNAL = 0x01;
		/** Set to indicate that content is carrying information about data, typed or untyped */
		public static final int CONTENT_DATA = 0x02;
		/** Set to indicate that content is carrying information about data, typed  */
		public static final int CONTENT_TYPED= 0x04;
		/** Set to indicate that content is carrying information about data, typed, elementary primitive  */
		public static final int CONTENT_TYPED_ELEMENTARY = 0x08;
		/** Set to indicate that content is carrying information about data, typed, block primitive  */
		public static final int CONTENT_TYPED_BLOCK = 0x10;
		
		/** Combination of constants telling about what content type is. */
		public final int FLAGS;
		
		
	private TContentType(int f ){ this.FLAGS = f; };
	
	/** Quick check for {@link #CONTENT_DATA} bit
	@return true if bit is set.
	*/
	public boolean isData(){ return ( FLAGS & CONTENT_DATA )!=0; };  
};