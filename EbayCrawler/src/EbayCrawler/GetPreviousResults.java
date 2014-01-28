package EbayCrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GetPreviousResults {
public static int maxCount;
private static String savedQuery;

public static boolean getByKeyword(String keyword, int sortOp) throws Exception{
	//Checks DB to ensure table exists
	 String dburl = "jdbc:mysql://localhost:3306/";
   String dbName = "ebay";
   String driver = "com.mysql.jdbc.Driver";
   String userName = MainMenu.databaseName;
   String password = MainMenu.databasePassword;
  
     // This will load the MySQL driver, each DB has its own driver
   	Class.forName(driver).newInstance();
      try{
     // Setup the connection with the DB
   	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
   	Statement query=connect.createStatement();
   	ResultSet RS=query.executeQuery("Select Count(*) from ebay.category");
   	RS.next();
   	if(RS.getInt(1)<1)return false;
   	connect.close();
      }
      catch(Exception e){
   	   return false;
      }	
      
      // Setup the connection with the DB
    	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
    	String sortOperation;

    	if(sortOp==1) sortOperation="itemNum Asc";
    	else if(sortOp==2) sortOperation="itemNum Desc";
    	else if(sortOp==3) sortOperation="title Asc";
    	else if(sortOp==4) sortOperation="title Desc";
    	else if (sortOp==5) sortOperation="timeStamp Asc";
    	else sortOperation="timeStamp Desc";
    	
      Statement stmt=connect.createStatement();
      String query="Select Count(*) from ebay.auction where title like '%"+keyword+"%' AND (date(timeStamp) >= '"+MainMenu.fromDate.getText()+"' AND date(timeStamp) <= '"+MainMenu.toDate.getText()+"')";
try{
      ResultSet rs= stmt.executeQuery(query);
      rs.next();
      maxCount=rs.getInt(1);
}
catch(Exception e){e.printStackTrace();}
	      
  	if(maxCount<1){
		String[][] noResult={{"No Results Found"}};
		String[] emptyHeader={""};
		MainMenu.previousTable.setModel(new DefaultTableModel(noResult,emptyHeader));
		 TableColumnModel tcm = MainMenu.previousTable.getColumnModel();
         TableColumn first = tcm.getColumn(0);
         first.setPreferredWidth(800);
		MainMenu.nextPageButton.setEnabled(false);
		MainMenu.previousPageButton.setEnabled(false);
		return true;
	}
  	//rs.close();
  	
  	
  	Statement stmt2=connect.createStatement();
     savedQuery="Select itemNum, title, timeStamp from ebay.auction where title like '%"+keyword+"%' AND (timeStamp >= '"+MainMenu.fromDate.getText()+"' AND timeStamp <= '"+MainMenu.toDate.getText()+ "')  order by "+ sortOperation+" limit ";
     query=savedQuery+((MainMenu.resultpage-1)*100)+", 100";
     try{
     ResultSet rs2=stmt2.executeQuery(query);
    
     Vector<TreeNode> auctions= new Vector<TreeNode>();
     while(rs2.next()){
    	 auctions.add(new TreeNode(rs2.getString(1),rs2.getString(2),rs2.getDate(3).toString()));
     }

	Object[][] data= new Object[auctions.size()][3];
	int size=auctions.size();
	for(int item=0;item<size;item++){
		TreeNode node=auctions.remove(0);	
		data[item][0]=node.name;
		data[item][1]=node.id;
		data[item][2]=node.timeStamp;
	}
	//Insert and redo Layout
	DefaultTableModel model=new DefaultTableModel(data,MainMenu.prevNames);
	MainMenu.previousTable.setModel(model);
	 TableColumnModel tcm = MainMenu.previousTable.getColumnModel();
     TableColumn first = tcm.getColumn(0);
     first.setResizable(false);
     TableColumn mid= tcm.getColumn(1);
     mid.setResizable(false);
     TableColumn last=tcm.getColumn(2);
     last.setResizable(false);
     first.setPreferredWidth(150);
     mid.setPreferredWidth(800);
     last.setPreferredWidth(400);

   model.fireTableDataChanged();
     }
  catch(Exception e){e.printStackTrace();}
      
      
      return true;
}

public static boolean getByNumber(String itemNum, int sortOp) throws Exception{
	//Checks DB to ensure table exists
	 String dburl = "jdbc:mysql://localhost:3306/";
   String dbName = "ebay";
   String driver = "com.mysql.jdbc.Driver";
   String userName = MainMenu.databaseName;
   String password = MainMenu.databasePassword;
  
     // This will load the MySQL driver, each DB has its own driver
   	Class.forName(driver).newInstance();
      try{
     // Setup the connection with the DB
   	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
   	Statement query=connect.createStatement();
   	ResultSet RS=query.executeQuery("Select Count(*) from ebay.category");
   	RS.next();
   	if(RS.getInt(1)<1)return false;
   	connect.close();
      }
      catch(Exception e){
   	   return false;
      }
      
   // Setup the connection with the DB
  	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
  	String sortOperation;

  	if(sortOp==1) sortOperation="itemNum Asc";
  	else if(sortOp==2) sortOperation="itemNum Desc";
  	else if(sortOp==3) sortOperation="title Asc";
  	else if(sortOp==4) sortOperation="title Desc";
  	else if (sortOp==5) sortOperation="timeStamp Asc";
  	else sortOperation="timeStamp Desc";

    
    Statement stmt=connect.createStatement();
    String query="Select Count(*) from ebay.auction where itemNum = "+itemNum+" AND (date(timeStamp) >= '"+MainMenu.fromDate.getText()+"' AND date(timeStamp) <= '"+MainMenu.toDate.getText()+"')";
	   ResultSet rs= stmt.executeQuery(query);
	      rs.next();
	      maxCount=rs.getInt(1);
	if(maxCount<1){
		String[][] noResult={{"No Results Found"}};
		String[] emptyHeader={""};
		MainMenu.previousTable.setModel(new DefaultTableModel(noResult,emptyHeader));
		 TableColumnModel tcm = MainMenu.previousTable.getColumnModel();
       TableColumn first = tcm.getColumn(0);
       first.setPreferredWidth(800);
		MainMenu.nextPageButton.setEnabled(false);
		MainMenu.previousPageButton.setEnabled(false);
		return true;
	}
	rs.close();
	
	
	Statement stmt2=connect.createStatement();
   savedQuery="Select itemNum, title, timeStamp from ebay.auction where itemNum = "+itemNum+"  AND (timeStamp >= '"+MainMenu.fromDate.getText()+"' AND timeStamp <= '"+MainMenu.toDate.getText()+ "')  order by "+ sortOperation+" limit ";
   query=savedQuery+((MainMenu.resultpage-1)*100)+", 100";
   ResultSet rs2=stmt2.executeQuery(query);
   Vector<TreeNode> auctions= new Vector<TreeNode>();
   while(rs2.next()){
  	 auctions.add(new TreeNode(rs2.getString(1),rs2.getString(2),rs2.getDate(3).toString()));
   }

	Object[][] data= new Object[auctions.size()][3];
	int size=auctions.size();
	for(int item=0;item<size;item++){
		TreeNode node=auctions.remove(0);	
		data[item][0]=node.name;
		data[item][1]=node.id;
		data[item][2]=node.timeStamp;
	}
	//Insert and redo Layout
	DefaultTableModel model=new DefaultTableModel(data,MainMenu.prevNames);
	MainMenu.previousTable.setModel(model);
	 TableColumnModel tcm = MainMenu.previousTable.getColumnModel();
   TableColumn first = tcm.getColumn(0);
   first.setResizable(false);
   TableColumn mid= tcm.getColumn(1);
   mid.setResizable(false);
   TableColumn last=tcm.getColumn(2);
   last.setResizable(false);
   first.setPreferredWidth(150);
   mid.setPreferredWidth(800);
   last.setPreferredWidth(400);

 model.fireTableDataChanged();
      return true;
}


public static boolean getPage() throws Exception{
	//Checks DB to ensure table exists
		 String dburl = "jdbc:mysql://localhost:3306/";
	   String dbName = "ebay";
	   String driver = "com.mysql.jdbc.Driver";
	   String userName = MainMenu.databaseName;
	   String password = MainMenu.databasePassword;
	  
	     // This will load the MySQL driver, each DB has its own driver
	   	Class.forName(driver).newInstance();
	      try{
	     // Setup the connection with the DB
	   	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
	   	Statement query=connect.createStatement();
	   	ResultSet RS=query.executeQuery("Select Count(*) from ebay.category");
	   	RS.next();
	   	if(RS.getInt(1)<1)return false;
	   	connect.close();
	      }
	      catch(Exception e){
	   	   return false;
	      }	
	      
	      // Setup the connection with the DB
	    	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
	    	 Statement stmt=connect.createStatement();
	    	String query=savedQuery+((MainMenu.resultpage*100)-1)+", 100";
	         ResultSet rs=stmt.executeQuery(query);
	         Vector<TreeNode> auctions= new Vector<TreeNode>();
	         while(rs.next()){
	        	 auctions.add(new TreeNode(rs.getString(1),rs.getString(2),rs.getDate(3).toString()));
	         }

	    	Object[][] data= new Object[auctions.size()][3];
	    	int size=auctions.size();
	    	for(int item=0;item<size;item++){
	    		TreeNode node=auctions.remove(0);	
	    		data[item][0]=node.id;
	    		data[item][1]=node.name;
	    		data[item][2]=node.timeStamp;
	    	}
	    	//Insert and redo Layout
	    	DefaultTableModel model=new DefaultTableModel(data,MainMenu.prevNames);
	    	MainMenu.previousTable.setModel(model);
	    	 TableColumnModel tcm = MainMenu.previousTable.getColumnModel();
	         TableColumn first = tcm.getColumn(0);
	         first.setResizable(false);
	         TableColumn last= tcm.getColumn(1);
	         last.setResizable(false);
	       model.fireTableDataChanged();
	       
	    	return true;
}
}
