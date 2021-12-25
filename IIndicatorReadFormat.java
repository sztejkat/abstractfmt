package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;

/**
	A reading counterpart for {@link IIndicatorWriteFormat}.
	<p>
	This is up to a caller to take care about proper order 
	of methods invocation and to defend against missues
	and implementations of this class should provide little if
	any defense against missuse and may fail misserably if
	abused.
*/
public interface IIndicatorReadFormat extends Closeable, IPrimitiveReadFormat
{
	/* ****************************************************
		
				Information and settings.
		
	****************************************************/
	/** A maximum of times the {@link #getIndicator}
	 can return name registration indicators and a maxium number
	 of registered names. Also a maxium number returned from
	 {@link #getSignalNumber}.
	@return non-negative, can be zero.
	*/
	public int getMaxRegistrations();	
	/** Informative method which can be used to tell
	that user of this format must check for 
	type indicators with {@link TIndicator#TYPE} flag set.
	@return true if described. Life time constant.
	 */
	public boolean isDescribed();
	/** Informative method which can be used to tell
	that user of this format must check for 
	flush indicators with {@link TIndicator#FLUSH} flag set.
	@return true if flushing. Life time constant.
	*/			
	public boolean isFlushing();
	
	
	/** Returns maximum supported signal name length by this format.
	This limit is non-adjustable and relates to physcial boundaries
	of format.
	@return length limit, non-zero positive */
	public int getMaxSupportedSignalNameLength();
	
	/** Allows to set limit for signal name. Default
	value is 1024 or {@link #getMaxSupportedSignalNameLength},
	which is smaller.
	<p>
	Adjusting value when {@link #open} was called may result
	in unpredictable effects. 
	<p>
	<i>Note:Setting this limit should, internally, stop fetching
	input data when limit is reached since this is
	a defense against "out of memory" attacks on name
	buffers.</i>
	
	@param characters name limit, non-zero positive.
			Passing value greater than {@link #getMaxSupportedSignalNameLength}
			may have unpredictable effects.
	*/
	public void setMaxSignalNameLength(int characters);
	
	/** Returns value set in {@link #setMaxSignalNameLength}
	@return value set in {@link #setMaxSignalNameLength} */
	public int getMaxSignalNameLength();
	
	/* ***************************************************
	
			Indicators.			
	
	
	* ****************************************************/
	/**
	Checks what is under a cursor in a stream.
	<p>
	If the indicator is with {@link TIndicator#NAME} flag set
	updates data accessible through {@link #getSignalName}.
	<p>
	If the indicator is with {@link TIndicator#REGISTER} flag set
	updates data accessible through {@link #getSignalNumber}.
	<p>
	If there is no indicator under cursor but some data (including block body)
	it returns {@link TIndicator#DATA} and does not move a cursor.
	<p>
	If cursor is at the physical end of file returns 
	{@link TIndicator#EOF}.		
	<p>
	Under no condition cursor is moved. 
	
	@return enum representing state of a stream
	@throws EFormatBoundaryExceeded if name or number related
	with a signal are out of allowed boundaries.	
	@see #next()
	*/
	public TIndicator getIndicator()throws IOException;
	/** Calls {@link #getIndicator} and calls {@link #next} if not on data
	@return as {@link #getIndicator}
	@throws IOException if failed. */
	public default TIndicator readIndicator()throws IOException
	{
	 	final TIndicator i = getIndicator();
	 	if ((i!=TIndicator.DATA)&&(i!=TIndicator.EOF))
	 						next();
	 	return i;
	}
	/** Calls {@link #getIndicator} and calls {@link #next} if 
	at {@link TIndicator#DATA}
	@throws IOException if failed. 
	*/	
	public default void skip()throws IOException
	{
		final TIndicator i = getIndicator();
	 	if (i==TIndicator.DATA)
	 						next();
	};
	
	/** Skips stream content to next element which
	can be either data or an indicator.
	<p>
	If cursor is at data ({@link #getIndicator}== {@link TIndicator#DATA})
	the remanining part of that data is skipped and cursor stops
	at nearest indicator.
	<p>
	If cursor is at an indicator this indicator is skipped and cursor
	is set to an element after it which may be either data or indicator.	 	
	@throws EUnexpectedEof if hit physical end of stream
		before reaching indicator.
	*/
	public void next()throws IOException;
		
