 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/

 
/*
 * Created on 19-jun-2003
 *
 */

package be.ibridge.kettle.job.entry.sql;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.WindowProperty;
import be.ibridge.kettle.core.database.DatabaseMeta;
import be.ibridge.kettle.core.dialog.DatabaseDialog;
import be.ibridge.kettle.job.JobMeta;
import be.ibridge.kettle.job.dialog.JobDialog;
import be.ibridge.kettle.job.entry.JobEntryDialogInterface;
import be.ibridge.kettle.job.entry.JobEntryInterface;
import be.ibridge.kettle.trans.step.BaseStepDialog;


/**
 * This dialog allows you to edit the SQL job entry settings. (select the connection and the sql script to be executed)
 *  
 * @author Matt
 * @since  19-06-2003
 */
public class JobEntrySQLDialog extends Dialog implements JobEntryDialogInterface
{
	private Label        wlName;
	private Text         wName;
    private FormData     fdlName, fdName;

	private Label        wlConnection;
	private CCombo       wConnection;
	private Button		 wbConnection;
	private FormData     fdlConnection, fdbConnection, fdConnection;
	
    private Label        wlUseSubs;
    private Button       wUseSubs;
    private FormData     fdlUseSubs, fdUseSubs;

	private Label        wlSQL;
	private Text         wSQL;
	private FormData     fdlSQL, fdSQL;

	private Label        wlPosition;
	private FormData     fdlPosition;
	
	private Button wOK, wCancel;
	private Listener lsOK, lsCancel;

	private JobEntrySQL    	jobEntry;
	private JobMeta         jobMeta;
	private Shell       	shell;
	private Props       	props;

	private SelectionAdapter lsDef;

	private boolean changed;
	
	public JobEntrySQLDialog(Shell parent, JobEntrySQL jobEntry, JobMeta jobMeta)
	{
		super(parent, SWT.NONE);
		props=Props.getInstance();
		this.jobEntry=jobEntry;
		this.jobMeta=jobMeta;

		if (this.jobEntry.getName() == null) this.jobEntry.setName("SQL");
	}

