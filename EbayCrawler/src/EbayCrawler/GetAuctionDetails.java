package EbayCrawler;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
/**
 * This Class will get all the details of a specific Auction through Regular Expressions
 * @author Natan Ritholtz
 *
 */
public class GetAuctionDetails {

	private static String webpage=null;
	
	/**
	 * This method will retrieve the auctions details via using the itemnum and mining Ebay
	 * @param itemnum The queried auction identifier that will be used to query the auction information
	 * @return True if no errors, False otherwise
	 * @throws Exception
	 */
	public static boolean getPageDetailsByItemNum(String itemnum) throws Exception{
		if(itemnum.length()!=12||Long.parseLong(itemnum)<0) return false;
		
		//Checks DB to ensure table exists
		 String dburl = "jdbc:mysql://localhost:3306/";
         String dbName = "ebay";
         String driver = "com.mysql.jdbc.Driver";
         String userName = MainMenu.databaseName;
         String password = MainMenu.databasePassword;
         try{
	      // This will load the MySQL driver, each DB has its own driver
	    	Class.forName(driver).newInstance();
	       
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
	       try{
		//The list of auction information parsed from read HTML
		 String title=null;
		 String leafCat = null;
		 String thumbLink=null; //Path name to thumbnail image
		 //A set of image path names that are stored with a '@@' as a demarcation for different item properties for extraction
		 String imageLink=null;
		 String itemCondition=null;
		 String itemLocation=null;
		//A set of item properties that are stored with a '@@' as a demarcation for different item properties for extraction
		 String itemSpecifics=null;
		 String sellerName=null;
		 String sellerRating=null;
		 String descriptionLink=null;
		 int imageCount=0;
		 boolean active=false;
		 boolean bid=false;
		 boolean buyNow=false;
		 String buyNowPrice=null;
		 boolean bestOffer=false;//Buy It Now, Bids, and/or Best Offer
		 boolean freeShipping=false;//Whether item has free shipping
		 String bidAmount=null;
		 boolean timedAuction=false;//Whether ends by time
		 boolean quantityAuction=false;//Whether ends by no quantity
		 String endTime=null;
		 String quantityLeft=null;//Implemented with 1,000 as Max Quantity
		 String dateEnded=null;
		  boolean sold=false;
		  boolean soldByBid=false;
		  String winningBid=null;
		 
		 //Creates/Checks if extension files directory exists
		File extFiles= new File("extFiles");
		if(!extFiles.exists()) extFiles.mkdir();
		//Constructs url to read from given itemnumber
		webpage = "http://www.ebay.com/itm/"+itemnum;
		//pass UA for HTML content
		BufferedReader reader = WebpageReaderWithAgent.read(webpage); 

		//Parses HTML as a string for easier expression matching
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		
		while (line != null) {
			sb.append( line ) ;
			line = reader.readLine(); 
		}
		//The patterns and Matchers to search auction information
		  Pattern titlePat = Pattern.compile("<h1 class=\"it-ttl\" itemprop=\"name\" id=\"itemTitle\"(?:><span class=\"[^\"]+?\">[^<]+?</span>|>)([^`]+?)</h1>"); //Auction Title Name
		  Pattern leafCategory=Pattern.compile("data-imageUrl=\"[^\"]+\" data-categories=\"(?:[0-9]+[,])*?([0-9]+)[,]\"");//Category ID Number
		  Pattern thumbImage=Pattern.compile("<img id=\"icImg\"[^~]+?(?=src)src=\"([^\"]+)\"");
		  Pattern auctionEnded=Pattern.compile("<span id=\"w1-3-_msg\" class=\"msgTextAlign\" >");
		  Pattern buyNowPat=Pattern.compile("(?:<span class=\"notranslate\" id=\"prcIsum\" itemprop=\"price\"  style=\"\">|<span class=\"notranslate\" id=\"mm-saleDscPrc\">)([^<]+?)</span>");
		  Pattern itemConditionPat=Pattern.compile("<meta name=\"twitter:data2\" content=\"([^\"]+?)\">");
		  Pattern itemLocationPat=Pattern.compile("(?:<div class=\"iti-eu-label u-flL\" id=\"\" style=\"\">[\\s]*Item location:</div><div class=\"iti-eu-bld-gry \">([^<]+)</div>|<div class=\"u-flL\">([^<]+?)</div>)");
		  Pattern bidPat=Pattern.compile("(?:<span class=\"notranslate\" id=\"prcIsum_bidPrice\" itemprop=\"price\">([^<]+)</span>|<div class=\"u-flL lable pdT4\" style=\"padding-top:3px;\">Starting bid:</div>[\\s]*<div class=\"u-flL w29 vi-price-np\"><span id=\"\" class=\"notranslate vi-VR-cvipPrice\">([^<]+)</span>)");
		  Pattern bestOfferPat=Pattern.compile("<span class=\"w2b-sgl\">Best offer available</span>");
		  Pattern freeShippingPat=Pattern.compile("Free</span> <span class=\"w2b-subhead\">Shipping");
		  Pattern itemSpecificsPat=Pattern.compile("<td class=\"attrLabels\">[\\s]*([^:]+?):[\\s]*</td>[\\s]*<td width=\"50.0%\">[\\s]*(?:<h2[^>]*?>|<h2[^>]*?><span[^>]+?>)([^<]+?)(?:</h2>|</span></h2>)");
		  Pattern imagePat=Pattern.compile("<td class=\"[^\"]*\" style=\"[^\"]*\">[\\s]*<img src=\"([^\"]+?)\" style");
		  Pattern sellerNamePat=Pattern.compile("(?:<h3 class=\"[^\"]*\">[\\s]*Seller information[\\s]*</h3>[\\s]*<div class=\"[^\"]*\">[\\s]*<div class=\"[^\"]*\">[\\s]*<a href=\"[^\"]*\" title=\"[^\"]*\">[\\s]*<span class=\"[^\"]*\">([^<]+?)</span></a>|<div class=\"mbg vi-VR-margBtm3\">[\\s]*<a href=\"[^\"]+?\" title=\"([^\"]+?)\")");
		  Pattern sellerRatingPat=Pattern.compile("[(]<a href=\"[^\"]*\" title=\"[^\"]*\">([^<]+?)</a>");
		  Pattern descriptionURLPat=Pattern.compile("(<div class=\"sh-cnt u-cb\">[^`]+?|<div id=\"desc_div\">[^`]+?)(?=<div class=\"asqMain\")");
		
		  
		  Matcher titleMatcher= titlePat.matcher(sb.toString());
		  Matcher leafCatMatcher=leafCategory.matcher(sb.toString());
		  Matcher thumbImageMatcher=thumbImage.matcher(sb.toString());
		  Matcher auctionEndedMatcher=auctionEnded.matcher(sb.toString());
		  Matcher buyNowMatcher=buyNowPat.matcher(sb.toString());
		  Matcher itemConditionMatcher=itemConditionPat.matcher(sb.toString());
		  Matcher itemLocationMatcher= itemLocationPat.matcher(sb.toString());
		  Matcher bidMatcher=bidPat.matcher(sb.toString());
		  Matcher bestOfferMatcher=bestOfferPat.matcher(sb.toString());
		  Matcher freeShippingMatcher=freeShippingPat.matcher(sb.toString());
		  Matcher itemSpecificsMatcher=itemSpecificsPat.matcher(sb.toString());
		  Matcher imageMatcher=imagePat.matcher(sb.toString());
		  Matcher sellerNameMatcher=sellerNamePat.matcher(sb.toString());
		  Matcher sellerRatingMatcher=sellerRatingPat.matcher(sb.toString());
		  Matcher descriptionURLMatcher=descriptionURLPat.matcher(sb.toString());
		  
		  titleMatcher.find();
		  sellerNameMatcher.find();
		  sellerRatingMatcher.find();
		  sellerName=(sellerNameMatcher.group(1)==null)?sellerNameMatcher.group(2):sellerNameMatcher.group(1) ;
		  sellerRating=sellerRatingMatcher.group(1);
		  while(leafCatMatcher.find())   leafCat=leafCatMatcher.group(1);
		  thumbImageMatcher.find();
		 //Condition not Available
		  itemCondition=(!itemConditionMatcher.find()) ? "N/A" : itemConditionMatcher.group(1);
		  itemLocationMatcher.find();
		  
		  //Build itemSpecifics String and initialize to string other than null if found at least once
		  while(itemSpecificsMatcher.find()){
			  if(itemSpecifics==null)itemSpecifics="";
			  itemSpecifics+=itemSpecificsMatcher.group(1)+": "+itemSpecificsMatcher.group(2)+"@@";
		  }
		  
		  //Construct the list of URLs that will be used to extract the images
		  String imageURL=null;
		  while(imageMatcher.find()){
			  imageCount++;
			  if(imageCount==1)imageURL="";
			  imageURL+=imageMatcher.group(1)+"@@";
		  }
		  bestOffer=bestOfferMatcher.find();
		  freeShipping=freeShippingMatcher.find();
		  active=!auctionEndedMatcher.find();
		  //If Buy Now Auction
		  if(buyNowMatcher.find()){
			  buyNow=true;
			  buyNowPrice=buyNowMatcher.group(1);
		  }
		  //If Bid Type Auction
		  if(bidMatcher.find()){
			  bid=true;
			  //If already over need new bid checker pattern
			  bidAmount=(bidMatcher.group(1)==null)?bidMatcher.group(2):bidMatcher.group(1);
		  }
		  //If Auction is still active
		  if(active){
			  Pattern timedAuctionPat=Pattern.compile("<span class=\"vi-tm-left\" id=\"vi-cdown_timeLeft\">[\\s]*<span>[(]([^<]+?)</span>[\\s]*<span class=\"endedDate\">([^<]+?)[)]</span>");
			  Pattern quantityAuctionPat=Pattern.compile("<span id=\"qtySubTxt\">[\\s]*<span class=\"\">[\\s]*([^<]+?)</span>");
			  
			  Matcher timedAuctionMatcher=timedAuctionPat.matcher(sb.toString());
			  Matcher quantityAuctionMatcher=quantityAuctionPat.matcher(sb.toString());

			  //Auction ends by time
			  if(timedAuctionMatcher.find()){
				 
				  timedAuction=true;
				  endTime=timedAuctionMatcher.group(1)+" "+timedAuctionMatcher.group(2).replace(" PST", "");
				  
			  }
			  
			  //Auction ends by Quantity
			  if(quantityAuctionMatcher.find()){
				  quantityAuction=true;
				  Pattern quantityLeftPat=Pattern.compile("<div aria-live=\"assertive\"  role=\"alert\" id=\"shQuantity-errTxt\" class=\"sh-err-text sh-err-hide\">There are (\\d+) items");
				  Matcher quantityLeftMatcher=quantityLeftPat.matcher(sb.toString());
				  if(quantityLeftMatcher.find()){
				  quantityLeft=quantityLeftMatcher.group(1);
				  }
				  else quantityLeft=quantityAuctionMatcher.group(1).replace(" available", "");
			  }
		  }
		  
		  
		  
		  //Else if Auction has ended
		  else{
			  
			  Pattern dateEndedPat=Pattern.compile("<span id=\"bb_tlft\">[\\s]*([^<]+?)<span class=\"endedDate\">[\\s]*<span>([^<]+?)</span>");
			  Pattern soldPat=Pattern.compile("alt=\"Item Sold\"");
			  Pattern winningBidPat=Pattern.compile("Winning bid:</div>[\\s]*<div class=\"[^\"]*\"><span id=\"[^\"]*\" class=\"[^\"]*\">([^<]+)</span>");
			  
			  Matcher dateEndedMatcher=dateEndedPat.matcher(sb.toString());
			  Matcher soldMatcher=soldPat.matcher(sb.toString());
			  Matcher winningBidMatcher=winningBidPat.matcher(sb.toString());
			  
			  dateEndedMatcher.find();
			  
			  //If Auction ended by being Sold
			  if(soldMatcher.find()){
				  sold=true;
			  //If Auction was sold by winning bid
			  if(winningBidMatcher.find()){
				  soldByBid=true;
				  winningBid=winningBidMatcher.group(1);
			  }
			  dateEnded=dateEndedMatcher.group(1)+" "+dateEndedMatcher.group(2).replace(" PST", "");
		  }
		  }
		  
		  // Writes Images to file extension folder, with main images having a subscript picture number format
		  BufferedImage image = null;
	        URL url = new URL(thumbImageMatcher.group(1).replace("$_12", "$_14"));
		  image = ImageIO.read(url);
		  ImageIO.write(image,"jpg",new File(System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_thumb.jpg"));
		  for(int count=0;count<imageCount;count++){
			  url= new URL(imageURL.split("@@")[count].substring(0, imageURL.split("@@")[count].length()-8)+"$_12.jpg");
			  image=ImageIO.read(url);
			  ImageIO.write(image, "jpg",new File(System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_"+count+".jpg"));
			  if(count==0)imageLink="";
			  imageLink+=System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_"+count+".jpg@@";
		  }
		  
		  //If contains no extra photos create a higher res version of Thumbnail
		  if(imageCount==0){
			  imageCount++;
			url=new URL(thumbImageMatcher.group(1));
			image=ImageIO.read(url);
			ImageIO.write(image, "jpg",new File(System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_0.jpg"));
			imageLink=System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_0.jpg";
		  }
		  
		  descriptionURLMatcher.find();
		  //Need to parse separate page for description
		  //pass UA for HTML content
		
		  ReadableByteChannel rbc = Channels.newChannel(new ByteArrayInputStream(descriptionURLMatcher.group(1).getBytes("UTF-8")));
		  FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_description.html");
		  descriptionLink=System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_description.html";
		  fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		  fos.close();
		  
		  title=titleMatcher.group(1).replace("&amp;", "&").replace("&#034;","\"").replace("<wbr/>", "").replace("&#039;", "'");
		  //Item location is outside US
		  itemLocation=(itemLocationMatcher.group(1)==null) ? itemLocationMatcher.group(2) : itemLocationMatcher.group(1);
		  thumbLink=System.getProperty("user.dir")+"\\"+extFiles+"\\"+itemnum+"_thumb.jpg";
		  
		  //Now inserts the auction into the database
		  Connection connect = DriverManager.getConnection(dburl+dbName,userName,password);
		    	  // PreparedStatements can use variables and are more efficient
		  
		    	PreparedStatement  preparedStatement = connect.prepareStatement("replace into  ebay.auction (itemNum,title,category,thumbLink,imageLink,itemCondition,itemLocation,itemSpecifics,sellerName,sellerRating,descriptionLink,imageCount,active,bid,buyNow,bestOffer,freeShipping,buyNowPrice,bidAmount,timedAuction,quantityAuction,endTime,quantityLeft,dateEnded,sold,soldByBid,winningBid,timeStamp) "
			        		  +"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			      preparedStatement.setString(1, itemnum);
			      preparedStatement.setString(2, title);
			      preparedStatement.setInt(3, Integer.parseInt(leafCat));
			      preparedStatement.setString(4, thumbLink);
			      preparedStatement.setString(5, imageLink);
			      if(itemCondition==null)preparedStatement.setNull(6, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(6, itemCondition); 
			      if(itemLocation==null)preparedStatement.setNull(7, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(7, itemLocation); 
			      if(itemSpecifics==null)preparedStatement.setNull(8, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(8, itemSpecifics); 
			      if(sellerName==null)preparedStatement.setNull(9, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(9, sellerName); 
			      if(sellerRating==null)preparedStatement.setNull(10, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(10, sellerRating); 
			      if(descriptionLink==null)preparedStatement.setNull(11, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(11, descriptionLink); 
			      preparedStatement.setInt(12, imageCount);
			      preparedStatement.setBoolean(13, active);
			      preparedStatement.setBoolean(14, bid);
			      preparedStatement.setBoolean(15, buyNow);
			      preparedStatement.setBoolean(16, bestOffer);
			      preparedStatement.setBoolean(17, freeShipping);
			      if(buyNowPrice==null)preparedStatement.setNull(18, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(18,buyNowPrice);  
			      if(bidAmount==null)preparedStatement.setNull(19, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(19, bidAmount);  
			      preparedStatement.setBoolean(20, timedAuction);
			      preparedStatement.setBoolean(21, quantityAuction);
			      if(endTime==null)preparedStatement.setNull(22, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(22, endTime); 
			      if(quantityLeft==null)preparedStatement.setNull(23, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(23, quantityLeft); 
			      if(dateEnded==null)preparedStatement.setNull(24, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(24, dateEnded); 
			      preparedStatement.setBoolean(25, sold);
			      preparedStatement.setBoolean(26, soldByBid);
			      if(winningBid==null)preparedStatement.setNull(27, java.sql.Types.VARCHAR);
			      else preparedStatement.setString(27, winningBid); 
			      java.util.Date utilDate = new java.util.Date();
			      java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
			      preparedStatement.setDate(28, sqlDate);
			      preparedStatement.executeUpdate();
			     
			    //Closes Database connection
			      if (connect != null) {
			        connect.close();
			      }
	       }
		      catch(Exception e){e.printStackTrace();}
			      displayResultsByKey(itemnum);
	return true;
	}
	/**
	 * The method will return to GUI all of the information from the DB regarding the queried itemNum auction
	 * @param itemNum The DB identifier for the queried auction
	 * @throws Exception
	 */
	public static void displayResultsByKey(String itemNum) throws Exception{
		 String dburl = "jdbc:mysql://localhost:3306/";
         String dbName = "ebay";
         String driver = "com.mysql.jdbc.Driver";
         String userName = MainMenu.databaseName;
         String password = MainMenu.databasePassword;
        
	      // This will load the MySQL driver, each DB has its own driver
	    	Class.forName(driver).newInstance();
	
	      // Setup the connection with the DB
	    	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
		      Statement stmt=connect.createStatement();
		   ResultSet rs= stmt.executeQuery("Select * from ebay.auction where itemNum="+itemNum);
		      rs.next();
		      //Set up GUI display for query
		    MainMenu.itemNumberLbl.setText(rs.getString(1));
		    MainMenu.titleLabel.setText(rs.getString(2));
		    //leafCat tree
		    String currCat=rs.getString(3);
		    //Set up Thumbnail image
		    ImageIcon tempimage=new ImageIcon(rs.getString(4).replace("\\", "\\"+"\\"));
		    if(tempimage.getIconHeight()>600|tempimage.getIconWidth()>600){
		    tempimage=new ImageIcon(tempimage.getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT));
		    }
	        MainMenu.thumbImageLbl.setIcon(tempimage);
	        MainMenu.thumbImageLbl.setBounds(27, 23, tempimage.getIconWidth(), tempimage.getIconHeight());
	        MainMenu.images=Integer.parseInt(rs.getString(12));
		    //Set up Image Gallery
	        MainMenu.imageArray= new String[MainMenu.images];
	        for(int position=0;position<MainMenu.images;position++){
	        	MainMenu.imageArray[position]=rs.getString(5).split("@@")[position].replace("\\", "\\"+"\\");
	        }
	        tempimage= new ImageIcon(MainMenu.imageArray[0]);
	        MainMenu.currentimage=0;
	        if(tempimage.getIconHeight()>600|tempimage.getIconWidth()>600){
	        	 tempimage=new ImageIcon(tempimage.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT));
	        }
	        MainMenu.imageLbl.setIcon(tempimage);
	        MainMenu.imageLbl.setBounds(MainMenu.imageLbl.getX(), MainMenu.imageLbl.getY(), tempimage.getIconWidth(), tempimage.getIconHeight());
		    MainMenu.itemConditionLbl.setText(rs.getString(6));
		    MainMenu.itemLocationLbl.setText(rs.getString(7));
		    String html=rs.getString(8);
		    if(html!=null)html=html.replace("@@", "<br>");
		    else html="N/A";
		    MainMenu.itemSpecificsLbl.setText("<html>"+html+"</html>");
		    MainMenu.sellerNameLbl.setText(rs.getString(9));
		    MainMenu.sellerRatingLbl.setText(rs.getString(10));
		    //Setup Description Panel
		    File descriptFile= new File(rs.getString(11).replace("\\", "\\"+"\\"));
		    MainMenu.descriptionLbl.setPage(descriptFile.toURI().toURL());
		    MainMenu.activeLbl.setText(Boolean.toString(rs.getBoolean(13)));
		    MainMenu.BidLbl.setText(Boolean.toString(rs.getBoolean(14)));
		    MainMenu.buyNowLbl.setText(Boolean.toString(rs.getBoolean(15)));
		    MainMenu.bestOfferLbl.setText(Boolean.toString(rs.getBoolean(16)));
		    MainMenu.freeShippingLbl.setText(Boolean.toString(rs.getBoolean(17)));
		    MainMenu.buyNowPriceLbl.setText(rs.getString(18));
		    MainMenu.bidAmountLbl.setText(rs.getString(19));
		    MainMenu.timedAuctionLbl.setText(Boolean.toString(rs.getBoolean(20)));
		    MainMenu.quantityAuctionLbl.setText(Boolean.toString(rs.getBoolean(21)));
		    MainMenu.endTimeLbl.setText(rs.getString(22));
		    MainMenu.quantityLeftLbl.setText(rs.getString(23));
		    MainMenu.dateEndedLbl.setText(rs.getString(24));
		    MainMenu.soldLbl.setText(Boolean.toString(rs.getBoolean(25)));
		    MainMenu.soldByBidLbl.setText(Boolean.toString(rs.getBoolean(26)));
		    MainMenu.winningBidLbl.setText(rs.getString(27));
		    
		    rs.close();
		    //Construct Tree
		    Stack<String> catList= new Stack<String>();
		    while(currCat!=null){
		    ResultSet rs2=stmt.executeQuery("Select name,parent from ebay.category where id="+currCat);
		    rs2.next();
		    catList.push(rs2.getString(1));
		    currCat=rs2.getString(2);
		    rs2.close();
		    }
	    	connect.close();
	       
	    	DefaultMutableTreeNode root = new DefaultMutableTreeNode(catList.pop());
	    	DefaultMutableTreeNode parentNode=root;
	    	while(!catList.empty()){
            DefaultMutableTreeNode node=new DefaultMutableTreeNode(catList.pop());
            parentNode.add(node);
            parentNode=node;
	    	}
	       DefaultTreeModel model=new DefaultTreeModel(root);
	       MainMenu.categoryTree.setModel(model);
	       MainMenu.categoryTree.setRootVisible(true);
	}
	
	/**
	 * This method will parse the supplied URL and send it to the getPageDetailsByURL 
	 * with the supplied URL's itemnum for parsing auction information
	 * @param URL The queried URL for ebay parsing
	 * @return True if no error, false otherwise
	 * @throws Exception
	 */
	public static boolean getPageDetailsByURL(String URL) throws Exception{
		webpage=URL;
		//pass UA for HTML content
				BufferedReader reader = WebpageReaderWithAgent.read(webpage); 
				//Parses HTML as a string for easier expression matching
				StringBuilder sb = new StringBuilder();
				String line = reader.readLine();
				
				while (line != null) {
					sb.append( line ) ;
					line = reader.readLine(); 
				}
				//Pattern to find the itemnumber
				Pattern itemNumPat=Pattern.compile("eBay item number:</div><div class=\"u-flL iti-act-num\">([^<]+?)</div>");
				Matcher itemNumMatcher=itemNumPat.matcher(sb.toString());
				//Incorrect URL format
				if(!itemNumMatcher.find()){
					return false;
				}
				//Pass to item number parsing method
				else{
					getPageDetailsByItemNum(itemNumMatcher.group(1));
					return true;
				}
				
	}


}
