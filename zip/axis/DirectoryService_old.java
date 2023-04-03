import java.util.Calendar;
import java.util.TimeZone;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.net.ssl.TrustManager;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.net.URL;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.FileReader;
import org.xml.sax.InputSource;
import java.io.FileWriter;
import java.io.File;
import org.apache.commons.codec.net.BCodec;

// 
// Decompiled by Procyon v0.5.36
// 

public class DirectoryService extends BCodec
{
    String accountId1;
    String accountId2;
    String versionOfDoc;
    String is106MU;
    
    public DirectoryService() {
        this.accountId1 = "ksingh";
        this.versionOfDoc = "";
        this.is106MU = "false";
    }
    
    public String writeFiles(final String str, final String isDirectoryService, final String accountId) throws Exception {
        this.accountId2 = accountId;
        if (this.accountId1.equals(this.accountId2)) {
            System.out.println("::" + this.accountId2 + "_CALLING_FROM_SERVER_SIDE");
        }
        System.out.println("came");
        if (!new File("/mnt/vs12shared/serviceshared/SurescriptsNew_Works/" + accountId).exists()) {
            new File("/mnt/vs12shared/serviceshared/SurescriptsNew_Works/" + accountId).mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").mkdirs();
        }
        File f;
        if (isDirectoryService.equals("true")) {
            f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
        }
        else {
            f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
        }
        FileWriter fw = null;
        FileReader Reader = null;
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
            final InputSource is2 = new InputSource();
            Reader = new FileReader(f);
            is2.setCharacterStream(Reader);
            is2.setEncoding("UTF-8");
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            template = docBuilder.parse(is2);
            if (Reader != null) {
                Reader.close();
            }
            final Node messageNode = template.getElementsByTagName("Message").item(0);
            final Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");
            if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {
                this.versionOfDoc = "10.6";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        finally {
            if (fw != null) {
                fw.close();
            }
            if (Reader != null) {
                Reader.close();
            }
        }
        if (fw != null) {
            fw.close();
        }
        if (Reader != null) {
            Reader.close();
        }
        String response = "";
        response = this.getResponse(f, isDirectoryService);
        if (response != null && !response.equals("")) {
            File resFile;
            if (isDirectoryService.equals("true")) {
                resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
            }
            else {
                resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
            }
            FileWriter resWriter = new FileWriter(resFile);
            resWriter.write(response);
            if (resWriter != null) {
                resWriter.close();
                resWriter = null;
            }
        }
        return response;
    }
    
    public String writeFiles1(final String str, final String isDirectoryService, final String accountId) throws Exception {
        System.out.println("\n<===============>eRx<===============>");
        File f = null;
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId).exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId).mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").mkdirs();
        }
        final String[] strFileList = str.split("!!");
        String replyMessage = "";
        FileWriter fw = null;
        FileReader Reader = null;
        for (int count = 0; count < strFileList.length; ++count) {
            try {
                if (isDirectoryService.equals("true")) {
                    f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
                }
                else {
                    f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");
                }
                fw = new FileWriter(f);
                fw.write(strFileList[count]);
                if (fw != null) {
                    fw.close();
                }
                Document template = null;
                final InputSource is2 = new InputSource();
                Reader = new FileReader(f);
                is2.setCharacterStream(Reader);
                is2.setEncoding("UTF-8");
                final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                template = docBuilder.parse(is2);
                if (Reader != null) {
                    Reader.close();
                }
                final Node messageNode = template.getElementsByTagName("Message").item(0);
                final Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");
                if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {
                    this.versionOfDoc = "10.6";
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            finally {
                if (fw != null) {
                    fw.close();
                }
                if (Reader != null) {
                    Reader.close();
                }
            }
            if (fw != null) {
                fw.close();
            }
            if (Reader != null) {
                Reader.close();
            }
            String response = "";
            response = this.getResponse(f, isDirectoryService);
            replyMessage = String.valueOf(replyMessage) + response;
            if (response != null && !response.equals("")) {
                File resFile = null;
                if (isDirectoryService.equals("true")) {
                    resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
                }
                else {
                    resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");
                }
                FileWriter resWriter = new FileWriter(resFile);
                resWriter.write(response);
                if (resWriter != null) {
                    resWriter.close();
                    resWriter = null;
                }
            }
            replyMessage = String.valueOf(replyMessage) + "!@#!";
        }
        replyMessage = replyMessage.substring(0, replyMessage.lastIndexOf("!@#!"));
        return replyMessage;
    }
    
    public String writeFiles2(final String str, final String isDirectoryService, final String accountId) throws Exception {
        this.is106MU = "true";
        System.out.println("\n====================eRx===============2222222");
        File f = null;
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId).exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId).mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories").mkdirs();
        }
        if (!new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").exists()) {
            new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging").mkdirs();
        }
        final String[] strFileList = str.split("!!");
        String replyMessage = "";
        FileWriter fw = null;
        FileReader Reader = null;
        for (int count = 0; count < strFileList.length; ++count) {
            try {
                if (isDirectoryService.equals("true")) {
                    f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/MessageFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
                }
                else {
                    f = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/MessageFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");
                }
                fw = new FileWriter(f);
                fw.write(strFileList[count]);
                if (fw != null) {
                    fw.close();
                }
                Document template = null;
                final InputSource is2 = new InputSource();
                Reader = new FileReader(f);
                is2.setCharacterStream(Reader);
                is2.setEncoding("UTF-8");
                final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                template = docBuilder.parse(is2);
                if (Reader != null) {
                    Reader.close();
                }
                final Node messageNode = template.getElementsByTagName("Message").item(0);
                final Node versionAttrNode = messageNode.getAttributes().getNamedItem("version");
                if (versionAttrNode != null && versionAttrNode.getNodeValue().equalsIgnoreCase("010")) {
                    this.versionOfDoc = "10.6";
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
            finally {
                if (fw != null) {
                    fw.close();
                }
                if (Reader != null) {
                    Reader.close();
                }
            }
            if (fw != null) {
                fw.close();
            }
            if (Reader != null) {
                Reader.close();
            }
            String response = "";
            response = this.getResponse(f, isDirectoryService);
            replyMessage = String.valueOf(replyMessage) + response;
            if (response != null && !response.equals("")) {
                File resFile = null;
                if (isDirectoryService.equals("true")) {
                    resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Directories/StatusFile_" + getUTCTime().replaceAll(":", "-") + ".xml");
                }
                else {
                    resFile = new File("/mnt/vs12shared/serviceshared/Surescripts_Works/" + accountId + "/Messaging/StatusFile_" + getUTCTime().replaceAll(":", "-") + "_" + count + ".xml");
                }
                FileWriter resWriter = new FileWriter(resFile);
                resWriter.write(response);
                if (resWriter != null) {
                    resWriter.close();
                    resWriter = null;
                }
            }
            replyMessage = String.valueOf(replyMessage) + "!@#!";
        }
        replyMessage = replyMessage.substring(0, replyMessage.lastIndexOf("!@#!"));
        return replyMessage;
    }
    
    public String getResponse(final File input, final String isDirectoryService) throws Exception {
        String strURL = "";
        System.out.println("\n\n ismu>>>>>>>>>>>>> " + this.is106MU);
        if (this.is106MU.equals("true")) {
            strURL = "https://messaging.surescripts.net/Glenwood106MU/AuthenticatingXmlServer.aspx";
        }
        else if (isDirectoryService.equals("true")) {
            strURL = "https://messaging.surescripts.net/Glenwood4dot4/Directoryxmlserver.aspx";
        }
        else if (this.versionOfDoc.equals("10.6")) {
            strURL = "https://messaging.surescripts.net/Glenwood106x/AuthenticatingXmlServer.aspx";
        }
        else {
            strURL = "https://messaging.surescripts.net/Glenwood4x/authenticatingxmlserver.aspx";
        }
        System.out.println("\n\n\n=======================>" + strURL);
        final URL url = new URL(strURL);
        String ResponseBody = "";
        HttpsURLConnection con = null;
        Label_0608: {
            try {
                String str = null;
                String data = "";
                final BufferedReader br = new BufferedReader(new FileReader(input));
                while ((str = br.readLine()) != null) {
                    data = String.valueOf(data) + str;
                }
                con = (HttpsURLConnection)url.openConnection();
                final KeyStore trustStore = KeyStore.getInstance("JKS");
                trustStore.load(new FileInputStream("/usr/java/jdk1.8.0_65/jre/lib/security/cacerts"), "changeit".toCharArray());
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(trustStore);
                final TrustManager[] tms = tmf.getTrustManagers();
                final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, tms, new SecureRandom());
                SSLContext.setDefault(sslContext);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");
                con.setConnectTimeout(200000);
                con.setReadTimeout(200000);
                con.setRequestProperty("Content-type", "text/xml");
                con.setRequestProperty("Authorization", this.getEncodedCredentials());
                con.setRequestProperty("Connection", "Close");
                con.setSSLSocketFactory(sslContext.getSocketFactory());
                final OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(data);
                wr.flush();
                wr.close();
                final StringBuffer response = new StringBuffer();
                final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(String.valueOf(inputLine) + "\n");
                }
                in.close();
                ResponseBody = response.toString();
                if (this.accountId1.equals(this.accountId2)) {
                    System.out.println("::" + this.accountId2 + "_POSTING_DATA_TO_SURESCRIPT");
                }
                if (this.accountId1.equals(this.accountId2)) {
                    System.out.println("::" + this.accountId2 + "_DATA_ACCNOLEGMENT_RECEIVED_FROM_SURESCRIPT");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                break Label_0608;
            }
            finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            if (con != null) {
                con.disconnect();
            }
        }
        if (this.accountId1.equals(this.accountId2)) {
            System.out.println("::" + this.accountId2 + "_BACK_TO_SERVER_SIDE");
        }
        return ResponseBody;
    }
    
    public String insertData(final String SPI, final String URL, final String accountId) throws Exception {
        Class.forName("org.postgresql.Driver");
        final Connection con = DriverManager.getConnection("jdbc:postgresql://localhost/surescripts?user=postgres&password=SureScript");
        final Statement st = con.createStatement();
        final String qry = "Insert into identifyprescriber(spi,url,accountid) values ('" + SPI + "','" + URL + "','" + accountId + "')";
        final int result = st.executeUpdate(qry);
        return new StringBuilder().append(result).toString();
    }
    
    public String getEncodedCredentials() {
        String returnValue = "";
        try {
            String str = "";
            if (this.is106MU.equals("true")) {
                str = new String("glenwood44!:Jb8KZRN8I4cOI".getBytes(), "UTF-8");
            }
            else {
                str = new String("GlenwoodPROD:P90k#Hn@eB#Prod".getBytes(), "UTF-8");
            }
            final byte[] toEncode = str.getBytes();
            final byte[] result = this.doEncoding(toEncode);
            returnValue = "Basic " + new String(result);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
    
    public static String getUTCTime() {
        final Date d = new Date();
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(d);
        final int hours = cal.get(11);
        final int mins = cal.get(12);
        final int secs = cal.get(13);
        final int date = cal.get(5);
        final int month = cal.get(2) + 1;
        final int year = cal.get(1);
        return String.valueOf(year) + "-" + convertToDoubleDigit(month) + "-" + convertToDoubleDigit(date) + "T" + convertToDoubleDigit(hours) + ":" + convertToDoubleDigit(mins) + ":" + convertToDoubleDigit(secs) + ".0Z";
    }
    
    public static String convertToDoubleDigit(final int value) {
        String returnValue = String.valueOf(value);
        if (returnValue.length() == 1) {
            returnValue = "0" + returnValue;
        }
        return returnValue;
    }
}
