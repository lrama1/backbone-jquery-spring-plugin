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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.events.TouchEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
		layout.numColumns = 3;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
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
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(206);
		tblclmnNewColumn_1.setText("Attribute");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Data Type");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		
		
		final TableEditor editor = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
				
		final TableEditor editor2 = new TableEditor(table);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor2.horizontalAlignment = SWT.LEFT;
		editor2.grabHorizontal = true;
		editor2.minimumWidth = 50;

		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
				
				// Clean up any previous editor control
				Control oldEditor2 = editor2.getEditor();
				if (oldEditor2 != null) oldEditor2.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) return;
		
				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(0));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text)editor.getEditor();
						editor.getItem().setText(0, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 0);
				
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
			}
		});
		
		
		
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(0, "changeMe");
				tableItem.setText(1, "String");
			}
		});
		btnNewButton.setText("Add Row");
		
		Button btnNewButton_1 = new Button(container, SWT.NONE);
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] tableItems = table.getItems();
				for(int i = 0; i < tableItems.length; i++){
					System.out.println(tableItems[i].getText(0));
					System.out.println(tableItems[i].getText(1));
					System.out.println();
				}
			}
		});
		btnNewButton_1.setText("New Button");
		new Label(container, SWT.NONE);
		
		
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
		updateStatus(null);
	}
	
	private void validateAllAttributes(TableItem[] tableItems){
		for(int i = 0; i < tableItems.length; i++){
			String attributeName = tableItems[i].getText(0);
			if(attributeName.trim().substring(0, 1).matches("[A-Z]")){
				updateStatus("Please lowercase the first letter of attribute " + attributeName);
				return;
			}			
		}
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
			attrs.put(tableItem.getText(0), tableItem.getText(1));
		}
		
		InputStream is = 
				TemplateMerger.merge("/bsbuilder/resources/java/class.java-template", domainPackageName,textSampleDomainClass.getText(), attrs);
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

}
