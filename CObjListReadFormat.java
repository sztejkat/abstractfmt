package sztejkat.abstractfmt;
import java.io.IOException;
import java.util.Arrays;
/**
		Primarily a test bed for {@link ASignalReadFormat} which
		writes signals and data to {@link CObjListFormat}.
		<p>
		Intended to be used in tests, but users may look at it as
		on a base, primitive implementation.
		<p>
		Absolutely not thread safe.
*/
public abstract class CObjListReadFormat extends ASignalReadFormat
{
					/** A media to which this class writes. */
					public final CObjListFormat media;
					/** Used to implement array operations stitching.
					Carries pointer from which return data in currently
					processes block on {@link #media} */
					private int array_op_ptr;
		/* *************************************************************
		
				Construction
		
		
		***************************************************************/
		/** Creates
		@param names_registry_size see {@link ASignalReadFormat#ASignalReadFormat(int,int,int,boolean)}
		@param max_name_length --//--
		@param max_events_recursion_depth --//--
		@param strict_described_types --//--
		@param media non null media from which read data.
		*/
		public CObjListReadFormat(
									 int names_registry_size,
									 int max_name_length,
									 int max_events_recursion_depth,
									 boolean strict_described_types,
									 CObjListFormat media
									 )
		{
			super(  names_registry_size,max_name_length,max_events_recursion_depth,strict_described_types);
			assert(media!=null);
			this.media = media;
		};		
		/* *************************************************************
		
				Services required by superclass.		
		
		***************************************************************/
		/*............................................................		
				Indicators		
		............................................................*/
		@Override protected int readIndicator()throws IOException
		{
			//check if end-of file
			if (media.isEmpty()) return EOF_INDICATOR;
			//Poll content under cursor
			Object at_cursor = media.getFirst();
			if (at_cursor instanceof CObjListFormat.INDICATOR)
			{
				//we have an indicator
				array_op_ptr = 0;		//each indicator clears array op.
				media.removeFirst();	//read it from media.
				return ((CObjListFormat.INDICATOR)at_cursor).type;
			}else
			{
				//we don't have indicator but data instead.
				return NO_INDICATOR;
			}
		};
		@Override  protected void skip()throws IOException,EUnexpectedEof
		{
			array_op_ptr = 0;	//reset any array op.
			//loop
			for(;;)
			{
				if (media.isEmpty()) throw new EUnexpectedEof();
				Object at_cursor = media.getFirst();
				if (at_cursor instanceof CObjListFormat.INDICATOR)
				{
						//indicator found in stream.
						return;
				}else
				{
					//remove from media.
					media.removeFirst();
				};
			}
		};
		/*............................................................		
				Signals		
		............................................................*/
		@Override protected void readSignalNameData(Appendable a, int limit)throws IOException
		{
			//We expect that in stream there is a String representing name,
			//as format specs say.			
			Object at_cursor = media.pollFirst();
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof String)
			{
				//Now we can just add it. A more complex system would fetch
				//data char-by-char and test limit, but since we do operate on
				//string form, we can just add it and rely on superclass sending
				//us Appendable which applies the limit.
				a.append((String)at_cursor);
			}else
				throw new EBrokenStream();
		};
		@Override protected int readRegisterIndex()throws IOException
		{
			//We expect that in stream there is a REGISTER_INDICATOR representing name,
			//as format specs say.			
			Object at_cursor = media.pollFirst();
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof CObjListFormat.REGISTER_INDICATOR)
			{
				return ((CObjListFormat.REGISTER_INDICATOR)at_cursor).name_index;
			}else
				throw new EBrokenStream();
		}
		@Override protected int readRegisterUse()throws IOException
		{
			//We expect that in stream there is a REGISTER_USE_INDICATOR representing name,
			//as format specs say.			
			Object at_cursor = media.pollFirst();
			if (at_cursor==null) throw new EUnexpectedEof();
			if (at_cursor instanceof CObjListFormat.REGISTER_USE_INDICATOR)
			{
				return ((CObjListFormat.REGISTER_USE_INDICATOR)at_cursor).name_index;
			}else
				throw new EBrokenStream();
		}
		/*............................................................		
				low level I/O		
		............................................................*/
		/** Empty */
		@Override protected void closeImpl()throws IOException{};
		/*............................................................		
				elementary primitive reads.
				
			Note: Since reads are basically un-typed, but our 
			data are typed by objects we will do some test, try
			some casts, but in many cases throw EDataMissmatch.
			
		............................................................*/
		@Override protected boolean readBooleanImpl()throws IOException
		{
			//There is no need to check for physical or logic EOF
			//because it is handled by a caller. We have to be however
			//prepared to have incorrect type of data if stream is untyped.
			Object at_cursor = media.pollFirst();	//we do remove data, as the exact
													//behaviour on EDataMissmatch is
			if (at_cursor instanceof Boolean)
			{
				return ((Boolean)at_cursor).booleanValue();
			}else
			if (at_cursor instanceof Number)
			{
				return ((Number)at_cursor).intValue()!=0;
			}else
				throw new EDataMissmatch(at_cursor.getClass()+" while expected Boolean");
		};
		
		
};