
import com.utils.PropertyFileReader;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.notification.EventType;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.search.FolderTraversal;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.FolderSchema;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.notification.GetEventsResults;
import microsoft.exchange.webservices.data.notification.ItemEvent;
import microsoft.exchange.webservices.data.notification.PullSubscription;
import microsoft.exchange.webservices.data.property.complex.*;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.FolderView;
import microsoft.exchange.webservices.data.search.ItemView;

import java.io.* ;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TriggerClass extends Observable {
    ExchangeService service;
    private String rid;
    private HashMap<String, String> response = new HashMap<String, String>();

    public TriggerClass(String rid, Observer classInstance) {
        this.addObserver(classInstance);
        this.rid = rid;
        this.response.put("emailBody", "");
        this.response.put("emailSubject", "");
        this.response.put("fromAddress", "");
        this.response.put("senderName", "");
        this.response.put("sendDate", "");
        this.response.put("recievedDate", "");
        this.response.put("toRecipients", "");
        this.response.put("ccRecipients", "");
        this.response.put("bccRecipients", "");
    }

    public TriggerClass() {
        // TODO Auto-generated constructor stub
    }

    public void startEvent() throws Exception {
        HashMap<String, Object> notifyMessage = new HashMap<String, Object>();
        notifyMessage.put("function", "sendKeyValues");
        notifyMessage.put("rid", this.rid);
        notifyMessage.put("response", this.response);
        this.setChanged();
        notifyObservers(notifyMessage);
        this.clearChanged();
        Map<String, String> userDetails = null;
        userDetails = getResourceDetails();
        service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
        service.setUrl(new URI("https://outlook.office365.com/ews/Exchange.asmx"));
        ExchangeCredentials credentials = new WebCredentials(userDetails.get("username"), userDetails.get("password"),
                "");
        service.setCredentials(credentials);

        FolderView view = new FolderView(1000);
        view.setPropertySet(new PropertySet(BasePropertySet.IdOnly));
        view.getPropertySet().add(FolderSchema.DisplayName);
        view.setTraversal(FolderTraversal.Deep);
        FindFoldersResults findFolderResults = service.findFolders(WellKnownFolderName.MsgFolderRoot, view);
        // String id = findFolderResults.getFolders().get(4).getId().toString();
        List<FolderId> folders = new ArrayList<FolderId>();
        /*
         * FolderId fid = new FolderId(id); FolderId fid1 = new
		 * FolderId(WellKnownFolderName.MsgFolderRoot) folders.add(fid);
		 * folders.add(fid1);
		 */

        for (Folder folder : findFolderResults) {
            FolderId fid = new FolderId(folder.getId().toString());
            folders.add(fid);
        }
        PullSubscription subscribeResponse = service.subscribeToPullNotifications(folders, 1, null, EventType.NewMail);

        while (true) {
            GetEventsResults events = null;
            try {
                events = subscribeResponse.getEvents();
                System.out.println("---------------------------------------------------------------");
            } catch (Exception e) {
                boolean a = false;
                while (a == false) {
                    try {
                        service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);
                        service.setUrl(new URI("https://outlook.office365.com/ews/Exchange.asmx"));
                        credentials = new WebCredentials(userDetails.get("username"), userDetails.get("password"), "");
                        service.setCredentials(credentials);
                        subscribeResponse = service.subscribeToPullNotifications(folders, 1, null, EventType.NewMail);
                        System.out.println("************************************************************");
                        a = true;
                    } catch (Exception e1) {
                        a = false;
                        Thread.sleep(500);
                    }
                }
                continue;
            }
            for (ItemEvent event : events.getItemEvents()) {
                System.out.println("New mail Recieved.");
                HashMap<String, String> messageData = new HashMap<String, String>();
                ClassLoader loader = org.joda.time.format.DateTimeFormatter.class.getClassLoader();
                // System.out.println(loader.getResource(""));
                
                
                
                PropertySet BindPropSet = new PropertySet(BasePropertySet.FirstClassProperties);
                BindPropSet.setRequestedBodyType(BodyType.Text);
                Item itm = Item.bind(service, event.getItemId(), BindPropSet);
                
                EmailMessage emailMessage = EmailMessage.bind(service, itm.getId(),BindPropSet);
               
                // Find an item in a conversation. Find the first item.
                FindItemsResults<Item> results = service.findItems(WellKnownFolderName.Inbox,
                                                                   new ItemView(1));
                
                if (!(null == emailMessage.getSubject())) {
                    if (!emailMessage.getSubject().isEmpty() ) {
                        try {
                        	
                            messageData.put("emailSubject", emailMessage.getSubject());
                            messageData.put("fromAddress", emailMessage.getFrom().getAddress());
                            messageData.put("senderName", emailMessage.getSender().getName());
                            Date dateTimeCreated = emailMessage.getDateTimeCreated();
                            messageData.put("sendDate", dateTimeCreated.toString());
                            Date dateTimeRecieved = emailMessage.getDateTimeReceived();
                            messageData.put("recievedDate", dateTimeRecieved.toString());
                            messageData.put("size", emailMessage.getSize() + "");
                            messageData.put("emailBody", emailMessage.getBody().toString());

                            emailMessage.getToRecipients();

                            EmailAddressCollection ccCollection = emailMessage.getCcRecipients();
                            EmailAddressCollection toCollection = emailMessage.getToRecipients();
                            EmailAddressCollection bccCollection = emailMessage.getBccRecipients();

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

                            if (itm.getHasAttachments()) {
                                System.err.println(itm.getAttachments());
                                AttachmentCollection attachmentsCol = itm.getAttachments();
                                for (int i = 0; i < attachmentsCol.getCount(); i++) {
                                    FileAttachment attachment = (FileAttachment) attachmentsCol.getPropertyAtIndex(i);
                                    File dir = new File("Attachments");
                                    dir.mkdir();
                                    attachment.load(System.getProperty("user.dir") + File.separator + dir + File.separator
                                            + strDate + "_" + attachment.getName());

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String emailBody = messageData.get("emailBody");
                        String emailSubject = messageData.get("emailSubject");
                        String senderName = messageData.get("senderName");
                        String sendDate = messageData.get("sendDate");
                        String receivedDate = messageData.get("recievedDate");
                        String toRecipients = messageData.get("toRecipients");
                        String ccRecipients = messageData.get("ccRecipients");
                        String bccRecipients = messageData.get("bccRecipients");
                        String fromAddress = messageData.get("fromAddress");

                        System.out.println("senderName - "+ senderName);
                        System.out.println("emailSubject - "+ emailSubject);
                        System.out.println("emailBody - "+ emailBody);
                      
                       // emailMessage.update(null);
                        
                        //emailMessage.save();

                        if (fromAddress.equalsIgnoreCase(userDetails.get("fromaddress"))) {
                           
                          
                            this.response.put("emailBody", emailBody);
                            this.response.put("emailSubject", emailSubject);
                            this.response.put("senderName", senderName);
                            this.response.put("sendDate", sendDate);
                            this.response.put("recievedDate", receivedDate);
                            this.response.put("toRecipients", toRecipients);
                            this.response.put("ccRecipients", ccRecipients);
                            this.response.put("bccRecipients", bccRecipients);
                            this.response.put("fromAddress", fromAddress);

                            notifyMessage = new HashMap<String, Object>();
                            notifyMessage.put("function", "fireEvent");
                            notifyMessage.put("rid", this.rid);
                            notifyMessage.put("response", this.response);
                            this.setChanged();
                            notifyObservers(notifyMessage);
                            this.clearChanged();

                        }
                    }
                }
               saveToFile(emailMessage, messageData.get("emailBody"));
           

            }
            
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) throws Exception {

        try {
            // startEvent();
            TriggerClass triggerClass = new TriggerClass();
            triggerClass.startEvent();

            Map<String, String> userDetails = new HashMap<String, String>();
            userDetails = getResourceDetails();
            System.out.println(
                    userDetails.get("username"));
            System.out.println(
                    userDetails.get("password"));
            System.out.println(
                    userDetails.get("fromaddress"));

            

        } catch (Exception e) {
            e.printStackTrace();
        }

        
        
        
    }
    

    private static String saveToFile(EmailMessage emailMessage,String body){
    	String path="Not Saved";
    	try{
    		DateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss ");
    		Date asd =emailMessage.getDateTimeReceived();
    		String reportDate = df.format(asd);
    		String sender_name = emailMessage.getSender().getName();
    		String email_subject = emailMessage.getSubject().replaceAll(":", "-");
    		
           // path  = "C:\\Users\\pdeeyagahage\\Downloads\\"+reportDate+"_"+sender_name+".eml";
            path  = "C:\\Users\\pdeeyagahage\\Downloads\\"+reportDate+"_"+sender_name+"_"+email_subject+".eml";
            emailMessage.load(new PropertySet(ItemSchema.MimeContent));
        
            MimeContent mc = emailMessage.getMimeContent();
          
         
            File file = new File(path);
            file.createNewFile();

            FileOutputStream fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				
				file.createNewFile();
			}
            
		
			fop.write(mc.getContent());
			fop.flush();
			fop.close();	
		
        }catch(IOException ex){
        	System.out.println("Exception");
        } catch (Exception e) {
        	System.out.println("Exception"+e.getMessage());
        	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return path;
    }
    
    
    
    @SuppressWarnings("null")
    public static Map<String, String> getResourceDetails() {
        Map<String, String> Credentials = new HashMap<String, String>();
        String username = System.getProperty("user.name");
        System.out.println("watching for user : 1111" + username);
        String path = File.separator + "C:" +File.separator + "Users" + File.separator + "pdeeyagahage" + File.separator+"Desktop"
                + File.separator + "emailUser.properties";
    

        PropertyFileReader reader = new PropertyFileReader(path);
        Credentials.put("username", reader.getPropertyValue("EMIAL_USERNAME"));
        Credentials.put("password", reader.getPropertyValue("EMAIL_PASSWORD"));
        Credentials.put("fromaddress", reader.getPropertyValue("FROM_ADDRESS"));
        return Credentials;
    }

}
