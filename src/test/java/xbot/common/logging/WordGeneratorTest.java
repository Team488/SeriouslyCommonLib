package xbot.common.logging;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;

import static junit.framework.TestCase.assertEquals;

public class WordGeneratorTest extends BaseWPITest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testWords() {
        WordGenerator wg = new WordGenerator();
        String chain = wg.getRandomWordChain(3, "-");
        System.out.println(chain);
    }

    @Test
    public void testOneWord() {
        WordGenerator wg = new WordGenerator();
        String chain = wg.getRandomWordChain(1, "-");
        assertFalse("Should only contain one word with no separator", chain.contains("-"));
    }
}