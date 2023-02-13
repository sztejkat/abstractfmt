package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.utils.CAdaptivePushBackReader;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormatStateBase0}
	specialized for testing advanced syntax processing
	considering the "catch phrases"
*/
public class Test_ATxtReadFormatStateBase0_Catchers extends ATest
{
		private static final class DUT extends ATxtReadFormatStateBase0<ATxtReadFormat1.TIntermediateSyntax>
		{
					class CRequiredPhrase extends ARequiredPhrase
					{		
							public int catched;
							
						CRequiredPhrase(String p){ super(p); }
						protected DUT that(){ return DUT.this; };
						@Override protected void onCatchPhraseCompleted(StringBuilder collected)throws IOException				
						{
							catched++;
							System.out.println("Catched!");
							super.onCatchPhraseCompleted(collected);
						}
						@Override protected int readImpl()throws IOException
						{
							return in.read();
						};
						@Override protected void unread(char c)throws IOException
						{
							in.unread(c);
						};
						
					};
					
					private final CAdaptivePushBackReader in;
					
				DUT(String text)
				{
					super(0,100); 
					in = new CAdaptivePushBackReader(new StringReader(text));
				};
				@Override protected void closeImpl(){};
				@Override protected void openImpl(){};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
		
	@Test public void testCatched()throws IOException
	{
		enter();
			DUT d = new DUT("mollydolly");
			
			DUT.CRequiredPhrase rq_1 = d.new CRequiredPhrase("molly");
			DUT.CRequiredPhrase rq_2 = d.new CRequiredPhrase("dolly");
			
			d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
			d.pushStateHandler(
							d.new CNextHandler(
									d.new CRequiredHandler(rq_1),
									d.new CRequiredHandler(rq_2)
									)
						);
			
			d.toNextChar();	//should trigger first catch phrase
			Assert.assertTrue(rq_1.catched==1);
			Assert.assertTrue(rq_2.catched==0);
			d.toNextChar();	//should trigger second catch phrase
			Assert.assertTrue(rq_1.catched==1);
			Assert.assertTrue(rq_2.catched==1);
			
		leave();
	};
	
	@Test public void testAlt()throws IOException
	{
		enter();
			DUT d = new DUT("<grant>");
			
			DUT.CRequiredPhrase rq_1 = d.new CRequiredPhrase(">");
			DUT.CRequiredPhrase rq_2 = d.new CRequiredPhrase("<");
			DUT.CRequiredPhrase rq_3 = d.new CRequiredPhrase("grant");
			
			
			d.pushStateHandler(d.new CCannotReadHandler("No more syntax"));
			d.pushStateHandler(
							d.new CRequiredHandler(
								d.new CAlterinativeHandler(
											rq_1,
											d.new CNextHandler(
															rq_2,
															d.new CRequiredHandler(
																	d.new CNextHandler(rq_3,
																		d.new CRequiredHandler(rq_1)
																		)
																	)
															)
											)
							)
						);
			
			d.toNextChar();	//should trigger first catch phrase
			Assert.assertTrue(rq_1.catched==0);
			Assert.assertTrue(rq_2.catched==1);
			Assert.assertTrue(rq_3.catched==0);
			d.toNextChar();	//should trigger second catch phrase
			Assert.assertTrue(rq_1.catched==0);
			Assert.assertTrue(rq_2.catched==1);
			Assert.assertTrue(rq_3.catched==1);
			d.toNextChar();	//should trigger second catch phrase			
			Assert.assertTrue(rq_1.catched==1);
			Assert.assertTrue(rq_2.catched==1);
			Assert.assertTrue(rq_3.catched==1);
			
		leave();
	};
};
