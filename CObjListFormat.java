package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.LinkedList;
/**
	A medium for test beds for {@link ASignalWriteFormat}/{@link ASignalReadFormat}
	which is writing all data and indicators in form of boxed data to {@link LinkedList}
	<p>
	This lists keeps following objects:
	<ul>
		<li>boxed elementary primitives:
			{@link Boolean},{@link Byte},{@link Character},
			{@link Short},{@link Integer}, {@link Long},
			{@link Double},{@link Float}
		</li>
		<li>primitive arrays <Code>boolean[]</code>... one
		array for each block write operations. No write collation happens on writing side
		and it is up to reading side to collate operations;</li>
		<li>String for event name data written with {@link ASignalWriteFormat#writeSignalNameData};</li>
		<li>{@link INDICATOR} instances for indicators taken from <code>static final INDICATOR xx_INDICATOR</code>
		fields of this class</li>
		<li>{@link REGISTER_USE_INDICATOR} or {@link REGISTER_INDICATOR} instances;</li>
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
						public final int name_index;
						public REGISTER_INDICATOR(int name_index){ super(ASignalReadFormat.REGISTER_INDICATOR); this.name_index=name_index;};
						public String toString(){ return ASignalReadFormat.indicatorToString(type)+"("+name_index+")"; };
				};
				
				
				/** Indicator for {@link ASignalReadFormat#REGISTER_USE_INDICATOR} */
				public static final class REGISTER_USE_INDICATOR extends INDICATOR
				{
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
				
				public static final INDICATOR TYPE_BOOLEAN_END=new INDICATOR(ASignalReadFormat.TYPE_BOOLEAN_END);
				public static final INDICATOR TYPE_BYTE_END=new INDICATOR(ASignalReadFormat.TYPE_BYTE_END);
				public static final INDICATOR TYPE_CHAR_END=new INDICATOR(ASignalReadFormat.TYPE_CHAR_END);
				public static final INDICATOR TYPE_SHORT_END=new INDICATOR(ASignalReadFormat.TYPE_SHORT_END);
				public static final INDICATOR TYPE_INT_END=new INDICATOR(ASignalReadFormat.TYPE_INT_END);
				public static final INDICATOR TYPE_LONG_END=new INDICATOR(ASignalReadFormat.TYPE_LONG_END);
				public static final INDICATOR TYPE_FLOAT_END=new INDICATOR(ASignalReadFormat.TYPE_FLOAT_END);
				public static final INDICATOR TYPE_DOUBLE_END=new INDICATOR(ASignalReadFormat.TYPE_DOUBLE_END);
				
				public static final INDICATOR TYPE_BOOLEAN_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_BOOLEAN_BLOCK_END);
				public static final INDICATOR TYPE_BYTE_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_BYTE_BLOCK_END);
				public static final INDICATOR TYPE_CHAR_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_CHAR_BLOCK_END);
				public static final INDICATOR TYPE_SHORT_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_SHORT_BLOCK_END);
				public static final INDICATOR TYPE_INT_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_INT_BLOCK_END);
				public static final INDICATOR TYPE_LONG_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_LONG_BLOCK_END);
				public static final INDICATOR TYPE_FLOAT_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_FLOAT_BLOCK_END);
				public static final INDICATOR TYPE_DOUBLE_BLOCK_END=new INDICATOR(ASignalReadFormat.TYPE_DOUBLE_BLOCK_END);
				
	
};