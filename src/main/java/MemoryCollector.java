import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

public class MemoryCollector {
    private HardwareAbstractionLayer hal;

    public MemoryCollector(HardwareAbstractionLayer hal) {
        this.hal = hal;
    }

    // METODOS
    public double getMemoryUsedPercent() {
        GlobalMemory memory = hal.getMemory(); // guarde o valor q vamos obter atraves do metodo getmemory nessa variável de nome memoria do tipo globalmemory
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;
        return (double) used / total * 100;
    }
    public void printMemoryInfo() {

        GlobalMemory memory = hal.getMemory();
        long total = memory.getTotal(); // puxar total de memoriaoq
        long available = memory.getAvailable(); // livre
        long used = total - available; // usada

        // OSHI retorna em bytes; converter para mb para ficar legível
        long totalMB = total / (1024 * 1024);
        long availableMB = available / (1024 * 1024);
        long usedMB = used / (1024 * 1024);

        System.out.println("Memória total: " + totalMB + " MB");
        System.out.println("Memória usada: " + usedMB + " MB");
        System.out.println("Memória livre: " + availableMB + " MB");


    }

}
/*
2 biblioetecas importadas do oshi q permitem puxar informaççoes a rrespeito do sistema
classe memorycollector declaraando o hardwareabstrac layer como hal vai ser ataraves dessa referencia hal q iremos chamar o metodo dessa biblioteca
entao temos o construtor  obriga a entregar um um hal ao memorycollector no momento em que o memorycollector nasce
dps vem os metodos que criamos uma variavel memory do tipo globalmemory e atraves dessa variavel chamamos com os gets

 */

