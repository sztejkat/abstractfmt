package sztejkat.abstractfmt.utils;
import java.util.Iterator;
import java.util.NoSuchElementException;
//Note: Original file from sztejkat.utils.datastructs package.
/**
   An iterator over an array which does not support remove.
   <p>
   This iterator preforms type-checking at {@link #next}
   and accepts arrays declared to be of any class, providing they do carry
   objects of type T.
   <p>
   This is a serious limitation of Java generics which does not allow
   us to declare lower bound for type parameters.
*/
public class CArrayIterator<T> implements Iterator<T>
{
	       //Note: We need to allow any arrya of superclass of T what basically means,
	       //we should also allow Object[] arrays. Using T[] here creates problems
	       //when we pass an array of super-type which also carries sub-types.
	       //Sadly there is no syntax to say:
	       //	Take parameter X[] in which X is a SUPERCLASS of T.
	       private final Object [] onArray;
	       private int ptr;
	       private final int maxptr;
       
  /** Creates joined iterator over multiple arrays of T 
  @param arrays arrays to iterate on in one sweep. Can't contain null sub-arrays.
  @param <T> type of iterator. Specified arrays can be of any type, but must carry objects
  	of type T.
  @return iterator
  */     
  @SuppressWarnings({"rawtypes","unchecked"})
  public static <T extends Object> Iterator<T> createIterator(Object[][] arrays)
  {
          Iterator<T> [] I = (Iterator<T>[])new Iterator[arrays.length];
          
          for(int i=0;i<arrays.length;i++)
                        I[i]=new CArrayIterator<T>(0,arrays[i].length,arrays[i]);
          return new CJoinedIterator<T>(I);
  };
  /** Creates iterator iterating from zero 
  @param onArray array to iterate on. For non-type checked
  	array see {@link #CArrayIterator(int,int,Object[])}
  */
  public CArrayIterator(T [] onArray)
  {
          this(onArray,0);
  };
  
  /** Creates iterator iterating from given index 
 @param onArray array to iterate on. For non-type checked
  	array see {@link #CArrayIterator(int,int,Object[])}
  @param start from index
  */
  public CArrayIterator(T [] onArray,int start)
  {
          this(onArray,start,onArray.length-start);
  };
  /** Creates iterator iterating from given, returning up to size elements. 
  @param onArray array to iterate on. For non-type checked
  	array see {@link #CArrayIterator(int,int,Object[])}
  @param start from index
  @param size number of elements
  */
  public CArrayIterator(T [] onArray,int start, int size)
  {
         assert(onArray!=null);
	 assert(start>=0);
	 assert(size>=0);
	 assert(start+size<=onArray.length);
         this.onArray=onArray;
         ptr = start;
         maxptr=start+size;
  };
  
  /** Creates iterator iterating from given, returning up to size elements.
  @param onArray array to iterate on, of unspecified type which 
  	 must carry elements of T.
  @param start from index
  @param size number of elements
  */
  public CArrayIterator(int start, int size,Object [] onArray)
  {
          assert(onArray!=null);
	  assert(start>=0);
	  assert(size>=0);
	  assert(start+size<=onArray.length);
	  this.onArray=onArray;
          ptr = start;
          maxptr=start+size;
  };

  public boolean hasNext()
  {
          return (ptr<maxptr);
  };
  @SuppressWarnings("unchecked")
  public T next()
  {          
          int p = ptr;
          if (ptr>=maxptr)
                  throw new NoSuchElementException("Index "+ptr+" on maxptr="+maxptr);
          ptr++;
          return  (T)onArray[p];
  };

  public void remove()
  {
   throw new UnsupportedOperationException("remove on ArrayIterator is not supported");
  };
  
  
  
  public static final class Test extends sztejkat.abstractfmt.test.ATest
  {
	  @org.junit.Test public void testIteration()
	  {
		  enter();
		  
		  	String [] x = new String[]{"A","C","D"};
			
			CArrayIterator<String> i = new CArrayIterator<String>(x);
			
			org.junit.Assert.assertTrue(i.hasNext());
			org.junit.Assert.assertTrue("A".equals(i.next()));
			org.junit.Assert.assertTrue("C".equals(i.next()));
			org.junit.Assert.assertTrue("D".equals(i.next()));
			
			org.junit.Assert.assertTrue(!i.hasNext());
			
			try{
				i.next();
				org.junit.Assert.fail("Should have thrown");
			}catch(NoSuchElementException ex){};
		  	
		  leave();
	  };
	  
	  @org.junit.Ignore  @org.junit.Test public void testRawGenericArray()
	  {
		  enter();
		  /*
		  	In this test we check, if we can correctly return
			specific types from very generic array.
			
			This test FAILS because Object[] can't be converted to String[]
		  */
		  	Object [] x = new Object[]{"A","C","D"};
			
			CArrayIterator<String> i = new CArrayIterator<String>((String[])x);
			
			org.junit.Assert.assertTrue(i.hasNext());
			org.junit.Assert.assertTrue("A".equals(i.next()));
			org.junit.Assert.assertTrue("C".equals(i.next()));
			org.junit.Assert.assertTrue("D".equals(i.next()));
			
			org.junit.Assert.assertTrue(!i.hasNext());
			
			try{
				i.next();
				org.junit.Assert.fail("Should have thrown");
			}catch(NoSuchElementException ex){};
		  	
		  leave();
	  };
	  
	  		 /*
			 	The typical scenario of use of this class would 
				be to have a certain class implementing iterable
				and return an iterator.
			
				This class may be parameterized tough,
				but it must carry inside an non-parameterized array
				of base type of generic,
				because we can't create generic array.
				
				Will it work or will it ClassCastException?
				
			Observation:
				The type erasure inside a class do in fact erase the
				type cast and no exception is thrown, even tough
				String is a subclass of Object and Object[] cannot 
				be cast to String[]
			*/
	  		private static class TX<T extends Object> implements Iterable<T>
			{
				private Object [] A = new Object[]{"A","C","D"};
					
				@SuppressWarnings("unchecked")
				public Iterator<T> iterator(){ return new CArrayIterator<T>((T[])A); };
				@SuppressWarnings("unchecked")
				public CArrayIterator<T> xiterator(){ return new CArrayIterator<T>((T[])A); };
			};
	   @org.junit.Test public void testRawGenericArrayErasure()
	  {
		  enter();
		 
		  	TX<String> t = new TX<String>();
			
			Iterator<String> i =  t.iterator();
			
			org.junit.Assert.assertTrue(i.hasNext());
			org.junit.Assert.assertTrue("A".equals(i.next()));
			org.junit.Assert.assertTrue("C".equals(i.next()));
			org.junit.Assert.assertTrue("D".equals(i.next()));
			
			org.junit.Assert.assertTrue(!i.hasNext());
			
			try{
				i.next();
				org.junit.Assert.fail("Should have thrown");
			}catch(NoSuchElementException ex){};		  	
		  leave();
	  };
	  
	  @org.junit.Test public void testRawGenericArrayErasurexx()
	  {
		  enter();
		  	Object [] x = new String[3];	 //Note: This cast is Ok.	
		  leave();
	  };
	 
  };
}