	public JobEntryInterface open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);
        JobDialog.setShellImage(shell, jobEntry);

		ModifyListener lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				jobEntry.setChanged();
			}
		};
		changed = jobEntry.hasChanged();

		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText("SQL Script");
		
		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(" &OK ");
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(" &Cancel ");

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, null);

		// Filename line
		wlName=new Label(shell, SWT.RIGHT);
		wlName.setText("Job entry name ");
 		props.setLook(wlName);
		fdlName=new FormData();
		fdlName.left = new FormAttachment(0, 0);
		fdlName.right= new FormAttachment(middle, 0);
		fdlName.top  = new FormAttachment(0, margin);
		wlName.setLayoutData(fdlName);
		wName=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wName);
		wName.addModifyListener(lsMod);
		fdName=new FormData();
		fdName.left = new FormAttachment(middle, margin);
		fdName.top  = new FormAttachment(0, margin);
		fdName.right= new FormAttachment(100, 0);
		wName.setLayoutData(fdName);

		// Connection line
		wlConnection=new Label(shell, SWT.RIGHT);
		wlConnection.setText("Connection ");
 		props.setLook(wlConnection);
		fdlConnection=new FormData();
		fdlConnection.left = new FormAttachment(0, 0);
		fdlConnection.right= new FormAttachment(middle, 0);
		fdlConnection.top  = new FormAttachment(wName, margin);
		wlConnection.setLayoutData(fdlConnection);
		
		wbConnection=new Button(shell, SWT.PUSH);
		wbConnection.setText("&New...");
		wbConnection.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent e) 
			{
				DatabaseMeta databaseMeta = new DatabaseMeta();
				DatabaseDialog cid = new DatabaseDialog(shell, databaseMeta);
				if (cid.open()!=null)
				{
					jobMeta.addDatabase(databaseMeta);
					
					// SB: Maybe do the same her as in BaseStepDialog: remove
					//     all db connections and add them again.
					wConnection.add(databaseMeta.getName());
					wConnection.select(wConnection.getItemCount()-1);
				}
			}
		});
		fdbConnection=new FormData();
		fdbConnection.right = new FormAttachment(100, 0);
		fdbConnection.top   = new FormAttachment(wName, margin);
		wbConnection.setLayoutData(fdbConnection);

		wConnection=new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
 		props.setLook(wConnection);
		for (int i=0;i<jobMeta.nrDatabases();i++)
		{
			DatabaseMeta ci = jobMeta.getDatabase(i);
			wConnection.add(ci.getName());
		}
		wConnection.select(0);
		wConnection.addModifyListener(lsMod);
		fdConnection=new FormData();
		fdConnection.left = new FormAttachment(middle, margin);
		fdConnection.top  = new FormAttachment(wName, margin);
		fdConnection.right= new FormAttachment(wbConnection, -margin);
		wConnection.setLayoutData(fdConnection);

        // Include Files?
        wlUseSubs=new Label(shell, SWT.RIGHT);
        wlUseSubs.setText("Use variable substitution?");
        props.setLook(wlUseSubs);
        fdlUseSubs=new FormData();
        fdlUseSubs.left = new FormAttachment(0, 0);
        fdlUseSubs.top  = new FormAttachment(wConnection, margin);
        fdlUseSubs.right= new FormAttachment(middle, -margin);
        wlUseSubs.setLayoutData(fdlUseSubs);
        wUseSubs=new Button(shell, SWT.CHECK);
        props.setLook(wUseSubs);
        fdUseSubs=new FormData();
        fdUseSubs.left = new FormAttachment(middle, margin);
        fdUseSubs.top  = new FormAttachment(wConnection, margin);
        fdUseSubs.right= new FormAttachment(100, 0);
        wUseSubs.setLayoutData(fdUseSubs);
        wUseSubs.addSelectionListener(new SelectionAdapter() 
            {
                public void widgetSelected(SelectionEvent e) 
                {
                	jobEntry.setUseVariableSubstitution(!jobEntry.getUseVariableSubstitution());
                	jobEntry.setChanged();
                }
            }
        );
		
		wlPosition=new Label(shell, SWT.NONE);
		wlPosition.setText("Linenr: 0        ");
 		props.setLook(wlPosition);
		fdlPosition=new FormData();
		fdlPosition.left   = new FormAttachment(0, 0);
		fdlPosition.bottom = new FormAttachment(wOK, -margin);
		wlPosition.setLayoutData(fdlPosition);

		// Script line
		wlSQL=new Label(shell, SWT.NONE);
		wlSQL.setText("SQL Script: ");
 		props.setLook(wlSQL);
		fdlSQL=new FormData();
		fdlSQL.left = new FormAttachment(0, 0);
		fdlSQL.top  = new FormAttachment(wConnection, margin);
		wlSQL.setLayoutData(fdlSQL);
		wSQL=new Text(shell, SWT.MULTI | SWT.LEFT | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
 		props.setLook(wSQL, Props.WIDGET_STYLE_FIXED);
		wSQL.addModifyListener(lsMod);
		fdSQL=new FormData();
		fdSQL.left   = new FormAttachment(0, 0);
		fdSQL.top    = new FormAttachment(wlSQL, margin);
		fdSQL.right  = new FormAttachment(100, -5);
		fdSQL.bottom = new FormAttachment(wlPosition, -margin);
		wSQL.setLayoutData(fdSQL);



		// Add listeners
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel(); } };
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		
		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener    (SWT.Selection, lsOK    );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wName.addSelectionListener( lsDef );
				
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );


		wSQL.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) 
			{
				int linenr = wSQL.getCaretLineNumber()+1;
				wlPosition.setText("Linenr: "+linenr+"   ");
			}
		})
		;
				
		getData();
		
		BaseStepDialog.setSize(shell);

		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return jobEntry;
	}

	public void dispose()
	{
		WindowProperty winprop = new WindowProperty(shell);
		props.setScreen(winprop);
		shell.dispose();
	}
	
	/**
	 * Copy information from the meta-data input to the dialog fields.
	 */ 
	public void getData()
	{
		if (jobEntry.getName() != null) wName.setText( jobEntry.getName() );
		if (jobEntry.getSQL()  != null) wSQL.setText( jobEntry.getSQL() );
		DatabaseMeta dbinfo = jobEntry.getDatabase(); 
		if (dbinfo!=null && dbinfo.getName()!=null) wConnection.setText(dbinfo.getName());
		else wConnection.setText("");

        wUseSubs.setSelection(jobEntry.getUseVariableSubstitution());		
		wName.selectAll();
	}
	
	private void cancel()
	{
		jobEntry.setChanged(changed);
		jobEntry=null;
		dispose();
	}
	
	private void ok()
	{
		jobEntry.setName(wName.getText());
		jobEntry.setSQL(wSQL.getText());
		jobEntry.setDatabase( jobMeta.findDatabase(wConnection.getText()));
		dispose();
	}
	
	
	public String toString()
	{
		return this.getClass().getName();
	}
}
