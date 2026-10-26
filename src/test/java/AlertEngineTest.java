import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlertEngineTest {
    @Test
    public void deveImprimirAlertaQuandoMemoriaAcimaDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class); // criação do mock de memorycollector pq é oq usamos pra trabalhar em alertengine
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(90.0); // acima do limite de 85.0
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0); // eu mocko as dependencias a classe sob teste nos rodamos com new. Os limites (cpu 80.0, memória 85.0) entram direto pelo construtor

        // linha abaixo Cria um "balde vazio" na memória, pronto pra guardar texto.
        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream(); // sempre que precisa testar um metodo que faz System.out.println diretamente, e você quer confirmar que o texto certo foi impresso, sem esse texto aparecer de verdade no console durante o teste.
        PrintStream streamOriginal = System.out; // Guarda o console original numa variável, pra não perder a referência.
        System.setOut(new PrintStream(outputCapturado)); // Troca o destino global de println: agora aponta pro balde, não pro console.

        // ACT
        alertEngine.checkMemory(); // roda a engrenagem de toda a estrutura acima
        // restaura o System.out logo depois do Act, antes de qualquer Assert
        System.setOut(streamOriginal); // Devolve o destino pro console original — normaliza tudo de novo.

        // ASSERT
        String textoImpresso = outputCapturado.toString(); // Lê o que ficou guardado no balde, convertendo em texto legível.
        assertTrue(textoImpresso.contains("Alerta, memoria acima do limite")); //Confirma se a mensagem esperada realmente estava lá dentro.
    }

    @Test
    public void naoDeveImprimirAlertaQuandoMemoriaAbaixoDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(50.0); // abaixo do limite de 85.0
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
        PrintStream streamOriginal = System.out;
        System.setOut(new PrintStream(outputCapturado));

        // ACT
        alertEngine.checkMemory();
        System.setOut(streamOriginal);

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertFalse(textoImpresso.contains("Alerta, memoria acima do limite")); // o alerta NÃO deve ter aparecido
    }

    @Test
    public void deveImprimirAlertaQuandoCpuAcimaDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class); // só pra satisfazer o construtor, nunca é chamado aqui
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
        PrintStream streamOriginal = System.out;
        System.setOut(new PrintStream(outputCapturado));

        // ACT
        alertEngine.checkCpu(90.0); // acima do limite de 80.0, valor passado direto por parâmetro
        System.setOut(streamOriginal);

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertTrue(textoImpresso.contains("Alerta, CPU acima do limite"));
    }

    @Test
    public void naoDeveImprimirAlertaQuandoCpuAbaixoDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
        PrintStream streamOriginal = System.out;
        System.setOut(new PrintStream(outputCapturado));

        // ACT
        alertEngine.checkCpu(50.0); // abaixo do limite de 80.0
        System.setOut(streamOriginal);

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertFalse(textoImpresso.contains("Alerta, CPU acima do limite"));
    }

    @Test
    public void deveImprimirAlertaQuandoCpuExatamenteNoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
        PrintStream streamOriginal = System.out;
        System.setOut(new PrintStream(outputCapturado));

        // ACT
        alertEngine.checkCpu(80.0); // exatamente no limite: o código usa >=, então deve alertar
        System.setOut(streamOriginal);

        // ASSERT
        String textoImpresso = outputCapturado.toString();
        assertTrue(textoImpresso.contains("Alerta, CPU acima do limite"));
    }
}