package sztejkat.abstractfmt;
import java.io.IOException;

/**
	A test bed for paired {@link ISignalReadFormat}/{@link ISignalWriteFormat}
	using focused on event operations.
	<p>
	Tests in this class are agnostic to whether streams
	are described or not.
*/
public abstract class ATestISignalFormat_Events extends ATestISignalFormatBase
{
	/* -----------------------------------------------------------------
	
			Motion with data-less events
	
	-----------------------------------------------------------------*/
	@org.junit.Test public void testBeginEnd_Flat()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of non-nested events.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.begin("event"+i);
			p.write.end();
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.next()==null);
		};
		p.read.close();
		leave();
	};    
	
	@org.junit.Test public void testBeginEnd_Flat_whatNext()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of non-nested events
			and check how whatNext() works.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.begin("event"+i);
			p.write.end();
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(p.read.next()==null);
		};
		org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
		p.read.close();
		leave();
	}; 
	
	
	
	@org.junit.Test public void testBeginEnd_Nested()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<63;i++)
		{
			p.write.begin("event"+i);			
		};
		for(int i=0;i<63;i++)
		{
			p.write.end();			
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<63;i++)
		{
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
		};
		for(int i=0;i<63;i++)
		{
			org.junit.Assert.assertTrue(p.read.next()==null);
		};
		p.read.close();
		leave();
	}; 
	
	@org.junit.Test public void testBeginEnd_Nested_whatNext()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events and pool how whatNext() works.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<63;i++)
		{
			p.write.begin("event"+i);			
		};
		for(int i=0;i<63;i++)
		{
			p.write.end();			
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<63;i++)
		{
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
		};
		for(int i=0;i<63;i++)
		{
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(p.read.next()==null);
		};
		org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.EOF);
		p.read.close();
		leave();
	}; 
	
	
	
	
	@org.junit.Test public void testSkip_event()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events and test if nextEvent()
			moves correctly.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.begin("event"+i);
				p.write.begin("deeper");
				p.write.end();
			p.write.end();
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			//Now we skip "event" and the deeper events.
			p.read.skip();
		};
		p.read.close();
		leave();
	};
	
	
	/* -----------------------------------------------------------------
	
			Motion with events filled with data.
	
	-----------------------------------------------------------------*/
	
	@org.junit.Test public void testFilledBeginEnd_Flat_1()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of non-nested events when there
			are primitive data.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			//by calling next now we skip integers in front of event.
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			//and this skips integers inside.
			org.junit.Assert.assertTrue(p.read.next()==null);
		};
		//We should be able to read last int.
		org.junit.Assert.assertTrue(p.read.readInt()==1000+15);
		p.read.close();
		leave();
	};     
	
	@org.junit.Test public void testFilledBeginEnd_Flat_2()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of non-nested events when there
			are primitive data, but this time we read a part of those data.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			//by calling next now we skip integers in front of event.
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			//and this skips integers inside.
			org.junit.Assert.assertTrue(p.read.next()==null);
			//Now we always read some of data.
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};  
	
	@org.junit.Test public void testFilledBeginEnd_Flat_3()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of non-nested events when there
			are primitive data, but this time we read a part of those data.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			//by calling next now we skip integers in front of event.
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.readInt()==-100);
			org.junit.Assert.assertTrue(p.read.next()==null);
			//Now we always read some of data.
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};     
	
	
	@org.junit.Test public void testFilledBeginEnd_Nested_1()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events when there
			are primitive data and we read it as it should.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100+i);
				p.write.begin("deep");
					p.write.writeInt(-300);
				p.write.end();
				p.write.writeInt(-200+i);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(p.read.readInt()==i);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.readInt()==-100+i);
				org.junit.Assert.assertTrue(("deep").equals(p.read.next()));
				org.junit.Assert.assertTrue(p.read.readInt()==-300);
				org.junit.Assert.assertTrue(p.read.next()==null);
				org.junit.Assert.assertTrue(p.read.readInt()==-200+i);
			org.junit.Assert.assertTrue(p.read.next()==null);			
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};     
	
	
	@org.junit.Test public void testFilledBeginEnd_Nested_whatNext()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events when there
			are primitive data and we read it as it should.
			
			We also check what is returned from whatNext();
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100+i);
				p.write.begin("deep");
					p.write.writeInt(-300);
				p.write.end();
				p.write.writeInt(-200+i);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(p.read.whatNext()>0);	//type agnostic test!
			org.junit.Assert.assertTrue(p.read.readInt()==i);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.whatNext()>0);	//type agnostic test!
			org.junit.Assert.assertTrue(p.read.readInt()==-100+i);
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
				org.junit.Assert.assertTrue(("deep").equals(p.read.next()));
				org.junit.Assert.assertTrue(p.read.whatNext()>0);	//type agnostic test!
				org.junit.Assert.assertTrue(p.read.readInt()==-300);
				org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
				org.junit.Assert.assertTrue(p.read.next()==null);
				org.junit.Assert.assertTrue(p.read.whatNext()>0);	//type agnostic test!
				org.junit.Assert.assertTrue(p.read.readInt()==-200+i);
			org.junit.Assert.assertTrue(p.read.whatNext()==ISignalReadFormat.SIGNAL);
			org.junit.Assert.assertTrue(p.read.next()==null);			
			org.junit.Assert.assertTrue(p.read.whatNext()>0);	//type agnostic test!
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};   
	
	
	@org.junit.Test public void testFilledBeginEnd_Nested_2()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events when there
			are primitive data, but we don't read some of them.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100+i);
				p.write.begin("deep");
					p.write.writeInt(-300);
				p.write.end();
				p.write.writeInt(-200+i);
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(p.read.readInt()==i);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			org.junit.Assert.assertTrue(p.read.readInt()==-100+i);
				org.junit.Assert.assertTrue(("deep").equals(p.read.next()));
				//org.junit.Assert.assertTrue(p.read.readInt()==-300);
				org.junit.Assert.assertTrue(p.read.next()==null);
				//org.junit.Assert.assertTrue(p.read.readInt()==-200+i);
			org.junit.Assert.assertTrue(p.read.next()==null);			
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};     
	
	@org.junit.Test public void testFilledSkip_1()throws IOException
	{
		enter();
		/*	
			A plain test in which we start and close a
			significant amount of nested events when there
			are primitive data and we skip whole events.
		*/	
		Pair  p = create();
		p.write.open();
		for(int i=0;i<16;i++)
		{
			p.write.writeInt(i);
			p.write.begin("event"+i);
				p.write.writeInt(-100+i);
				p.write.begin("deep");
					p.write.writeInt(-300);
				p.write.end();
				p.write.writeInt(-200+i);
				p.write.begin("plank");
					p.write.writeInt(-400);
				p.write.end();
			p.write.end();
			p.write.writeInt(1000+i);
		};
		p.write.close();
		p.read.open();
		for(int i=0;i<16;i++)
		{
			org.junit.Assert.assertTrue(p.read.readInt()==i);
			org.junit.Assert.assertTrue(("event"+i).equals(p.read.next()));
			//Now we don't care of the inside.
			p.read.skip();
			//and we just should get integer after the end.			
			org.junit.Assert.assertTrue(p.read.readInt()==1000+i);
		};
		p.read.close();
		leave();
	};     
};