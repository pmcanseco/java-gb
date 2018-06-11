import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by aalves on 6/11/18
 */
public class CartridgeTest extends UnitTest {
    @Test
    public void parseResourceGames() {
        class TestCase {
            private String game;
            private String title;
            private MbcManager.CartridgeType cartridgeType;
            private int ramSize;
            private int romSize;
            private Cartridge.Locale locale;

            public TestCase(String game, String title, MbcManager.CartridgeType cartridgeType, int ramSize, int romSize, Cartridge.Locale locale) {
                this.game = game;
                this.title = title;
                this.cartridgeType = cartridgeType;
                this.ramSize = ramSize;
                this.romSize = romSize;
                this.locale = locale;
            }
        }

        TestCase[] cases = new TestCase[]{
                new TestCase("drmario.gb", "DR.MARIO", MbcManager.cartridgeTypes.get(0x00), 0, 32768, Cartridge.Locale.Japanese),
                new TestCase("pokebluejp.gb", "POKEMON BLUE", MbcManager.cartridgeTypes.get(0x03), 32768, 524288, Cartridge.Locale.Japanese),
                new TestCase("tetris.gb", "TETRIS", MbcManager.cartridgeTypes.get(0x00), 0, 32768, Cartridge.Locale.Japanese),
                new TestCase("gb-test-roms/halt_bug.gb", "\u0080", MbcManager.cartridgeTypes.get(0x02), 0, 32768, Cartridge.Locale.Japanese),
                new TestCase("gb-test-roms/cgb_sound/cgb_sound.gb", "CGB_SOUNDÀ", MbcManager.cartridgeTypes.get(0x02), 8192, 65536, Cartridge.Locale.Japanese),
        };

        for (TestCase tc : cases) {
            URL gameUrl = getClass().getClassLoader().getResource(tc.game);
            assertNotNull(String.format("Game cartridge for %s was not found.", tc.game), gameUrl);
            Cartridge c = new Cartridge(gameUrl.getPath());
            assertEquals(c.getCartridgeType().name, tc.cartridgeType, c.getCartridgeType());
            assertEquals(tc.title, c.getTitle());
            assertEquals(tc.ramSize, c.getRamSize());
            assertEquals(tc.locale, c.getLocale());
            assertEquals(tc.romSize, c.getRomSize());
            assertTrue(c.validChecksum());
        }
    }
}
