package com.cv.integration.listener;

import com.cv.integration.common.Util1;
import com.cv.integration.entity.*;
import com.cv.integration.repo.*;
import com.cv.integration.service.COAService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountMessageListener {
    @Autowired
    private final GlRepo glRepo;
    @Autowired
    private final GlService glService;
    @Autowired
    private final TraderRepo traderRepo;
    @Autowired
    private final JmsTemplate jmsTemplate;

    @Autowired
    private final COAOpeningRepo openingRepo;
    @Autowired
    private final COAService coaService;
    @Autowired
    private final COARepo coaRepo;
    private static final String LISTEN_QUEUE = "ACCOUNT_QUEUE";
    private final Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
    private final HashMap<String, String> hmSrc = new HashMap<>();
    private final HashMap<String, String> hmAcc = new HashMap<>();
    private final HashMap<String, String> hmDep = new HashMap<>();


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
            case "GL_NEW" -> processGLNEW(message);
            case "TRADER" -> processTrader(message);
            case "GL_DEL" -> deleteGL(message);
            case "OPENING" -> processOpening(message);
            case "COA" -> processCOA(message);
            default -> log.error("Unexpected value: " + entity);
        }
    }

    private void processCOA(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                ChartOfAccount coa = gson.fromJson(data, ChartOfAccount.class);
                coa = coaService.save(coa);
                String code = String.format("%s,%s", coa.getMigCode(), coa.getKey().getCoaCode());
                sendMessage(senderQueue, entity, code);
            }
        } catch (Exception e) {
            log.error(String.format("processCOA: %s", e.getMessage()));
        }
    }

    private void processOpening(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                COAOpening op = gson.fromJson(data, COAOpening.class);
                op.setSourceAccId(getAccount(op.getTraderCode()));
                openingRepo.deleteOpening(op.getTraderCode());
                openingRepo.save(op);
                sendMessage(senderQueue, entity, op.getTraderCode());
            }
        } catch (JMSException e) {
            log.error(String.format("processOpening: %s", e.getMessage()));
        }

    }

    private void deleteGL(MapMessage message) {
        try {
            String vouNo = message.getString("VOU_NO");
            String tranSource = message.getString("TRAN_SOURCE");
            String srcAcc = message.getString("SRC_ACC");
            if (Objects.isNull(srcAcc)) {
                glRepo.deleteGl(vouNo, tranSource);
            } else {
                glRepo.deleteGl(vouNo, tranSource, srcAcc);

            }
        } catch (JMSException e) {
            log.error(String.format("deleteGL: %s", e.getMessage()));

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
                            gl.setSrcAccCode(Util1.hmSysProp.get(gl.getCurCode()));
                        }
                    }
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
                                gl.setSrcAccCode(Util1.hmSysProp.get(gl.getCurCode()));
                            }
                        }
                        double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                       if (amt > 0) {
                            //follow coa department
                            String deptCode = Util1.isNull(getDeptCode(gl.getSrcAccCode()), getDeptCode(gl.getAccCode()));
                            if (deptCode != null) {
                                gl.setDeptCode(deptCode);
                            }
                            glService.save(gl);
                        }
                    }
                }
                //telling received successfully
                sendMessage(senderQueue, entity, vouNo);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processGL: %s", e));
        }
    }

    private String getDeptCode(String account) {
        if (hmDep.isEmpty()) {
            List<ChartOfAccount> list = coaService.getCOADepartment();
            if (!list.isEmpty()) {
                list.forEach((d) -> hmDep.put(d.getKey().getCoaCode(), d.getDeptCode()));
            } else {
                hmDep.put("EMP", "EMP");
            }
        }
        return hmDep.get(account);
    }

    private void processGLNEW(MapMessage message) {
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
                        if (Objects.isNull(gl.getSrcAccCode())) {
                            gl.setSrcAccCode(getSourceAcc(gl));
                        }
                        if (Objects.isNull(gl.getAccCode())) {
                            gl.setAccCode(getAccount(gl.getTraderCode()));
                        }
                        if (Util1.isMultiCur()) {
                            if (gl.isCash()) {
                                gl.setSrcAccCode(Util1.hmSysProp.get((gl.getCurCode())));
                            }
                        }
                        double amt = Util1.getDouble(gl.getDrAmt()) + Util1.getDouble(gl.getCrAmt());
                        if (amt > 0) {
                            glRepo.save(gl);
                        }
                    }
                }
                //telling received successfully
                sendMessage(senderQueue, entity, vouNo);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processGL: %s", e));
        }
    }

    private String getSourceAcc(Gl gl) {
        String createdBy = gl.getCreatedBy();
        String compCode = gl.getCompCode();
        Integer macId = gl.getMacId();
        String coaName = gl.getMigName();
        String catId = gl.getMigId();
        String coaParent = gl.getCoaParent();
        String key = String.format("%s*%s*%s", catId, coaParent, compCode);
        if (hmSrc.isEmpty()) {
            List<ChartOfAccount> lv3 = coaRepo.getLV3(compCode);
            for (ChartOfAccount coa : lv3) {
                String tmp = String.format("%s*%s*%s", coa.getMigCode(), coa.getCoaParent(), compCode);
                hmSrc.put(tmp, coa.getKey().getCoaCode());
            }
        }
        String account = hmSrc.get(key);
        if (Objects.isNull(account)) {
            ChartOfAccount coa = new ChartOfAccount();
            COAKey coaKey = new COAKey();
            coaKey.setCompCode(compCode);
            coa.setKey(coaKey);
            coa.setCoaNameEng(coaName);
            coa.setActive(true);
            coa.setMarked(false);
            coa.setCreatedDate(Util1.getTodayDate());
            coa.setCreatedBy(createdBy);
            coa.setCoaParent(coaParent);
            coa.setOption("USR");
            coa.setCoaLevel(3);
            coa.setMigCode(catId);
            coa.setMacId(macId);
            coa = coaService.save(coa);
            account = coa.getKey().getCoaCode();
            log.info("crate new chart of account.");
            hmSrc.put(key, coa.getKey().getCoaCode());
        }
        return account;
    }

    private String getAccount(String traderCode) {
        if (hmAcc.isEmpty()) {
            List<Trader> traders = traderRepo.findAll();
            for (Trader trader : traders) {
                hmAcc.put(trader.getTraderCode(), trader.getAccountCode());
            }
        }
        return hmAcc.get(traderCode);
    }

    private void processTrader(MapMessage message) {
        try {
            String senderQueue = message.getString("SENDER_QUEUE");
            String entity = String.format("ACK_%s", message.getString("OPTION"));
            String data = message.getString("DATA");
            if (!Objects.isNull(data)) {
                Trader trader = gson.fromJson(data, Trader.class);
                trader.setAccountCode(getChartOfAccount(trader.getDiscriminator()));
                traderRepo.save(trader);
                String traderCode = trader.getTraderCode();
                sendMessage(senderQueue, entity, traderCode);
            }
        } catch (Exception e) {
            throw new IllegalStateException(String.format("processCustomer: %s", e));
        }
    }


    private String getChartOfAccount(String type) {
        return type.equals("C") ? Util1.getCusAcc() : Util1.getSupAcc();
    }


}
