package br.net.unicom.backend.scheduledtask;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.VendaFatura;
import br.net.unicom.backend.model.enums.VendaFaturaStatusEnum;
import br.net.unicom.backend.repository.VendaRepository;
import jakarta.transaction.Transactional;

@Component
public class FixScheduledTasks {

    Logger logger = LoggerFactory.getLogger(RegistroJornadaScheduledTasks.class);

    @Autowired
    VendaRepository vendaRepository;

    /*@Scheduled(fixedDelay  = 600000, timeUnit = TimeUnit.SECONDS)
    @Transactional
	public void fixFaturas() {

        logger.info("Fixing faturas...");

        List<Venda> vendaList = vendaRepository.findAll();

        for(Venda venda : vendaList) {

            logger.info("Corrigindo vendaId " + venda.getVendaId() + " safra " + venda.getSafra().toString());

            LocalDate safra = venda.getSafra();

            List<VendaFatura> faturaList = venda.getFaturaList();
            List<VendaFatura> faturaListCorrigida = new ArrayList<>();

            for(int i=1; i<=8; i++) {
                VendaFatura vendaFaturaCorrigida = new VendaFatura(venda, i);
                vendaFaturaCorrigida.setMes(safra.plus(i, ChronoUnit.MONTHS));
                vendaFaturaCorrigida.setStatus(VendaFaturaStatusEnum.NA);
                vendaFaturaCorrigida.setValor(0d);

                for (VendaFatura vendaFatura : faturaList) {
                    if (vendaFatura.getMes().getMonthValue() == vendaFaturaCorrigida.getMes().getMonthValue() && vendaFatura.getMes().getYear() == vendaFaturaCorrigida.getMes().getYear()) {
                        vendaFaturaCorrigida.setStatus(vendaFatura.getStatus());
                        vendaFaturaCorrigida.setValor(vendaFatura.getValor());
                    }
                }

                faturaListCorrigida.add(vendaFaturaCorrigida);
            }

            for (int i=8; i>=1; i--) {
                if (faturaListCorrigida.get(i-1).getStatus().equals(VendaFaturaStatusEnum.NA))
                    faturaListCorrigida.remove(i - 1);
                else
                    break;
            }

            //logger.info("Antes:");
            //logger.info(faturaList.toString());
            //logger.info("Depois:");
            //logger.info(faturaListCorrigida.toString());

            venda.setFaturaList(faturaListCorrigida);

            vendaRepository.saveAndFlush(venda);

            logger.info(venda.getVendaId().toString());

        }

    }*/
}
