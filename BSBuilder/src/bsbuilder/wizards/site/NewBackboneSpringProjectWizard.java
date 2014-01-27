package bsbuilder.wizards.site;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import bsbuilder.wizards.site.utils.CommonUtils;

import bsbuilder.wizards.site.utils.TemplateMerger;

public class NewBackboneSpringProjectWizard extends Wizard implements
		INewWizard, IExecutableExtension {

	/*
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse
	 * framework.
	 */
	private WizardNewProjectCreationPage wizardPage;
	private BackboneProjectWizardPageTwo pageTwo;
	private BackboneProjectWizardPageThree pageThree;
	private BackboneProjectWizardPageFour pageFour;

	private IConfigurationElement config;

	private IWorkbench workbench;

	private IStructuredSelection selection;

	private IProject project;
	
	private Map<String, IFolder> folders = new HashMap<String, IFolder>();

	@Override
	public void addPages() {
		/*
		 * Unlike the custom new wizard, we just add the pre-defined one and
		 * don't necessarily define our own.
		 */
		wizardPage = new WizardNewProjectCreationPage(
				"NewExampleComSiteProject");
		wizardPage.setDescription("Create a new Pre-Scaffolded Backbone/Spring Web Project.");
		wizardPage.setTitle("Pre-Scaffolded Backbone/Spring Web Project");
		addPage(wizardPage);
		
		pageTwo = new BackboneProjectWizardPageTwo("test");
		addPage(pageTwo);
		
		pageThree = new BackboneProjectWizardPageThree("");
		addPage(pageThree);
		
		pageFour = new BackboneProjectWizardPageFour("securityOptions");
		addPage(pageFour);
		
	}

	@Override
	public boolean performFinish() {

		try{
		if (project != null) {
			return true;
		}

		final IProject projectHandle = wizardPage.getProjectHandle();

		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		desc.setLocationURI(projectURI);
		
		//The following all go in the .project file
		desc.setNatureIds(new String[] {"org.eclipse.jem.workbench.JavaEMFNature",
				"org.eclipse.wst.common.modulecore.ModuleCoreNature",
				"org.eclipse.jdt.core.javanature",
				"org.eclipse.m2e.core.maven2Nature",
				"org.eclipse.wst.common.project.facet.core.nature",
				"org.eclipse.wst.jsdt.core.jsNature"});
		ICommand[] commands = 
				new ICommand[] { desc.newCommand(), desc.newCommand(),
				desc.newCommand(), desc.newCommand(),desc.newCommand()};
		commands[0].setBuilderName("org.eclipse.wst.jsdt.core.javascriptValidator");
		commands[1].setBuilderName("org.eclipse.jdt.core.javabuilder");
		commands[2].setBuilderName("org.eclipse.wst.common.project.facet.core.builder");
		commands[3].setBuilderName("org.eclipse.m2e.core.maven2Builder");
		commands[4].setBuilderName("org.eclipse.wst.validation.validationbuilder");
		desc.setBuildSpec(commands);
		final boolean xssSelected = pageFour.getXssCheckbox().getSelection();
		final boolean csrfSelected = pageFour.getCsrfCheckbox().getSelection();
		
		final String basePackageName = pageTwo.getBasePackageName(); 
		final String controllerPackageName = pageTwo.getControllerPackage();
		final String domainPackageName = pageTwo.getDomainPackage();
		//final String utilPackageName = pageTwo.getBasePackageName() + ".util"; 
		
		final String domainClassName = pageThree.getDomainClassName();
		final String domainClassSourceCode = pageThree.getClassSource(basePackageName, domainPackageName, xssSelected || csrfSelected);
		final String domainClassIdAttributeName = pageThree.getDomainClassAttributeName();
		final String controllerClassName = domainClassName + "Controller";
		//final String mainControllerSourceCode = pageThree.getMainControllerSource(controllerPackageName, utilPackageName);
		//final String domainControllerSourceCode = pageThree.getControllerSource(basePackageName, controllerPackageName, domainClassName, domainClassIdAttributeName);
		final String controllerTestSourceCode = pageThree.getControllerTestSource(basePackageName, controllerPackageName, domainClassName);
		final String servicePackageName = pageTwo.getBasePackageName() + ".service";
		final String daoPackageName = pageTwo.getBasePackageName() + ".dao";
		final String commonPackageName = pageTwo.getBasePackageName() + ".common";
		final String securityPackageName = pageTwo.getBasePackageName() + ".security";
		
		
		final SourceCodeGeneratorParameters params = new SourceCodeGeneratorParameters();
		params.setBasePackageName(basePackageName);
		params.setControllerPackageName(controllerPackageName);
		params.setDomainPackageName(domainPackageName);
		params.setDomainClassName(domainClassName);
		params.setDomainClassSourceCode(domainClassSourceCode);
		params.setDomainClassIdAttributeName(domainClassIdAttributeName);
		params.setControllerClassName(controllerClassName);
		params.setMainControllerSourceCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "common-controller.java-template")); 
		params.setDomainControllerSourceCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "controller.java-template"));
		params.setControllerTestSourceCode(controllerTestSourceCode);
		params.setServicePackageName(servicePackageName);
		//params.setServiceSourceCode(pageThree.getSeviceSourceCode(basePackageName, servicePackageName, domainClassName, domainClassIdAttributeName));
		params.setServiceSourceCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "service.java-template"));
		
		params.setDaoPackageName(daoPackageName);
		//params.setDaoSourceCode(pageThree.getDaoSourceCode(basePackageName, daoPackageName, domainClassName, domainClassIdAttributeName));
		params.setDaoSourceCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "dao.java-template"));
				
		params.setCommonPackageName(commonPackageName);
		params.setListWrapperSourceCode(pageThree.getListWrapperSourceCode(basePackageName, commonPackageName, domainClassName));
		params.setSecurityPackageName(securityPackageName);
		params.setSecurityUserDetailsServiceSourceCode(pageThree.getSecurityUserDetailsServiceSourceCode(securityPackageName));
		params.setSecurityUserDetailsSourceCode(pageThree.getSecurityUserDetailsSourceCode(securityPackageName));
		if(xssSelected || csrfSelected){
			params.setSecurityAspectCode(pageThree.buildSourceCode(basePackageName, domainClassName, "", "security-aspect.java-template"));
			params.setSecuredDomainCode(pageThree.buildSourceCode(basePackageName, domainClassName, "", "security-domain.java-template"));
		}
		params.setSecurityEnumCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "security-annotation-type.java-template"));
		params.setSecurityAnnotationCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "security-field-annotation.java-template"));
		params.setSecurityTokenGeneratorCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "security-token-generator.java-template"));
		
		params.setSampleMessageBundleContent(pageThree.getMessageBundleContent("", "", ""));
		params.setSampleMessageBundleContentEs(pageThree.getMessageBundleContentEs("", "", ""));
		params.setUtilPackageName(basePackageName + ".util");
		//params.setResourceBundleUtilSourceCode(pageThree.getResourceBundleSourceCode(utilPackageName));
		params.setResourceBundleUtilSourceCode(pageThree.buildSourceCode(basePackageName, domainClassName, domainClassIdAttributeName, "exposed-resource-bundle.java-template"));
		
		params.setSampleESAPIProperties(CommonUtils.linesToString(IOUtils.readLines(getClass().getResourceAsStream("/bsbuilder/resources/esapi/ESAPI.properties")),"\n"));
		
		
		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, params , monitor);
			}
		};

		/*
		 * This isn't as robust as the code in the BasicNewProjectResourceWizard
		 * class. Consider beefing this up to improve error handling.
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}

		project = projectHandle;

		if (project == null) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(config);
		BasicNewProjectResourceWizard.selectAndReveal(project, workbench
				.getActiveWorkbenchWindow());

		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * This creates the project in the workspace.
	 * 
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject proj, 
			SourceCodeGeneratorParameters params,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);
			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it
			 * before updating the perspective.
			 */
			IContainer container = (IContainer) proj;			

			/* Add an pom file */
			CommonUtils.addFileToProject(container, new Path("pom.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/pom.xml-template", proj.getName(),
							params.getBasePackageName(), params.getControllerPackageName(), params.getUtilPackageName()), monitor);			

			//call create folders here
			createFolderStructures(container, monitor);			
			
			//add 3rd party JS libs
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("ejs_fulljslint.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/ejs_fulljslint.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.10.2.min.js"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery-1.10.2.min.map"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.10.2.min.map"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("require.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/require.js"), monitor);			
			//CommonUtils.CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.dataTables.js"), 
			//		this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery.dataTables.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("json2.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/json2.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("underscore.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/underscore-min.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid.css"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-paginator.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-paginator.css"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-paginator.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-paginator.js"), monitor);			
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-select-all.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-select-all.css"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backgrid-select-all.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backgrid-select-all.js"), monitor);			
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone-pageable.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone-pageable.js"), monitor);
			//
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap-datepicker.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/bootstrap-datepicker.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("datepicker.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/datepicker.css"), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap.min.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/bootstrap.min.js"), monitor);
			
			
			//ANOMALY, why does text.js have to be outside the libs folder
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("text.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/text.js"), monitor);
						
			Map<String, Object> mapOfValues = new HashMap<String, Object>();
			mapOfValues.put("className",  params.getDomainClassName());
			mapOfValues.put("projectName", proj.getName());
			mapOfValues.put("domainClassIdAttributeName", params.getDomainClassIdAttributeName());			
			mapOfValues.put("domainPackageName", params.getDomainPackageName());
			
			mapOfValues.put("attrs", pageThree.getModelAttributes());
			//CommonUtils.CommonUtils.addFileToProject(yourJsFolder, new Path("components.js"), 
			//		TemplateMerger.merge("/bsbuilder/resources/web/js/components.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/models"), new Path(params.getDomainClassName()  + "Model.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/collections"), new Path(params.getDomainClassName() + "Collection.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(params.getDomainClassName() + "EditView.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(params.getDomainClassName() + "CollectionView.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/globals"), new Path("global.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/libs/global.js", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("main.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/main-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("app.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/app-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("router.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template.js", mapOfValues), monitor);
			
			addVariousSettings(folders.get(".settings"), proj, params.getBasePackageName(), params.getControllerPackageName() ,monitor);
			
			/* Add web-xml file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("web.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/web.xml-template", proj.getName(), params.getBasePackageName(), params.getControllerPackageName(), params.getUtilPackageName()), monitor);
			/* Add Spring servlet dispathcer mapping file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("yourdispatcher-servlet.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/yourdispatcher-servlet.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);
			/* Add Spring context files */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("applicationContext.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/applicationContext.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("spring-security.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/spring-security.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/spring"), new Path("ehcache.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/ehcache.xml-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);

			
			/* Add a java model */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDomainPackageName(), params.getDomainClassName(),
					params.getDomainClassSourceCode() , monitor);
			
			Map<String, Object> modelAttributes = pageThree.getModelAttributes();
			/* Add a default jsp file.  This is dependent on the Java Model generation */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
							TemplateMerger.merge("/bsbuilder/resources/web/jsps/index.jsp-template", mapOfValues), monitor);
			
			/*Add a default CSS */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/css"), new Path("site.css"),
					TemplateMerger.merge("/bsbuilder/resources/css/site.css-template", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap.min.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/css/bootstrap.min.css"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap-theme.min.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/css/bootstrap-theme.min.css"), monitor);
	
	
			
			/*Add a backbone template file.  This is dependent on the Java Model generation.
			 * Instead of using plain html, we are going to use JSP so we can use Spring's
			 * Message Bundles for localization.
			 * */			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/templates"), new Path(params.getDomainClassName() + "EditTemplate.jsp"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template",  mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/templates"), new Path(params.getDomainClassName() + "ListTemplate.jsp"), 
			TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template", mapOfValues), monitor);

			
			/* Add Controllers*/
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName() , "MainController", 
					params.getMainControllerSourceCode() , monitor);			
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getControllerPackageName(), params.getControllerClassName(), 
					params.getDomainControllerSourceCode(), monitor);
			
			/* Add Service */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getServicePackageName(), params.getDomainClassName() + "Service",
					params.getServiceSourceCode() , monitor);			
			
			/* Add DAO */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getDaoPackageName(), params.getDomainClassName() + "DAO",
					params.getDaoSourceCode() , monitor);
			
			/* Add Security */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SampleUserDetailsService",
					params.getSecurityUserDetailsServiceSourceCode() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SampleUserDetails",
					params.getSecurityUserDetailsSourceCode() , monitor);
			if(params.getSecurityAspectCode() != null){
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SecurityAspect",
					params.getSecurityAspectCode() , monitor);
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SecuredDomain",
						params.getSecuredDomainCode() , monitor);
				CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName(), "SecurityTokenGenerator",
						params.getSecurityTokenGeneratorCode() , monitor);
			}
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName() + ".annotation", "EncodeType",
					params.getSecurityEnumCode() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getSecurityPackageName() + ".annotation", "SecuredField",
					params.getSecurityAnnotationCode() , monitor);
			
			/* Add ListWrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getCommonPackageName(), "ListWrapper",
					params.getListWrapperSourceCode() , monitor);
			
			/* Add Resource Bundle wrapper */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), params.getUtilPackageName(), "ExposedResourceBundleMessageSource",
					params.getResourceBundleUtilSourceCode() , monitor);
			
			/* Add message bundles */
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_en.properties",
					params.getSampleMessageBundleContent() , monitor);
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "locales", "messages_es.properties",
					params.getSampleMessageBundleContentEs() , monitor);
			
			CommonUtils.createPackageAndClass(folders.get("src/main/resources"), "", "ESAPI.properties",
					params.getSampleESAPIProperties() , monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/resources"), new Path("log4j.properties"),
					TemplateMerger.merge("/bsbuilder/resources/other/log4j.properties-template", proj.getName(),params.getBasePackageName(),params.getControllerPackageName(), params.getUtilPackageName()), monitor);

			
			
			//add junit for Controllers
			CommonUtils.createPackageAndClass(folders.get("src/test/java"), params.getControllerPackageName(),
					params.getControllerClassName() + "Test", params.getControllerTestSourceCode() , monitor);
			
			
			IJavaProject javaProject = JavaCore.create(proj);
			
			//Create classpath entries which really creates the ".classpath" file of the Eclipse project
			createClassPathEntries(folders.get("target/classes"), folders.get("target/test-classes"), folders.get("src/main/java"),
					folders.get("src/test/java"), folders.get("src/main/resources"), folders.get("src/test/resources"), javaProject);
			
			//add bsbuilder-specific settings
			addBSBuilderSettings(folders.get(".settings"), project, params.getBasePackageName(), monitor);
			
		} catch (Throwable ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}
	
	private void createFolderStructures(IContainer container, IProgressMonitor monitor)
		throws Exception
	{
		//target
		IFolder outputFolder = container.getFolder(new Path("target"));
		outputFolder.create(true, true, monitor);
		folders.put("target", outputFolder);

		//target/classes
		IFolder outputFolder2 = outputFolder.getFolder(new Path("classes"));
		outputFolder2.create(true, true, monitor);
		folders.put("target/classes", outputFolder2);
		
		//target/test-classes
		IFolder outputFolder3 = outputFolder.getFolder(new Path("test-classes"));
		outputFolder3.create(true, true, monitor);	
		folders.put("target/test-classes", outputFolder3);

		//
		IFolder srcFolder = container.getFolder(new Path("src"));
		//src/main/java
		srcFolder.create(false, true, new NullProgressMonitor());		
		IFolder srcFolder21 = srcFolder.getFolder(new Path("main"));
		srcFolder21.create(false, true, new NullProgressMonitor());
		IFolder srcFolder31 = srcFolder21.getFolder(new Path("java"));
		srcFolder31.create(false, true, new NullProgressMonitor());
		folders.put("src/main/java", srcFolder31);
		
		//src/main/resources
		IFolder srcFolder42 = srcFolder21.getFolder(new Path("resources"));
		srcFolder42.create(false, true, new NullProgressMonitor());
		folders.put("src/main/resources", srcFolder42);
		
		//src/test/java
		IFolder srcFolder22 = srcFolder.getFolder(new Path("test"));
		srcFolder22.create(false, true, new NullProgressMonitor());
		IFolder srcFolder32 = srcFolder22.getFolder(new Path("java"));
		srcFolder32.create(false, true, new NullProgressMonitor());
		folders.put("src/test/java", srcFolder32);
		
		//src/test/resources
		IFolder srcFolder43 = srcFolder22.getFolder(new Path("resources"));
		srcFolder43.create(false, true, new NullProgressMonitor());
		folders.put("src/test/resources", srcFolder43);
		
		//src/main/webapp
		IFolder srcFolder41 = srcFolder21.getFolder(new Path("webapp"));
		srcFolder41.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp", srcFolder41);
		
		//src/main/webapp/WEB-INF
		IFolder srcFolder51 = srcFolder41.getFolder(new Path("WEB-INF"));
		srcFolder51.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF", srcFolder51);
		
		//.settings
		IFolder settingsFolder = container.getFolder(new Path(".settings"));
		settingsFolder.create(false, true, new NullProgressMonitor());
		folders.put(".settings", settingsFolder);
		
		//src/main/webapp/WEB-INF/spring
		IFolder springFolder = srcFolder51.getFolder(new Path("spring"));
		springFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/spring", springFolder);
		
		//src/main/webapp/WEB-INF/resources
		IFolder resourcesFolder = srcFolder51.getFolder(new Path("resources"));
		resourcesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources", resourcesFolder);
		
		//src/main/webapp/WEB-INF/resources/css
		IFolder cssFolder = resourcesFolder.getFolder(new Path("css"));
		cssFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/css", cssFolder);
		
		//src/main/webapp/WEB-INF/resources/js
		IFolder jsFolder = resourcesFolder.getFolder(new Path("js"));
		jsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js", jsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/libs
		IFolder jsLibsFolder = jsFolder.getFolder(new Path("libs"));
		jsLibsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/libs", jsLibsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/models
		IFolder modelsFolder = jsFolder.getFolder(new Path("models"));
		modelsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/models", modelsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/collections
		IFolder collectionsFolder = jsFolder.getFolder(new Path("collections"));
		collectionsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/collections", collectionsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/globals
		IFolder globalsFolder = jsFolder.getFolder(new Path("globals"));
		globalsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/globals", globalsFolder);
		
		//src/main/webapp/WEB-INF/resources/js/views
		IFolder viewsFolder = jsFolder.getFolder(new Path("views"));
		viewsFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/js/views", viewsFolder);
		
		//src/main/webapp/WEB-INF/resources/templates
		IFolder templatesFolder = resourcesFolder.getFolder(new Path("templates"));
		templatesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources/templates", templatesFolder);
	}
	
	
	


	private void createClassPathEntries(IFolder outputFolder2,
			IFolder outputFolder3, IFolder srcFolder31, IFolder srcFolder32,
			IFolder mainResourcesFolder, IFolder testResourcesFolder,
			IJavaProject javaProject) throws JavaModelException {
		IClasspathEntry javasrc = JavaCore.newSourceEntry(srcFolder31.getFullPath(), null, null, outputFolder2.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry mainResources = JavaCore.newSourceEntry(mainResourcesFolder.getFullPath(), null, null, outputFolder2.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry testsrc = JavaCore.newSourceEntry(srcFolder32.getFullPath(), null, null, outputFolder3.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		IClasspathEntry testResources = JavaCore.newSourceEntry(testResourcesFolder.getFullPath(), null, null, outputFolder3.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		
		IClasspathEntry jre = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5"),
				new IAccessRule[0], 
				new IClasspathAttribute[] { JavaCore.newClasspathAttribute("maven.pomderived", "true")}, false);
		
		IClasspathEntry mavenContainer = JavaCore.newContainerEntry(new Path("org.eclipse.m2e.MAVEN2_CLASSPATH_CONTAINER"),
				new IAccessRule[0], 
				new IClasspathAttribute[] { 
						JavaCore.newClasspathAttribute("maven.pomderived", "true"),
			 			JavaCore.newClasspathAttribute("org.eclipse.jst.component.dependency", "/WEB-INF/lib")
					}, false);			

		IClasspathEntry[] entries = new IClasspathEntry[] { javasrc,testsrc, mainResources, testResources ,jre, mavenContainer };			
		javaProject.setRawClasspath(entries, outputFolder2.getFullPath(), new NullProgressMonitor());
	}

	
	private void addVariousSettings(IFolder settingsFolder, IProject project, String basePackageName, String controllerPackageName, IProgressMonitor monitor)
		throws Exception{
		//TODO Add value substitution capability for setting values within settings files
		InputStream jdtCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.jdt.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.jdt.core.prefs"),
				jdtCorePref, monitor);
		
		InputStream m2eCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.m2e.core.prefs.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.m2e.core.prefs"),
				m2eCorePref, monitor);
		
		//InputStream wstCommonComponent = this.getClass().getResourceAsStream(
		//		"/bsbuilder/resources/settings/org.eclipse.wst.common.component.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.component"),
				TemplateMerger.merge("/bsbuilder/resources/settings/org.eclipse.wst.common.component.template", project.getName(),basePackageName,controllerPackageName, ""), monitor);
		
		InputStream wstCommonProject = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.common.project.facet.core.xml.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.project.facet.core.xml"),
				wstCommonProject, monitor);
		
		InputStream wstJsdtContainer = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.container.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.container"),
				wstJsdtContainer, monitor);
		
		InputStream wstJsdtName = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.name.template");
		CommonUtils.addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.name"),
				wstJsdtName, monitor);
		
		InputStream jsdtScope = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/jsdtscope.template");
		CommonUtils.addFileToProject(settingsFolder, new Path(".jsdtscope"),
				jsdtScope, monitor);
	}
	
	private void addBSBuilderSettings(IFolder settingsFolder, IProject project, 
			String basePackageName, IProgressMonitor monitor)
			throws Exception{		
		String props = "basePackage=" + basePackageName;
        InputStream stream = new ByteArrayInputStream(props.getBytes());
		CommonUtils.addFileToProject(settingsFolder, new Path("org.bsbuilder.settings"),
				stream, monitor);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	/**
	 * Sets the initialization data for the wizard.
	 */
	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}

	public class SourceCodeGeneratorParameters{
		private String basePackageName; 
		private String controllerPackageName; 
		private String mainControllerSourceCode;
		private String controllerClassName; 			
		private String domainControllerSourceCode;
		private String controllerTestSourceCode;
		private String domainPackageName; 
		private String domainClassName; 
		private String domainClassSourceCode;
		private String domainClassIdAttributeName;
		private String servicePackageName;
		private String serviceSourceCode;
		private String daoPackageName;
		private String daoSourceCode;
		private String commonPackageName;
		private String listWrapperSourceCode;
		private String securityPackageName;
		private String securityUserDetailsServiceSourceCode;
		private String securityUserDetailsSourceCode;
		private String securityAspectCode;
		private String securityEnumCode;
		private String securityAnnotationCode;
		private String securedDomainCode;
		private String securityTokenGeneratorCode;
		private String utilPackageName;
		private String resourceBundleUtilSourceCode;
		private String sampleMessageBundleContent;
		private String sampleMessageBundleContentEs;
		private String sampleESAPIProperties;
		
		
		public String getBasePackageName() {
			return basePackageName;
		}
		public void setBasePackageName(String basePackageName) {
			this.basePackageName = basePackageName;
		}
		public String getControllerPackageName() {
			return controllerPackageName;
		}
		public void setControllerPackageName(String controllerPackageName) {
			this.controllerPackageName = controllerPackageName;
		}
		public String getMainControllerSourceCode() {
			return mainControllerSourceCode;
		}
		public void setMainControllerSourceCode(String mainControllerSourceCode) {
			this.mainControllerSourceCode = mainControllerSourceCode;
		}
		public String getControllerClassName() {
			return controllerClassName;
		}
		public void setControllerClassName(String controllerClassName) {
			this.controllerClassName = controllerClassName;
		}
		public String getDomainControllerSourceCode() {
			return domainControllerSourceCode;
		}
		public void setDomainControllerSourceCode(String domainControllerSourceCode) {
			this.domainControllerSourceCode = domainControllerSourceCode;
		}
		public String getControllerTestSourceCode() {
			return controllerTestSourceCode;
		}
		public void setControllerTestSourceCode(String controllerTestSourceCode) {
			this.controllerTestSourceCode = controllerTestSourceCode;
		}
		public String getDomainPackageName() {
			return domainPackageName;
		}
		public void setDomainPackageName(String domainPackageName) {
			this.domainPackageName = domainPackageName;
		}
		public String getDomainClassName() {
			return domainClassName;
		}
		public void setDomainClassName(String domainClassName) {
			this.domainClassName = domainClassName;
		}
		public String getDomainClassSourceCode() {
			return domainClassSourceCode;
		}
		public void setDomainClassSourceCode(String domainClassSourceCode) {
			this.domainClassSourceCode = domainClassSourceCode;
		}
		public String getDomainClassIdAttributeName() {
			return domainClassIdAttributeName;
		}
		public void setDomainClassIdAttributeName(String domainClassIdAttributeName) {
			this.domainClassIdAttributeName = domainClassIdAttributeName;
		}
		public String getServicePackageName() {
			return servicePackageName;
		}
		public void setServicePackageName(String servicePackageName) {
			this.servicePackageName = servicePackageName;
		}
		public String getServiceSourceCode() {
			return serviceSourceCode;
		}
		public void setServiceSourceCode(String serviceSourceCode) {
			this.serviceSourceCode = serviceSourceCode;
		}
		public String getDaoPackageName() {
			return daoPackageName;
		}
		public void setDaoPackageName(String daoPackageName) {
			this.daoPackageName = daoPackageName;
		}
		public String getDaoSourceCode() {
			return daoSourceCode;
		}
		public void setDaoSourceCode(String daoSourceCode) {
			this.daoSourceCode = daoSourceCode;
		}
		public String getListWrapperSourceCode() {
			return listWrapperSourceCode;
		}
		public void setListWrapperSourceCode(String listWrapperSourceCode) {
			this.listWrapperSourceCode = listWrapperSourceCode;
		}
		public String getCommonPackageName() {
			return commonPackageName;
		}
		public void setCommonPackageName(String commonPackageName) {
			this.commonPackageName = commonPackageName;
		}
		public String getSecurityPackageName() {
			return securityPackageName;
		}
		public void setSecurityPackageName(String securityPackageName) {
			this.securityPackageName = securityPackageName;
		}
		public String getSecurityUserDetailsServiceSourceCode() {
			return securityUserDetailsServiceSourceCode;
		}
		public void setSecurityUserDetailsServiceSourceCode(
				String securityUserDetailsServiceSourceCode) {
			this.securityUserDetailsServiceSourceCode = securityUserDetailsServiceSourceCode;
		}
		public String getSampleMessageBundleContent() {
			return sampleMessageBundleContent;
		}
		public void setSampleMessageBundleContent(String sampleMessageBundleContent) {
			this.sampleMessageBundleContent = sampleMessageBundleContent;
		}
		public String getSampleMessageBundleContentEs() {
			return sampleMessageBundleContentEs;
		}
		public void setSampleMessageBundleContentEs(String sampleMessageBundleContentEs) {
			this.sampleMessageBundleContentEs = sampleMessageBundleContentEs;
		}
		public String getUtilPackageName() {
			return utilPackageName;
		}
		public void setUtilPackageName(String utilPackageName) {
			this.utilPackageName = utilPackageName;
		}
		public String getResourceBundleUtilSourceCode() {
			return resourceBundleUtilSourceCode;
		}
		public void setResourceBundleUtilSourceCode(String resourceBundleUtilSourceCode) {
			this.resourceBundleUtilSourceCode = resourceBundleUtilSourceCode;
		}
		public String getSecurityUserDetailsSourceCode() {
			return securityUserDetailsSourceCode;
		}
		public void setSecurityUserDetailsSourceCode(
				String securityUserDetailsSourceCode) {
			this.securityUserDetailsSourceCode = securityUserDetailsSourceCode;
		}
		public String getSampleESAPIProperties() {
			return sampleESAPIProperties;
		}
		public void setSampleESAPIProperties(String sampleESAPIProperties) {
			this.sampleESAPIProperties = sampleESAPIProperties;
		}
		public String getSecurityAspectCode() {
			return securityAspectCode;
		}
		public void setSecurityAspectCode(String securityAspectCode) {
			this.securityAspectCode = securityAspectCode;
		}
		public String getSecuredDomainCode() {
			return securedDomainCode;
		}
		public void setSecuredDomainCode(String securedDomainCode) {
			this.securedDomainCode = securedDomainCode;
		}
		public String getSecurityEnumCode() {
			return securityEnumCode;
		}
		public void setSecurityEnumCode(String securityEnumCode) {
			this.securityEnumCode = securityEnumCode;
		}
		public String getSecurityAnnotationCode() {
			return securityAnnotationCode;
		}
		public void setSecurityAnnotationCode(String securityAnnotationCode) {
			this.securityAnnotationCode = securityAnnotationCode;
		}
		public String getSecurityTokenGeneratorCode() {
			return securityTokenGeneratorCode;
		}
		public void setSecurityTokenGeneratorCode(String securityTokenGeneratorCode) {
			this.securityTokenGeneratorCode = securityTokenGeneratorCode;
		}	
		
	}

}
