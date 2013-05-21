package bsbuilder.wizards.site;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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



import bsbuilder.wizards.backbone.BackbonePageNewWizard;
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
		
	}

	@Override
	public boolean performFinish() {

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

		final String basePackageName = pageTwo.getBasePackageName(); 
		final String controllerPackageName = pageTwo.getControllerPackage();
		final String domainPackageName = pageTwo.getDomainPackage();
		
		final String domainClassName = pageThree.getDomainClassName();
		final String classSourceCode = pageThree.getClassSource(domainPackageName);
		final String domainClassIdAttrName = pageThree.getDomainClassAttributeName();
		final String controllerClassName = domainClassName + "Controller";
		final String controllerSourceCode = pageThree.getControllerSource(basePackageName, controllerPackageName, domainClassName);
		
		
		//generate POJO
		
		
		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, basePackageName, controllerPackageName, 
						controllerClassName, controllerSourceCode,
						domainPackageName, domainClassName ,classSourceCode, domainClassIdAttrName , monitor);
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
			String basePackageName, 
			String controllerPackageName, String controllerClassName, String controllerSourceCode,
			String domainPackageName, 
			String domainClassName, String domainClassSourceCode,
			String domainClassIdAttributeName,
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
							basePackageName,controllerPackageName), monitor);			

			//call create folders here
			createFolderStructures(container, monitor);			
			
			//add 3rd party JS libs
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("ejs_fulljslint.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/ejs_fulljslint.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("jquery.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/jquery-1.9.1.min.js"), monitor);
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
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("backbone-pageable.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/backbone-pageable.js"), monitor);
			//
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap-datepicker.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/bootstrap-datepicker.js"), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("datepicker.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/datepicker.css"), monitor);
			
			//ANOMALY, why does text.js have to be outside the libs folder
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("text.js"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/web/js/libs/text.js"), monitor);
						
			Map<String, Object> mapOfValues = new HashMap<String, Object>();
			mapOfValues.put("className", domainClassName);
			mapOfValues.put("projectName", proj.getName());
			mapOfValues.put("domainClassIdAttributeName", domainClassIdAttributeName);
			mapOfValues.put("attrs", pageThree.getModelAttributes());
			//CommonUtils.CommonUtils.addFileToProject(yourJsFolder, new Path("components.js"), 
			//		TemplateMerger.merge("/bsbuilder/resources/web/js/components.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/models"), new Path(domainClassName + "Model.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/collections"), new Path(domainClassName + "Collection.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(domainClassName + "EditView.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/views"), new Path(domainClassName + "CollectionView.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/globals"), new Path("global.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/libs/global.js", mapOfValues), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("main.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/main-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("app.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/main/app-template.js", mapOfValues), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js"), new Path("router.js"), 
					TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template.js", mapOfValues), monitor);
			
			addVariousSettings(folders.get(".settings"), proj, basePackageName,controllerPackageName ,monitor);
			
			/* Add web-xml file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("web.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/web.xml-template", proj.getName(),basePackageName,controllerPackageName), monitor);
			/* Add Spring servlet dispathcer mapping file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("yourdispatcher-servlet.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/yourdispatcher-servlet.xml-template", proj.getName(),basePackageName,controllerPackageName), monitor);
			/* Add Spring applicationContext file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("applicationContext.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/applicationContext.xml-template", proj.getName(),basePackageName,controllerPackageName), monitor);
						
			/* Add a default html file */
			CommonUtils.addFileToProject(folders.get("src/main/webapp"), new Path("index.html"),
					BackbonePageNewWizard.openContentStream("Welcome to "
							+ proj.getName()), monitor);
					
			/* Add a java model */
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), domainPackageName, domainClassName, domainClassSourceCode , monitor);
			
			Map<String, Object> modelAttributes = pageThree.getModelAttributes();
			/* Add a default jsp file.  This is dependent on the Java Model generation */
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
					TemplateMerger.merge("/bsbuilder/resources/web/jsps/index.jsp-template", proj.getName(),"",""), monitor);
			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/js/libs"), new Path("bootstrap.min.css"), 
					this.getClass().getResourceAsStream("/bsbuilder/resources/css/bootstrap.min.css"), monitor);
	
			
			/*Add a backbone template file.  This is dependent on the Java Model generation*/			
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/templates"), new Path(domainClassName + "EditTemplate.htm"), 
					TemplateMerger.mergeMap("/bsbuilder/resources/web/js/backbone/templates/EditTemplate.htm-template", domainClassName ,modelAttributes ), monitor);
			CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF/resources/templates"), new Path(domainClassName + "ListTemplate.htm"), 
					TemplateMerger.mergeMap("/bsbuilder/resources/web/js/backbone/templates/ListTemplate.htm-template", domainClassName ,modelAttributes ), monitor);

			
			/* Add a Controller*/
			CommonUtils.createPackageAndClass(folders.get("src/main/java"), controllerPackageName, controllerClassName, controllerSourceCode , monitor);
			
			IJavaProject javaProject = JavaCore.create(proj);
			
			//Create classpath entries which really creates the ".classpath" file of the Eclipse project
			createClassPathEntries(folders.get("target/classes"), folders.get("target/test-classes"), folders.get("src/main/java"),
					folders.get("src/test/java"), javaProject);
			
			//add bsbuilder-specific settings
			addBSBuilderSettings(folders.get(".settings"), project, basePackageName, monitor);
			
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
		
		//src/test/java
		IFolder srcFolder22 = srcFolder.getFolder(new Path("test"));
		srcFolder22.create(false, true, new NullProgressMonitor());
		IFolder srcFolder32 = srcFolder22.getFolder(new Path("java"));
		srcFolder32.create(false, true, new NullProgressMonitor());
		folders.put("src/test/java", srcFolder32);
		
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
		
		
		//src/main/webapp/WEB-INF/resources
		IFolder resourcesFolder = srcFolder51.getFolder(new Path("resources"));
		resourcesFolder.create(false, true, new NullProgressMonitor());
		folders.put("src/main/webapp/WEB-INF/resources", resourcesFolder);
		
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
			IJavaProject javaProject) throws JavaModelException {
		IClasspathEntry javasrc = JavaCore.newSourceEntry(srcFolder31.getFullPath(), null, null, outputFolder2.getFullPath(), 
				new IClasspathAttribute[] { 
					JavaCore.newClasspathAttribute("optional", "true"),
					JavaCore.newClasspathAttribute("maven.pomderived", "true")
					});
		IClasspathEntry testsrc = JavaCore.newSourceEntry(srcFolder32.getFullPath(), null, null, outputFolder3.getFullPath(), 
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

		IClasspathEntry[] entries = new IClasspathEntry[] { javasrc,testsrc, jre, mavenContainer };			
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
				TemplateMerger.merge("/bsbuilder/resources/settings/org.eclipse.wst.common.component.template", project.getName(),basePackageName,controllerPackageName), monitor);
		
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

	

}