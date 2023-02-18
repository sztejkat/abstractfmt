package sztejkat.abstractfmt.txt;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test bed for {@link ATxtReadFormatStateBase1}.
*/
public class Test_ATxtReadFormatStateBase1 extends ATest
{
		private static final class DUT extends ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax>
		{
					final AStateHandler<ATxtReadFormat1.TIntermediateSyntax> no_state_handler = 
						new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
					{
						@Override public void toNextChar(){ throw new AbstractMethodError(); };
					};
					final class XSyntaxHandler extends ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>
					{
						XSyntaxHandler(ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax> s){super(s); };
						protected String getCollected(){ return collected.toString(); };
						@Override public void toNextChar(){ throw new AbstractMethodError(); };
						@Override public boolean tryEnter()throws IOException{ throw new AbstractMethodError(); };
					};
					final XSyntaxHandler no_syntax_handler = new XSyntaxHandler(this);
				DUT(String text)
				{
					super(new StringReader(text),0,100); 
				};
				@Override protected void closeImpl(){};
				@Override protected void openImpl(){};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
		
		
    /* ----------------------------------------------------------------
    
    
    		Functionality common to AStateHandler / ASyntaxHandler.
    
    
    
    ----------------------------------------------------------------*/
		
	@Test public void testRead_ViaAStateHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_state_handler);
			Assert.assertTrue(d.no_state_handler.read()=='m');
			Assert.assertTrue(d.no_state_handler.read()=='o');
			Assert.assertTrue(d.no_state_handler.read()==-1);
			Assert.assertTrue(d.getNextChar()==-1);
			Assert.assertTrue(d.getNextSyntaxElement()==null);
		
