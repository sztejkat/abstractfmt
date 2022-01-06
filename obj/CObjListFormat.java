package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.*;
import java.io.IOException;
import java.util.LinkedList;
/**
	A data exchange medium for test beds for {@link ASignalWriteFormat}/{@link ASignalReadFormat}
	which is writing all boxed data and indicators to a {@link LinkedList}
	<p>
	This lists keeps following objects:
	<ul>
		<li>{@link TIndicator} for indicators;</li>
		<li>{@link #OPEN} for opening marker;</li>
		<li>{@link String} for signal names;</li>
		<li>{@link Integer} for signal index numbers;</li>		
		<li>boxed elementary primitives to represent elementary primitive writes;</li>
		<li>primitive arrays <code>boolean[]</code> and etc., possibly fragmented to many
		arrays, one per write operation including empty ones, used to represents primitive 
		block writes;</li>
	</ul>
*/
public final class CObjListFormat extends LinkedList<Object>
{
				private static final long serialVersionUID=1L;	//for -Xlint only.
				/** Used as "open()" indicator */
				public static final Object OPEN = new Object();
	
	/** Prints human readable form to specified output
	@param w where to print
	@param separator which what separate each element
	@throws IOException if failed 
	*/
	public void printTo(Appendable w,String separator)throws IOException
	{
		boolean needs_separator = false;
		for( Object i: this)
		{
			if (needs_separator) w.append(separator);
			
			if (i==null) w.append("--null--");			
			else
			if (i==OPEN) w.append("OPEN STREAM");
			else
			if (i instanceof String) w.append("\""+i+"\"");
			else
			if (i instanceof boolean[])
			{
				boolean [] a = (boolean[])i;
				w.append("boolean["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Boolean.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof byte[])
			{
				byte [] a = (byte[])i;
				w.append("byte["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Byte.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof char[])
			{
				char [] a = (char[])i;
				w.append("char["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					ca=true;
					char c = a[j];
					if ((c>=32)&&(c<=127))
						w.append("\'"+a[j]+"\'");
					else
						w.append("#"+Integer.toHexString(c));
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof short[])
			{
				short [] a = (short[])i;
				w.append("short["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Short.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof int[])
			{
				int [] a = (int[])i;
				w.append("int["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Integer.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof long[])
			{
				long [] a = (long[])i;
				w.append("long["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Long.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof float[])
			{
				float [] a = (float[])i;
				w.append("float["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Float.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
			if (i instanceof double[])
			{
				double [] a = (double[])i;
				w.append("short["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) w.append(',');
					w.append(Double.toString(a[j]));ca=true;
				};
				if (j<a.length) w.append("...");
				w.append('}');
			}else
				w.append(i.toString());
			needs_separator=true;
		};
	}
	/** Dumps the list to human readable form for inspection
	during testing and debuging */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		try{
			printTo(sb,",");
			}catch(IOException ex){ throw new RuntimeException(ex); };
		return sb.toString();
	};
};