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
			Test if open() do correctly direct to openImpl
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
				dut.close()
				try{
					dut.open();
					Assert.fail();
				}catch(EClosed ex){ System.out.println(ex); };
				Assert.assertTrue(dut._closeImpl==1);
				Assert.assertTrue(dut._openImpl==1);
		leave();
	};
};