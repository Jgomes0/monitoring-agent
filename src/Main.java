//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


public class Main {
    public static void main(String[] args) {
        System.out.println("Monitoring Agent iniciado!");
        double cpuLoad;
        SystemInfoCollector collector = new SystemInfoCollector(); // rodar a aplicação de monitoramento
        AlertEngine alertEngine = new AlertEngine(collector);
        collector.printBasicInfo();
        while (true) {
            try {
                cpuLoad = collector.printCpuinfo(); // ÚNICA chamada do tick, guarda o resultado
                collector.printMemoryInfo();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Monitoramente de cpu interrompido");
                Thread.currentThread().interrupt();
                break;
            }
            alertEngine.checkMemory();
            alertEngine.checkCpu(cpuLoad);
        }


    }
}

