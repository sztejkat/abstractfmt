package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.IStructReadFormat;
import sztejkat.abstractfmt.IStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
/**
	A test case veryfining that all characters can be used in names of signals.
*/
public class ATestCase_ComplexSignalName extends AInterOpTestCase<IStructReadFormat,IStructWriteFormat>
{

	/**
		Test checking if signal name is encoded correctly
		@param name name to check
	@throws IOException .
	*/
	private void testSignalName(String name)throws IOException
	{
			enter();
			System.out.println("Testing name:\""+name+"\"");
			for(int i=0;i<name.length();i++)
			{
				char c = name.charAt(i);
				System.out.println("  ["+i+"]=0x"+Integer.toHexString(c)+
								( (c>=32) ? "("+c+")" : "?"));
			};
			CPair<?,?> p = createTestDevice();
			p.writer.open();
			if (p.writer.getMaxSignalNameLength()<name.length())
						p.writer.setMaxSignalNameLength(name.length()+1);
				 p.writer.begin(name);
			p.writer.close();
			
			p.reader.open();
				Assert.assertTrue(name.equals(p.reader.next()));
			p.reader.close();
	};
	
	@Test public void test_empty()throws IOException{ testSignalName(""); };
	@Test public void test_javatypes()throws IOException
	{
		enter();
		//Note: dump files for all those test will be the same.
			testSignalName("boolean");
			testSignalName("byte");
			testSignalName("char");
			testSignalName("short");
			testSignalName("int");
			testSignalName("long");
			testSignalName("float");
			testSignalName("double");
		leave();
	};
	@Test public void test_javaarrays()throws IOException
	{
		enter();
		//Note: dump files for all those test will be the same.
			testSignalName("boolean[]");
			testSignalName("byte[]");
			testSignalName("char[]");
			testSignalName("short[]");
			testSignalName("int[]");
			testSignalName("long[]");
			testSignalName("float[]");
			testSignalName("double[]");
		leave();
	};
	
	@Test public void test_xml_unfriendly()throws IOException
	{
		enter();
		//Note: dump files for all those test will be the same.
			testSignalName("<event");
			testSignalName("<!--event-->");
			testSignalName("&amp;event");
			testSignalName("\"rookie\"");
			testSignalName("\tmarkiz");
			testSignalName("\nmarkiz");
			testSignalName("\rmarkiz");
		leave();
	};
	
	@Test public void test_json_unfriendly()throws IOException
	{
		enter();
		//Note: dump files for all those test will be the same.
			testSignalName("{event:");
			testSignalName(" markiz");
			testSignalName("\tmarkiz");
			testSignalName("\nmarkiz");
			testSignalName("\rmarkiz");
			testSignalName("\"rookie\"");			
		leave();
	};
	
	@Test public void testZeroInName()throws IOException{ testSignalName("Ba\u0000ca"); };
	/** Tests name built of characters in specified unicode range
	@param from lower boundary, inclusive
	@param to upper boundary, inclusive
	@throws IOException if format failed.
	*/
	private void testRangeName(char from, char to)throws IOException
	{
		enter();
		StringBuilder sb = new StringBuilder();
		for(char i=from; i<=to; i++)
		{
			sb.append(i);
			if (i==0xFFFF) break;  //to prevent stuck on roll over.
		};
		testSignalName(sb.toString());
		leave();
	};
	@Test public void testControlChars()throws IOException{testRangeName((char)0,(char)32); };
	@Test public void testAscii()throws IOException{testRangeName((char)33,(char)127); };
	@Test public void testUpperAscii()throws IOException{testRangeName((char)128,(char)255); };
	
