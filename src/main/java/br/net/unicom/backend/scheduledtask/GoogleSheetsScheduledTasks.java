package br.net.unicom.backend.scheduledtask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import br.net.unicom.backend.model.Empresa;
import br.net.unicom.backend.model.GoogleSheetsVenda;
import br.net.unicom.backend.model.Venda;
import br.net.unicom.backend.model.projection.VendaDataStatusProjection;
import br.net.unicom.backend.repository.EmpresaRepository;
import br.net.unicom.backend.repository.VendaRepository;
import br.net.unicom.backend.service.GoogleDriveService;
import br.net.unicom.backend.service.GoogleSheetsService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Component
public class GoogleSheetsScheduledTasks {

    Logger logger = LoggerFactory.getLogger(GoogleSheetsScheduledTasks.class);

    @Autowired
    EmpresaRepository empresaRepository;

    @Autowired
    VendaRepository vendaRepository;

    @Autowired
    GoogleDriveService googleDriveService;

    @Autowired
    GoogleSheetsService googleSheetsService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.SECONDS)
    @Transactional
	public void espelharVendas() {

        logger.info("Espelhando vendas...");

        List<Empresa> empresaList = empresaRepository.findAll();

        for (Empresa empresa : empresaList) {
            try {
                if (empresa.getGoogledriveFolderId().isBlank()) {
                    //logger.info("Empresa sem GoogleDrive configurado!");
                    continue;
                }

                logger.info("Espelhando vendas - " + empresa.getNome());
                
                String folderId = googleDriveService.getFolderId(empresa.getGoogledriveFolderId(), "espelho/");

                File file = googleDriveService.getFile(folderId, "Vendas", false).orElse(null);

                if (file == null) {
                    logger.info("Criando planilha...");
                    
                    File fileMetadata = new File();
                    fileMetadata.setParents(Collections.singletonList(folderId));
                    fileMetadata.setName("Vendas");
                    fileMetadata.setMimeType("application/vnd.google-apps.spreadsheet");

                    file = googleDriveService.getInstance().files().create(fileMetadata).setFields("id").execute();

                    logger.info("Planilha criada com sucesso!");
                }

                Spreadsheet spreadsheet = googleSheetsService.getInstance().spreadsheets().get(file.getId()).execute();

                Sheet sheet = spreadsheet.getSheets().get(0);

                if (!sheet.getProperties().getTitle().equals("Vendas")) {
                    logger.info("Configurando folha...");

                    List<Request> requests = new ArrayList<>();

                     requests.add(new Request()
                        .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties()
                                            .setTitle("Vendas")
                                            )
                        .setFields("title")
                        )
                    );

                    BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

                    googleSheetsService.getInstance().spreadsheets().batchUpdate(spreadsheet.getSpreadsheetId(), body).execute();
                    
                    List<Object> fields = new ArrayList<>();
                    JsonSerializer<Object> serializer = objectMapper.getSerializerProviderInstance().findValueSerializer(GoogleSheetsVenda.class);
                    serializer.properties().forEachRemaining(property -> fields.add(property.getName()));

                    logger.info(fields.toString());

                    ValueRange header = new ValueRange()
                        .setValues(Arrays.asList(
                            fields
                        ));

                    googleSheetsService.getInstance().spreadsheets().values().update(spreadsheet.getSpreadsheetId(), "A1", header).setValueInputOption("RAW").execute();

                    logger.info("Folha configurada com sucesso!");
                }

                List<List<Object>> rows = googleSheetsService.getInstance().spreadsheets().values().get(spreadsheet.getSpreadsheetId(), "Vendas!A2:B99999").execute().getValues();

                Map<Integer, GoogleSheetsVenda> googleSheetsVendaMap = new HashMap<>();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                if (rows != null) {

                    for (int i = 0; i < rows.size(); i++) {
                        List<Object> row = rows.get(i);
                        if (row.size() < 2)
                            continue;
                        GoogleSheetsVenda googleSheetsVenda = new GoogleSheetsVenda();
                        Integer vendaId = Integer.parseInt(row.get(0).toString());
                        googleSheetsVenda.setVendaId(vendaId);
                        googleSheetsVenda.setDataStatus(LocalDateTime.parse(row.get(1).toString(), formatter));
                        googleSheetsVenda.setRowId(i + 2);

                        googleSheetsVendaMap.put(vendaId, googleSheetsVenda);
                    }

                }

                List<VendaDataStatusProjection> vendaDataStatusProjectionList = vendaRepository.findAllVendaDataStatusProjectionByEmpresaId(empresa.getEmpresaId());

                List<Integer> updateVendaIdList = new ArrayList<>();
                List<Integer> appendVendaIdList = new ArrayList<>();

                for (VendaDataStatusProjection vendaDataStatusProjection : vendaDataStatusProjectionList) {
                    if (!googleSheetsVendaMap.containsKey(vendaDataStatusProjection.getVendaId()))
                        appendVendaIdList.add(vendaDataStatusProjection.getVendaId());
                    else if (vendaDataStatusProjection.getDataStatus().compareTo(googleSheetsVendaMap.get(vendaDataStatusProjection.getVendaId()).getDataStatus()) != 0)
                        updateVendaIdList.add(vendaDataStatusProjection.getVendaId());
                }

                logger.info("Vendas para atualizar: " + String.valueOf(updateVendaIdList.size()));
                logger.info("Vendas para adicionar: " + String.valueOf(appendVendaIdList.size()));

                // atualizar

                while (updateVendaIdList.size() > 0) {
                    List<Integer> subIdList = updateVendaIdList.subList(0, Math.min(updateVendaIdList.size(), 1000));
                    List<Venda> vendaList = vendaRepository.findAllByVendaIdIn(subIdList);
                    subIdList.clear();
                    List<ValueRange> data = new ArrayList<>();
                    vendaList.forEach(venda -> {
                        List<Object> values = new ArrayList<>();
                        GoogleSheetsVenda googleSheetsVenda = modelMapper.map(venda, GoogleSheetsVenda.class);
                        objectMapper.valueToTree(googleSheetsVenda).fields().forEachRemaining(field -> values.add(field.getValue().asText()));

                        data.add(new ValueRange()
                            .setRange("A" + googleSheetsVendaMap.get(venda.getVendaId()).getRowId())
                            .setValues(
                                Arrays.asList(
                                    values
                                    //Arrays.asList(venda.getVendaId(), venda.getDataStatus().format(formatter), venda.getNome())
                                )
                            )
                        );                    });

                    BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(data);

                    googleSheetsService.getInstance().spreadsheets().values()
                    .batchUpdate(spreadsheet.getSpreadsheetId(), batchBody)
                    .execute();

                    logger.info(String.valueOf(vendaList.size()) + " vendas atualizadas!");

                    Thread.sleep(1000);
                } 

                // adicionar

                while (appendVendaIdList.size() > 0) {
                    List<Integer> subIdList = appendVendaIdList.subList(0, Math.min(appendVendaIdList.size(), 1000));
                    List<Venda> vendaList = vendaRepository.findAllByVendaIdIn(subIdList);
                    subIdList.clear();

                    ValueRange appendBody = new ValueRange();
                    List<List<Object>> data = new ArrayList<>();

                    vendaList.forEach(venda -> {
                        List<Object> values = new ArrayList<>();
                        GoogleSheetsVenda googleSheetsVenda = modelMapper.map(venda, GoogleSheetsVenda.class);
                        objectMapper.valueToTree(googleSheetsVenda).fields().forEachRemaining(field -> values.add(field.getValue().asText()));
                        data.add(values);
                    });

                    appendBody.setValues(data);

                    googleSheetsService.getInstance().spreadsheets().values()
                    .append(spreadsheet.getSpreadsheetId(), "A1", appendBody)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(false)
                    .execute();

                    logger.info(String.valueOf(vendaList.size()) + " vendas adicionadas!");

                    entityManager.clear();

                    Thread.sleep(1000);
                } 

            } catch (Exception e) {
                logger.error("Erro ao espelhar vendas: ", e);
            }
        }

        logger.info("Vendas espelhadas!");
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
