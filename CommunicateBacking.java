package com.sophlean.rmes.web.backing.mi.semiproduct;

import com.sophlean.core.ctx.SessionContext;
import com.sophlean.core.ctx.security.PermissionManager;
import com.sophlean.core.ctx.web.Page;
import com.sophlean.core.util.PhantomJsUtils;
import com.sophlean.core.util.log.Log;
import com.sophlean.rmes.service.quartz.EmailFromMskjAdmin;
import com.sophlean.rmes.web.backing.AbstractBacking;
import org.springframework.context.annotation.Scope;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.*;

/**
 * Created by lql on 2016/10/10.
 */
@Named("communicateBacking")
@Scope("session")
public class CommunicateBacking extends AbstractBacking {

    private static final long serialVersionUID = 615615615L;

    protected static final Log log = Log.getLog(CommunicateBacking.class);

    @Resource
    private PhantomJsUtils phantomJsUtils;
    @Resource
    private EmailFromMskjAdmin emailFromMskjAdmin;
    @Resource
    private SessionContext sessionContext;

    @Named("communicatePages")
    @Scope("singleton")
    public static class Pages {
        public static final Page PAGE = new Page(
                "page.communicate",
                "/views/mi/communicate.xhtml",
                "#{communicateBacking.gotoPage()}",
                PermissionManager.PRODUCTION_KPI_VIEW);
    }

    public CommunicateBacking() {
        super(Pages.PAGE);
    }

    private List<String> errorList1 = new ArrayList<>();
    private List<String> errorList2 = new ArrayList<>();
    private List<String> errorList3 = new ArrayList<>();
    private List<String> toErrorList1 = Arrays.asList("指标");
    private List<String> toErrorList2 = Arrays.asList("根因举措");
    private List<String> toErrorList3 = Arrays.asList("行动进度");
    private String emailContent = "hello";
    private Master master;
    private boolean send1 = false;
    private boolean send2 = false;
    private boolean send3 = false;
    private Map<String, Master> masterMap = new HashMap<>();

    @Override
    protected void doReset() {
        master = new Master();
        master.setName("lql");
        master.setType("生产问题");
        master.setEmailAddress("304592637@qq.com");
        master.setPhoto("/in/img/p1.jpg");
        masterMap.put(master.getName(), master);
        errorList1.clear();
        errorList2.clear();
        errorList3.clear();

    }

    public void generatePicture() {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        phantomJsUtils.generate("d:/qm/rmes3.qm/web/conf/p.js", "http://localhost:8080/views/mi/overallCockpitAnalyze.xhtml", path.substring(1, path.length() - 17), "/in/img/errorPicture1.png", 50, 50);
        phantomJsUtils.generate("d:/qm/rmes3.qm/web/conf/p.js", "http://localhost:8080/views/mi/overallCockpitAnalyze.xhtml", path.substring(1, path.length() - 17), "/in/img/errorPicture2.png", 50, 50);
        phantomJsUtils.generate("d:/qm/rmes3.qm/web/conf/p.js", "http://localhost:8080/views/mi/overallCockpitAnalyze.xhtml", path.substring(1, path.length() - 17), "/in/img/errorPicture3.png", 50, 50);

    }

    public void valueChange1() {
        if(!errorList1.isEmpty()) {
            send1 = true;
        }
        else {
            send1 = false;
        }
    }

    public void valueChange2() {
        if(!errorList2.isEmpty()) {
            send2 = true;
        }
        else {
            send2 = false;
        }
    }

    public void valueChange3() {
        if(!errorList3.isEmpty()) {
            send3 = true;
        }
        else {
            send3 = false;
        }
    }

