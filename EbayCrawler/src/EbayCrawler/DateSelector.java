
package EbayCrawler;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Date Selector GUI adaptation of the Java Swing Calendar Picker
 * @author Natan Ritholtz
 *
 */
class DateSelector {
 
        int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);;
        JLabel l = new JLabel("", JLabel.CENTER);
        String day = "";
        JDialog d;
        JButton[] button = new JButton[49]; //The days that will be selected on the pane

        /**
         * Creates a Pane for Selecting the day
         * @param parent The parent frame that will be used to create the new pane
         */
        public DateSelector(JFrame parent) {
                d = new JDialog();
                d.setModal(true);
                String[] header = { "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };
                JPanel p1 = new JPanel(new GridLayout(7, 7));
                p1.setPreferredSize(new Dimension(430, 120));
                //Creates the days and a listener for the changing the month,etc.
                for (int x = 0; x < button.length; x++) {
                        final int selection = x;
                        button[x] = new JButton();
                        button[x].setFocusPainted(false);
                        button[x].setBackground(Color.white);
                        if (x > 6)
                                button[x].addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent ae) {
                                                day = button[selection].getActionCommand();
                                                d.dispose();
                                        }
                                });
                        if (x < 7) {
                                button[x].setText(header[x]);
                                button[x].setForeground(Color.red);
                        }
                        p1.add(button[x]);
                }
                //Previous Month
                JPanel p2 = new JPanel(new GridLayout(1, 3));
                JButton previous = new JButton();
                previous.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\previous.png"));
                previous.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                                month--;
                                displayDate();
                        }
                });
                p2.add(previous);
                p2.add(l);
                //Next Month
                JButton next = new JButton();
                next.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\next.png"));
                next.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                                month++;
                                displayDate();
                        }
                });
                p2.add(next);
                d.add(p1, BorderLayout.CENTER);
                d.add(p2, BorderLayout.SOUTH);
                d.pack();
                d.setLocationRelativeTo(parent);
                displayDate();
                d.setVisible(true);
        }
        //First disables all days and enables only days that exists for that month
        public void displayDate() {
                for (int x = 7; x < button.length; x++){
                        button[x].setText("");
                        button[x].setEnabled(false);
                }
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                                "MMMM yyyy");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month, 1);
                int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
                for (int x = 6 + dayOfWeek, day = 1; day <= daysInMonth; x++, day++){
                        button[x].setText("" + day);
                        button[x].setEnabled(true);
                }
                l.setText(sdf.format(cal.getTime()));
                d.setTitle("Date Picker");
        }
        
        /**
         * Returns the SQL date format string for the selected date, however will return the same day for either a
         * too early or too late date from the from/to date already selected
         * @param origin Whether the date is selected from "from" or "to" text field
         * @return The selected date in SQL date string format
         * @throws Exception
         */
        public String setPickedDate(String origin) throws Exception {
                if (day.equals(""))
                        return day;
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                                "yyyy-MM-dd");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(year, month, Integer.parseInt(day));
                if(origin=="from"){
                	if(cal.getTime().after(sdf.parse(MainMenu.toDate.getText()))) return MainMenu.toDate.getText();
                }
                else{
                	if(cal.getTime().before(sdf.parse(MainMenu.fromDate.getText()))) return MainMenu.fromDate.getText();
                }
                return sdf.format(cal.getTime());
        }
}

