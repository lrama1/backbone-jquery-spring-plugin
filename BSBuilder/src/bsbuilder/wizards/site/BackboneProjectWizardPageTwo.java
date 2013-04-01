package bsbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class BackboneProjectWizardPageTwo extends WizardPage {
	private Text textBasePackage;
	private Text textControllerPackage;

	protected BackboneProjectWizardPageTwo(String pageName) {
		super(pageName);
		setTitle("Select Base Package");
		setDescription("Select a base package name to use for your Java Classes.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Base Package Name:");
		
		textBasePackage = new Text(container, SWT.BORDER);
		textBasePackage.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				validatePackage(textBasePackage.getText());
				textControllerPackage.setText(textBasePackage.getText() + ".controller" );
			}
		});
		textBasePackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblControllerPackage = new Label(container, SWT.NONE);
		lblControllerPackage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblControllerPackage.setText("Controller Package:");
		
		textControllerPackage = new Text(container, SWT.BORDER);
		textControllerPackage.setEnabled(false);
		textControllerPackage.setEditable(false);
		textControllerPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		//Validate on display
		validatePackage(textBasePackage.getText());
	}
	
	private void validatePackage(String packageName){
		if(packageName.trim().length() == 0){
			updateStatus("Please specify a package name");
			return;			
		}
		if(packageName.trim().endsWith(".")){
			updateStatus("Please specify a valid format for your package name");
			return;			
		}
		
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getBasePackageName(){
		return textBasePackage.getText();
	}
	
	public String getControllerPackage(){
		return textControllerPackage.getText();
	}

}
