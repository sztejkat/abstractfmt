package sztejkat.abstractfmt.compat;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.EClosed;
import java.io.Reader;
import java.io.IOException;

/**
	Common base for readers
*/
abstract class AStructReader extends Reader
{
			/** Underlying format */
			protected final IStructReadFormat in;
			/** State tracker */
			private boolean is_closed;
			
	/*  *****************************************************
		
			Construction
		
		
		******************************************************/
	/** Creates
	@param in non null format on which it will be laid over.
		The cursor in format should be either in front of
		or inside the byte sequence available through
		{@link IStructReadFormat#readCharBlock} family of
		methods.
	*/
	public AStructReader(IStructReadFormat in)
	{
		assert(in!=null);
		this.in = in;
	};
	/*  *****************************************************
		
			Support services
		
		
	******************************************************/
	/** State tracker.
	@return true if {@link #close} was run at least once */
	protected final boolean isClosed(){ return is_closed; };
	/** State validator
	@throws EClosed if {@link #isClosed} gives true. */
	protected final void validateNotClosed()throws EClosed
	{
		if (is_closed) throw new EClosed();
	};
	/** Invoked at first call to {@link #close}.
	Subclasses may override it to perform additional
	operations during close, like for an example
	skipping remaning data.
	@throws IOException if failed
	@see #in
	*/
	protected void closeImpl()throws IOException{};
	
	/*  *****************************************************
		
			Reader
		
			
	******************************************************/
	/**
		Makes this object unusable.  
	*/
	@Override public void close()throws IOException
	{
		//Intentionally: no flushing!
		if (!is_closed)
		{
			try{
				closeImpl();
			}finally{ is_closed = true; }
		};
	};
}