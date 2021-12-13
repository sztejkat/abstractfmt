package sztejkat.abstractfmt;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
/**
	A lower level write format used as a "driver" 
	to implement {@link ISignalWriteFormat}.
	<p>
	This is based on concept of <a href="doc-file/indicator-format.html">"indicators"</a>.
	<p>
	This is up to a caller to take care about proper order 
	of methods invocation and to defend against missues
	and implementations of this class should provide little if
	any defense against missuse and may fail misserably if
	abused.
	
	<h2>Described versus un-described</h2>
	The indicator format decides by itself if it is described 
	or not. Writing classes should always assume format is
	described and call all methods in a proper way.
*/
public interface IIndicatorWriteFormat extends Closeable, Flushable, IPrimitiveWriteFormat
{
		/* ****************************************************
		
				Information and settings.
		
		****************************************************/
		/** A maximum of calls to {@link #writeBeginRegister}
		 and a maximum number to be passed there.
		@return non-negative, can be zero.
		*/
		public int getMaxRegistrations();	
		/** Informative method which can be used to tell
		if this indicator format is doing any job in
		{@link #writeType} or {@link #writeFlush} */
		public boolean isDescribed();
		/** Informative method which can be used to tell
		if this indicator format is doing any job in
		{@link #writeFlush} */			
		public boolean isFlushing();
		/* ****************************************************
		
				Signals related indicators.
		
		****************************************************/
		
		/**
			Writes to a stream {@link TIndicator#BEGIN_DIRECT}.
			@param signal_name name of a signal
			@throws IOException if failed at low level.
		*/
		public void writeBeginDirect(String signal_name)throws IOException;
		/**
			Writes to a stream {@link TIndicator#END_BEGIN_DIRECT}.
			@param signal_name name of a signal
			@throws IOException if failed at low level.
		*/
		public void writeEndBeginDirect(String signal_name)throws IOException;		
		/**
			Writes to a stream {@link TIndicator#BEGIN_REGISTER}
			@param signal_name name of a signal
			@param number a numeric value under which this signal name
			is to be registered. Callers must call this method in such
			a way, that:
			<ul>
				<li>calls with the same name are made only once;</li>
				<li>number assigned to to name equals to number of calls
				to this method since inside a caller. With this this 
				driver may decide to not pass this value inside a stream
				and use;</li>
			</ul>
			This number is in 0...{@link #getMaxRegistrations}-1 range
			@throws IOException if failed at low level.			
		*/
		public void writeBeginRegister(String signal_name, int number)throws IOException;
		/**
			Writes to a stream {@link TIndicator#END_BEGIN_REGISTER}
			@param signal_name as {@link #writeBeginRegister}
			@param number {@link #writeBeginRegister}
			@throws IOException if failed at low level.
		*/
		public void writeEndBeginRegister(String signal_name, int number)throws IOException;		
		/**
			Writes to a stream {@link TIndicator#BEGIN_USE}
			@param number a numeric value under which this signal name
			is registered. Callers must call this method passing
			number which was previously registered with
			{@link #writeBeginRegister}.
			@throws IOException if failed at low level.
		*/
		public void writeBeginUse(int number)throws IOException;
		/**
			Writes to a stream {@link TIndicator#END_BEGIN_USE}
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
		is to be stored. This method should be called in following sequence:
		<pre>
			writeType(X)
			<i>write primitive or block</i>
			writeFlush(X)
		</pre>
		regardless if upper level format is described or not.
		
		@param type indicator with {@link TIndicator#TYPE} flag set 
		@throws IOException if failed at low level.
		*/ 
		public void writeType(TIndicator type)throws IOException;
		/** Writes indicator telling that specific data end information
		is to be stored. Un-described formats may ignore it,
		but if {@link #writeType} is called this method must also be
		called.
		@param flush indicator with {@link TIndicator#FLUSH} flag set
				and  {@link TIndicator#READ_ONLY} not set.
		@throws IOException if failed at low level.
		*/ 
		public void writeFlush(TIndicator flush)throws IOException;
		
		/* ****************************************************
		
				IPrimitiveWriteFormat	
		
		****************************************************/
		/** 
		It is up to caller to always call it in sequence:
		<pre>
			writeType(...)
			writeBoolean(....)
			writeFlush(...)
		</pre>
		*/
		@Override public void writeBoolean(boolean v)throws IOException;		
		/** It is up to caller to invoke this method only if there
		is an unclosed "begin" signal.		
		<p>
		It is up to caller to call it in sequence:
		<pre>
			writeType(...)
			writeBooleanBlock(...
			writeBooleanBlock(...
			....
			writeFlush(...)
		</pre>
		*/
		@Override public void writeBooleanBlock(boolean [] buffer, int offset, int length)throws IOException;		
		
};