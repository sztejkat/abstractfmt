package sztejkat.abstractfmt;
import sztejkat.abstractfmt.obj.*;
import sztejkat.abstractfmt.utils.*;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
/**
	A test bed for {@link AStructReadFormatBase0}, stand alone junit tests.
	<p>
	Note: Those tests are based on {@link CObjStructReadFormat0} implementation
	since this is an easiest way to check how exactly the write format do behave
	in its implementation-specific aspects
*/
public class Test_AStructReadFormatBase0 extends sztejkat.abstractfmt.test.ATest
{
				/** Just a convinience implementation, if for the future I would have to
					change it or modify */
					private static final class DUT extends CObjStructReadFormat0
					{
						public DUT(Iterator<IObjStructFormat0> stream,
											  int max_supported_recursion_depth,
											  int max_supported_name_length
											  ){ super(stream,
														max_supported_recursion_depth,
														max_supported_name_length
														);};
								int _openImpl;	
						@Override protected void openImpl()throws IOException{_openImpl++;};
								int _closeImpl;
						@Override protected void closeImpl()throws IOException{_closeImpl++;};
								
					};
					
	@Test public void testOpenClose()throws IOException
	{
		/*
			We test if open() can be done twice and close too.
			We also check if can't re open closed.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
									}),-1,1000
							);
				d.open();
				Assert.assertTrue(d._openImpl==1);
				d.open();
				Assert.assertTrue(d._openImpl==1);
				d.close();
				Assert.assertTrue(d._closeImpl==1);
				d.close();
				Assert.assertTrue(d._closeImpl==1);
				
				try{
					d.open();
					Assert.fail();
				}catch(EClosed ex){};
		leave();
	};
};