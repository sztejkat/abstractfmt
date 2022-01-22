package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
/**
	A <a href="package-summary.html#tree-tire">third-tire</a> service which is 
	used to implement {@link ISignalWriteFormat}.
	<p>
	This is based on concept of <a href="package-summary.html#indicators">indicators</a>.
	<p>
	This is up to a caller to take care about proper order 
	of methods invocation and to defend against missues.
	Implementations of this class may provide little if
	any defense against missuse and may fail misserably if
	abused.
	<p>
	For defensive layer see {@link CIndicatorWriteFormatProtector}.
	
	<h1>Thread safety</h1>
	Format are <u>not thread safe</u>.	
*/
public interface IIndicatorWriteFormat extends Closeable, Flushable, IPrimitiveWriteFormat
{
	/* ****************************************************
	
			Information and settings.
	
	****************************************************/
	/** A maximum of calls to {@link #writeBeginRegister} and
	{@link #writeEndBeginRegister} and a maximum number to be passed there +1.
	@return non-negative, can be zero, life-time constat.
	*/
	public int getMaxRegistrations();	
	
	/** Informative method which can be used to tell
	if this indicator format is doing any job in
	{@link #writeType} and/or {@link #writeFlush}.
	Notice both methods <u>must be called</u> regardless of what is returned here.
	@return true if generates described stream, life-time constat.
	*/
	public boolean isDescribed();
	
	/** Informative method which can be used to tell
	if this indicator format is doing any job in
	{@link #writeFlush}.
	<p>
	This method may not return true if	{@link #isDescribed} is false.
	@return true if generates flushing informatio, life-time constat.
	
	@see #isDescribed */			
	public boolean isFlushing();
	
	/** Returns maximum supported signal name length by this format.
	This limit is non-adjustable and relates to physcial boundaries
	of format.
	@return length limit, non-zero positive, life-time constat. */
	public int getMaxSupportedSignalNameLength();
	
	
	
	/* ****************************************************
	
			Signals related indicators.
	
	****************************************************/		
	/**
		Writes to a stream {@link TIndicator#BEGIN_DIRECT}.
		@param signal_name name of a signal. Passing null or
			name longer than {@link #getMaxSupportedSignalNameLength}
			may have unpredictable results.
		@throws IOException if failed at low level.
	*/
	public void writeBeginDirect(String signal_name)throws IOException;
	/**
		Writes to a stream {@link TIndicator#END_BEGIN_DIRECT}.
		<p>
		<i>Note:Implementations are allowed to implement this method
		as:</i> 
		<pre>
			writeEnd();writeBeginDirect(signal_name);			
		</pre>
		@param signal_name name of a signal. Passing null or
			name longer than {@link #getMaxSupportedSignalNameLength}
			may have unpredictable results.
		@throws IOException if failed at low level.
	*/
	public void writeEndBeginDirect(String signal_name)throws IOException;		
	/**
		Writes to a stream {@link TIndicator#BEGIN_REGISTER}.
		Using this method when {@link #getMaxRegistrations}
		returns zero may result in upredictable behaviour.
		@param signal_name name of a signal. Passing null or
			name longer than {@link #getMaxSupportedSignalNameLength}
			may have unpredictable results.
		@param number a numeric value under which this signal name
		is to be registered. Callers must call this method in such
		a way, that:
		<ul>
			<li>calls with the same name are made only once;</li>
			<li>number assigned to to name equals to number of already
			made calls to this method (ie <code>writeBeginRegister("a",0),writeBeginRegister("b",1)</code>
			and so on. This allows format to use <i>implict</i> numbering and ignore this value;</li>
		</ul>
		This number is in 0...{@link #getMaxRegistrations}-1 range
		@throws IOException if failed at low level.			
	*/
	public void writeBeginRegister(String signal_name, int number)throws IOException;
	/**
		Writes to a stream {@link TIndicator#END_BEGIN_REGISTER}
		<p>
		<i>Note:Implementations are allowed to implement this method
		as:</i> 
		<pre>
			writeEnd();writeEndBeginRegister(signal_name);			
		</pre>
		@param signal_name as {@link #writeBeginRegister}
		@param number {@link #writeBeginRegister}
		@throws IOException if failed at low level.
	*/
	public void writeEndBeginRegister(String signal_name, int number)throws IOException;		
	/**
		Writes to a stream {@link TIndicator#BEGIN_USE}.
		Using this method when {@link #getMaxRegistrations}
		returns zero may result in upredictable behaviour.
		@param number a numeric value under which signal name used here 
		was previously is registered. Callers must call this method passing
		number which was previously registered with	{@link #writeBeginRegister} or
		{@link #writeEndBeginRegister}.
		@throws IOException if failed at low level.
	*/
	public void writeBeginUse(int number)throws IOException;
	/**
		Writes to a stream {@link TIndicator#END_BEGIN_USE}
		<p>
		<i>Note:Implementations are allowed to implement this method
		as:</i> 
		<pre>
			writeEnd();writeEndBeginRegister(signal_name);			
		</pre>
		@param number as {@link #writeBeginUse}
		@throws IOException if failed at low level.
	*/
	public void writeEndBeginUse(int number)throws IOException;
	/** Write to a stream the {@link TIndicator#END}
		@throws IOException if failed at low level.
	*/ 
	public void writeEnd()throws IOException;
	
