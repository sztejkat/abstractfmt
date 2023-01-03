package sztejkat.abstractfmt.bin.chunk;
import  sztejkat.abstractfmt.logging.SLogging;
import  sztejkat.abstractfmt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
	A base chunk implementation.
	<p>
	This implementation adds intermediate layer responsible for
	raw byte access on chunk processing, some elementary state
	management and signal processing what includes names encoding.
	
*/
abstract class AChunkWriteFormat0 extends ARegisteringStructWriteFormat
{
		 private static final long TLEVEL = SLogging.getDebugLevelForClass(AChunkWriteFormat0.class);
         private static final boolean TRACE = (TLEVEL!=0);
         private static final boolean DUMP = (TLEVEL>=2);
         private static final java.io.PrintStream TOUT = TRACE ? SLogging.createDebugOutputForClass("AChunkWriteFormat0.",AChunkWriteFormat0.class) : null;

				/** Header HHH bits (see package description) */
				static final byte HEADER_REGISTER = (byte)0b00;
				static final byte HEADER_BEGIN_DIRECT = (byte)0b001;
				static final byte HEADER_END_BEGIN_DIRECT = (byte)0b010;
				static final byte HEADER_BEGIN_REGISTERED = (byte)0b011;
				static final byte HEADER_END_BEGIN_REGISTERED = (byte)0b100;
				static final byte HEADER_END = (byte)0b101;
				static final byte HEADER_CONTINUE = (byte)0b110;
				static final byte HEADER_EXTENDED_REGISTERED = (byte)0b111;

