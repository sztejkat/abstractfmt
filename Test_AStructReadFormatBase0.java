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
	
	@Test public void testBasicPrimitiveReads()throws IOException
	{
		/*
			We test if we can correctly read primitive data
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										ELMT_BOOLEAN.valueOf(true),
										ELMT_BOOLEAN.valueOf(false),
										ELMT_BYTE.valueOf((byte)33),
										ELMT_CHAR.valueOf('c'),
										ELMT_SHORT.valueOf((short)-13444),
										ELMT_INT.valueOf(123323334),
										ELMT_LONG.valueOf(34844484L),
										ELMT_FLOAT.valueOf(334.3f),
										ELMT_DOUBLE.valueOf(234.3e33)
									}),-1,1000
							);
				d.open();
				Assert.assertTrue(d.readBoolean()==true);
				Assert.assertTrue(d.readBoolean()==false);
				Assert.assertTrue(d.readByte()==(byte)33);
				Assert.assertTrue(d.readChar()=='c');
				Assert.assertTrue(d.readShort()==(short)-13444);
				Assert.assertTrue(d.readInt()==123323334);
				Assert.assertTrue(d.readLong()==34844484L);
				Assert.assertTrue(d.readFloat()==334.3f);
				Assert.assertTrue(d.readDouble()==234.3e33);
				//And eof handling.
				try{
					d.readInt();
					Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	
	@Test public void testBasicPrimitiveBlockRead_int()throws IOException
	{
		/*
			We test if we can correctly read block, based on integer case
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45)
									}),-1,1000
							);
				d.open();
				int [] t = new int [32];
				Assert.assertTrue(d.readIntBlock(t,1,100)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				//and if we correctly poll for eof?
				//Notice, according to contract -1 is retured on logic eof (signal)
				//while physical eof is an exception
				try{
						d.readIntBlock(t,1,100);
				Assert.fail();
				}catch(EEof ex){ System.out.println(ex); };
		leave();
	};
	
	
	@Test public void testBasicPrimitiveEnclosedBlockRead_int()throws IOException
	{
		/*
			We test if we can correctly read block, based on integer case,
			when it is enclosed in signals.
		*/
		enter();
				DUT d = new DUT(
							new CArrayIterator<IObjStructFormat0>(
									new IObjStructFormat0[]
									{
										new SIG_BEGIN("start"),
										BLK_INT.valueOf(44),
										BLK_INT.valueOf(14),
										BLK_INT.valueOf(45),
										SIG_END.INSTANCE,
										ELMT_CHAR.valueOf('4'),
									}),-1,1000
							);
				d.open();
				Assert.assertTrue("start".equals(d.next()));
				int [] t = new int [32];
				Assert.assertTrue(d.readIntBlock(t,1,100)==3);
				Assert.assertTrue(t[1]==44);
				Assert.assertTrue(t[2]==14);
				Assert.assertTrue(t[3]==45);
				Assert.assertTrue(d.readIntBlock(t,1,100)==-1);
				Assert.assertTrue(d.readIntBlock(t,1,100)==-1);
				Assert.assertTrue(d.readIntBlock(t,1,100)==-1);
				Assert.assertTrue(null==d.next());
				Assert.assertTrue(d.readChar()=='4');
		leave();
	};
};