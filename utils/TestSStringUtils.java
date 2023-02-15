package sztejkat.abstractfmt.utils;
import org.junit.Test;
import org.junit.Assert;

/**
	Elementary test for {@link SStringUtils}
*/
public class TestSStringUtils extends sztejkat.abstractfmt.test.ATest
{
	@Test public void test_equalsCaseInsensitive()
	{
		enter();
		
			Assert.assertTrue( SStringUtils.equalsCaseInsensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"MARRY antuanETE"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseInsensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"MARRY antuanETl"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseInsensitive(
													new StringBuilder(
															"Marry AntuaneteL"
															),
															"MARRY antuanETE"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseInsensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"MARRY antuanETEL"
															));
		leave();
	};
	
	
	
	
	
	
	
	
	
	@Test public void test_equalsCaseSensitive()
	{
		enter();
		
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"MARRY antuanETE"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"MARRY antuanETl"
															));
			Assert.assertTrue( SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuane"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuanetel"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															"Marry Antuanetel"
															),
															""
															));
			Assert.assertTrue( !SStringUtils.equalsCaseSensitive(
													new StringBuilder(
															""
															),
															"Marry Antuanetel"
															));
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	@Test public void test_canStartWithCaseSensitive_1()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"M"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"Marry Antuanet"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( 1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"Marry Antuanete"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"Marry Antuanetel"
															),
															"Marry Antuanete"
															));
		leave();
	};
	
	@Test public void test_canStartWithCaseSensitive_2()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"M"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"Mort"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"Morty Antuanete"
															),
															"Marry Antuanete"
															));
		leave();
	};
	
	@Test public void test_canStartWithCaseSensitive_at()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"xxx"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"xxxry"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( 1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"xxxry Antuanete"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseSensitive(
													new StringBuilder(
															"xxxt"
															),
															"Marry Antuanete"
															,3));
		leave();
	};
	
	
	
	
	
	
	
	
	
	
	
	@Test public void test_canStartWithCaseInsensitive_1()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"M"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"MARRy Antuanet"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( 1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"marry antuanete"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"MaRRy antuanetel"
															),
															"Marry Antuanete"
															));
		leave();
	};
	
	@Test public void test_canStartWithCaseInsensitive_2()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"m"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"moRT"
															),
															"Marry Antuanete"
															));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"morty Antuanete"
															),
															"Marry Antuanete"
															));
		leave();
	};
	
	@Test public void test_canStartWithCaseInsensitive_at()
	{
		enter();
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"xxx"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( 0==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"xxxRy"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( 1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"xxxRy antuanete"
															),
															"Marry Antuanete"
															,3));
			Assert.assertTrue( -1==SStringUtils.canStartWithCaseInsensitive(
													new StringBuilder(
															"xxxt"
															),
															"Marry Antuanete"
															,3));
		leave();
	};
};