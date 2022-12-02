package sztejkat.abstractfmt;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
import java.io.Closeable;

/**
	Just a wrapper which passes calls to wrapped {@link IStructWriteFormat}.
	<p>
	Good base for filters.
*/
class AStructFormatAdapterBase<T extends IFormatLimits & Closeable> implements IFormatLimits,Closeable
{
	//Note:	Usually I would use an automatic code generation tool
	//	    to produce adataper without manual coding, but this
	//		time I will do it by hand to avoid creating unnecessairly
	//		complex compile-time dependencies.
	
					protected final T engine;
					
		public AStructFormatAdapterBase(T engine)
		{
			assert(engine!=null);
			this.engine = engine;
		};
		/* ***************************************************************
		
				IFormatLimits
		
		
		****************************************************************/
		@Override public void setMaxSignalNameLength(int characters){ engine.setMaxSignalNameLength(characters); };
		@Override public int getMaxSignalNameLength(){ return engine.getMaxSignalNameLength(); };
		@Override public int getMaxSupportedSignalNameLength(){ return engine.getMaxSupportedSignalNameLength(); };
		@Override public int getMaxStructRecursionDepth(){ return engine.getMaxStructRecursionDepth(); };
		@Override public void setMaxStructRecursionDepth(int max_depth)throws IllegalStateException,IllegalArgumentException
		{
			engine.setMaxStructRecursionDepth(max_depth);
		}
		@Override public int getMaxSupportedStructRecursionDepth(){ return engine.getMaxSupportedStructRecursionDepth(); };
		/* ***************************************************************
		
				Closeable
		
		
		****************************************************************/
		@Override public void close()throws IOException{ engine.close(); };
};