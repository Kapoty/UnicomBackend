package br.net.unicom.backend.scheduledtask;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.net.unicom.backend.service.RegistroJornadaService;

@Component
public class RegistroJornadaScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(RegistroJornadaScheduledTasks.class);

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
	public void gerenciarUsuarios() {
        registroJornadaService.gerenciarUsuarios();
    }

}
