import oshi.software.os.OperatingSystem;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UptimeCollectorTest {

    @Test
    public void deveFormatarUptimeEmHorasMinutosESegundos() {
        // ARRANGE
        OperatingSystem os = mock(OperatingSystem.class); // mock da dependência (interface do OSHI)
        when(os.getSystemUptime()).thenReturn(7385L); // 7385 s = 2h 3min 5s (o L é necessário: o método devolve long)
        UptimeCollector uptimeCollector = new UptimeCollector(os); // classe sob teste roda com new

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream(); // "balde" na memória que guarda o texto impresso
        PrintStream streamOriginal = System.out; // guarda o console original
        System.setOut(new PrintStream(outputCapturado)); // println passa a escrever no balde

        // ACT
        uptimeCollector.printUptime();
        System.setOut(streamOriginal); // devolve o console ao normal

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertTrue(textoImpresso.contains("2h 3min 5s"));
    }

    @Test
    public void deveFormatarUptimeZerado() {
        // ARRANGE
        OperatingSystem os = mock(OperatingSystem.class);
        when(os.getSystemUptime()).thenReturn(0L); // menor valor possível
        UptimeCollector uptimeCollector = new UptimeCollector(os);

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
        PrintStream streamOriginal = System.out;
        System.setOut(new PrintStream(outputCapturado));

        // ACT
        uptimeCollector.printUptime();
        System.setOut(streamOriginal);

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertTrue(textoImpresso.contains("0h 0min 0s"));
    }
}