package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
/**
	The <i>typed</i> stream implementing {@link ITypedStructWriteFormat}.
	<p>
	This stream is implemented over the {@link AReservedNameWriteFormat}.
	
	<h1>Use of reserved names</h1>
	This stream do reserve following names:
	<pre id="default_reserved_names_table">
		bool
		byte
		char
		short
		int 
		long
		float
		double
		str
		bool[]
		byte[]
		char[]
		short[]
		int[]
		long[]
		float[]
		double[]		
	</pre>
	with {@link AReservedNameWriteFormat#reserveName}
	and requests to optimize them by calling {@link IStructWriteFormat#optimizeBeginName}
	on the underlying stream. The call is made in order of appearance on above list.
	<p>
	<i>Note: This set may be customized by an apropriate constructor argument
	what may be wise if XML format is used as a back-end due to some of them not
	being valid XML tag names.</i>
	
	<h2>Describing primitive elementary operations</h2>
	If an allowed primitive elementary operation is initialized
	this class tests if:
	<ul>
		<li>there was already primitive elementary operation since last user
		signal;</li>
		<li>said operation is same type;</li>
	</ul>
	If it is, nothing is done.
	<p>
	If it is not:
	<ul>
		<li>if there was an elementary primitive operation of another type
		the <code>end</code> signal is written to a stream;</li>
		<li>then the <code>begin</code> signal is written 
		with {@link AReservedNameWriteFormat#beginReserved} and a name matching the type.
	</ul>
	
	<h2>Describing primitive block operations</h2>
	Alike rules do apply, but since no block operation type can be changed the set 
	of choices is limited.
	
	<h1>Effect and examples</h1>
	In effect the sequnece of elementary primitive operations of the same type
	is enclosed by <code>begin(type_name)</code> and <code>end</code> signals.
	<p>
	For an example if user writes:
	<pre>
		begin("mystruct")
		writeInt(33)
		writeInt(34)
		writeFloat(0.1f)
		end()
	</pre>
	the underlying stream will receive:
	<pre>
		begin("mystruct")
			begin("int")
		writeInt(33)
		writeInt(34)
			end();
			begin("float");
		writeFloat(0.1f)
			end();
		end()
	</pre>
	And alike, for block sequence it will be like below:
	<br>
	User writes:
	<pre>
		begin("mystruct")
		writeIntBlock(new int[100])
		writeIntBlock(new int[100])
		end()
	</pre>
	the underlying stream will receive:
	<pre>
		begin("mystruct")
			begin("int[]")
		writeIntBlock(new int[100])
		writeIntBlock(new int[100])
			end()
		end()
	</pre>
	Notice a slightly unefficient double <code>end()</code> for block operations. This cost is intentional
	due to greater simplicity of encoding and a necessity to ensure that blocks are properly terminated
	even in case of un-typed stream.
	<p>
	Theoretically the side effect <i>could be</i> such that a typed stream <i>could</i> allow:
	<pre>
		writeIntBlock(...)
		writeLongBlock(...)
	</pre>
	due to injected <code>end</code> signals but I will directly <code>prohibit it</code> for sake of
	contract clarity.
	
	<h1>Design notes</h1>
	This class do slightly duplicate the state machine of {@link AStructFormatBase}.
	This is intentional.
*/
public class CTypedStructWriteFormat extends AReservedNameWriteFormat implements ITypedStructWriteFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(CTypedStructWriteFormat.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("CTypedStructWriteFormat.",CTypedStructWriteFormat.class) : null;
 
			/** An index used for reserved "bool" name in type names table
			@see #type_names
			*/
			public static final int BOOLEAN_idx = 0;
			/** An index used for reserved "byte" name in type names table
			@see #type_names
			*/
			public static final int BYTE_idx = 1;
			/** An index used for reserved "char" name in type names table
			@see #type_names
			*/
			public static final int CHAR_idx = 2;
			/** An index used for reserved "short" name in type names table
			@see #type_names
			*/
			public static final int SHORT_idx = 3;
			/** An index used for reserved "int" name in type names table
			@see #type_names
			*/
			public static final int INT_idx = 4;
			/** An index used for reserved "long" name in type names table
			@see #type_names
			*/
			public static final int LONG_idx = 5;
			/** An index used for reserved "float" name in type names table
			@see #type_names
			*/
			public static final int FLOAT_idx = 6;
			/** An index used for reserved "double" name in type names table
			@see #type_names
			*/
			public static final int DOUBLE_idx = 7;
			
			//Important: all blocks above STRING_blk_idx. See validation code.
			/** An index used for reserved "str" name in type names table
			@see #type_names
			*/
			public static final int STRING_blk_idx = 8;
			
			/** An index used for reserved "bool[]" name in type names table
			@see #type_names
			*/
			public static final int BOOLEAN_blk_idx = 9;
			/** An index used for reserved "byte[]" name in type names table
			@see #type_names
			*/
			public static final int BYTE_blk_idx = 10;
			/** An index used for reserved "char[]" name in type names table
			@see #type_names
			*/
			public static final int CHAR_blk_idx = 11;
			/** An index used for reserved "short[]" name in type names table
			@see #type_names
			*/
			public static final int SHORT_blk_idx = 12;
			/** An index used for reserved "int[]" name in type names table
			@see #type_names
			*/
			public static final int INT_blk_idx = 13;
			/** An index used for reserved "long[]" name in type names table
			@see #type_names
			*/
			public static final int LONG_blk_idx = 14;
			/** An index used for reserved "float[]" name in type names table
			@see #type_names
			*/
			public static final int FLOAT_blk_idx = 15;
			/** An index used for reserved "double[]" name in type names table
			@see #type_names
			*/
			public static final int DOUBLE_blk_idx = 16;
			
			/** Default value to be used for {@link #type_names} */
			public static final String [] DEFAULT_TYPE_NAMES =
			{
				//Note: meaning must match the XXX_idx constants.
						"bool",
						"byte",
						"char",
						"short",
						"int",
						"long",
						"float",
						"double",
						"str",
						"bool[]",
						"byte[]",
						"char[]",
						"short[]",
						"int[]",
						"long[]",
						"float[]",
						"double[]"	
			};
		
				/** An immutable array indexed by <code>XXX_idx</code> constants
				used to fetch type names. */
				private final String [] type_names;
				/** is carrying <code>XXXX_idx</code> operation code in progress
				or -1 if there is no operation in progress. */
				private int current_type;
				/** Tracks format open state */
				private boolean opened;
				/** Tracks format closes state */
				private boolean closed;
				
		/* *************************************************************************
		
		
				Construction
		
		
		**************************************************************************/
		/** Creates
		@param engine an underlying low level engine, see {@link AStructWriteFormatAdapter#AStructWriteFormatAdapter}
		@param escape an escape character. This character must not be a first character of any type name
		@param type_names an array of names of types, indexed by <code>XXXX_idx</code> constants.
				Immutable, taken not copied, cannot be null, can't contain null.
		*/
		public CTypedStructWriteFormat(
							IStructWriteFormat engine, 
							char escape,
							String [] type_names
							)
		{
			super(engine, escape);
			assert(assertTypeNames(type_names));
			this.type_names = type_names;
			//reserve them, order doesn't matter.
			for(int i = type_names.length; --i>=0;)
				reserveName(type_names[i]);
			//no type info
			current_type = -1;
		};
		/** Validates constructor parameter in assertion mode
		@param type_names constructor parameter
		@return always true, but may assert before returning.
		*/
		private static boolean assertTypeNames(String [] type_names)
		{
			assert(type_names!=null);
			assert(type_names.length==1+DOUBLE_blk_idx):"type_names.length="+type_names.length;
			for(int i = type_names.length; --i>=0;)
			{
				assert(type_names[i]!=null):"null name at type_names["+i+"]";
			};
			return true;
		};
		/* ***********************************************************************
		
				Internal services
		
		
		************************************************************************/
		/* ----------------------------------------------------------------
					State validation
		----------------------------------------------------------------*/
		/** Throws if closed 
		@throws EClosed .*/
		private final void validateNotClosed()throws EClosed
		{
			if (closed) throw new EClosed("Already closed");
		};
		/** Throws if not opened 
		@throws ENotOpen .*/
		private final void validateOpen()throws ENotOpen
		{
			if (!opened) throw new ENotOpen("Not open yet");
		};
		/** Calls {@link #validateOpen} and {@link #validateNotClosed}
		@throws EClosed if already closed
		@throws ENotOpen if not open yet.
		*/
		private final void validateUsable()throws EClosed,ENotOpen
		{
			validateOpen();
		 	validateNotClosed();
		};
		/* *************************************************************************
		
		
				IStructWriteFormat
		
		
		**************************************************************************/
		/* ----------------------------------------------------------------------
		
				Signals
		
		----------------------------------------------------------------------*/
		/** Terminates pending type information, if any 
		@throws IOException if failed.
		*/
		private void closePendingTypeInfo()throws IOException
		{
			if (current_type!=-1)
			{
				if (TRACE) TOUT.println("closePendingTypeInfo(), closing pending type info.");
				current_type=-1;
				super.end();
			}else
			{
				if (TRACE) TOUT.println("closePendingTypeInfo(), no pending type info.");
			};
		};
		/** Overriden to manage type info */ 
		@Override public void begin(String name)throws IOException
		{
			if (TRACE) TOUT.println("begin(\""+name+"\") ENTER");
			validateUsable();
			//clean-up type info if present
			closePendingTypeInfo();
			//do it.
			super.begin(name);
			if (TRACE) TOUT.println("begin() LEAVE");
		};
		/** Overriden to manage type info */
		@Override public void end()throws IOException
		{
			if (TRACE) TOUT.println("end() ENTER");
			validateUsable();
			//clean-up type info if present
			closePendingTypeInfo();
			super.end();
			if (TRACE) TOUT.println("end() LEAVE");
		};
		/* ----------------------------------------------------------------------
		
				Elementary ops
		
		----------------------------------------------------------------------*/
		/** Makes sure that type info describes what is specified by passed constant
		@param XXX_idx type constant 
		@throws IOException if failed. 
		*/
		private void validateTypeInfo(int XXX_idx)throws IOException
		{
			if (TRACE) TOUT.println("validateTypeInfo("+XXX_idx+") ENTER");
			assert(XXX_idx>=BOOLEAN_idx);
			assert(XXX_idx<=DOUBLE_blk_idx);
			validateUsable();
			int ct = current_type ; //fast access.
			if (ct!=XXX_idx)
			{
				//different
				if (TRACE) TOUT.println("validateTypeInfo, switching type");
				closePendingTypeInfo();	
				super.beginReserved(type_names[XXX_idx]);
				current_type = XXX_idx;
			};
			if (TRACE) TOUT.println("validateTypeInfo("+XXX_idx+") LEAVE");
		};
		/** Makes sure that type info describes what is specified by passed constant
		AND that said elementary operation is currently allowed.
		@param XXX_idx type constant, elementary primitive operation.
		@throws IOException if failed. 
		*/
		private void validateElementaryTypeInfo(int XXX_idx)throws IOException
		{
			assert(XXX_idx>=BOOLEAN_idx);
			assert(XXX_idx<STRING_blk_idx);
			int ct = current_type;
			if (ct>=STRING_blk_idx) //this simple check is enough, all blocks are above it.
				throw new IllegalStateException("blok operation "+type_names[ct]+" in progress");
			validateTypeInfo(XXX_idx);
		};
		/** Makes sure that type info describes what is specified by passed constant
		AND that said block operation is currently allowed.
		@param XXX_idx type constant, primitive block operation.
		@throws IOException if failed. 
		*/
		private void validateBlockTypeInfo(int XXX_idx)throws IOException
		{
			assert(XXX_idx>=STRING_blk_idx);
			assert(XXX_idx<=DOUBLE_blk_idx);
			int ct = current_type;
			if ((ct>=STRING_blk_idx)&&(ct!=XXX_idx)) //this simple check is enough to reject non-matching blocks.
				throw new IllegalStateException("confliciting blok operation "+type_names[ct]+" in progress");
			validateTypeInfo(XXX_idx);
		};
		/* ..............................................................................
		
				Primitive ops.
				
		..............................................................................*/
		/** Overriden to inject type information */
		@Override public void writeBoolean(boolean v)throws IOException
		{
			validateElementaryTypeInfo(BOOLEAN_idx);
			super.writeBoolean(v);
		};
		/** Overriden to inject type information */
		@Override public void writeByte(byte v)throws IOException
		{
			validateElementaryTypeInfo(BYTE_idx);
			super.writeByte(v);
		};
		/** Overriden to inject type information */
		@Override public void writeChar(char v)throws IOException
		{
			validateElementaryTypeInfo(CHAR_idx);
			super.writeChar(v);
		};
		/** Overriden to inject type information */
		@Override public void writeShort(short v)throws IOException
		{
			validateElementaryTypeInfo(SHORT_idx);
			super.writeShort(v);
		};
		/** Overriden to inject type information */
		@Override public void writeInt(int v)throws IOException
		{
			validateElementaryTypeInfo(INT_idx);
			super.writeInt(v);
		};
		/** Overriden to inject type information */
		@Override public void writeLong(long v)throws IOException
		{
			validateElementaryTypeInfo(LONG_idx);
			super.writeLong(v);
		};
		/** Overriden to inject type information */
		@Override public void writeFloat(float v)throws IOException
		{
			validateElementaryTypeInfo(FLOAT_idx);
			super.writeFloat(v);
		};
		/** Overriden to inject type information */
		@Override public void writeDouble(double v)throws IOException
		{
			validateElementaryTypeInfo(DOUBLE_idx);
			super.writeDouble(v);
		};
		/* ..............................................................................
		
				block ops.
				
		..............................................................................*/
		/** Overriden to inject type information */
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(BOOLEAN_blk_idx);
			super.writeBooleanBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeBooleanBlock(boolean [] buffer)throws IOException
		{
			validateBlockTypeInfo(BOOLEAN_blk_idx);
			super.writeBooleanBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeBooleanBlock(boolean v)throws IOException
		{
			validateBlockTypeInfo(BOOLEAN_blk_idx);
			super.writeBooleanBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeByteBlock(byte [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(BYTE_blk_idx);
			super.writeByteBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeByteBlock(byte [] buffer)throws IOException
		{
			validateBlockTypeInfo(BYTE_blk_idx);
			super.writeByteBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeByteBlock(byte v)throws IOException
		{
			validateBlockTypeInfo(BYTE_blk_idx);
			super.writeByteBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeCharBlock(char [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(CHAR_blk_idx);
			super.writeCharBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeCharBlock(char [] buffer)throws IOException
		{
			validateBlockTypeInfo(CHAR_blk_idx);
			super.writeCharBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeCharBlock(char v)throws IOException
		{
			validateBlockTypeInfo(CHAR_blk_idx);
			super.writeCharBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeShortBlock(short [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(SHORT_blk_idx);
			super.writeShortBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeShortBlock(short [] buffer)throws IOException
		{
			validateBlockTypeInfo(SHORT_blk_idx);
			super.writeShortBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeShortBlock(short v)throws IOException
		{
			validateBlockTypeInfo(SHORT_blk_idx);
			super.writeShortBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeIntBlock(int [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(INT_blk_idx);
			super.writeIntBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeIntBlock(int [] buffer)throws IOException
		{
			validateBlockTypeInfo(INT_blk_idx);
			super.writeIntBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeIntBlock(int v)throws IOException
		{
			validateBlockTypeInfo(INT_blk_idx);
			super.writeIntBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeLongBlock(long [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(LONG_blk_idx);
			super.writeLongBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeLongBlock(long [] buffer)throws IOException
		{
			validateBlockTypeInfo(LONG_blk_idx);
			super.writeLongBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeLongBlock(long v)throws IOException
		{
			validateBlockTypeInfo(LONG_blk_idx);
			super.writeLongBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeFloatBlock(float [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(FLOAT_blk_idx);
			super.writeFloatBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeFloatBlock(float [] buffer)throws IOException
		{
			validateBlockTypeInfo(FLOAT_blk_idx);
			super.writeFloatBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeFloatBlock(float v)throws IOException
		{
			validateBlockTypeInfo(FLOAT_blk_idx);
			super.writeFloatBlock(v);
		};
		
		/** Overriden to inject type information */
		@Override public void writeDoubleBlock(double [] buffer, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(DOUBLE_blk_idx);
			super.writeDoubleBlock(buffer,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeDoubleBlock(double [] buffer)throws IOException
		{
			validateBlockTypeInfo(DOUBLE_blk_idx);
			super.writeDoubleBlock(buffer);
		};
		/** Overriden to inject type information */
		@Override public void writeDoubleBlock(double v)throws IOException
		{
			validateBlockTypeInfo(DOUBLE_blk_idx);
			super.writeDoubleBlock(v);
		};
		
		
		/** Overriden to inject type information */
		@Override public void writeString(CharSequence characters, int offset, int length)throws IOException
		{
			validateBlockTypeInfo(STRING_blk_idx);
			super.writeString(characters,offset,length);
		};
		/** Overriden to inject type information */
		@Override public void writeString(CharSequence characters)throws IOException
		{
			validateBlockTypeInfo(STRING_blk_idx);
			super.writeString(characters);
		};
	
		@Override public void writeString(char c)throws IOException
		{
			validateBlockTypeInfo(STRING_blk_idx);
			super.writeString(c);
		}
		/* ----------------------------------------------------------------------
		
				State management.
		
		----------------------------------------------------------------------*/
		/** Overriden to make sure, that reserved names are optimized. */
		@Override public void open()throws IOException,EClosed
		{
			//Note: we need to duplicate some state tracking machinery.
			if (TRACE) TOUT.println("open() ENTER");
			validateNotClosed();
			if (opened)
			{
				if (TRACE) TOUT.println("open(), already open LEAVE");
				 return;
		    };			
		    if (TRACE) TOUT.println("open()->openImpl()");
			openImpl();
			opened = true; //not set if openImpl failed.
						 //low level close will be invoked regardless.
			if (TRACE) TOUT.println("open() LEAVE");
		};		
		private void openImpl()throws IOException
		{
			if (TRACE) TOUT.println("openImpl() ENTER");
			//Note: reserved names optimization can be turned on only AFTER stream is
			//opened.
			super.open();
			//now hint downstream about optimization. 
			//This time order DO MATTER.
			for(int i=0,n=type_names.length; i<n; i++)
			{
				if (TRACE) TOUT.println("optimizing \""+type_names[i]+"\"");
				super.optimizeBeginName(type_names[i]);
			};
			//make sure no type info is present.
			current_type = -1;
			if (TRACE) TOUT.println("openImpl() LEAVE");
		};
		/** Overriden to ensure, that if type information was written it is terminated. */
		@Override public void close()throws IOException
		{
			if (TRACE) TOUT.println("close() ENTER");
			if (closed) 
			{
				if (TRACE) TOUT.println("close(), alreay closed LEAVE");
				return;
			};
			
			try{					
					if (TRACE) TOUT.println("close()->closeImpl()");
					closeImpl();
					if (TRACE) TOUT.println("close()->super.close()");
				}finally{ closed = true; }
			if (TRACE) TOUT.println("close()  LEAVE");
		};
		
		private void closeImpl()throws IOException
		{
			if (TRACE) TOUT.println("closeImpl() ENTER");
			try{
				closePendingTypeInfo();
			}finally{	super.close();  };
			if (TRACE) TOUT.println("closeImpl() LEAVE");
		};
};