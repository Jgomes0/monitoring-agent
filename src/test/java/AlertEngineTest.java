import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlertEngineTest {
    // Campos da classe: o logger e o capturador são compartilhados entre o setUp, os testes e o tearDown.
    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    // Roda ANTES de cada teste: monta o capturador de logs do zero.
    @BeforeEach
    void setUp() {
        // Obtém o logger utilizado pela classe AlertEngine.
        // O cast para Logger permite acessar recursos específicos do Logback,
        // como addAppender() e setLevel().
        logger = (Logger) LoggerFactory.getLogger(AlertEngine.class);
        listAppender = new ListAppender<>(); // se nenhum log for gerado, o listappender nao recebe a mensagem // apenas recebe e armazena os logs emitidos pelo Logger.
        // Inicializa o ListAppender para que ele possa começar a receber logs.
        listAppender.start();

        // Conecta o capturador ao logger do AlertEngine.
        // A partir daqui, as mensagens registradas por esse logger
        // também serão armazenadas na lista do ListAppender.
        logger.addAppender(listAppender);
        // Define o nível mínimo de log como ALL.
        // Isso permite capturar mensagens de qualquer nível,
        // incluindo TRACE, DEBUG, INFO, WARN e ERROR.
        logger.setLevel(Level.ALL);
    }

    // Roda DEPOIS de cada teste, mesmo que ele falhe.
    @AfterEach
    void tearDown() {
        // CLEANUP
        logger.detachAppender(listAppender);
        listAppender.stop();
        // Devolve o nível original do logger (herdado do logback.xml),
        // já que o setLevel(Level.ALL) do setUp altera o logger compartilhado por todos os testes.
        logger.setLevel(null);
    }

    @Test
    public void deveImprimirAlertaQuandoMemoriaAcimaDoLimite() {
        // Cria um mock de MemoryCollector.
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);

        // Configura o comportamento do mock usando Mockito.
        // Quando getMemoryUsedPercent() for chamado, retornará 90.0%.
        // Isso simula uma utilização de memória acima do limite estabelecido.
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(90.0);

        // Instancia a classe REAL que queremos testar.
        // Passamos o mock como dependência e definimos os limites:
        // CPU = 80% e memória = 85%.
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkMemory(); // roda a engrenagem de toda a estrutura acima

        // ASSERT
        // Deve existir exatamente uma mensagem registrada: nem zero, nem duas.
        // Esta verificação vem primeiro: se a lista estiver vazia, o teste falha aqui com uma mensagem clara.
        assertEquals(1, listAppender.list.size());

        // O alerta precisa ser registrado no nível WARN (isso o teste do console não conseguia verificar).
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());

        // Acessa a lista de logs capturados e pega a primeira mensagem registrada.
        // get(0) acessa o primeiro evento e getFormattedMessage()
        // extrai o texto da mensagem de log.
        String textoImpresso = listAppender.list.get(0).getFormattedMessage();

        // Verifica se a mensagem capturada contém o texto esperado.
        // Se o alerta não tiver sido registrado corretamente, o teste falhará.
        assertTrue(textoImpresso.contains("Alerta, memoria acima do limite"));
    }

    @Test
    public void naoDeveImprimirAlertaQuandoMemoriaAbaixoDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(50.0); // abaixo do limite de 85.0
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkMemory();

        // ASSERT
        // Como o logger está em Level.ALL, qualquer mensagem seria capturada: lista vazia = nenhum alerta registrado.
        assertTrue(listAppender.list.isEmpty()); // o alerta NÃO deve ter aparecido
    }

    @Test
    public void deveImprimirAlertaQuandoCpuAcimaDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class); // só pra satisfazer o construtor, nunca é chamado aqui
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkCpu(90.0); // acima do limite de 80.0, valor passado direto por parâmetro

        // ASSERT
        assertEquals(1, listAppender.list.size());
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());
        assertTrue(listAppender.list.get(0).getFormattedMessage().contains("Alerta, CPU acima do limite"));
    }

    @Test
    public void naoDeveImprimirAlertaQuandoCpuAbaixoDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkCpu(50.0); // abaixo do limite de 80.0

        // ASSERT
        assertTrue(listAppender.list.isEmpty());
    }

    @Test
    public void deveImprimirAlertaQuandoCpuExatamenteNoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkCpu(80.0); // exatamente no limite: o código usa >=, então deve alertar

        // ASSERT
        assertEquals(1, listAppender.list.size());
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());
        assertTrue(listAppender.list.get(0).getFormattedMessage().contains("Alerta, CPU acima do limite"));
    }

    // ---------- TESTES NOVOS: o alerta só aparece quando a situação MUDA ----------

    @Test
    public void naoDeveRepetirAlertaEnquantoMemoriaContinuaAcimaDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(90.0); // sempre 90%, acima do limite de 85.0
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT: o programa checa a memória 3 vezes seguidas (como faz a cada segundo)
        alertEngine.checkMemory();
        alertEngine.checkMemory();
        alertEngine.checkMemory();

        // ASSERT: mesmo checando 3 vezes, o alerta só foi registrado uma vez
        assertEquals(1, listAppender.list.size());
    }

    @Test
    public void deveRegistrarRecuperacaoEAlertarDeNovoQuandoMemoriaVoltarASubir() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        // Cada chamada devolve o próximo valor da lista: 90, depois 50, depois 90.
        when(memoryCollector.getMemoryUsedPercent()).thenReturn(90.0, 50.0, 90.0);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkMemory(); // 90%: passou do limite
        alertEngine.checkMemory(); // 50%: voltou ao normal
        alertEngine.checkMemory(); // 90%: passou do limite de novo

        // ASSERT: devem existir 3 mensagens, nesta ordem: WARN, INFO, WARN
        assertEquals(3, listAppender.list.size());
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());
        assertEquals(Level.INFO, listAppender.list.get(1).getLevel());
        assertEquals(Level.WARN, listAppender.list.get(2).getLevel());
    }

    @Test
    public void naoDeveRepetirAlertaEnquantoCpuContinuaAcimaDoLimite() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class); // só pra satisfazer o construtor
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT: a CPU é checada 3 vezes seguidas, sempre acima do limite de 80.0
        alertEngine.checkCpu(90.0);
        alertEngine.checkCpu(90.0);
        alertEngine.checkCpu(90.0);

        // ASSERT: o alerta só foi registrado uma vez
        assertEquals(1, listAppender.list.size());
    }

    @Test
    public void deveRegistrarRecuperacaoEAlertarDeNovoQuandoCpuVoltarASubir() {
        // ARRANGE
        MemoryCollector memoryCollector = mock(MemoryCollector.class);
        AlertEngine alertEngine = new AlertEngine(memoryCollector, 80.0, 85.0);

        // ACT
        alertEngine.checkCpu(90.0); // passou do limite
        alertEngine.checkCpu(50.0); // voltou ao normal
        alertEngine.checkCpu(90.0); // passou do limite de novo

        // ASSERT: devem existir 3 mensagens, nesta ordem: WARN, INFO, WARN
        assertEquals(3, listAppender.list.size());
        assertEquals(Level.WARN, listAppender.list.get(0).getLevel());
        assertEquals(Level.INFO, listAppender.list.get(1).getLevel());
        assertEquals(Level.WARN, listAppender.list.get(2).getLevel());
    }
}