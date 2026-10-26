
public class AlertEngine {
    private double cpuLimit;
    private double memoryLimit;
    private MemoryCollector memoryCollector;

    public AlertEngine(MemoryCollector memoryCollector, double cpuLimit, double memoryLimit) {
        this.memoryCollector = memoryCollector;
        this.cpuLimit = cpuLimit;
        this.memoryLimit = memoryLimit;
    }

    public void checkMemory(){
        double memoryUsedPercent = memoryCollector.getMemoryUsedPercent();
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

/*
daí vem o construtor que obriga a entregar um memory collector ao alertengine no momento em que o alertengine nasce

dps vem um metodo checkmemory q retorna void
q tem uma variavel do tipo double chamado usedpercenet q é = (usamos o metodo do memory collector  pra puxar a porcentagem))

if pra caso esse numero seja maior ou = escrevea o alerta na tela

segundo metodo também void public d nome checkcpu q tem um load do tipo double como parametro.
caso load seeja maior que o cpu imprime um alerta na tela
 */