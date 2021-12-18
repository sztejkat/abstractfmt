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
	/** Allows to set limit for signal name. Default
	value is 1024. Adjusting this value during stream
	processing may have unpredictable results.
	@param characters name limit, non-zero positive. 
	*/
	public void setMaxSignalNameLength(int characters);
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
	This value is cleared if returned indicator has no such flag set.
	@return name of signal. 
	@throws IllegalStateException if cursor is not at indicator carrying signal name.
	*/
	public String getSignalName();
	/** Returns most recently read number during processing
	the {@link #getIndicator} which returned 
	indicator with {@link TIndicator#REGISTER} flag set.
	<p>
	This value is cleared if returned indicator has no such flag set.
	@return name of signal. 
	@throws IllegalStateException if cursor is not at indicator carrying signal number.
	*/
	public int getSignalNumber();
	
	
	
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/** Read primitive and moves cursor at the element after it.
		<p>
		In undescribed ({@link #isDescribed}==false) streams it can be:
		<ul>
			<li>data - in such case {@link #getIndicator} returns
			{@link TIndicator#DATA};</li>
			<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
		</ul> 
		In described ({@link #isDescribed}==true) stream it must be an indicator:
		<ul>
			<li>any with {@link TIndicator#FLUSH}, if {@link #isFlushing};</li>
			<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
		</ul>
		This method may be called only if cursor is at data.
		
	@throws EUnexpectedEof if there is not enough data physically in stream to complete
		read.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator before completion of an operation.
		In such case the indicator must be available for {@link #readIndicator}
	*/
	public boolean readBoolean()throws IOException;
	/**
	Reads part of block, moves cursor.
	<p>
	In undescribed ({@link #isDescribed}==false) streams it can be:
	<ul>
		<li>data - in such case {@link #getIndicator} returns
		{@link TIndicator#DATA};</li>
		<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
	</ul> 
	In described ({@link #isDescribed}==true) stream it must be an indicator:
	<ul>
		<li>data - if some data remains un-read. In such case {@link #getIndicator} returns
		{@link TIndicator#DATA};</li>
		<li>any with {@link TIndicator#FLUSH}, if {@link #isFlushing};</li>
		<li>an indicator with {@link TIndicator#SIGNAL} flag set;</li>
	</ul>
	This method may be called only if cursor is at data.
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
		
	@throws EUnexpectedEof if there is not enough data physically in stream.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator inside a single element read, ie. between bytes
		if an interger.	In such case the indicator must be available 
		for {@link #getIndicator}
	*/
	public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		
};