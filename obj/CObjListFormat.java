package sztejkat.abstractfmt.obj;
import sztejkat.abstractfmt.ASignalWriteFormat;
import sztejkat.abstractfmt.ASignalReadFormat;
import java.io.IOException;
import java.util.LinkedList;
/**
	A data exchange medium for test beds for {@link ASignalWriteFormat}/{@link ASignalReadFormat}
	which is writing all boxed data and indicators to a {@link LinkedList}
	<p>
	This lists keeps following objects:
	<ul>
		<li>boxed elementary primitives to represent elementary primitive writes:
			{@link Boolean},{@link Byte},{@link Character},
			{@link Short},{@link Integer}, {@link Long},
			{@link Double},{@link Float}
		</li>
		<li>primitive arrays <Code>boolean[]</code>... one
		array for each block write operation. No write collation happens on writing side
		and it is up to reading side to collate operations;
		</li>
		<li>
		String for event name data written with {@link ASignalWriteFormat#writeSignalNameData};
		</li>
		<li>{@link INDICATOR} static final instances for all indicators declared in {@link ASignalReadFormat}
		except below two;</li>
		<li>per data instances of {@link REGISTER_USE_INDICATOR} or {@link REGISTER_INDICATOR} 
		for name registration indicators;</li>
	</ul>
*/
public final class CObjListFormat extends LinkedList<Object>
{
					private static final long serialVersionUID=1L;	//for -Xlint only.
					
					
				/** Indicator type classes.
				<p>
				Instances of this class cannot be created directly
				and static constant fields of {@link CObjListFormat}
				must be used. As a benefit == can be used to detect
				an indicator type.
				*/
				public static class INDICATOR
				{
						/** One of {@link ASignalReadFormat}
						indicators, <code>xxx_INDICATOR</code>
						or <code>TYPE_xxx</code> */
						public final int type;
						
						private INDICATOR(int t){ this.type=t;};
						public String toString(){ return ASignalReadFormat.indicatorToString(type); };
				};
				
				
				
				/** Indicator for {@link ASignalReadFormat#REGISTER_INDICATOR} */
				public static final class REGISTER_INDICATOR extends INDICATOR
				{
						/** Event name index carried in this indicator */
						public final int name_index;
						public REGISTER_INDICATOR(int name_index){ super(ASignalReadFormat.REGISTER_INDICATOR); this.name_index=name_index;};
						public String toString(){ return ASignalReadFormat.indicatorToString(type)+"("+name_index+")"; };
				};
				
				
				/** Indicator for {@link ASignalReadFormat#REGISTER_USE_INDICATOR} */
				public static final class REGISTER_USE_INDICATOR extends INDICATOR
				{
						/** Event name index carried in this indicator */
						public final int name_index;
						public REGISTER_USE_INDICATOR(int name_index){ super(ASignalReadFormat.REGISTER_USE_INDICATOR); this.name_index=name_index;};
						public String toString(){ return ASignalReadFormat.indicatorToString(type)+"("+name_index+")"; };
				};
				
				
				
				/* ............................................
					Instances for indicators.
					Notice there is no EOF_INDICATOR. This is intentional.
				............................................................*/
				public static final INDICATOR NO_INDICATOR=new INDICATOR(ASignalReadFormat.NO_INDICATOR);
				public static final INDICATOR BEGIN_INDICATOR=new INDICATOR(ASignalReadFormat.BEGIN_INDICATOR);
				public static final INDICATOR END_INDICATOR=new INDICATOR(ASignalReadFormat.END_INDICATOR);
				public static final INDICATOR END_BEGIN_INDICATOR=new INDICATOR(ASignalReadFormat.END_BEGIN_INDICATOR);
				public static final INDICATOR DIRECT_INDICATOR=new INDICATOR(ASignalReadFormat.DIRECT_INDICATOR);
				