	@Test public void testP0x0100()throws IOException{testRangeName((char)0x0100, (char)0x01FF);}	
	@Test public void testP0x0200()throws IOException{testRangeName((char)0x0200, (char)0x02FF);}
	@Test public void testP0x0300()throws IOException{testRangeName((char)0x0300, (char)0x03FF);}
	@Test public void testP0x0400()throws IOException{testRangeName((char)0x0400, (char)0x04FF);}
	@Test public void testP0x0500()throws IOException{testRangeName((char)0x0500, (char)0x05FF);}
	@Test public void testP0x0600()throws IOException{testRangeName((char)0x0600, (char)0x06FF);}
	@Test public void testP0x0700()throws IOException{testRangeName((char)0x0700, (char)0x07FF);}
	@Test public void testP0x0800()throws IOException{testRangeName((char)0x0800, (char)0x08FF);}
	@Test public void testP0x0900()throws IOException{testRangeName((char)0x0900, (char)0x09FF);}
	@Test public void testP0x0A00()throws IOException{testRangeName((char)0x0A00, (char)0x0AFF);}
	@Test public void testP0x0B00()throws IOException{testRangeName((char)0x0B00, (char)0x0BFF);}
	@Test public void testP0x0C00()throws IOException{testRangeName((char)0x0C00, (char)0x0CFF);}
	@Test public void testP0x0D00()throws IOException{testRangeName((char)0x0D00, (char)0x0DFF);}
	@Test public void testP0x0E00()throws IOException{testRangeName((char)0x0E00, (char)0x0EFF);}
	@Test public void testP0x0F00()throws IOException{testRangeName((char)0x0F00, (char)0x0FFF);}
	
	@Test public void testP0x1100()throws IOException{testRangeName((char)0x1100, (char)0x11FF);}	
	@Test public void testP0x1200()throws IOException{testRangeName((char)0x1200, (char)0x12FF);}
	@Test public void testP0x1300()throws IOException{testRangeName((char)0x1300, (char)0x13FF);}
	@Test public void testP0x1400()throws IOException{testRangeName((char)0x1400, (char)0x14FF);}
	@Test public void testP0x1500()throws IOException{testRangeName((char)0x1500, (char)0x15FF);}
	@Test public void testP0x1600()throws IOException{testRangeName((char)0x1600, (char)0x16FF);}
	@Test public void testP0x1700()throws IOException{testRangeName((char)0x1700, (char)0x17FF);}
	@Test public void testP0x1800()throws IOException{testRangeName((char)0x1800, (char)0x18FF);}
	@Test public void testP0x1900()throws IOException{testRangeName((char)0x1900, (char)0x19FF);}
	@Test public void testP0x1A00()throws IOException{testRangeName((char)0x1A00, (char)0x1AFF);}
	@Test public void testP0x1B00()throws IOException{testRangeName((char)0x1B00, (char)0x1BFF);}
	@Test public void testP0x1C00()throws IOException{testRangeName((char)0x1C00, (char)0x1CFF);}
	@Test public void testP0x1D00()throws IOException{testRangeName((char)0x1D00, (char)0x1DFF);}
	@Test public void testP0x1E00()throws IOException{testRangeName((char)0x1E00, (char)0x1EFF);}
	@Test public void testP0x1F00()throws IOException{testRangeName((char)0x1F00, (char)0x1FFF);}
	
	@Test public void testP0x2100()throws IOException{testRangeName((char)0x2100, (char)0x21FF);}	
	@Test public void testP0x2200()throws IOException{testRangeName((char)0x2200, (char)0x22FF);}
	@Test public void testP0x2300()throws IOException{testRangeName((char)0x2300, (char)0x23FF);}
	@Test public void testP0x2400()throws IOException{testRangeName((char)0x2400, (char)0x24FF);}
	@Test public void testP0x2500()throws IOException{testRangeName((char)0x2500, (char)0x25FF);}
	@Test public void testP0x2600()throws IOException{testRangeName((char)0x2600, (char)0x26FF);}
	@Test public void testP0x2700()throws IOException{testRangeName((char)0x2700, (char)0x27FF);}
	@Test public void testP0x2800()throws IOException{testRangeName((char)0x2800, (char)0x28FF);}
	@Test public void testP0x2900()throws IOException{testRangeName((char)0x2900, (char)0x29FF);}
	@Test public void testP0x2A00()throws IOException{testRangeName((char)0x2A00, (char)0x2AFF);}
	@Test public void testP0x2B00()throws IOException{testRangeName((char)0x2B00, (char)0x2BFF);}
	@Test public void testP0x2C00()throws IOException{testRangeName((char)0x2C00, (char)0x2CFF);}
	@Test public void testP0x2D00()throws IOException{testRangeName((char)0x2D00, (char)0x2DFF);}
	@Test public void testP0x2E00()throws IOException{testRangeName((char)0x2E00, (char)0x2EFF);}
	@Test public void testP0x2F00()throws IOException{testRangeName((char)0x2F00, (char)0x2FFF);}
	
