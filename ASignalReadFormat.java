package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalReadFormat} over the
		{@link IIndicatorReadFormat} which adds throwing 
		{@link EBrokenFormat} as a request to make format permanently broken.
*/
public abstract class ASignalReadFormat extends ASignalReadFormat0
{
					/** Set if any method detected {@link EBrokenFormat}.
					Makes stream permanently broken, except {@link #close} */
					private boolean is_broken;
					
	/** Creates read format
	@param input --//--
	@throws AssertionError error if parameters do not match.
	@see IIndicatorReadFormat#getMaxRegistrations
	*/
	protected ASignalReadFormat(
								 IIndicatorReadFormat input
								 )
	{
		super(input);
	};				
	/* ****************************************************************
	
				Tooling
	
	*****************************************************************/
	private void validateNotBroken()throws EBrokenFormat
	{
		if (is_broken) throw new EBrokenFormat();
	};		
	private void breakFormat(){ is_broken = true; };
	/* ****************************************************************
	
				ISignalReadFormat
	
	*****************************************************************/
	/* -------------------------------------------------------------
				Signals
	-------------------------------------------------------------*/
	@Override public String next()throws IOException
	{
		validateNotBroken();
		try{
			return super.next();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};		
	@Override public TContentType whatNext()throws IOException
	{
		validateNotBroken();
		try{
			return super.whatNext();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public boolean readBoolean()throws IOException
	{
		validateNotBroken();
		try{
			return super.readBoolean();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public byte readByte()throws IOException
	{
		validateNotBroken();
		try{
			return super.readByte();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public char readChar()throws IOException
	{
		validateNotBroken();
		try{
			return super.readChar();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public short readShort()throws IOException
	{
		validateNotBroken();
		try{
			return super.readShort();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readInt()throws IOException
	{
		validateNotBroken();
		try{
			return super.readInt();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public long readLong()throws IOException
	{
		validateNotBroken();
		try{
			return super.readLong();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public float readFloat()throws IOException
	{
		validateNotBroken();
		try{
			return super.readFloat();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public double readDouble()throws IOException
	{
		validateNotBroken();
		try{
			return super.readDouble();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readBooleanBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readByteBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readByteBlock()throws IOException
	{
		validateNotBroken();
		try{
			return super.readByteBlock();
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readCharBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readCharBlock(Appendable buffer, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readCharBlock(buffer,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readShortBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readIntBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readLongBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readFloatBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readDoubleBlock(buffer,offset,length);
		}catch(EBrokenFormat ex){ breakFormat(); throw ex; }
	};
	
}