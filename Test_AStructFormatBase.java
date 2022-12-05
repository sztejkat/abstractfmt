package sztejkat.abstractfmt;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
	An implementation-specific test bed for {@link AStructFormatBase} 
*/
public class Test_AStructFormatBase extends sztejkat.abstractfmt.test.ATest
{
			/** A test bed, mostly dummy services. */
			private static final class DUT extends AStructFormatBase
			{	
						private final int max_supported_name_length;
						private final int max_supported_recursion_depth;
						
					public DUT(int max_supported_name_length,
							   int max_supported_recursion_depth)
					{
						this.max_supported_name_length = max_supported_name_length;
						this.max_supported_recursion_depth = max_supported_recursion_depth;
					};
					/* *************************************************
								IFormatLimits					
					***************************************************/
					@Override public int getMaxSupportedStructRecursionDepth(){ return max_supported_recursion_depth; };
					@Override public int getMaxSupportedSignalNameLength(){ return max_supported_name_length; };
					/* *************************************************
								AStructFormatBase					
					***************************************************/
							int _openImpl;
					@Override protected void openImpl()throws IOException{ _openImpl++; };
							int _closeImpl;
					@Override protected void closeImpl()throws IOException{ _closeImpl++; };
					
							int _startBooleanBlock, _endBooleanBlock;							
					@Override protected void startBooleanBlock()throws IOException{ _startBooleanBlock++; };
					@Override protected void endBooleanBlock()throws IOException{ _endBooleanBlock++; };
					
					
							int _startByteBlock, _endByteBlock;							
					@Override protected void startByteBlock()throws IOException{ _startByteBlock++; };
					@Override protected void endByteBlock()throws IOException{ _endByteBlock++; };
					
					
							int _startCharBlock, _endCharBlock;							
					@Override protected void startCharBlock()throws IOException{ _startCharBlock++; };
					@Override protected void endCharBlock()throws IOException{ _endCharBlock++; };
					
					
							int _startShortBlock, _endShortBlock;							
					@Override protected void startShortBlock()throws IOException{ _startShortBlock++; };
					@Override protected void endShortBlock()throws IOException{ _endShortBlock++; };
					
							int _startIntBlock, _endIntBlock;							
					@Override protected void startIntBlock()throws IOException{ _startIntBlock++; };
					@Override protected void endIntBlock()throws IOException{ _endIntBlock++; };
					
					
							int _startLongBlock, _endLongBlock;							
					@Override protected void startLongBlock()throws IOException{ _startLongBlock++; };
					@Override protected void endLongBlock()throws IOException{ _endLongBlock++; };
					
					
							int _startFloatBlock, _endFloatBlock;							
					@Override protected void startFloatBlock()throws IOException{ _startFloatBlock++; };
					@Override protected void endFloatBlock()throws IOException{ _endFloatBlock++; };
					
							int _startDoubleBlock, _endDoubleBlock;							
					@Override protected void startDoubleBlock()throws IOException{ _startDoubleBlock++; };
					@Override protected void endDoubleBlock()throws IOException{ _endDoubleBlock++; };
					
							int _startStringBlock, _endStringBlock;							
					@Override protected void startStringBlock()throws IOException{ _startStringBlock++; };
					@Override protected void endStringBlock()throws IOException{ _endStringBlock++; };
					
			};
			
			
	@Test public void testOpenClose()throws IOException
	{
		/*
			Test if open()/close() do correctly direct to openImpl/closeImpl
		*/
		enter();
			DUT dut = new DUT(10,-1);
				dut.open();
				Assert.assertTrue(dut._openImpl==1);
				for(int i=0;i<10;i++)
				{
					dut.open();
					Assert.assertTrue(dut._openImpl==1);
				};
				dut.close();
				Assert.assertTrue(dut._closeImpl==1);
				for(int i=0;i<10;i++)
				{
					dut.close();
					Assert.assertTrue(dut._closeImpl==1);
				};
		leave();
	};
	
	@Test public void testCantReopen()throws IOException
	{
		/*
			Test if open() can't be done after closing.
		*/
		enter();
			DUT dut = new DUT(10,-1);
				dut.open();
				dut.close();
				try{
					dut.open();
					Assert.fail();
				}catch(EClosed ex){ System.out.println(ex); };
				Assert.assertTrue(dut._closeImpl==1);
				Assert.assertTrue(dut._openImpl==1);
		leave();
	};
	
