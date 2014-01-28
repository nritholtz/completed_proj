 
package EbayCrawler;
 

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;


 

/**
 * The GUI and Main functionality inteface of the program
 * @author Natan Ritholtz
 *
 */

public class MainMenu {
	public static String databaseName,databasePassword; //Global variables for the database connections
	static JButton update;
	static JButton cancelUpdate;
	static JButton previousButton,nextButton,queryButton,previousPageButton,nextPageButton,previousResultPageButton,nextResultPageButton,previousSearchButton;
    public static int timerTick,images,currentimage,currentpage,resultpage;
    public static String[] imageArray; //String array used to store the images pathways
    static Timer timer; //Timer used to update progress bar
    static JProgressBar updatePB;
    public static  JLabel updateLabel;
    final static String INITPANEL = "Initialize";
    final static String UPDATEPANEL = "Update Categories";
    final static int extraWindowWidth = 100;
    private static JFrame frame;
    private static JTextField textField;
    //Fields for Search by id/url
    static JButton searchButton;
    public static JLabel dateEndedLbl,itemNumberLbl,titleLabel,thumbImageLbl,itemConditionLbl,itemSpecificsLbl,activeLbl,BidLbl,bidAmountLbl,buyNowLbl,buyNowPriceLbl,bestOfferLbl,timedAuctionLbl,endTimeLbl,quantityAuctionLbl,quantityLeftLbl,freeShippingLbl,soldLbl,soldByBidLbl,winningBidLbl,searchLbl;
    public static JLabel sellerNameLbl,sellerRatingLbl,itemLocationLbl,imageLbl,queryLbl,previousLbl;
    public static JTree categoryTree,queryTree;
    public static JEditorPane descriptionLbl;
    //Fields for search by keyword
    public static JTextField queryText;
    public static JScrollPane scrollPane_2;
    public static JComboBox<TreeNode> sopLbl, previousBox;
   public static JRadioButton allListRB,buyItNowRB,auctionRB;
    static ButtonGroup bg;
    public static JTable queryTable;
    static  JTabbedPane tabbedPane;
   public static String[] colNames={"Item Number","Title"}; //The column names for the JTable that displays the query results
   public static String[] prevNames={"Item Number", "Title", "Time Stamp"};
   public static String currKeyword,currCatNum;
   public static int searchNum;
   //Fields for search previous results 
   private static JTextField previousSearchText;
   public static JTable previousTable;
   private JLabel lblLimitDates;
   public static JButton fromDateButton,toDateButton;
   public static JFormattedTextField fromDate, toDate;

   
   //A listener for any buttons pressed
    class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ae) {

        	try{
        	//If update Categories button is pushed, getCategories and start Progress Bar
        	 if(ae.getSource()==update){
        		              
        		updateWorker worker= new updateWorker();
                          	worker.execute();
          					       				
                      }
                  
        	
        	
        	//If update categories cancel button is pushed, send GetCategories a flag to cancel ASAP
        	else if(ae.getSource()==cancelUpdate){
        		String str = "<html>" + "<font color=\"#FF0000\">" + "<b>" + 
                		  "Cancelling...." + "</b>" + "</font>" + "</html>";
          		cancelUpdate.setEnabled(false);
                  updateLabel.setText(str);
                  timer.stop();
                  timerTick=-1;
        	}
        	 //If search by ID pushed
        	else if(ae.getSource()==searchButton){
        		searchWorker worker= new searchWorker();
        		worker.execute();
        		
        	}
        	else if(ae.getSource()==previousButton){
        		previousWorker worker= new previousWorker();
              	worker.execute();
        	}
        	else if(ae.getSource()==nextButton){
        		nextWorker worker= new nextWorker();
              	worker.execute();
        	}
        	else if(ae.getSource()==queryButton){
        		queryWorker worker=new queryWorker();
        		worker.execute();
        		
        	}
        	else if (ae.getSource()==previousPageButton){
        		previousPageWorker worker = new previousPageWorker();
        		worker.execute();
        	}
        	else if(ae.getSource()==nextPageButton){
        		nextPageWorker worker= new nextPageWorker();
        		worker.execute();
        	}
        	else if (ae.getSource()==nextResultPageButton){
        		nextResultPageWorker worker = new nextResultPageWorker();
        		worker.execute();
        		
        	}
        	else if (ae.getSource()==previousSearchButton){
        		previousSearch worker= new previousSearch();
        		worker.execute();
        	}
        	else if (ae.getSource()==fromDateButton){
        		fromDate.setText(new DateSelector(frame).setPickedDate("from"));
        	}
        	else if (ae.getSource()==toDateButton){
        		toDate.setText(new DateSelector(frame).setPickedDate("to"));
        	}
        	} catch (Exception e) {
					e.printStackTrace();
				}
        	
        }
    }
    

        /**
         * Creates the Window Components for the Program's JFrame including the Tab Structure
         * @param pane The created JFrame for this menu's instance
         */
    
    public void addComponentToPane(Container pane) {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Traditional Arabic", Font.PLAIN, 14));
      
       //Create Update Panel
        JPanel card2 = new JPanel();
        update = new JButton("Update");
        update.addActionListener(new ButtonListener());
        cancelUpdate= new JButton("Cancel");
        cancelUpdate.addActionListener(new ButtonListener());
  
        updatePB = new JProgressBar(0, 100);
        updatePB.setValue(0);
        updatePB.setStringPainted(true);

        updateLabel = new JLabel();
    
    JPanel updatePanel = new JPanel();
    updatePanel.setLayout(new BorderLayout());
        card2.add(update);
        card2.add(cancelUpdate);
        card2.add(updatePB);
        
        
        card2.add(updatePanel, BorderLayout.NORTH);
    card2.add(updateLabel, BorderLayout.CENTER);
    card2.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
      
    cancelUpdate.setEnabled(false);
 
    	
    	//The refresh of the bar via a timer and check for progress done
         timer = new Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
        if (timerTick == 100){
          timer.stop();
          update.setEnabled(true);
          cancelUpdate.setEnabled(false);
          updatePB.setValue(0);
          String str = "<html>" + "<font color=\"#0000FF\">" + "<b>" + 
        		  "Finished in "+((double)(System.currentTimeMillis()-updateWorker.currtime)/60000.0)+" minutes." + "</b>" + "</font>" + "</html>";
          updateLabel.setText(str);
        }
        updatePB.setValue(timerTick);
            }
        });
        
         tabbedPane.addTab(UPDATEPANEL, card2);
        
         //Creates search by Auction Panel with its corresponding UI elements
        JPanel card3 = new JPanel();
        tabbedPane.addTab("Search by Auction", null, card3, null);
        card3.setLayout(null);
        
        JLabel searchID = new JLabel("Enter Auction ID or Auction URL:");
        searchID.setFont(searchID.getFont().deriveFont(searchID.getFont().getStyle() | Font.BOLD));
        searchID.setBounds(52, 15, 218, 14);
        card3.add(searchID);
        
        textField = new JTextField();
        textField.setBounds(230, 11, 226, 30);
        card3.add(textField);
        textField.setColumns(10);
        
         searchButton = new JButton("Search");
        searchButton.addActionListener(new ButtonListener());
        searchButton.setBounds(451, 10, 100, 23);
        card3.add(searchButton);
        
        //A border panel for UI effect
        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        borderPanel.setBounds(4, 45, 1265, 585);
        card3.add(borderPanel);
        borderPanel.setLayout(null);
        
        JTabbedPane auctionDisplay = new JTabbedPane(JTabbedPane.TOP);
        auctionDisplay.setBounds(6, 0, 1259, 585);
        borderPanel.add(auctionDisplay);
        
        //Panel with General auction info
        JPanel generalInfo = new JPanel();
        auctionDisplay.addTab("General Information", null, generalInfo, null);
        generalInfo.setLayout(null);
        
        JLabel lblItemNumber = new JLabel("Item Number");
        lblItemNumber.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblItemNumber.setBounds(475, 23, 89, 14);
        generalInfo.add(lblItemNumber);
        
         itemNumberLbl = new JLabel();
        itemNumberLbl.setBounds(556, 23, 117, 14);
        generalInfo.add(itemNumberLbl);
        
        JLabel lblTitle = new JLabel("Title");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTitle.setBounds(475, 48, 67, 14);
        generalInfo.add(lblTitle);
         
         JScrollPane scrollPane_1 = new JScrollPane();
         scrollPane_1.setViewportBorder(null);
         scrollPane_1.setBounds(556, 39, 389, 44);
         generalInfo.add(scrollPane_1);
        
         titleLabel = new JLabel();
         titleLabel.setVerticalAlignment(SwingConstants.TOP);
         scrollPane_1.setViewportView(titleLabel);
        
         thumbImageLbl = new JLabel();
        thumbImageLbl.setIcon(new ImageIcon());
        thumbImageLbl.setBounds(27, 23, 300, 300);
        generalInfo.add(thumbImageLbl);
        
        JLabel lblItemCondition = new JLabel("Item Condition");
        lblItemCondition.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblItemCondition.setBounds(475, 94, 89, 14);
        generalInfo.add(lblItemCondition);
        
         itemConditionLbl = new JLabel();
        itemConditionLbl.setBounds(574, 94, 371, 14);
        generalInfo.add(itemConditionLbl);
        
        JLabel lblItemSpecifics = new JLabel("Item Specifics");
        lblItemSpecifics.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblItemSpecifics.setBounds(475, 119, 107, 14);
        generalInfo.add(lblItemSpecifics);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(null);
        scrollPane.setBounds(574, 115, 371, 95);
        generalInfo.add(scrollPane);
        
         itemSpecificsLbl = new JLabel();
        itemSpecificsLbl.setVerticalAlignment(SwingConstants.TOP);
        scrollPane.setViewportView(itemSpecificsLbl);
        
        JLabel lblActive = new JLabel("Active");
        lblActive.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblActive.setBounds(475, 238, 89, 14);
        generalInfo.add(lblActive);
        
         activeLbl = new JLabel();
        activeLbl.setBounds(556, 238, 117, 14);
        generalInfo.add(activeLbl);
        
        JLabel lblBid = new JLabel("Bid Type Auction");
        lblBid.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblBid.setBounds(475, 263, 123, 14);
        generalInfo.add(lblBid);
        
         BidLbl = new JLabel();
        BidLbl.setBounds(590, 263, 160, 14);
        generalInfo.add(BidLbl);
        
        JLabel lblBidCurrentPrice = new JLabel("Bid Current Price");
        lblBidCurrentPrice.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblBidCurrentPrice.setBounds(475, 288, 123, 14);
        generalInfo.add(lblBidCurrentPrice);
        
         bidAmountLbl = new JLabel();
        bidAmountLbl.setBounds(590, 288, 160, 14);
        generalInfo.add(bidAmountLbl);
        
        JLabel lblBuyNowAuction = new JLabel("Buy Now Auction");
        lblBuyNowAuction.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblBuyNowAuction.setBounds(475, 313, 123, 14);
        generalInfo.add(lblBuyNowAuction);
        
         buyNowLbl = new JLabel();
        buyNowLbl.setBounds(590, 313, 160, 14);
        generalInfo.add(buyNowLbl);
        
        JLabel lblBuyNowPrice = new JLabel("Buy Now Price");
        lblBuyNowPrice.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblBuyNowPrice.setBounds(475, 338, 123, 14);
        generalInfo.add(lblBuyNowPrice);
        
         buyNowPriceLbl = new JLabel();
        buyNowPriceLbl.setBounds(590, 338, 160, 14);
        generalInfo.add(buyNowPriceLbl);
        
        JLabel lblBestOfferAvailable = new JLabel("Best Offer Available");
        lblBestOfferAvailable.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblBestOfferAvailable.setBounds(475, 363, 123, 14);
        generalInfo.add(lblBestOfferAvailable);
        
         bestOfferLbl = new JLabel();
        bestOfferLbl.setBounds(590, 363, 160, 14);
        generalInfo.add(bestOfferLbl);
         
        //Set up tree for Category View
        JScrollPane treePane = new JScrollPane();
        DefaultMutableTreeNode dummy= new DefaultMutableTreeNode("");
        treePane.setBounds(104, 395, 235, 151);
        generalInfo.add(treePane);
         categoryTree = new JTree(dummy);
        categoryTree.setBackground(UIManager.getColor("Button.background"));
        treePane.setViewportView(categoryTree);
        categoryTree.setRootVisible(false);
        categoryTree.setShowsRootHandles(true);
    
        JLabel lblCategoryHierarchy = new JLabel("Category Hierarchy");
        lblCategoryHierarchy.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblCategoryHierarchy.setBounds(89, 371, 123, 14);
        generalInfo.add(lblCategoryHierarchy);
        
        JLabel lblTimedAuction = new JLabel("Timed Auction");
        lblTimedAuction.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblTimedAuction.setBounds(475, 388, 123, 14);
        generalInfo.add(lblTimedAuction);
        
         timedAuctionLbl = new JLabel();
        timedAuctionLbl.setBounds(590, 388, 160, 14);
        generalInfo.add(timedAuctionLbl);
        
        JLabel lblEndingAt = new JLabel("Ending At");
        lblEndingAt.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblEndingAt.setBounds(475, 413, 123, 14);
        generalInfo.add(lblEndingAt);
        
         endTimeLbl = new JLabel();
        endTimeLbl.setBounds(590, 413, 160, 14);
        generalInfo.add(endTimeLbl);
        
        JLabel lblQuantityAuction = new JLabel("Quantity Auction");
        lblQuantityAuction.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblQuantityAuction.setBounds(475, 438, 123, 14);
        generalInfo.add(lblQuantityAuction);
        
         quantityAuctionLbl = new JLabel();
        quantityAuctionLbl.setBounds(590, 438, 160, 14);
        generalInfo.add(quantityAuctionLbl);
        
        JLabel lblQuantityLeft = new JLabel("Quantity Left");
        lblQuantityLeft.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblQuantityLeft.setBounds(475, 463, 123, 14);
        generalInfo.add(lblQuantityLeft);
        
         quantityLeftLbl = new JLabel();
        quantityLeftLbl.setBounds(590, 463, 160, 14);
        generalInfo.add(quantityLeftLbl);
        
        JLabel lblFreeShipping = new JLabel("Free Shipping");
        lblFreeShipping.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblFreeShipping.setBounds(757, 238, 123, 14);
        generalInfo.add(lblFreeShipping);
        
         freeShippingLbl = new JLabel();
        freeShippingLbl.setBounds(872, 238, 160, 14);
        generalInfo.add(freeShippingLbl);
        
        JLabel lblSold = new JLabel("Sold");
        lblSold.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSold.setBounds(760, 263, 89, 14);
        generalInfo.add(lblSold);
        
         soldLbl = new JLabel();
        soldLbl.setBounds(872, 263, 117, 14);
        generalInfo.add(soldLbl);
        
        JLabel lblSoldByBid = new JLabel("Sold By Bid");
        lblSoldByBid.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSoldByBid.setBounds(760, 288, 123, 14);
        generalInfo.add(lblSoldByBid);
        
         soldByBidLbl = new JLabel();
        soldByBidLbl.setBounds(875, 288, 160, 14);
        generalInfo.add(soldByBidLbl);
        
        JLabel lblWinningBid = new JLabel("Winning Bid");
        lblWinningBid.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblWinningBid.setBounds(757, 313, 123, 14);
        generalInfo.add(lblWinningBid);
        
         winningBidLbl = new JLabel();
        winningBidLbl.setBounds(872, 313, 160, 14);
        generalInfo.add(winningBidLbl);
        
        JLabel lblEndedAt = new JLabel("Ended At");
        lblEndedAt.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblEndedAt.setBounds(757, 338, 123, 14);
        generalInfo.add(lblEndedAt);
        
        dateEndedLbl = new JLabel();
        dateEndedLbl.setBounds(872, 338, 160, 14);
        generalInfo.add(dateEndedLbl);
        
        //A panel with seller info
        JPanel sellerInfo = new JPanel();
        auctionDisplay.addTab("Seller Information", null, sellerInfo, null);
        sellerInfo.setLayout(null);
        
        JLabel lblSellerName = new JLabel("Seller Name");
        lblSellerName.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSellerName.setBounds(74, 66, 123, 14);
        sellerInfo.add(lblSellerName);
        
         sellerNameLbl = new JLabel();
        sellerNameLbl.setBounds(189, 66, 160, 14);
        sellerInfo.add(sellerNameLbl);
        
        JLabel lblSellerRating = new JLabel("Seller Rating");
        lblSellerRating.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSellerRating.setBounds(74, 135, 123, 14);
        sellerInfo.add(lblSellerRating);
        
         sellerRatingLbl = new JLabel();
        sellerRatingLbl.setBounds(189, 135, 160, 14);
        sellerInfo.add(sellerRatingLbl);
        
        JLabel lblItemLocation = new JLabel("Item Location");
        lblItemLocation.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblItemLocation.setBounds(74, 224, 123, 14);
        sellerInfo.add(lblItemLocation);
        
         itemLocationLbl = new JLabel();
        itemLocationLbl.setBounds(189, 224, 408, 14);
        sellerInfo.add(itemLocationLbl);
        
        JPanel imageGallery = new JPanel();
        auctionDisplay.addTab("Image Gallery", null, imageGallery, null);
        imageGallery.setLayout(null);
        
        JPanel imageDisplay = new JPanel();
        imageDisplay.setBounds(0, 0, 1244, 457);
        imageGallery.add(imageDisplay);
        imageDisplay.setLayout(new CardLayout(0, 0));
        
         imageLbl = new JLabel();
         imageLbl.setHorizontalAlignment(SwingConstants.CENTER);
        imageDisplay.add(imageLbl, "name_66389012592982");
        
         previousButton = new JButton("Previous");
        previousButton.setBounds(366, 483, 89, 23);
        previousButton.addActionListener(new ButtonListener());
        imageGallery.add(previousButton);
        
         nextButton = new JButton("Next");
        nextButton.setBounds(701, 483, 89, 23);
        nextButton.addActionListener(new ButtonListener());
        imageGallery.add(nextButton);
        
        //Panel for description
        JPanel descriptionPanel = new JPanel();
        auctionDisplay.addTab("Description", null, descriptionPanel, null);
        descriptionPanel.setLayout(new CardLayout(0, 0));
        
        descriptionLbl = new JEditorPane();
        JScrollPane scrollpane= new JScrollPane(descriptionLbl);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        descriptionPanel.add(scrollpane);
        descriptionLbl.setEditable(false);
        
        
        searchLbl = new JLabel("");
        searchLbl.setForeground(Color.RED);
        searchLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
        searchLbl.setBounds(569, 15, 259, 19);
        card3.add(searchLbl);
        pane.add(tabbedPane);
        
        //Query by Keyword Panel
        JPanel card4 = new JPanel();
        card4.setLayout(null);
        tabbedPane.addTab("Search By Keyword", null, card4, null);
        
        JLabel label = new JLabel("Enter Query:");
        label.setFont(label.getFont().deriveFont(label.getFont().getStyle() | Font.BOLD));
        label.setBounds(52, 15, 218, 14);
        card4.add(label);
        
        queryText = new JTextField();
        queryText.setColumns(10);
        queryText.setBounds(230, 11, 226, 30);
        card4.add(queryText);
        
        queryButton = new JButton("Search");
        queryButton.addActionListener(new ButtonListener());
        queryButton.setBounds(451, 10, 100, 23);
        card4.add(queryButton);
        
        JPanel borderPanelQ = new JPanel();
        borderPanelQ.setBackground(UIManager.getColor("Button.background"));
        borderPanelQ.setLayout(null);
        borderPanelQ.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        borderPanelQ.setBounds(230, 60, 1039, 570);
        card4.add(borderPanelQ);
        
        
        JScrollPane scrollPane_3 = new JScrollPane();
        scrollPane_3.setBounds(0, 0, 1019, 504);
        borderPanelQ.add(scrollPane_3);
        queryTable = new JTable(){
        	public boolean isCellEditable(int row, int column){
        		return false;
        	};
        };
        scrollPane_3.setViewportView(queryTable);
        queryTable.setBackground(UIManager.getColor("Button.background"));
        queryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        queryTable.setShowGrid(false);
        queryTable.setShowVerticalLines(false);
        //Buttons for next and previous page query results
         previousPageButton = new JButton("Previous Page");
         previousPageButton.addActionListener(new ButtonListener());
        previousPageButton.setBounds(274, 525, 144, 23);
        previousPageButton.setEnabled(false);
        borderPanelQ.add(previousPageButton);
        
         nextPageButton = new JButton("Next Page");
        nextPageButton.setBounds(556, 525, 144, 23);
        nextPageButton.addActionListener(new ButtonListener());
        nextPageButton.setEnabled(false);
        borderPanelQ.add(nextPageButton);
        
        //Sets up the double click for view auction result
        queryTable.addMouseListener(new MouseAdapter() {
        	   public void mouseClicked(MouseEvent e) {
        	      if (e.getClickCount() == 2) {
        	    	  queryViewWorker worker= new queryViewWorker();
        	    	  worker.execute();
        	        
        	         }
        	   }
        	});
        
         queryLbl = new JLabel("");
        queryLbl.setForeground(Color.RED);
        queryLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
        queryLbl.setBounds(569, 15, 259, 19);
        card4.add(queryLbl);
        
        // Category Tree Selection
       
        JPanel catTreePanel = new JPanel();
        catTreePanel.setLocation(0, 40);
        catTreePanel.setSize(226, 590);
        catTreePanel.setLayout(null);
        catTreePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        card4.add(catTreePanel);
      
		      scrollPane_2 = new JScrollPane();
		     scrollPane_2.setBounds(0, 21, 218, 558);
		     catTreePanel.add(scrollPane_2);
		     
		     //Populate Tree
			    queryTree=new JTree(setTree());
			    queryTree.setSelectionRow(0);
			     scrollPane_2.setViewportView(queryTree);
			     queryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		         queryTree.setBackground(UIManager.getColor("Button.background"));
		         queryTree.setRootVisible(true);
		         queryTree.setShowsRootHandles(true);
		
        JLabel lblSearchInCategory = new JLabel("Search in Category:");
        lblSearchInCategory.setFont(lblSearchInCategory.getFont().deriveFont(lblSearchInCategory.getFont().getStyle() | Font.BOLD));
        lblSearchInCategory.setBounds(0, 0, 218, 14);
        catTreePanel.add(lblSearchInCategory);
        
        JLabel lblSort = new JLabel("Sort:");
        lblSort.setFont(lblSort.getFont().deriveFont(lblSort.getFont().getStyle() | Font.BOLD));
        lblSort.setBounds(838, 15, 218, 14);
        card4.add(lblSort);
        
         sopLbl = new JComboBox<TreeNode>();
         sopLbl.setEditable(false);
         sopLbl.addItem(new TreeNode("Ending Soonest","1"));
         sopLbl.addItem(new TreeNode("Newly Listed","10"));
         sopLbl.addItem(new TreeNode("Best Match","12"));
        sopLbl.setBounds(869, 12, 187, 20);
        card4.add(sopLbl);
        
         allListRB = new JRadioButton("All Listings");
        allListRB.setSelected(true);
        allListRB.setFont(new Font("Tahoma", Font.BOLD, 11));
        allListRB.setBounds(461, 41, 109, 23);
        card4.add(allListRB);
        
         auctionRB = new JRadioButton("Auction");
        auctionRB.setFont(new Font("Tahoma", Font.BOLD, 11));
        auctionRB.setBounds(572, 41, 109, 23);
        card4.add(auctionRB);
        
         buyItNowRB = new JRadioButton("Buy It Now");
        buyItNowRB.setFont(new Font("Tahoma", Font.BOLD, 11));
        buyItNowRB.setBounds(684, 40, 109, 23);
        card4.add(buyItNowRB);
        
       
        bg= new ButtonGroup();
        bg.add(allListRB);
        bg.add(auctionRB);
        bg.add(buyItNowRB);
        
        //Set up the search by Results Pane
        JPanel card5 = new JPanel();
        card5.setLayout(null);
        tabbedPane.addTab("Search Previous Results", null, card5, null);
        
        JLabel lblEnterKeywords = new JLabel("Enter Keywords or Item Number:");
        lblEnterKeywords.setFont(lblEnterKeywords.getFont().deriveFont(lblEnterKeywords.getFont().getStyle() | Font.BOLD));
        lblEnterKeywords.setBounds(44, 19, 218, 14);
        card5.add(lblEnterKeywords);
        
        
        previousSearchText = new JTextField();
        previousSearchText.setColumns(10);
        previousSearchText.setBounds(230, 11, 226, 30);
        card5.add(previousSearchText);
        
        previousSearchButton = new JButton("Search");
        previousSearchButton.addActionListener(new ButtonListener());
        previousSearchButton.setBounds(451, 10, 100, 23);
        card5.add(previousSearchButton);
        
        previousLbl = new JLabel("");
        previousLbl.setForeground(Color.RED);
        previousLbl.setFont(new Font("Tahoma", Font.ITALIC, 11));
        previousLbl.setBounds(569, 15, 259, 19);
        card5.add(previousLbl);
        
        JLabel label_1 = new JLabel("Sort:");
        label_1.setFont(label_1.getFont().deriveFont(label_1.getFont().getStyle() | Font.BOLD));
        label_1.setBounds(939, 14, 218, 14);
        card5.add(label_1);
        
       previousBox = new JComboBox<TreeNode>();
        previousBox.setEditable(false);
        previousBox.addItem(new TreeNode("Sort By Item Number Ascending","1"));
        previousBox.addItem(new TreeNode("Sort By Item Number Descending","2"));
        previousBox.addItem(new TreeNode("Sort By Title Ascending","3"));
        previousBox.addItem(new TreeNode("Sort By Title Descending","4"));
        previousBox.addItem(new TreeNode("Sort By Time Stamp Ascending","5"));
        previousBox.addItem(new TreeNode("Sort By Time Stamp Descending","6"));
        previousBox.setBounds(970, 11, 289, 20);
        card5.add(previousBox);
       
        JPanel previousPane = new JPanel();
        previousPane.setBackground(UIManager.getColor("Button.background"));
        previousPane.setLayout(null);
        previousPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        previousPane.setBounds(132, 60, 1137, 570);
        card5.add(previousPane);
        
        previousTable = new JTable(){
        	public boolean isCellEditable(int row, int column){
        		return false;
        	};
        };
        JScrollPane scrollPane_4 = new JScrollPane();
        scrollPane_4.setBounds(0, 0, 1269, 504);
        previousPane.add(scrollPane_4);
        scrollPane_4.setViewportView(previousTable);
        previousTable.setBackground(UIManager.getColor("Button.background"));
        previousTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        previousTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        previousTable.setShowGrid(false);
        previousTable.setShowVerticalLines(false);
        
      //Sets up the double click for view auction result
        previousTable.addMouseListener(new MouseAdapter() {
        	   public void mouseClicked(MouseEvent e) {
        	      if (e.getClickCount() == 2) {
        	    	  resultViewWorker worker= new resultViewWorker();
        	    	  worker.execute();
        	        
        	         }
        	   }
        	});
        
        previousResultPageButton = new JButton("Previous Page");
        previousResultPageButton.addActionListener(new ButtonListener());
        previousResultPageButton.setBounds(274, 525, 144, 23);
        previousResultPageButton.setEnabled(false);
        previousPane.add(previousResultPageButton);
       
        nextResultPageButton = new JButton("Next Page");
        nextResultPageButton.setBounds(556, 525, 144, 23);
        nextResultPageButton.addActionListener(new ButtonListener());
        nextResultPageButton.setEnabled(false);
        previousPane.add(nextResultPageButton);
        
        lblLimitDates = new JLabel("Limit Dates:");
        lblLimitDates.setFont(lblLimitDates.getFont().deriveFont(lblLimitDates.getFont().getStyle() | Font.BOLD));
        lblLimitDates.setBounds(10, 61, 100, 14);
        card5.add(lblLimitDates);
       
        DateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        java.util.Date utilDate = new java.util.Date();
        
        fromDate = new JFormattedTextField();
        fromDate.setColumns(10);
        fromDate.setBounds(0, 75, 78, 30);
        fromDate.setText(format.format(utilDate));
        fromDate.setEditable(false);
        card5.add(fromDate);
        
        toDate = new JFormattedTextField();
        toDate.setColumns(10);
        toDate.setBounds(0, 148, 78, 30);
        toDate.setText(format.format(utilDate));
        toDate.setEditable(false);
        card5.add(toDate);

        
        JLabel lblTo = new JLabel("to");
        lblTo.setFont(lblTo.getFont().deriveFont(lblTo.getFont().getStyle() | Font.BOLD));
        lblTo.setBounds(10, 116, 100, 14);
        card5.add(lblTo);
        
         fromDateButton = new JButton();
        fromDateButton.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\icon.png")); 
        fromDateButton.setBounds(88, 77, 25, 23);
        fromDateButton.addActionListener(new ButtonListener());
        card5.add(fromDateButton);
        
        toDateButton = new JButton();
        toDateButton.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\icon.png")); 
        toDateButton.setBounds(88, 152, 25, 23);
        toDateButton.addActionListener(new ButtonListener());
        card5.add(toDateButton);
     
    }
 
    /**
     * Method to construct the category tree
     * @return the root of the category, null if the categories are not setup properly yet
     */
    public static TreeNode setTree(){
    	
        TreeNode root= new TreeNode("All Categories","-1");
        
         //Populate Category Tree
         String dburl = "jdbc:mysql://localhost:3306/";
         String dbName = "ebay";
         String driver = "com.mysql.jdbc.Driver";
         String userName = databaseName;
         String password = databasePassword;
        try{
	      // This will load the MySQL driver, each DB has its own driver
	    	Class.forName(driver).newInstance();
	
	      // Setup the connection with the DB
	    	Connection  connect = DriverManager.getConnection(dburl+dbName,userName,password);
		      Statement stmt=connect.createStatement();
		   ResultSet rs= stmt.executeQuery("Select * from ebay.category where level=1 order by name asc");
		     while(rs.next()){
		    	 TreeNode level1= new TreeNode(rs.getString("name"),rs.getString("id"));
		    	 root.add(level1);
		    	 Statement stmt2=connect.createStatement();
		    	 ResultSet rs2=stmt2.executeQuery("Select * from ebay.category where parent="+rs.getString("id")+" order by name asc");
		    	 while(rs2.next()){
		    		 TreeNode level2= new TreeNode(rs2.getString("name"),rs2.getString("id"));
		    		 level1.add(level2);
		    		 Statement stmt3=connect.createStatement();
		    		 ResultSet rs3=stmt3.executeQuery("Select * from ebay.category where parent="+rs2.getString("id")+" order by name asc");
			    	 while(rs3.next()){
			    		 TreeNode level3= new TreeNode(rs3.getString("name"),rs3.getString("id"));
			    		 level2.add(level3);
			    		 Statement stmt4=connect.createStatement();
			    		 ResultSet rs4=stmt4.executeQuery("Select * from ebay.category where parent="+rs3.getString("id")+" order by name asc");
				    	 while(rs4.next()){
				    		 TreeNode level4= new TreeNode(rs4.getString("name"),rs4.getString("id"));
				    		 level3.add(level4);
				    		 Statement stmt5=connect.createStatement();
				    		 ResultSet rs5=stmt5.executeQuery("Select * from ebay.category where parent="+rs4.getString("id")+" order by name asc");
					    	 while(rs5.next()){
					    		 TreeNode level5= new TreeNode(rs5.getString("name"),rs5.getString("id"));
					    		 level4.add(level5);
					    		 Statement stmt6=connect.createStatement();
					    		 ResultSet rs6=stmt6.executeQuery("Select * from ebay.category where parent="+rs5.getString("id")+" order by name asc");
						    	 while(rs6.next()){
						    		 TreeNode level6= new TreeNode(rs6.getString("name"),rs6.getString("id"));
						    		 level5.add(level6);
						    	 }
						    	 
					    	 }
					    	 
				    	 }
				    	
			    	 }
			    	 
		    	 }
		    
		     }
		    
		     connect.close();
		     return root;
        }
        catch(Exception e){return null;}
        }
    
    /**
     * Creates the GUI
     * @throws IOException  The image parsed is invalid
     */
    private static void createAndShowGUI() throws IOException {
        //Create and set up the window.
        frame = new JFrame("Ebay Crawler");
        //Sets up Icon Image
        Image image = null;
        URL url = new URL("http://mobile.ebay.com/uploads/app-icon-images/AppIcon_eBay.png");
        image = ImageIO.read(url);
        frame.setIconImage(image);
        frame.setLocation(120,120);
        frame.setSize(1280, 720);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        MainMenu menu = new MainMenu();
        menu.addComponentToPane(frame.getContentPane());
        
        //Sets up the author information on bottom of Panel
        JPanel Authored = new JPanel();
        Authored.setBorder(new MatteBorder(1, 0, 0, 0, (Color) new Color(0, 0, 0)));
        frame.getContentPane().add(Authored, BorderLayout.SOUTH);
        
        JLabel author = new JLabel("Created By Natan Ritholtz for Computer Science 370");
        author.setFont(new Font("Times New Roman", Font.ITALIC, 13));
        Authored.add(author);
 
        //Display the window.
        frame.setVisible(true);
        
    }
    
    
 // Worker class to do updateCategories
    public static class updateWorker extends SwingWorker<Void, Void> {
    	  public static long currtime;
      @Override
      public Void doInBackground() throws Exception {
    	currtime=System.currentTimeMillis();
    	  update.setEnabled(false);
          cancelUpdate.setEnabled(true);
    timerTick = 0;
    updatePB.setValue(timerTick);
    String str = "<html>" + "<font color=\"#008000\">" + "<b>" + 
"Update in Progress..." + "</b>" + "</font>" + "</html>";
    updateLabel.setText(str);
          timer.start();
          //Runs getCategories
    	  GetCategories.getCategories();

        return null;
      }
    }

 // Worker class to do search by id/url
    public static class searchWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  searchLbl.setText("Searching..");
  		boolean errorCheck;
  		if(textField.getText().trim().matches("[\\d]+"))errorCheck=GetAuctionDetails.getPageDetailsByItemNum(textField.getText().trim());
  		else errorCheck=GetAuctionDetails.getPageDetailsByURL(textField.getText().trim());
  		if(!errorCheck) searchLbl.setText("Database not setup or invalid query");
  		else searchLbl.setText("");
        return null;
      }
    }
    
 // Worker class to do search by keyword
    public static class queryWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  queryLbl.setText("Searching..");
  		boolean errorCheck;
  		currentpage=1;
  		errorCheck=GetQueryResult.getQuery(queryText.getText().trim(), ((TreeNode) queryTree.getLastSelectedPathComponent()).id, Integer.parseInt(((TreeNode) sopLbl.getSelectedItem()).id), currentpage);
  		if(!errorCheck) queryLbl.setText("Database not setup or invalid query");
  		else queryLbl.setText("");
        return null;
      }
    }
 // Worker class to do next Image
    public static class nextWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  if(currentimage>=0 && currentimage<images-1)currentimage+=1;
    	  else currentimage=0;
    	  ImageIcon tempimage= new ImageIcon(MainMenu.imageArray[currentimage]);
    	  if(tempimage.getIconHeight()>600|tempimage.getIconWidth()>600){
	        	 tempimage=new ImageIcon(tempimage.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT));
	        }
	        imageLbl.setIcon(tempimage);
        return null;
      }
    }
    
 // Worker class to do previous Image
    public static class previousWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  if(currentimage>0 && currentimage<images)currentimage-=1;
    	  else currentimage=images-1;
    	  ImageIcon tempimage= new ImageIcon(MainMenu.imageArray[currentimage]);
    	  if(tempimage.getIconHeight()>600|tempimage.getIconWidth()>600){
	        	 tempimage=new ImageIcon(tempimage.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT));
	        }
	       imageLbl.setIcon(tempimage);
        return null;
      }
    }
   
    // Worker class to pull up auction view for selected query auction
    public static class queryViewWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	   
    	    int row = queryTable.getSelectedRow();
    	    textField.setText((String)queryTable.getValueAt(row, 0));
    	    tabbedPane.setSelectedIndex(1);
    	    searchButton.doClick();
        return null;
      }
    }
  
 // Worker class to pull up previous page Query
    public static class previousPageWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  queryLbl.setText("Searching..");
    		boolean errorCheck;
    	    currentpage--;
    	    errorCheck= GetQueryResult.getQuery(currKeyword, currCatNum, searchNum, currentpage);
    	    if(!errorCheck) queryLbl.setText("Database not setup or invalid query");
      		else queryLbl.setText("");
        return null;
      }
    }
    
 // Worker class to pull up next page Query
    public static class nextPageWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  queryLbl.setText("Searching..");
    		boolean errorCheck;
    	    currentpage++;
    	    errorCheck= GetQueryResult.getQuery(currKeyword, currCatNum, searchNum, currentpage);
    	    if(!errorCheck) queryLbl.setText("Database not setup or invalid query");
      		else queryLbl.setText("");
        return null;
      }
    }
    
 // Worker class to do search previous searches
    public static class previousSearch extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  previousLbl.setText("Searching..");
  		boolean errorCheck;
  		resultpage=1;
  		if(previousSearchText.getText().trim().matches("[\\d]+"))errorCheck=GetPreviousResults.getByNumber(previousSearchText.getText().trim(),Integer.parseInt(((TreeNode) previousBox.getSelectedItem()).id));
  		else errorCheck=GetPreviousResults.getByKeyword(previousSearchText.getText().trim(),Integer.parseInt(((TreeNode) previousBox.getSelectedItem()).id));
  		if(!errorCheck) previousLbl.setText("Database not setup or invalid query");
  		else previousLbl.setText("");
        return null;
      }
    }  
    
    
    
 // Worker class to pull up previous result page Query
    public static class previousResultPageWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  previousLbl.setText("Searching..");
    		boolean errorCheck;
    	    resultpage--;
    	    if(resultpage==1) previousResultPageButton.setEnabled(false);
    	    else previousResultPageButton.setEnabled(true);
    	    errorCheck= GetPreviousResults.getPage();
    	    if(!errorCheck) previousLbl.setText("Database not setup or invalid query");
      		else previousLbl.setText("");
        return null;
      }
    }
    
 // Worker class to pull up next result page Query
    public static class nextResultPageWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	  previousLbl.setText("Searching..");
    		boolean errorCheck;
    	    resultpage++;
    	    if(resultpage==GetPreviousResults.maxCount) nextResultPageButton.setEnabled(false);
    	    else nextResultPageButton.setEnabled(true);
    	    errorCheck= GetPreviousResults.getPage();
    	    if(!errorCheck) previousLbl.setText("Database not setup or invalid query");
      		else previousLbl.setText("");
        return null;
      }
    }

    // Worker class to pull up auction view for selected query auction
    public static class resultViewWorker extends SwingWorker<Void, Void> {
 
      @Override
      public Void doInBackground() throws Exception {
    	   
    	    int row = previousTable.getSelectedRow();
    	    textField.setText((String)previousTable.getValueAt(row, 0));
    	    tabbedPane.setSelectedIndex(1);
    	   GetAuctionDetails.displayResultsByKey(textField.getText());
        return null;
      }
    }
  
    public static void main(String[] args) {
        /* Sets UI Look and Feel */
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

   
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                	//Will check config folder for database parameters otherwise will prompt user for information
                	File config= new File(System.getProperty("user.dir")+"\\config.txt");
            		if(config.exists()) {
                	BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir")+"\\config.txt"));
                	databaseName=reader.readLine();
                	databasePassword=reader.readLine();
                	reader.close();
            		}
            		else{
                	databaseName=JOptionPane.showInputDialog(null,"Please enter your database username","EbayCrawler",JOptionPane.PLAIN_MESSAGE);
                	if(databaseName==null) System.exit(0);
                	databasePassword=JOptionPane.showInputDialog(null,"Please enter your database password","EbayCrawler",JOptionPane.PLAIN_MESSAGE);
                	if(databasePassword==null) System.exit(0);
                	
                	PrintWriter writer = new PrintWriter("config.txt", "UTF-8");
                    writer.println(databaseName);
                    writer.println(databasePassword);
                    writer.close();
            		}
                	Initialize.createTables();
					createAndShowGUI();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
        
    }
}
