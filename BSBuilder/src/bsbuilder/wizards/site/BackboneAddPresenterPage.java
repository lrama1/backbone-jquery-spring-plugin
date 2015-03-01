package bsbuilder.wizards.site;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.custom.ScrolledComposite;


public class BackboneAddPresenterPage extends WizardPage {
	private Text presenterName;
	org.eclipse.swt.widgets.List backboneViewList;
	private List<String> viewPaths;

	public BackboneAddPresenterPage(String pageName) {
		super(pageName);
		setTitle("Backbone MVC Options");
		setDescription("Add A New Presenter View");
	}
	
	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		layout.verticalSpacing = 9;
		
		setControl(container);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		presenterName = new Text(container, SWT.BORDER);
		presenterName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		backboneViewList = new org.eclipse.swt.widgets.List(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		
		for(String path : viewPaths){
			backboneViewList.add(path);
		}
		
		
		GridData gd_backboneViewList = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_backboneViewList.widthHint = 490;
		gd_backboneViewList.heightHint = 153;
		backboneViewList.setLayoutData(gd_backboneViewList);
		
	}
	
	
	public void setViewNamesFound(List<String> viewPaths){
		this.viewPaths = viewPaths;
	}
	
	public String[] getSelectedViews(){
		return backboneViewList.getSelection();
	}
	
	public String getPresenterName(){
		return presenterName.getText();
	}


}
