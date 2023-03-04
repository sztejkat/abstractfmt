package sztejkat.abstractfmt.obj;
import  sztejkat.abstractfmt.EBrokenFormat;
import java.io.IOException;
/**
	Used to indicated abusive use of format
	produced by {@link CStrictObjStructWriteFormat1}.
	Thrown by all <code>Strict_XXXX</code> elements
	on unallowed type conversion.
*/
public class EAbusedFormat extends EBrokenFormat
{
	private static final long serialVersionUID=1L;
	public EAbusedFormat(){};
	public EAbusedFormat(String msg, Throwable cause){ super(msg,cause); };
	public EAbusedFormat(Throwable cause){ super(cause); };
	public EAbusedFormat(String msg){ super(msg); };
};