	@Test public void testP0x3100()throws IOException{testRangeName((char)0x3100, (char)0x31FF);}	
	@Test public void testP0x3200()throws IOException{testRangeName((char)0x3200, (char)0x32FF);}
	@Test public void testP0x3300()throws IOException{testRangeName((char)0x3300, (char)0x33FF);}
	@Test public void testP0x3400()throws IOException{testRangeName((char)0x3400, (char)0x34FF);}
	@Test public void testP0x3500()throws IOException{testRangeName((char)0x3500, (char)0x35FF);}
	@Test public void testP0x3600()throws IOException{testRangeName((char)0x3600, (char)0x36FF);}
	@Test public void testP0x3700()throws IOException{testRangeName((char)0x3700, (char)0x37FF);}
	@Test public void testP0x3800()throws IOException{testRangeName((char)0x3800, (char)0x38FF);}
	@Test public void testP0x3900()throws IOException{testRangeName((char)0x3900, (char)0x39FF);}
	@Test public void testP0x3A00()throws IOException{testRangeName((char)0x3A00, (char)0x3AFF);}
	@Test public void testP0x3B00()throws IOException{testRangeName((char)0x3B00, (char)0x3BFF);}
	@Test public void testP0x3C00()throws IOException{testRangeName((char)0x3C00, (char)0x3CFF);}
	@Test public void testP0x3D00()throws IOException{testRangeName((char)0x3D00, (char)0x3DFF);}
	@Test public void testP0x3E00()throws IOException{testRangeName((char)0x3E00, (char)0x3EFF);}
	@Test public void testP0x3F00()throws IOException{testRangeName((char)0x3F00, (char)0x3FFF);}
	
	@Test public void testP0x4100()throws IOException{testRangeName((char)0x4100, (char)0x41FF);}	
	@Test public void testP0x4200()throws IOException{testRangeName((char)0x4200, (char)0x42FF);}
	@Test public void testP0x4300()throws IOException{testRangeName((char)0x4300, (char)0x43FF);}
	@Test public void testP0x4400()throws IOException{testRangeName((char)0x4400, (char)0x44FF);}
	@Test public void testP0x4500()throws IOException{testRangeName((char)0x4500, (char)0x45FF);}
	@Test public void testP0x4600()throws IOException{testRangeName((char)0x4600, (char)0x46FF);}
	@Test public void testP0x4700()throws IOException{testRangeName((char)0x4700, (char)0x47FF);}
	@Test public void testP0x4800()throws IOException{testRangeName((char)0x4800, (char)0x48FF);}
	@Test public void testP0x4900()throws IOException{testRangeName((char)0x4900, (char)0x49FF);}
	@Test public void testP0x4A00()throws IOException{testRangeName((char)0x4A00, (char)0x4AFF);}
	@Test public void testP0x4B00()throws IOException{testRangeName((char)0x4B00, (char)0x4BFF);}
	@Test public void testP0x4C00()throws IOException{testRangeName((char)0x4C00, (char)0x4CFF);}
	@Test public void testP0x4D00()throws IOException{testRangeName((char)0x4D00, (char)0x4DFF);}
	@Test public void testP0x4E00()throws IOException{testRangeName((char)0x4E00, (char)0x4EFF);}
	@Test public void testP0x4F00()throws IOException{testRangeName((char)0x4F00, (char)0x4FFF);}
	
	@Test public void testP0x5100()throws IOException{testRangeName((char)0x5100, (char)0x51FF);}	
	@Test public void testP0x5200()throws IOException{testRangeName((char)0x5200, (char)0x52FF);}
	@Test public void testP0x5300()throws IOException{testRangeName((char)0x5300, (char)0x53FF);}
	@Test public void testP0x5400()throws IOException{testRangeName((char)0x5400, (char)0x54FF);}
	@Test public void testP0x5500()throws IOException{testRangeName((char)0x5500, (char)0x55FF);}
	@Test public void testP0x5600()throws IOException{testRangeName((char)0x5600, (char)0x56FF);}
	@Test public void testP0x5700()throws IOException{testRangeName((char)0x5700, (char)0x57FF);}
	@Test public void testP0x5800()throws IOException{testRangeName((char)0x5800, (char)0x58FF);}
	@Test public void testP0x5900()throws IOException{testRangeName((char)0x5900, (char)0x59FF);}
	@Test public void testP0x5A00()throws IOException{testRangeName((char)0x5A00, (char)0x5AFF);}
	@Test public void testP0x5B00()throws IOException{testRangeName((char)0x5B00, (char)0x5BFF);}
	@Test public void testP0x5C00()throws IOException{testRangeName((char)0x5C00, (char)0x5CFF);}
	@Test public void testP0x5D00()throws IOException{testRangeName((char)0x5D00, (char)0x5DFF);}
	@Test public void testP0x5E00()throws IOException{testRangeName((char)0x5E00, (char)0x5EFF);}
	@Test public void testP0x5F00()throws IOException{testRangeName((char)0x5F00, (char)0x5FFF);}
	
