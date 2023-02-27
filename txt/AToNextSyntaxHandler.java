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
public abstract class AToNextSyntaxHandler<TState extends ATxtReadFormat1.TIntermediateSyntax> 
				extends ASyntaxHandler<TState>
{
					 private static final long TLEVEL = SLogging.getDebugLevelForClass(AToNextSyntaxHandler.class);
					 private static final boolean TRACE = (TLEVEL!=0);
					 private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AToNextSyntaxHandler.",AToNextSyntaxHandler.class) : null;
				
	/* *****************************************************************************
	
				Construction
	
	******************************************************************************/
	/**
		Creates.
		@param parser parser, non null
	*/
	protected AToNextSyntaxHandler(ATxtReadFormatStateBase1<TState> parser)
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
		@return if non null state will move to that handler. If null
				state will be popped. Also controls if state is set or pushed on {@link #enterStateHandler}.
				
		@see #enterStateHandler
		@see #leaveStateHandler
	*/
	protected abstract ATxtReadFormatStateBase0.IStateHandler getNextHandler(); 
	
	/* *****************************************************************************
	
				Services for subclasses
	
	******************************************************************************/
	/** Acts on {@link #tryEnter()}==true and manages state according to {@link #getNextHandler}.
	Current state handler becomes <code>this</code>.
	@throws EFormatBoundaryExceeded if {@link #pushStateHandler} thrown.*/
	protected final void enterStateHandler()throws EFormatBoundaryExceeded
	{
		if (TRACE) TOUT.println("enterStateHandler("+this+")");
		if (getNextHandler()==null) 
					pushStateHandler(this);
				else
					setStateHandler(this);
	};
	/** Acts on {@link #toNextChar()} figuring out that needs to move to next state
	and manages state according to {@link #getNextHandler}.
	Current state handler becomes either {@link #getNextHandler} or what was recently
	a current state handler.
	*/
	protected final void leaveStateHandler()
	{
		if (TRACE) TOUT.println("leaveStateHandler("+this+")->"+getNextHandler());
		if (getNextHandler()==null) 
					popStateHandler();
				else
					setStateHandler(getNextHandler());
	};
};