package bsbuilder.wizards.backbone;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class BackbonePageNewWizardPage2 extends WizardPage {
	private Text sampleText;

	protected BackbonePageNewWizardPage2(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
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
		lblNewLabel.setText("New Label");
		
		sampleText = new Text(container, SWT.BORDER);
		sampleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

	}

	public Text getSampleText() {
		return sampleText;
	}

	public void setSampleText(Text sampleText) {
		this.sampleText = sampleText;
	}
	
	

	
}