    public void sendEmail() {
        master = masterMap.get(master.getName());
        new Thread() {
            public void run() {
                Properties prop = new Properties();
                prop.setProperty("mail.host", "smtp.163.com");
                prop.setProperty("mail.transport.protocol", "smtp");
                prop.setProperty("mail.smtp.auth", "true");
                Session session = Session.getInstance(prop);
                try {
                    Transport ts = session.getTransport();
                    ts.connect("smtp.163.com", "18233566165", "lql304592637");
                    MimeMessage message = createMsg(session);
                    ts.sendMessage(message, message.getAllRecipients());
                    ts.close();
                }
                catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }.start();
        errorList1.clear();
        errorList2.clear();
        errorList3.clear();
    }

    private MimeMessage createMsg(Session session) {
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("18233566165@163.com"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(master.getEmailAddress()));
            String subject = "问题解决" + "&" + master.getType();
            message.setSubject(subject);
            MimeBodyPart text = new MimeBodyPart();

            StringBuilder mailContent = new StringBuilder();
            mailContent.append("<h3>" + subject + "</h3>").append("</br>");
            mailContent.append("内容：" + emailContent).append("</br>");
            if(send1 || send2 || send3) {
                mailContent.append("问题截图如下：").append("</br>");
                if(send1) {
                    mailContent.append("<img src='cid:error1.png'>").append("</br>");
                }
                if(send2) {
                    mailContent.append("<img src='cid:error2.png'>").append("</br>");
                }
                if(send3) {
                    mailContent.append("<img src='cid:error3.png'>").append("</br>");
                }
            }
            text.setContent(mailContent.toString(), "text/html;charset=UTF-8");
            // 准备图片数据
            MimeMultipart mm = new MimeMultipart();
            if(send1) {
                MimeBodyPart image = new MimeBodyPart();
                String path = this.getClass().getClassLoader().getResource("").getPath();
                DataHandler dh = new DataHandler(new FileDataSource(path.substring(1, path.length() - 17) + "/in/img/errorPicture1.png"));
                image.setDataHandler(dh);
                image.setContentID("error1.png");
                mm.addBodyPart(image);
            }
            if(send2) {
                MimeBodyPart image = new MimeBodyPart();
                String path = this.getClass().getClassLoader().getResource("").getPath();
                DataHandler dh = new DataHandler(new FileDataSource(path.substring(1, path.length() - 17) + "/in/img/errorPicture2.png"));
                image.setDataHandler(dh);
                image.setContentID("error2.png");
                mm.addBodyPart(image);
            }
            if(send3) {
                MimeBodyPart image = new MimeBodyPart();
                String path = this.getClass().getClassLoader().getResource("").getPath();
                DataHandler dh = new DataHandler(new FileDataSource(path.substring(1, path.length() - 17) + "/in/img/errorPicture3.png"));
                image.setDataHandler(dh);
                image.setContentID("error3.png");
                mm.addBodyPart(image);
            }
            mm.addBodyPart(text);
            mm.setSubType("related");

            message.setContent(mm);
            message.saveChanges();
            return message;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<String> getErrorList1() {
        return errorList1;
    }

    public void setErrorList1(List<String> errorList1) {
        this.errorList1 = errorList1;
    }

    public List<String> getErrorList2() {
        return errorList2;
    }

    public void setErrorList2(List<String> errorList2) {
        this.errorList2 = errorList2;
    }

    public List<String> getErrorList3() {
        return errorList3;
    }

    public void setErrorList3(List<String> errorList3) {
        this.errorList3 = errorList3;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }

    public List<String> getToErrorList1() {
        return toErrorList1;
    }

    public void setToErrorList1(List<String> toErrorList1) {
        this.toErrorList1 = toErrorList1;
    }

    public List<String> getToErrorList2() {
        return toErrorList2;
    }

    public void setToErrorList2(List<String> toErrorList2) {
        this.toErrorList2 = toErrorList2;
    }

    public List<String> getToErrorList3() {
        return toErrorList3;
    }

    public void setToErrorList3(List<String> toErrorList3) {
        this.toErrorList3 = toErrorList3;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }
}
