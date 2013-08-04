package bsbuilder.wizards.site.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class CommonUtils {

	public static void createPackageAndClass(IFolder parentFolder, String packageName, 
			String className, String sourceCode, IProgressMonitor monitor ) throws Exception{
		String[] path = packageName.split("\\.");
		for(int i = 0; i < path.length; i++){
			IFolder javaSrc = parentFolder.getFolder(new Path(path[i]));
			if(!javaSrc.exists())
				javaSrc.create(false, true, new NullProgressMonitor());
			parentFolder = javaSrc;
		}		
		String filename = "";
		if(className.indexOf(".") == -1)
			filename = className + ".java";
		else
			filename = className;
			
		addFileToProject(parentFolder, new Path(filename),
				new ByteArrayInputStream(sourceCode.getBytes()), monitor);		
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
	public static void addFileToProject(IContainer container, Path path,
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
