import org.slf4j.LoggerFactory;
import java.net.InetAddress; // coletar informações da rede como nome da maquina,endereço ip,hostnome (Quero usar a classe InetAddress, que está dentro do pacote java.net)
import java.net.UnknownHostException; // captar exceções
import org.slf4j.Logger; // esse é o import certo, a maioria das vezes o inteljj oferece o java.util.logging.Logger

public class SystemInfoCollector {
    private static final Logger logger = LoggerFactory.getLogger(SystemInfoCollector.class);
    public void printBasicInfo() {
       // Informações do sistema operacional, independem da rede
        String sistemaOperacional = System.getProperty("os.name");
        String versaoSistema = System.getProperty("os.version");
        String arquitetura = System.getProperty("os.arch");
        System.out.println("Sistema operacional: " + sistemaOperacional);
        System.out.println("Versão: " + versaoSistema);
        System.out.println("Arquitetura: " + arquitetura);
        try {
            InetAddress localHost = InetAddress.getLocalHost(); // captar informações da rede local
            String hostname = localHost.getHostName(); // captar o nome da maquina local
            System.out.println("Servidor: " + hostname);

        } catch (UnknownHostException e) { // caso não consiga descobrir o hostname pedido do inetdress
            logger.error("não foi possível descobrir o hostname", e);
        }
    }
    }
