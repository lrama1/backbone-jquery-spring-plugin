package bsbuilder.wizards.site;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class BackboneProjectWizardPageFour extends WizardPage {
	private Button xssCheckbox;
	private Button csrfCheckbox;

	protected BackboneProjectWizardPageFour(String pageName) {
		super(pageName);
		setTitle("Add OWASP Security Features");
		setDescription("Creates scaffolding for security features.");
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
		new Label(container, SWT.NONE);
		
		xssCheckbox = new Button(container, SWT.CHECK);
		GridData gd_xssCheckbox = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_xssCheckbox.widthHint = 251;
		xssCheckbox.setLayoutData(gd_xssCheckbox);
		xssCheckbox.setText("Add code for XSS prevention");
		new Label(container, SWT.NONE);
		
		csrfCheckbox = new Button(container, SWT.CHECK);
		csrfCheckbox.setText("Add code for CSRF prevention");

	}

	public Button getXssCheckbox() {
		return xssCheckbox;
	}
	public Button getCsrfCheckbox() {
		return csrfCheckbox;
	}
}