	@Test public void testP0x6100()throws IOException{testRangeName((char)0x6100, (char)0x61FF);}	
	@Test public void testP0x6200()throws IOException{testRangeName((char)0x6200, (char)0x62FF);}
	@Test public void testP0x6300()throws IOException{testRangeName((char)0x6300, (char)0x63FF);}
	@Test public void testP0x6400()throws IOException{testRangeName((char)0x6400, (char)0x64FF);}
	@Test public void testP0x6500()throws IOException{testRangeName((char)0x6500, (char)0x65FF);}
	@Test public void testP0x6600()throws IOException{testRangeName((char)0x6600, (char)0x66FF);}
	@Test public void testP0x6700()throws IOException{testRangeName((char)0x6700, (char)0x67FF);}
	@Test public void testP0x6800()throws IOException{testRangeName((char)0x6800, (char)0x68FF);}
	@Test public void testP0x6900()throws IOException{testRangeName((char)0x6900, (char)0x69FF);}
	@Test public void testP0x6A00()throws IOException{testRangeName((char)0x6A00, (char)0x6AFF);}
	@Test public void testP0x6B00()throws IOException{testRangeName((char)0x6B00, (char)0x6BFF);}
	@Test public void testP0x6C00()throws IOException{testRangeName((char)0x6C00, (char)0x6CFF);}
	@Test public void testP0x6D00()throws IOException{testRangeName((char)0x6D00, (char)0x6DFF);}
	@Test public void testP0x6E00()throws IOException{testRangeName((char)0x6E00, (char)0x6EFF);}
	@Test public void testP0x6F00()throws IOException{testRangeName((char)0x6F00, (char)0x6FFF);}
	
	@Test public void testP0x7100()throws IOException{testRangeName((char)0x7100, (char)0x71FF);}	
	@Test public void testP0x7200()throws IOException{testRangeName((char)0x7200, (char)0x72FF);}
	@Test public void testP0x7300()throws IOException{testRangeName((char)0x7300, (char)0x73FF);}
	@Test public void testP0x7400()throws IOException{testRangeName((char)0x7400, (char)0x74FF);}
	@Test public void testP0x7500()throws IOException{testRangeName((char)0x7500, (char)0x75FF);}
	@Test public void testP0x7600()throws IOException{testRangeName((char)0x7600, (char)0x76FF);}
	@Test public void testP0x7700()throws IOException{testRangeName((char)0x7700, (char)0x77FF);}
	@Test public void testP0x7800()throws IOException{testRangeName((char)0x7800, (char)0x78FF);}
	@Test public void testP0x7900()throws IOException{testRangeName((char)0x7900, (char)0x79FF);}
	@Test public void testP0x7A00()throws IOException{testRangeName((char)0x7A00, (char)0x7AFF);}
	@Test public void testP0x7B00()throws IOException{testRangeName((char)0x7B00, (char)0x7BFF);}
	@Test public void testP0x7C00()throws IOException{testRangeName((char)0x7C00, (char)0x7CFF);}
	@Test public void testP0x7D00()throws IOException{testRangeName((char)0x7D00, (char)0x7DFF);}
	@Test public void testP0x7E00()throws IOException{testRangeName((char)0x7E00, (char)0x7EFF);}
	@Test public void testP0x7F00()throws IOException{testRangeName((char)0x7F00, (char)0x7FFF);}
	