				/** Represents buffer type handler. Buffer type handlers
				are set of semi-static methods handling buffers depending
				on their type.
				They do share the
				{@link AChunkWriteFormat0#current_at},
				{@link AChunkWriteFormat0#buffer},
				{@link AChunkWriteFormat0#raw}.
				*/
				private abstract class ABufferHandler
				{
					/** @return true if buffer can be written, false if there is no more space in it
					@see AChunkWriteFormat0#current_at
					*/
					abstract boolean canWrite();
					/** Flushes buffer,
					resets it to null handler, buffer to empty
					@throws IOException if failed.
					*/
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("ABufferHandler.terminate()");
						current_at=0;
						current_buffer_handler=null;
					};
				};
				
				/** A handler for a REGISTER buffer, in indexed mode. */
				private final class CRegister_Indexed extends ABufferHandler
				{
						int index = -1;
					void initialize(int index)
					{
						if (TRACE) TOUT.println("CRegister_Indexed.initialize("+index+")");
						assert(index<=127);
						assert(index>=0);
						this.index = index; 
					};
					boolean canWrite(){ return false; };
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("CRegister_Indexed.terminate()");
						assert(index>=0):"not initialized";
						raw.write(0b1000 | HEADER_REGISTER);
						raw.write(index);
						index=-1;
						super.terminate();
					};
				};
				private final class CRegister_Ordered extends ABufferHandler
				{						
					boolean canWrite(){ return false; };
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("CRegister_Ordered.terminate()");
						raw.write(HEADER_REGISTER);
						super.terminate();
					};
				};
				private final class CBeginDirect extends ABufferHandler
				{						
					boolean canWrite(){ return current_at<31; };
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("CBeginDirect.terminate()");
						//Note: This header must be written even if it has zero size.
						raw.write(HEADER_BEGIN_DIRECT | (current_at<<3));
						raw.write(buffer,0,current_at);
						super.terminate();
					};
				};
				private final class CEndBeginDirect extends ABufferHandler
				{						
					boolean canWrite(){ return current_at<31; };
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("CEndBeginDirect.terminate()");
						//Note: This header must be written even if it has zero size.
						raw.write(HEADER_END_BEGIN_DIRECT | (current_at<<3));
						raw.write(buffer,0,current_at);
						super.terminate();
					};
				};
				private class AxBeginRegistered extends ABufferHandler
				{					
							/** Index to register */
							int register_index=-1;
							/** Header */
							final byte header;
							/** Mask to set in second byte of HEADER_EXTENDED_REGISTERED */
							final byte ex_mask;
					AxBeginRegistered(byte header, byte ex_mask){ this.header = header; this.ex_mask= ex_mask;};
					void initialize(int register_index)
					{ 
						if (TRACE) TOUT.println("AxBeginRegistered.initialize("+register_index+")");
						assert(register_index>=0);
						assert(register_index<=127);
						this.register_index = register_index;
					};
					boolean canWrite()
					{
						//use limits for extened, will optimize it at flush
						return current_at<31; 
					};
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("AxBeginRegistered.terminate() "+Integer.toHexString(header)+" "+Integer.toHexString(ex_mask)+" register_index="+register_index+" ENTER");
						//Note: This header must be written even if it has zero size.
						//Now figure out optimized header
						final int s = current_at;  
						assert(register_index>=0):"not initialized";
						assert((s & ~0b1111_1)==0);
						if (register_index<=7)
						{
							//optimization is possible, if sizes do match.
							switch(s)
							{
								case 1:
										raw.write(
												(0b00 << 6)
												|
												(register_index<<3)
												|
												header);
										if (TRACE) TOUT.println("AxBeginRegistered.terminate(), optimized to 1");
										break;
								case 2:
										raw.write(
												(0b01 << 6)
												|
												(register_index<<3)
												|
												header);
										if (TRACE) TOUT.println("AxBeginRegistered.terminate(), optimized to 2");
										break;
								case 4:
										raw.write(
												(0b10 << 6)
												|
												(register_index<<3)
												|
												header);
										if (TRACE) TOUT.println("AxBeginRegistered.terminate(), optimized to 4");
										break;
								case 8:
										raw.write(
												(0b11 << 6)
												|
												(register_index<<3)
												|
												header);
										if (TRACE) TOUT.println("AxBeginRegistered.terminate(), optimized to 8");
										break;
								default:
										raw.write(
												(s<<3)
												|
												HEADER_EXTENDED_REGISTERED);
										raw.write(
													ex_mask
													|
													register_index
													);
										if (TRACE) TOUT.println("AxBeginRegistered.terminate(), not optimized ");
										break;
							}
							raw.write(buffer,0,s);
						}else
						{
							if (TRACE) TOUT.println("AxBeginRegistered.terminate(), no optimization possible ");
							//optimization is impossible, use long form.
							raw.write(
												(s<<3)
												|
												HEADER_EXTENDED_REGISTERED);
							raw.write(
										ex_mask
										|
										register_index
										);
							raw.write(buffer,0,current_at);
						};
						super.terminate();
						if (TRACE) TOUT.println("AxBeginRegistered.terminate() LEAVE");
					};
				};
				
				private final class CBeginRegistered extends AxBeginRegistered
				{					
						CBeginRegistered()
						{
							super(HEADER_BEGIN_REGISTERED,(byte)0x00);
						};
				};
				private final class CEndBeginRegistered extends AxBeginRegistered
				{					
						CEndBeginRegistered()
						{
							super(HEADER_END_BEGIN_REGISTERED,(byte)0x80);
						};
				};
				private final class CEnd extends ABufferHandler
				{						
					boolean canWrite(){ return current_at<31; };
					void terminate()throws IOException
					{
						if (TRACE) TOUT.println("CEnd.terminate()");
						//Note: This header must be written even if it has zero size.
						raw.write(HEADER_END | (current_at<<3));
						raw.write(buffer,0,current_at);
						super.terminate();
					};
				};
				private final class CContinue extends ABufferHandler
				{						
					boolean canWrite(){ return current_at<4095; };
					void terminate()throws IOException
					{
						//This header may be skipped if it is empty
						if (TRACE) TOUT.println("CContinue.terminate() ENTER");
						final int s = current_at;
						assert( (s & ~0xFFF)==0);
						if (s!=0)
						{
							//decide on form
							if (s<=15)
							{
								raw.write((s<<4) | HEADER_CONTINUE);
								if (TRACE) TOUT.println("CContinue.terminate() short form, s ="+s);
							}else
							{
								raw.write((s<<4) | 0b1000 | HEADER_CONTINUE);
								raw.write(s >>> 4 );
								if (TRACE) TOUT.println("CContinue.terminate() long form form s="+s);
							};
							raw.write(buffer,0,s);
						};
						super.terminate();
						if (TRACE) TOUT.println("CContinue.terminate() LEAVE");
					};
				};
				/** Raw binary input stream */
				private final OutputStream raw;
				/** Current write cursor in {@link #buffer} */
				private int current_at;
				/** True if is using indexed registration, false if ordering based */
				private boolean indexed_registration;
				/** A chunk buffer.
				<p>
				The usable size of this buffer depends on
				a type of a chunk, except if chunk is 
				{@link #HEADER_BEGIN_REGISTERED} or
				{@link #HEADER_END_BEGIN_REGISTERED}
				which may, during flushing, transform to 
				{@link #HEADER_EXTENDED_REGISTERED}
				<p>
				Pre-allocated to max chunk size.
				*/
				private byte [] buffer = new byte[4095];
			
				/** A handler of current buffer */
				private ABufferHandler current_buffer_handler;
	
				//Default handler instances, shared over enire life
				private final CRegister_Indexed Register_Indexed = new CRegister_Indexed();
				private final CRegister_Ordered Register_Ordered = new CRegister_Ordered();
				private final CBeginDirect BeginDirect = new CBeginDirect();
				private final CEndBeginDirect EndBeginDirect = new CEndBeginDirect();
				private final CBeginRegistered BeginRegistered = new CBeginRegistered();
				private final CEndBeginRegistered EndBeginRegistered = new CEndBeginRegistered();
				private final CEnd End = new CEnd();
				private final CContinue Continue = new CContinue();
				
	/* *****************************************************************************
	
	
			Construction
			
	
	
	* *****************************************************************************/
	/** Creates
	@param name_registry_capacity {@link ARegisteringStructWriteFormat#ARegisteringStructWriteFormat(int)}
			This value cannot be larger than 128. Recommended value is 128, minimum resonable is 8.
	@param raw raw binary stream to write to. Will be closed on {@link #close}.
	@param indexed_registration if true names are registered directly, by index.
			If false names are registered indirectly, by order of appearance.
			For typed streams it is recommended to use "by index" registration,
			as first 8 registered names can be encoded very efficiently for 
			typical elementary elements lengths.
	*/
	AChunkWriteFormat0(int name_registry_capacity,
					   OutputStream raw,
					   boolean indexed_registration
					   )
	{
		super(name_registry_capacity);
		if (TRACE) TOUT.println("new AChunkWriteFormat0(name_registry_capacity="+name_registry_capacity+",indexed_registration="+indexed_registration+")"); 
		assert(raw!=null);
		assert(name_registry_capacity<=128):"name_registry_capacity="+name_registry_capacity+" too large, up to 127 is supported";
		this.raw = raw;
		this.indexed_registration = indexed_registration;
	};
				
	/* *****************************************************************************
	
			Intermediate level
			
				This intermediate level is responsible for
				transparent handling of "continue" chunk inside a
				payload chunk.
	
	
	******************************************************************************/
	/** Starts "continue" chunk,
	assuming there is no current chunk
	*/
	private void openContinueChunk()
	{
		if (TRACE) TOUT.println("openContinueChunk()");
		assert(current_buffer_handler==null);
		assert(current_at == 0);
		current_buffer_handler = Continue;
	};
	/** Tells current chunk  to terminate, if any 
	@throws IOException if failed.
	*/
	private void terminateChunk()throws IOException
	{
		if (TRACE) TOUT.println("terminateChunk()");
		if (current_buffer_handler!=null)
		{
			current_buffer_handler.terminate();
			assert(current_buffer_handler==null);
			assert(current_at == 0);
		};
	};
	/** Terminates chunk and starts new one 
	@param new_handler new chunk, can be null 
	@throws IOException if failed.
	*/
	private void openChunk(ABufferHandler new_handler)throws IOException
	{
		if (TRACE) TOUT.println("openChunk("+new_handler+")");
		terminateChunk();
		current_buffer_handler = new_handler;
	};
	
	/** Transparently writes raw byte to chunk payload, flushing 
	and continuing if necessary
	@param b what to write
	@throws IOException if failed */
	protected final void out(byte b)throws IOException
	{
		if (DUMP) TOUT.println("out("+Integer.toHexString(b)+") ENTER");
		//Check if we do have a free space?
		//Free space depends on chunk used
		if (current_buffer_handler==null)	openContinueChunk();
		if (!current_buffer_handler.canWrite())
		{
			if (TRACE) TOUT.println("out() needs to open next chunk");
			current_buffer_handler.terminate();
			openContinueChunk();
		};
		assert(current_buffer_handler!=null);
		buffer[current_at++]=b;
		if (DUMP) TOUT.println("out() LEAVE");
	};
	/* *****************************************************************************
	
			Elementary encoding, common
	
    *******************************************************************************/
	
	/**
		Encodes string, as specs are saying.
		Uses {@link #encodeStringCharacter}
		@param s text to encode
		@param at from where
		@param length how many
		@throws IOException if failed.
	*/
	protected void encodeString(CharSequence s, int at, int length)throws IOException
	{
		if (TRACE) TOUT.println("encodeString("+s+",at="+at+",length="+length+") ENTER");
		for(int i=at;i<length;i++)
		{
			encodeStringCharacter(s.charAt(i));
		};
		if (TRACE) TOUT.println("encodeString() LEAVE");
	};
	/**
		Encodes string character, as specs are saying.
		@param c text to encode
		@throws IOException if failed.
		@see #out
	*/
	protected void encodeStringCharacter(char c)throws IOException
	{		
		if (DUMP) TOUT.println("encodeStringCharacter("+Integer.toHexString(c)+") ENTER");
		char z = (char)(c>>>7);
		if (z==0)
		{
			out((byte)c);
		}else
		{ 
			out((byte)((c & 0x7F) | 0x80));
			c = (char)(c>>>7);
			z = (char)(z>>>7);
			if (z==0)
			{
				out((byte)c);
			}else
			{
				out((byte)((c & 0x7F) | 0x80));
				c = (char)(c>>>7);
				out((byte)c);
			};
		};
		if (DUMP) TOUT.println("encodeStringCharacter("+Integer.toHexString(c)+") LEAVE");
	}
	
	/* *****************************************************************************
	
			Services required by ARegisteringStructWriteFormat
	
	******************************************************************************/
	/* ---------------------------------------------------------------------------
				Signals
	---------------------------------------------------------------------------*/
	/**
		Encodes signal name, as specs are saying.
		Uses {@link #encodeString}.
		@param name name to encode
		@throws IOException if failed.
	*/
	protected void encodeSignalName(String name)throws IOException
	{
		if (TRACE) TOUT.println("encodeSignalName()->");
		encodeString(name,0,name.length());
	};
	/** Common code for {@link #beginAndRegisterImpl} and {@link #endBeginAndRegisterImpl}
	@param index index passed to above methods.
	@throws IOException if failed 
	*/
	private void commonRegisterImpl(int index)throws IOException
	{
		if (TRACE) TOUT.println("commonRegisterImpl("+index+") ENTER");
		terminateChunk();
		if (indexed_registration)
		{
			current_buffer_handler =  Register_Indexed;
			Register_Indexed.initialize(index);
		}else
		{
			current_buffer_handler = Register_Ordered;
		};
		if (TRACE) TOUT.println("commonRegisterImpl() LEAVE");
	};
	/** Note: order parameter is ignored as it is deduced automatically */
	@Override protected void beginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("beginAndRegisterImpl ENTER");
		commonRegisterImpl(index);
		//now all is left is to terminate above
		openChunk(BeginDirect);
		//write name
		encodeSignalName(name);
		//terminate it with end signal
		openChunk(End);
		if (TRACE) TOUT.println("beginAndRegisterImpl LEAVE");
	};
	/** Note: order parameter is ignored as it is deduced automatically */
	@Override protected void endBeginAndRegisterImpl(String name, int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("endBeginAndRegisterImpl ENTER");
		commonRegisterImpl(index);
		//now all is left is to terminate above
		openChunk(EndBeginDirect);
		//write name
		encodeSignalName(name);
		//terminate it with end signal
		openChunk(End);
		if (TRACE) TOUT.println("endBeginAndRegisterImpl LEAVE");
	};
	
	@Override protected void beginRegisteredImpl(int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("beginRegisteredImpl ENTER");
		openChunk(BeginRegistered);
		if (indexed_registration)
		{
			BeginRegistered.initialize(index);
		}else
		{
			BeginRegistered.initialize(order);
		};
		if (TRACE) TOUT.println("beginRegisteredImpl LEAVE");
	};
	@Override protected void endBeginRegisteredImpl(int index, int order)throws IOException
	{
		if (TRACE) TOUT.println("endBeginRegisteredImpl ENTER");
		openChunk(EndBeginRegistered);
		if (indexed_registration)
		{
			EndBeginRegistered.initialize(index);
		}else
		{
			EndBeginRegistered.initialize(order);
		};
		if (TRACE) TOUT.println("endBeginRegisteredImpl LEAVE");
	};
	
	@Override protected void beginDirectImpl(String name)throws IOException
	{
		if (TRACE) TOUT.println("beginDirectImpl ENTER"); 
		openChunk(BeginDirect);
		encodeSignalName(name);
		//terminate it with end signal
		openChunk(End);
		if (TRACE) TOUT.println("beginDirectImpl LEAVE");
	};
	@Override protected void endBeginDirectImpl(String name)throws IOException
	{
		if (TRACE) TOUT.println("endBeginDirectImpl ENTER");
		openChunk(EndBeginDirect);
		encodeSignalName(name);
		//terminate it with end signal
		openChunk(End);
		if (TRACE) TOUT.println("endBeginDirectImpl LEAVE");
	};	
	
	@Override protected void endImpl()throws IOException
	{
		if (TRACE) TOUT.println("endImpl ENTER");
		//terminate current if any and open end chunk
		openChunk(End);
		if (TRACE) TOUT.println("endImpl LEAVE");
	};
	/* *****************************************************************************
	
			Services required by AStructWriteFormatBase0			
	
	******************************************************************************/
	/* ------------------------------------------------------------------
				State related.
				
				 Note: Nothing is done on open() so it is not overriden
				 		here. Let us leave it to subclass.
	------------------------------------------------------------------*/
	@Override protected void flushImpl()throws IOException
	{
		if (TRACE) TOUT.println("flushImpl ENTER");
		//just terminate chunk and flush down-stream
		terminateChunk();
		raw.flush();
		if (TRACE) TOUT.println("flushImpl LEAVE");
	};
	
	@Override protected void closeImpl()throws IOException
	{
		if (TRACE) TOUT.println("closeImpl ENTER");
		assert(current_buffer_handler==null);
		raw.close();
		if (TRACE) TOUT.println("closeImpl LEAVE");
	};
	
};
