public class AlertEngine {
    private double cpuLimit = 80.0;
    private double memoryLimit = 85.0;
    private SystemInfoCollector collectorAlert;

    public AlertEngine(SystemInfoCollector collectorAlert) {
        this.collectorAlert = collectorAlert;
    }

    public void checkMemory(){
        double memoryUsedPercent=
                collectorAlert.getMemoryUsedPercent();
        if(memoryUsedPercent >= memoryLimit){
            System.out.println("Alerta, memoria acima do limite");
        }
    }
    public void checkCpu(double load){
        if(load >= cpuLimit){
            System.out.println("Alerta, CPU acima do limite");
        }

    }

}
