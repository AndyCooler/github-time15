package types;

import com.mythosapps.time15.util.BuchungUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by andreas on 05.01.16.
 */
public class BuchungUtilTest {

    @Test
    public void testBruttoNettoTax() {

        Assert.assertEquals(new String[]{"85.000,00 €", "0.0", "0.0"}, bruttoNettoTax(85.0, 1000));
    }

    private String[] bruttoNettoTax(double rate, int stunden) {
        int billableMinutes = stunden * 60;
        return new String[]{
                BuchungUtil.getNettoForDisplay(rate, billableMinutes),
                "0.0",
                "0.0"
        };
    }
}
