package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.IOException;

/**
	A reading counterpart for {@link IIndicatorWriteFormat}.
	<p>
	Implementations must allow to set during construction
	the limit on signal name length.
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
		If there is no indicator under cursor but some data
		returns {@link TIndicator#DATA}.
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
	@return name of signal. 
	@throws IllegalStateException if name is invalid.
	*/
	public String getSignalName();
	/** Returns most recently read number during processing
	the {@link #readIndicator} which returned 
	indicator with {@link TIndicator#REGISTER} flag set	
	<p>
	Calling this method invalidates the stored number.
	@return name of signal. 
	@throws IllegalStateException if number is invalid.
	*/
	public int getSignalNumber();
	
	
	/* *************************************************************
	
			Primitives	
	
	* *************************************************************/
	/**
	@throws ENoMoreData if could not intialize operation because
		there is an indicator at cursor.
	@throws EUnexpectedEof if there is not enough data physically in stream.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator before completion of an operation.
		In such case the indicator must be available for {@link #readIndicator}
	*/
	public boolean readBoolean()throws IOException;
	/**
	@throws ENoMoreData if could not intialize operation because
		there is an indicator at cursor.
	@throws EUnexpectedEof if there is not enough data physically in stream.
	@throws ECorruptedFormat if could initialize operation, but
		reached the indicator inside a single element read, ie. between bytes
		if an interger.	In such case the indicator must be available 
		for {@link #readIndicator}
	*/
	public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		
};