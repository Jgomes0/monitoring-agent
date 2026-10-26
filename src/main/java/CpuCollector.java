import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class CpuCollector {
    private final CentralProcessor cpu;
    private long[] prevTicks = new long[CentralProcessor.TickType.values().length]; // "quantas categorias de tick existem?"

    public CpuCollector( HardwareAbstractionLayer hal) {
        this.cpu = hal.getProcessor();
    }


    public double printCpuinfo() {
        double load = cpu.getSystemCpuLoadBetweenTicks(prevTicks); // compara o retato atual com o guardado em prev ticks fora do metodo e devolve o load
        prevTicks = cpu.getSystemCpuLoadTicks(); // tira um novo retrato para virar o prev ticks da proxima comparação
        System.out.printf("CPU: %.1f%%%n", load * 100);
        return load * 100;
    }

}
