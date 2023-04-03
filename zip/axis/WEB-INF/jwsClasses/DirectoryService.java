import java.io.BufferedReader;

import java.io.File;

import java.io.FileInputStream;

import java.io.FileReader;

import java.io.FileWriter;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

import java.io.StringReader;

import java.io.UnsupportedEncodingException;

import java.net.URL;

import java.security.KeyStore;

import java.security.SecureRandom;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import java.util.Date;

import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.KeyManager;

import javax.net.ssl.SSLContext;

import javax.net.ssl.TrustManager;

import javax.net.ssl.TrustManagerFactory;

import javax.net.ssl.KeyManager;

import javax.net.ssl.KeyManagerFactory;

import javax.xml.parsers.DocumentBuilder;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.net.BCodec;

import org.w3c.dom.Document;

import org.w3c.dom.Node;

import org.xml.sax.InputSource;



public class DirectoryService extends BCodec {

   String accountId1 = "ksingh";

   String accountId2;

   String versionOfDoc = "";

   String is106MU = "false";
 String isEPA = "false";



   public String writeFiles(String str, String isDirectoryService, String accountId) throws Exception {

      this.accountId2 = accountId;

      if (this.accountId1.equals(this.accountId2)) {

         System.out.println("::" + this.accountId2 + "_CALLING_FROM_SERVER_SIDE");

      }



      System.out.println("came");

      if (!(new File("/mnt/vs12shared/serviceshared/SurescriptsNew_Works/" + accountId)).exists()) {

         (new File("/mnt/vs12shared/serviceshared/SurescriptsNew_Works/" + accountId)).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).mkdirs();

      }



      File f;