	/** Returns most recently read name during processing
	the {@link #getIndicator} which returned 
	indicator with {@link TIndicator#NAME} flag set.
	<p>
	Calling it for other indicators may have unpredictable results.
	@return name of signal. 
	*/
	public String getSignalName();
	/** Returns most recently read number during processing
	the {@link #getIndicator} which returned 
	indicator with {@link TIndicator#REGISTER} flag set.
	<p>
	Calling it for other indicators may have unpredictable results.
	@return index of signal, non-negative, but may be out of order
			which shows that stream is malformed.
	*/
	public int getSignalNumber();
	
	
	
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/** Read primitive and moves cursor at the element after it.
	<p>
	In undescribed ({@link #isDescribed}==false) streams after
	return from this method curstor can be:
	<ul>
		<li>data - in such case {@link #getIndicator} returns
		{@link TIndicator#DATA};</li>
		<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
	</ul> 
	In described ({@link #isDescribed}==true) stream 
	after return from this method curstor must be at an indicator:
	<ul>
		<li>with {@link TIndicator#FLUSH}, if {@link #isFlushing} or;</li>
		<li>with {@link TIndicator#SIGNAL} flag set if not {@link #isFlushing};</li>
	</ul>
	This method may be called only if cursor is at data and this condition must
	be validated by calling {@link #getIndicator} prior to calling this method.
	If this method is called without checking indicator the effect may be unpredictable.	
	
	@throws ENoMoreData if reached indicator <u>inside</u> a single element.	
	@throws EUnexpectedEof if there is not enough data physically in stream to complete
		read.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator before completion of an operation.
		In such case the indicator must be available for {@link #readIndicator}
	*/
	@Override public boolean readBoolean()throws IOException;
	/**
	Reads part of block, moves cursor.
	<p>
	In undescribed ({@link #isDescribed}==false) streams after
	return from this method curstor can be:
	<ul>
		<li>data - in such case {@link #getIndicator} returns
		{@link TIndicator#DATA};</li>
		<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
	</ul> 
	In described ({@link #isDescribed}==true) stream 
	after return from this method curstor must be at:
	<ul>
		<li>data - if some data remains un-read. In such case {@link #getIndicator} returns
		{@link TIndicator#DATA};</li>
		<li> an indicator with {@link TIndicator#FLUSH}, if {@link #isFlushing} or;</li>
		<li> an indicator with {@link TIndicator#SIGNAL} flag set if not {@link #isFlushing};</li>
	</ul>
	This method may be called <u>only if cursor is at DATA</u> and this condition must
	be validated by calling {@link #getIndicator} prior to calling this method.
	If this method is called without checking indicator the effect may be unpredictable.
	Effectively there is no such behaviour like: 
	<i>"if it returns partial read, then subsequent calls will return 0"</i>
	 but instead the effect is unpredictable.
	<p>
	Reading blocks from indicator streams should be done in a sequence
	of calls of block reads of the same type and this sequence
	may be terminated only by an indicator. If it is done otherwise
	the behavior is unspecified and format may fail to work.
	<p>
	This method reads data item by item, as long as the requested number
	of items is read <u>or</u> and indicator is reached.
	
	@return number of read elements. Partial read can be returned only 
			if at an attempt to read an item the cursor was at an indicator. 
			Due to the fact, that this call is not allowed when cursor
			is not at data zero is returned only if <code>length</code>
			was zero.
		
	@throws EUnexpectedEof if there is not enough data physically in stream
		to complete operation for a single element.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator inside a single element read, ie. between bytes
		if an interger.	In such case the indicator must be available 
		for {@link #getIndicator}
	*/
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
	/**
	@return 0...255, byte which was read. Due to the fact, that calling this method is allowed only
		when indicator is at DATA this method never returns -1.
	*/
	@Override public int readByteBlock()throws IOException;	
	
	
	/* ************************************************
		
				State, Closeable		
		
	*************************************************/
	/** Reads and validates opening sequence, if any.
	Calling this method more than once may fail.
	Calling any method of this class except
	{@link #getMaxRegistrations},
	{@link #isDescribed},
	{@link #isFlushing},
	{@link #setMaxSignalNameLength},
	{@link #getMaxSignalNameLength},
	{@link #getMaxSupportedSignalNameLength}
	 without calling {@link #open}
	may have unpredictable results.
	*/
	public void open()throws IOException;
	/**
	Closes format, makes it unusable.
	<p>
	Once closed calling all all except {@link #getMaxRegistrations},
	{@link #isDescribed},{@link #isFlushing},
	{@link #getMaxSignalNameLength},
	{@link #getMaxSupportedSignalNameLength} 
	 may have unpredictable results.
	<p>
	Calling it multiple times may have unpredictable results.
	<p>
	Calling it without calling open is allowed and should
	release resources.
	*/
	@Override public void close()throws IOException;
};