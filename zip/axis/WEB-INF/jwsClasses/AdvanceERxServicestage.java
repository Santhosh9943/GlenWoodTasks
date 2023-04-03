import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.Credentials;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.commons.codec.net.BCodec;
import org.apache.commons.httpclient.Header;

import java.util.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

//import javax.activation.DataHandler;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
@SuppressWarnings("unchecked")
public class AdvanceERxService  extends BCodec{
	String server			= "192.168.10.31";			
	String database			= "formularydb";
	String user				= "glace";
	String pass				= "glacenxt";
/*	String server			= "1.1.1.111";			
	String database			= "drugdb2";
	String user				= "pcare";

	String pass				= "pcare007";*/
	public Vector getFormularyDetail(String brandName, String form, String strength, String route, String drugName, String pageNo, Vector details){
		Vector finalList=new Vector();
		try{		
 			/*String server			= "1.1.1.111";			
			String database			= "drugdb2";
			String user				= "pcare";
			String pass				= "pcare007";*/
			DatabaseConn dbUtils=null;

			dbUtils	= new DatabaseConn(server, database, user, pass);

			for(int j=0;j<details.size();j++){				
				HashMap eleDetail=(HashMap)details.get(j);
				String payerId		= "'"+eleDetail.get("payerid").toString().trim()+"'";
				String formularyId	= "'"+eleDetail.get("formid").toString().trim()+"'";
				String coverageId	= "'"+eleDetail.get("covid").toString().trim()+"'";
				String copayId		= "'"+eleDetail.get("copayid").toString().trim()+"'";
				String alternativeId= "'"+eleDetail.get("alterid").toString().trim()+"'";
				String payerName	= eleDetail.get("payername").toString().trim();
				String insId		= eleDetail.get("insid").toString().trim();
				
				String ndc	= new String();
				String drugDetail	= new String();
				Vector drugDetails	= searchDrug(brandName, form, strength, route, drugName, dbUtils, formularyId, coverageId, payerId);

				for(int i=0;i<drugDetails.size();i++){
					Hashtable<String,String> resHash=(Hashtable)drugDetails.get(i);					
					if(ndc.contains(resHash.get("ndccode").toString()))
						continue;
					if(i==0){
						ndc += "'"+resHash.get("ndccode").toString()+"'";
						//ndc += resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}							
					else if(!drugDetail.equalsIgnoreCase(resHash.get("drugdetail").toString())){
						ndc += "!@!'"+resHash.get("ndccode").toString()+"'";
						//ndc += "!@!"+resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}
					else{							
						ndc += ",'"+resHash.get("ndccode").toString()+"'";
						//ndc += ","+resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}
				}

				String ndcCode[]=ndc.split("!@!");
				for(int k=0;k<ndcCode.length;k++){
					for(int i=0;i<drugDetails.size();i++){
						Hashtable<String,String> resHash=(Hashtable)drugDetails.get(i);
						if(ndcCode[k].contains(resHash.get("ndccode").toString())){
							if(!resHash.get("covid").toString().trim().equals("")){								
								Vector alterVec=searchAlternativeDetail(alternativeId, ndcCode[k], payerId, dbUtils);
								Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);
								Hashtable finalHash=new Hashtable();
								String drugdetail[]=resHash.get("drugdetail").toString().split("!~!");
								finalHash.put("ndccode",resHash.get("ndccode").toString());
								finalHash.put("drugdetail",resHash.get("drugdetail").toString());
								finalHash.put("drugname",resHash.get("drugname").toString());
								finalHash.put("tecode",resHash.get("tecode").toString());
								finalHash.put("payerName",payerName);
								finalHash.put("insId",insId);
								finalHash.put("status", "0!~!"+eleDetail.get("payerid").toString());
								if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "B");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "B0");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "G");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "GO");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "BD");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "GD");
								finalHash.put("coverage", coverageRes);
								finalHash.put("copay", new Vector());
								finalHash.put("alternative", alterVec);
								finalList.add(finalHash);
								ndcCode[k]="";
								break;
							}						
							else if(Integer.parseInt(resHash.get("status").toString())>-2){													
								Hashtable finalHash=new Hashtable();								
								String drugdetail[]=resHash.get("drugdetail").toString().split("!~!");
								finalHash.put("ndccode",resHash.get("ndccode").toString());
								finalHash.put("drugdetail",resHash.get("drugdetail").toString());							
								finalHash.put("drugname",resHash.get("drugname").toString());
								finalHash.put("payerName",payerName);
								finalHash.put("insId",insId);
								finalHash.put("tecode",resHash.get("tecode").toString());								
								Vector alterVec=new Vector();
								Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);
								Vector copayRes=new Vector();
								if(Integer.parseInt(resHash.get("status").toString())==1 || Integer.parseInt(resHash.get("status").toString())==0){
									alterVec=searchAlternativeDetail(alternativeId, ndcCode[k], payerId, dbUtils);
								}
								if(Integer.parseInt(resHash.get("status").toString())!=0){
									copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, resHash.get("status").toString(), dbUtils);		
								}								
								finalHash.put("status", resHash.get("status").toString()+"!~!"+eleDetail.get("payerid").toString());
								if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "B");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "B0");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "G");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "GO");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "BD");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "GD");
								finalHash.put("coverage", coverageRes);
								finalHash.put("copay", copayRes);
								finalHash.put("alternative", alterVec);
								finalList.add(finalHash);
								ndcCode[k]="";
								break;								
							}
							else{
								String drugType=new String();								
								String drugdetail[]=resHash.get("drugdetail").toString().split("!~!");
								if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("RX"))
									drugType="B";
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("RX"))
									drugType="G";
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("OTC"))
									drugType="BO";
								else if(drugdetail[4].equalsIgnoreCase("G")  && drugdetail[5].equalsIgnoreCase("OTC"))
									drugType="GO";	
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("DIS"))
									drugType="BD";
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("DIS"))
									drugType="GD";							
								Vector covVector=new Vector();
								covVector=searchStatus(formularyId, ndcCode[k], payerId, dbUtils);
								boolean flag=true;
								if(covVector.size()==0){
									covVector=searchStatus(formularyId, payerId, drugdetail[4], drugdetail[5], dbUtils);
									flag=false;
								}
								Hashtable finalHash=new Hashtable();						
								finalHash.put("ndccode",resHash.get("ndccode").toString());
								finalHash.put("drugdetail",resHash.get("drugdetail").toString());							
								finalHash.put("drugname",resHash.get("drugname").toString());
								finalHash.put("payerName",payerName);
								finalHash.put("insId",insId);
								Vector alterVec=new Vector();
								Vector copayRes=new Vector();
								Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);								
								if(Integer.parseInt(resHash.get("status").toString())==1 || Integer.parseInt(resHash.get("status").toString())==0){
									alterVec=searchAlternativeDetail(alternativeId, ndcCode[k], payerId, dbUtils);
								}				
								if(covVector.size()==0){
									if(Integer.parseInt(resHash.get("status").toString())!=0){
										copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, "-1", dbUtils);				
									}
									finalHash.put("tecode",resHash.get("tecode").toString());
									finalHash.put("status", "-1!~!"+eleDetail.get("payerid").toString());
									finalHash.put("drugtype", drugType);
								}
								else{
									Hashtable excHash=(Hashtable)covVector.get(0);
									if(Integer.parseInt(resHash.get("status").toString())!=0){
										copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, excHash.get("status").toString(), dbUtils);				
									}
									finalHash.put("tecode",resHash.get("tecode").toString());
									finalHash.put("status", excHash.get("status").toString()+"!~!"+eleDetail.get("payerid").toString());
									if(flag)
										finalHash.put("drugtype", drugType);
									else
										finalHash.put("drugtype", excHash.get("drugtype").toString());
								}
								finalHash.put("coverage", coverageRes);
								finalHash.put("copay", copayRes);
								finalHash.put("alternative", alterVec);
								finalList.add(finalHash);
								ndcCode[k]="";
								break;								
							}
						}
					}
				}				
				System.out.println("Searching Completed....");
			}
			dbUtils.closeConn();				
		}catch(Exception e){
			e.printStackTrace();
		}	
		return finalList;
	}
	public Vector getFormularyDetailWithNDCCode(String NDCCode,  Vector details){
		Vector formDetailList= new Vector();
		try{
			/*String server			= "1.1.1.111";			
			String database			= "drugdb2";
			String user				= "pcare";
			String pass				= "pcare007";*/
			DatabaseConn dbUtils	= new DatabaseConn(server, database, user, pass);

			String payerId			= new String();
			String formularyId		= new String();
			String coverageId		= new String();
			String copayId			= new String();
			String alternativeId	= new String();

			for(int j=0;j<details.size();j++){				
				HashMap eleDetail=(HashMap)details.get(j);
				payerId		= "'"+eleDetail.get("payerid").toString().trim()+"'";
				formularyId	= "'"+eleDetail.get("formid").toString().trim()+"'";
				coverageId	= "'"+eleDetail.get("covid").toString().trim()+"'";
				copayId		= "'"+eleDetail.get("copayid").toString().trim()+"'";
				alternativeId="'"+eleDetail.get("alterid").toString().trim()+"'";
				Vector drugDetails		= getDrugDetails("'"+NDCCode.trim()+"'", payerId, alternativeId, dbUtils);
				for(int i=0;i<drugDetails.size();i++){
					Hashtable<String,String> resHash=(Hashtable)drugDetails.get(i);
					Hashtable formDetail=new Hashtable();

					formDetail.put("ndccode",resHash.get("ndc").toString());
					formDetail.put("rxname",resHash.get("name").toString());
					formDetail.put("drugname",resHash.get("drugname").toString());
					formDetail.put("form",resHash.get("form").toString());
					formDetail.put("route",resHash.get("route").toString());
					formDetail.put("strength",resHash.get("strength").toString());
					formDetail.put("tecode",resHash.get("tecode").toString());

					if(resHash.get("type").toString().equalsIgnoreCase("B") && resHash.get("pro_status").toString().equalsIgnoreCase("RX"))
						formDetail.put("drugtype", "B");
					else if(resHash.get("type").toString().equalsIgnoreCase("B") && resHash.get("pro_status").toString().equalsIgnoreCase("OTC"))
						formDetail.put("drugtype", "B0");
					else if(resHash.get("type").toString().equalsIgnoreCase("G") && resHash.get("pro_status").toString().equalsIgnoreCase("RX"))
						formDetail.put("drugtype", "G");
					else if(resHash.get("type").toString().equalsIgnoreCase("G") && resHash.get("pro_status").toString().equalsIgnoreCase("OTC"))
						formDetail.put("drugtype", "GO");

					Vector Status=searchStatus(formularyId, resHash.get("ndc").toString(), payerId, dbUtils);
					String forStatus="";
					if(Status.size()>0){
						Hashtable statusHash=(Hashtable)Status.get(0);						
						forStatus=statusHash.get("status").toString();
					}
					formDetail.put("status",forStatus+"!~!"+eleDetail.get("payerid").toString());
					formDetail.put("alternative",searchAlternativeDetail(alternativeId, resHash.get("ndc").toString(), payerId, dbUtils));
					formDetail.put("coverage",searchCoverageDetail(coverageId, resHash.get("ndc").toString(), payerId, dbUtils));
					formDetail.put("copay",searchCopayDetail(copayId, resHash.get("ndc").toString(), payerId, forStatus, dbUtils));

					formDetailList.add(formDetail);
					formDetail=null;
				}
			}
			System.out.println("Searching Completed");
			dbUtils.closeConn();
		}catch(Exception e){
			e.printStackTrace();
		}		
		return formDetailList;
	}
	public Vector getTherapeuticAlternativeDetails(String drugName, String form, String strength, String teCode, String ndc_Code,  Vector details){
		Vector finalList=new Vector();
		try{
			/*String server			= "1.1.1.111";			
			String database			= "drugdb2";
			String user				= "pcare";
			String pass				= "pcare007";*/
			DatabaseConn dbUtils	= new DatabaseConn(server, database, user, pass);

			for(int j=0;j<details.size();j++){				
				HashMap eleDetail=(HashMap)details.get(j);
				String payerId		= "'"+eleDetail.get("payerid").toString().trim()+"'";
				String formularyId	= "'"+eleDetail.get("formid").toString().trim()+"'";
				String coverageId	= "'"+eleDetail.get("covid").toString().trim()+"'";
				String copayId		= "'"+eleDetail.get("copayid").toString().trim()+"'";
				String alternativeId= "'"+eleDetail.get("alterid").toString().trim()+"'";
				String payername	= eleDetail.get("payername").toString().trim();
				String insId		= eleDetail.get("insid").toString().trim();
				
				Vector drugDetails		= getTherapeuticDrugDetail(drugName, form, strength, teCode, dbUtils, payerId, alternativeId, ndc_Code, coverageId, formularyId);
				String ndc=new String();
				//String drugDetail=new String();
				Hashtable drugDetail=new Hashtable();
				for(int i=0;i<drugDetails.size();i++){
					Hashtable<String,String> resHash=(Hashtable)drugDetails.get(i);
					String t=resHash.get("drugdetail").toString();
					if(ndc.contains(resHash.get("ndccode").toString()))
						continue;
					if(i==0){						
						ndc += "'"+resHash.get("ndccode").toString()+"'";
						drugDetail.put(t, 0);					
					}
					else{
						if(drugDetail.get(t)==null){
							ndc += "!@!'"+resHash.get("ndccode").toString()+"'";
							drugDetail.put(t, 0);
						}
						else {
							ndc += ",'"+resHash.get("ndccode").toString()+"'";
						}
					}
					/*if(i==0){
						ndc += "'"+resHash.get("ndccode").toString()+"'";
						//ndc += resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}							
					else if(!drugDetail.equalsIgnoreCase(resHash.get("drugdetail").toString())){
						ndc += "!@!'"+resHash.get("ndccode").toString()+"'";
						//ndc += "!@!"+resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}
					else{							
						ndc += ",'"+resHash.get("ndccode").toString()+"'";
						//ndc += ","+resHash.get("ndccode").toString();						
						drugDetail=resHash.get("drugdetail").toString();
					}*/
				}
				String ndcCode[]=ndc.split("!@!");
				for(int k=0;k<ndcCode.length;k++){
					for(int i=0;i<drugDetails.size();i++){
						Hashtable<String,String> resHash=(Hashtable)drugDetails.get(i);
						if(ndcCode[k].contains(resHash.get("ndccode").toString())){
							if(!resHash.get("covid").toString().trim().equals("")){
								ndcCode[k]="";
								break;								
							}	
							else if(Integer.parseInt(resHash.get("status").toString())>-2){
								if(Integer.parseInt(resHash.get("status").toString())>-1 && Integer.parseInt(resHash.get("status").toString())<2){
									ndcCode[k]="";
									break;	
								}
								Hashtable finalHash=new Hashtable();
								Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);
								Vector copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, resHash.get("status").toString(), dbUtils);									
								String drugdetail[]=resHash.get("drugdetail").toString().split("!~!");
								finalHash.put("ndccode",resHash.get("ndccode").toString());
								finalHash.put("rxname",drugdetail[0]);
								finalHash.put("drugname",resHash.get("drugname").toString()+"~~"+resHash.get("type").toString());
								finalHash.put("drugnameonly",resHash.get("drugname").toString());
								finalHash.put("form",drugdetail[3]);
								finalHash.put("route",drugdetail[2]);
								finalHash.put("strength",drugdetail[1]);
								finalHash.put("tecode","");
								finalHash.put("payerName",payername);
								finalHash.put("insId",insId);
								finalHash.put("status", resHash.get("status").toString()+"!~!"+eleDetail.get("payerid").toString());
								if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "B");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "B0");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("RX"))
									finalHash.put("drugtype", "G");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("OTC"))
									finalHash.put("drugtype", "GO");
								else if(drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "BD");
								else if(drugdetail[4].equalsIgnoreCase("G") && drugdetail[5].equalsIgnoreCase("DIS"))
									finalHash.put("drugtype", "GD");
								finalHash.put("coverage", coverageRes);
								finalHash.put("copay", copayRes);
								finalHash.put("alternative", new Vector());
								finalList.add(finalHash);
								ndcCode[k]="";
								break;									
							}
							else{
								String drugdetail[]=resHash.get("drugdetail").toString().split("!~!");
								Vector covVector=searchStatus(formularyId, payerId, drugdetail[4], drugdetail[5], dbUtils);
								Hashtable finalHash=new Hashtable();
								if(covVector.size()!=0){
									Hashtable excHash=(Hashtable)covVector.get(0);								
									if(Integer.parseInt(excHash.get("status").toString())<2 && Integer.parseInt(excHash.get("status").toString())>-1){
										ndcCode[k]="";
										break;									
									}
									Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);
									Vector copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, excHash.get("status").toString(), dbUtils);								
									finalHash.put("ndccode",resHash.get("ndccode").toString());
									finalHash.put("rxname",drugdetail[0]);
									finalHash.put("drugname",resHash.get("drugname").toString()+"~~"+resHash.get("type").toString());
									finalHash.put("drugnameonly",resHash.get("drugname").toString());
									finalHash.put("form",drugdetail[3]);
									finalHash.put("route",drugdetail[2]);
									finalHash.put("strength",drugdetail[1]);
									finalHash.put("tecode","");
									finalHash.put("payerName",payername);
									finalHash.put("insId",insId);
									finalHash.put("status", excHash.get("status").toString()+"!~!"+eleDetail.get("payerid").toString());
									finalHash.put("drugtype", excHash.get("drugtype").toString());
									finalHash.put("coverage", coverageRes);
									finalHash.put("copay", copayRes);
									finalHash.put("alternative", new Vector());
									finalList.add(finalHash);									
								}
								else{
									Vector coverageRes=searchCoverageDetail(coverageId, ndcCode[k], payerId, dbUtils);
									Vector copayRes=searchCopayDetail(copayId, ndcCode[k], payerId, "-1", dbUtils);								
									finalHash.put("ndccode",resHash.get("ndccode").toString());
									finalHash.put("rxname",drugdetail[0]);
									finalHash.put("drugname",resHash.get("drugname").toString()+"~~"+resHash.get("type").toString());
									finalHash.put("drugnameonly",resHash.get("drugname").toString());
									finalHash.put("form",drugdetail[3]);
									finalHash.put("route",drugdetail[2]);
									finalHash.put("strength",drugdetail[1]);
									finalHash.put("tecode","");
									finalHash.put("payerName",payername);
									finalHash.put("insId",insId);
									finalHash.put("status", "-1!~!"+eleDetail.get("payerid").toString());
									finalHash.put("drugtype", drugdetail[4]);
									finalHash.put("coverage", coverageRes);
									finalHash.put("copay", copayRes);
									finalHash.put("alternative", new Vector());
									finalList.add(finalHash);									
								}
								ndcCode[k]="";
								break;	
							}
						}
					}
				}	
			}
			//System.out.println("Searching Completed"+finalList);
			dbUtils.closeConn();
		}catch(Exception e){
			e.printStackTrace();
		}		
		return finalList;
	}
	public Vector getTherapeuticDrugDetail(String drugName, String form, String strength, String teCode, DatabaseConn dbUtils, String payerId, String alternativeId, String ndcCode, String coverageId, String formularyId)throws Exception{
		StringBuffer qryStr=new StringBuffer();
		qryStr.append(" select distinct m.ndccode, m.type, m.drugdetail, case when coalesce(k.status,'')='' then '-2' when k.status='U' then '-1'  else k.status end::integer as status, m.drugname, m.tecode, '' as covid from ( ");
		//Commented for lexi migrations
		//qryStr.append(" select 'P' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
		qryStr.append(" select 'P' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
		qryStr.append(" inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
		if(!ndcCode.equalsIgnoreCase("-1")){
			qryStr.append(" and brand_description in ( select brand_description from brandname inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS' ");
			qryStr.append(" and ndc_code::varchar(11) in ( select alternative_detail_alternative_pro_id::varchar(11) from formulary_benefit_header");
			qryStr.append(" inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
			qryStr.append(" inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
			qryStr.append(" and alternative_header_alternative_id in ("+alternativeId+") and alternative_detail_product_id in ('"+ndcCode+"')))");
		}
		qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");
		qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id");
		qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id ");
		qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
		qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
		qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id ");
		/*qryStr.append(" where 1=1 and drug_status<>'DIS' and ndc_code::varchar(11) in ( ");
		qryStr.append(" select alternative_detail_alternative_pro_id::varchar(11) from formulary_benefit_header ");
		qryStr.append(" inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+") ");
		qryStr.append(" inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id and alternative_header_alternative_id in ("+alternativeId+") and alternative_detail_product_id='"+ndcCode+"')");*/
		
		qryStr.append(" union");
		//Commented for Lexi - Migration
	//	qryStr.append(" select 'T' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
		qryStr.append(" select 'T' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
		//qryStr.append(" inner join  on ndc_code::varchar(11)=brand_tecode_ndc::varchar(11) /* and brand_tecode_te_code ='"+teCode+"' */ and drug_status<>'DIS'");
		//qryStr.append(" inner join brandname on brandname.brand_code = ndc_drug_brand_map.brand_code");
		qryStr.append(" inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
		qryStr.append(" inner join drug_relation_map on maindrugcode=drug_relation_map_code");		
		qryStr.append(" inner join drug_details on drug_relation_map_drug_id = drug_details_id and (/*drug_details_name ilike '"+drugName+"' or*/ brand_case_description in (SELECT distinct brand_case_description from ndc_drug_brand_map  inner join brandname on ndc_drug_brand_map.brand_code=brandname.brand_code and therapeutic_alternative not ilike 'Multiple Classes - functionallity will be added in next version' and therapeutic_alternative ilike '"+teCode+"'  limit 15))");
		qryStr.append(" inner join drug_form on drug_form_id = drug_relation_map_form_id /*and drug_form_name ilike '"+form+"'*/");
		qryStr.append(" inner join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id /*and drug_dosage_name ilike '"+strength+"'*/");
		qryStr.append(" inner join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id");
		qryStr.append(" inner join drug_route on drug_route_id = drug_relation_map_route_id");
		//qryStr.append(" group by brand_description,drug_details_name, drug_dosage_desc , drug_dosage_unit_name, drug_route_name , drug_form_name,ndc_code,drug_type,drug_status,brand_tecode_te_code");

		qryStr.append(" )m");
		//qryStr.append(" left join formulary_coverage_detail_de on m.ndccode::varchar(11)=coverage_detail_de_product_id::varchar(11) and coverage_detail_de_coverage_id in ("+coverageId+") ");
		//qryStr.append(" left join formulary_coverage_header on coverage_header_id=coverage_detail_de_coverage_header_id ");
		//qryStr.append(" left join formulary_benefit_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" left join (select status_detail_status as status, status_detail_product_id from formulary_benefit_header");
		qryStr.append(" inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("+payerId+") and status_header_formularyid in ("+formularyId+")");
		qryStr.append(" inner join formulary_status_detail on status_header_id=status_detail_status_header_id  )k  on k.status_detail_product_id::varchar(11) = m.ndccode::varchar(11)");
		qryStr.append(" order by m.drugdetail, status desc");
		//System.out.println("Therapeutic Alternative : -------> "+qryStr.toString());
		return dbUtils.executeQueryToVector(qryStr.toString());
	}
	public Vector getDrugDetails(String NDCCode, String payerId, String alternativeId, DatabaseConn dbUtils)throws Exception {
		StringBuffer qryStr=new StringBuffer();
		qryStr.append(" select ndc_code as ndc, brand_description as name, drug_dosage_unit_name as strength , drug_route_name as route , drug_form_name as form, drug_type as type, drug_status as pro_status, drug_details_name as drugname, tecode ");
		qryStr.append(" from ndc_drug_brand_map");
		qryStr.append(" inner join brandname on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
		if(!NDCCode.equalsIgnoreCase("-1")){
			qryStr.append(" and ndc_code in ( select alternative_detail_alternative_pro_id from formulary_benefit_header");
			qryStr.append(" inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
			qryStr.append(" inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
			qryStr.append(" and alternative_header_alternative_id in ("+alternativeId+") and alternative_detail_product_id="+NDCCode+")");
		}
		qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code");
		qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id");
		qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id");
		qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id");
		qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id");
		qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id");				
		qryStr.append(" group by brand_description,drug_details_name, drug_dosage_desc , drug_dosage_unit_name, drug_route_name , drug_form_name,ndc_code,drug_type,drug_status, tecode");
		qryStr.append(" order by drug_dosage_unit_name");
		//System.out.println("Alternative Drug: -------> "+qryStr.toString());
		Vector drugVector=dbUtils.executeQueryToVector(qryStr.toString());
		return drugVector; 
	}
	public Vector searchAlternativeDetail(String alternativeId, String NDCCode, String payerId, DatabaseConn dbUtils)throws Exception{
		if(alternativeId.equals("''"))
			return new Vector();
		StringBuffer qryStr=new StringBuffer();	
		System.out.println("Searching Alternative...");
		qryStr.append(" select distinct alternative_detail_product_id as proid, alternative_detail_alternative_pro_id as aproid, alternative_detail_preference_level as level, ndc_code as ndc, brand_description as name,drug_details_name as drugname, drug_form_name as form, drug_route_name as route, drug_dosage_unit_name as strength, benefit_header_senderid as payerid  from formulary_benefit_header");
		qryStr.append(" inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
		qryStr.append(" and alternative_header_alternative_id in ("+alternativeId+") and alternative_detail_product_id in ("+NDCCode+")");
		qryStr.append(" left join ndc_drug_brand_map on ndc_code=alternative_detail_alternative_pro_id ");
		qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");				
		qryStr.append(" left join brandname on brandname.brand_code = ndc_drug_brand_map.brand_code");
		qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id ");
		qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id ");
		qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
		qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
		qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id where coalesce(ndc_code,'')<>''");		
		qryStr.append(" order by alternative_detail_preference_level desc");
		//System.out.println("Alternative : -------> "+qryStr.toString());
		Vector alterRes=dbUtils.executeQueryToVector(qryStr.toString());		
		return alterRes;
	}
	public Vector searchCopayDetail(String copayId, String NDCCode, String payerId, String forStatus, DatabaseConn dbUtils)throws Exception{
		if(copayId.trim().equals("''"))
			return new Vector();
		StringBuffer qryStr	= new StringBuffer();
		System.out.println("Searching Copay...");
		qryStr.append(" SELECT distinct 'true' as drugspec, copay_detail_ds_pharmacy_type as phatype, copay_detail_ds_flat_amount as amount, round(case when coalesce(copay_detail_ds_precent_rate,0)=0 then copay_detail_ds_precent_rate else copay_detail_ds_precent_rate*100 end) as percent, copay_detail_ds_first_term as term, copay_detail_ds_min_copay as mincopay, copay_detail_ds_max_copay as maxcopay, copay_detail_ds_supply_days as days, copay_detail_ds_copay_tier as tier, copay_detail_ds_max_copay_tier as maxtier, benefit_header_senderid as payerid from formulary_benefit_header");
		qryStr.append(" inner join formulary_copay_header on benefit_header_id=copay_header_benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_copay_detail_ds on copay_detail_ds_copay_header_id=copay_header_id");
		qryStr.append(" and copay_detail_ds_copay_id in ("+copayId+") and copay_detail_ds_product_id in ("+NDCCode+") order by tier desc");
		//System.out.println("Copay 1: -------> "+qryStr.toString());
		Vector copayRes=dbUtils.executeQueryToVector(qryStr.toString());

		if(copayRes.size()==0){
			qryStr.setLength(0);
			qryStr.append(" SELECT distinct 'false' as drugspec, copay_detail_sl_formualry_status as fstatus, copay_detail_sl_product_type as protype, copay_detail_sl_pharmacy_type as phatype, copay_detail_sl_oop_range_start as ranstart, copay_detail_sl_oop_range_end as ranend,copay_detail_sl_flat_amount as amount, round(case when coalesce(copay_detail_sl_precent_rate,0)=0 then copay_detail_sl_precent_rate else copay_detail_sl_precent_rate*100 end)  as percent, copay_detail_sl_first_term as term, copay_detail_sl_min_copay as mincopay, copay_detail_sl_max_copay as maxcopay, copay_detail_sl_supply_days as days, copay_detail_sl_copay_tier as tier, copay_detail_sl_max_copay_tier as maxtier, benefit_header_senderid as payerid from formulary_benefit_header");
			qryStr.append(" inner join formulary_copay_header on benefit_header_id=copay_header_benefit_header_id and benefit_header_senderid in ("+payerId+")");
			qryStr.append(" inner join formulary_copay_detail_sl on copay_detail_sl_copay_header_id=copay_header_id");
			qryStr.append(" and copay_detail_sl_copay_id in ("+copayId+")");
			qryStr.append(" where copay_detail_sl_formualry_status='"+forStatus+"'");
			//System.out.println("Copay 2: -------> "+qryStr.toString());
			copayRes=dbUtils.executeQueryToVector(qryStr.toString());			
		}		
		return copayRes;
	}
	public Vector searchCoverageDetail(String coverageId, String NDCCode, String payerId, DatabaseConn dbUtils)throws Exception{
		if(coverageId.equals("''"))
			return new Vector();
		StringBuffer qryStr=new StringBuffer();
		System.out.println("Searching Coverage...");
		qryStr.append(" SELECT distinct k.drugdetail, isal as al, coverage_detail_min_age as minage, coverage_detail_product_id as ndccode, coverage_detail_min_age_qualifier as minageq, coverage_detail_max_age as maxage, coverage_detail_max_age_qualifier as maxageq,coverage_detail_gender as gender, coverage_detail_max_amount as maxamount, coverage_detail_max_amount_qualifier as maxamountq, coverage_detail_time_period as period, coverage_detail_time_period_start as periodstart,");
		qryStr.append(" coverage_detail_time_period_end as periodend, coverage_detail_time_period_units as periodunits, (select coverage_detail_rs_url from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+") inner join formulary_coverage_detail_rs on coverage_header_id=coverage_detail_rs_coverage_header_id and coverage_detail_rs_coverage_id in ("+coverageId+") limit 1)as rsurl, coverage_detail_rd_url as rdurl, coverage_detail_stepdrug_product_id as stepdrugid,coverage_detail_stepdrug_qualifier as stepdrugq,coverage_detail_stepdrug_classid as classid, coverage_detail_stepdrug_subclassid as subclassid,");
		qryStr.append(" coverage_detail_no_of_drugs_try as nooftry, coverage_detail_step_order as steporder, coverage_detail_diagnosis_code as dxcode, coverage_detail_diagnosis_code_qualifier as dxq,coalesce(coverage_detail_short_msg,'') as shortmsg, ");
		qryStr.append(" coverage_detail_long_msg as longmsg, benefit_header_senderid as payerid, ispa as pa, ismn as mn, isst as st from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_coverage_detail on coverage_header_id=coverage_detail_coverage_header_id and coverage_detail_coverage_id in ("+coverageId+")");
		qryStr.append(" and coverage_detail_product_id in ("+NDCCode+") ");
		qryStr.append(" left join (");
		qryStr.append(" select ndc_code as ndccode, trim(coalesce(brand_description,'') || ' ' || coalesce(drug_dosage_unit_name,'') || ' ' || coalesce(drug_route_name,'') || ' ' || coalesce(drug_form_name,'')) as drugdetail from brandname ");
		//Added ndc_code conditions to optmize this query
		qryStr.append(" inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS' ");
		qryStr.append(" inner join formulary_coverage_detail on coverage_detail_stepdrug_product_id=ndc_code and coverage_detail_coverage_id in ("+coverageId+") and coverage_detail_stepdrug_product_id is not null and ndc_code is not null ");
		qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");
		qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id ");
		qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
		qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
		qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id ");
		qryStr.append(" )k on k.ndccode=coverage_detail_stepdrug_product_id	order by coverage_detail_product_id ");
		//System.out.println("Coverage 1: -------> "+qryStr.toString());
		Vector coverageRes=dbUtils.executeQueryToVector(qryStr.toString());
		if(coverageRes.size()<1){
			qryStr.setLength(0);
			qryStr.append(" SELECT distinct 'summary' as summarycoverage, coverage_detail_rs_resource_ink_type as restype, coverage_detail_rs_url as url from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
			qryStr.append(" inner join formulary_coverage_detail_rs on coverage_header_id=coverage_detail_rs_coverage_header_id and coverage_detail_rs_coverage_id in ("+coverageId+")");
			//System.out.println("Coverage 2: -------> "+qryStr.toString());
			coverageRes=dbUtils.executeQueryToVector(qryStr.toString());
			return coverageRes;	
		}
		else{
			String tmpNdcCode="";
			Vector CoverageNew=new Vector();
			Hashtable tmpHash=new Hashtable();
			boolean add=false;
			boolean ageFlag=false, paFlag=false, mnFlag=false, stFlag=false;
			for(int i=0;i<coverageRes.size();i++){
				Hashtable cov=(Hashtable)coverageRes.get(i);
				String al=cov.get("al").toString();
				String pa=cov.get("pa").toString();
				String mn=cov.get("mn").toString();
				String st=cov.get("st").toString();
				if(al.equals("t"))
					ageFlag=true;
				if(pa.equals("t"))
					paFlag=true;
				if(mn.equals("t"))
					mnFlag=true;
				if(st.equals("t"))
					stFlag=true;
			}
			for(int i=0;i<coverageRes.size();i++){
				Hashtable cov=(Hashtable)coverageRes.get(i);
				String ndccode=cov.get("ndccode").toString();
				String al=cov.get("al").toString();
				String pa=cov.get("pa").toString();
				String mn=cov.get("mn").toString();
				String st=cov.get("st").toString();
				if(tmpNdcCode.equals(ndccode)){
					CoverageNew.add(tmpHash);
					CoverageNew.add(cov);
					add=true;
					break;					
				}
				else{
					tmpNdcCode=ndccode;
					if(ageFlag && cov.get("minage").toString().trim().equals(""))
						cov.put("minage", "0");
					if(pa.equals("t"))
						cov.put("pa", "t");
					if(mn.equals("t"))
						cov.put("mn", "t");
					if(st.equals("t"))
						cov.put("st", "t");
					tmpHash=cov;
				}			
			}
			if(!add)
				CoverageNew.add(tmpHash);
			return CoverageNew;
		}			
	}
	public Vector searchExclusion(String NDCCode, String payerId, String coverageId, DatabaseConn dbUtils)throws Exception{
		if(coverageId.equals("''") || NDCCode.equals(""))
			return new Vector();
		System.out.println("Searching Exclusion...");
		StringBuffer qryStr=new StringBuffer();	 
		qryStr.append(" SELECT distinct coverage_detail_de_product_id as ndccode, benefit_header_senderid as payerid from formulary_benefit_header"); 
		qryStr.append(" inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_coverage_detail_de on coverage_header_id=coverage_detail_de_coverage_header_id and coverage_detail_de_coverage_id in ("+coverageId+")"); 
		qryStr.append(" and coverage_detail_de_product_id in("+NDCCode+")");
		//System.out.println("Exclusion Search: -------> "+qryStr.toString());
		Vector exclusionVector=dbUtils.executeQueryToVector(qryStr.toString());
		return exclusionVector;
	}
	public Vector searchStatus(String formularyId, String NDCCode, String payerId, DatabaseConn dbUtils)throws Exception{
		if(formularyId.equals("''") || NDCCode.equals(""))
			return new Vector();
		System.out.println("Searching Status...");
		StringBuffer qryStr=new StringBuffer();	
		qryStr.append(" select distinct case when status_detail_status='U' then '-1' else status_detail_status end as status, status_detail_product_id as ndccode, benefit_header_senderid as payerid from formulary_benefit_header");
		qryStr.append(" inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_status_detail on status_detail_status_header_id=status_header_id");
		qryStr.append(" and status_header_formularyid in ("+formularyId+") and status_detail_product_id in("+NDCCode+") order by status desc limit 1");
		//System.out.println("Status 1: -------> "+qryStr.toString());
		Vector statusVector=dbUtils.executeQueryToVector(qryStr.toString());
		return statusVector;
	}
	public Vector searchStatus(String formularyId, String payerId, String drugType, String proStatus, DatabaseConn dbUtils)throws Exception{
		if(formularyId.equals("''"))
			return new Vector();
		System.out.println("Searching Status2...");
		StringBuffer qryStr=new StringBuffer();	
		qryStr.append(" select distinct benefit_header_senderid as payerid, ");
		if(drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("RX"))
			qryStr.append(" case when status_header_brand_formulary_status='U' then '-1' else status_header_brand_formulary_status end as status, 'B' as drugtype");
		else if(drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("RX"))
			qryStr.append(" case when status_header_generic_formulary_status='U' then '-1' else status_header_generic_formulary_status end as status, 'G' as drugtype");
		else if(drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("OTC"))
			qryStr.append(" case when status_header_brand_over_count_formulary_status='U' then '-1' else status_header_brand_over_count_formulary_status end as status, 'BO' as drugtype");
		else if(drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("OTC")) 
			qryStr.append(" case when status_header_generic_over_count_formulary_status='U' then '-1' else status_header_generic_over_count_formulary_status end as status, 'GO' as drugtype");
		else if(drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("DIS"))
			qryStr.append(" case when status_header_brand_formulary_status='U' then '-1' else status_header_brand_formulary_status end as status, 'BD' as drugtype");
		else if(drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("DIS"))
			qryStr.append(" case when status_header_generic_formulary_status='U' then '-1' else status_header_generic_formulary_status end as status, 'GD' as drugtype");
		else 
			qryStr.append(" -2 as status, '' as drugtype");
		qryStr.append(" ,status_header_cost_limit as cost_limit, benefit_header_senderid as payerid from formulary_benefit_header");
		qryStr.append(" inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id ");
		qryStr.append(" and benefit_header_senderid in ("+payerId+") and status_header_formularyid in ("+formularyId+") order by status desc limit 1");
		//System.out.println("Status 2: -------> "+qryStr.toString());
		Vector headerVector=dbUtils.executeQueryToVector(qryStr.toString());
		return headerVector;
	}
	public Vector searchDrug(String brandName, String form, String strength, String route, String drugName, DatabaseConn dbUtils, String formularyId, String coverageId, String payerId)throws Exception {
		System.out.println("Searching Drugs...");
		StringBuffer qryStr=new StringBuffer();
		qryStr.append(" select ndc_code as ndccode, case when coalesce(k.status,'')='' then '-2' when k.status='U' then '-1'  else k.status end::integer as status, coverage_detail_de_product_id as covid, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname"); 
		//Commented for Lexi -Data Migration
		//qryStr.append(" inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and (drug_status<>'DIS' or isdis=true)"); 
		qryStr.append(" inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and (drug_status<>'DIS')"); 
		if(!brandName.equalsIgnoreCase(""))
			qryStr.append(" and brand_description ilike '"+brandName.replaceAll("\'", "\'\'")+"%'");
		qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code"); 		
		qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id"); 
		qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id"); 
		qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id"); 
		qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id"); 
		qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id"); 

		qryStr.append(" left join formulary_coverage_detail_de on ndc_code::varchar(11)=coverage_detail_de_product_id::varchar(11) and coverage_detail_de_coverage_id in ("+coverageId+")");
		qryStr.append(" left join formulary_coverage_header on coverage_header_id=coverage_detail_de_coverage_header_id ");
		qryStr.append(" left join formulary_benefit_header b on coverage_header_benefit_header_id=b.benefit_header_id and b.benefit_header_senderid in ("+payerId+")");
	/*	qryStr.append(" left join (select coverage_detail_de_product_id as covid from formulary_benefit_header ");
		qryStr.append(" inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id  and benefit_header_senderid in ("+payerId+")");
		qryStr.append(" inner join formulary_coverage_detail_de on coverage_header_id=coverage_detail_de_coverage_header_id and coverage_detail_de_coverage_id in ("+coverageId+"))l on l.covid::varchar(11)=ndc_code::varchar(11)");*/

		qryStr.append(" left join (select status_detail_status as status, status_detail_product_id as proid from formulary_benefit_header ");
		qryStr.append(" inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("+payerId+") and status_header_formularyid in ("+formularyId+")");
		qryStr.append(" inner join formulary_status_detail on status_header_id=status_detail_status_header_id)k on k.proid::varchar(11)=ndc_code::varchar(11)");

		qryStr.append(" where 1=1 and ndc_code is not null");	
		if(!form.equalsIgnoreCase(""))
			qryStr.append(" and drug_form_name ilike '"+form.replaceAll("\'", "\'\'")+"%'");
		if(!strength.equalsIgnoreCase(""))
			qryStr.append(" and drug_dosage_name ilike '"+strength.replaceAll("\'", "\'\'")+"%'");
		if(!route.equalsIgnoreCase(""))
			qryStr.append(" and drug_route_name ilike '"+route.replaceAll("\'", "\'\'")+"%'");
		if(!drugName.equalsIgnoreCase(""))
			qryStr.append(" and drug_details_name ilike '"+drugName.replaceAll("\'", "\'\'")+"%'");
		//qryStr.append(" group by brand_description, drug_dosage_desc , drug_dosage_unit_name, drug_route_name ,drug_form_name, ndc_code,drug_type ,drug_status, drug_details_name, tecode ");
		qryStr.append(" order by drugdetail, status desc");
		//System.out.println("Drug Search: -------> "+qryStr.toString());
		Vector drugVector=dbUtils.executeQueryToVector(qryStr.toString());
		return drugVector; 
	}
	public String sendTransaction(String urlString , String transaction, boolean isXML) throws Exception
{
System.out.println("\n\n\n\n\n\n++++==========================================\n\n\n\n");		
OutputStream out ;
		BufferedReader in;
		HttpURLConnection con;
		String response = "";
		int BUFFER_SIZE = 100;
	//	URL url = new URL("https://switch.rxhub.net/rxhub");

		URL url = new URL("https://switch-cert01.rxhub.net/rxhub");
		con = (HttpURLConnection)url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("POST");
                con.setConnectTimeout(120000);
                con.setReadTimeout(120000);
	//	System.out.println("Request String:::::"+transaction);
		con.setRequestProperty("Content-length", String.valueOf(transaction.length()));
		con.setRequestProperty("Content-type",isXML?"text/xml":"text/plain" );
		con.setRequestProperty("Authorization",getAdvanceeRxEncodedCredentials() );

		out = con.getOutputStream();
		out.write(transaction.getBytes());
		out.flush();
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		char[] cbuf =new char[BUFFER_SIZE + 1];
		while(true){
			int numCharRead = in.read(cbuf,0,BUFFER_SIZE);
			if(numCharRead == -1){
				break;

			}
			String line = new String(cbuf,0,numCharRead)	;
			response += line;
		}
		in.close();
		out.close();
		con.disconnect();
		return stripNonValidXMLCharacters(response);	
	}

	public String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in))) return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
			if((current == 0x1d || current == 0x1f)){
				out.append('*');
			} else if (current == 0x1c ){
				out.append(':');
			} else if ( current == 0x1e ) {
				out.append('~');
			}
			else {
				out.append(current);
			}
		}
		return out.toString();
	}    
	public String getAdvanceeRxEncodedCredentials()
	{
		String returnValue="";
		try
		{
			//String str=new String("SURESCRIPTS-XHUB:56QJ9JKSE5".getBytes(),"UTF-8");
			 
			String str=new String("SURESCRIPTS-XHUB:TEITB4R26Q".getBytes(),"UTF-8");
			byte toEncode[]=str.getBytes();
			byte result[]=this.doEncoding(toEncode);
			returnValue="Basic "+new String(result);
		}catch(java.io.UnsupportedEncodingException e){e.printStackTrace();}
		catch(org.apache.commons.codec.EncoderException ee){ee.printStackTrace();}
		return returnValue;
	}
	public String attachment(String fileName, String binaryData) {

		String mtomWriteDirString = "C:\\WebUpload";
		File mtomWriteDir = new File(mtomWriteDirString);
		mtomWriteDir.mkdirs();
		File fileToWrite = new File(mtomWriteDir, fileName);

		if (!fileToWrite.exists()) {
			try {
				fileToWrite.createNewFile();
				writeFile(fileToWrite, binaryData);
				String msg = " File " + fileName + " has been successfully saved";
				//log.debug(msg);
				return msg;
			} catch (IOException e) {
				e.printStackTrace();
				String msg = "Permission is denied to write the file";
				//log.error(msg, e);
				return msg;
			}
		} else {
			try {
				writeFile(fileToWrite, binaryData);
				String msg = " File " + fileName + " has been successfully saved";
				//log.debug(msg);
				return msg;
			} catch (IOException e) {
				String msg = "Permission is denied to write the file";
				//log.error(msg, e);
				return msg;
			}
		}
	}	

	private void writeFile(File fileToWrite, String binaryData) throws IOException {
		OutputStream outStream = new FileOutputStream(fileToWrite);
		outStream.write(Base64.decode(binaryData));
		outStream.flush();
		outStream.close();
	}




	public void invoke(MessageContext msgContext) throws AxisFault 
	{

		Message reqMsg = msgContext.getRequestMessage();
		Message respMsg = msgContext.getResponseMessage();
		Attachments messageAttachments = reqMsg.getAttachmentsImpl();
		int attachmentCount= messageAttachments.getAttachmentCount();
		AttachmentPart attachments[] = new AttachmentPart[attachmentCount];
		Iterator it = messageAttachments.getAttachments().iterator();
		int count = 0;
		while (it.hasNext()) 
		{
			AttachmentPart part = (AttachmentPart) it.next();
			attachments[count++] = part;
		}		
		try
		{
			respMsg.writeTo(new FileOutputStream("c:/soaprespMesg.txt"));
			reqMsg.writeTo(new FileOutputStream("c:/soapreqMesg.txt"));		
		}
		catch(Exception e)
		{
			System.out.println("Exception in Writing Soap Message..."+ e);

		}
	}

}

