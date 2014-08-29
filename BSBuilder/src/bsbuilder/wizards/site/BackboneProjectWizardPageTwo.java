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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class BackboneProjectWizardPageTwo extends WizardPage {
	private Text textBasePackage;
	private Text textControllerPackage;
	private Text textDomainPackage;
	private Text textMongoHost;
	private Text textMongoPort;
	private Text textMongoDBName;
	private Button btnUseMongodb;
	private Button btnPrepForOracle;
	private Button btnPrepForMysql;

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
				textDomainPackage.setText(textBasePackage.getText() + ".web.domain" );
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
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Domain Package:");
		
		textDomainPackage = new Text(container, SWT.BORDER);
		textDomainPackage.setEditable(false);
		textDomainPackage.setEnabled(false);
		textDomainPackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		btnUseMongodb = new Button(container, SWT.CHECK);
		btnUseMongodb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if(btnUseMongodb.getSelection()){
					textMongoHost.setEnabled(true);
					textMongoPort.setEnabled(true);
					textMongoDBName.setEnabled(true);
				}else{
					textMongoHost.setEnabled(false);
					textMongoPort.setEnabled(false);
					textMongoDBName.setEnabled(false);
				}
			}
		});
		btnUseMongodb.setText("Use MongoDB");
		new Label(container, SWT.NONE);
		
		Group grpMongoDbParams = new Group(container, SWT.NONE);
		GridData gd_grpMongoDbParams = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_grpMongoDbParams.widthHint = 411;
		gd_grpMongoDbParams.heightHint = 133;
		grpMongoDbParams.setLayoutData(gd_grpMongoDbParams);
		grpMongoDbParams.setText("Mongo DB Params");
		
		Label lblNewLabel_2 = new Label(grpMongoDbParams, SWT.NONE);
		lblNewLabel_2.setAlignment(SWT.RIGHT);
		lblNewLabel_2.setBounds(23, 32, 102, 17);
		lblNewLabel_2.setText("Host Name:");
		
		Label lblNewLabel_3 = new Label(grpMongoDbParams, SWT.NONE);
		lblNewLabel_3.setAlignment(SWT.RIGHT);
		lblNewLabel_3.setBounds(23, 76, 102, 17);
		lblNewLabel_3.setText("Port:");
		
		Label lblDbName = new Label(grpMongoDbParams, SWT.NONE);
		lblDbName.setAlignment(SWT.RIGHT);
		lblDbName.setBounds(23, 120, 102, 17);
		lblDbName.setText("DB Name:");
		
		textMongoHost = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoHost.setText("localhost");
		textMongoHost.setEnabled(false);
		textMongoHost.setBounds(131, 27, 246, 27);
		
		textMongoPort = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoPort.setText("27017");
		textMongoPort.setEnabled(false);
		textMongoPort.setBounds(132, 71, 102, 27);
		
		textMongoDBName = new Text(grpMongoDbParams, SWT.BORDER);
		textMongoDBName.setText("localdb");
		textMongoDBName.setEnabled(false);
		textMongoDBName.setBounds(131, 115, 149, 27);
		new Label(container, SWT.NONE);
		
		btnPrepForMysql = new Button(container, SWT.CHECK);
		btnPrepForMysql.setText("Prep For MySQL");
		new Label(container, SWT.NONE);
		
		btnPrepForOracle = new Button(container, SWT.CHECK);
		GridData gd_btnPrepForOracle = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnPrepForOracle.heightHint = 33;
		btnPrepForOracle.setLayoutData(gd_btnPrepForOracle);
		btnPrepForOracle.setText("Prep For Oracle");
		
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
	
	public String getDomainPackage(){
		return textDomainPackage.getText();
	}
	
	public boolean useMongoDB(){
		return btnUseMongodb.getSelection();
	}
	
	public String getMongoHostName(){
		return textMongoHost.getText();
	}
	
	public String getMongoPort(){
		return textMongoPort.getText();
	}
	
	public String getMongoDBName(){
		return textMongoDBName.getText();
	}
	
	public boolean prepForOracle(){
		return btnPrepForOracle.getSelection();
	}
	
	public boolean prepForMySql(){
		return btnPrepForMysql.getSelection();
	}

}
