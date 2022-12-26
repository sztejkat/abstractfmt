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
			/** Physical end of file.
			<p>
			Carried class: {@link EEof}
			*/
			EOF(EEof.class,false,false,false,false),
			/** A signal, unspecified what kind. 
			<p>
			A proper method to use is {@link IStructReadFormat#next}.
			<p>
			Carried class: {@link Void#TYPE}*/
			SIG(Void.TYPE,false,false,false,true),
			/** An elementary primitive of boolean type.
			<p>
			A proper method to use is {@link IStructReadFormat#readBoolean}
			<p>
			Carried class: {@link Boolean#TYPE} */
			BOOLEAN(Boolean.TYPE,true,true,false,false),
			/** An elementary primitive of byte type.
			<p>
			A proper method to use is {@link IStructReadFormat#readByte}
			<p>
			Carried class: {@link Byte#TYPE}*/
			BYTE(Byte.TYPE,true,true,false,false),
			/** An elementary primitive of char type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readChar}
			<p>
			Carried class: {@link Character#TYPE}*/
			CHAR(Character.TYPE,true,true,false,false),
			/** An elementary primitive of short type.			
			<p>
			A proper method to use is {@link IStructReadFormat#readShort}
			<p>
			Carried class: {@link Short#TYPE}*/
			SHORT(Short.TYPE,true,true,false,false),
			/** An elementary primitive of int type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readInt}
			<p>
			Carried class: {@link Integer#TYPE}*/
			INT(Integer.TYPE,true,true,false,false),
			/** An elementary primitive of long type.
			<p>
			A proper method to use is {@link IStructReadFormat#readLong}
			<p>
			Carried class: {@link Long#TYPE}*/
			LONG(Long.TYPE,true,true,false,false),
			/** An elementary primitive of float type.
			<p>
			A proper method to use is {@link IStructReadFormat#readFloat}
			<p>
			Carried class: {@link Float#TYPE}*/
			FLOAT(Float.TYPE,true,true,false,false),
			/** An elementary primitive of double type.
			<p>
			A proper method to use is {@link IStructReadFormat#readDouble}
			<p>
			Carried class: {@link Double#TYPE}*/
			DOUBLE(Double.TYPE,true,true,false,false),
			/** A primitive block of boolean type.
			<p>
			A proper method to use is {@link IStructReadFormat#readBooleanBlock}
			<p>
			Carried class: <code>boolean[].class</code>*/
			BOOLEAN_BLK(boolean[].class,true,false,true,false),
			/** A primitive block of byte type.
			<p>
			A proper method to use is {@link IStructReadFormat#readByteBlock}
			<p>
			Carried class: <code>byte[].class</code>*/
			BYTE_BLK(byte[].class,true,false,true,false),
			/** A primitive block of char type.
			<p>
			A proper method to use is {@link IStructReadFormat#readCharBlock}
			<p>
			Carried class: <code>char[].class</code>*/
			CHAR_BLK(char[].class,true,false,true,false),
			/** A primitive block of short type.
			<p>
			A proper method to use is {@link IStructReadFormat#readShortBlock}
			<p>
			Carried class: <code>short[].class</code>*/
			SHORT_BLK(short[].class,true,false,true,false),
			/** A primitive block of int type.
			<p>
			A proper method to use is {@link IStructReadFormat#readIntBlock}
			<p>
			Carried class: <code>int[].class</code>*/
			INT_BLK(int[].class,true,false,true,false),
			/** A primitive block of long type.
			<p>
			A proper method to use is {@link IStructReadFormat#readLongBlock}
			<p>
			Carried class: <code>long[].class</code>*/
			LONG_BLK(long[].class,true,false,true,false),
			/** A primitive block of float type.
			<p>
			A proper method to use is {@link IStructReadFormat#readFloatBlock}
			<p>
			Carried class: <code>float[].class</code>*/
			FLOAT_BLK(float[].class,true,false,true,false),
			/** A primitive block of double type.
			<p>
			A proper method to use is {@link IStructReadFormat#readDoubleBlock}
			<p>
			Carried class: <code>double[].class</code>*/
			DOUBLE_BLK(double[].class,true,false,true,false),
			/** A primitive block of String type.
			<p> 
			A proper method to use is {@link IStructReadFormat#readString}
			<p>
			Carried class: <code>String.class</code>*/
			STRING_BLK(String.class,true,false,true,false),
			/** If this constant is returned it means, that the stream is an extension of {@link ITypedStructReadFormat}
			and is carrying an extended set of elements. This is up to this extension to declare own enum set and
			an apropriate method to peek for such elements and read them.
			<p>
			Carried class: <code>null</code>*/
			EXTENDED(null,false,false,false,false);
			
				/** Optional class representing Java class of that element. */
				public final Class<?> element_java_class;
				/** True if it is either elementary or block primitive */
				public final boolean is_primitive;
				/** True if it is an elementary primitive */
				public final boolean is_elementary_primitive;
				/** True if it is an block primitive */
				public final boolean is_block_primitive;
				/** True if it is a signal */
				public final boolean is_signal;
				
			private TElement(Class<?> element_java_class,
							boolean is_primitive,
							boolean is_elementary_primitive,
							boolean is_block_primitive,
							boolean is_signal
							)
			{
				this.element_java_class=element_java_class;
				this.is_primitive=is_primitive;
				this.is_elementary_primitive=is_elementary_primitive;
				this.is_block_primitive=is_block_primitive;
				this.is_signal=is_signal;
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
		@return what kind of element is available next in stream.
		@throws IOException if failed. This method is:
		<ul>
			<li>not allowed to throw {@link EEof} except if it encountered a reason to throw the {@link EUnexpectedEof}/
			{@link ETemporaryEndOfFile} inside a part of data which it needed to parse during deducing what is next in stream;</li>
			<li>is <u>not</u> allowed to throw {@link EFormatBoundaryExceeded} if above parsing detected it but is 
			required to postpone it till an apropriate method is called. Since the format declares stream boundaries
			for recursion depth and signal name length only throwing this exception should be postponed till nearest {@link #next};
			</li>
			<li>is allowed to throw {@link EBrokenFormat} if above parsing detected it;</li>
		</ul>
	*/
	public TElement peek()throws IOException;
};