package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;

/**
	A reading counterpart for {@link IIndicatorWriteFormat}.
	<p>
	Implementations must allow to set during construction
	the limit on signal name length.
	<p>
	This is up to a caller to take care about proper order 
	of methods invocation and to defend against missues
	and implementations of this class should provide little if
	any defense against missuse and may fail misserably if
	abused.
*/
public interface IIndicatorReadFormat extends Closeable, IPrimitiveReadFormat
{
	/* ***************************************************
	
			Indicators.			
	
	
	* ****************************************************/
	/**
		Checks what is under a cursor in a stream.
		<p>
		If the cursor is at an indicator fetches it, moves
		cursor after it and returns it.
		<p>
		If the indicator is with {@link TIndicator#NAME} flag set
		updates data accessible through {@link #getSignalName}.
		<p>
		If the indicator is with {@link TIndicator#REGISTER} flag set
		updates data accessible through {@link #getSignalNumber}.
		<p>
		If there is no indicator under cursor but some data (including block body)
		it returns {@link TIndicator#DATA}.
		<p>
		If cursor is at the physical end of file returns 
		{@link TIndicator#EOF}.		
		
		@return enum representing state of a stream
		@throws EFormatBoundaryExceeded if name or number related
		with a signal are out of allowed boundaries.
	*/
	public TIndicator readIndicator()throws IOException;
	
	/** Returns most recently read name during processing
	the {@link #readIndicator} which returned 
	indicator with {@link TIndicator#NAME} flag set.
	<p>
	Calling this method invalidates the stored name.
	<p>
	Any call to {@link #readIndicator} invalidates it.
	@return name of signal. 
	@throws IllegalStateException if name is invalid.
	*/
	public String getSignalName();
	/** Returns most recently read number during processing
	the {@link #readIndicator} which returned 
	indicator with {@link TIndicator#REGISTER} flag set	
	<p>
	Calling this method invalidates the stored number.
	<p>
	Any call to {@link #readIndicator} invalidates it.
	@return name of signal. 
	@throws IllegalStateException if number is invalid.
	*/
	public int getSignalNumber();
	
	/** Skips stream content to nearest indicator.
	 That indicator must be available for {@link #readIndicator}
	@throws EUnexpectedEof if hit physical end of stream
		before reaching indicator.
	*/
	public void skip()throws IOException;
	
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/**
	@throws ENoMoreData if could not intialize operation because
		there is an indicator at cursor. If it happens this indicator must
		be available for {@link #readIndicator}
	@throws EUnexpectedEof if there is not enough data physically in stream.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator before completion of an operation.
		In such case the indicator must be available for {@link #readIndicator}
	*/
	public boolean readBoolean()throws IOException;
	/** Reads part of block, moves cursor.
	<p>
	Reading blocks from indicator streams should be done in a sequence
	of calls of block reads of the same type. If it is done otherwise
	the behavior is unspecified. 	
	<p>
	During block read the {@link #readIndicator} must return {@link TIndicator#DATA}
	unless indicator is at cursor.
	<p>
	This method reads data item by item, as long as the requested number
	of items is read <u>or</u> and indicator is reached.
	
	@return number of read elements. Partial read can be returned only 
			if at an attempt to read an item the cursor was at an indicator. 
		
	@throws EUnexpectedEof if there is not enough data physically in stream.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator inside a single element read, ie. between bytes
		if an interger.	In such case the indicator must be available 
		for {@link #readIndicator}
	*/
	public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		
};