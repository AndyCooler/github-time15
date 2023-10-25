package types;

import static org.junit.Assert.assertEquals;

import com.mythosapps.time15.util.BuchungUtil;

import org.junit.Test;

/**
 * Created by andreas on 05.01.16.
 */
public class BuchungUtilTest {

    @Test
    public void testBruttoNettoTax1() {
        assertEquals(testValues("85.000,00", "16.150,00", "101.150,00"), bruttoNettoTax(85.0, 1000));
    }

    @Test
    public void testBruttoNettoTax2() {
        assertEquals(testValues("12.707,50", "2.414,43", "15.121,93"), bruttoNettoTax(85.0, 149.5));
    }

    @Test
    public void testBruttoNettoTax3() {
        assertEquals(testValues("10.433,75", "1.982,41", "12.416,16"), bruttoNettoTax(85.0, 122.75));
    }

    @Test
    public void testBruttoNettoTax4() {
        assertEquals(testValues("10.072,50", "1.913,78", "11.986,28"), bruttoNettoTax(85.0, 118.5));
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
