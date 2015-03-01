package bsbuilder.wizards.backbone.actions;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.core.runtime.Path;

import bsbuilder.wizards.site.BackboneAddPresenterPage;
import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class AddNewPresenterWizard extends Wizard implements INewWizard {

	private BackboneAddPresenterPage addPresenterPage;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	private Properties bsBuilderProperties = new Properties();
	private List<String> viewPaths = new ArrayList<String>();
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		
		Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            String basePath = project.getLocation().toOSString();
			try {
				bsBuilderProperties.load(new FileReader(new File(basePath + "/.settings/" + "org.bsbuilder.settings")));
				IFolder folder = project.getFolder("src/main/webapp/WEB-INF/resources/js/views");

				IResource[] resources =  folder.members(false);
				
				for(IResource resource : resources){
					if(resource instanceof org.eclipse.core.internal.resources.File){
						org.eclipse.core.internal.resources.File jsFile = (org.eclipse.core.internal.resources.File) resource;
						String[] pathSegments = jsFile.getLocation().segments();
						
						System.out.println(pathSegments[pathSegments.length-2] +
								pathSegments[pathSegments.length-1] + "==xx==>" +   resource.getName());
						viewPaths.add(pathSegments[pathSegments.length-2] + "/" +
								pathSegments[pathSegments.length-1]);						
					}
				}
								
				
			} catch (Exception e) {
				//man we really need to clean this up
				e.printStackTrace();
				StatusManager.getManager().handle(new Status(IStatus.ERROR, "BSB", e.getMessage()), StatusManager.BLOCK); 
			} 
		}
	}
	
	@Override
	public void addPages() {
		addPresenterPage = new BackboneAddPresenterPage("");
		addPresenterPage.setViewNamesFound(viewPaths);
		addPage(addPresenterPage);

	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		//CommonUtils.addFileToProject(container, path, contentStream, monitor)
		
		
	    Object firstElement = selection.getFirstElement();
	    if (firstElement instanceof IAdaptable)
        {
            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
            IContainer projectContainer = (IContainer) project;
            try {
				String projectName = project.getName();
				String basePackageName = bsBuilderProperties.getProperty("basePackage");				
				createCompositePresenter(projectContainer, projectName);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	        
		return true;
	}
	
	private void createCompositePresenter(IContainer projectContainer, String projectName) throws Exception{
		IFolder presenterFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/presenters"));
		String presenterName = this.addPresenterPage.getPresenterName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();		
		mapOfValues.put("compositePresenterName", presenterName);
		mapOfValues.put("childViews", arrayToList(addPresenterPage.getSelectedViews()));
		
		CommonUtils.addFileToProject(presenterFolder, new Path(presenterName + ".js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/composite-presenter-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private Map<String, String> arrayToList(String[] selectedViews){
		Map<String, String> views = new HashMap<String, String>();
		for(String viewPath : selectedViews){
			String viewName = viewPath.substring(viewPath.lastIndexOf("/")+1, viewPath.lastIndexOf('.'));
			views.put(viewPath.replace(" ","").replace(".js", ""), viewName);
		}
		return views;
	}
	
	

}
