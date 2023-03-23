package types;

import static org.junit.Assert.assertEquals;

import com.mythosapps.time15.util.BuchungUtil;

import org.junit.Test;

/**
 * Created by andreas on 05.01.16.
 */
public class BuchungUtilTest {

    @Test
    public void testBruttoNettoTax() {

        assertEquals(testValues("85.000,00", "16.150,00", "101.150,00"), bruttoNettoTax(85.0, 1000));
    }

    private String[] testValues(String netto, String tax, String brutto) {
        return new String[]{netto + " €", tax + " €", brutto + " €"};
    }

    private String[] bruttoNettoTax(double rate, double stunden) {
        int billableMinutes = (int) (stunden * 60);
        return new String[]{
                BuchungUtil.getNettoForDisplay(rate, billableMinutes),
                BuchungUtil.getTaxForDisplay(rate, billableMinutes),
                BuchungUtil.getBruttoForDisplay(rate, billableMinutes),
        };
    }
}