				public static final INDICATOR TYPE_BOOLEAN=new INDICATOR(ASignalReadFormat.TYPE_BOOLEAN);
				public static final INDICATOR TYPE_BYTE=new INDICATOR(ASignalReadFormat.TYPE_BYTE);
				public static final INDICATOR TYPE_CHAR=new INDICATOR(ASignalReadFormat.TYPE_CHAR);
				public static final INDICATOR TYPE_SHORT=new INDICATOR(ASignalReadFormat.TYPE_SHORT);
				public static final INDICATOR TYPE_INT=new INDICATOR(ASignalReadFormat.TYPE_INT);
				public static final INDICATOR TYPE_LONG=new INDICATOR(ASignalReadFormat.TYPE_LONG);
				public static final INDICATOR TYPE_FLOAT=new INDICATOR(ASignalReadFormat.TYPE_FLOAT);
				public static final INDICATOR TYPE_DOUBLE=new INDICATOR(ASignalReadFormat.TYPE_DOUBLE);
				
				public static final INDICATOR TYPE_BOOLEAN_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_BOOLEAN_BLOCK);
				public static final INDICATOR TYPE_BYTE_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_BYTE_BLOCK);
				public static final INDICATOR TYPE_CHAR_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_CHAR_BLOCK);
				public static final INDICATOR TYPE_SHORT_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_SHORT_BLOCK);
				public static final INDICATOR TYPE_INT_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_INT_BLOCK);
				public static final INDICATOR TYPE_LONG_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_LONG_BLOCK);
				public static final INDICATOR TYPE_FLOAT_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_FLOAT_BLOCK);
				public static final INDICATOR TYPE_DOUBLE_BLOCK=new INDICATOR(ASignalReadFormat.TYPE_DOUBLE_BLOCK);
				
				public static final INDICATOR FLUSH_BOOLEAN=new INDICATOR(ASignalReadFormat.FLUSH_BOOLEAN);
				public static final INDICATOR FLUSH_BYTE=new INDICATOR(ASignalReadFormat.FLUSH_BYTE);
				public static final INDICATOR FLUSH_CHAR=new INDICATOR(ASignalReadFormat.FLUSH_CHAR);
				public static final INDICATOR FLUSH_SHORT=new INDICATOR(ASignalReadFormat.FLUSH_SHORT);
				public static final INDICATOR FLUSH_INT=new INDICATOR(ASignalReadFormat.FLUSH_INT);
				public static final INDICATOR FLUSH_LONG=new INDICATOR(ASignalReadFormat.FLUSH_LONG);
				public static final INDICATOR FLUSH_FLOAT=new INDICATOR(ASignalReadFormat.FLUSH_FLOAT);
				public static final INDICATOR FLUSH_DOUBLE=new INDICATOR(ASignalReadFormat.FLUSH_DOUBLE);
				
				public static final INDICATOR FLUSH_BOOLEAN_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_BOOLEAN_BLOCK);
				public static final INDICATOR FLUSH_BYTE_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_BYTE_BLOCK);
				public static final INDICATOR FLUSH_CHAR_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_CHAR_BLOCK);
				public static final INDICATOR FLUSH_SHORT_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_SHORT_BLOCK);
				public static final INDICATOR FLUSH_INT_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_INT_BLOCK);
				public static final INDICATOR FLUSH_LONG_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_LONG_BLOCK);
				public static final INDICATOR FLUSH_FLOAT_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_FLOAT_BLOCK);
				public static final INDICATOR FLUSH_DOUBLE_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_DOUBLE_BLOCK);
				
				public static final INDICATOR FLUSH=new INDICATOR(ASignalReadFormat.FLUSH);
				public static final INDICATOR FLUSH_BLOCK=new INDICATOR(ASignalReadFormat.FLUSH_BLOCK);
				public static final INDICATOR FLUSH_ANY=new INDICATOR(ASignalReadFormat.FLUSH_ANY);
				
};