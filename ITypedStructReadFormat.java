package sztejkat.abstractfmt;
import java.io.IOException;
/**
	An extension to {@link IStructReadFormat} which provides capabilities to
	peek what kind of element is next in a stream and validate if a propert
	method is used to pick it up.
	<p>
	It is a <i>fully described</i> format allowing <i>dumb transcoding</i>.
	See library overview for an explanation.
	
	<h1>Impact on methods</h1>
	This contract modifies contracts of all methods in following way:
	<ul>
		<li><i>{@link IFormatLimits}</i> - unaffected;</li>
		<li><i>{@link #next},{@link #skip}</i> - unaffected;</li>
		<li><i>{@link #open},{@link #close}</i> - unaffected;</li>
		<li><i>elementary primitive reads {@link #readBoolean} and etc.
		and block reads {@link #readBooleanBlock} and etc.</i> -
		do throw {@link ETypeMissmatch} if when element under
		cursor is not of an apropriate type to either initiate or continue operation
		<p>
		After throwing an exception the stream must behave as specified in {@link ETypeMissmatch}.
		</li>
		<li>the behavior of block end elementary primitive reads if 
		the block operation is already in progress is not affected;</li>
		<li>the behavior of block end elementary primitive reads if 
		the there is a signal at cursor is not affected;</li>
	</ul>
*/
public interface ITypedStructReadFormat extends IStructReadFormat
{
		/** Enumerates possible stream elements. */
		public enum TElement
		{
			/** A signal, unspecified what kind. 
			This value is <u>never</u> returned during
			a block read operation. Instead the block
			type is returned until the closing signal
			is not fetched by {@link IStructReadFormat#next}.
			It is done this way to clearly indicate that
			block operation is allowed.
			<p>
			A proper method to use is {@link IStructReadFormat#next}.
			<p>
			Carried class: {@link Void#TYPE}*/
			SIG(null),
			/** An elementary primitive of boolean type.
			<p>
			A proper method to use is {@link IStructReadFormat#readBoolean}
			<p>
			Carried class: {@link Boolean#TYPE} */
			BOOLEAN(Boolean.TYPE),
			/** An elementary primitive of byte type.
			<p>
			A proper method to use is {@link IStructReadFormat#readByte}
			<p>
			Carried class: {@link Byte#TYPE}*/
			BYTE(Byte.TYPE),
			/** An elementary primitive of char type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readChar}
			<p>
			Carried class: {@link Character#TYPE}*/
			CHAR(Character.TYPE),
			/** An elementary primitive of short type.			
			<p>
			A proper method to use is {@link IStructReadFormat#readShort}
			<p>
			Carried class: {@link Short#TYPE}*/
			SHORT(Short.TYPE),
			/** An elementary primitive of int type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readInt}
			<p>
			Carried class: {@link Integer#TYPE}*/
			INT(Integer.TYPE),
			/** An elementary primitive of long type.
			<p>
			A proper method to use is {@link IStructReadFormat#readLong}
			<p>
			Carried class: {@link Long#TYPE}*/
			LONG(Long.TYPE),
			/** An elementary primitive of float type.
			<p>
			A proper method to use is {@link IStructReadFormat#readFloat}
			<p>
			Carried class: {@link Float#TYPE}*/
			FLOAT(Float.TYPE),
			/** An elementary primitive of double type.
			<p>
			A proper method to use is {@link IStructReadFormat#readDouble}
			<p>
			Carried class: {@link Double#TYPE}*/
			DOUBLE(Double.TYPE),
			/** A primitive block of boolean type.
			<p>
			A proper method to use is {@link IStructReadFormat#readBooleanBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>boolean[].class</code>*/
			BOOLEAN_BLK(boolean[].class, true),
			/** A primitive block of byte type.
			<p>
			A proper method to use is {@link IStructReadFormat#readByteBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>byte[].class</code>*/
			BYTE_BLK(byte[].class, true),
			/** A primitive block of char type.
			<p>
			A proper method to use is {@link IStructReadFormat#readCharBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>char[].class</code>*/
			CHAR_BLK(char[].class, true),
			/** A primitive block of short type.
			<p>
			A proper method to use is {@link IStructReadFormat#readShortBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>short[].class</code>*/
			SHORT_BLK(short[].class, true),
			/** A primitive block of int type.
			<p>
			A proper method to use is {@link IStructReadFormat#readIntBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>int[].class</code>*/
			INT_BLK(int[].class, true),
			/** A primitive block of long type.
			<p>
			A proper method to use is {@link IStructReadFormat#readLongBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>long[].class</code>*/
			LONG_BLK(long[].class, true),
			/** A primitive block of float type.
			<p>
			A proper method to use is {@link IStructReadFormat#readFloatBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>float[].class</code>*/
			FLOAT_BLK(float[].class, true),
			/** A primitive block of double type.
			<p>
			A proper method to use is {@link IStructReadFormat#readDoubleBlock}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>double[].class</code>*/
			DOUBLE_BLK(double[].class, true),
			/** A primitive block of String type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readString}.
			Also returned if there are no more data in block, since block read
			is allowed in that condition.
			<p>
			Carried class: <code>String.class</code>*/
			STRING_BLK(String.class, true),
			/** When {@link #peek()} touched end-of file in a place
			in which it was allowed. This value is <u>not</u> returned
			and an exception is thrown instead if end-of-file happens
			inside some type information control data.
			<p>
			As a rule of thumb it is allowed to be returned 
			if there are no data in block or elementary element but there
			is no "end" signal or if <code>next()</code> called in that 
			context would have thrown an {@link EEof}.*/
			EOF(null,false);
			
				/** Optional class representing Java class of that element.
				Null for {@link #SIG}*/
				public final Class<?> element_java_class;
				/** Declared to speed up processing 
				since blocks needs to be treated in slightly
				different way during implementation. 
				<p>
				True if type represents a block operation.
				*/
				public final boolean is_block; 
				
			private TElement(Class<?> element_java_class)
			{
				this(element_java_class,false);
			};
			private TElement(Class<?> element_java_class, boolean is_block)
			{
				this.element_java_class=element_java_class;
				this.is_block = is_block;
			};
			
		}
		
	/**
		Checks, with a minimum possible impact on an underlying raw stream,
		what kind of stream element is present at cursor position and thous what kind of method is
		allowed to be used next.
		<p>
		This method <u>is allowed</u> to read-ahead some data and, if necessary, parse them,
		but the logic cursor must be left un-moved and:
		<pre>
			for(;;){ peek()==peek(); };
		</pre>
		must not throw ever.
		@return what kind of operation on a stream is allowed. Non null.
		@throws IOException if failed. Includes all {@link EEof}, but only
				when encountered where it should not be expected , limits and broken
				format exceptions.
	*/
	public TElement peek()throws IOException;
};