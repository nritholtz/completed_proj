package EbayCrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTree;
/**
 * Populates the category table in the database with newly updated Ebay Categories
 * @author Natan Ritholtz
 *
 */
public class GetCategories {
	//Connection and DB components
	private static String webpage = null;
	 private static Connection connect = null;
	  private static PreparedStatement preparedStatement = null;
	    static int progress;
	    
	public static BufferedReader read(String url) throws Exception {
		return new BufferedReader(
			new InputStreamReader(
				new URL(url).openStream()));
	} // read

	
	/**
	 * A function that inserts a new category into the database
	 * @param catID The ebay specific category ID
	 * @param name Category name
	 * @param level Level of category in hierarchy 
	 * @param parent The database id of the parent category (if it exists)
	 * @return The database id of the newly created category
	 */
	public static int insertCategory(String name, int level, int parent, String catID){
		try {
			 String dburl = "jdbc:mysql://localhost:3306/";
	         String dbName = "ebay";
	         String driver = "com.mysql.jdbc.Driver";
	         String userName = MainMenu.databaseName;
	         String password = MainMenu.databasePassword;
	        
		      // This will load the MySQL driver, each DB has its own driver
		    	Class.forName(driver).newInstance();
		       
		      // Setup the connection with the DB
		    	  connect = DriverManager.getConnection(dburl+dbName,userName,password);
		  
		    

		      // PreparedStatements can use variables and are more efficient
		      preparedStatement = connect
		          .prepareStatement("replace into  ebay.category (id,name,level,parent) values (?, ?, ?,?)");
		      preparedStatement.setInt(1, Integer.parseInt(catID));
		      preparedStatement.setString(2, name);
		      preparedStatement.setInt(3, level);
		      //Uses -1 for parent categories
		      if(parent!=-1) preparedStatement.setInt(4, parent);
		      else preparedStatement.setNull(4, java.sql.Types.INTEGER);
		      preparedStatement.executeUpdate();
	        
		      //Closes Database connection
		      if (connect != null) {
		        connect.close();
		      }   
		    }
		 
		 catch (Exception e) {
			 System.out.println(e);
		    }

		  return Integer.parseInt(catID);
	}
	

	
	        
	        //Implements Error Checking by returning false when no DB exists.
    
