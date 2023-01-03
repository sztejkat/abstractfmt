package sztejkat.abstractfmt.test;
import sztejkat.abstractfmt.ITypedStructReadFormat;
import sztejkat.abstractfmt.ITypedStructWriteFormat;
import sztejkat.abstractfmt.ENotOpen;
import sztejkat.abstractfmt.EEof;
import sztejkat.abstractfmt.ETypeMissmatch;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;
import static sztejkat.abstractfmt.ITypedStructReadFormat.TElement;

/**
	Test if  
	{@link ITypedStructReadFormat}/{@link ITypedStructWriteFormat}
	do correctly defend against type abuse.
*/
public class ATestCase_TypeDefence extends AInterOpTestCase<ITypedStructReadFormat,ITypedStructWriteFormat>
{
	@Test public void detectsIncorrectBoolean()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeBoolean(true);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByteBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readBoolean();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectByte()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeByte((byte)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByteBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readByte();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectChar()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeChar((char)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readChar();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectShort()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeShort((short)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readShort();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectInt()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeInt(3);
			p.writer.writeFloat(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
						p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readInt();
			p.reader.close();
		leave();
	};
	
	
	
	@Test public void detectsIncorrectLong()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeLong((long)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readLong();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectFloat()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeFloat((float)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDouble();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readFloat();
			p.reader.close();
		leave();
	};
	
	
	@Test public void detectsIncorrectDouble()throws IOException
	{
		enter();
			CPair<ITypedStructReadFormat,ITypedStructWriteFormat> p = createTestDevice();
			p.writer.open();
			p.writer.writeDouble((double)3);
			p.writer.writeInt(0);
			p.writer.close();			
			
			p.reader.open();
			//Notice ETypeMissmatch contract, which says that cursor must be un-moved!
			try{
					p.reader.readBoolean();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readChar();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShort();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readInt();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readLong();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloat();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readByte();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readBooleanBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readCharBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readShortBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readIntBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			try{
					p.reader.readLongBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readFloatBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readDoubleBlock();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			try{
					p.reader.readString();
					Assert.fail();
			}catch(ETypeMissmatch ex){ System.out.println(ex); };
			
			//And at last a correct one!
			p.reader.readDouble();
			p.reader.close();
		leave();
	};
};