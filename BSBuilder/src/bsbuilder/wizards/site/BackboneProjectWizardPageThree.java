package bsbuilder.wizards.site;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Combo;

import bsbuilder.wizards.site.utils.TemplateMerger;

public class BackboneProjectWizardPageThree extends WizardPage {
	private Text textSampleDomainClass;
	private Table table;
	private Map<String, Object> attrs = new LinkedHashMap<String, Object>();

	protected BackboneProjectWizardPageThree(String pageName) {
		super(pageName);
		setTitle("Sample Domain Class");
		setDescription("Create a sample domain class which will be wired to" +
				" a sample Restful Controller");		
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Sample Domain Class:");
		new Label(container, SWT.NONE);
		
		textSampleDomainClass = new Text(container, SWT.BORDER);
		textSampleDomainClass.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				validatePackage(textSampleDomainClass.getText());
			}
		});
		textSampleDomainClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(206);
		tblclmnNewColumn_1.setText("Attribute");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(208);
		tblclmnNewColumn.setText("Data Type");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
				
		TableColumn tblclmnIdFlag = new TableColumn(table, SWT.NONE);
		tblclmnIdFlag.setWidth(100);
		tblclmnIdFlag.setText("Is Id?");
		
		
		/*
		final TableEditor editor1 = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor1.horizontalAlignment = SWT.LEFT;
		editor1.grabHorizontal = true;
		editor1.minimumWidth = 50;
				
		final TableEditor editor2 = new TableEditor(table);				
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor2.horizontalAlignment = SWT.LEFT;
		editor2.grabHorizontal = true;
		editor2.minimumWidth = 50;
		
		final TableEditor editor3 = new TableEditor(table);				
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor3.horizontalAlignment = SWT.LEFT;
		editor3.grabHorizontal = true;
		editor3.minimumWidth = 50;
		*/
		
		/*table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor1.getEditor();
				if (oldEditor != null) oldEditor.dispose();
				
				// Clean up any previous editor control
				Control oldEditor2 = editor2.getEditor();
				if (oldEditor2 != null) oldEditor2.dispose();
				
				// Clean up any previous editor control
				Control oldEditor3 = editor3.getEditor();
				if (oldEditor3 != null) oldEditor3.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) return;
		
				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(0));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text)editor1.getEditor();
						editor1.getItem().setText(0, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor1.setEditor(newEditor, item, 0);
				
				// The control that will be the editor must be a child of the Table
				//Text newEditor2 = new Text(table, SWT.NONE);
				Combo comboEditor = new Combo(table, SWT.NONE);
				comboEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				String[] itemsForCombo = {"String","Date"};
				comboEditor.setItems(itemsForCombo);
				
				comboEditor.setText(item.getText(1));
				comboEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						//Combo combo = (Combo)editor2.getEditor();
						//editor2.getItem().setText(1, combo.getText());
						Combo combo = (Combo)me.getSource();						
						editor2.getItem().setText(1, combo.getText());
					}
				});	
				editor2.setEditor(comboEditor, item, 1);
				
				Button radioButtonEditor = new Button(table, SWT.RADIO);
				radioButtonEditor.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
						System.out.println();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
				editor3.setEditor(radioButtonEditor, item, 2);
			}
		});*/
		
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
			      // height cannot be per row so simply set
			      event.height = 20;
			   }
		});
		
		
		Button btnNewButton = new Button(container, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 164;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				final TableItem tableItem = new TableItem(table, SWT.NONE);
				
				tableItem.setText(0, "changeMe");
				TableEditor column1Editor = new TableEditor(table);
				column1Editor.horizontalAlignment = SWT.LEFT;
				column1Editor.grabHorizontal = true;
				column1Editor.minimumWidth = 50;
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText("changeMe");					
				newEditor.addModifyListener(new ModifyListener() {					
					@Override
					public void modifyText(ModifyEvent arg0) {
					  String value = ((Text)arg0.getSource()).getText();	
					  tableItem.setText(0, value);		
					  validateAttrName(value);
					}
				});							
				column1Editor.setEditor(newEditor, tableItem, 0);
				
				tableItem.setText(1, "String");
				TableEditor column2Editor = new TableEditor(table);
				column2Editor.horizontalAlignment = SWT.LEFT;
				column2Editor.grabHorizontal = true;
				column2Editor.minimumWidth = 50;
				CCombo combo = new CCombo(table, SWT.NONE);
			    combo.setText("String");
			    combo.add("String");
			    combo.add("Date");		
			    combo.add("Number");
			    combo.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(ModifyEvent arg0) {
						tableItem.setText(1, ((CCombo)arg0.getSource()).getText());
					}
				});
			    column2Editor.setEditor(combo, tableItem, 1);
			    
			    TableEditor column3Editor = new TableEditor(table);
			    column3Editor.horizontalAlignment = SWT.LEFT;
			    column3Editor.grabHorizontal = true;
			    column3Editor.minimumWidth = 50;
			    column3Editor.minimumHeight = 30;
			    Button radioButtonEditor = new Button(table, SWT.RADIO);
			    radioButtonEditor.setText("X");
			    if(table.getItems().length == 1){
			    	radioButtonEditor.setSelection(true);
			    	tableItem.setText(2, "X");
			    }
			    radioButtonEditor.addSelectionListener(new SelectionListener() {					
					@Override
					public void widgetSelected(SelectionEvent arg0) {
						for(TableItem tableItemReset : table.getItems())
							tableItemReset.setText(2, "");
						tableItem.setText(2, "X");
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {
						
					}
				});
			    column3Editor.setEditor(radioButtonEditor, tableItem, 2);
			    validateAttrsPresence();
			}
		});
		btnNewButton.setText("Add Row");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button btnNewButton_1 = new Button(container, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] tableItems = table.getItems();			
				for(int i = 0; i < tableItems.length; i++){
					System.out.println(tableItems[i].getText(0) + "=====" + tableItems[i].getText(1) + tableItems[i].getText(2));
				}
			}
		});
		btnNewButton_1.setText("?");
		
		
		validatePackage(textSampleDomainClass.getText());
	}
	
	private void validatePackage(String className){			
		if(className.trim().length() == 0){
			updateStatus("Please specify a Class name");
			return;			
		}		
		if(!className.trim().substring(0, 1).matches("[A-Z]")){
			updateStatus("Please capitalize the first letter.");
			return;	
		}		
		if(!validateAttrsPresence()){
			return;
		}
		updateStatus(null);
	}
	
	private boolean validateAttrsPresence(){
		if(table.getItemCount() == 0){
			updateStatus("Please add at least 1 attribute");
			return false;
		}
		updateStatus(null);
		return true;
	}
	
	private void validateAttrName(String attributeName){
		if(attributeName.trim().substring(0, 1).matches("[A-Z]")){
			updateStatus("Attribute Name must start with a lowercase letter.");
			return;
		}
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getDomainClassName(){
		return textSampleDomainClass.getText();
	}
	
	public String getClassSource(String domainPackageName){
		TableItem[] tableItems = table.getItems();
				
		for(TableItem tableItem : tableItems){
			String dataType = tableItem.getText(1);
			String qualifiedType = "";
			if(dataType.equals("String"))
				qualifiedType = "String";
			else if(dataType.equals("Date"))
				qualifiedType = "java.util.Date";
			else if(dataType.equals("Number"))
				qualifiedType = "Integer";
			attrs.put(tableItem.getText(0), qualifiedType);
		}
		
		InputStream is = 
				TemplateMerger.merge("/bsbuilder/resources/java/class.java-template", domainPackageName,textSampleDomainClass.getText(), "" ,attrs);
		BufferedReader br 	= new BufferedReader(new InputStreamReader(is));
		String line = "";
		StringWriter stringWriter = new StringWriter();
		try{
		while((line = br.readLine())!= null){
			stringWriter.write(line + "\n");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return stringWriter.toString();
	}
	
	public String getControllerSource(String basePackageName, String controllerPackageName, String domainClassName){				
		//InputStream is = 
		//		TemplateMerger.merge("/bsbuilder/resources/java/controller.java-template", controllerPackageName,textSampleDomainClass.getText());
		Map<String, Object> valuesToPlug = new LinkedHashMap<String, Object>();
		valuesToPlug.put("basePackageName", basePackageName);
		valuesToPlug.put("domainClassName", domainClassName);
		valuesToPlug.put("controllerPackageName", controllerPackageName);
		InputStream is = 
				TemplateMerger.merge("/bsbuilder/resources/java/controller.java-template", valuesToPlug);

		
		BufferedReader br 	= new BufferedReader(new InputStreamReader(is));
		String line = "";
		StringWriter stringWriter = new StringWriter();
		try{
		while((line = br.readLine())!= null){
			stringWriter.write(line + "\n");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return stringWriter.toString();
	}
	
	public Map<String, Object> getModelAttributes(){
		return attrs;
	}
	
	public String getDomainClassAttributeName(){
		String idAttrName = "";
		TableItem[] tableItems = table.getItems();			
		for(int i = 0; i < tableItems.length; i++){
			if("X".equals(tableItems[i].getText(2))){
				idAttrName = tableItems[i].getText(0);
			}
		}
		return idAttrName;
	}

}
