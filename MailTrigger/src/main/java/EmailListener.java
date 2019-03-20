

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.notification.ItemEvent;
import microsoft.exchange.webservices.data.notification.PullSubscription;
import microsoft.exchange.webservices.data.property.complex.Attachment;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

//Tharindu Gihan
public class EmailListener {
	ExchangeService service;
	HashMap<String, String> response;
	TriggerClass triggerClass=null;



	public void beforeMethod() throws Exception {

		service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
		service.setUrl(new URI(
				"https://outlook.office365.com/ews/Exchange.asmx"));
		ExchangeCredentials credentials = new WebCredentials("padalwis@gmail.com", "7ujm2wsx!@", "");
		service.setCredentials(credentials);
		
		

	}
	

	public void ListenToExchangeEmails(TriggerClass a) throws Exception {
		synchronized(this){
//		beforeMethod();
		
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
		service.setUrl(new URI(
				"https://outlook.office365.com/ews/Exchange.asmx"));
		ExchangeCredentials credentials = new WebCredentials("padalwis@gmail.com", "7ujm2wsx!@", "");
		service.setCredentials(credentials);
		
		FolderId fid = new FolderId(WellKnownFolderName.Inbox);
		List<FolderId> folders = new ArrayList<FolderId>();
		folders.add(fid);

		PullSubscription subscribeResponse = service.subscribeToPullNotifications(folders, 1, null,EventType.NewMail);
		//PullSubscription subscribeResponse2 = service.subscribeToPullNotifications();
		while (true) {
			GetEventsResults events = subscribeResponse.getEvents();
			for (ItemEvent event : events.getItemEvents()) {
				System.out.println(event.getItemId());
				//EmailMessage emailMessage = EmailMessage.bind(service,event.getItemId());

				response = readEmailItem(event.getItemId());
				if(!response.isEmpty()){
					notify(); // EmialListener is notified.
				}
				
				String emailBody =response.get("emailBody");
//				a.passEvent(emailBody);
//				TriggerClass.passEvent1(emailBody);
				System.out.println(emailBody);
				
							
				
			}

			Thread.sleep(500);

		}
		}

	}

	public HashMap<String, String> readEmailItem(ItemId itemId) {
		HashMap<String, String> messageData = new HashMap<String, String>();
		try {
			Item itm = Item.bind(service, itemId,
					PropertySet.FirstClassProperties);
			EmailMessage emailMessage = EmailMessage.bind(service, itm.getId());
			// messageData.put("emailItemId", emailMessage.getId().toString());
			messageData.put("emailSubject", emailMessage.getSubject().toString());
			messageData.put("fromAddress", emailMessage.getFrom().getAddress()
					.toString());
			messageData.put("senderName", emailMessage.getSender().getName()
					.toString());
			Date dateTimeCreated = emailMessage.getDateTimeCreated();
			messageData.put("sendDate", dateTimeCreated.toString());
			Date dateTimeRecieved = emailMessage.getDateTimeReceived();
			messageData.put("recievedDate", dateTimeRecieved.toString());
			messageData.put("size", emailMessage.getSize() + "");
			messageData.put("emailBody", emailMessage.getBody().toString());
			
			

			emailMessage.getToRecipients();

			EmailAddressCollection ccCollection = emailMessage
					.getCcRecipients();
			EmailAddressCollection toCollection = emailMessage
					.getToRecipients();
			EmailAddressCollection bccCollection = emailMessage
					.getBccRecipients();

			List<EmailAddress> ccAddressList = ccCollection.getItems();
			List<EmailAddress> toAddressList = toCollection.getItems();
			List<EmailAddress> bccAddressList = bccCollection.getItems();

			String ccNames = "";
			String toNames = "";
			String bccNames = "";

			for (EmailAddress emailAddress : ccAddressList) {
				if (ccNames == "") {
					ccNames = ccNames + emailAddress.getAddress();
				} else {
					ccNames = ccNames + "," + emailAddress.getAddress();
				}
			}
			for (EmailAddress emailAddress : toAddressList) {
				if (toNames == "") {
					toNames = toNames + emailAddress.getAddress();
				} else {
					toNames = toNames + "," + emailAddress.getAddress();
				}

			}
			for (EmailAddress emailAddress : bccAddressList) {
				if (bccNames == "") {
					bccNames = bccNames + emailAddress.getAddress();
				} else {
					bccNames = bccNames + "," + emailAddress.getAddress();
				}

			}

			messageData.put("ccRecipients", ccNames);
			messageData.put("toRecipients", toNames);
			messageData.put("bccRecipients", bccNames);
			
			SimpleDateFormat sdfDate = new SimpleDateFormat("HH.mm a");// dd/MM/yyyy
			String strDate = sdfDate.format(dateTimeRecieved);
			// Attchements saving

			if (itm.getHasAttachments()) {
				System.err.println(itm.getAttachments());
				AttachmentCollection attachmentsCol = itm.getAttachments();
				for (int i = 0; i < attachmentsCol.getCount(); i++) {
					FileAttachment attachment = (FileAttachment) attachmentsCol
							.getPropertyAtIndex(i);
					File dir = new File("Attachments");
					dir.mkdir();
					attachment.load(System.getProperty("user.dir")+File.separator +dir+File.separator+ strDate + "_"
							+ attachment.getName());

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return messageData;
	}


	public HashMap<String, String> getResponse() {
		return response;
	}
	
	
}
