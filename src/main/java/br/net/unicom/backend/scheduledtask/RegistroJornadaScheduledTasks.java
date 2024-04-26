package br.net.unicom.backend.scheduledtask;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.net.unicom.backend.service.RegistroJornadaService;

@Component
public class RegistroJornadaScheduledTasks {

    @Autowired
    RegistroJornadaService registroJornadaService;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
	public void gerenciarUsuarios() {
        registroJornadaService.gerenciarUsuarios();
    }

}
