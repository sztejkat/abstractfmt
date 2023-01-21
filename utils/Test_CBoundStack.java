package sztejkat.abstractfmt.utils;
import sztejkat.abstractfmt.*;
import sztejkat.abstractfmt.test.ATest;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.Assert;
/**
	Test for {@link CBoundStack}
*/
public class Test_CBoundStack extends ATest
{
	@Test public void checkIfIsPushedAndPopped()throws EFormatBoundaryExceeded
	{
		enter();
		CBoundStack<String> d = new CBoundStack<String>();
		d.push("marcie");
		d.push("darcie");
		Assert.assertTrue("darcie".equals(d.pop()));
		Assert.assertTrue("marcie".equals(d.peek()));
		Assert.assertTrue("marcie".equals(d.pop()));
		Assert.assertTrue(null==d.peek());
		try{
			d.pop();
			Assert.fail();
		}catch(NoSuchElementException ex){};
		leave();
	};
	@Test public void checkIfStackLimitWorks()throws EFormatBoundaryExceeded
	{
		enter();
		CBoundStack<String> d = new CBoundStack<String>();
		d.setStackLimit(2);
		d.push("marcie");
		d.push("darcie");
		d.pop();
		d.push("darcie");
		try{
			d.push("darcie");
			Assert.fail();
		}catch(EFormatBoundaryExceeded ex){};
		leave();
	};
	@Test public void checkIfStackLimitWorks_during_set()throws EFormatBoundaryExceeded
	{
		enter();
		CBoundStack<String> d = new CBoundStack<String>();		
		d.push("marcie");
		d.push("darcie");
		d.push("darcie");
		try{
			d.setStackLimit(2);
			Assert.fail();
		}catch(IllegalStateException ex){};
		leave();
	};
}; 