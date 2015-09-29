package co.com.binariasystems.fmw.util.velocity;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.activation.FileDataSource;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import co.com.binariasystems.fmw.util.mail.SimpleMailMessage;
import co.com.binariasystems.fmw.util.mail.javamail.JavaMailSender;
import co.com.binariasystems.fmw.util.mail.javamail.MimeMessageHelper;
import co.com.binariasystems.fmw.util.mail.javamail.MimeMessagePreparator;

public class VelocityMailSender {
	private VelocityEngineFactoryHelper velocityEngineFactory;
	private JavaMailSender mailSender;

	

	public VelocityEngineFactoryHelper getVelocityEngineFactory() {
		return velocityEngineFactory;
	}

	public void setVelocityEngineFactory(VelocityEngineFactoryHelper velocityEngineFactory) {
		this.velocityEngineFactory = velocityEngineFactory;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void send(final SimpleMailMessage msg, final String templatePath, final Map<String, Object> templateParams) {
		send(msg, templatePath, templateParams, null, null);
	}
	
	public void sendWithInlineResources(final SimpleMailMessage msg, final String templatePath, final Map<String, Object> templateParams, final Map<String, File> inlineResources) {
		send(msg, templatePath, templateParams, inlineResources, null);
		
	}

	public void sendWithAtachments(final SimpleMailMessage msg, final String templatePath, final Map<String, Object> templateParams, final Map<String, File> attachments) {
		send(msg, templatePath, templateParams, null, attachments);
	}

	public void send(final SimpleMailMessage msg, final String templatePath, final Map<String, Object> templateParams, final Map<String, File> inlineResources, final Map<String, File> attachments) {
		
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
            	MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED);
               message.setTo(msg.getTo());
               message.setFrom(msg.getFrom());
               message.setSubject(msg.getSubject());
               message.setSentDate(new Date());
               
               if(inlineResources != null){
            	   for(String key : inlineResources.keySet()){
            		   FileDataSource resource = new FileDataSource(inlineResources.get(key));
            		   message.addInline(key, resource);
            	   }
               }
               
               if(attachments != null){
            	   for(String key : attachments.keySet()){
            		   FileDataSource atachment = new FileDataSource(attachments.get(key));
            		   message.addAttachment(key, atachment);
            	   }
               }
               
               if(templateParams != null){
            	   templateParams.put("dateTool", new DateTool());
            	   templateParams.put("numberTool", new NumberTool());
               }
               
               String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngineFactory.getEngine(), templatePath, "UTF-8", templateParams);
               
               message.setText(body, true);
            }
         };
         
         mailSender.send(preparator);
	}
}
