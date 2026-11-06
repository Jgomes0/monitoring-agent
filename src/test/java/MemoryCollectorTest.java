import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.GlobalMemory;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryCollectorTest {
    @Test
    public void deveCalcularPercentualDeMemoriaUsadaCorretamente() {
        // parte abaixo se achama ARRANGE
        HardwareAbstractionLayer hal = mock(HardwareAbstractionLayer.class); //  criação de um mock de hal
        GlobalMemory globalMemory = mock(GlobalMemory.class); // criação de um mock de  global memory
        when(hal.getMemory()).thenReturn(globalMemory); // ENSINA o hal falso: "quando te perguntarem getMemory(), devolva esse globalMemory"
        when(globalMemory.getTotal()).thenReturn(16000L);      // ENSINA o globalMemory: "quando perguntarem getTotal(), devolva 16000"
        when(globalMemory.getAvailable()).thenReturn(4000L);   // ENSINA o globalMemory: "quando perguntarem getAvailable(), devolva 4000"

        // parte abaixo se chama ACT
        MemoryCollector memoryCollector = new MemoryCollector(hal);
        double resultado = memoryCollector.getMemoryUsedPercent();

        // ASSERTS
        assertEquals(75.0, resultado, 0.001);

    }
    @Test
    public void deveExibirInformacoesDeMemoria() {

        HardwareAbstractionLayer hal = mock(HardwareAbstractionLayer.class);
        GlobalMemory globalMemory = mock(GlobalMemory.class);

        when(hal.getMemory()).thenReturn(globalMemory);
        when(globalMemory.getTotal()).thenReturn(16000L * 1024 * 1024);
        when(globalMemory.getAvailable()).thenReturn(4000L * 1024 * 1024);

        MemoryCollector memoryCollector = new MemoryCollector(hal);

        memoryCollector.printMemoryInfo();
    }


}