		leave();
	};
	
	@Test public void testRead_ViaASyntaxHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.read()=='m');
			Assert.assertTrue(d.no_syntax_handler.read()=='o');
			Assert.assertTrue(d.no_syntax_handler.read()==-1);
			Assert.assertTrue(d.getNextChar()==-1);
			Assert.assertTrue(d.getNextSyntaxElement()==null);
		
		leave();
	};
	
	
	
	
	@Test public void testReadAlways_ViaAStateHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_state_handler);
			Assert.assertTrue(d.no_state_handler.readAlways()=='m');
			Assert.assertTrue(d.no_state_handler.readAlways()=='o');
			try{
				Assert.assertTrue(d.no_state_handler.readAlways()==-1);
				Assert.fail();
			}catch(IOException ex){};
			try{
				d.getNextChar();
				Assert.fail();
			}catch(AssertionError ex){};
		leave();
	};
	@Test public void testReadAlways_ViaASyntaxHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='m');
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='o');
			try{
				Assert.assertTrue(d.no_syntax_handler.readAlways()==-1);
				Assert.fail();
			}catch(IOException ex){};
			try{
				d.getNextChar();
				Assert.fail();
			}catch(AssertionError ex){};
		leave();
	};
	
	
	@Test public void testUnread_ViaAStateHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_state_handler);
			Assert.assertTrue(d.no_state_handler.readAlways()=='m');
			d.no_state_handler.unread('x');
			Assert.assertTrue(d.no_state_handler.readAlways()=='x');
			Assert.assertTrue(d.no_state_handler.readAlways()=='o');
			d.no_state_handler.unread("ax");
			Assert.assertTrue(d.no_state_handler.readAlways()=='a');
			Assert.assertTrue(d.no_state_handler.readAlways()=='x');
			d.no_state_handler.unread("zca",1,1);
			Assert.assertTrue(d.no_state_handler.readAlways()=='c');
			Assert.assertTrue(d.no_state_handler.read()==-1);
			
		leave();
	};	
	
	@Test public void testUnread_ASyntaxHandler()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='m');
			d.no_state_handler.unread('x');
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='x');
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='o');
			d.no_state_handler.unread("ax");
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='a');
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='x');
			d.no_state_handler.unread("zca",1,1);
			Assert.assertTrue(d.no_syntax_handler.readAlways()=='c');
			Assert.assertTrue(d.no_syntax_handler.read()==-1);
			
		leave();
	};	
	
	
	
	
	/* ---------------------------------------------------------------
    
    
    		Catch-phrase
    
    
    
    ----------------------------------------------------------------*/
    @Test public void testCollect()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.collect()=='m');
			Assert.assertTrue(d.no_syntax_handler.collect()=='o');
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			try{
				d.no_syntax_handler.collect();
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			try{
				d.getNextChar();
				Assert.fail();
			}catch(AssertionError ex){};
			
		leave();
	};
	@Test public void testTryCollect()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryCollect()=='m');
			Assert.assertTrue(d.no_syntax_handler.tryCollect()=='o');			
			Assert.assertTrue(d.no_syntax_handler.tryCollect()==-1);
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			try{
				d.getNextChar();
				Assert.fail();
			}catch(AssertionError ex){};
			
		leave();
	};
	@Test public void testCollectNone()throws IOException
	{
		enter();
		DUT d = new DUT("");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.collect()==-1);
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			try{
				d.getNextChar();
				Assert.fail();
			}catch(AssertionError ex){};
			
		leave();
	};
	@Test public void testUnreadCollected()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.collect()=='m');
			Assert.assertTrue(d.no_syntax_handler.collect()=='o');
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			d.no_syntax_handler.unread();
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			Assert.assertTrue(d.no_syntax_handler.collect()=='m');
			Assert.assertTrue(d.no_syntax_handler.collect()=='o');
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	/* Note: I do not test canStartWithCollected/canStartWithCollectedCaseInsensitive
			 beacues they are just one-liners calling specials from 
			 SStringUtils which are tested elsewhere. */
	@Test public void testLooksAt()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAt("mo"));
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtTooShort()throws IOException
	{
		enter();
		DUT d = new DUT("<star");
		
			d.setStateHandler(d.no_syntax_handler);
			try{
				d.no_syntax_handler.looksAt("<start");
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			Assert.assertTrue("<star".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtNothing()throws IOException
	{
		enter();
		DUT d = new DUT("");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAt("<start")==false);
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtLong()throws IOException
	{
		enter();
		DUT d = new DUT("<start agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAt("<start"));
			Assert.assertTrue("<start".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtNo()throws IOException
	{
		enter();
		DUT d = new DUT("<stxrt agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.looksAt("<start"));
			Assert.assertTrue("<stx".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	@Test public void testTryLooksAt()throws IOException
	{
		enter();
		DUT d = new DUT("mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAt("mo"));
			Assert.assertTrue("mo".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtTooShort()throws IOException
	{
		enter();
		DUT d = new DUT("<star");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.tryLooksAt("<start"));
			Assert.assertTrue("<star".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtNothing()throws IOException
	{
		enter();
		DUT d = new DUT("");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAt("<start")==false);
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtLong()throws IOException
	{
		enter();
		DUT d = new DUT("<start agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAt("<start"));
			Assert.assertTrue("<start".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtNo()throws IOException
	{
		enter();
		DUT d = new DUT("<stxrt agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.tryLooksAt("<start"));
			Assert.assertTrue("<stx".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	@Test public void testTryLooksAtCaseInsensitive()throws IOException
	{
		enter();
		DUT d = new DUT("mO");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAtCaseInsensitive("mo"));
			Assert.assertTrue("mO".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtCaseInsensitiveTooShort()throws IOException
	{
		enter();
		DUT d = new DUT("<stAr");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.tryLooksAtCaseInsensitive("<start"));
			Assert.assertTrue("<stAr".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtCaseInsensitiveNothing()throws IOException
	{
		enter();
		DUT d = new DUT("");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAtCaseInsensitive("<start")==false);
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtCaseInsensitiveLong()throws IOException
	{
		enter();
		DUT d = new DUT("<StaRt agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.tryLooksAtCaseInsensitive("<start"));
			Assert.assertTrue("<StaRt".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testTryLooksAtCaseInsensitiveNo()throws IOException
	{
		enter();
		DUT d = new DUT("<Stxrt agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.tryLooksAtCaseInsensitive("<start"));
			Assert.assertTrue("<Stx".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	@Test public void testLooksAtCaseInsensitive()throws IOException
	{
		enter();
		DUT d = new DUT("Mo");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAtCaseInsensitive("mo"));
			Assert.assertTrue("Mo".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtCaseInsensitiveTooShort()throws IOException
	{
		enter();
		DUT d = new DUT("<Star");
		
			d.setStateHandler(d.no_syntax_handler);
			try{
				d.no_syntax_handler.looksAtCaseInsensitive("<start");
				Assert.fail();
			}catch(EUnexpectedEof ex){};
			Assert.assertTrue("<Star".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtCaseInsensitiveNothing()throws IOException
	{
		enter();
		DUT d = new DUT("");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAtCaseInsensitive("<start")==false);
			Assert.assertTrue("".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtCaseInsensitiveLong()throws IOException
	{
		enter();
		DUT d = new DUT("<Start agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(d.no_syntax_handler.looksAtCaseInsensitive("<start"));
			Assert.assertTrue("<Start".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	@Test public void testLooksAtCaseInsensitiveNo()throws IOException
	{
		enter();
		DUT d = new DUT("<sTxrt agata>");
		
			d.setStateHandler(d.no_syntax_handler);
			Assert.assertTrue(!d.no_syntax_handler.looksAtCaseInsensitive("<start"));
			Assert.assertTrue("<sTx".equals(d.no_syntax_handler.getCollected()));
			
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------------------------------------------------------
    
    
    		Letst logic behind catch phrases.
    
    
    
    ----------------------------------------------------------------*/
    	private static final class DUT2 extends ATxtReadFormatStateBase1<ATxtReadFormat1.TIntermediateSyntax>
		{	
				final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> ABACUS = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public void toNextChar()throws IOException
					{
						queueNextChar('A',ATxtReadFormat1.TIntermediateSyntax.VOID);
					};
					@Override public boolean tryEnter()throws IOException
					{
						if (looksAt("abacus"))
						{
							setStateHandler(this);
							return true;
						}else
						{
							unread();
							return false;
						}
					}
				};
				
				final ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax> ZORRO = new ASyntaxHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public void toNextChar()throws IOException
					{
						queueNextChar('Z',ATxtReadFormat1.TIntermediateSyntax.VOID);
					};
					@Override public boolean tryEnter()throws IOException
					{
						if (looksAt("zorro"))
						{
							setStateHandler(this);
							return true;
						}else
						{
							unread();
							return false;
						}
					}
				};
				
				final AStateHandler<ATxtReadFormat1.TIntermediateSyntax> zero = new AStateHandler<ATxtReadFormat1.TIntermediateSyntax>(this)
				{
					@Override public void toNextChar()throws IOException
					{
						if (!ZORRO.tryEnter()) if (!ABACUS.tryEnter()) { throw new EBrokenFormat(); };
					};
				};
			
					
				DUT2(String text)
				{
					super(new StringReader(text),0,100); 
				};
				@Override protected void openImpl(){};
				@Override public int getMaxSupportedStructRecursionDepth(){ return -1; };
				@Override public int getMaxSupportedSignalNameLength(){ return 10000; };
		};
    @Test public void testLogic_1()throws IOException
    {
    	enter();
    	
    		final DUT2 d = new DUT2("abacus zorro");
    		
    		d.setStateHandler(d.zero);
    		
    		d.toNextChar();
    		Assert.assertTrue(d.getNextSyntaxElement()==ATxtReadFormat1.TIntermediateSyntax.VOID);
    		Assert.assertTrue(d.getNextChar()=='A');
    		Assert.assertTrue(d.getStateHandler()==d.ABACUS);
    		
    	leave();
    };
};