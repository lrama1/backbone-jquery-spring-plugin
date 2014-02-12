package bsbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;


public class BackboneProjectWizardPageFive extends WizardPage {

	public BackboneProjectWizardPageFive(String pageName) {
		super(pageName);
		setTitle("Backbone MVC Options");
		setDescription("Some options to customize the Backbone Code");
	}

	Button btnRadioButton;
	Button btnRadioButton_1;
	
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
		new Label(container, SWT.NONE);
		
		btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.setText("Generate JSP Templates");
		btnRadioButton.setSelection(true);
		new Label(container, SWT.NONE);
		
		btnRadioButton_1 = new Button(container, SWT.RADIO);
		btnRadioButton_1.setText("Generate Plain HTML Templates");
		
	}
	
	public boolean isJSPTemplate(){
		return btnRadioButton.getSelection();
	}
	
	public boolean isHTMLTemplate(){
		return btnRadioButton_1.getSelection();
	}

}
