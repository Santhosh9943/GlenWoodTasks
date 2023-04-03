<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.lang.management.*,java.util.*,org.w3c.dom.NodeList,org.w3c.dom.Document,java.io.File,java.io.*,javax.xml.parsers.DocumentBuilderFactory,javax.xml.parsers.DocumentBuilder,java.util.Date" %>
<%@ page import="org.w3c.dom.Document,org.w3c.dom.Node,org.w3c.dom.NodeList,java.sql.*" %>


<%
out.flush();
 response.setContentType("text/plain"); 
int maxConnections = 60;
String sharePath = "";
String connectionString = "";

int Shared = 1;
int DB = 1;
float DBRT = 0;
int DBSP = 0;
int SHSP = 0;
int DBP = 0;
long DBFS = 0;
long SHFS = 0;
long TFS = 0;
long RFS = 0;
long VARFS = 0;
long USRFS = 0;
long FMEM = 0;
long FJVM = 0;



	/*
	shared path size
*/
	try{

	 

 /*
 Temp folder free space
 */
 try{
 File tempFolder = new File("/tmp/");
 if(tempFolder.exists()){
 	TFS = tempFolder.getUsableSpace();
 }
 }catch(Exception e){
	 System.out.println("Exception in accessing temp folder space-->"+e.getMessage());
 }
try{ 	
File rootFolder = new File("/");
 	 if(rootFolder.exists()){
 	 	RFS = rootFolder.getUsableSpace();
 	 }	 	
}catch(Exception ex){
	System.out.println("Exception in accessing root folder space-->"+ex.getMessage());
}

/*
Var folder free space
*/
try{
File tempFolder = new File("/var/");
if(tempFolder.exists()){
    VARFS = tempFolder.getUsableSpace();
}
}catch(Exception e){
    System.out.println("Exception in accessing var folder space-->"+e.getMessage());
}

/*
Usr folder free space
*/
try{
File tempFolder = new File("/usr/");
if(tempFolder.exists()){
    USRFS = tempFolder.getUsableSpace();
}
}catch(Exception e){
    System.out.println("Exception in accessing usr folder space-->"+e.getMessage());
}

try{
FJVM = Runtime.getRuntime().maxMemory();
}catch(Exception ex){
	System.out.println("Exception in obtaining jvm space-->"+ex.getMessage());
}
 
try{
		FMEM = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
	        .getOperatingSystemMXBean()).getFreePhysicalMemorySize();
		System.out.println("free space in MB"+(FMEM/1024/1024));
}catch(Exception ex){
	System.out.println("Exception in free RAM space-->"+ex.getMessage());
}

// System.out.println("final out come----"+"Shared:"+Shared+"\n"+"DB:"+DB+"\n"+"DBRT:"+0.1+"\n"+"DBSP:"+DBSP+"\n"+"SHSP:"+SHSP+"\n"+"DBP:"+DBP+"\n"+"DBFS:"+DBFS+"\n"+"SHFS:"+SHFS+"\n"+"TFS:"+TFS+"\n"+"RFS:"+RFS);
 out.print("Shared:"+Shared+"\n"+"DB:"+1+"\n"+"DBRT:"+0.1+"\n"+"DBFS:"+RFS+"\n"+"SHFS:"+RFS+"\n"+"TFS:"+TFS+"\n"+"RFS:"+RFS+"\n"+"FMEM:"+FMEM+"\n"+"FJVM:"+FJVM+"\n"+"VARFS:"+VARFS+"\n"+"USRFS:"+USRFS);

}catch(Exception ex){
	ex.printStackTrace();
}finally{
	
	
}
%>
