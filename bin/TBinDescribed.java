package sztejkat.abstractfmt.bin;
import sztejkat.abstractfmt.TIndicator;
/**
	Set of usefull constants for described format.
*/
interface TBinDescribed
{
	static final byte BEGIN_DIRECT =(byte)0;
	static final byte BEGIN_REGISTER =(byte)1;
	static final byte BEGIN_USE =(byte)2;
	static final byte END =(byte)3;
	static final byte END_BEGIN_DIRECT =(byte)4;
	static final byte END_BEGIN_REGISTER =(byte)5;
	static final byte END_BEGIN_USE =(byte)6;
	
	static final byte TYPE_BOOLEAN =(byte)7;
	static final byte TYPE_BYTE =(byte)8;
	static final byte TYPE_CHAR =(byte)9;
	static final byte TYPE_SHORT =(byte)0xA;
	static final byte TYPE_INT =(byte)0xB;
	static final byte TYPE_LONG =(byte)0xC;
	static final byte TYPE_FLOAT =(byte)0xD;
	static final byte TYPE_DOUBLE =(byte)0xE;
	
	static final byte TYPE_BOOLEAN_BLOCK =(byte)0xF;
	static final byte TYPE_BYTE_BLOCK =(byte)0x10;
	static final byte TYPE_CHAR_BLOCK =(byte)0x11;
	static final byte TYPE_SHORT_BLOCK =(byte)0x12;
	static final byte TYPE_INT_BLOCK =(byte)0x13;
	static final byte TYPE_LONG_BLOCK =(byte)0x14;
	static final byte TYPE_FLOAT_BLOCK =(byte)0x15;
	static final byte TYPE_DOUBLE_BLOCK =(byte)0x16;
	
	static final byte DATA_SHORT =(byte)0x17;
	static final byte DATA_MEDIUM =(byte)0x18;
	static final byte DATA_LONG =(byte)0x19;
	
	/** Returns a'priori known maximum size of payload 
	for specified type indicator
	@param type indicator with {@link TIndicator#TYPE}
		flag set.
	@return size of payload chunk, in bytes
	*/
	static int chunkPayloadForType(TIndicator type)
	{
		switch(type)
		{
			case TYPE_BOOLEAN: return 0;
			case TYPE_BYTE: return 1;
			case TYPE_CHAR: 
			case TYPE_SHORT: return 2;
			case TYPE_INT: return 4;
			case TYPE_LONG: return 8;
			case TYPE_FLOAT: return 4;
			case TYPE_DOUBLE: return 8;
			
			case TYPE_BOOLEAN_BLOCK: return 7;
			case TYPE_BYTE_BLOCK:	 return 1*7;
			case TYPE_CHAR_BLOCK:	  
			case TYPE_SHORT_BLOCK:   return 2*7;
			case TYPE_INT_BLOCK: 	 return 4*7;
			case TYPE_LONG_BLOCK:	 return 8*7;
			case TYPE_FLOAT_BLOCK:   return 4*7;
			case TYPE_DOUBLE_BLOCK:  return 8*7;
			
			default: throw new AssertionError(type+" is not TYPE");
		}
	};
	
	/** Returns a'priori known payload size unit
	@param type indicator with {@link TIndicator#TYPE}
		and {@link TIndicator#BLOCK}flag set
	@return size of payload chunk, in bytes, per single
		size unit (ie 1 byte, 2 char and so on)
	*/
	static int chunkPayloadUnitForBlockType(TIndicator type)
	{
		switch(type)
		{
			case TYPE_BOOLEAN_BLOCK: return 1;
			case TYPE_BYTE_BLOCK: return 1;
			case TYPE_CHAR_BLOCK: return 2;
			case TYPE_SHORT_BLOCK: return 2;
			case TYPE_INT_BLOCK: return 4;
			case TYPE_LONG_BLOCK: return 8;
			case TYPE_FLOAT_BLOCK: return 4;
			case TYPE_DOUBLE_BLOCK: return 8;
			
			default: throw new AssertionError(type+" is not TYPE_x_BLOCK");
		}
	};
};