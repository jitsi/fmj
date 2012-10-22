package ejmf.toolkit.multiplayer;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
* TrackTable is the view for a TrackModel.
*/

public class TrackTable extends JTable {
	/**
	* Create TrackTable from a TrackModel.	A TrackTable	
	* refines TrackModel, imposing Track semantics on table data.
	* @return tm TableModel from which TrackModel
	* is constructed.
	*/
    public TrackTable(TableModel tm) {
	super(tm);

	setCellSelectionEnabled(true);
	setRowSelectionAllowed(false);
	setColumnSelectionAllowed(false);
	setSelectionMode(0);
        TableColumnModel tcm = getColumnModel();
	JTableHeader header = new JTableHeader(tcm);
 	header.setReorderingAllowed(false);	

	setTableHeader(header);
	header.setResizingAllowed(true);

	tcm.setColumnMargin(5);
	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
	    public void setValue(Object o) {
		setText((o == null) ? "" : o.toString());
	    }
	};

	renderer.setHorizontalAlignment(JLabel.RIGHT);

	// Setup Track# column
	TableColumn col = getColumn("Track#");
	col.setCellRenderer(renderer);
	// In Swing 1.0.1 this will never work.
	// These is a swing bug.
	// col.sizeWidthToFit();
	col.setCellRenderer(renderer);
	col.setResizable(false);

	// Setup  Media file column
	col = getColumn("Media");
	TableCellEditor cellEditor = new DefaultCellEditor(new JTextField());
	MediaFileCellEditorListener mcl = 
			new MediaFileCellEditorListener();
	cellEditor.addCellEditorListener(mcl);
        col.setCellEditor(cellEditor);
	MediaFileCellRenderer m_renderer = new MediaFileCellRenderer();
	col.setCellRenderer(m_renderer);

	// Setup StartTime column
	col = getColumn("Start Time");
	cellEditor = new DefaultCellEditor(new JTextField());
	StartCellEditorListener scl = new StartCellEditorListener(this);
	cellEditor.addCellEditorListener(scl);
        col.setCellEditor(cellEditor);
        col.setResizable(false);
	col.setCellRenderer(renderer);

	// Setup PlayingTime column
	col = getColumn("Play Time");
	cellEditor = new DefaultCellEditor(new JTextField());
	PlayingTimeCellEditorListener dcl = new PlayingTimeCellEditorListener();
	cellEditor.addCellEditorListener(dcl);
        col.setCellEditor(cellEditor);
	col.setResizable(false);
	col.setCellRenderer(renderer);
    }

	/* Cell editing listener for playing time column
	*/
    class PlayingTimeCellEditorListener implements CellEditorListener {
    
        public PlayingTimeCellEditorListener() {
	    super();
        }

        public void editingCanceled(ChangeEvent e) {  
        }

        public void editingStopped(ChangeEvent e) {  
	    TrackModel model = (TrackModel) getModel();
    	    int row = getSelectedRow();
    	    int col = getSelectedColumn();
    	    try {
    	        DefaultCellEditor ce = (DefaultCellEditor) e.getSource();
    	        JTextField c = (JTextField) ce.getComponent();

		// Extract value from cell and convert to double.
	        double time = Double.valueOf(c.getText()).doubleValue();

		Track track = model.getTrackList().getTrack(row);

		// Playing time can not be longer than duration.
		time = Math.min(time, track.getDuration().getSeconds());

    	        model.setPlayingTime(row, time);
    	    } catch (Exception ex) {
    	        ex.printStackTrace();
    	        System.out.println(ex.getClass().getName());
    	    }
        }
    }
    
	/*
	* Media file column cell editing listener. All operations	
	* are no-ops.
	*/
    class MediaFileCellEditorListener implements CellEditorListener {
        JTable table;
        public MediaFileCellEditorListener() {
    	    super();
        }
        public void editingCanceled(ChangeEvent e) {
        }
        public void editingStopped(ChangeEvent e) {  
        }
    }
    
	/*
	* Media file column cell renderer. 
	* This method adds a tooltip to media file cell
	* and then calls its super.
	* <P>
	* The cell's tool tip displays the full path name of 
	* media file or media locator.
	*/
    class MediaFileCellRenderer extends DefaultTableCellRenderer {

        public MediaFileCellRenderer() {
    	    super();
        }
    
        public Component getTableCellRendererComponent(JTable table,
    						Object value,
    						boolean isSelected,
    						boolean hasFocus,
    						int row,
    						int column) {
    	    JLabel label = (JLabel) super.getTableCellRendererComponent(
    					TrackTable.this, null,
    					true, true, 0, 1);
    
    	    if (label != null) {
		TrackModel model = (TrackModel) getModel();
    	        Track track = model.getTrack(row);
    	        label.setToolTipText(track.getMediaLocator().toString());
    	    }
    	
    	    return super.getTableCellRendererComponent(
    			table, value, isSelected, hasFocus, row, column);
        }
    }
}
    
/*
* Start cell editor listener
*/
class StartCellEditorListener implements CellEditorListener {
    JTable table;
    TrackModel model;

    public StartCellEditorListener(JTable table) {
	this.table = table;
	model = (TrackModel) table.getModel();
    }

    public void editingCanceled(ChangeEvent e) {  
    }

    public void editingStopped(ChangeEvent e) {  
	int row = table.getSelectedRow();
	int col = table.getSelectedColumn();
	try {
	    DefaultCellEditor ce = (DefaultCellEditor) e.getSource();
	    JTextField c = (JTextField) ce.getComponent();
	    model.setStartTime(row, c.getText());
	} catch (Exception ex) {
	    ex.printStackTrace();
	    System.out.println(ex.getClass().getName());
	}
    }
}