	@Test public void testP0x8100()throws IOException{testRangeName((char)0x8100, (char)0x81FF);}	
	@Test public void testP0x8200()throws IOException{testRangeName((char)0x8200, (char)0x82FF);}
	@Test public void testP0x8300()throws IOException{testRangeName((char)0x8300, (char)0x83FF);}
	@Test public void testP0x8400()throws IOException{testRangeName((char)0x8400, (char)0x84FF);}
	@Test public void testP0x8500()throws IOException{testRangeName((char)0x8500, (char)0x85FF);}
	@Test public void testP0x8600()throws IOException{testRangeName((char)0x8600, (char)0x86FF);}
	@Test public void testP0x8700()throws IOException{testRangeName((char)0x8700, (char)0x87FF);}
	@Test public void testP0x8800()throws IOException{testRangeName((char)0x8800, (char)0x88FF);}
	@Test public void testP0x8900()throws IOException{testRangeName((char)0x8900, (char)0x89FF);}
	@Test public void testP0x8A00()throws IOException{testRangeName((char)0x8A00, (char)0x8AFF);}
	@Test public void testP0x8B00()throws IOException{testRangeName((char)0x8B00, (char)0x8BFF);}
	@Test public void testP0x8C00()throws IOException{testRangeName((char)0x8C00, (char)0x8CFF);}
	@Test public void testP0x8D00()throws IOException{testRangeName((char)0x8D00, (char)0x8DFF);}
	@Test public void testP0x8E00()throws IOException{testRangeName((char)0x8E00, (char)0x8EFF);}
	@Test public void testP0x8F00()throws IOException{testRangeName((char)0x8F00, (char)0x8FFF);}
	
	@Test public void testP0x9100()throws IOException{testRangeName((char)0x9100, (char)0x91FF);}	
	@Test public void testP0x9200()throws IOException{testRangeName((char)0x9200, (char)0x92FF);}
	@Test public void testP0x9300()throws IOException{testRangeName((char)0x9300, (char)0x93FF);}
	@Test public void testP0x9400()throws IOException{testRangeName((char)0x9400, (char)0x94FF);}
	@Test public void testP0x9500()throws IOException{testRangeName((char)0x9500, (char)0x95FF);}
	@Test public void testP0x9600()throws IOException{testRangeName((char)0x9600, (char)0x96FF);}
	@Test public void testP0x9700()throws IOException{testRangeName((char)0x9700, (char)0x97FF);}
	@Test public void testP0x9800()throws IOException{testRangeName((char)0x9800, (char)0x98FF);}
	@Test public void testP0x9900()throws IOException{testRangeName((char)0x9900, (char)0x99FF);}
	@Test public void testP0x9A00()throws IOException{testRangeName((char)0x9A00, (char)0x9AFF);}
	@Test public void testP0x9B00()throws IOException{testRangeName((char)0x9B00, (char)0x9BFF);}
	@Test public void testP0x9C00()throws IOException{testRangeName((char)0x9C00, (char)0x9CFF);}
	@Test public void testP0x9D00()throws IOException{testRangeName((char)0x9D00, (char)0x9DFF);}
	@Test public void testP0x9E00()throws IOException{testRangeName((char)0x9E00, (char)0x9EFF);}
	@Test public void testP0x9F00()throws IOException{testRangeName((char)0x9F00, (char)0x9FFF);}
	
	
	@Test public void testP0xA100()throws IOException{testRangeName((char)0xA100, (char)0xA1FF);}	
	@Test public void testP0xA200()throws IOException{testRangeName((char)0xA200, (char)0xA2FF);}
	@Test public void testP0xA300()throws IOException{testRangeName((char)0xA300, (char)0xA3FF);}
	@Test public void testP0xA400()throws IOException{testRangeName((char)0xA400, (char)0xA4FF);}
	@Test public void testP0xA500()throws IOException{testRangeName((char)0xA500, (char)0xA5FF);}
	@Test public void testP0xA600()throws IOException{testRangeName((char)0xA600, (char)0xA6FF);}
	@Test public void testP0xA700()throws IOException{testRangeName((char)0xA700, (char)0xA7FF);}
	@Test public void testP0xA800()throws IOException{testRangeName((char)0xA800, (char)0xA8FF);}
	@Test public void testP0xA900()throws IOException{testRangeName((char)0xA900, (char)0xA9FF);}
	@Test public void testP0xAA00()throws IOException{testRangeName((char)0xAA00, (char)0xAAFF);}
	@Test public void testP0xAB00()throws IOException{testRangeName((char)0xAB00, (char)0xABFF);}
	@Test public void testP0xAC00()throws IOException{testRangeName((char)0xAC00, (char)0xACFF);}
	@Test public void testP0xAD00()throws IOException{testRangeName((char)0xAD00, (char)0xADFF);}
	@Test public void testP0xAE00()throws IOException{testRangeName((char)0xAE00, (char)0xAEFF);}
	@Test public void testP0xAF00()throws IOException{testRangeName((char)0xAF00, (char)0xAFFF);}
	
