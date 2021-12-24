package com.cv.integration.listener;

import com.cv.integration.common.Util1;
import com.cv.integration.entity.*;
import com.cv.integration.repo.GlRepo;
import com.cv.integration.repo.SeqTableRepo;
import com.cv.integration.repo.SystemPropertyRepo;
import com.cv.integration.repo.TraderRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Session;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountMessageListener {
    @Autowired
    private final GlRepo glRepo;
    @Autowired
    private final TraderRepo traderRepo;
    @Autowired
    private final JmsTemplate jmsTemplate;
    @Autowired
    private final SystemPropertyRepo systemPropertyRepo;
    @Autowired
    private final SeqTableRepo seqTableRepo;
    private static final String LISTEN_QUEUE = "ACCOUNT_QUEUE";
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final Map<String, String> hmProperty = new HashMap<>();


    private void sendMessage(String senderQueue, String entity, String code) {
        MessageCreator mc = (Session session) -> {
            MapMessage mm = session.createMapMessage();
            mm.setString("ENTITY", entity);
            mm.setString("CODE", code);
            return mm;
        };
        jmsTemplate.send(senderQueue, mc);
        log.info(String.format("sendMessage: %s", entity));
    }

    @JmsListener(destination = LISTEN_QUEUE)
    private void receivedMessage(MapMessage message) throws JMSException {
        String entity = message.getString("ENTITY");
        log.info(String.format("receivedMessage: %s", entity));
        switch (entity) {
            case "GL" -> processGL(message);
            case "GL_LIST" -> processGLList(message);
            case "TRADER" -> processTrader(message);
            default -> log.error("Unexpected value: " + entity);
        }
    }

    private void processGL(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                Gl gl = gson.fromJson(data, Gl.class);
                String vouNo = gl.getRefNo();
                glRepo.deleteGl(vouNo, gl.getTranSource());
                if (!gl.isDeleted()) {
                    if (Util1.isMultiCur()) {
                        if (gl.isCash()) {
                            gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                        }
                    }
                    gl.setGlCode(getGlCode(gl.getCompCode(), gl.getMacId()));
                    log.info("GL Code: " + gl.getGlCode());
                    glRepo.save(gl);
                }
                //telling received successfully
                sendMessage(senderQueue, entity, vouNo);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processGL: %s", e));
        }
    }

    private void processGLList(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                Gl[] glList = gson.fromJson(data, Gl[].class);
                String vouNo = glList[0].getRefNo();
                String tranSource = glList[0].getTranSource();
                boolean delete = glList[0].isDeleted();
                glRepo.deleteGl(vouNo, tranSource);
                if (!delete) {
                    for (Gl gl : glList) {
                        if (Util1.isMultiCur()) {
                            if (gl.isCash()) {
                                gl.setSrcAccCode(Util1.getProperty(gl.getCurCode()));
                            }
                        }
                        gl.setGlCode(getGlCode(gl.getCompCode(), gl.getMacId()));
                        glRepo.save(gl);
                    }
                }
                //telling received successfully
                sendMessage(senderQueue, entity, vouNo);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processGL: %s", e));
        }
    }

    private void processTrader(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                Trader trader = gson.fromJson(data, Trader.class);
                String type = trader.getDiscriminator();
                String compCode = trader.getCompCode();
                if (Objects.isNull(trader.getAccountCode())) trader.setAccountCode(getTraderAccount(type, compCode));
                traderRepo.save(trader);
                String traderCode = trader.getTraderCode();
                sendMessage(senderQueue, entity, traderCode);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processCustomer: %s", e));
        }
    }

    private String getTraderAccount(String type, String compCode) {
        if (hmProperty.get(type) == null) {
            String propKey = type.equals("C") ? "system.customer.setup.account" : "system.supplier.setup.account";
            Optional<SystemProperty> sys = systemPropertyRepo.findById(new SystemPropertyKey(propKey, compCode));
            sys.ifPresent(systemProperty -> hmProperty.put(type, systemProperty.getPropValue()));
        }
        return hmProperty.get(type);
    }

    private String getGlCode(String compCode, Integer macId) {
        int seqNo = 1;
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyyyy");
        SeqKey key = new SeqKey();
        key.setCompCode(compCode);
        key.setPeriod(period);
        key.setSeqOption("GL");
        key.setMacId(macId);
        Optional<SeqTable> seq = seqTableRepo.findById(key);
        if (seq.isPresent()) {
            seqNo = seq.get().getSeqNo();
        }
        SeqTable seqTable = new SeqTable();
        seqTable.setSeqKey(key);
        seqTable.setSeqNo(seqNo + 1);
        seqTableRepo.save(seqTable);
        return String.format("%0" + 3 + "d", macId) + period + String.format("%0" + 9 + "d", seqNo);
    }
}
