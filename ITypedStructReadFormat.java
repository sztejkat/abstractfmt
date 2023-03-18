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
	
	<h1>Extending type information</h1>
	The future development of structured format may provide some additional
	types which are not possible to express using the closed <code>enum</code>
	list of types which are defined in this class.
	<p>
	For an example user may decide to provide <code>IBigStructReadFormat</code> which
	will have dedicated API to handle big-decimals or big-integer types. That format
	will extend the <code>IStructReadFormat</code> what is a natural decission
	and will work superb.
	<p>
	The problem is how to efficiently extend the {@link ITypedStructReadFormat}? Especially
	how to retain compatibility with it <u>and</u> open a path to provide a <u>more detailed</u>
	information from {@link #peek}?
	<p>
	Using <code>enum</code> is very clear and nice solution, but the set of possible 
	enum values is closed. We can't extend enum so a subclass of <code>ITypedStructReadFormat</code>
	can't define own enum which extends <code>TElement</code>.
	<p>
	On the other end of a spectrum there is a plain list of named integer contants
	like, for an example: <code> public static final int SIG = 0;</code>. This soultion is <u>not closed</u>
	but can easily produce a "name clash" and user may easily mess up constants from different extended types.
	
	<h2>Using type information</h2>
	The obvious positive attribute of <code>enum</code> it is that when You are returning it from a
	function user clearly knows what to expect and how to react on it. Primarly because the set of returned
	values <u>is closed</u>. Never less the Java do expect You to use the <code>default:</code> switch-case cause:
	<pre>
	String actOnEnum(enum)
	{
		switch(enum)
		{
		   case A: return "on A"
		   ....
		   <b>default:...</b> return "on default"
		}
		<i>whithout default Java will complain about missing return statement here</i> 
	}
	</pre>
	From conceptual point of view it is pointless - enum set is closed, You can't have anything more than You 
	listed to why the hell <code>default</code>? Because of two things:
	<ul>
		<li>first the actual switch-case is implemented by:
		<pre>
			switch(enum.ordinal())
			{
			....
			}
		</pre>
		</li>
		<li>and the second - that Java linking is dynamic and the at the runtime it may
		happen that a class file where <code>enum</code> is declared might carry later or earlier
		definitin of <code>enum</code> that the one which existed when <code>switch(...)</code>
		was compiled.</li>
	</ul>
	This means that, conceptually speaking, every user of <code>enum type</code> <u>must be prepared</u>
	to see some enum constant which he/she did <u>not know about</u>.
	
	<h2>Extending enums</h2>
	There are three possible scenarios when You may wish to provide additional information 
	with enums:
	<ol>
		<li>Your additional information can be used to compute base information.
		For an example You may read Your detailed information two times and construct
		from it the base information.</li>
		<li>Your additional information is <u>more detailed</u> than base information.
		In such case You can alaway map two ore more constants representing
		Your new information to some constant from <code>enum</code> describing old information.
		</li>
		<li>And finally, when Your information is something totally new. It can't be 
		mapped to old one and can't be used to compute old one. Like our example 
		"next thing is a big decimal". There was no big decimals before and big decimal
		is neither elementary primitive nor a sequence of it. We simply can't call it using 
		old names.
		</li>
	</ol>
	First two cases can be easily handled by:
	<pre>
	interface IMyExtendedTypedStructReadFormat extends ITypedStructReadFormat
	{
		public enum TExtendedElement{....}
		
		public TElement peek() // use transformatins to produce old type
		public TExtendedElement extendedPeek() //just give extended type
	}
	</pre>
	The third case is trickier.
	
	<h2 id="EXTENDED_TYPE">Opening the path for extending type information</h2>
	This contract do open the path for the third case by making following steps:
	<ul>
		<li>the {@link #peek} is <u>allowed to</u> return <code>null</code> if it cannot express
			the information about what is inside a stream with {@link TElement}.
			Using null is a more clear signal that we have someting we don't 
			stand a chance to understand than having a dedicated constant;</li>
		<li>the extended type information is recommended to be declared like below:
		<pre>
			enum TExtendedElement
			{
				SIG(TElement.SIG),
				....
					//This can be null if mapping doesn't exist.
					public final TElement asTElement;
					
				TExtendedElement(TElement asTElement){ this.asTElement=asTElement; };
			}
		</pre>
		</li>
		<li>there is an extended type information API:
		<pre>
			default TElement peek(){ return extendedPeek.asTElement; } 
			TExtendedElement extendedPeek()
		</pre>
		which can be used in scenario:
		<pre>
			TExtendedElement e= extendedPeek();
			if (e.asTElement!=null) handleBaseInformation(e.asTElement)
			else
									handleExtended(e)
		</pre>
		or 
		<pre>
			TElement e = peek();
			if (e!=null)
			{
				switch(e)...
			}else
			{
				switch(extendedPeek())...
			}
		</pre>
		</li>
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
		@return what kind of operation on a stream is allowed. 
				Can return null if the information about what is next
				in stream cannot be expressed by {@link TElement} enum.
				<p>
				Null can be returned only by those implementations which
				do implement their own <a href="#EXTENDED_TYPE">extended type information.</a>.
				<p>
				Code which is not prepared for extended types is allowed to
				either <code>assert(peek()!=null)</code> or don't check for null
				at all.
		@throws IOException if failed. Includes all {@link EEof} ( but only
				when encountered where it should not be expected ), limits and broken
				format exceptions.
	*/
	public TElement peek()throws IOException;
};