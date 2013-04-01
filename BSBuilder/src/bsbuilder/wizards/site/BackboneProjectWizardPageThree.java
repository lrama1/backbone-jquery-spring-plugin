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

public class BackboneProjectWizardPageThree extends WizardPage {
	private Text textSampleDomainClass;

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
		layout.numColumns = 2;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Sample Domain Class:");
		
		textSampleDomainClass = new Text(container, SWT.BORDER);
		textSampleDomainClass.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				validatePackage(textSampleDomainClass.getText());
			}
		});
		textSampleDomainClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
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
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

}
