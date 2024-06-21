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

    Logger logger = LoggerFactory.getLogger(RegistroJornadaScheduledTasks.class);

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
	public void gerenciarUsuarios() {
        registroJornadaService.gerenciarUsuarios();
    }

    @Scheduled(fixedRate = 8, timeUnit = TimeUnit.HOURS)
	public void criarRegistroDeUsuariosAtivos() {
        logger.info("Criando registro de usuarios ativos...");
        registroJornadaService.criarRegistroDeUsuariosAtivos();
    }

}
