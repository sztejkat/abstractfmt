package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;


/**
	Extends {@link ISignalReadFormat} to a <a href="package.html#fullydescribed">
	<i>fully described</i></a> format.	
	<p>
	All methods must throw {@link EDataMissmatch}
	if this read operation breaks <a href="package.html#uninterruptible"><i>un-interruptability</i></a>
	rules, that is data found in stream do not match the type expected by this 
	call, a read inside other data is made, format is incorrect end etc. 
*/
public interface IDescribedSignalReadFormat extends ISignalReadFormat
{
		/* ----------------------------------------------------------------
				Contract clarification
		-------------------------------------------------------------------*/
		/** Applies to all other elementary and block reads.
		In addition to what {@link ISignalReadFormat#readBoolean} describes
		those method do:
		@throws EDataMissmatch if this read operation breaks <i>un-interruptability</i>
				rules, that is data found in stream do not match the type expected by this 
				call 
		*/
		public boolean readBoolean()throws IOException;
		/* ----------------------------------------------------------------
				Contract specifics, types of elements.
		-------------------------------------------------------------------*/
				
				/** Indicates that next element in stream is an elementary primitive
				which should be processed by {@link #readBoolean} */
				public static final int PRMTV_BOOLEAN=1;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readByte} */
				public static final int PRMTV_BYTE=2;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readChar} */
				public static final int PRMTV_CHAR=3;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readShort} */
				public static final int PRMTV_SHORT=4;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readInt} */
				public static final int PRMTV_INT=5;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readLong} */
				public static final int PRMTV_LONG=6;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readFloat} */
				public static final int PRMTV_FLOAT=7;
				/** Indicates that next PRMTV in stream is an PRMTVary primitive
				which should be processed by {@link #readDouble} */
				public static final int PRMTV_DOUBLE=8;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readBooleanBlock}.*/
				public static final int PRMTV_BOOLEAN_BLOCK=9;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readByteBlock}. */
				public static final int PRMTV_BYTE_BLOCK=10;				
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readCharBlock}. */
				public static final int PRMTV_CHAR_BLOCK=11;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readShortBlock}. */
				public static final int PRMTV_SHORT_BLOCK=12;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readIntBlock}. */
				public static final int PRMTV_INT_BLOCK=13;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readLongBlock}.*/
				public static final int PRMTV_LONG_BLOCK=14;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readFloatBlock}.*/
				public static final int PRMTV_FLOAT_BLOCK=15;
				/** Indicates that next PRMTV in stream is a block operation
				which should be processed by {@link #readDoubleBlock}.*/
				public static final int PRMTV_DOUBLE_BLOCK=16;
		/** 
			Extends the details returned from {@link ISignalReadFormat#hasData}
			to provide detailed informations about if has data to skip then
			what those data would actually be.
			@return one of:
				<ul>
					<li>{@link #EOF};</li>
					<li>{@link #SIGNAL};</li>
					<li>{@link #PRMTV_BOOLEAN};</li>
					<li>{@link #PRMTV_BYTE};</li>
					<li>{@link #PRMTV_CHAR};</li>
					<li>{@link #PRMTV_SHORT};</li>
					<li>{@link #PRMTV_INT};</li>
					<li>{@link #PRMTV_LONG};</li>
					<li>{@link #PRMTV_FLOAT};</li>
					<li>{@link #PRMTV_DOUBLE};</li>
					<li>{@link #PRMTV_BYTE_BLOCK};</li>
					<li>{@link #PRMTV_BOOLEAN_BLOCK};</li>
					<li>{@link #PRMTV_CHAR_BLOCK};</li>
					<li>{@link #PRMTV_SHORT_BLOCK};</li>
					<li>{@link #PRMTV_INT_BLOCK};</li>
					<li>{@link #PRMTV_LONG_BLOCK};</li>
					<li>{@link #PRMTV_FLOAT_BLOCK};</li>
					<li>{@link #PRMTV_DOUBLE_BLOCK};</li>
				</ul>
				The effect should be such, that calling a read method which is
				apropriate to returned code must not throw any data-match or end-of-event
				related exceptions.
				
			@throws IOException if low level i/o failed, except of end-of-stream condition.
			@throws ECorruptedFormat if could not decode end signal due to other errors.
		*/
		public int hasData()throws IOException;
};