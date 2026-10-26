import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CpuCollectorTest {

    @Test
    public void deveRetornarCargaDaCpuEmPercentual() {
        // ARRANGE
        HardwareAbstractionLayer hal = mock(HardwareAbstractionLayer.class); // dependência do CpuCollector
        CentralProcessor cpu = mock(CentralProcessor.class); // o CpuCollector deriva isto de hal.getProcessor()
        when(hal.getProcessor()).thenReturn(cpu); // liga os dois mocks
        when(cpu.getSystemCpuLoadBetweenTicks(any(long[].class))).thenReturn(0.25); // OSHI devolve 0.0 a 1.0
        CpuCollector cpuCollector = new CpuCollector(hal); // classe sob teste roda com new

        // ACT
        double resultado = cpuCollector.printCpuinfo();

        // ASSERT
        assertEquals(25.0, resultado, 0.001); // 0.25 vira 25.0 (percentual)
    }

    @Test
    public void deveUsarOsTicksDaLeituraAnteriorNaProximaLeitura() {
        // ARRANGE
        HardwareAbstractionLayer hal = mock(HardwareAbstractionLayer.class);
        CentralProcessor cpu = mock(CentralProcessor.class);
        when(hal.getProcessor()).thenReturn(cpu);

        long[] ticksDaPrimeiraLeitura = {100L, 0L, 50L, 800L, 0L, 0L, 0L, 0L}; // diferente do array inicial (só zeros)
        when(cpu.getSystemCpuLoadTicks()).thenReturn(ticksDaPrimeiraLeitura); // "retrato" que o CpuCollector vai guardar
        when(cpu.getSystemCpuLoadBetweenTicks(any(long[].class))).thenReturn(0.10);
        CpuCollector cpuCollector = new CpuCollector(hal);

        // ACT
        cpuCollector.printCpuinfo(); // 1ª leitura: compara com zeros e guarda ticksDaPrimeiraLeitura
        cpuCollector.printCpuinfo(); // 2ª leitura: deve comparar com o que a 1ª guardou

        // ASSERT
        verify(cpu).getSystemCpuLoadBetweenTicks(ticksDaPrimeiraLeitura); // confirma que a 2ª leitura recebeu os ticks da 1ª
    }
}