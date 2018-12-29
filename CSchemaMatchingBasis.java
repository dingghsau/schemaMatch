package sau.db.dgh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.xml.soap.Detail;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;

import sun.print.resources.serviceui_zh_TW;

public class CSchemaMatchingBasis {
	
	public ArrayList <CZiJu> listZJ=new ArrayList<CZiJu>();
	
	public void chuLiChaXunRiZhi(String str1, String str2) {
		// TODO Auto-generated method stub
		readFileByLines(str1,str1);		
	}
	
	
    public void readFileByLines(String fileName, String outFile) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        try {  
            System.out.println("以行为单位读取文件内容，一次读一整行：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            String sqlStatementString = "";
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                //System.out.println("line " + line + ": " + tempString); 
                if (!tempString.equals("###")) {
                	sqlStatementString = sqlStatementString + tempString; 
				}
                else {
                	chuLiChaXunYuJu(sqlStatementString, outFile);
                	sqlStatementString = "";
				}
                line++;
            }
            if (!sqlStatementString.equals("")) {
            	chuLiChaXunYuJu(sqlStatementString, outFile);
			}
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
    } 
    
    
    public String getAtt(String s1)
    {
    	String strRtnString = "";
    	PooledSqlManager dbCon1 = PooledSqlManager.createInstance();
		try {
			dbCon1.connectDB();
			String sql = "select viewName, att from T_ViewAtt where viewName = '"
					+ s1 + "'";
			ResultSet rs = dbCon1.executeQuery(sql);
			String tempS1 = "";
			while(rs.next())//在dept表中进行遍历，直至为空
			{
				tempS1 = tempS1+ " " + rs.getString("att");
			}
			if (!tempS1.equals("")) {
				tempS1 = tempS1.substring(1,tempS1.length());
			}
			strRtnString = tempS1;
		}
		catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			dbCon1.closeDB();
		}		
    	return strRtnString;
    }
    
    
    public void myCla(String cla)
    {
    	cla.toLowerCase();
    	cla.replaceAll("(", "");
    	cla.replaceAll(")", "");
    	String aa[] = cla.split(" ");
    	int flag = 0;
    	String seq1 = "";
    	String seq2 = "";
    	String seq3 = "";
    	String seq4 = "";
    	String seq5 = "";
    	String seq6 = "";
    	for (int i = 0; i < aa.length; i++) {
			if (aa[i].equals(Def.s1)) {
				flag = 1;
			}
			if (flag == 1) {
				aa[i+1] = aa[i+1].replace(",", "");
				if (aa[i+1].equals("*"))
				{
					seq1 = deDaoShuXing(aa[i+1]);
				}
				continue;
			}
			if (aa[i].equals(Def.s2)) {
				flag = 2;
			}
			if (flag == 2) {
				for (int j = i+1; j < aa.length; j++) {
					if (Def.relSet.indexOf(aa[j]) == -1 
							&& aa[j] != "!!") {
						seq2 = seq2 + aa[j];
						seq5 = seq5 + aa[j+1];
					}
					else {
						aa[j] = "!!";
						aa[j+1] = "!!";
					}
					if(aa[j].equals(Def.s3)) break;
				}
				continue;
			}
			if (aa[i].equals(Def.s3)) {
				flag = 3;
			}
			if (flag == 3) {
				for (int j = i+1; j < aa.length; j++) {
					seq6 = seq6 + aa[j+2].replace("=", "");
					if (aa[j]== "," && !aa[j].equals(Def.s4)) {
						seq3 = seq3 + aa[j];
					}
				}
			}
			if (aa[i].equals(Def.s4)) {
				flag = 4;
			}
			if (flag == 4) {
				for (int j = i+1; j < aa.length; j++) {
					if (aa[j]== ",") {
						seq4 = seq4 + aa[j];
					}
				}
			}
		}
    	baoCunZiJu(seq1, seq2, seq3, seq4,seq5,seq6);
    }
    
    public void saveCla(String seq1, String seq2, String seq3, String seq4, String seq5, String seq6)
    {
    	PooledSqlManager dbCon1 = PooledSqlManager.createInstance();
		try {
			dbCon1.connectDB();
			String sql = "insert into T_Seq(seq1, seq2, seq3, " + 
					"seq4, seq5, seq6) values ("
					+ "'"+ seq1 + "','" + seq2 + "','" + seq3 + 
					"','" + seq4 + "','" + seq5 + "','" + 
					seq6 + "')";
					
			dbCon1.updateQuery(sql);
			String tempS1 = "";
			
		}
		catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			dbCon1.closeDB();
		}
    }
    
    
    
    public void Cla(String ss)
    {
    	ss.toLowerCase();
    	int iindex = ss.indexOf(Def.s1);
    	for (int i = 0; i < 100; i++) {
    		int cindex = ss.indexOf(Def.s1, iindex+1);
    		if(cindex != -1)
        	{
        		String cla = ss.substring(iindex, cindex);
        		myCla(cla);
        		iindex = cindex;
        	}
    		else {
    			String cla = ss.substring(iindex, ss.length());
        		myCla(cla);
    			break;
			}
		}
    	
    }
    
    public void singleSqlStatement(String fileName)
    {
    	File file = new File(fileName);  
        BufferedReader reader = null;  
        try {  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            String sqlStatement = "";
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                //System.out.println("line " + line + ": " + tempString); 
                Cla(tempString);
            }

            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
    }
    
    
    
    public void processStament(String sqlStatementString, String outFile) {
		System.out.println(sqlStatementString);
		//替换所有的集合符号为井号
		sqlStatementString.toUpperCase();
		sqlStatementString.replaceAll(Def.set1, "####");
		//替换所有的集合符号为井号
		sqlStatementString.replaceAll(Def.set4, "####");
		sqlStatementString.replaceAll(Def.set3, "####");
		sqlStatementString.replaceAll(Def.set2, "####");
		String aa[] = sqlStatementString.split("####");
		//aa每个元素为一个sql语句保存到文件中。		 
		for (int i = 0; i < aa.length; i++) {
			try{
			      String data = aa[i];

			      File file =new File(outFile);

			      //如果文件不存在则创建
			      if(!file.exists()){
			       file.createNewFile();
			      }
			      //追加问价
			      FileWriter fileWritter = new FileWriter(file.getName(),true);
			      fileWritter.write(data);
			      fileWritter.close();

			      System.out.println("写结束！语句已经输出到文件");

			     }catch(IOException e){
			      e.printStackTrace();
			     }
		}
	}
    
    public void calcualteFreq() {
    	String strRtnString = "";
    	PooledSqlManager dbCon1 = PooledSqlManager.createInstance();
		try {
			dbCon1.connectDB();
			String sql = "select * from T_Seq";
			ResultSet rs = dbCon1.executeQuery(sql);
			String tempS1 = "";
			while(rs.next())//在dept表中进行遍历，直至为空
			{
				String sid = "";
				CZiJu zj1 = new CZiJu();
				CZiJu zj2 = new CZiJu();	
				CZiJu zj3 = new CZiJu();	
				CZiJu zj4 = new CZiJu();	
				zj1.xulie = rs.getString(2);
				zj2.xulie = rs.getString(3);
				zj3.xulie = rs.getString(4);
				zj4.xulie = rs.getString(5);
				zj1.leixing = "1";
				zj2.leixing = "2";
				zj3.leixing = "3";
				zj4.leixing = "4";
				zj1.pinlv = 1;
				zj2.pinlv = 1;
				zj3.pinlv = 1;
				zj4.pinlv = 1;
				sid = rs.getString(1);
				zj1.lsid.add(sid);
				zj2.lsid.add(sid);
				zj3.lsid.add(sid);
				zj4.lsid.add(sid);
				addCla(zj1,zj2,zj3,zj4);
			}
			if (!tempS1.equals("")) {
				tempS1 = tempS1.substring(1,tempS1.length());
			}
			strRtnString = tempS1;
		}
		catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			dbCon1.closeDB();
		}		
	}
    
    public void addCla(CZiJu zj1, CZiJu zj2, CZiJu zj3, CZiJu zj4) {
    	int flag = 0;
    	String xulie = zj1.xulie;
    	int iindex = -1;
    	String leixing = "";
		for (int i = 0; i < listZJ.size(); i++) {
			CZiJu temZiJu = listZJ.get(i);
			if(xulie.equals(temZiJu.xulie) &&
					leixing.equals(temZiJu.leixing))
			{
				flag = 1;
				iindex = i;
				break;
			}
		}
		if (flag == 0) {
			listZJ.add(zj1);
		}
		else {
			CZiJu temZiJu = listZJ.get(iindex);
			temZiJu.lsid.add(zj1.lsid.get(0));
			temZiJu.pinlv++;
		}
		
		
		flag = 0;
		xulie = zj2.xulie;
		leixing = zj2.leixing;
		iindex = -1;
		
		
		for (int i = 0; i < listZJ.size(); i++) {
			CZiJu temZiJu = listZJ.get(i);
			if(xulie.equals(temZiJu.xulie) 
					&& leixing.equals(temZiJu.xulie))
			{
				flag = 1;
				iindex = i;
				break;
			}
		}
		if (flag == 0) {
			listZJ.add(zj2);
		}
		else {
			CZiJu temZiJu = listZJ.get(iindex);
			temZiJu.lsid.add(zj2.lsid.get(0));
			temZiJu.pinlv++;
		}		
		flag = 0;
		xulie = zj3.xulie;
		leixing = zj3.leixing;
		iindex = -1;	
		for (int i = 0; i < listZJ.size(); i++) {
			CZiJu temZiJu = listZJ.get(i);
			if(xulie.equals(temZiJu.xulie) 
					&& leixing.equals(temZiJu.xulie))
			{
				flag = 1;
				iindex = i;
				break;
			}
		}
		if (flag == 0) {
			listZJ.add(zj3);
		}
		else {
			CZiJu temZiJu = listZJ.get(iindex);
			temZiJu.lsid.add(zj3.lsid.get(0));
			temZiJu.pinlv++;
		}
		flag = 0;
		xulie = zj4.xulie;
		leixing = zj4.leixing;
		iindex = -1;	
		for (int i = 0; i < listZJ.size(); i++) {
			CZiJu temZiJu = listZJ.get(i);
			if(xulie.equals(temZiJu.xulie) 
					&& leixing.equals(temZiJu.xulie))
			{
				flag = 1;
				iindex = i;
				break;
			}
		}
		if (flag == 0) {
			listZJ.add(zj4);
		}
		else {
			CZiJu temZiJu = listZJ.get(iindex);
			temZiJu.lsid.add(zj4.lsid.get(0));
			temZiJu.pinlv++;
		}
	}
    
    public void calculateFre1 () {
    	String strRtnString = "";
    	PooledSqlManager dbCon1 = PooledSqlManager.createInstance();
		try {
			dbCon1.connectDB();
			String sql = "select count(*) from T_Seq where seq1 is not null";
			ResultSet rs = dbCon1.executeQuery(sql);
			String tempS1 = "";
			if(rs.next())//在dept表中进行遍历，直至为空
			{
				int geshu = rs.getInt(1);
				calculateFreqForCla(geshu, "1");
			}
			
			sql = "select count(*) from T_Seq where seq2 is not null";
			rs = dbCon1.executeQuery(sql);
			if(rs.next())//在dept表中进行遍历，直至为空
			{
				int geshu = rs.getInt(1);
				calculateFreqForCla(geshu, "2");
			}
			
			sql = "select count(*) from T_Seq where seq3 is not null";
			rs = dbCon1.executeQuery(sql);
			if(rs.next())//在dept表中进行遍历，直至为空
			{
				int geshu = rs.getInt(1);
				calculateFreqForCla(geshu, "3");
			}
			
			sql = "select count(*) from T_Seq where seq4 is not null";
			rs = dbCon1.executeQuery(sql);
			if(rs.next())//在dept表中进行遍历，直至为空
			{
				int geshu = rs.getInt(1);
				calculateFreqForCla(geshu, "4");
			}
		}
		catch (SQLException e) {
			e.printStackTrace(); 
		} finally {
			dbCon1.closeDB();
		}		
	}
    
    
    public void calculateFreqForCla (int geshu, String leixing) {
    	if (geshu == 0) {
			return;
		}
    	for (int i = 0; i < listZJ.size(); i++) {
    		CZiJu temZiJu = listZJ.get(i);
    		if (leixing.equals(temZiJu.leixing)) {
    			temZiJu.pinlv = temZiJu.pinlv / geshu;
			}
    		
		}
	}
    
    
    public void  createGraph() {
    	ArrayList <CZiJu> temList=new ArrayList<CZiJu>();

    	for (int i = 0; i < listZJ.size(); i++) {
    		CZiJu temCZiJu  = new CZiJu();
    		temCZiJu.leixing = listZJ.get(i).leixing;
    		temCZiJu.xulie = listZJ.get(i).xulie;
    		temCZiJu.pinlv = listZJ.get(i).pinlv;
    		for (int j = 0; j < listZJ.get(i).lsid.size(); j++) {
				String st1 = listZJ.get(i).lsid.get(j);
    			temCZiJu.lsid.add(st1);
			}
    		temList.add(temCZiJu);
		}
    	
    	for (int i = 0; i < listZJ.size(); i++) {
    		CZiJu node  = listZJ.get(i);
    		for (int j = 0; j < temList.size(); j++) {
				CZiJu temNode = temList.get(j);
				String aa1[] = node.xulie.split(" ");
				String aa2[] = node.xulie.split(" ");
				String att1 = "";
				String att2 = "";
				int flag = 0;
				for (int k = 0; k < aa1.length; k++) {
					att1 = aa1[k];
					for (int k2 = 0; k2 < aa2.length; k2++) {
						att2 = aa2[k2];
						if(att1.equals(att2)) {
							flag = 1;
							
							break;
						}
					}
					if (flag == 1) {
						node.bian.add(j);
						break;
						
					}
				}
				ArrayList <String> temList1= node.lsid;
				ArrayList <String> temList2= temNode.lsid;
				String str11 = "";
				String str22 = "";
				int flag1 = 0;
				for (int k = 0; k < temList1.size(); k++) {
					str11 = temList1.get(k);
					for (int k2 = 0; k2 < temList2.size(); k2++) {
						str22 = temList2.get(k2);
						if (str11.equals(str22)) {
							flag1 = 1;
							break;
						}
					}
					if (flag1 == 1) {
						node.bian.add(j);
						break;
					}
				}
			}
    		
		}
	}
}









































