package bsbuilder.wizards.site;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;




import bsbuilder.wizards.backbone.BackbonePageNewWizard;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class NewBackboneSpringProjectWizard extends Wizard implements
		INewWizard, IExecutableExtension {

	/*
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse
	 * framework.
	 */
	private WizardNewProjectCreationPage wizardPage;

	private IConfigurationElement config;

	private IWorkbench workbench;

	private IStructuredSelection selection;

	private IProject project;

	
	public void addPages() {
		/*
		 * Unlike the custom new wizard, we just add the pre-defined one and
		 * don't necessarily define our own.
		 */
		wizardPage = new WizardNewProjectCreationPage(
				"NewExampleComSiteProject");
		wizardPage.setDescription("Create a new Example.com Site Project.");
		wizardPage.setTitle("New Example.com Site Project");
		addPage(wizardPage);
		
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

		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, monitor);
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
			//InputStream pomStream = this.getClass().getResourceAsStream(
			//		"/bsbuilder/resources/maven/pom.xml.template");
			addFileToProject(container, new Path("pom.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/pom.xml.template", proj.getName()), monitor);
			
			/* Add the style folder and the site.css file to it */
			final IFolder styleFolder = container.getFolder(new Path("styles"));
			styleFolder.create(true, true, monitor);
			
			InputStream resourceStream = this.getClass().getResourceAsStream(
			"/bsbuilder/resources/css/site-css.template");

			addFileToProject(container, new Path(styleFolder.getName()
					+ Path.SEPARATOR + "style.css"),
					resourceStream, monitor);
			resourceStream.close();
			
			/*
			 * Add the images folder, which is an official Exmample.com standard
			 * for static web projects.
			 */
			IFolder imageFolder = container.getFolder(new Path("images"));
			imageFolder.create(true, true, monitor);
			

			IFolder outputFolder = container.getFolder(new Path("target"));
			outputFolder.create(true, true, monitor);
//
			IFolder outputFolder2 = outputFolder.getFolder(new Path("classes"));
			outputFolder2.create(true, true, monitor);
			
			IFolder outputFolder3 = outputFolder.getFolder(new Path("test-classes"));
			outputFolder3.create(true, true, monitor);
						

			IFolder srcFolder = container.getFolder(new Path("src"));
			srcFolder.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder21 = srcFolder.getFolder(new Path("main"));
			srcFolder21.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder22 = srcFolder.getFolder(new Path("test"));
			srcFolder22.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder31 = srcFolder21.getFolder(new Path("java"));
			srcFolder31.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder32 = srcFolder22.getFolder(new Path("java"));
			srcFolder32.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder41 = srcFolder21.getFolder(new Path("webapp"));
			srcFolder41.create(false, true, new NullProgressMonitor());
			
			IFolder srcFolder51 = srcFolder41.getFolder(new Path("WEB-INF"));
			srcFolder51.create(false, true, new NullProgressMonitor());
			
			IFolder settingsFolder = container.getFolder(new Path(".settings"));
			settingsFolder.create(false, true, new NullProgressMonitor());
			
			addVariousSettings(settingsFolder, proj ,monitor);
			
			/* Add web-xml file */
			//InputStream webXMLStream = this.getClass().getResourceAsStream(
			//		"/bsbuilder/resources/maven/web.xml.template");
			addFileToProject(srcFolder51, new Path("web.xml"),
					TemplateMerger.merge("/bsbuilder/resources/maven/web.xml.template", proj.getName()), monitor);
			
			/* Add a default html file */
			addFileToProject(srcFolder41, new Path("index.html"),
					BackbonePageNewWizard.openContentStream("Welcome to "
							+ proj.getName()), monitor);
			
			IJavaProject javaProject = JavaCore.create(proj);
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
			
		} catch (Throwable ioe) {
			IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK,
					ioe.getLocalizedMessage(), null);
			throw new CoreException(status);
		} finally {
			monitor.done();
		}
	}

	
	private void addVariousSettings(IFolder settingsFolder, IProject project, IProgressMonitor monitor)
		throws Exception{
		//TODO Add value substitution capability for setting values within settings files
		InputStream jdtCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.jdt.core.prefs.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.jdt.core.prefs"),
				jdtCorePref, monitor);
		
		InputStream m2eCorePref = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.m2e.core.prefs.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.m2e.core.prefs"),
				m2eCorePref, monitor);
		
		//InputStream wstCommonComponent = this.getClass().getResourceAsStream(
		//		"/bsbuilder/resources/settings/org.eclipse.wst.common.component.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.component"),
				TemplateMerger.merge("/bsbuilder/resources/settings/org.eclipse.wst.common.component.template", project.getName()), monitor);
		
		InputStream wstCommonProject = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.common.project.facet.core.xml.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.wst.common.project.facet.core.xml"),
				wstCommonProject, monitor);
		
		InputStream wstJsdtContainer = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.container.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.container"),
				wstJsdtContainer, monitor);
		
		InputStream wstJsdtName = this.getClass().getResourceAsStream(
				"/bsbuilder/resources/settings/org.eclipse.wst.jsdt.ui.superType.name.template");
		addFileToProject(settingsFolder, new Path("org.eclipse.wst.jsdt.ui.superType.name"),
				wstJsdtName, monitor);
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

	/**
	 * Adds a new file to the project.
	 * 
	 * @param container
	 * @param path
	 * @param contentStream
	 * @param monitor
	 * @throws CoreException
	 */
	private void addFileToProject(IContainer container, Path path,
			InputStream contentStream, IProgressMonitor monitor)
			throws CoreException {
		final IFile file = container.getFile(path);

		if (file.exists()) {
			file.setContents(contentStream, true, true, monitor);
		} else {
			file.create(contentStream, true, monitor);
		}
	}

}
