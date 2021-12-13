package sztejkat.abstractfmt;

/**
	Represents "indicators" which are used to implement
	signals, type information and state reporting.
	<p>
	At low level the stream format is defined by 
	the sequence of indicators interlaved with data.
	<p>
	For details of when which indicator may appear
	check {@link IIndicatorWriteFormat}.
*/
public enum TIndicator
{
		/*--------------------------------------------
			Stream state, read only indicators.
		---------------------------------------------*/
		/** 
		 This indicator is to be returned from stream
		 indicator querry to inform that there is
		 physically no more data in stream, altough it
		 may be a temporary condition. 
		 <p>
		 This is read only indicator and is NOT written
		 to a stream.
		*/
		EOF(TIndicator.STATUS+TIndicator.READ_ONLY),
		/** 
		 This indicator is to be returned from stream
		 indicator querry to inform that there are data
		 under a cursor and no indicator information
		 is present in this place.
		 <p>
		 This is read only indicator and is NOT written
		 to a stream.
		*/		
		DATA(TIndicator.STATUS+TIndicator.READ_ONLY),
		
		/*--------------------------------------------
			Signals, read-write indicators.
		---------------------------------------------*/
		/** This indicator is written to a stream
		when "begin" signal is stored with it's name
		written directly to a stream.
		<p>
		This indicator is tightly bound with 
		with a signal name which should be avaialable
		to readers after this indicator is read from
		stream
		*/ 
		BEGIN_DIRECT(TIndicator.SIGNAL+TIndicator.NAME+TIndicator.IS_BEGIN),		
		/** This indicator is written to a stream
		when "begin" signal is stored with it's name
		written directly to a stream together
		with a number which can be the used to quickly
		write a "begin" signal with {@link #BEGIN_USE}
		or {@link #END_BEGIN_USE}
		<p>
		This indicator is tightly bound with 
		with a signal name and number 
		which both should be avaialable to readers 
		after this indicator is read from
		stream
		*/ 
		BEGIN_REGISTER(TIndicator.SIGNAL+TIndicator.NAME+TIndicator.REGISTER+TIndicator.IS_BEGIN),
		/** This indicator is written to a stream
		when "begin" signal registered previously
		with {@link #BEGIN_REGISTER} or {@link #END_BEGIN_REGISTER}
		is to be used.
		<p> 
		This indicator is tightly bound with 
		with a signal number 
		which should be avaialable to readers 
		after this indicator is read from
		stream */
		BEGIN_USE(TIndicator.SIGNAL+TIndicator.REGISTER+TIndicator.IS_BEGIN+TIndicator.USE_REGISTER),
		/** Written to stream to indicated an end signal */
		END(TIndicator.SIGNAL+TIndicator.IS_END),
		/** Written to a stream to indicate {@link #END}
		followed by {@link #BEGIN_DIRECT} without anything
		in between */
		END_BEGIN_DIRECT(TIndicator.SIGNAL+TIndicator.NAME+TIndicator.IS_END+TIndicator.IS_BEGIN),
		/** Written to a stream to indicate {@link #END}
		followed by {@link #BEGIN_REGISTER} without anything
		in between */
		END_BEGIN_REGISTER(TIndicator.SIGNAL+TIndicator.NAME+TIndicator.REGISTER+TIndicator.IS_END+TIndicator.IS_BEGIN),
		/** Written to a stream to indicate {@link #END}
		followed by {@link #END_BEGIN_USE} without anything
		in between */
		END_BEGIN_USE(TIndicator.SIGNAL+TIndicator.REGISTER+TIndicator.IS_END+TIndicator.IS_BEGIN+TIndicator.USE_REGISTER),
		
