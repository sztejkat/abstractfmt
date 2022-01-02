package sztejkat.abstractfmt.testsuite.indicator;
import sztejkat.abstractfmt.testsuite.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
/**
	A test which check if indicator allows opening 
	and closing in different scenarios.
*/
public class TestOpenClose extends ATestCase<Pair>
{
	@Test public void openClose()throws IOException
	{
		/*
			A primary test which check if we can
			open, close write and end then open,close
			read end.
		*/
		enter();
		Pair p = create();
		p.write.open();
		p.write.flush();
		p.write.close();
		p.read.open();
		p.read.close();
		leave();
	};
	
};