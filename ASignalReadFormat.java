package sztejkat.abstractfmt;
import java.io.IOException;

/**
		An implementation of {@link ISignalReadFormat} over the
		{@link #IIndicatorReadFormat} which adds handling of {@link EBrokenStream}.
*/
public abstract class ASignalReadFormat extends ASignalReadFormat0
{
					/** Set if any method detected {@link EBrokenStream}.
					Makes stream permanently broken, except {@link #close} */
					private boolean is_broken;
					
	/** Creates read format
	@param max_events_recursion_depth see {@link ASignalReadFormat0#ASignalReadFormat0}
	@param input --//--
	@throws Assertion error if parameters do not match.
	@see IIndicatorReadFormat#getMaxRegistrations
	*/
	protected ASignalReadFormat(
								 int max_events_recursion_depth,
								 IIndicatorReadFormat input
								 )
	{
		super(max_events_recursion_depth,input);
	};				
	/* ****************************************************************
	
				Tooling
	
	*****************************************************************/
	private void validateNotBroken()throws EBrokenStream
	{
		if (is_broken) throw new EBrokenStream();
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
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};		
	@Override public int whatNext()throws IOException
	{
		validateNotBroken();
		try{
			return super.whatNext();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public boolean readBoolean()throws IOException
	{
		validateNotBroken();
		try{
			return super.readBoolean();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public byte readByte()throws IOException
	{
		validateNotBroken();
		try{
			return super.readByte();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public char readChar()throws IOException
	{
		validateNotBroken();
		try{
			return super.readChar();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public short readShort()throws IOException
	{
		validateNotBroken();
		try{
			return super.readShort();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readInt()throws IOException
	{
		validateNotBroken();
		try{
			return super.readInt();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public long readLong()throws IOException
	{
		validateNotBroken();
		try{
			return super.readLong();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public float readFloat()throws IOException
	{
		validateNotBroken();
		try{
			return super.readFloat();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public double readDouble()throws IOException
	{
		validateNotBroken();
		try{
			return super.readDouble();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readBooleanBlock(boolean [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readBooleanBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readByteBlock(byte [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readByteBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readByteBlock()throws IOException
	{
		validateNotBroken();
		try{
			return super.readByteBlock();
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readCharBlock(char [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readCharBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readCharBlock(Appendable buffer, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readCharBlock(buffer,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readShortBlock(short [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readShortBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readIntBlock(int [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readIntBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readLongBlock(long [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readLongBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readFloatBlock(float [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readFloatBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	@Override public int readDoubleBlock(double [] buffer, int offset, int length)throws IOException
	{
		validateNotBroken();
		try{
			return super.readDoubleBlock(buffer,offset,length);
		}catch(EBrokenStream ex){ breakFormat(); throw ex; }
	};
	
}