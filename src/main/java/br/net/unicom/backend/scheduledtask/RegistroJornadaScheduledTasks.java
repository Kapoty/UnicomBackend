package br.net.unicom.backend.scheduledtask;

import java.io.FileWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.service.RegistroJornadaService;
import jakarta.transaction.Transactional;

@Component
public class RegistroJornadaScheduledTasks {

    Logger logger = LoggerFactory.getLogger(RegistroJornadaScheduledTasks.class);

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Autowired
    VendaRepository vendaRepository;

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
	public void gerenciarUsuarios() {
        registroJornadaService.gerenciarUsuarios();
    }

    /*@Transactional
    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void writeCsv() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long memory = runtime.totalMemory() - runtime.freeMemory();
            logger.info(String.valueOf(memory));
            Instant start = Instant.now();
            Instant finish;
            long timeElapsed;

            Writer writer = new FileWriter("yourfile.csv");
            StatefulBeanToCsv<Venda> beanToCsv = new StatefulBeanToCsvBuilder<Venda>(writer).build();
            beanToCsv.write(vendaRepository.findAll());
            writer.close();

            finish = Instant.now();
            timeElapsed = Duration.between(start, finish).toMillis();
            logger.info("1: " + String.valueOf(timeElapsed));
            memory = runtime.totalMemory() - runtime.freeMemory();
            logger.info(String.valueOf(memory));
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
    }*/

}
