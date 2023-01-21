package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormatStateBase0}.
*/
public class Test_ATxtReadFormatStateBase0 extends ATest
{
		private static final class DUT extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>
		{
					class H extends AStateHandler
					{
								final int c;
								final ATxtReadFormat1.TIntermediateSyntax s;
						H(int c, ATxtReadFormat1.TIntermediateSyntax s)
						{
							this.c=c;
							this.s=s;
						};
						protected void toNextChar()throws IOException
						{
							System.out.println("called H("+c+")");
							setNextChar(c,s);
						};
					};
					
					final H H1   = new H(1,ATxtReadFormat1.TIntermediateSyntax.VOID);
					final H Heof = new H(-1,null);
				DUT()
				{
					super(0,100); 
				};
				@Override protected void closeImpl(){};
				@Override protected void openImpl(){};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
	@Test public void testCanSetStateStack()
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.H1);
			Assert.assertTrue(d.getStateHandler()==d.H1);
			d.setStateHandler(d.Heof);
			Assert.assertTrue(d.getStateHandler()==d.Heof);
		leave();
	};
	
	@Test public void testCanPushPopState()throws EFormatBoundaryExceeded
	{
		enter();
			DUT d = new DUT();
			d.pushStateHandler(d.H1);
			d.pushStateHandler(d.Heof);
			Assert.assertTrue(d.getStateHandler()==d.Heof);
			d.popStateHandler();
			Assert.assertTrue(d.getStateHandler()==d.H1);			
			try{
				d.popStateHandler();
			}catch(NoSuchElementException ex){};
		leave();
	};
	
	@Test public void testLimitsStackHandler()throws EFormatBoundaryExceeded
	{
		enter();
			DUT d = new DUT();
			d.setHandlerStackLimit(2);
			d.pushStateHandler(d.H1);
			d.pushStateHandler(d.Heof);
			try{
				d.pushStateHandler(d.H1);
			}catch(EFormatBoundaryExceeded ex){};
		leave();
	};
	
	@Test public void testLimitsStackHandlerOnSet()throws EFormatBoundaryExceeded
	{
		enter();
			DUT d = new DUT();			
			d.pushStateHandler(d.H1);
			d.pushStateHandler(d.Heof);
			d.pushStateHandler(d.H1);
			try{
				d.setHandlerStackLimit(2);
			}catch(IllegalStateException ex){};
		leave();
	};
	
	@Test public void testCallsCurrent()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.H1);
			d.toNextChar();
			Assert.assertTrue(d.getNextSyntaxElement()==d.H1.s);
			Assert.assertTrue(d.getNextChar()==d.H1.c);
			d.setStateHandler(d.Heof);
			d.toNextChar();
			Assert.assertTrue(d.getNextSyntaxElement()==d.Heof.s);
			Assert.assertTrue(d.getNextChar()==d.Heof.c);
		leave();
	};
};