      if (isDirectoryService.equals("true")) {

         f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_"

               + getUTCTime().replaceAll(":", "-") + ".xml");

      } else {

         f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_"

               + getUTCTime().replaceAll(":", "-") + ".xml");

      }



      FileWriter fw = null;

      FileReader Reader = null;



      DocumentBuilderFactory docBuilderFactory;

      try {

         fw = new FileWriter(f);

         fw.write(str);

         if (this.accountId1.equals(this.accountId2)) {

            System.out.println("::" + this.accountId2 + "_FILE_HAS_BEEN_WRITTEN");

         }



         if (fw != null) {

            fw.close();

            fw = null;

         }



         Document template = null;

         InputSource is2 = new InputSource();

         Reader = new FileReader(f);

         is2.setCharacterStream(Reader);

         is2.setEncoding("UTF-8");

         docBuilderFactory = DocumentBuilderFactory.newInstance();

         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

         template = docBuilder.parse(is2);

         if (Reader != null) {

            Reader.close();

         }



         Node messageNode = template.getElementsByTagName("Message").item(0);

         Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");

         if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {

            this.versionOfDoc = "10.6";

         }

      } catch (IOException var17) {

         var17.printStackTrace();

      } catch (Exception var18) {

         var18.printStackTrace();

      } finally {

         if (fw != null) {

            fw.close();

         }



         if (Reader != null) {

            Reader.close();

         }



      }



      String response = "";

      response = this.getResponse(f, isDirectoryService, is106MU);

      if (response != null && !response.equals("")) {

         File resFile;

         if (isDirectoryService.equals("true")) {

            resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

                  + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");

         } else {

            resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/StatusFile_"

                  + getUTCTime().replaceAll(":", "-") + ".xml");

         }



         FileWriter resWriter = new FileWriter(resFile);

         resWriter.write(response);

         if (resWriter != null) {

            resWriter.close();

            docBuilderFactory = null;

         }

      }



      return response;

   }



   public String writeFiles1(String str, String isDirectoryService, String accountId, String is106MU) throws Exception {

      System.out.println("\n<===============>eRx<===============>");

      File f = null;

      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).mkdirs();

      }



      String[] strFileList = str.split("!!");

      String replyMessage = "";

      FileWriter fw = null;

      FileReader Reader = null;



      for (int count = 0; count < strFileList.length; ++count) {

         InputSource resFileIS;

         DocumentBuilderFactory docBuilderFactory;

         try {

            if (isDirectoryService.equals("true")) {

               f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_"

                     + getUTCTime().replaceAll(":", "-") + ".xml");

            } else {

               f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_"

                     + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");

            }



            fw = new FileWriter(f);

            fw.write(strFileList[count]);

            if (fw != null) {

               fw.close();

            }



            Document template = null;

            resFileIS = new InputSource();

            Reader = new FileReader(f);

            resFileIS.setCharacterStream(Reader);

            resFileIS.setEncoding("UTF-8");

            docBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            template = docBuilder.parse(resFileIS);

            if (Reader != null) {

               Reader.close();

            }



            Node messageNode = template.getElementsByTagName("Message").item(0);

            Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");

            if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {

               this.versionOfDoc = "10.6";

            }

         } catch (IOException var20) {

            var20.printStackTrace();

         } catch (Exception var21) {

            var21.printStackTrace();

         } finally {

            if (fw != null) {

               fw.close();

            }



            if (Reader != null) {

               Reader.close();

            }



         }



         String response = "";

         response = this.getResponse(f, isDirectoryService, is106MU);

         replyMessage = replyMessage + response;

         if (response != null && !response.equals("")) {

            File resFile = null;

            if (isDirectoryService.equals("true")) {

               resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

                     + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");

            } else {

               resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

                     + "/Messaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");

            }



            FileWriter resWriter = new FileWriter(resFile);

            resWriter.write(response);

            if (resWriter != null) {

               resWriter.close();

               docBuilderFactory = null;

            }

         }



         replyMessage = replyMessage + "!@#!";

      }



      replyMessage = replyMessage.substring(0, replyMessage.lastIndexOf("!@#!"));

      return replyMessage;

   }
	public String writeFiles3(String xml, String isDirectoryService, String accountId) throws Exception {
	   
	   System.out.println(">>>>>>>>writeFiles3>>>>>>>");
	   //this.is106MU = "true";
	   String replyMessage = "";
	   File f = null;

	   if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).exists()) {

		   (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).mkdirs();

	   }



	   if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/EPAMessaging")).exists()) {

		   (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/EPAMessaging")).mkdirs();

	   }


	   FileWriter fw = null;
	   FileReader Reader = null;

	   try {
		   InputSource resFileIS;
		   DocumentBuilderFactory docBuilderFactory;
		   f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/EPAMessaging/MessageFile_"
				   + getUTCTime().replaceAll(":", "-") + ".xml");
		   fw = new FileWriter(f);
		   fw.write(xml);
		   if (fw != null) {

			   fw.close();

		   }

		   String response = "";

		    this.isEPA="true";
		   response= this.getResponse(f, isDirectoryService, is106MU);

		   replyMessage = response;

		   if (response != null && !response.equals("")) {

			   File resFile = null;
			   resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

					   + "/EPAMessaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
			   FileWriter resWriter = new FileWriter(resFile);
			   resWriter.write(response);
			   if (resWriter != null) {
				   resWriter.close();
				   docBuilderFactory = null;
			   }
		   }
	   }catch(Exception e) {
		   e.printStackTrace();
	   }


	   return replyMessage;
}



   public String writeFiles2(String str, String isDirectoryService, String accountId) throws Exception {

      this.is106MU = "true";

      System.out.println("\n====================eRx===============2222222");

      File f = null;

      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId)).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories")).mkdirs();

      }



      if (!(new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).exists()) {

         (new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging")).mkdirs();

      }



      String[] strFileList = str.split("!!");

      String replyMessage = "";

      FileWriter fw = null;

      FileReader Reader = null;



      for (int count = 0; count < strFileList.length; ++count) {

         InputSource resFileIS;

         DocumentBuilderFactory docBuilderFactory;

         try {

            if (isDirectoryService.equals("true")) {

               f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_"

                     + getUTCTime().replaceAll(":", "-") + ".xml");

            } else {

               f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_"

                     + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");

            }



            fw = new FileWriter(f);

            fw.write(strFileList[count]);

            if (fw != null) {

               fw.close();

            }



            Document template = null;

            resFileIS = new InputSource();

            Reader = new FileReader(f);

            resFileIS.setCharacterStream(Reader);

            resFileIS.setEncoding("UTF-8");

            docBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            template = docBuilder.parse(resFileIS);

            if (Reader != null) {

               Reader.close();

            }



            Node messageNode = template.getElementsByTagName("Message").item(0);

            Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");

            if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {

               this.versionOfDoc = "10.6";

            }

         } catch (IOException var20) {

            var20.printStackTrace();

         } catch (Exception var21) {

            var21.printStackTrace();

         } finally {

            if (fw != null) {

               fw.close();

            }



            if (Reader != null) {

               Reader.close();

            }



         }



         String response = "";

         response = this.getResponse(f, isDirectoryService, is106MU);

         replyMessage = replyMessage + response;

         if (response != null && !response.equals("")) {

            File resFile = null;

            if (isDirectoryService.equals("true")) {

               resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

                     + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");

            } else {

               resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId

                     + "/Messaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");

            }



            FileWriter resWriter = new FileWriter(resFile);

            resWriter.write(response);

            if (resWriter != null) {

               resWriter.close();

               docBuilderFactory = null;

            }

         }



         replyMessage = replyMessage + "!@#!";

      }



      replyMessage = replyMessage.substring(0, replyMessage.lastIndexOf("!@#!"));

      return replyMessage;

   }


	public String getEPAResponse(File input, String isDirectoryService, String is106mu) throws Exception {
	   

System.out.println(">>>>>>>>>>>>>>>getEPAResponse>>>>>>>>>>>>>>");
	      String strURL = "";

	      String messageId = "";

	      logStep("is transaction mu " + is106MU);



	      String ResponseBody = "";

	      HttpsURLConnection con = null;



	      try {

	         String str = null;

	         String data = "";



	         for (BufferedReader br = new BufferedReader(new FileReader(input)); (str = br.readLine()) != null; data = data

	               + str) {

	         }

	         try {

	            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()

	                  .parse(new InputSource(new StringReader(data)));

	            messageId = document.getElementsByTagName("MessageID").item(0).getChildNodes().item(0).getNodeValue();

	            logStep("performing transaction for messageID " + messageId);

	         } catch (Exception var24) {

	            logStep("error while parsing messageID: " + var24.getLocalizedMessage());

	            var24.printStackTrace();

	         }

	      //   strURL="https://smr-staging.surescripts.net/erx/Glenwood/v6_1?id="+messageId;
	         strURL="https://epa-staging.surescripts.net/epa/GlenwoodTest/EPAV2_0?id="+messageId;
System.out.println(">>>>>>>>>>strURL>>>>>>>>>>"+strURL);
	         logStep("transaction connection url " + strURL);

	         URL url = new URL(strURL);

	         con = (HttpsURLConnection) url.openConnection();

	         KeyStore trustStore = KeyStore.getInstance("JKS");

	         trustStore.load(new FileInputStream("/mnt/version/SurescriptsKeystore/surescripts.jks"),

	               "Glenwood100*".toCharArray());

	         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

	         kmf.init(trustStore, "Glenwood100*".toCharArray());

	         KeyManager[] kms = kmf.getKeyManagers();

	         SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

	         sslContext.init(kms, (TrustManager[]) null, new SecureRandom());

	         SSLContext.setDefault(sslContext);

	         con.setDoInput(true);

	         con.setDoOutput(true);

	         con.setRequestMethod("POST");

	         con.setConnectTimeout(200000);

	         con.setReadTimeout(200000);

	         con.setRequestProperty("Content-type", "text/xml");

	        if (!isDirectoryService.equals("true")) { 
		con.setRequestProperty("Authorization", this.getEncodedCredentials());
	}
	         con.setRequestProperty("Connection", "Close");

	         con.setSSLSocketFactory(sslContext.getSocketFactory());

	         OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

	         wr.write(data);

	         wr.flush();

	         wr.close();


int  responseCode = con.getResponseCode();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Post parameters : " + data);
				System.out.println("Response Code : " + responseCode);



	         StringBuffer response = new StringBuffer();

	         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));



	         String inputLine;

	         while ((inputLine = in.readLine()) != null) {

	            response.append(inputLine + "\n");

	         }



	         in.close();

	         logStep("transaction response for messageID [" + messageId + "] " + response.toString());

	         ResponseBody = response.toString();

	         if (this.accountId1.equals(this.accountId2)) {

	            logStep("::" + this.accountId2 + "_POSTING_DATA_TO_SURESCRIPT");

	         }



	         if (this.accountId1.equals(this.accountId2)) {

	            logStep("::" + this.accountId2 + "_DATA_ACCNOLEGMENT_RECEIVED_FROM_SURESCRIPT");

	         }

	      } catch (Exception var21) {

	         var21.printStackTrace();

	      } finally {

	         if (con != null) {

	            con.disconnect();

	         }



	      }



	      if (this.accountId1.equals(this.accountId2)) {

	         logStep("::" + this.accountId2 + "_BACK_TO_SERVER_SIDE");

	      }


