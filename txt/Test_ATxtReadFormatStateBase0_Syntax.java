package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormatStateBase0}
	specialized for testing advanced syntax processing.
*/
public class Test_ATxtReadFormatStateBase0_Syntax extends ATest
{
		private static final class DUT extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>
		{
					class CCountingStateHandler extends AStateHandler
					{		
							public int enters;
							public int leaves;
							public int calls;
						protected DUT that(){ return DUT.this; };
						@Override protected void onEnter(){ enters++; };
						@Override protected void onLeave(){ leaves++;};
						@Override protected void toNextChar()throws IOException
						{
							calls++;
							System.out.println("CCountingStateHandler.toNextChar() calls="+calls);
						}
					};
					
					class CRecognized extends CCountingStateHandler
					{		
							private final int chars_to_report;
						CRecognized(int chars_to_report){ this.chars_to_report = chars_to_report; };
						@Override protected void onEnter(){ enters++; };
						@Override protected void onLeave(){ leaves++;};
						@Override protected void toNextChar()throws IOException
						{
							System.out.println("CRecognized.toNextChar()");
							super.toNextChar();
							if (calls<chars_to_report)
							{
								//process recognized character.
								that().queueNextChar('c',DUT.TIntermediateSyntax.VOID);
							}
							else
							{
								System.out.println("CRecognized.toNextChar(), last character");
								//we processed everything, but still RECOGNIZED it.
								that().queueNextChar('c',DUT.TIntermediateSyntax.VOID);
								that().popStateHandler();
							};
						}
					};
					
					class CNotRecognized extends CCountingStateHandler
					{		
							
						@Override protected void onEnter(){ enters++; };
						@Override protected void onLeave(){ leaves++;};
						@Override protected void toNextChar()throws IOException
						{
							System.out.println("CNotRecognized.toNextChar()");
							super.toNextChar();
							that().popStateHandler();
						}
					};
					
					
					class CRecognizedNTimes extends CCountingStateHandler
					{		
							public int chars_recognized;
							private final int chars_to_report;
							public int times;
						CRecognizedNTimes(int chars_to_report,int times)
						{
							this.chars_to_report = chars_to_report;
							this.times = times;
						};
						@Override protected void onEnter(){ enters++; };
						@Override protected void onLeave(){ leaves++;};
						@Override protected void toNextChar()throws IOException
						{
							System.out.println("CRecognizedNTimes.toNextChar(), times="+times);
							super.toNextChar();
							if (times==0) 
							{
								System.out.println("CRecognizedNTimes.toNextChar(), not recognizing anymore.");
								that().popStateHandler();
							}else
							{						
								chars_recognized++;
								if (chars_recognized<chars_to_report)
								{
									//process recognized character.
									that().queueNextChar('c',DUT.TIntermediateSyntax.VOID);
									System.out.println("CRecognizedNTimes.toNextChar(), my char");
								}
								else
								{
									times--;
									chars_recognized = 0;
									System.out.println("CRecognizedNTimes.toNextChar(), last character");
									//we processed everything, but still RECOGNIZED it.
									that().queueNextChar('c',DUT.TIntermediateSyntax.VOID);
									that().popStateHandler();
								};
							};
						}
					};
					
				DUT()
				{
					super(0,100); 
				};
				@Override protected void closeImpl(){};
				@Override protected void openImpl(){};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
		
		
	@Test public void testSingleRequiredElement()throws IOException
	{
		enter();
		/*
			This is a simple test in which we do define
			a syntax which consist of single required element.
		*/
		
		DUT d = new DUT();
		DUT.CCountingStateHandler recognized = d.new CRecognized(2);
		
		d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
		d.pushStateHandler(
					d.new CRequiredHandler(recognized,"unrecognized")
						);
		
		d.toNextChar();
		Assert.assertTrue(recognized.enters==1);
		Assert.assertTrue(recognized.leaves==0);
		Assert.assertTrue(recognized.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized.enters==1);
		Assert.assertTrue(recognized.leaves==1);
		Assert.assertTrue(recognized.calls==2);
		
		try{
			d.toNextChar();
			Assert.fail();
		}catch(EBrokenFormat ex)
		{
			System.out.println(ex);
			Assert.assertTrue(ex.getMessage().equals("No more syntax"));
		};
		Assert.assertTrue(recognized.enters==1);
		Assert.assertTrue(recognized.leaves==1);
		Assert.assertTrue(recognized.calls==2);
		
		
		leave();
	};
	
	
	
