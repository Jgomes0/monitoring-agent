import java.net.InetAddress; // coletar informações da rede como nome da maquina,endereço ip,hostnome (Quero usar a classe InetAddress, que está dentro do pacote java.net)
import java.net.UnknownHostException; // captar exceções
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

public class SystemInfoCollector {
    // variáveis que serão usadas por muitos metodos definidas no corpo da classe
    private final SystemInfo si = new SystemInfo(); // porta de entrada da OSHI, objeto q vamos usar para acessar a biblioteca oshi
    private final HardwareAbstractionLayer hal = si.getHardware(); // // método, por definição, já escrita na biblioteca OSHI, sempre devolve um objeto do tipo HardwareAbstractionLayer
    private long[] prevTicks = new long[CentralProcessor.TickType.values().length]; // "quantas categorias de tick existem?"
    private final CentralProcessor cpu = hal.getProcessor();


    public void printBasicInfo() {
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // captar informações da rede local
            String hostname = localHost.getHostName(); // captar o nome da maquina local

            //vem da classe System, que pega propriedades do ambiente onde o Java está rodando.
            String sistemaOperacional = System.getProperty("os.name");
            String versaoSistema = System.getProperty("os.version");
            String arquitetura = System.getProperty("os.arch");
            System.out.println("Servidor: " + hostname);
            System.out.println("Sistema operacional: " + sistemaOperacional);
            System.out.println("Versão: " + versaoSistema);
            System.out.println("Arquitetura: " + arquitetura);

        } catch (UnknownHostException e) { // caso não consiga descobrir o hostname pedido do inetdress
            System.out.println("nao foi possivel descobrir o hostname");
        }
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

    public double getMemoryUsedPercent() {
        GlobalMemory memory = hal.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;
        return (double) used / total * 100;
    }

    public double printCpuinfo() {
        double load = cpu.getSystemCpuLoadBetweenTicks(prevTicks); // compara o retato atual com o guardado em prev ticks fora do metodo e devolve o load
        prevTicks = cpu.getSystemCpuLoadTicks(); // tira um novo retrato para virar o prev ticks da proxima comparação
        System.out.printf("CPU: %.1f%%%n", load * 100);
        return load * 100;
    }




    }