	@Test public void testIfDetectsNotOpen()throws IOException
	{
		/*
			In this test we check, if validateBooleanBlock()
			do correctly trigger startBooleanBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endBooleanBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			try{
				dut.validateBooleanBlock();
				Assert.fail();
				}catch(ENotOpen ex){System.out.println(ex);};
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_boolean()throws IOException
	{
		/*
			In this test we check, if validateBooleanBlock()
			do correctly trigger startBooleanBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endBooleanBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateBooleanBlock();
			Assert.assertTrue(dut._startBooleanBlock==1);
			Assert.assertTrue(dut._endBooleanBlock==0);
			dut.validateBooleanBlock();
			Assert.assertTrue(dut._startBooleanBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startBooleanBlock==1);
			Assert.assertTrue(dut._endBooleanBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfBooleanBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateBooleanBlock();
			Assert.assertTrue(dut._startBooleanBlock==1);
			Assert.assertTrue(dut._endBooleanBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	@Test public void testIfDetectsBlockStart_byte()throws IOException
	{
		/*
			In this test we check, if validateByteBlock()
			do correctly trigger startByteBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endByteBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateByteBlock();
			Assert.assertTrue(dut._startByteBlock==1);
			Assert.assertTrue(dut._endByteBlock==0);
			dut.validateByteBlock();
			Assert.assertTrue(dut._startByteBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startByteBlock==1);
			Assert.assertTrue(dut._endByteBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfByteBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateByteBlock();
			Assert.assertTrue(dut._startByteBlock==1);
			Assert.assertTrue(dut._endByteBlock==0);
			try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	@Test public void testIfDetectsBlockStart_char()throws IOException
	{
		/*
			In this test we check, if validateCharBlock()
			do correctly trigger startCharBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endCharBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateCharBlock();
			Assert.assertTrue(dut._startCharBlock==1);
			Assert.assertTrue(dut._endCharBlock==0);
			dut.validateCharBlock();
			Assert.assertTrue(dut._startCharBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startCharBlock==1);
			Assert.assertTrue(dut._endCharBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfCharBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateCharBlock();
			Assert.assertTrue(dut._startCharBlock==1);
			Assert.assertTrue(dut._endCharBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_short()throws IOException
	{
		/*
			In this test we check, if validateShortBlock()
			do correctly trigger startShortBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endShortBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateShortBlock();
			Assert.assertTrue(dut._startShortBlock==1);
			Assert.assertTrue(dut._endShortBlock==0);
			dut.validateShortBlock();
			Assert.assertTrue(dut._startShortBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startShortBlock==1);
			Assert.assertTrue(dut._endShortBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfShortBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateShortBlock();
			Assert.assertTrue(dut._startShortBlock==1);
			Assert.assertTrue(dut._endShortBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_int()throws IOException
	{
		/*
			In this test we check, if validateIntBlock()
			do correctly trigger startIntBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endIntBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateIntBlock();
			Assert.assertTrue(dut._startIntBlock==1);
			Assert.assertTrue(dut._endIntBlock==0);
			dut.validateIntBlock();
			Assert.assertTrue(dut._startIntBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startIntBlock==1);
			Assert.assertTrue(dut._endIntBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfIntBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateIntBlock();
			Assert.assertTrue(dut._startIntBlock==1);
			Assert.assertTrue(dut._endIntBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_long()throws IOException
	{
		/*
			In this test we check, if validateLongBlock()
			do correctly trigger startLongBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endLongBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateLongBlock();
			Assert.assertTrue(dut._startLongBlock==1);
			Assert.assertTrue(dut._endLongBlock==0);
			dut.validateLongBlock();
			Assert.assertTrue(dut._startLongBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startLongBlock==1);
			Assert.assertTrue(dut._endLongBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfLongBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateLongBlock();
			Assert.assertTrue(dut._startLongBlock==1);
			Assert.assertTrue(dut._endLongBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_float()throws IOException
	{
		/*
			In this test we check, if validateFloatBlock()
			do correctly trigger startFloatBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endFloatBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateFloatBlock();
			Assert.assertTrue(dut._startFloatBlock==1);
			Assert.assertTrue(dut._endFloatBlock==0);
			dut.validateFloatBlock();
			Assert.assertTrue(dut._startFloatBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startFloatBlock==1);
			Assert.assertTrue(dut._endFloatBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfFloatBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateFloatBlock();
			Assert.assertTrue(dut._startFloatBlock==1);
			Assert.assertTrue(dut._endFloatBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_double()throws IOException
	{
		/*
			In this test we check, if validateDoubleBlock()
			do correctly trigger startDoubleBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endDoubleBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateDoubleBlock();
			Assert.assertTrue(dut._startDoubleBlock==1);
			Assert.assertTrue(dut._endDoubleBlock==0);
			dut.validateDoubleBlock();
			Assert.assertTrue(dut._startDoubleBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startDoubleBlock==1);
			Assert.assertTrue(dut._endDoubleBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfDoubleBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateDoubleBlock();
			Assert.assertTrue(dut._startDoubleBlock==1);
			Assert.assertTrue(dut._endDoubleBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateStringBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
	
	
	
	
	
	
	
	
	@Test public void testIfDetectsBlockStart_string()throws IOException
	{
		/*
			In this test we check, if validateStringBlock()
			do correctly trigger startStringBlock()
			and if terminatePendingBlockOperation()
			do correctly trigger endStringBlock()
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateStringBlock();
			Assert.assertTrue(dut._startStringBlock==1);
			Assert.assertTrue(dut._endStringBlock==0);
			dut.validateStringBlock();
			Assert.assertTrue(dut._startStringBlock==1);
			dut.terminatePendingBlockOperation();
			Assert.assertTrue(dut._startStringBlock==1);
			Assert.assertTrue(dut._endStringBlock==1);
			dut.close();
		leave();
	};
	@Test public void testIfStringBlockPreventsOthers()throws IOException
	{
		/*
			In this test we check, if starting a block prevents
			other blocks and primitive ops. 
			
			Notice, this test is slightly implementation specific
			because I know, that stream which failed can be re-tried.
			If testing would be strictly by contract I should not re-try
			after failure.
		*/
		enter();
			DUT dut = new DUT(10,-1);
			dut.open();
			dut.validateStringBlock();
			Assert.assertTrue(dut._startStringBlock==1);
			Assert.assertTrue(dut._endStringBlock==0);
			try{
					dut.validateByteBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCharBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateShortBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateIntBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateLongBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateFloatBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateDoubleBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateBooleanBlock();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
				try{
					dut.validateCanDoElementaryOp();
					Assert.fail();
				}catch(IllegalStateException ex){System.out.println(ex);};
			dut.close();
		leave();
	};
};