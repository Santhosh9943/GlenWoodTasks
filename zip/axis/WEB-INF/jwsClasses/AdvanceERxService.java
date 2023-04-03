import org.apache.axis.AxisFault;
import java.util.Iterator;
import org.apache.axis.attachments.Attachments;
import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.Base64;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import javax.net.ssl.TrustManager;
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import org.apache.commons.codec.net.BCodec;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

// 
// Decompiled by Procyon v0.5.36
// 

public class AdvanceERxService extends BCodec {
    String server;
    String database;
    String user;
    String pass;

    public AdvanceERxService() {
        this.server = "192.168.10.31";
        this.database = "formularydb";
        this.user = "glace";
        this.pass = "glacenxt";
    }

    public Vector getFormularyDetail( String brandName,  String form,  String strength,
             String route,  String drugName,  String pageNo,  Vector details) {
         Vector List = new Vector();
        try {
            DatabaseConn dbUtils = null;
            dbUtils = new DatabaseConn(this.server, this.database, this.user, this.pass);
            for (int j = 0; j < details.size(); ++j) {
                 HashMap eleDetail = (HashMap) details.get(j);
                 String payerId = "'" + eleDetail.get("payerid").toString().trim() + "'";
                 String formularyId = "'" + eleDetail.get("formid").toString().trim() + "'";
                 String coverageId = "'" + eleDetail.get("covid").toString().trim() + "'";
                 String copayId = "'" + eleDetail.get("copayid").toString().trim() + "'";
                 String alternativeId = "'" + eleDetail.get("alterid").toString().trim() + "'";
                 String payerName = eleDetail.get("payername").toString().trim();
                 String insId = eleDetail.get("insid").toString().trim();
                String ndc = new String();
                String drugDetail = new String();
                 Vector drugDetails = this.searchDrug(brandName, form, strength, route, drugName, dbUtils,
                        formularyId, coverageId, payerId);
                for (int i = 0; i < drugDetails.size(); ++i) {
                     Hashtable<String, String> resHash = (Hashtable<String, String>) drugDetails.get(i);
                    if (!ndc.contains(resHash.get("ndccode").toString())) {
                        if (i == 0) {
                            ndc = String.valueOf(ndc) + "'" + resHash.get("ndccode").toString() + "'";
                            drugDetail = resHash.get("drugdetail").toString();
                        } else if (!drugDetail.equalsIgnoreCase(resHash.get("drugdetail").toString())) {
                            ndc = String.valueOf(ndc) + "!@!'" + resHash.get("ndccode").toString() + "'";
                            drugDetail = resHash.get("drugdetail").toString();
                        } else {
                            ndc = String.valueOf(ndc) + ",'" + resHash.get("ndccode").toString() + "'";
                            drugDetail = resHash.get("drugdetail").toString();
                        }
                    }
                }
                 String[] ndcCode = ndc.split("!@!");
                for (int k = 0; k < ndcCode.length; ++k) {
                    int l = 0;
                    while (l < drugDetails.size()) {
                         Hashtable<String, String> resHash2 = (Hashtable<String, String>) drugDetails.get(l);
                        if (ndcCode[k].contains(resHash2.get("ndccode").toString())) {
                            if (!resHash2.get("covid").toString().trim().equals("")) {
                                 Vector alterVec = this.searchAlternativeDetail(alternativeId, ndcCode[k], payerId,
                                        dbUtils);
                                 Vector coverageRes = this.searchCoverageDetail(coverageId, ndcCode[k], payerId,
                                        dbUtils);
                                 Hashtable Hash = new Hashtable();
                                 String[] drugdetail = resHash2.get("drugdetail").toString().split("!~!");
                                Hash.put("ndccode", resHash2.get("ndccode").toString());
                                Hash.put("drugdetail", resHash2.get("drugdetail").toString());
                                Hash.put("drugname", resHash2.get("drugname").toString());
                                Hash.put("tecode", resHash2.get("tecode").toString());
                                Hash.put("payerName", payerName);
                                Hash.put("insId", insId);
                                Hash.put("status", "0!~!" + eleDetail.get("payerid").toString());
                                if (drugdetail[4].equalsIgnoreCase("B") && drugdetail[5].equalsIgnoreCase("RX")) {
                                    Hash.put("drugtype", "B");
                                } else if (drugdetail[4].equalsIgnoreCase("B")
                                        && drugdetail[5].equalsIgnoreCase("OTC")) {
                                    Hash.put("drugtype", "B0");
                                } else if (drugdetail[4].equalsIgnoreCase("G")
                                        && drugdetail[5].equalsIgnoreCase("RX")) {
                                    Hash.put("drugtype", "G");
                                } else if (drugdetail[4].equalsIgnoreCase("G")
                                        && drugdetail[5].equalsIgnoreCase("OTC")) {
                                    Hash.put("drugtype", "GO");
                                } else if (drugdetail[4].equalsIgnoreCase("B")
                                        && drugdetail[5].equalsIgnoreCase("DIS")) {
                                    Hash.put("drugtype", "BD");
                                } else if (drugdetail[4].equalsIgnoreCase("G")
                                        && drugdetail[5].equalsIgnoreCase("DIS")) {
                                    Hash.put("drugtype", "GD");
                                }
                                Hash.put("coverage", coverageRes);
                                Hash.put("copay", new Vector());
                                Hash.put("alternative", alterVec);
                                List.add(Hash);
                                ndcCode[k] = "";
                                break;
                            }
                            if (Integer.parseInt(resHash2.get("status").toString()) > -2) {
                                 Hashtable Hash2 = new Hashtable();
                                 String[] drugdetail2 = resHash2.get("drugdetail").toString().split("!~!");
                                Hash2.put("ndccode", resHash2.get("ndccode").toString());
                                Hash2.put("drugdetail", resHash2.get("drugdetail").toString());
                                Hash2.put("drugname", resHash2.get("drugname").toString());
                                Hash2.put("payerName", payerName);
                                Hash2.put("insId", insId);
                                Hash2.put("tecode", resHash2.get("tecode").toString());
                                Vector alterVec2 = new Vector();
                                 Vector coverageRes2 = this.searchCoverageDetail(coverageId, ndcCode[k], payerId,
                                        dbUtils);
                                Vector copayRes = new Vector();
                                if (Integer.parseInt(resHash2.get("status").toString()) == 1
                                        || Integer.parseInt(resHash2.get("status").toString()) == 0) {
                                    alterVec2 = this.searchAlternativeDetail(alternativeId, ndcCode[k], payerId,
                                            dbUtils);
                                }
                                if (Integer.parseInt(resHash2.get("status").toString()) != 0) {
                                    copayRes = this.searchCopayDetail(copayId, ndcCode[k], payerId,
                                            resHash2.get("status").toString(), dbUtils);
                                }
                                Hash2.put("status", String.valueOf(resHash2.get("status").toString()) + "!~!"
                                        + eleDetail.get("payerid").toString());
                                if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("RX")) {
                                    Hash2.put("drugtype", "B");
                                } else if (drugdetail2[4].equalsIgnoreCase("B")
                                        && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                    Hash2.put("drugtype", "B0");
                                } else if (drugdetail2[4].equalsIgnoreCase("G")
                                        && drugdetail2[5].equalsIgnoreCase("RX")) {
                                    Hash2.put("drugtype", "G");
                                } else if (drugdetail2[4].equalsIgnoreCase("G")
                                        && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                    Hash2.put("drugtype", "GO");
                                } else if (drugdetail2[4].equalsIgnoreCase("B")
                                        && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                    Hash2.put("drugtype", "BD");
                                } else if (drugdetail2[4].equalsIgnoreCase("G")
                                        && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                    Hash2.put("drugtype", "GD");
                                }
                                Hash2.put("coverage", coverageRes2);
                                Hash2.put("copay", copayRes);
                                Hash2.put("alternative", alterVec2);
                                List.add(Hash2);
                                ndcCode[k] = "";
                                break;
                            }
                            String drugType = new String();
                             String[] drugdetail2 = resHash2.get("drugdetail").toString().split("!~!");
                            if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("RX")) {
                                drugType = "B";
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("RX")) {
                                drugType = "G";
                            } else if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                drugType = "BO";
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                drugType = "GO";
                            } else if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                drugType = "BD";
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                drugType = "GD";
                            }
                            Vector covVector = new Vector();
                            covVector = this.searchStatus(formularyId, ndcCode[k], payerId, dbUtils);
                            boolean flag = true;
                            if (covVector.size() == 0) {
                                covVector = this.searchStatus(formularyId, payerId, drugdetail2[4], drugdetail2[5],
                                        dbUtils);
                                flag = false;
                            }
                             Hashtable Hash3 = new Hashtable();
                            Hash3.put("ndccode", resHash2.get("ndccode").toString());
                            Hash3.put("drugdetail", resHash2.get("drugdetail").toString());
                            Hash3.put("drugname", resHash2.get("drugname").toString());
                            Hash3.put("payerName", payerName);
                            Hash3.put("insId", insId);
                            Vector alterVec3 = new Vector();
                            Vector copayRes2 = new Vector();
                             Vector coverageRes3 = this.searchCoverageDetail(coverageId, ndcCode[k], payerId,
                                    dbUtils);
                            if (Integer.parseInt(resHash2.get("status").toString()) == 1
                                    || Integer.parseInt(resHash2.get("status").toString()) == 0) {
                                alterVec3 = this.searchAlternativeDetail(alternativeId, ndcCode[k], payerId, dbUtils);
                            }
                            if (covVector.size() == 0) {
                                if (Integer.parseInt(resHash2.get("status").toString()) != 0) {
                                    copayRes2 = this.searchCopayDetail(copayId, ndcCode[k], payerId, "-1", dbUtils);
                                }
                                Hash3.put("tecode", resHash2.get("tecode").toString());
                                Hash3.put("status", "-1!~!" + eleDetail.get("payerid").toString());
                                Hash3.put("drugtype", drugType);
                            } else {
                                 Hashtable excHash = (Hashtable) covVector.get(0);
                                if (Integer.parseInt(resHash2.get("status").toString()) != 0) {
                                    copayRes2 = this.searchCopayDetail(copayId, ndcCode[k], payerId,
                                            excHash.get("status").toString(), dbUtils);
                                }
                                Hash3.put("tecode", resHash2.get("tecode").toString());
                                Hash3.put("status", String.valueOf(excHash.get("status").toString()) + "!~!"
                                        + eleDetail.get("payerid").toString());
                                if (flag) {
                                    Hash3.put("drugtype", drugType);
                                } else {
                                    Hash3.put("drugtype", excHash.get("drugtype").toString());
                                }
                            }
                            Hash3.put("coverage", coverageRes3);
                            Hash3.put("copay", copayRes2);
                            Hash3.put("alternative", alterVec3);
                            List.add(Hash3);
                            ndcCode[k] = "";
                            break;
                        } else {
                            ++l;
                        }
                    }
                }
                System.out.println("Searching Completed....");
            }
            dbUtils.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List;
    }

    public Vector getFormularyDetailWithNDCCode( String NDCCode,  Vector details) {
         Vector formDetailList = new Vector();
        try {
             DatabaseConn dbUtils = new DatabaseConn(this.server, this.database, this.user, this.pass);
            String payerId = new String();
            String formularyId = new String();
            String coverageId = new String();
            String copayId = new String();
            String alternativeId = new String();
            for (int j = 0; j < details.size(); ++j) {
                 HashMap eleDetail = (HashMap) details.get(j);
                payerId = "'" + eleDetail.get("payerid").toString().trim() + "'";
                formularyId = "'" + eleDetail.get("formid").toString().trim() + "'";
                coverageId = "'" + eleDetail.get("covid").toString().trim() + "'";
                copayId = "'" + eleDetail.get("copayid").toString().trim() + "'";
                alternativeId = "'" + eleDetail.get("alterid").toString().trim() + "'";
                 Vector drugDetails = this.getDrugDetails("'" + NDCCode.trim() + "'", payerId, alternativeId,
                        dbUtils);
                for (int i = 0; i < drugDetails.size(); ++i) {
                     Hashtable<String, String> resHash = (Hashtable<String, String>) drugDetails.get(i);
                    Hashtable formDetail = new Hashtable();
                    formDetail.put("ndccode", resHash.get("ndc").toString());
                    formDetail.put("rxname", resHash.get("name").toString());
                    formDetail.put("drugname", resHash.get("drugname").toString());
                    formDetail.put("form", resHash.get("form").toString());
                    formDetail.put("route", resHash.get("route").toString());
                    formDetail.put("strength", resHash.get("strength").toString());
                    formDetail.put("tecode", resHash.get("tecode").toString());
                    if (resHash.get("type").toString().equalsIgnoreCase("B")
                            && resHash.get("pro_status").toString().equalsIgnoreCase("RX")) {
                        formDetail.put("drugtype", "B");
                    } else if (resHash.get("type").toString().equalsIgnoreCase("B")
                            && resHash.get("pro_status").toString().equalsIgnoreCase("OTC")) {
                        formDetail.put("drugtype", "B0");
                    } else if (resHash.get("type").toString().equalsIgnoreCase("G")
                            && resHash.get("pro_status").toString().equalsIgnoreCase("RX")) {
                        formDetail.put("drugtype", "G");
                    } else if (resHash.get("type").toString().equalsIgnoreCase("G")
                            && resHash.get("pro_status").toString().equalsIgnoreCase("OTC")) {
                        formDetail.put("drugtype", "GO");
                    }
                     Vector Status = this.searchStatus(formularyId, resHash.get("ndc").toString(), payerId,
                            dbUtils);
                    String forStatus = "";
                    if (Status.size() > 0) {
                         Hashtable statusHash = (Hashtable) Status.get(0);
                        forStatus = statusHash.get("status").toString();
                    }
                    formDetail.put("status", String.valueOf(forStatus) + "!~!" + eleDetail.get("payerid").toString());
                    formDetail.put("alternative", this.searchAlternativeDetail(alternativeId,
                            resHash.get("ndc").toString(), payerId, dbUtils));
                    formDetail.put("coverage",
                            this.searchCoverageDetail(coverageId, resHash.get("ndc").toString(), payerId, dbUtils));
                    formDetail.put("copay", this.searchCopayDetail(copayId, resHash.get("ndc").toString(), payerId,
                            forStatus, dbUtils));
                    formDetailList.add(formDetail);
                    formDetail = null;
                }
            }
            System.out.println("Searching Completed");
            dbUtils.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formDetailList;
    }

    public Vector getTherapeuticAlternativeDetails( String drugName,  String form,  String strength,
             String teCode,  String ndc_Code,  Vector details) {
         Vector List = new Vector();
        try {
             DatabaseConn dbUtils = new DatabaseConn(this.server, this.database, this.user, this.pass);
            for (int j = 0; j < details.size(); ++j) {
                 HashMap eleDetail = (HashMap) details.get(j);
                 String payerId = "'" + eleDetail.get("payerid").toString().trim() + "'";
                 String formularyId = "'" + eleDetail.get("formid").toString().trim() + "'";
                 String coverageId = "'" + eleDetail.get("covid").toString().trim() + "'";
                 String copayId = "'" + eleDetail.get("copayid").toString().trim() + "'";
                 String alternativeId = "'" + eleDetail.get("alterid").toString().trim() + "'";
                 String payername = eleDetail.get("payername").toString().trim();
                 String insId = eleDetail.get("insid").toString().trim();
                 Vector drugDetails = this.getTherapeuticDrugDetail(drugName, form, strength, teCode, dbUtils,
                        payerId, alternativeId, ndc_Code, coverageId, formularyId);
                String ndc = new String();
                 Hashtable drugDetail = new Hashtable();
                for (int i = 0; i < drugDetails.size(); ++i) {
                     Hashtable<String, String> resHash = (Hashtable<String, String>) drugDetails.get(i);
                     String t = resHash.get("drugdetail").toString();
                    if (!ndc.contains(resHash.get("ndccode").toString())) {
                        if (i == 0) {
                            ndc = String.valueOf(ndc) + "'" + resHash.get("ndccode").toString() + "'";
                            drugDetail.put(t, 0);
                        } else if (drugDetail.get(t) == null) {
                            ndc = String.valueOf(ndc) + "!@!'" + resHash.get("ndccode").toString() + "'";
                            drugDetail.put(t, 0);
                        } else {
                            ndc = String.valueOf(ndc) + ",'" + resHash.get("ndccode").toString() + "'";
                        }
                    }
                }
                 String[] ndcCode = ndc.split("!@!");
                for (int k = 0; k < ndcCode.length; ++k) {
                    int l = 0;
                    while (l < drugDetails.size()) {
                         Hashtable<String, String> resHash2 = (Hashtable<String, String>) drugDetails.get(l);
                        if (ndcCode[k].contains(resHash2.get("ndccode").toString())) {
                            if (!resHash2.get("covid").toString().trim().equals("")) {
                                ndcCode[k] = "";
                                break;
                            }
                            if (Integer.parseInt(resHash2.get("status").toString()) <= -2) {
                                 String[] drugdetail = resHash2.get("drugdetail").toString().split("!~!");
                                 Vector covVector = this.searchStatus(formularyId, payerId, drugdetail[4],
                                        drugdetail[5], dbUtils);
                                 Hashtable Hash = new Hashtable();
                                if (covVector.size() != 0) {
                                     Hashtable excHash = (Hashtable) covVector.get(0);
                                    if (Integer.parseInt(excHash.get("status").toString()) < 2
                                            && Integer.parseInt(excHash.get("status").toString()) > -1) {
                                        ndcCode[k] = "";
                                        break;
                                    }
                                     Vector coverageRes = this.searchCoverageDetail(coverageId, ndcCode[k],
                                            payerId, dbUtils);
                                     Vector copayRes = this.searchCopayDetail(copayId, ndcCode[k], payerId,
                                            excHash.get("status").toString(), dbUtils);
                                    Hash.put("ndccode", resHash2.get("ndccode").toString());
                                    Hash.put("rxname", drugdetail[0]);
                                    Hash.put("drugname", String.valueOf(resHash2.get("drugname").toString()) + "~~"
                                            + resHash2.get("type").toString());
                                    Hash.put("drugnameonly", resHash2.get("drugname").toString());
                                    Hash.put("form", drugdetail[3]);
                                    Hash.put("route", drugdetail[2]);
                                    Hash.put("strength", drugdetail[1]);
                                    Hash.put("tecode", "");
                                    Hash.put("payerName", payername);
                                    Hash.put("insId", insId);
                                    Hash.put("status", String.valueOf(excHash.get("status").toString()) + "!~!"
                                            + eleDetail.get("payerid").toString());
                                    Hash.put("drugtype", excHash.get("drugtype").toString());
                                    Hash.put("coverage", coverageRes);
                                    Hash.put("copay", copayRes);
                                    Hash.put("alternative", new Vector());
                                    List.add(Hash);
                                } else {
                                     Vector coverageRes2 = this.searchCoverageDetail(coverageId, ndcCode[k],
                                            payerId, dbUtils);
                                     Vector copayRes2 = this.searchCopayDetail(copayId, ndcCode[k], payerId, "-1",
                                            dbUtils);
                                    Hash.put("ndccode", resHash2.get("ndccode").toString());
                                    Hash.put("rxname", drugdetail[0]);
                                    Hash.put("drugname", String.valueOf(resHash2.get("drugname").toString()) + "~~"
                                            + resHash2.get("type").toString());
                                    Hash.put("drugnameonly", resHash2.get("drugname").toString());
                                    Hash.put("form", drugdetail[3]);
                                    Hash.put("route", drugdetail[2]);
                                    Hash.put("strength", drugdetail[1]);
                                    Hash.put("tecode", "");
                                    Hash.put("payerName", payername);
                                    Hash.put("insId", insId);
                                    Hash.put("status", "-1!~!" + eleDetail.get("payerid").toString());
                                    Hash.put("drugtype", drugdetail[4]);
                                    Hash.put("coverage", coverageRes2);
                                    Hash.put("copay", copayRes2);
                                    Hash.put("alternative", new Vector());
                                    List.add(Hash);
                                }
                                ndcCode[k] = "";
                                break;
                            }
                            if (Integer.parseInt(resHash2.get("status").toString()) > -1
                                    && Integer.parseInt(resHash2.get("status").toString()) < 2) {
                                ndcCode[k] = "";
                                break;
                            }
                             Hashtable Hash2 = new Hashtable();
                             Vector coverageRes3 = this.searchCoverageDetail(coverageId, ndcCode[k], payerId,
                                    dbUtils);
                             Vector copayRes3 = this.searchCopayDetail(copayId, ndcCode[k], payerId,
                                    resHash2.get("status").toString(), dbUtils);
                             String[] drugdetail2 = resHash2.get("drugdetail").toString().split("!~!");
                            Hash2.put("ndccode", resHash2.get("ndccode").toString());
                            Hash2.put("rxname", drugdetail2[0]);
                            Hash2.put("drugname", String.valueOf(resHash2.get("drugname").toString()) + "~~"
                                    + resHash2.get("type").toString());
                            Hash2.put("drugnameonly", resHash2.get("drugname").toString());
                            Hash2.put("form", drugdetail2[3]);
                            Hash2.put("route", drugdetail2[2]);
                            Hash2.put("strength", drugdetail2[1]);
                            Hash2.put("tecode", "");
                            Hash2.put("payerName", payername);
                            Hash2.put("insId", insId);
                            Hash2.put("status", String.valueOf(resHash2.get("status").toString()) + "!~!"
                                    + eleDetail.get("payerid").toString());
                            if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("RX")) {
                                Hash2.put("drugtype", "B");
                            } else if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                Hash2.put("drugtype", "B0");
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("RX")) {
                                Hash2.put("drugtype", "G");
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("OTC")) {
                                Hash2.put("drugtype", "GO");
                            } else if (drugdetail2[4].equalsIgnoreCase("B") && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                Hash2.put("drugtype", "BD");
                            } else if (drugdetail2[4].equalsIgnoreCase("G") && drugdetail2[5].equalsIgnoreCase("DIS")) {
                                Hash2.put("drugtype", "GD");
                            }
                            Hash2.put("coverage", coverageRes3);
                            Hash2.put("copay", copayRes3);
                            Hash2.put("alternative", new Vector());
                            List.add(Hash2);
                            ndcCode[k] = "";
                            break;
                        } else {
                            ++l;
                        }
                    }
                }
            }
            dbUtils.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List;
    }

    public Vector getTherapeuticDrugDetail( String drugName,  String form,  String strength,
             String teCode,  DatabaseConn dbUtils,  String payerId,  String alternativeId,
             String ndcCode,  String coverageId,  String formularyId) throws Exception {
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(
                " select distinct m.ndccode, m.type, m.drugdetail, case when coalesce(k.status,'')='' then '-2' when k.status='U' then '-1'  else k.status end::integer as status, m.drugname, m.tecode, '' as covid from ( ");
        qryStr.append(
                " select 'P' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
        qryStr.append(
                " inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
        if (!ndcCode.equalsIgnoreCase("-1")) {
            qryStr.append(
                    " and brand_description in ( select brand_description from brandname inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS' ");
            qryStr.append(
                    " and ndc_code::varchar(11) in ( select alternative_detail_alternative_pro_id::varchar(11) from formulary_benefit_header");
            qryStr.append(
                    " inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                            + payerId + ")");
            qryStr.append(
                    " inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
            qryStr.append(" and alternative_header_alternative_id in (" + alternativeId
                    + ") and alternative_detail_product_id in ('" + ndcCode + "')))");
        }
        qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");
        qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id");
        qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id ");
        qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
        qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
        qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id ");
        qryStr.append(" union");
        qryStr.append(
                " select 'T' as type, ndc_code as ndccode, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
        qryStr.append(
                " inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
        qryStr.append(" inner join drug_relation_map on maindrugcode=drug_relation_map_code");
        qryStr.append(
                " inner join drug_details on drug_relation_map_drug_id = drug_details_id and (/*drug_details_name ilike '"
                        + drugName
                        + "' or*/ brand_case_description in (SELECT distinct brand_case_description from ndc_drug_brand_map  inner join brandname on ndc_drug_brand_map.brand_code=brandname.brand_code and therapeutic_alternative not ilike 'Multiple Classes - functionallity will be added in next version' and therapeutic_alternative ilike '"
                        + teCode + "'  limit 15))");
        qryStr.append(" inner join drug_form on drug_form_id = drug_relation_map_form_id /*and drug_form_name ilike '"
                + form + "'*/");
        qryStr.append(
                " inner join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id /*and drug_dosage_name ilike '"
                        + strength + "'*/");
        qryStr.append(" inner join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id");
        qryStr.append(" inner join drug_route on drug_route_id = drug_relation_map_route_id");
        qryStr.append(" )m");
        qryStr.append(
                " left join (select status_detail_status as status, status_detail_product_id from formulary_benefit_header");
        qryStr.append(
                " inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("
                        + payerId + ") and status_header_formularyid in (" + formularyId + ")");
        qryStr.append(
                " inner join formulary_status_detail on status_header_id=status_detail_status_header_id  )k  on k.status_detail_product_id::varchar(11) = m.ndccode::varchar(11)");
        qryStr.append(" order by m.drugdetail, status desc");
        return dbUtils.executeQueryToVector(qryStr.toString());
    }

    public Vector getDrugDetails( String NDCCode,  String payerId,  String alternativeId,
             DatabaseConn dbUtils) throws Exception {
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(
                " select ndc_code as ndc, brand_description as name, drug_dosage_unit_name as strength , drug_route_name as route , drug_form_name as form, drug_type as type, drug_status as pro_status, drug_details_name as drugname, tecode ");
        qryStr.append(" from ndc_drug_brand_map");
        qryStr.append(
                " inner join brandname on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS'");
        if (!NDCCode.equalsIgnoreCase("-1")) {
            qryStr.append(
                    " and ndc_code in ( select alternative_detail_alternative_pro_id from formulary_benefit_header");
            qryStr.append(
                    " inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                            + payerId + ")");
            qryStr.append(
                    " inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
            qryStr.append(" and alternative_header_alternative_id in (" + alternativeId
                    + ") and alternative_detail_product_id=" + NDCCode + ")");
        }
        qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code");
        qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id");
        qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id");
        qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id");
        qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id");
        qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id");
        qryStr.append(
                " group by brand_description,drug_details_name, drug_dosage_desc , drug_dosage_unit_name, drug_route_name , drug_form_name,ndc_code,drug_type,drug_status, tecode");
        qryStr.append(" order by drug_dosage_unit_name");
         Vector drugVector = dbUtils.executeQueryToVector(qryStr.toString());
        return drugVector;
    }

    public Vector searchAlternativeDetail( String alternativeId,  String NDCCode,  String payerId,
             DatabaseConn dbUtils) throws Exception {
        if (alternativeId.equals("''")) {
            return new Vector();
        }
         StringBuffer qryStr = new StringBuffer();
        System.out.println("Searching Alternative...");
        qryStr.append(
                " select distinct alternative_detail_product_id as proid, alternative_detail_alternative_pro_id as aproid, alternative_detail_preference_level as level, ndc_code as ndc, brand_description as name,drug_details_name as drugname, drug_form_name as form, drug_route_name as route, drug_dosage_unit_name as strength, benefit_header_senderid as payerid  from formulary_benefit_header");
        qryStr.append(
                " inner join formulary_alternative_header on alternative_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(
                " inner join formulary_alternative_detail on alternative_detail_alternative_header_id=alternative_header_id");
        qryStr.append(" and alternative_header_alternative_id in (" + alternativeId
                + ") and alternative_detail_product_id in (" + NDCCode + ")");
        qryStr.append(" left join ndc_drug_brand_map on ndc_code=alternative_detail_alternative_pro_id ");
        qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");
        qryStr.append(" left join brandname on brandname.brand_code = ndc_drug_brand_map.brand_code");
        qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id ");
        qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id ");
        qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
        qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
        qryStr.append(
                " left join drug_route on drug_route_id = drug_relation_map_route_id where coalesce(ndc_code,'')<>''");
        qryStr.append(" order by alternative_detail_preference_level desc");
         Vector alterRes = dbUtils.executeQueryToVector(qryStr.toString());
        return alterRes;
    }

    public Vector searchCopayDetail( String copayId,  String NDCCode,  String payerId,
             String forStatus,  DatabaseConn dbUtils) throws Exception {
        if (copayId.trim().equals("''")) {
            return new Vector();
        }
         StringBuffer qryStr = new StringBuffer();
        System.out.println("Searching Copay...");
        qryStr.append(
                " SELECT distinct 'true' as drugspec, copay_detail_ds_pharmacy_type as phatype, copay_detail_ds_flat_amount as amount, round(case when coalesce(copay_detail_ds_precent_rate,0)=0 then copay_detail_ds_precent_rate else copay_detail_ds_precent_rate*100 end) as percent, copay_detail_ds_first_term as term, copay_detail_ds_min_copay as mincopay, copay_detail_ds_max_copay as maxcopay, copay_detail_ds_supply_days as days, copay_detail_ds_copay_tier as tier, copay_detail_ds_max_copay_tier as maxtier, benefit_header_senderid as payerid from formulary_benefit_header");
        qryStr.append(
                " inner join formulary_copay_header on benefit_header_id=copay_header_benefit_header_id and benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(" inner join formulary_copay_detail_ds on copay_detail_ds_copay_header_id=copay_header_id");
        qryStr.append(" and copay_detail_ds_copay_id in (" + copayId + ") and copay_detail_ds_product_id in (" + NDCCode
                + ") order by tier desc");
        Vector copayRes = dbUtils.executeQueryToVector(qryStr.toString());
        if (copayRes.size() == 0) {
            qryStr.setLength(0);
            qryStr.append(
                    " SELECT distinct 'false' as drugspec, copay_detail_sl_formualry_status as fstatus, copay_detail_sl_product_type as protype, copay_detail_sl_pharmacy_type as phatype, copay_detail_sl_oop_range_start as ranstart, copay_detail_sl_oop_range_end as ranend,copay_detail_sl_flat_amount as amount, round(case when coalesce(copay_detail_sl_precent_rate,0)=0 then copay_detail_sl_precent_rate else copay_detail_sl_precent_rate*100 end)  as percent, copay_detail_sl_first_term as term, copay_detail_sl_min_copay as mincopay, copay_detail_sl_max_copay as maxcopay, copay_detail_sl_supply_days as days, copay_detail_sl_copay_tier as tier, copay_detail_sl_max_copay_tier as maxtier, benefit_header_senderid as payerid from formulary_benefit_header");
            qryStr.append(
                    " inner join formulary_copay_header on benefit_header_id=copay_header_benefit_header_id and benefit_header_senderid in ("
                            + payerId + ")");
            qryStr.append(" inner join formulary_copay_detail_sl on copay_detail_sl_copay_header_id=copay_header_id");
            qryStr.append(" and copay_detail_sl_copay_id in (" + copayId + ")");
            qryStr.append(" where copay_detail_sl_formualry_status='" + forStatus + "'");
            copayRes = dbUtils.executeQueryToVector(qryStr.toString());
        }
        return copayRes;
    }

    public Vector searchCoverageDetail( String coverageId,  String NDCCode,  String payerId,
             DatabaseConn dbUtils) throws Exception {
        if (coverageId.equals("''")) {
            return new Vector();
        }
         StringBuffer qryStr = new StringBuffer();
        System.out.println("Searching Coverage...");
        qryStr.append(
                " SELECT distinct k.drugdetail, isal as al, coverage_detail_min_age as minage, coverage_detail_product_id as ndccode, coverage_detail_min_age_qualifier as minageq, coverage_detail_max_age as maxage, coverage_detail_max_age_qualifier as maxageq,coverage_detail_gender as gender, coverage_detail_max_amount as maxamount, coverage_detail_max_amount_qualifier as maxamountq, coverage_detail_time_period as period, coverage_detail_time_period_start as periodstart,");
        qryStr.append(
                " coverage_detail_time_period_end as periodend, coverage_detail_time_period_units as periodunits, (select coverage_detail_rs_url from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                        + payerId
                        + ") inner join formulary_coverage_detail_rs on coverage_header_id=coverage_detail_rs_coverage_header_id and coverage_detail_rs_coverage_id in ("
                        + coverageId
                        + ") limit 1)as rsurl, coverage_detail_rd_url as rdurl, coverage_detail_stepdrug_product_id as stepdrugid,coverage_detail_stepdrug_qualifier as stepdrugq,coverage_detail_stepdrug_classid as classid, coverage_detail_stepdrug_subclassid as subclassid,");
        qryStr.append(
                " coverage_detail_no_of_drugs_try as nooftry, coverage_detail_step_order as steporder, coverage_detail_diagnosis_code as dxcode, coverage_detail_diagnosis_code_qualifier as dxq,coalesce(coverage_detail_short_msg,'') as shortmsg, ");
        qryStr.append(
                " coverage_detail_long_msg as longmsg, benefit_header_senderid as payerid, ispa as pa, ismn as mn, isst as st from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(
                " inner join formulary_coverage_detail on coverage_header_id=coverage_detail_coverage_header_id and coverage_detail_coverage_id in ("
                        + coverageId + ")");
        qryStr.append(" and coverage_detail_product_id in (" + NDCCode + ") ");
        qryStr.append(" left join (");
        qryStr.append(
                " select ndc_code as ndccode, trim(coalesce(brand_description,'') || ' ' || coalesce(drug_dosage_unit_name,'') || ' ' || coalesce(drug_route_name,'') || ' ' || coalesce(drug_form_name,'')) as drugdetail from brandname ");
        qryStr.append(
                " inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and drug_status<>'DIS' ");
        qryStr.append(
                " inner join formulary_coverage_detail on coverage_detail_stepdrug_product_id=ndc_code and coverage_detail_coverage_id in ("
                        + coverageId
                        + ") and coverage_detail_stepdrug_product_id is not null and ndc_code is not null ");
        qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code ");
        qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id ");
        qryStr.append(
                " left join drug_form on drug_form_id = drug_relation_map_form_id left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id ");
        qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id ");
        qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id ");
        qryStr.append(" )k on k.ndccode=coverage_detail_stepdrug_product_id\torder by coverage_detail_product_id ");
        Vector coverageRes = dbUtils.executeQueryToVector(qryStr.toString());
        if (coverageRes.size() < 1) {
            qryStr.setLength(0);
            qryStr.append(
                    " SELECT distinct 'summary' as summarycoverage, coverage_detail_rs_resource_ink_type as restype, coverage_detail_rs_url as url from formulary_benefit_header inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                            + payerId + ")");
            qryStr.append(
                    " inner join formulary_coverage_detail_rs on coverage_header_id=coverage_detail_rs_coverage_header_id and coverage_detail_rs_coverage_id in ("
                            + coverageId + ")");
            coverageRes = dbUtils.executeQueryToVector(qryStr.toString());
            return coverageRes;
        }
        String tmpNdcCode = "";
         Vector CoverageNew = new Vector();
        Hashtable tmpHash = new Hashtable();
        boolean add = false;
        boolean ageFlag = false;
        boolean paFlag = false;
        boolean mnFlag = false;
        boolean stFlag = false;
        for (int i = 0; i < coverageRes.size(); ++i) {
             Hashtable cov = (Hashtable) coverageRes.get(i);
             String al = cov.get("al").toString();
             String pa = cov.get("pa").toString();
             String mn = cov.get("mn").toString();
             String st = cov.get("st").toString();
            if (al.equals("t")) {
                ageFlag = true;
            }
            if (pa.equals("t")) {
                paFlag = true;
            }
            if (mn.equals("t")) {
                mnFlag = true;
            }
            if (st.equals("t")) {
                stFlag = true;
            }
        }
        for (int i = 0; i < coverageRes.size(); ++i) {
             Hashtable cov = (Hashtable) coverageRes.get(i);
             String ndccode = cov.get("ndccode").toString();
             String al2 = cov.get("al").toString();
             String pa2 = cov.get("pa").toString();
             String mn2 = cov.get("mn").toString();
             String st2 = cov.get("st").toString();
            if (tmpNdcCode.equals(ndccode)) {
                CoverageNew.add(tmpHash);
                CoverageNew.add(cov);
                add = true;
                break;
            }
            tmpNdcCode = ndccode;
            if (ageFlag && cov.get("minage").toString().trim().equals("")) {
                cov.put("minage", "0");
            }
            if (pa2.equals("t")) {
                cov.put("pa", "t");
            }
            if (mn2.equals("t")) {
                cov.put("mn", "t");
            }
            if (st2.equals("t")) {
                cov.put("st", "t");
            }
            tmpHash = cov;
        }
        if (!add) {
            CoverageNew.add(tmpHash);
        }
        return CoverageNew;
    }

    public Vector searchExclusion( String NDCCode,  String payerId,  String coverageId,
             DatabaseConn dbUtils) throws Exception {
        if (coverageId.equals("''") || NDCCode.equals("")) {
            return new Vector();
        }
        System.out.println("Searching Exclusion...");
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(
                " SELECT distinct coverage_detail_de_product_id as ndccode, benefit_header_senderid as payerid from formulary_benefit_header");
        qryStr.append(
                " inner join formulary_coverage_header on coverage_header_benefit_header_id=benefit_header_id and benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(
                " inner join formulary_coverage_detail_de on coverage_header_id=coverage_detail_de_coverage_header_id and coverage_detail_de_coverage_id in ("
                        + coverageId + ")");
        qryStr.append(" and coverage_detail_de_product_id in(" + NDCCode + ")");
         Vector exclusionVector = dbUtils.executeQueryToVector(qryStr.toString());
        return exclusionVector;
    }

    public Vector searchStatus( String formularyId,  String NDCCode,  String payerId,
             DatabaseConn dbUtils) throws Exception {
        if (formularyId.equals("''") || NDCCode.equals("")) {
            return new Vector();
        }
        System.out.println("Searching Status...");
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(
                " select distinct case when status_detail_status='U' then '-1' else status_detail_status end as status, status_detail_product_id as ndccode, benefit_header_senderid as payerid from formulary_benefit_header");
        qryStr.append(
                " inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(" inner join formulary_status_detail on status_detail_status_header_id=status_header_id");
        qryStr.append(" and status_header_formularyid in (" + formularyId + ") and status_detail_product_id in("
                + NDCCode + ") order by status desc limit 1");
         Vector statusVector = dbUtils.executeQueryToVector(qryStr.toString());
        return statusVector;
    }

    public Vector searchStatus( String formularyId,  String payerId,  String drugType,
             String proStatus,  DatabaseConn dbUtils) throws Exception {
        if (formularyId.equals("''")) {
            return new Vector();
        }
        System.out.println("Searching Status2...");
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(" select distinct benefit_header_senderid as payerid, ");
        if (drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("RX")) {
            qryStr.append(
                    " case when status_header_brand_formulary_status='U' then '-1' else status_header_brand_formulary_status end as status, 'B' as drugtype");
        } else if (drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("RX")) {
            qryStr.append(
                    " case when status_header_generic_formulary_status='U' then '-1' else status_header_generic_formulary_status end as status, 'G' as drugtype");
        } else if (drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("OTC")) {
            qryStr.append(
                    " case when status_header_brand_over_count_formulary_status='U' then '-1' else status_header_brand_over_count_formulary_status end as status, 'BO' as drugtype");
        } else if (drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("OTC")) {
            qryStr.append(
                    " case when status_header_generic_over_count_formulary_status='U' then '-1' else status_header_generic_over_count_formulary_status end as status, 'GO' as drugtype");
        } else if (drugType.equalsIgnoreCase("B") && proStatus.equalsIgnoreCase("DIS")) {
            qryStr.append(
                    " case when status_header_brand_formulary_status='U' then '-1' else status_header_brand_formulary_status end as status, 'BD' as drugtype");
        } else if (drugType.equalsIgnoreCase("G") && proStatus.equalsIgnoreCase("DIS")) {
            qryStr.append(
                    " case when status_header_generic_formulary_status='U' then '-1' else status_header_generic_formulary_status end as status, 'GD' as drugtype");
        } else {
            qryStr.append(" -2 as status, '' as drugtype");
        }
        qryStr.append(
                " ,status_header_cost_limit as cost_limit, benefit_header_senderid as payerid from formulary_benefit_header");
        qryStr.append(" inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id ");
        qryStr.append(" and benefit_header_senderid in (" + payerId + ") and status_header_formularyid in ("
                + formularyId + ") order by status desc limit 1");
         Vector headerVector = dbUtils.executeQueryToVector(qryStr.toString());
        return headerVector;
    }

    public Vector searchDrug( String brandName,  String form,  String strength,  String route,
             String drugName,  DatabaseConn dbUtils,  String formularyId,  String coverageId,
             String payerId) throws Exception {
        System.out.println("Searching Drugs...");
         StringBuffer qryStr = new StringBuffer();
        qryStr.append(
                " select ndc_code as ndccode, case when coalesce(k.status,'')='' then '-2' when k.status='U' then '-1'  else k.status end::integer as status, coverage_detail_de_product_id as covid, trim(coalesce(brand_description,'') || '!~!' || coalesce(drug_dosage_unit_name,'') || '!~!' || coalesce(drug_route_name,'') || '!~!' || coalesce(drug_form_name,'') || '!~!' || coalesce(drug_type,'') || '!~!' || coalesce(drug_status,'')) as drugdetail, drug_details_name as drugname, therapeutic_alternative as tecode from brandname");
        qryStr.append(
                " inner join ndc_drug_brand_map on brandname.brand_code = ndc_drug_brand_map.brand_code and (drug_status<>'DIS')");
        if (!brandName.equalsIgnoreCase("")) {
            qryStr.append(" and brand_description ilike '" + brandName.replaceAll("'", "''") + "%'");
        }
        qryStr.append(" left join drug_relation_map on maindrugcode=drug_relation_map_code");
        qryStr.append(" left join drug_details on drug_relation_map_drug_id = drug_details_id");
        qryStr.append(" left join drug_form on drug_form_id = drug_relation_map_form_id");
        qryStr.append(" left join drug_dosage on drug_dosage_id = drug_relation_map_dosage_id");
        qryStr.append(" left join drug_dosage_unit on drug_dosage_unit_id = drug_relation_map_unit_id");
        qryStr.append(" left join drug_route on drug_route_id = drug_relation_map_route_id");
        qryStr.append(
                " left join formulary_coverage_detail_de on ndc_code::varchar(11)=coverage_detail_de_product_id::varchar(11) and coverage_detail_de_coverage_id in ("
                        + coverageId + ")");
        qryStr.append(
                " left join formulary_coverage_header on coverage_header_id=coverage_detail_de_coverage_header_id ");
        qryStr.append(
                " left join formulary_benefit_header b on coverage_header_benefit_header_id=b.benefit_header_id and b.benefit_header_senderid in ("
                        + payerId + ")");
        qryStr.append(
                " left join (select status_detail_status as status, status_detail_product_id as proid from formulary_benefit_header ");
        qryStr.append(
                " inner join formulary_status_header on benefit_header_id=status_header_benefit_header_id and benefit_header_senderid in ("
                        + payerId + ") and status_header_formularyid in (" + formularyId + ")");
        qryStr.append(
                " inner join formulary_status_detail on status_header_id=status_detail_status_header_id)k on k.proid::varchar(11)=ndc_code::varchar(11)");
        qryStr.append(" where 1=1 and ndc_code is not null");
        if (!form.equalsIgnoreCase("")) {
            qryStr.append(" and drug_form_name ilike '" + form.replaceAll("'", "''") + "%'");
        }
        if (!strength.equalsIgnoreCase("")) {
            qryStr.append(" and drug_dosage_name ilike '" + strength.replaceAll("'", "''") + "%'");
        }
        if (!route.equalsIgnoreCase("")) {
            qryStr.append(" and drug_route_name ilike '" + route.replaceAll("'", "''") + "%'");
        }
        if (!drugName.equalsIgnoreCase("")) {
            qryStr.append(" and drug_details_name ilike '" + drugName.replaceAll("'", "''") + "%'");
        }
        qryStr.append(" order by drugdetail, status desc");
         Vector drugVector = dbUtils.executeQueryToVector(qryStr.toString());
        return drugVector;
    }

    public String sendTransaction( String urlString,  String transaction,  boolean isXML)
            throws Exception {
        logStep("Processing new transaction");
        String response = "";
        String messageId = "";
         int BUFFER_SIZE = 100;

        try {
            if (isXML) {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(transaction)));
                messageId = document.getElementsByTagName("MessageID").item(0).getChildNodes().item(0).getNodeValue();
                logStep("Message ID in current transaction " + messageId);
             }
            URL url = new URL("https://switch.surescripts.net/rxhub");

            if (isXML) {
				url = new URL("https://smh-staging.surescripts.net/medhistory/Glenwood/mhv3_0?id=" + messageId);
			} else {
				url = new URL("https://shbr-staging.surescripts.net/rxhub");
			}

            logStep("calling webservice "+url);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
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
            con.setConnectTimeout(120000);
            con.setReadTimeout(120000);
            con.setRequestProperty("Content-length", String.valueOf(transaction.length()));
            con.setRequestProperty("Content-type", isXML ? "text/xml" : "text/plain");
            con.setRequestProperty("Authorization", this.getAdvanceeRxEncodedCredentials());
            con.setSSLSocketFactory(sslContext.getSocketFactory());
             OutputStream out = con.getOutputStream();
            out.write(transaction.getBytes());
            out.flush();
             BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
             char[] cbuf = new char[BUFFER_SIZE + 1];
            while (true) {
                 int numCharRead = in.read(cbuf, 0, BUFFER_SIZE);
                if (numCharRead == -1) {
                    break;
                }
                 String line = new String(cbuf, 0, numCharRead);
                response = String.valueOf(response) + line;
            }
            in.close();
            out.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logStep("response received for transaction with MessageID ["+messageId+"]:\n"+this.stripNonValidXMLCharacters(response));

        return this.stripNonValidXMLCharacters(response);
    }

    private void logStep(String message) {
        System.out.println(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())+" [INFO] "+message);
    }

    public String stripNonValidXMLCharacters( String in) {
         StringBuffer out = new StringBuffer();
        if (in == null || "".equals(in)) {
            return "";
        }
        for (int i = 0; i < in.length(); ++i) {
             char current = in.charAt(i);
            if (current == '\u001d' || current == '\u001f') {
                out.append('*');
            } else if (current == '\u001c') {
                out.append(':');
            } else if (current == '\u001e') {
                out.append('~');
            } else {
                out.append(current);
            }
        }
        return out.toString();
    }

    public String getAdvanceeRxEncodedCredentials() {
        String returnValue = "";
        try {
             String str = new String("SURESCRIPTS-XHUB:56QJ9JKSE5".getBytes(), "UTF-8");
             byte[] toEncode = str.getBytes();
             byte[] result = doEncoding(toEncode);
            returnValue = "Basic " + new String(result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return returnValue;
    }

    public String attachment( String fileName,  String binaryData) {
         String mtomWriteDirString = "C:\\WebUpload";
         File mtomWriteDir = new File(mtomWriteDirString);
        mtomWriteDir.mkdirs();
         File fileToWrite = new File(mtomWriteDir, fileName);
        if (!fileToWrite.exists()) {
            try {
                fileToWrite.createNewFile();
                this.writeFile(fileToWrite, binaryData);
                return " File " + fileName + " has been successfully saved";
            } catch (IOException e) {
                e.printStackTrace();
                return "Permission is denied to write the file";
            }
        }
        try {
            this.writeFile(fileToWrite, binaryData);
            return " File " + fileName + " has been successfully saved";
        } catch (IOException ex) {
            return "Permission is denied to write the file";
        }
    }

    private void writeFile( File fileToWrite,  String binaryData) throws IOException {
         OutputStream outStream = new FileOutputStream(fileToWrite);
        outStream.write(Base64.decode(binaryData));
        outStream.flush();
        outStream.close();
    }

    public void invoke( MessageContext msgContext) throws AxisFault {
         Message reqMsg = msgContext.getRequestMessage();
         Message respMsg = msgContext.getResponseMessage();
         Attachments messageAttachments = reqMsg.getAttachmentsImpl();
         int attachmentCount = messageAttachments.getAttachmentCount();
         AttachmentPart[] attachments = new AttachmentPart[attachmentCount];
         Iterator it = messageAttachments.getAttachments().iterator();
        int count = 0;
        while (it.hasNext()) {
             AttachmentPart part = (AttachmentPart) it.next();
            attachments[count++] = part;
        }
        try {
            respMsg.writeTo((OutputStream) new FileOutputStream("c:/soaprespMesg.txt"));
            reqMsg.writeTo((OutputStream) new FileOutputStream("c:/soapreqMesg.txt"));
        } catch (Exception e) {
            System.out.println("Exception in Writing Soap Message..." + e);
        }
    }
}
