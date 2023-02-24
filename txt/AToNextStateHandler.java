package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import sztejkat.abstractfmt.logging.SLogging;
import java.io.IOException;
/** 
	A state handler which is providing facilities to move to next state
	handler in a generic way.
	<p>
	This class is usefull when You create abstract state handlers which 
	either move to next state or lay over it and the behavior is to be
	varying in subclasses.
*/
public abstract class AToNextStateHandler<TState extends ATxtReadFormat1.TIntermediateSyntax> 
				extends AStateHandler<TState>
{
					 private static final long TLEVEL = SLogging.getDebugLevelForClass(AToNextStateHandler.class);
					 private static final boolean TRACE = (TLEVEL!=0);
					 private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AToNextStateHandler.",AToNextStateHandler.class) : null;
				
	/* *****************************************************************************
	
				Construction
	
	******************************************************************************/
	/**
		Creates.
		@param parser parser, non null
	*/
	protected AToNextStateHandler(ATxtReadFormatStateBase1<TState> parser)
	{
		super(parser);
	}
	/* *****************************************************************************
	
				Services required from subclasses
	
	******************************************************************************/
	/** A next handler to use.
		<p>
		Note: This method is necessary to allow breaking "cyclic construction" of cyclic state machines
			by referencing to a common outer class field.
		@return state to which will move to that handler. 
				
		@see #toNextStateHandler
	*/
	protected abstract ATxtReadFormatStateBase0.IStateHandler getNextHandler(); 
	
	/* *****************************************************************************
	
				Services for subclasses
	
	******************************************************************************/
	/** Acts on {@link #toNextChar()} figuring out that needs to move to next state
	and manages state according to {@link #getNextHandler}.
	*/
	protected final void toNextStateHandler()
	{
		if (TRACE) TOUT.println("toNextStateHandler("+this+")->"+getNextHandler());
		setStateHandler(getNextHandler());
	};
};