	/* ****************************************************
	
			Type related indicators.
	
	
	****************************************************/
	/** Writes indicator telling that specific type start information
	is to be stored. This method must be always called in following sequence:
	<pre>
		writeType(X)
		<i>write primitive or block</i>
		writeFlush(X)
	</pre>
	<u>regardless if format is described</u> or not.
	<p>
	Even tough this method looks like "do-nothing" in non-described formats,
	implementations are allowed to use it to do some housekeeping so it
	<u>must</u> be called.
	
	@param type indicator with {@link TIndicator#TYPE} flag set.
		Passing other indicators may have unpredictable efects. 
	@throws IOException if failed at low level.
	*/ 
	public void writeType(TIndicator type)throws IOException;
	/** 
	Writes indicator telling that specific end-of-data information
	is to be stored. 
	<p>
	See {@link #writeType} for use requirements.
	
	@param flush indicator with {@link TIndicator#FLUSH} flag set
			and  {@link TIndicator#READ_ONLY} not set.
			Passing other indicators may have unpredictable efects.
	@throws IOException if failed at low level.
	*/ 
	public void writeFlush(TIndicator flush)throws IOException;
	
	/* ****************************************************
	
			IPrimitiveWriteFormat	
	
	****************************************************/
	/** Writes boolean primivtive.
	<p>
	It is up to caller to always call it in sequence
	regardless if is described stream or not:
	<pre>
		writeType(...)
		writeBoolean(....)
		writeFlush(...)
	</pre>
	Not writing type information may have unpredictable results.
	<p>
	<i>Note: All other elementary primitive writes do behave alike.</i>
	@see #isDescribed
	@see #writeType
	@see #writeFlush
	*/
	@Override public void writeBoolean(boolean v)throws IOException;		
	/**
	Writes boolean primitives block.
	<p>
	It is up to caller to invoke this method only if there
	is an unclosed "begin" signal.		
	<p>
	It is up to caller to always call it in sequence
	regardless if is described stream or not:
	<pre>
		writeType(...)
		writeBooleanBlock(...
		writeBooleanBlock(...
		....
		writeFlush(...)
	</pre>
	Not writing type information may have unpredictable results.
	<p>
	<i>Note: All other primitive block writes do behave alike.</i>
	@see #isDescribed
	@throws AssertionError if buffer, offset or length are incorrect the behaviour
			of this method is unpredictable.
	*/
	@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
	
	/* ************************************************
	
			State, Closeable, Flushable		
	
	*************************************************/
	/** Writes opening sequence, if any.
	<p>
	Calling this method more than once may fail.
	<p>
	Calling any method of this class except informative like:
	{@link #getMaxRegistrations},
	{@link #isDescribed},{@link #isFlushing},
	{@link #getMaxSupportedSignalNameLength} 
	may have unpredictable results if made before calling {@link #open}
	<p>
	@throws IOException if failed at low level
	*/
	public void open()throws IOException;
	/**
	Closes format, makes it unusable. If format was open ({@link #open} was called)
	writes closing sequence, if any.
	<p>
	Once format is closed calling all except informative like: {@link #getMaxRegistrations},
	{@link #isDescribed},{@link #isFlushing},{@link #getMaxSupportedSignalNameLength} 
	may have unpredictable results.
	<p>
	This method may just close resources without flushing buffers,
	so calling {@link #flush} is up to user.
	<p>
	Calling it multiple times may have unpredictable results.
	<p>
	Calling it without calling {@link #open} is allowed and should
	release resources, but produced stream may be malformed.
	@throws IOException if failed at low level
	*/
	@Override public void close()throws IOException;
};