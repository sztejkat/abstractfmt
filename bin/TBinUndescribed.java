package sztejkat.abstractfmt.bin;

/**
	Set of usefull constants for undescribed format.
*/
interface TBinUndescribed
{
	static final byte BEGIN_DIRECT =(byte)0;
	static final byte BEGIN_REGISTER =(byte)1;
	static final byte BEGIN_USE =(byte)2;
	static final byte END =(byte)3;
	static final byte END_BEGIN_DIRECT =(byte)4;
	static final byte END_BEGIN_REGISTER =(byte)5;
	static final byte END_BEGIN_USE =(byte)6;
	static final byte DATA_SHORT =(byte)7;
	static final byte DATA_MEDIUM =(byte)8;
	static final byte DATA_LONG =(byte)9;
};