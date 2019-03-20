

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class SendEmail {
	public ExchangeService service;
	public static String[] recipientsArray;
	public static String[] attachmentsArray;
	public String attachments;
	
	String emailBody;
	String subject;
	String recipients;
	String from;
	String username;
	String password;
	String URI;
	
	public static void main(String[] args) throws URISyntaxException {
		SendEmail setupEmail = new SendEmail();
	
	//	attachments.add("C:\\Users\\tgherath\\Desktop\\hello gihan.txt");
		String emailBody="<table style=\"height: 91px;\" width=\"562\"> <tbody> <tr style=\"background-color: #8585ad;\"> <td><img style=\"float: left;\" id=\"1\" src=\"cid:wso2header\" alt=\"interactive connection\" width=\"100\" height=\"100\" /><img style=\"float: left;\" id=\"1\" src=\"cid:wso2footer\" alt=\"interactive connection\" width=\"100\" height=\"100\" /></td> <td> <p style=\"font-size: 30px;\"><span style=\"color: #ffffff;\"><strong>Automated BOT</strong></span></p> </td> </tr> </tbody> </table> <table style=\"height: 21px; background-color: #1a75ff;\" width=\"561\"> <tbody> <tr> <td style=\"text-align: center;\"><strong><span style=\"color: #ffffff;\">WSO2 Online Support System&nbsp;</span></strong></td> </tr> </tbody> </table> <table style=\"height: 124px;\" width=\"564\"> <thead> <tr style=\"background-color: #0052cc;\"> <td> <p><strong><span style=\"color: #ffffff;\">&nbsp;Issue Name</span></strong></p> </td> <td> <p><strong><span style=\"color: #ffffff;\">&nbsp;Status</span></strong></p> </td> </tr> </thead> <tbody> <tr style=\"background-color: #8585ad;\"> <td>&nbsp;</td> <td>&nbsp;</td> </tr> <tr style=\"background-color: #8585ad;\"> <td>&nbsp;</td> <td>&nbsp;</td> </tr> <tr style=\"background-color: #8585ad;\"> <td>&nbsp;</td> <td>&nbsp;</td> </tr> <tr style=\"background-color: #8585ad;\"> <td>&nbsp;</td> <td><span style=\"color: #ffffff;\">This leaves matched</span></td> </tr> <tr style=\"background-color: #8585ad;\"> <td>&nbsp;</td> <td>&nbsp;</td> </tr> </tbody> </table> <p>Copyright @ 2016  corporation. All right reserved</p> <p><strong>&nbsp;</strong></p>";
		String from="pdeeyagahage@gmail.com";
		String username="pdeeyagahage@gmail.com";
		String password="login123$";
		String URI="https://outlook.office365.com/ews/Exchange.asmx";
		String recipients="malperera@gmail.com";
		String subject ="Hello malee part 3";
		String attachments="C:/Users/pdeeyagahage/Downloads/helloWorld.txt";
		//String footer="C:/Users/tgherath/Desktop/wso2.png";
		//String header="C:/Users/tgherath/Desktop/wso2.png";
		//setupEmail.setAttachmentArray("C:\\Users\\tgherath\\Desktop\\hello gihan.txt");
		//setupEmail.setAttachments("C:\\Users\\tgherath\\Desktop\\hello gihan.txt");
			
		//setupEmail.sendEmail(setupEmail.getEmailBody(), setupEmail.getSubject(), setupEmail.getRecipients(), from, attachments, username, password, URI);
	
		
		
		//setupEmail.sendEmail(emailBody, subject, recipients, from, attachments,username,password,URI,header,footer);
		setupEmail.sendEmail(emailBody, subject, recipients, from,attachments,username,password,URI);

	}

	
	public void sendEmail(String body, String subject, String recipients,
			String from,String username,String password,String URI)
			throws URISyntaxException {
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
		service.setUrl(new URI(URI));
		ExchangeCredentials credentials = new WebCredentials(
				username,password, "");
		service.setCredentials(credentials);

		try {
			EmailMessage replymessage = new EmailMessage(service);
			EmailAddress fromEmailAddress = new EmailAddress(from);
			replymessage.setFrom(fromEmailAddress);
			recipientsArray = recipients.split(",");
			
			//attachmentsArray = attachments.split(",");
			
			
			//File file=new File("C:\\Users\\tgherath\\Desktop\\wso2.jpg");
		//	replymessage.getAttachments().addFileAttachment("wso2header",headerImage);
			//replymessage.getAttachments().addFileAttachment("wso2footer",footerImage);
			//replymessage.getAttachments().getItems().get(0).setIsInline(true);
			//replymessage.getAttachments().getItems().get(1).setIsInline(true);
			
			//Send to multiple recipients 
			for (int i = 0; i < recipientsArray.length; i++) {
				replymessage.getToRecipients().add(recipientsArray[i]);
				System.out.println(recipientsArray[i]);
			}
			//send multiple attachments
			/*System.out.println(attachmentsArray.length);
			for (int i = 0; i < attachmentsArray.length; i++) {
				replymessage.getAttachments().addFileAttachment(attachmentsArray[i]);
			}*/
			replymessage.setSubject(subject);
			replymessage.setBody(new MessageBody(body));
			replymessage.send();
			System.out.println("done");

		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
	
	/*public void sendEmail(String body, String subject, String recipients,
			String from, String attachments,String username,String password,String URI,String headerImage,String footerImage)
			throws URISyntaxException {
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
		service.setUrl(new URI(URI));
		ExchangeCredentials credentials = new WebCredentials(
				username,password, "");
		service.setCredentials(credentials);

		try {
			EmailMessage replymessage = new EmailMessage(service);
			EmailAddress fromEmailAddress = new EmailAddress(from);
			replymessage.setFrom(fromEmailAddress);
			recipientsArray = recipients.split(",");
			
			attachmentsArray = attachments.split(",");
			
			
			//File file=new File("C:\\Users\\tgherath\\Desktop\\wso2.jpg");
			replymessage.getAttachments().addFileAttachment("wso2header",headerImage);
			replymessage.getAttachments().addFileAttachment("wso2footer",footerImage);
			replymessage.getAttachments().getItems().get(0).setIsInline(true);
			replymessage.getAttachments().getItems().get(1).setIsInline(true);
			
			//Send to multiple recipients 
			for (int i = 0; i < recipientsArray.length; i++) {
				replymessage.getToRecipients().add(recipientsArray[i]);
				System.out.println(recipientsArray[i]);
			}
			//send multiple attachments
			System.out.println(attachmentsArray.length);
			for (int i = 0; i < attachmentsArray.length; i++) {
				replymessage.getAttachments().addFileAttachment(attachmentsArray[i]);
			}
			replymessage.setSubject(subject);
			replymessage.setBody(new MessageBody(body));
			replymessage.send();
			System.out.println("done");

		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}*/

	public ExchangeService getService() {
		return service;
	}

	public void setService(ExchangeService service) {
		this.service = service;
	}

	public static String[] getRecipientsArray() {
		return recipientsArray;
	}

	public static void setRecipientsArray(String[] recipientsArray) {
		SendEmail.recipientsArray = recipientsArray;
	}


	public String getEmailBody() {
		return emailBody;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	public static String[] getAttchementssArray() {
		return attachmentsArray;
	}

	public static void setAttchementssArray(String[] attchementssArray) {
		SendEmail.attachmentsArray = attchementssArray;
	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}

	
	
	

}
