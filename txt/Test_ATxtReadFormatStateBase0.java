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
					abstract class ACountingStateHandler implements IStateHandler
					{		
							public int enters;
							public int leaves;
							public int activated;
							public int deactivated;
							
						@Override public void onEnter()
						{
							IStateHandler.super.onEnter();
							Assert.assertTrue(activated==deactivated);//must be inactive
							enters++; 
						};
						@Override public void onLeave()
						{ 
							IStateHandler.super.onLeave();
							Assert.assertTrue(activated==deactivated);//must be inactive							
							leaves++;
							Assert.assertTrue(enters==leaves);
						};
						@Override public void onActivated()
						{
							IStateHandler.super.onActivated();
							Assert.assertTrue(enters>leaves); //must be entered
							Assert.assertTrue(activated==deactivated);//must be inactive
							activated++; 
						};
						@Override public void onDeactivated()
						{
							IStateHandler.super.onDeactivated();
							Assert.assertTrue(enters>leaves); //must be entered
							Assert.assertTrue(activated>deactivated);//must be active
							deactivated++;
							Assert.assertTrue(activated==deactivated);//must be inactive
						};
					};
					class H extends ACountingStateHandler
					{
								final int c;
								final ATxtReadFormat1.TIntermediateSyntax s;
								int calls;
						H(int c, ATxtReadFormat1.TIntermediateSyntax s)
						{
							this.c=c;
							this.s=s;
						};
						@Override public void toNextChar()throws IOException
						{
							calls++;
							System.out.println("called H("+c+")");
							setNextChar(c,s);
						};
					};
					
					class Hqueues extends ACountingStateHandler
					{
								final int [] c;
								final ATxtReadFormat1.TIntermediateSyntax [] s;
								int calls;
						Hqueues(
									int [] c,
									ATxtReadFormat1.TIntermediateSyntax [] s
								 )
						{
							this.c=c;
							this.s=s;
						};
						@Override public void toNextChar()throws IOException
						{
							System.out.println("called Hqueues()");
							for(int i=0;i<c.length;i++)
							{
								System.out.println("->"+c[i]+" "+s[i]);
								queueNextChar(c[i],s[i]);
							};
							calls++;
						};
					};
					
					class Hswitching extends ACountingStateHandler
					{
								final int c;
								final IStateHandler next;
								int calls;
						Hswitching(int c, IStateHandler next)
						{
							this.c=c;
							this.next = next;
						};
						@Override public void toNextChar()throws IOException
						{
							calls++;
							System.out.println("called Hswitching("+c+")");
							if (calls>=c)
							{
								System.out.println("moving to next");
								setStateHandler(next);
							};
						};
					};
					
					
					final H H1   = new H(1,ATxtReadFormat1.TIntermediateSyntax.VOID);
					final H Heof = new H(-1,null);
					final Hqueues Hqueues2 = new Hqueues(
											new int[]{3,5},
											new ATxtReadFormat1.TIntermediateSyntax []
											{
												ATxtReadFormat1.TIntermediateSyntax.VOID,
												ATxtReadFormat1.TIntermediateSyntax.TOKEN
											});
					final Hswitching Hs = new Hswitching(3,H1);
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
	
	@Test public void testSetCounts()
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.H1);
			Assert.assertTrue(d.H1.enters==1);
			d.setStateHandler(d.Heof);
			Assert.assertTrue(d.H1.enters==1);
			Assert.assertTrue(d.H1.leaves==1);
			Assert.assertTrue(d.Heof.enters==1);
			Assert.assertTrue(d.Heof.leaves==0);
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
	
	@Test public void testPushPopState_counts()throws EFormatBoundaryExceeded
	{
		enter();
			DUT d = new DUT();
			d.pushStateHandler(d.H1);
				Assert.assertTrue(d.H1.enters==1);
				Assert.assertTrue(d.H1.leaves==0);
				Assert.assertTrue(d.H1.activated==1);
				Assert.assertTrue(d.H1.deactivated==0);
			d.pushStateHandler(d.Heof);
				Assert.assertTrue(d.H1.enters==1);
				Assert.assertTrue(d.H1.leaves==0);
				Assert.assertTrue(d.H1.activated==1);
				Assert.assertTrue(d.H1.deactivated==1);
				
				Assert.assertTrue(d.Heof.enters==1);
				Assert.assertTrue(d.Heof.leaves==0);
				Assert.assertTrue(d.Heof.activated==1);
				Assert.assertTrue(d.Heof.deactivated==0);
				
			d.popStateHandler();
			
				Assert.assertTrue(d.H1.enters==1);
				Assert.assertTrue(d.H1.leaves==0);
				Assert.assertTrue(d.H1.activated==2);
				Assert.assertTrue(d.H1.deactivated==1);
				
				Assert.assertTrue(d.Heof.enters==1);
				Assert.assertTrue(d.Heof.leaves==1);
				Assert.assertTrue(d.Heof.activated==1);
				Assert.assertTrue(d.Heof.deactivated==1);
				
			d.setStateHandler(d.Heof);
			
				Assert.assertTrue(d.Heof.enters==2);
				Assert.assertTrue(d.Heof.leaves==1);
				Assert.assertTrue(d.Heof.activated==2);
				Assert.assertTrue(d.Heof.deactivated==1);
				
				Assert.assertTrue(d.H1.enters==1);
				Assert.assertTrue(d.H1.leaves==1);
				Assert.assertTrue(d.H1.activated==2);
				Assert.assertTrue(d.H1.deactivated==2);
			
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
	
	
	@Test public void testSyntaxQueue_with_small_bursts()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.Hqueues2);			
			for(int i=1; i< 50; i++)
			{
				d.toNextChar();
				System.out.println("i="+i);
				Assert.assertTrue(d.Hqueues2.calls==i);
				Assert.assertTrue(d.getNextSyntaxElement()==ATxtReadFormat1.TIntermediateSyntax.VOID);
				Assert.assertTrue(d.getNextChar()==3);
				Assert.assertTrue(d.getNextSyntaxElement()==ATxtReadFormat1.TIntermediateSyntax.VOID);
				Assert.assertTrue(d.getNextChar()==3);
				Assert.assertTrue(d.getNextSyntaxElement()==ATxtReadFormat1.TIntermediateSyntax.VOID);
				Assert.assertTrue(d.getNextChar()==3);
			
				d.toNextChar();
				Assert.assertTrue(d.Hqueues2.calls==i);
				Assert.assertTrue(d.getNextSyntaxElement()==ATxtReadFormat1.TIntermediateSyntax.TOKEN);
				Assert.assertTrue(d.getNextChar()==5);
			
			};
			
		leave();
	};
	
	@Test public void testSyntaxQueueOverflow()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.H1);
			//Fill in queue sure to trigger overflows several times.
			System.out.println("filling");
			for(int i=0; i< 250; i++)
			{
				ATxtReadFormat1.TIntermediateSyntax v = ((i & 0x01) !=0) ? 
										ATxtReadFormat1.TIntermediateSyntax.VOID:
										ATxtReadFormat1.TIntermediateSyntax.SIG_END;
									 
				d.queueNextChar(i,v);
			};
			System.out.println("picking");
			//And pick it up.
			//Since we filled it by false operation first toNextChar() would
			//drop our first op. Thous we first test, then nextchar.
			for(int i=0; i< 250; i++)
			{				
				ATxtReadFormat1.TIntermediateSyntax v = ((i & 0x01) !=0) ? 
										ATxtReadFormat1.TIntermediateSyntax.VOID:
										ATxtReadFormat1.TIntermediateSyntax.SIG_END;
				System.out.println("i="+i+" "+d.getNextSyntaxElement()+" expected "+v+" "+d.getNextChar());
				Assert.assertTrue(d.getNextSyntaxElement()==v);
				Assert.assertTrue(d.getNextChar()==i);
				d.toNextChar();
				//A last call to next means, we allready picked up everything
				Assert.assertTrue(d.H1.calls== ((i<249) ? 0 : 1));
			};
			//and after last we should have one additional call.
			Assert.assertTrue(d.H1.calls==1);
			
		leave();
	};
	
	
	@Test(timeout=5000) public void testHandlerLoops()throws IOException
	{
		enter();
			DUT d = new DUT();
			d.setStateHandler(d.Hs);			
			d.toNextChar();
			Assert.assertTrue(d.Hs.calls==3);
			Assert.assertTrue(d.H1.calls==1);
		leave();
	};
	
	
	
	
};