package EbayCrawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * This will initialize the MySQL database tables
 * @author Natan Ritholtz
 *
 */
public class Initialize {
	 private static Connection connect = null;
	
	public static void createTables() throws Exception{
		 String dburl = "jdbc:mysql://localhost:3306/";
	     String driver = "com.mysql.jdbc.Driver";
	     String userName = MainMenu.databaseName;
	     String password = MainMenu.databasePassword;
		// This will load the MySQL driver, each DB has its own driver
    	Class.forName(driver).newInstance();
       
      // Setup the connection with the DB
    	  connect = DriverManager.getConnection(dburl,userName,password);
    	  Statement statement = connect.createStatement();
          // create the DBs .. 
    	  statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS `ebay`");
    	  statement.executeUpdate("CREATE TABLE  IF NOT EXISTS `ebay`.`category` ( "
			  +"`id` INT(11) NOT NULL, "
			  +"`name` VARCHAR(45) NOT NULL, "
			  +"`level` INT(11) NOT NULL, "
			  +"`parent` INT(11) NULL DEFAULT NULL, "
			  +"PRIMARY KEY (`id`), "
			  +"INDEX `parent_idx` (`parent` ASC), "
			  +"CONSTRAINT `parent` "
			    +"FOREIGN KEY (`parent`) "
			    +"REFERENCES `ebay`.`category` (`id`) "
			    +"ON DELETE CASCADE "
			    +"ON UPDATE CASCADE) "
			+"ENGINE = InnoDB "
			+"DEFAULT CHARACTER SET = utf8" );
    	  statement.executeUpdate("CREATE TABLE IF NOT EXISTS `ebay`.`auction` ("
  +"`itemNum` VARCHAR(30) NOT NULL PRIMARY KEY,"
  +"`title` VARCHAR(180) NOT NULL,"
  +"`category` INT NOT NULL,"
  +"`thumbLink` VARCHAR(300) NOT NULL,"
  +"`imageLink` VARCHAR(12000) NOT NULL,"
  +"`itemCondition` VARCHAR(45) NOT NULL,"
  +"`itemLocation` VARCHAR(45) NULL DEFAULT 'N/A',"
  +"`itemSpecifics` VARCHAR(1000) NULL DEFAULT 'N/A',"
  +"`sellerName` VARCHAR(45) NOT NULL,"
  +"`sellerRating` VARCHAR(45) NOT NULL,"
  +"`descriptionLink` VARCHAR(180) NOT NULL,"
  +"`imageCount` INT NULL DEFAULT 0,"
  +"`active` TINYINT NULL DEFAULT 0,"
  +"`bid` TINYINT NULL DEFAULT 0,"
  +"`buyNow` TINYINT NULL DEFAULT 0,"
  +"`bestOffer` TINYINT NULL DEFAULT 0,"
  +"`freeShipping` TINYINT NULL DEFAULT 0,"
  +"`buyNowPrice` VARCHAR(50) NULL DEFAULT NULL,"
  +"`bidAmount` VARCHAR(50) NULL DEFAULT NULL,"
  +"`timedAuction` TINYINT NULL DEFAULT NULL,"
  +"`quantityAuction` TINYINT NULL DEFAULT NULL,"
  +"`endTime` VARCHAR(45) NULL DEFAULT NULL,"
  +"`quantityLeft` VARCHAR(45) NULL DEFAULT NULL,"
  +"`dateEnded` VARCHAR(45) NULL DEFAULT NULL,"
  +"`sold` TINYINT NULL DEFAULT NULL,"
  +"`soldByBid` TINYINT NULL DEFAULT NULL,"
  +"`winningBid` VARCHAR(50) NULL DEFAULT NULL,"
  +"`timeStamp` DATE NOT NULL,"
  +"INDEX `category_idx` (`category` ASC),"
  +"CONSTRAINT `category`"
   +" FOREIGN KEY (`category`)"
   +" REFERENCES `ebay`.`category` (`id`)"
   +" ON DELETE CASCADE"
    +" ON UPDATE CASCADE) "); 
    	  
    	  //Will insert cached version of categories for initial setup
    	  ResultSet rs=  statement.executeQuery("select count(*) from ebay.category");
    	  rs.next();
    	  if(rs.getInt(1)<1){
    	  statement.executeUpdate("set foreign_key_checks=0");
    	  String filename=(System.getProperty("user.dir")+"\\cache.csv").replace("\\", "/");
    	  statement.executeUpdate("LOAD DATA LOCAL INFILE \""+filename+"\" INTO TABLE ebay.category  FIELDS TERMINATED BY ';' (id, name, level, parent)");
    	  statement.executeUpdate("Delete from ebay.category where id=0");
    	  statement.executeUpdate("set foreign_key_checks=1");
    	  }
    	  rs.close();
    	  connect.close();
	}

}
