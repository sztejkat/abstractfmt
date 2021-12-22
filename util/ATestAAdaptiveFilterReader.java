package sztejkat.abstractfmt.util;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;

abstract class ATestAAdaptiveFilterReader extends sztejkat.utils.test.ATest
{
		/** Creates test case which will use fill() to fill output buffer
		with specified sequence of strigs 
		@param sq sequence of strings to put in buffer on fill() requests
		@return test class
		*/
		protected abstract AAdaptiveFilterReader create(String [] sq);
		
	@Test(timeout=1000) public void test_1()throws IOException
	{
		enter();
		/*
			Just test if two fills will returns a consistent, continous block
			when read char-by char.
		*/
		AAdaptiveFilterReader f = create(new String[]{"Plasma","Dragon"});
		
		StringBuilder sb = new StringBuilder();
		for(;;)
		{
			int c = f.read();
			if (c==-1) break;
			sb.append((char)c);
		};
		
		System.out.println(sb);
		
		Assert.assertTrue(sb.toString().equals("PlasmaDragon"));
		
		leave();
	};
	
	@Test(timeout=1000) public void test_2()throws IOException
	{
		enter();
		/*
			Just test if two fills will returns a consistent, continous block
			when read in bulk single operation
		*/
		AAdaptiveFilterReader f = create(new String[]{"Plasma","Dragon"});
		
		StringBuilder sb = new StringBuilder();
		char [] c = new char[1024];
		int r = f.read(c,10,1024);
		sb.append(c,10,r);
		System.out.println(sb);
		
		Assert.assertTrue(sb.toString().equals("PlasmaDragon"));
		
		leave();
	};
	
	@Test(timeout=1000) public void test_3()throws IOException
	{
		enter();
		/*
			Just test if a sequence of block reads will reconstruct 
			a complete text.
		*/
		AAdaptiveFilterReader f = create(new String[]{"Plasma","Dragon"});
		
		StringBuilder sb = new StringBuilder();
		char [] c = new char[22];
		for(;;)
		{
			int r = f.read(c,10,2);
			if (r==-1) break;			
			sb.append(c,10,r);
			if (r!=2) break;
		};
		System.out.println(sb);
		Assert.assertTrue(sb.toString().equals("PlasmaDragon"));
		
		leave();
	};
	
	
	@Test(timeout=1000) public void test_4()throws IOException
	{
		enter();
		/*
			Test if read char-by char will then continously return eof.
		*/
		AAdaptiveFilterReader f = create(new String[]{"Plasma","Dragon"});
		
		StringBuilder sb = new StringBuilder();
		for(;;)
		{
			int c = f.read();
			if (c==-1)
			{
					Assert.assertTrue(f.read()==-1);
					Assert.assertTrue(f.read()==-1);
					Assert.assertTrue(f.read()==-1);
					Assert.assertTrue(f.read()==-1);
					Assert.assertTrue(f.read()==-1);
			 		break;
			 };
			sb.append((char)c);
		};
		
		System.out.println(sb);
		
		Assert.assertTrue(sb.toString().equals("PlasmaDragon"));
		
		leave();
	};
	
	
	@Test(timeout=1000) public void test_5()throws IOException
	{
		enter();
		/*
			Test if read bulk will then continously return eof.
		*/
		AAdaptiveFilterReader f = create(new String[]{"Plasma","Dragon"});
		
		StringBuilder sb = new StringBuilder();
		char [] c = new char[1024];
		int r = f.read(c,10,1024);		
		sb.append(c,10,r);
		System.out.println(sb);		
		Assert.assertTrue(sb.toString().equals("PlasmaDragon"));
		
		Assert.assertTrue(f.read(c,10,1024)==-1);
		Assert.assertTrue(f.read(c,10,1024)==-1);
		Assert.assertTrue(f.read(c,10,1024)==-1);
		
		leave();
	};
};