	@Test public void testP0xB100()throws IOException{testRangeName((char)0xB100, (char)0xB1FF);}	
	@Test public void testP0xB200()throws IOException{testRangeName((char)0xB200, (char)0xB2FF);}
	@Test public void testP0xB300()throws IOException{testRangeName((char)0xB300, (char)0xB3FF);}
	@Test public void testP0xB400()throws IOException{testRangeName((char)0xB400, (char)0xB4FF);}
	@Test public void testP0xB500()throws IOException{testRangeName((char)0xB500, (char)0xB5FF);}
	@Test public void testP0xB600()throws IOException{testRangeName((char)0xB600, (char)0xB6FF);}
	@Test public void testP0xB700()throws IOException{testRangeName((char)0xB700, (char)0xB7FF);}
	@Test public void testP0xB800()throws IOException{testRangeName((char)0xB800, (char)0xB8FF);}
	@Test public void testP0xB900()throws IOException{testRangeName((char)0xB900, (char)0xB9FF);}
	@Test public void testP0xBA00()throws IOException{testRangeName((char)0xBA00, (char)0xBAFF);}
	@Test public void testP0xBB00()throws IOException{testRangeName((char)0xBB00, (char)0xBBFF);}
	@Test public void testP0xBC00()throws IOException{testRangeName((char)0xBC00, (char)0xBCFF);}
	@Test public void testP0xBD00()throws IOException{testRangeName((char)0xBD00, (char)0xBDFF);}
	@Test public void testP0xBE00()throws IOException{testRangeName((char)0xBE00, (char)0xBEFF);}
	@Test public void testP0xBF00()throws IOException{testRangeName((char)0xBF00, (char)0xBFFF);}
	
	@Test public void testP0xC100()throws IOException{testRangeName((char)0xC100, (char)0xC1FF);}	
	@Test public void testP0xC200()throws IOException{testRangeName((char)0xC200, (char)0xC2FF);}
	@Test public void testP0xC300()throws IOException{testRangeName((char)0xC300, (char)0xC3FF);}
	@Test public void testP0xC400()throws IOException{testRangeName((char)0xC400, (char)0xC4FF);}
	@Test public void testP0xC500()throws IOException{testRangeName((char)0xC500, (char)0xC5FF);}
	@Test public void testP0xC600()throws IOException{testRangeName((char)0xC600, (char)0xC6FF);}
	@Test public void testP0xC700()throws IOException{testRangeName((char)0xC700, (char)0xC7FF);}
	@Test public void testP0xC800()throws IOException{testRangeName((char)0xC800, (char)0xC8FF);}
	@Test public void testP0xC900()throws IOException{testRangeName((char)0xC900, (char)0xC9FF);}
	@Test public void testP0xCA00()throws IOException{testRangeName((char)0xCA00, (char)0xCAFF);}
	@Test public void testP0xCB00()throws IOException{testRangeName((char)0xCB00, (char)0xCBFF);}
	@Test public void testP0xCC00()throws IOException{testRangeName((char)0xCC00, (char)0xCCFF);}
	@Test public void testP0xCD00()throws IOException{testRangeName((char)0xCD00, (char)0xCDFF);}
	@Test public void testP0xCE00()throws IOException{testRangeName((char)0xCE00, (char)0xCEFF);}
	@Test public void testP0xCF00()throws IOException{testRangeName((char)0xCF00, (char)0xCFFF);}
	
