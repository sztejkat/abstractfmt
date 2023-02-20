package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.EUnexpectedEof;
import sztejkat.abstractfmt.EFormatBoundaryExceeded;
import java.io.IOException;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
/**
	A core state handler implementation for {@link ATxtReadFormatStateBase1}
	focused on consuming characters provided by {@link ATxtReadFormatStateBase1#in()}
*/
public abstract class AStateHandler<TSyntax extends ATxtReadFormat1.ISyntax> 
	   implements ATxtReadFormatStateBase0.IStateHandler
{
					/** A reference to state machine, never null. */
					protected final ATxtReadFormatStateBase1<TSyntax> parser;
		/* ************************************************************************
	
				Construction
	
	 
	    *************************************************************************/			
		/** Creates, servicing specified parser 
		@param parser non-null parser which states and characters queue 
				will be modified by this handler.
		*/
		protected AStateHandler(ATxtReadFormatStateBase1<TSyntax> parser)
		{
			assert(parser!=null);
			this.parser = parser;
		};
		
		/* ************************************************************************
	
				Character access API for subclasses.
	
	
	    *************************************************************************/
		/** Shortcut method.
		@return {@link #parser}.in()
		*/
		protected final CAdaptivePushBackReader in(){ return parser.in(); };
		/** Reads data from {@link ATxtReadFormatStateBase1#in}.
		If encounters end-of-file puts eof indicator into {@link ATxtReadFormatStateBase1#queueNextChar}
		and returns -1. Otherwise just returns the character.
		@return -1 or 0...0xFFFF
		@throws IOException if failed.
		*/
		protected final int read()throws IOException
		{
			int r = in().read();
			assert((r>=-1)&&(r<=0xFFFF));
			if (r==-1)
			{
				parser.queueNextChar(-1,null);
			};
			return r;
		};
		/** Reads data from {@link ATxtReadFormatStateBase1#in}.
		If encounters end-of-file returns -1. Otherwise just returns the character.
		@return -1 or 0...0xFFFF
		@throws IOException if failed.
		*/
		protected final int tryRead()throws IOException
		{
			return in().read();
		};
		/** Reads data from {@link #in}.
		If encounters end-of-file throws {@link EUnexpectedEof}
		 Otherwise just returns the character.
		@return 0...0xFFFF
		@throws IOException if failed.
		@throws EUnexpectedEof if reached end-of-file.
		*/
		protected final char readAlways()throws IOException, EUnexpectedEof
		{
			int r = in().read();
			assert((r>=-1)&&(r<=0xFFFF));
			if (r==-1)
			{
				throw new EUnexpectedEof();
			};
			return (char)r;
		};
		/** Calls <code>{@link #in()}.unread(...).</code>
		@param c --//--
		@throws IOException --//--
		@see CAdaptivePushBackReader#unread(char)
		*/
		protected final void unread(char c)throws IOException{ in().unread(c); };
		/** Calls <code>{@link #in()}.unread(...).</code>
		@param chars --//--
		@throws IOException --//--
		@see CAdaptivePushBackReader#unread(CharSequence)
		*/
		protected final void unread(CharSequence chars)throws IOException{ in().unread(chars); };
		/** Calls <code>{@link #in()}.unread(...).</code>
		@param chars --//--
		@param from --//--
		@param length --//--
		@throws IOException --//--
		@see CAdaptivePushBackReader#unread(CharSequence,int,int)
		*/
		protected final void unread(CharSequence chars,int from, int length)throws IOException
		{
			in().unread(chars,from,length);
		}
		/* ************************************************************************
	
				Common shortcuts.	
	
	    *************************************************************************/
		/** Calls <code>parser</code>
		@param s state handler to push
		@throws EFormatBoundaryExceeded if handlers stack limit was reached. 
		*/
		protected final void pushStateHandler(ATxtReadFormatStateBase0.IStateHandler s)throws EFormatBoundaryExceeded
		{
			parser.pushStateHandler(s);
		};
		/** Calls <code>parser</code>
		@param s state handler to set
		*/
		protected final void setStateHandler(ATxtReadFormatStateBase0.IStateHandler s)
		{
			parser.setStateHandler(s);
		};
		/** Calls <code>parser</code>
		*/
		protected final void popStateHandler()
		{
			parser.popStateHandler();
		};
		/** Calls <code>parser</code>
		@param c see {@link ATxtReadFormatStateBase0#queueNextChar}
		@param syntax --//--
		*/
		protected final void queueNextChar(int c, TSyntax syntax)
		{
			parser.queueNextChar(c,syntax);
		};
		/** Calls <code>parser</code>
		@param c see {@link ATxtReadFormatStateBase0#queueNextCodepoint}
		@param syntax --//--
		*/
		protected final void queueNextCodepoint(int c, TSyntax syntax)
		{
			parser.queueNextCodepoint(c,syntax);
		};
		/** Calls <code>parser</code>
		@return c see {@link ATxtReadFormatStateBase1#getLineInfoMessage}		
		*/
		protected String getLineInfoMessage(){ return parser.getLineInfoMessage(); };
}