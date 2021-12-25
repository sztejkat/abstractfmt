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
		<li>primitive arrays <code>boolean[]</code>, possibly fragmented to many
		arrays including empty ones.;</li>
	</ul>
*/
public final class CObjListFormat extends LinkedList<Object>
{
				private static final long serialVersionUID=1L;	//for -Xlint only.
				/** Used as "open()" indicator */
				public static final Object OPEN = new Object();
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean comma = false;
		for( Object i: this)
		{
			if (comma) sb.append(',');
			
			if (i==null) sb.append("--null--");			
			else
			if (i==OPEN) sb.append("OPEN STREAM");
			else
			if (i instanceof String) sb.append("\""+i+"\"");
			else
			if (i instanceof boolean[])
			{
				boolean [] a = (boolean[])i;
				sb.append("boolean["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof byte[])
			{
				byte [] a = (byte[])i;
				sb.append("byte["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof char[])
			{
				char [] a = (char[])i;
				sb.append("char["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					ca=true;
					char c = a[j];
					if ((c>=32)&&(c<=127))
						sb.append("\'"+a[j]+"\'");
					else
						sb.append("#"+Integer.toHexString(c));
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof short[])
			{
				short [] a = (short[])i;
				sb.append("short["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof int[])
			{
				int [] a = (int[])i;
				sb.append("int["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof long[])
			{
				long [] a = (long[])i;
				sb.append("long["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof float[])
			{
				float [] a = (float[])i;
				sb.append("float["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
			if (i instanceof double[])
			{
				double [] a = (double[])i;
				sb.append("short["+a.length+"]{");
				int j;
				int lim = Math.min(a.length, 10);
				boolean ca =false;
				for( j=0;j<lim;j++)
				{ 
					if (ca) sb.append(',');
					sb.append(a[j]);ca=true;
				};
				if (j<a.length) sb.append("...");
				sb.append('}');
			}else
				sb.append(i.toString());
			comma=true;
		};
		return sb.toString();
	};
};