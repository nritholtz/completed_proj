package EbayCrawler;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GetQueryResult {
	private static String webpage=null;
	/**
	 * This method will pull the query result for a query with passed in parameters for the query
	 * @param keywords The user generated keywords for the search
	 * @param categoryNum The category number in which the search is limited, -1 for all categories
	 * @param searchOp The search optimization number in which the query will be sorted
	 * @param pagenum The page number to pull the query from
	 * @return True if no errors, False otherwise
	 * @throws Exception
	 */
	public static boolean getQuery(String keywords, String categoryNum, int searchOp,int pagenum) throws Exception{
		//Save Parameters for future passing if next/previous pages
		MainMenu.currKeyword=keywords;
		MainMenu.currCatNum=categoryNum;
		MainMenu.searchNum=searchOp;
		
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
	       //Passed Filter for auctionType
	       String auctType="";
	       if(MainMenu.auctionRB.isSelected())auctType="&LH_Auction=1";
	       else if(MainMenu.buyItNowRB.isSelected())auctType="&LH_BIN=1";
	       
	       //Search in All Categories
	       if(categoryNum=="-1"||categoryNum==null){
	    	   webpage="http://www.ebay.com/sch/i.html?_nkw="+keywords.replace(" ", "+")+"&_sacat=0&_pgn="+pagenum+"&_ipg=25&_sop="+searchOp+auctType;
	       }
	       else  webpage="http://www.ebay.com/sch/"+categoryNum+"/i.html?_nkw="+keywords.replace(" ", "+")+"&_pgn="+pagenum+"&_ipg=25&_sop="+searchOp+auctType;
	       
	       
	     //pass UA for HTML content
			BufferedReader reader = WebpageReaderWithAgent.read(webpage); 

			//Parses HTML as a string for easier expression matching
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			
			while (line != null) {
				sb.append( line ) ;
				line = reader.readLine(); 
			}
	       reader.close();

	       //Gets auctionListings query details
	       try{
			Pattern listingID= Pattern.compile("listingId=\"(\\d{12})\"");
			Pattern notFound=Pattern.compile("(?:category, so we searched in|results found for)");
			//Checks whether there is more results in previous or next page
			Pattern nextPageEnabledPat=Pattern.compile("(?:<a  title=\"Next page of results\" aria-disabled=\"true\"|Retry your search)");
			Pattern previousPageEnabledPat=Pattern.compile("(?:<a  title=\"Previous page of results\" aria-disabled=\"true\"|Retry your search)");
			Pattern imageURL=Pattern.compile("src=\"[^\"]+?\" class=\"img\" alt='([^']+?)'");
			
			Matcher previousSearcher=previousPageEnabledPat.matcher(sb.toString());
			Matcher nextSearcher=nextPageEnabledPat.matcher(sb.toString());
			Matcher auctionListingMatcher=listingID.matcher(sb.toString());
			Matcher notFoundMatcher=notFound.matcher(sb.toString());
			//If no results found, setup a no results found view

			if(notFoundMatcher.find()||keywords.length()==0){
				String[][] noResult={{"No Results Found"}};
				String[] emptyHeader={""};
				MainMenu.queryTable.setModel(new DefaultTableModel(noResult,emptyHeader));
				 TableColumnModel tcm = MainMenu.queryTable.getColumnModel();
	             TableColumn first = tcm.getColumn(0);
	             first.setPreferredWidth(800);
				MainMenu.nextPageButton.setEnabled(false);
				MainMenu.previousPageButton.setEnabled(false);
				return true;
			}
			//Create a structure to temporarily hold the query results for use later to selected
			Vector<TreeNode> auctions= new Vector<TreeNode>();
			while(auctionListingMatcher.find()){
				String id=auctionListingMatcher.group(1);
				auctionListingMatcher.usePattern(imageURL);
				auctionListingMatcher.find();
				auctions.add(new TreeNode(auctionListingMatcher.group(1).replace("&amp;", "&").replace("&#034;","\"").replace("<wbr/>", "").replace("&#039;", "'"),id));
				auctionListingMatcher.usePattern(listingID);
			}
			Object[][] data= new Object[auctions.size()][2];
			int size=auctions.size();
			for(int item=0;item<size;item++){
				TreeNode node=auctions.remove(0);	
				data[item][0]=node.id;
				data[item][1]=node.name;
			}
			//Insert and redo Layout
			DefaultTableModel model=new DefaultTableModel(data,MainMenu.colNames);
			MainMenu.queryTable.setModel(model);
			 TableColumnModel tcm = MainMenu.queryTable.getColumnModel();
             TableColumn first = tcm.getColumn(0);
             first.setResizable(false);
             TableColumn last= tcm.getColumn(1);
             last.setResizable(false);
             first.setPreferredWidth(150);
             last.setPreferredWidth(850);

	       model.fireTableDataChanged();
	       
	       //Determine whether there is a next or previous page, and set buttons accordingly
	       if(previousSearcher.find()) MainMenu.previousPageButton.setEnabled(false);
			else MainMenu.previousPageButton.setEnabled(true);
			if(nextSearcher.find()) MainMenu.nextPageButton.setEnabled(false);
			else MainMenu.nextPageButton.setEnabled(true);
	       }
	       catch(Exception e){e.printStackTrace();}
	       return true;
	}

}
