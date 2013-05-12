package bsbuilder.wizards.backbone;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bsbuilder.wizards.site.BackboneProjectWizardPageThree;

public class BackboneNewModelWizard extends Wizard implements INewWizard {

	private BackboneProjectWizardPageThree pageThree;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		//ResourcesPlugin.getWorkspace().getRoot().get
		
		Object object = selection.getFirstElement();
		/*System.out.println(object.getClass().getName());
		System.out.println();
		if (object instanceof IResource){
			Object obj = ((IResource)object).getParent();
			System.out.println();
		}
		System.out.println();*/
		try{
			Method method = getParentMethod(object.getClass().getMethods());
			while(method != null){
				object = method.invoke(object);	
				if(object != null){
					System.out.println(object.getClass().getName());
					method = getParentMethod(object.getClass().getMethods());
				}
				else{
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Method getParentMethod(Method[] methods){
		for(Method method : methods){
			if (method.getName().equals("getParent")){
				return method;
			}
		}
		return null;
	}
	
	@Override
	public void addPages() {
		pageThree = new BackboneProjectWizardPageThree("");
		addPage(pageThree);
	}

	@Override
	public boolean performFinish() {
		
		System.out.println();
		IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                	//createNewModel();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
		return true;
	}
	
	private void createNewModel(){		
//		IContainer container = (IContainer) proj;
//		IFolder srcFolder = container.getFolder(new Path("src"));
//		IFolder srcFolder21 = srcFolder.getFolder(new Path("main"));
//		IFolder srcFolder41 = srcFolder21.getFolder(new Path("webapp"));
//		IFolder srcFolder51 = srcFolder41.getFolder(new Path("WEB-INF"));
//		IFolder resourcesFolder = srcFolder51.getFolder(new Path("resources"));
//		IFolder jsFolder = resourcesFolder.getFolder(new Path("js"));
//		IFolder modelsFolder = jsFolder.getFolder(new Path("models"));
	}

}