	@Test public void testThreeRequiredElements()throws IOException
	{
		enter();
		/*
			This is a simple test in which we do define
			a syntax which consist of three required elements.
		*/
		
		DUT d = new DUT();
		DUT.CCountingStateHandler recognized_0 = d.new CRecognized(2);
		DUT.CCountingStateHandler recognized_1 = d.new CRecognized(2);
		DUT.CCountingStateHandler recognized_2 = d.new CRecognized(2);
		
		d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
		d.pushStateHandler(			
					d.new CNextHandler(
							d.new CRequiredHandler(recognized_0),
								d.new CNextHandler(
										d.new CRequiredHandler(recognized_1),
										d.new CRequiredHandler(recognized_2)
										)
							  )
						);
		
		d.toNextChar();
		Assert.assertTrue(recognized_0.enters==1);
		Assert.assertTrue(recognized_0.leaves==0);
		Assert.assertTrue(recognized_0.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized_0.enters==1);
		Assert.assertTrue(recognized_0.leaves==1);
		Assert.assertTrue(recognized_0.calls==2);
		
		d.toNextChar();
		Assert.assertTrue(recognized_1.enters==1);
		Assert.assertTrue(recognized_1.leaves==0);
		Assert.assertTrue(recognized_1.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized_1.enters==1);
		Assert.assertTrue(recognized_1.leaves==1);
		Assert.assertTrue(recognized_1.calls==2);
		
		d.toNextChar();
		Assert.assertTrue(recognized_2.enters==1);
		Assert.assertTrue(recognized_2.leaves==0);
		Assert.assertTrue(recognized_2.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized_2.enters==1);
		Assert.assertTrue(recognized_2.leaves==1);
		Assert.assertTrue(recognized_2.calls==2);
		
		try{
			d.toNextChar();
			Assert.fail();
		}catch(EBrokenFormat ex)
		{
			System.out.println(ex);
			Assert.assertTrue(ex.getMessage().equals("No more syntax"));
		};
		
		leave();
	};
	
	
	@Test public void testThreeRequiredElementsWithOneMissing()throws IOException
	{
		enter();
		/*
			This is a simple test in which we do define
			a syntax which consist of three required elements.
		*/
		
		DUT d = new DUT();
		DUT.CCountingStateHandler recognized_0 = d.new CRecognized(2);
		DUT.CCountingStateHandler recognized_1 = d.new CRecognized(2);
		DUT.CCountingStateHandler recognized_2 = d.new CNotRecognized();
		
		d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
		d.pushStateHandler(			
					d.new CNextHandler(
							d.new CRequiredHandler(recognized_0),
								d.new CNextHandler(
										d.new CRequiredHandler(recognized_1),
										d.new CRequiredHandler(recognized_2,"U")
										)
							  )
						);
		
		d.toNextChar();
		Assert.assertTrue(recognized_0.enters==1);
		Assert.assertTrue(recognized_0.leaves==0);
		Assert.assertTrue(recognized_0.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized_0.enters==1);
		Assert.assertTrue(recognized_0.leaves==1);
		Assert.assertTrue(recognized_0.calls==2);
		
		d.toNextChar();
		Assert.assertTrue(recognized_1.enters==1);
		Assert.assertTrue(recognized_1.leaves==0);
		Assert.assertTrue(recognized_1.calls==1);
		
		d.toNextChar();
		Assert.assertTrue(recognized_1.enters==1);
		Assert.assertTrue(recognized_1.leaves==1);
		Assert.assertTrue(recognized_1.calls==2);
		
		try{
			d.toNextChar();
			Assert.fail();
		}catch(EBrokenFormat ex)
		{
			System.out.println(ex);
			Assert.assertTrue(ex.getMessage().equals("U"));
		};
		
		leave();
	};
	
	
	
	@Test public void testRepeatElement()throws IOException
	{
		enter();
		/*
			This is a simple test in which we do define
			a syntax which consist of one repeating elements
		*/
		
		DUT d = new DUT();
		DUT.CRecognizedNTimes recognized_0 = d.new CRecognizedNTimes(3,2);
		
		d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
		d.pushStateHandler(			
						d.new CRepeatHandler(recognized_0)
						);
		
		d.toNextChar();
		d.toNextChar();
		d.toNextChar();
		
		d.toNextChar();
		d.toNextChar();
		d.toNextChar();
		Assert.assertTrue(recognized_0.calls==6);
		Assert.assertTrue(recognized_0.times==0);
		
		try{
			d.toNextChar();
			Assert.fail();
		}catch(EBrokenFormat ex)
		{
			System.out.println(ex);
			Assert.assertTrue(ex.getMessage().equals("No more syntax"));
		};
		
		leave();
	};
	
};