		/** This indicator is written to mark, that a single
		elementary primitive of specified type do follow.
		<p>
		It is an optional indicator used by described streams.
		 */
		TYPE_BOOLEAN(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_BYTE(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_CHAR(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_SHORT(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_INT(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_LONG(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_FLOAT(TIndicator.TYPE+TIndicator.ELEMENT),
		/** See {@link #TYPE_BOOLEAN} */
		TYPE_DOUBLE(TIndicator.TYPE+TIndicator.ELEMENT),
		
		/** This indicator is written to mark, that a single
		elementary primitive of specified type just was
		read.
		<p>
		It is an optional indicator used by described streams
		which need to enclose the primitive from both sides.
		*/
		FLUSH_BOOLEAN(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_BYTE(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_CHAR(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_SHORT(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_INT(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_LONG(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_FLOAT(TIndicator.FLUSH+TIndicator.ELEMENT),
		/** See {@link #FLUSH_BOOLEAN} */
		FLUSH_DOUBLE(TIndicator.FLUSH+TIndicator.ELEMENT),
		
		
		/** This indicator is written to mark, that a block
		of primitives of specified type do follow.
		<p>
		It is an optional indicator used by described streams.
		*/		 
		TYPE_BOOLEAN_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_BYTE_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_CHAR_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_SHORT_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_INT_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_LONG_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_FLOAT_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		/** See {@link #TYPE_BOOLEAN_BLOCK} */
		TYPE_DOUBLE_BLOCK(TIndicator.TYPE+TIndicator.BLOCK),
		
		/** Like {@link #FLUSH_BOOLEAN} but for blocks */
		FLUSH_BOOLEAN_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_BYTE_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_CHAR_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_SHORT_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_INT_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_LONG_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_FLOAT_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		/** See {@link #FLUSH_BOOLEAN_BLOCK} */
		FLUSH_DOUBLE_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK),
		
		/** This indicator is read from stream if described
		stream needs to surround elementary primitives from
		both sides with some indicators, but the indicator
		is not type specifiec.
		<p>
		This is a read only indicator.
		*/
		FLUSH_ELEMENTARY(TIndicator.FLUSH+TIndicator.ELEMENT+TIndicator.READ_ONLY),
		/** This indicator is read from stream if described
		stream needs to surround blocks of primitives from
		both sides with some indicators, but the indicator
		is not type specifiec.
		<p>
		This is a read only indicator.
		*/
		FLUSH_BLOCK(TIndicator.FLUSH+TIndicator.BLOCK+TIndicator.READ_ONLY),
		/** This indicator is read from stream if described
		stream needs to surround blocks of primitives or elementary primitives from
		both sides with some indicators, but the indicator
		is neither type specifiec nor specific to block or primitive.
		<p>
		This is a read only indicator.
		*/
		FLUSH_ANY(TIndicator.FLUSH+TIndicator.ELEMENT+TIndicator.BLOCK+TIndicator.READ_ONLY);
		
		
		
				/** If set indicator describes "start of type"/"type information" */
				public static final int TYPE = 0x01;
				/** If set indicator describes "end of data"/"end of type information" */
				public static final int FLUSH = 0x02;
				/** If set indicator describes block primitive operation*/
				public static final int BLOCK = 0x04;
				/** If set indicator describes elementary primitive operation*/
				public static final int ELEMENT = 0x08;
				/** If set indicator describes signals management */
				public static final int SIGNAL = 0x10;				
				/** If set indicator describes stream status management */
				public static final int STATUS = 0x20;
				/** If set indicator describes indicator which do carry signal name*/
				public static final int NAME = 0x40;
				/** If set indicator describes indicator which do carry signal number*/
				public static final int REGISTER = 0x80;
				/** If set indicator is read-only and cannot be written to a stream.*/
				public static final int READ_ONLY = 0x100;
				/** If set indicator is one of end indicators */
				public static final int IS_END = 0x200;
				/** If set indicator is one of begin indicators */
				public static final int IS_BEGIN = 0x400;
				/** If set indicator describes indicator which do carry use of a signal number*/
				public static final int USE_REGISTER = 0x800;
				
				/** Combination of {@link #TYPE}, {@link #FLUSH}, {@link #BLOCK},
				{@link #ELEMENT}, {@link #SIGNAL}, {@link #STATUS},{@link #NAME},
				{@link #REGISTER},{@link #READ_ONLY},{@link #IS_END},{@link #IS_BEGIN}
				 */
				public final int FLAGS;
		
		private TIndicator(int FLAGS)
		{
			this.FLAGS = FLAGS;
		};
		
}