package br.net.unicom.backend.service;

import java.time.Duration;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.net.unicom.backend.model.RegistroPonto;
import br.net.unicom.backend.repository.RegistroPontoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RegistroPontoService {

    Logger logger = LoggerFactory.getLogger(RegistroPontoService.class);

    @Autowired
    private RegistroPontoRepository registroPontoRepository;

    public Integer getLockedSeconds(RegistroPonto registroPonto) {

        LocalTime now = LocalTime.now();
        int minutesLocked = 1;

        if (registroPonto.getEntrada() == null)
            return 0;
        else if (registroPonto.getIntervaloInicio() == null)
            return (int) Math.max(Duration.between(now, registroPonto.getEntrada().plusMinutes(minutesLocked)).toSeconds(), 0);
        else if (registroPonto.getIntervaloFim() == null)
            return (int) Math.max(Duration.between(now, registroPonto.getIntervaloInicio().plusMinutes(minutesLocked)).toSeconds(), 0);
        else if (registroPonto.getSaida() == null)
            return (int) Math.max(Duration.between(now, registroPonto.getIntervaloFim().plusMinutes(minutesLocked)).toSeconds(), 0);
        
        return 0;
    }

}
