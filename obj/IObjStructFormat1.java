package sztejkat.abstractfmt.obj;

/**
	Common data types for {@link CObjStructWriteFormat1}/
	{@link CObjStructReadFormat1}.
	
	<h1>Format</h1>
	An object stream is a collection of instances
	of {@link IObjStructFormat0} and {@link IObjStructFormat1}
	objects.
	<p>
	The {@link SIG_END} is used to represent an end signal,
	the {@link SIG_END_BEGIN} and {@link SIG_BEGIN}
	are used to represent signals with directly encoded names
	and  {@link SIG_END_BEGIN_AND_REGISTER},
	 {@link SIG_BEGIN_AND_REGISTER},
	 {@link SIG_END_BEGIN_REGISTERED},
	 {@link SIG_BEGIN_REGISTERED}
	are used to implement name registry support.
	<p>
	Everything else is exactly as in {@link IObjStructFormat0}
	
*/
public interface IObjStructFormat1 extends IObjStructFormat0
{
	
};