package sztejkat.abstractfmt;
import java.io.IOException;
/**
	A core, elementary implementation.
	<p>
	This implementation is based on what is discussed in
	<a href="doc-files/core-implementation.html">this document</a>.
	<p>
	This class divides support methods into three groups:
	<ul>
		<li>signal indicators
		{@link #writeBeginSignalIndicator},{@link #writeEndSignalIndicator}
		{@link #writeEndBeginSignalIndicator};</li>
		<li>types indicators for describes streams,
		{@link writeBooleanType} and ect, {@link #writeBooleanBlockType} and etc.
		All implemented as no-operation, as expected for un-described streams;</li>
		<li>event names handling
		{@link #writeDirectName},{@link #writeSignalNameData},{@link #writeRegisterName}
		{@link #writeRegisterUse};</li>
	</ul>
	This class provides all necessary defensive layers and names registry.
	<p>
	This class does not define any primitive write ops
*/
public abstract class ASignalWriteFormat
{
				/** A names registry, filled up with names, first
				null indicates end of used area. Null if registry is not used.*/
				private final String [] names_registry;
				/** A names registry, hash codes for registered names.
				Null if registry is not used.*/
				private final int [] names_registry_hash;
				/** See constructor */
				private final int max_name_length;
				/** See constructor */
				private final int max_elements_recursion_depth;
		/* *******************************************************
		
		
						Construction
		
		
		*********************************************************/
		/** Creates write format
		@param names_registry_size maximum number of names 
			to register if compact names should be implemented.
			This will be the maximum number of calls to {@link #writeRegisterName}
			and maximum index passed to that method will be <code>names_registry_size-1</code>.
			<p>
			Zero to disable registry and always use {@link #writeDirectName}
		@param max_name_length greater or equal to 8. Maximum length of names
			to be accepted in {@link #begin(String, boolean)} and be passed to
			{@link #writeSignalNameData}
		@param max_elements_recursion_depth specifies the allowed depth of elements
			nesting. Zero disables limit, 1 sets limit to: "no nested elements allowed",
			2 allows element within an element and so on. If this limit is exceed
			the {@link #begin(String,boolean)} will throw <code>IllegalStateException</code>.
		*/
		protected ASignalWriteFormat(int names_registry_size,
									 int max_name_length,
									 int max_elements_recursion_depth
									 )
		{
			assert(names_registry_size>=0):"names_registry_size="+names_registry_size;
			assert(max_name_length>=8):"max_name_length="+max_name_length;
			assert(max_elements_recursion_depth>=0):"max_elements_recursion_depth="+max_elements_recursion_depth;
			if (names_registry_size>0)
			{
				this.names_registry 	 = new String[names_registry_size];
				this.names_registry_hash = new int[names_registry_size];
			}else
			{
				this.names_registry = null;
				this.names_registry_hash=null;
			}
			this.max_name_length=max_name_length;
			this.max_elements_recursion_depth=max_elements_recursion_depth;
		};
		
		
		
		/* *******************************************************
		
		
						Names registry manipulation
		
		
		*********************************************************/
		/** Attempts to locate name into an index.
		@param name name to look for
		@return non-negative indicating position at which 
				it is found in {@link #names_registry}, -1 if it is 
				not found
		*/				
		private int findInIndex(String name)
		{
			assert(name!=null);
			int hash = name.hashCode();
			for(int i=names_registry.length;--i>=0;)
			{
				if (names_registry_hash[i]==hash)
				{
					if (names_registry[i].equals(name)) return i;
				}
			};
			return -1;
		};
		/** Attempts to put name into an index, assuming it is not there.
		@param name name to put
		@return non-negative indicating position at which 
				it is found in {@link #names_registry}, -1 if it could not be put.
		*/
		private int putToIndex(String name)
		{
			assert(name!=null);
			assert(findInIndex(name)==-1):"already in index";
			//Note: This time we need to iterate from zero, because we need
			//to assign numbers from zero upwards in writeRegisterName
			for(int i=0, n=names_registry.length; i<n;i++)
			{
				if (names_registry[i]==null)
				{
					names_registry[i] = name;
					names_registry_hash[i]=name.hashCode();
					return i;
				}
			};
			return -1;
		};
		/* ********************************************************
		
		
				Core services required from subclasses		
				
		
		**********************************************************/
		/*========================================================
		
				Indicators
		
		=========================================================*/
		/* -------------------------------------------------------
				Signals		
		---------------------------------------------------------*/
		/** Should write to stream an indicator that this is a begin
		signal being written now.
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeDirectName};</li>
			<li>{@link #writeRegisterName};</li>
			<li>{@link #writeRegisterUse};</li>
		</ul>
		@throws IOException if low level i/o failed.
		*/
		protected abstract void writeBeginSignalIndicator()throws IOException;
		/** Should write to stream an indicator that this is an end signal.
		@throws IOException if low level i/o failed.
		*/
		protected abstract void writeEndSignalIndicator()throws IOException;
		/** Should have, functionally same effect as:
		<pre>
			writeEndSignalIndicator()
			writeBeginSignalIndicator()
		</pre>
		and default implementation is such.
		@throws IOException if low level i/o failed.
		*/
		protected void writeEndBeginSignalIndicator()throws IOException
		{
			writeEndSignalIndicator();
			writeBeginSignalIndicator();
		};
		/* -------------------------------------------------------
				Types of primitives for described streams		
		---------------------------------------------------------*/
		/** Should write type indicator for {@link IDescribedSignalReadFormat#hasData}.
		Un-described formats may implement is a no-op. Default is no-op. 
		@throws IOException if low level i/o failed.*/
		protected  void writeBooleanType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeByteType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeCharType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeShortType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeIntType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeLongType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeFloatType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeDoubleType()throws IOException{};
		
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeBooleanBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeByteBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeCharBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeShortBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeIntBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeLongBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeFloatBlockType()throws IOException{};
		/** See {@link #writeBooleanType}
		@throws IOException if low level i/o failed.*/
		protected  void writeDoubleBlockType()throws IOException{};
		/*========================================================
		
				Names
		
		=========================================================*/
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be encoded directly.
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeSignalNameData};</li>
		</ul>
		@throws IOException if low level i/o failed.*/
		protected abstract void writeDirectName()throws IOException;
		/** Invoked only after {@link #writeDirectName} or {@link #writeRegisterName}
		to specify the signal name.
		@param name never null, always not longer than {@link ISignalWriteFormat#getSupportedSignalLength}.
		@throws IOException if low level i/o failed.*/
		protected abstract void writeSignalNameData(String name)throws IOException;
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be encoded directly AND that this signal
		is used to indicate what index is assigned to a name.		
		<p>
		Once this method is called only following calls
		can be made:
		<ul>
			<li>{@link #writeSignalNameData};</li>
		</ul>
		@param name_index the non-negative name index assigned to name which will
			follow. This class ensures that:
			<ul>
				<li><code>name_index</code> is zero for first call of this
				method, 1 for next and so on, until stream is closed or
				the name registry pool is full;</li>
			</ul>
		@throws IOException if low level i/o failed.*/
		protected abstract void writeRegisterName(int name_index)throws IOException;
		/** Invoked only after {@link #writeBeginSignalIndicator} to
		indicate that signal name will be passes as a number only and won't be written
		directly.
		@param name_index the non-negative name index assigned to name
				in apropriate {@link #writeRegisterName} call.
		@throws IOException if low level i/o failed.*/
		protected abstract void writeRegisterUse(int name_index)throws IOException;
		
		
		/* ********************************************************
		
		
				ISignalWriteFormat
				
		
		**********************************************************/
		
		.... to be done ....
};