    public static boolean getCategories() throws Exception  {
    	
    			progress=0;
    			
    			//Page that will be used to update categories
    			webpage = "http://listings.ebay.com/_W0QQloctZShowCatIdsQQsacatZQ2d1QQsalocationZatsQQsocmdZListingCategoryList";
    			
    			//pass UA for HTML content
    			BufferedReader reader = WebpageReaderWithAgent.read(webpage); 
    			//Parses HTML as a string for easier expression matching
    			StringBuilder sb = new StringBuilder();
    			String line = reader.readLine();
    			//Used for database ID for parent categories
    			int parentID;
    			
    			while (line != null) {
    				sb.append( line ) ;
    				line = reader.readLine(); 
    			} 

    				//Different regex patterns used dependent on page type
    			  Pattern parent = Pattern.compile("<td colspan=\"5\"><a href=\"([^\"]+?)\"><i>See all ([^<]+)(?=categories...</i></a>)"); //Parent Category regex
    			  Pattern sub1=Pattern.compile("<td colspan=\"6\">[\\s]*?(?:<b>[\\s]*?<a href=\"[^\"]+\">|<a href=\"[^\"]+\">[\\s]*?<b>|<b>)([^<]+)(?:</b></a>[\\s]*[(]#([0-9]+)[)]|</a>[\\s]*[(]#([0-9]+)[)])");
    			  Pattern sub1child=Pattern.compile("<td colspan=\"6\">[\\s]*?(?:<b>[\\s]*?<a href=\"[^\"]+\">|<a href=\"[^\"]+\">[\\s]*?<b>|<b>)([^<]+)(?:</a>[\\s]*</b>[\\s]*[(]#([0-9]+)[)]|(?:</a>|</b>)[\\s]*[(]#([0-9]+)[)])");
    			  Pattern nonAllChild=Pattern.compile("<td colspan=\"5\">[\\s]*?(?:<a href=\"[^\"]+\">([^<]+)</a>[\\s]*[(]#([0-9]+)[)]|([^(]+?)[(]#([0-9]+)[)])");
    			  Pattern sub2=Pattern.compile("<td colspan=\"5\">[\\s]*?(?:<a href=\"[^\"]+\">([^<]+)</a>[\\s]*[(]#([0-9]+)[)]|([^(]+?)[(]#([0-9]+)[)])");
    			  Pattern nonAllsub2=Pattern.compile("<td colspan=\"4\">[\\s]*?(?:<a href=\"[^\"]+\">([^<]+)</a>[\\s]*[(]#([0-9]+)[)]|([^(]+?)[(]#([0-9]+)[)])");
    			  Pattern nonAllsub3=Pattern.compile("<td colspan=\"3\">[\\s]*?(?:<a href=\"[^\"]+\">([^<]+)</a>[\\s]*[(]#([0-9]+)[)]|([^(]+?)(?:[(]#([0-9]+)[)]|[(][^)]+?[)]\\s*[(]#([0-9]+)[)]))");
    			  Pattern nonAllsub4=Pattern.compile("<td colspan=\"2\">[\\s]*?(?:<a href=\"[^\"]+\">([^<]+)</a>[\\s]*[(]#([0-9]+)[)]|([^(]+?)(?:[(]#([0-9]+)[)]|[(][^)]+?[)]\\s*[(]#([0-9]+)[)]))");
    			
    			  Matcher parentMatcher= sub1.matcher(sb.toString());
    			  //Place holders for region marking
    			 int savedpos=0,savedend=0;
    			 
    			  while(parentMatcher.find(savedpos)){
    				  String parID;
    				  String tempParent=parentMatcher.group(1).replace("&amp;", "&"); //First instance of category
    				  if(parentMatcher.group(2)==null) parID=parentMatcher.group(3);
    				  else parID=parentMatcher.group(2);
    				  savedpos=parentMatcher.end();
    				  parentMatcher.usePattern(parent);
    				  parentMatcher.find(); //Finds next See All
    				  if(!parentMatcher.group(2).replace("&amp;", "&").trim().equals(tempParent)){ //Category doesn't contain see all
    					  parentID=insertCategory(tempParent,1,-1,parID);
    					  parentMatcher.usePattern(sub1);
    					  parentMatcher.find(savedpos);
    					  savedend=parentMatcher.start();
    					  parentMatcher.usePattern(nonAllChild);
    					  parentMatcher.region(savedpos, savedend);
    					  while(parentMatcher.find()){
    						  if(parentMatcher.group(1)==null)insertCategory(parentMatcher.group(3).replace("&amp;", "&"),2,parentID,parentMatcher.group(4));//if not highlighted (such that item count is 0)
    							else insertCategory(parentMatcher.group(1).replace("&amp;", "&"),2,parentID,parentMatcher.group(2)); //if highlighted (such that count is greater than 0)

    					  }
    					  parentMatcher.reset();
    					  parentMatcher.usePattern(sub1);
    					//Re-updates progress bar and checks for cancellation
      					if(MainMenu.timerTick==-1) {
      						String str = "<html>" + "<font color=\"#FF0000\">" + "<b>" + 
      			          		  "Cancelled." + "</b>" + "</font>" + "</html>";
      			            MainMenu.updateLabel.setText(str);
      			          MainMenu.update.setEnabled(true);
      			          return true;
      					}
    					  progress=((int)tempParent.toLowerCase().charAt(0)-96)*3;
    					  MainMenu.timerTick=progress;
    				  }
    				  else{ //Category contains See all
    					  String newParent=parentMatcher.group(2).replace("&amp;", "&");
    					  parentID=insertCategory(parentMatcher.group(2).replace("&amp;", "&"),1,-1,parID);
    				  savedpos=parentMatcher.end();
    				  reader=WebpageReaderWithAgent.read(parentMatcher.group(1));
    				  StringBuilder sb2 = new StringBuilder();
    					String line2 = reader.readLine();
    					//Parses HTML as a string for easier expression matching
    					
    					while (line2 != null) {
    						sb2.append( line2 ) ;
    						line2 = reader.readLine(); 
    					} // while
    					
    					Matcher sub1Matcher= parent.matcher(sb2.toString());
    					if(sub1Matcher.find()){ //Page contains see all format
    						sub1Matcher.reset();
    						sub1Matcher.usePattern(sub1);
    						while(sub1Matcher.find()){
    							if(sub1Matcher.group(2)==null) parID=sub1Matcher.group(3);
    		    				  else parID=sub1Matcher.group(2);
    							int level2ID=insertCategory(sub1Matcher.group(1).replace("&amp;", "&"),2,parentID,parID);
    							sub1Matcher.usePattern(parent);
    							sub1Matcher.find();
    						reader=WebpageReaderWithAgent.read(parentMatcher.group(1).split(".com")[0]+".com"+sub1Matcher.group(1));
    						StringBuilder sb3= new StringBuilder();
    						String line3= reader.readLine();
    						while(line3!=null){
    							sb3.append(line3);
    							line3=reader.readLine();
    						}
    
    						Matcher sub2Matcher=sub1.matcher(sb3.toString());
    						int holdPosStart=0, holdPosEnd=0;
    						while(sub2Matcher.find()){ //Level 3
    							if(sub2Matcher.group(2)==null) parID=sub2Matcher.group(3);
  		    				  else parID=sub2Matcher.group(2);
    							int level3ID=insertCategory(sub2Matcher.group(1).replace("&amp;", "&"),3,level2ID,parID);
    							holdPosStart=sub2Matcher.end();
    							Matcher sub3Matcher=sub1.matcher(sb3.toString());
    							if(sub3Matcher.find(holdPosStart)) holdPosEnd=sub3Matcher.start();
    							else holdPosEnd=sb3.length();
    							sub3Matcher.region(holdPosStart, holdPosEnd);
    							sub3Matcher.usePattern(sub2);
    							int sub4Start=0, sub4End=0;
    							while(sub3Matcher.find()){ //Level 4
    								int level4ID;
    								  if(sub3Matcher.group(1)==null)level4ID=insertCategory(sub3Matcher.group(3).replace("&amp;", "&"),4,level3ID,sub3Matcher.group(4));//if not highlighted (such that item count is 0)
    	    							else level4ID=insertCategory(sub3Matcher.group(1).replace("&amp;", "&"),4,level3ID,sub3Matcher.group(2)); //if highlighted (such that count is greater than 0)
    								sub4Start=sub3Matcher.end();
    								Matcher sub4Matcher=sub2.matcher(sb3.toString());
    								if(sub4Matcher.find(sub4Start)) sub4End=sub4Matcher.start();
    								else sub4End=sb3.length();
    								sub4Matcher.region(sub4Start, sub4End);
    								sub4Matcher.usePattern(nonAllsub2);
    								int sub5Start=0, sub5End=0;
    								while(sub4Matcher.find()){//Level 5
    									int level5ID;
    									 if(sub4Matcher.group(1)==null)level5ID=insertCategory(sub4Matcher.group(3).replace("&amp;", "&"),5,level4ID,sub4Matcher.group(4));//if not highlighted (such that item count is 0)
     	    							else level5ID=insertCategory(sub4Matcher.group(1).replace("&amp;", "&"),5,level4ID,sub4Matcher.group(2)); //if highlighted (such that count is greater than 0)
    								sub5Start=sub4Matcher.end();
    								Matcher sub5Matcher=nonAllsub2.matcher(sb3.toString());
    								if(sub5Matcher.find(sub5Start)) sub5End=sub5Matcher.start();
    								else sub5End=sb3.length();
    								sub5Matcher.region(sub5Start, sub5End);
    								sub5Matcher.usePattern(nonAllsub3);
    								while(sub5Matcher.find()){//Level 6
    									 if(sub5Matcher.group(1)==null){
    										 if(sub5Matcher.group(4)==null) parID=sub5Matcher.group(5);
    										 else parID=sub5Matcher.group(4);
    										insertCategory(sub5Matcher.group(3).replace("&amp;", "&"),6,level5ID,parID);//if not highlighted (such that item count is 0)
    									 }
      	    							else insertCategory(sub5Matcher.group(1).replace("&amp;", "&"),6,level5ID,sub5Matcher.group(2)); //if highlighted (such that count is greater than 0)
    									
    								}
    								}
    							}
    						}
    						
    						}
    					}
    					else{ //Doesn't contain see all format
    						sub1Matcher.reset();
    						sub1Matcher.usePattern(sub1child);
    						int sub1Start=0,sub1End=0;
    					while(sub1Matcher.find(sub1Start)){
    						if(sub1Matcher.group(2)==null) parID=sub1Matcher.group(3);
		    				  else parID=sub1Matcher.group(2);
							int level2ID=insertCategory(sub1Matcher.group(1).replace("&amp;", "&"),2,parentID,parID);
    						sub1Start=sub1Matcher.end();
    						if(sub1Matcher.find()) sub1End=sub1Matcher.start();
    						else sub1End=sb2.length();
    						sub1Matcher.region(sub1Start, sub1End);
    						sub1Matcher.usePattern(sub2);
    						int sub2Start=0,sub2End=0;
    						while(sub1Matcher.find()){//Level 3
    							int level3ID;
    							if(sub1Matcher.group(1)==null)level3ID=insertCategory(sub1Matcher.group(3).replace("&amp;", "&"),3,level2ID,sub1Matcher.group(4));//if not highlighted (such that item count is 0)
    							else level3ID=insertCategory(sub1Matcher.group(1).replace("&amp;", "&"),3,level2ID,sub1Matcher.group(2)); //if highlighted (such that count is greater than 0)
    							sub2Start=sub1Matcher.end();
    							Matcher sub2Matcher= sub2.matcher(sb2.toString());
    							if(sub2Matcher.find(sub2Start))sub2End=sub2Matcher.start();
    							else sub2End=sub1End;
    							sub2Matcher.reset();
    							sub2Matcher.region(sub2Start, sub2End);
    							sub2Matcher.usePattern(nonAllsub2);
    							int sub3Start=0, sub3End=0;
    							while(sub2Matcher.find()){//Level 4
    								int level4ID;
    								 if(sub2Matcher.group(1)==null)level4ID=insertCategory(sub2Matcher.group(3).replace("&amp;", "&"),4,level3ID,sub2Matcher.group(4));//if not highlighted (such that item count is 0)
  	    							else level4ID=insertCategory(sub2Matcher.group(1).replace("&amp;", "&"),4,level3ID,sub2Matcher.group(2)); //if highlighted (such that count is greater than 0)
    								sub3Start=sub2Matcher.end();
    								Matcher sub3Matcher=nonAllsub2.matcher(sb2.toString());
    								if(sub3Matcher.find(sub3Start)) sub3End=sub3Matcher.start();
    								else sub3End=sb2.length();
    								sub3Matcher.region(sub3Start, sub3End);
    								sub3Matcher.usePattern(nonAllsub3);
    								int sub4Start=0,sub4End=0;
    								while(sub3Matcher.find()){//Level 5
    									int level5ID;
    									if(sub3Matcher.group(1)==null){
   										 if(sub3Matcher.group(4)==null) parID=sub3Matcher.group(5);
   										 else parID=sub3Matcher.group(4);
   										 level5ID=insertCategory(sub3Matcher.group(3).replace("&amp;", "&"),5,level4ID,parID);//if not highlighted (such that item count is 0)
   									 }
     	    							else level5ID=insertCategory(sub3Matcher.group(1).replace("&amp;", "&"),5,level4ID,sub3Matcher.group(2)); //if highlighted (such that count is greater than 0)
    									sub4Start=sub3Matcher.end();
    									Matcher sub4Matcher=nonAllsub3.matcher(sb2.toString());
    									if(sub4Matcher.find(sub4Start)) sub4End=sub4Matcher.start();
    									else sub4End=sb2.length();
    									sub4Matcher.region(sub4Start, sub4End);
    									sub4Matcher.usePattern(nonAllsub4);
    									while(sub4Matcher.find()){//Level 6
    										if(sub4Matcher.group(1)==null){
    	   										 if(sub4Matcher.group(4)==null) parID=sub4Matcher.group(5);
    	   										 else parID=sub4Matcher.group(4);
    	   										insertCategory(sub4Matcher.group(3).replace("&amp;", "&"),6,level5ID,parID);//if not highlighted (such that item count is 0)
    	   									 }
    	     	    							else insertCategory(sub4Matcher.group(1).replace("&amp;", "&"),6,level5ID,sub4Matcher.group(2)); //if highlighted (such that count is greater than 0)
    									}
    								}
    							}
    						}
    						sub1Matcher.reset();
    						sub1Matcher.usePattern(sub1child);
    					}
    					
    					}
    					parentMatcher.usePattern(sub1);
    					//Re-updates progress bar and checks for cancellation
    					if(MainMenu.timerTick==-1) {
      						String str = "<html>" + "<font color=\"#FF0000\">" + "<b>" + 
      			          		  "Cancelled." + "</b>" + "</font>" + "</html>";;
      			            MainMenu.updateLabel.setText(str);
      						return true;
      					}
    					  progress=((int)newParent.toLowerCase().charAt(0)-96)*3;
    					  MainMenu.timerTick=progress;
    			  }
    			  }
    			 
    			  MainMenu.timerTick=98;
    			  /**
    			   * Since Ebay Motors has different format for url passage, we need to do a special search
    			   */
				String parID;
				reader=WebpageReaderWithAgent.read("http://listings.ebaymotors.com/_W0QQloctZShowCatIdsQQsacatZQ2d1QQsocmdZListingCategoryList");
					  parentID=insertCategory("eBay Motors",1,-1,"6000");
				  StringBuilder sb2 = new StringBuilder();
					String line2 = reader.readLine();
					//Parses HTML as a string for easier expression matching
					while (line2 != null) {
						sb2.append( line2 ) ;
						line2 = reader.readLine(); 
					} // while
					
					Matcher sub1Matcher= sub1.matcher(sb2.toString());
						while(sub1Matcher.find()){
							if(sub1Matcher.group(2)==null) parID=sub1Matcher.group(3);
		    				  else parID=sub1Matcher.group(2);
							int level2ID=insertCategory(sub1Matcher.group(1).replace("&amp;", "&"),2,parentID,parID);
							sub1Matcher.usePattern(parent);
							sub1Matcher.find();
						reader=WebpageReaderWithAgent.read("http://listings.ebaymotors.com"+sub1Matcher.group(1));
						StringBuilder sb3= new StringBuilder();
						String line3= reader.readLine();
						while(line3!=null){
							sb3.append(line3);
							line3=reader.readLine();
						}

						Matcher sub2Matcher=sub1child.matcher(sb3.toString());
						int holdPosStart=0, holdPosEnd=0;
						while(sub2Matcher.find()){ //Level 3
							if(sub2Matcher.group(2)==null) parID=sub2Matcher.group(3);
		    				  else parID=sub2Matcher.group(2);
							int level3ID=insertCategory(sub2Matcher.group(1).replace("&amp;", "&"),3,level2ID,parID);
							holdPosStart=sub2Matcher.end();
							Matcher sub3Matcher=sub1child.matcher(sb3.toString());
							if(sub3Matcher.find(holdPosStart)) holdPosEnd=sub3Matcher.start();
							else holdPosEnd=sb3.length();
							sub3Matcher.region(holdPosStart, holdPosEnd);
							sub3Matcher.usePattern(sub2);
							int sub4Start=0, sub4End=0;
							while(sub3Matcher.find()){ //Level 4
								int level4ID;
								  if(sub3Matcher.group(1)==null)level4ID=insertCategory(sub3Matcher.group(3).replace("&amp;", "&"),4,level3ID,sub3Matcher.group(4));//if not highlighted (such that item count is 0)
	    							else level4ID=insertCategory(sub3Matcher.group(1).replace("&amp;", "&"),4,level3ID,sub3Matcher.group(2)); //if highlighted (such that count is greater than 0)
								sub4Start=sub3Matcher.end();
								Matcher sub4Matcher=sub2.matcher(sb3.toString());
								if(sub4Matcher.find(sub4Start)) sub4End=sub4Matcher.start();
								else sub4End=sb3.length();
								sub4Matcher.region(sub4Start, sub4End);
								sub4Matcher.usePattern(nonAllsub2);
								int sub5Start=0, sub5End=0;
								while(sub4Matcher.find()){//Level 5
									int level5ID;
									 if(sub4Matcher.group(1)==null)level5ID=insertCategory(sub4Matcher.group(3).replace("&amp;", "&"),5,level4ID,sub4Matcher.group(4));//if not highlighted (such that item count is 0)
 	    							else level5ID=insertCategory(sub4Matcher.group(1).replace("&amp;", "&"),5,level4ID,sub4Matcher.group(2)); //if highlighted (such that count is greater than 0)
								sub5Start=sub4Matcher.end();
								Matcher sub5Matcher=nonAllsub2.matcher(sb3.toString());
								if(sub5Matcher.find(sub5Start)) sub5End=sub5Matcher.start();
								else sub5End=sb3.length();
								sub5Matcher.region(sub5Start, sub5End);
								sub5Matcher.usePattern(nonAllsub3);
								while(sub5Matcher.find()){//Level 6
									 if(sub5Matcher.group(1)==null){
										 if(sub5Matcher.group(4)==null) parID=sub5Matcher.group(5);
										 else parID=sub5Matcher.group(4);
										insertCategory(sub5Matcher.group(3).replace("&amp;", "&"),6,level5ID,parID);//if not highlighted (such that item count is 0)
									 }
  	    							else insertCategory(sub5Matcher.group(1).replace("&amp;", "&"),6,level5ID,sub5Matcher.group(2)); //if highlighted (such that count is greater than 0)
									
								}
								}
							}
						}
						sub1Matcher.usePattern(sub1);
						}
					
				  
    			  progress=99;
    			  MainMenu.timerTick=progress;
    			   reader.close();
    			   //Set up the category hierachy now that the GetCategories is done
    			   MainMenu.queryTree=new JTree(MainMenu.setTree());
    			   MainMenu.scrollPane_2.setViewportView(MainMenu.queryTree);
    			   progress=100;
    			   MainMenu.timerTick=progress;
    			   return true;
    		}
  


}