	@Test public void testP0xD100()throws IOException{testRangeName((char)0xD100, (char)0xD1FF);}	
	@Test public void testP0xD200()throws IOException{testRangeName((char)0xD200, (char)0xD2FF);}
	@Test public void testP0xD300()throws IOException{testRangeName((char)0xD300, (char)0xD3FF);}
	@Test public void testP0xD400()throws IOException{testRangeName((char)0xD400, (char)0xD4FF);}
	@Test public void testP0xD500()throws IOException{testRangeName((char)0xD500, (char)0xD5FF);}
	@Test public void testP0xD600()throws IOException{testRangeName((char)0xD600, (char)0xD6FF);}
	@Test public void testP0xD700()throws IOException{testRangeName((char)0xD700, (char)0xD7FF);}
	@Test public void testP0xD800()throws IOException{testRangeName((char)0xD800, (char)0xD8FF);}
	@Test public void testP0xD900()throws IOException{testRangeName((char)0xD900, (char)0xD9FF);}
	@Test public void testP0xDA00()throws IOException{testRangeName((char)0xDA00, (char)0xDAFF);}
	@Test public void testP0xDB00()throws IOException{testRangeName((char)0xDB00, (char)0xDBFF);}
	@Test public void testP0xDC00()throws IOException{testRangeName((char)0xDC00, (char)0xDCFF);}
	@Test public void testP0xDD00()throws IOException{testRangeName((char)0xDD00, (char)0xDDFF);}
	@Test public void testP0xDE00()throws IOException{testRangeName((char)0xDE00, (char)0xDEFF);}
	@Test public void testP0xDF00()throws IOException{testRangeName((char)0xDF00, (char)0xDFFF);}
	
	@Test public void testP0xE100()throws IOException{testRangeName((char)0xE100, (char)0xE1FF);}	
	@Test public void testP0xE200()throws IOException{testRangeName((char)0xE200, (char)0xE2FF);}
	@Test public void testP0xE300()throws IOException{testRangeName((char)0xE300, (char)0xE3FF);}
	@Test public void testP0xE400()throws IOException{testRangeName((char)0xE400, (char)0xE4FF);}
	@Test public void testP0xE500()throws IOException{testRangeName((char)0xE500, (char)0xE5FF);}
	@Test public void testP0xE600()throws IOException{testRangeName((char)0xE600, (char)0xE6FF);}
	@Test public void testP0xE700()throws IOException{testRangeName((char)0xE700, (char)0xE7FF);}
	@Test public void testP0xE800()throws IOException{testRangeName((char)0xE800, (char)0xE8FF);}
	@Test public void testP0xE900()throws IOException{testRangeName((char)0xE900, (char)0xE9FF);}
	@Test public void testP0xEA00()throws IOException{testRangeName((char)0xEA00, (char)0xEAFF);}
	@Test public void testP0xEB00()throws IOException{testRangeName((char)0xEB00, (char)0xEBFF);}
	@Test public void testP0xEC00()throws IOException{testRangeName((char)0xEC00, (char)0xECFF);}
	@Test public void testP0xED00()throws IOException{testRangeName((char)0xED00, (char)0xEDFF);}
	@Test public void testP0xEE00()throws IOException{testRangeName((char)0xEE00, (char)0xEEFF);}
	@Test public void testP0xEF00()throws IOException{testRangeName((char)0xEF00, (char)0xEFFF);}
	
	@Test public void testP0xF100()throws IOException{testRangeName((char)0xF100, (char)0xF1FF);}	
	@Test public void testP0xF200()throws IOException{testRangeName((char)0xF200, (char)0xF2FF);}
	@Test public void testP0xF300()throws IOException{testRangeName((char)0xF300, (char)0xF3FF);}
	@Test public void testP0xF400()throws IOException{testRangeName((char)0xF400, (char)0xF4FF);}
	@Test public void testP0xF500()throws IOException{testRangeName((char)0xF500, (char)0xF5FF);}
	@Test public void testP0xF600()throws IOException{testRangeName((char)0xF600, (char)0xF6FF);}
	@Test public void testP0xF700()throws IOException{testRangeName((char)0xF700, (char)0xF7FF);}
	@Test public void testP0xF800()throws IOException{testRangeName((char)0xF800, (char)0xF8FF);}
	@Test public void testP0xF900()throws IOException{testRangeName((char)0xF900, (char)0xF9FF);}
	@Test public void testP0xFA00()throws IOException{testRangeName((char)0xFA00, (char)0xFAFF);}
	@Test public void testP0xFB00()throws IOException{testRangeName((char)0xFB00, (char)0xFBFF);}
	@Test public void testP0xFC00()throws IOException{testRangeName((char)0xFC00, (char)0xFCFF);}
	@Test public void testP0xFD00()throws IOException{testRangeName((char)0xFD00, (char)0xFDFF);}
	@Test public void testP0xFE00()throws IOException{testRangeName((char)0xFE00, (char)0xFEFF);}
	@Test public void testP0xFF00()throws IOException{testRangeName((char)0xFF00, (char)0xFFFF);}
};