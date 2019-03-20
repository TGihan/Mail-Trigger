package com.utils;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

public class ReadSavedMail {
	
	private static String getTextFromMessage(Message message) throws Exception {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	   // System.out.println(result);
	    return result;
	    
	}
	
	private static String getTextFromMimeMultipart(
	        MimeMultipart mimeMultipart) throws Exception{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
	        } else if (bodyPart.isMimeType("text/html")) {
	            String html = (String) bodyPart.getContent();
	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}
	
	public static void display(File emlFile) throws Exception{
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");

        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream source = new FileInputStream(emlFile);
        MimeMessage message = new MimeMessage(mailSession, source);
        
        System.out.println("From : " + message.getFrom()[0]);
        System.out.println("Subject : " + message.getSubject());
        System.out.println("-----------------------------------");
        String mailbody = getTextFromMessage(message);
        System.out.println("Mail body:-");
        System.out.println(mailbody);
        
        //System.out.println("Body : " +  message.getContent());
        
    }
	
   public static void main(String args[]) throws Exception{
	   
	   File path = new File("C:\\Users\\pdeeyagahage\\Downloads\\20_10_2016_12_09_39 _Maleesha Perera_Maleesha Perera has shared 'JulyLogMalee'.eml");
       display(path);
   }
   
   
   
   
   
   
   
}