System.out.println(">>>>>>>>ResponseBody>>>>>>>"+ResponseBody);
	      return ResponseBody;

	   

   }



   public String getResponse(File input, String isDirectoryService, String is106mu) throws Exception {

      String strURL = "";

      String messageId = "";

      logStep("is transaction mu " + is106MU);



      String ResponseBody = "";

      HttpsURLConnection con = null;



      try {

         String str = null;

         String data = "";



         for (BufferedReader br = new BufferedReader(new FileReader(input)); (str = br.readLine()) != null; data = data

               + str) {

         }

         try {

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()

                  .parse(new InputSource(new StringReader(data)));

            messageId = document.getElementsByTagName("MessageID").item(0).getChildNodes().item(0).getNodeValue();

            logStep("performing transaction for messageID " + messageId);

         } catch (Exception var24) {

            logStep("error while parsing messageID: " + var24.getLocalizedMessage());

            var24.printStackTrace();

         }



         // disabling this code due to ambiguity during url selection while moving

         // certification to production

         /*

          * if (is106MU.equals("true")) { strURL =

          * "https://smr.surescripts.net/erx/Glenwood6/v6_1?id=" + messageId; } else {

          * strURL =

          * "https://messaging.surescripts.net/Glenwood6/AuthenticatingXmlServer.aspx"; }

          */



         //strURL = "https://smr.surescripts.net/erx/Glenwood6/v6_1?id=" + messageId;
	 	    	strURL="https://smr-staging.surescripts.net/erx/Glenwood/v6_1?id="+messageId;


         if (isDirectoryService.equals("true")) {

            //strURL = "https://dir.surescripts.net/directory/Directory6dot1/v6_1?id=" + messageId;
		 strURL = "https://dir-staging.surescripts.net/directory/Directory6dot1/v6_1?id=" + messageId;
         }

System.out.println(">>>>>isEPA>>>>>>>"+isEPA);
         if (isEPA.equals("true")) {

             //strURL = "https://dir.surescripts.net/directory/Directory6dot1/v6_1?id=" + messageId;
        	 strURL="https://epa-staging.surescripts.net/epa/GlenwoodTest/EPAV2_0?id="+messageId;
          }




         logStep("transaction connection url " + strURL);

         URL url = new URL(strURL);



         con = (HttpsURLConnection) url.openConnection();

         KeyStore trustStore = KeyStore.getInstance("JKS");

         trustStore.load(new FileInputStream("/mnt/version/SurescriptsKeystore/surescripts.jks"),

               "Glenwood100*".toCharArray());

         KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

         kmf.init(trustStore, "Glenwood100*".toCharArray());

         KeyManager[] kms = kmf.getKeyManagers();

         SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

         sslContext.init(kms, (TrustManager[]) null, new SecureRandom());

         SSLContext.setDefault(sslContext);

         con.setDoInput(true);

         con.setDoOutput(true);

         con.setRequestMethod("POST");

         con.setConnectTimeout(200000);

         con.setReadTimeout(200000);

         con.setRequestProperty("Content-type", "text/xml");

        if (!isDirectoryService.equals("true")) { 
	con.setRequestProperty("Authorization", this.getEncodedCredentials());
}
         con.setRequestProperty("Connection", "Close");

         con.setSSLSocketFactory(sslContext.getSocketFactory());

         OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

         wr.write(data);

         wr.flush();

         wr.close();

 int  responseCode = con.getResponseCode();
				System.out.println("\nSending 'POST' request to URL : " + url);
				System.out.println("Post parameters : " + data);
				System.out.println("Response Code : " + responseCode);


         StringBuffer response = new StringBuffer();

         BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));



         String inputLine;

         while ((inputLine = in.readLine()) != null) {

            response.append(inputLine + "\n");

         }



         in.close();

         logStep("transaction response for messageID [" + messageId + "] " + response.toString());

         ResponseBody = response.toString();

         if (this.accountId1.equals(this.accountId2)) {

            logStep("::" + this.accountId2 + "_POSTING_DATA_TO_SURESCRIPT");

         }



         if (this.accountId1.equals(this.accountId2)) {

            logStep("::" + this.accountId2 + "_DATA_ACCNOLEGMENT_RECEIVED_FROM_SURESCRIPT");

         }

      } catch (Exception var21) {

         var21.printStackTrace();

      } finally {

         if (con != null) {

            con.disconnect();

         }



      }



      if (this.accountId1.equals(this.accountId2)) {

         logStep("::" + this.accountId2 + "_BACK_TO_SERVER_SIDE");

      }



      return ResponseBody;

   }



   private void logStep(String message) {

      System.out.println(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())

            + " [INFO] " + message);

   }



   public String insertData(String SPI, String URL, String accountId) throws Exception {

      Class.forName("org.postgresql.Driver");

      Connection con = DriverManager

            .getConnection("jdbc:postgresql://localhost/surescripts?user=postgres&password=SureScript");

      Statement st = con.createStatement();

      String qry = "Insert into identifyprescriber(spi,url,accountid) values ('" + SPI + "','" + URL + "','" + accountId

            + "')";

      int result = st.executeUpdate(qry);

      return "" + result;

   }



   public String getEncodedCredentials() {

      String returnValue = "";



      try {

         String str = "";

         if (this.is106MU.equals("true")) {

            logStep("for glenwood44! since is106MU");

            str = new String("glenwood44!:Jb8KZRN8I4cOI".getBytes(), "UTF-8");

         } else {

            logStep("for GlenwoodPROD");

            str = new String("Glenw00d67:S$crpts44".getBytes(), "UTF-8");

         }

         // str = new

         // String("glenwood:f084f59de116cbae7212a9b5656055fb014c3073329cc23962b1c2de445883bd".getBytes(),

         // "UTF-8");

         byte[] toEncode = str.getBytes();

         byte[] result = doEncoding(toEncode);

         returnValue = "Basic " + new String(result);

      } catch (UnsupportedEncodingException var5) {

         var5.printStackTrace();

      } catch (Exception e) {

         e.printStackTrace();

      }

      return returnValue;

   }



   public static String getUTCTime() {

      Date d = new Date();

      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

      cal.setTime(d);

      int hours = cal.get(11);

      int mins = cal.get(12);

      int secs = cal.get(13);

      int date = cal.get(5);

      int month = cal.get(2) + 1;

      int year = cal.get(1);

      return year + "-" + convertToDoubleDigit(month) + "-" + convertToDoubleDigit(date) + "T"

            + convertToDoubleDigit(hours) + ":" + convertToDoubleDigit(mins) + ":" + convertToDoubleDigit(secs) + ".0Z";

   }



   public static String convertToDoubleDigit(int value) {

      String returnValue = String.valueOf(value);

      if (returnValue.length() == 1) {

         returnValue = "0" + returnValue;

      }



      return returnValue;

   }

}
