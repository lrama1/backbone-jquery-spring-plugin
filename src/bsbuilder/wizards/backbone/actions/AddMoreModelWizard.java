package bsbuilder.wizards.backbone.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bsbuilder.wizards.site.BackboneProjectWizardPageFive;
import bsbuilder.wizards.site.BackboneProjectWizardPageFour;
import bsbuilder.wizards.site.BackboneProjectWizardPageThree;
import bsbuilder.wizards.site.utils.CommonUtils;
import bsbuilder.wizards.site.utils.TemplateMerger;

public class AddMoreModelWizard extends Wizard implements INewWizard {

	private BackboneProjectWizardPageThree pageThree;
	private BackboneProjectWizardPageFour pageFour;
	private BackboneProjectWizardPageFive pageFive;
	private IWorkbench workbench;
	private IStructuredSelection selection;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}
	
	@Override
	public void addPages() {
		pageThree = new BackboneProjectWizardPageThree("");
		pageFour = new BackboneProjectWizardPageFour("");
		pageFive = new BackboneProjectWizardPageFive("");
		addPage(pageThree);
		addPage(pageFour);
		addPage(pageFive);
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
				Properties bsBuilderProperties = new Properties();
				String basePath = project.getLocation().toOSString();
				bsBuilderProperties.load(new FileReader(new File(basePath + "/.settings/" + "org.bsbuilder.settings")));
				String projectName = project.getName();
				String basePackageName = bsBuilderProperties.getProperty("basePackage");
				
				//create Domain Class
				createJavaDomainClass(projectContainer, basePackageName, pageThree.getDomainClassName());				
								
				//create Backbone Template files
				Map<String, Object> modelAttributes = pageThree.getModelAttributes();
				createEditAndListTemplateFiles(projectContainer, pageThree.getDomainClassName(), modelAttributes);
				
				//createController
				createControllerClass(projectContainer, basePackageName);
				
				//createService
				createServiceClass(projectContainer, basePackageName);
				
				//createDao
				createDaoClass(projectContainer, basePackageName);
				
				//create backbone model
				createBackboneModel(projectContainer, projectName);
				
				//create backbone collection
				createBackboneCollection(projectContainer, projectName);
				
				//create backbone edit view
				createBackboneEditView(projectContainer, projectName);
				
				//create backbone collection view
				createCollectionView(projectContainer, projectName);
				
				//
				addNewRoutesToRouter(projectContainer, projectName);
				
				//
				addNewTabsToHomePage(projectContainer, pageThree.getDomainClassName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	        
		return true;
	}
	
	private void createJavaDomainClass(IContainer projectContainer, String basePackageName, String className 
			) throws Exception{
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		String domainPackageName = basePackageName + ".web.domain";
		final boolean xssSelected = pageFour.getXssCheckbox().getSelection();
		final boolean csrfSelected = pageFour.getCsrfCheckbox().getSelection();
		CommonUtils.createPackageAndClass(javaFolder, domainPackageName, className, pageThree.getClassSource(basePackageName, domainPackageName, xssSelected || csrfSelected), 
				new NullProgressMonitor());
	}
	
	//NOT NEEDED
	// TODO - Determine if we really need to do this again	
	private void createJSPFile(IContainer projectContainer){
		//CommonUtils.addFileToProject(folders.get("src/main/webapp/WEB-INF"), new Path("index.jsp"),
		//		TemplateMerger.merge("/bsbuilder/resources/web/jsps/index.jsp-template", proj.getName(),"",""), monitor);
	}
	
	private void createEditAndListTemplateFiles(IContainer projectContainer, String domainClassName,
			Map<String, Object> modelAttributes) throws Exception{
		IFolder templatesFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/templates"));
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("attrs", modelAttributes);
		Path editPath;
		Path listPath;
		if(pageFive.isJSPTemplate()){
			editPath = new Path(domainClassName + "EditTemplate.jsp");
			listPath = new Path(domainClassName + "ListTemplate.jsp");
		}else{
			editPath = new Path(domainClassName + "EditTemplate.htm");
			listPath = new Path(domainClassName + "ListTemplate.htm");
		}
		
		CommonUtils.addFileToProject(templatesFolder, editPath, 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/EditTemplate.jsp-template",
						 mapOfValues ), new NullProgressMonitor());
		CommonUtils.addFileToProject(templatesFolder, listPath, 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/templates/ListTemplate.jsp-template",
						 mapOfValues), new NullProgressMonitor());

	}
	
	private void createControllerClass(IContainer projectContainer, String basePackageName)
	throws Exception{
		/* Add a Controller*/
		
		final String controllerClassName = pageThree.getDomainClassName() + "Controller";
		String controllerPackageName = basePackageName + ".controller";
		final String controllerSourceCode = pageThree.buildSourceCode(basePackageName, pageThree.getDomainClassName(), 
				pageThree.getDomainClassAttributeName(), "controller.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, controllerPackageName, controllerClassName, controllerSourceCode , new NullProgressMonitor());
		
	}
	
	private void createServiceClass(IContainer projectContainer, String basePackageName)
	throws Exception{
		final String serviceClassName = pageThree.getDomainClassName() + "Service";
		String servicePackageName = basePackageName + ".service";
		final String serviceSourceCode = pageThree.buildSourceCode(basePackageName, pageThree.getDomainClassName(), 
				pageThree.getDomainClassAttributeName(), "service.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, servicePackageName, serviceClassName, serviceSourceCode , new NullProgressMonitor());
	}
	
	private void createDaoClass(IContainer projectContainer, String basePackageName)
			throws Exception{
		final String daoClassName = pageThree.getDomainClassName() + "DAO";
		String daoPackageName = basePackageName + ".dao";
		final String daoSourceCode = pageThree.buildSourceCode(basePackageName, pageThree.getDomainClassName(), pageThree.getDomainClassAttributeName(),
				"dao.java-template");
		IFolder javaFolder = projectContainer.getFolder(new Path("src/main/java"));
		CommonUtils.createPackageAndClass(javaFolder, daoPackageName, daoClassName, daoSourceCode , new NullProgressMonitor());
	}
	
	private void createBackboneModel(IContainer projectContainer, String projectName) throws Exception{
		IFolder modelsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/models"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		CommonUtils.addFileToProject(modelsFolder, new Path(domainClassName + "Model.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/models/model-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createBackboneCollection(IContainer projectContainer, String projectName) throws Exception{
		IFolder collectionsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/collections"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());		
		CommonUtils.addFileToProject(collectionsFolder, new Path(domainClassName + "Collection.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/collections/collection-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	//LOOK HERE
	private void createBackboneEditView(IContainer projectContainer, String projectName) throws Exception{
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "EditView.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/view-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void createCollectionView(IContainer projectContainer, String projectName) throws Exception{
		IFolder viewsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js/views"));
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();		
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		mapOfValues.put("templateType", pageFive.isJSPTemplate()?"JSP" : "HTML");
		CommonUtils.addFileToProject(viewsFolder, new Path(domainClassName + "CollectionView.js"), 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/views/collection-view-template.js", mapOfValues), new NullProgressMonitor());
	}
	
	private void addNewTabsToHomePage(IContainer projectContainer, String className) throws Exception{
		IFolder indexFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF"));
		IFile indexJSPFile = indexFolder.getFile("index.jsp");
		File file = indexJSPFile.getRawLocation().toFile();
		String regex = "<!-- MARKER FOR INSERTING -->";
		String modifiedFile = FileUtils.readFileToString(file);
		modifiedFile = modifier(modifiedFile, regex,
				"<li><a href=\"#" + className.toLowerCase() + "s" + "\" data-toggle=\"tab\">" + className + "s" + "</a></li>\n", "");
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		indexJSPFile.delete(true, new NullProgressMonitor());
		indexJSPFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
	}
	
	private void addNewRoutesToRouter(IContainer projectContainer, String projectName) throws Exception{
		IFolder jsFolder = projectContainer.getFolder(new Path("src/main/webapp/WEB-INF/resources/js"));
		IFile routerFile = jsFolder.getFile("router.js");
		//int lineToInsertTo = getLineToInsertNewRoutesTo(routerFile);
		String domainClassName = pageThree.getDomainClassName();
		Map<String, Object> mapOfValues = new HashMap<String, Object>();
		mapOfValues.put("className", domainClassName);
		mapOfValues.put("projectName", projectName);
		mapOfValues.put("domainClassIdAttributeName", pageThree.getDomainClassAttributeName());
		mapOfValues.put("attrs", pageThree.getModelAttributes());
		
		File file = routerFile.getRawLocation().toFile();
		String backboneModelName = domainClassName + "Model";
		String backboneModelViewName = domainClassName + "EditView";
		String backboneCollectionName = domainClassName + "Collection";
		String backboneCollectionViewName = domainClassName + "CollectionView";
		String defineStringToInsert = ",'models/" + backboneModelName + "'" +
				",'views/" + backboneModelViewName + "'" + 
				",'collections/" + backboneCollectionName + "'" + 
				",'views/" + backboneCollectionViewName + "'\n";	
		
		//insert params into the 'define'
		/*String defineModifierRegex = "define\\s*\\(\\[[\\d\\w\\s\\'\\,\\/]*\\]";
		String modifiedFile = modifier(file, defineModifierRegex,
				defineStringToInsert, "]");
		
		//insert corresponding params into the function
		String functionParamStringToInsert = ", " + backboneModelName + ", " + backboneModelViewName +
				", " + backboneCollectionName + ", " + backboneCollectionViewName;
		
		String functionModifierRegex = "function\\s*\\([\\d\\w\\s\\$\\,]*\\)";
		modifiedFile = modifier(modifiedFile, functionModifierRegex,
				functionParamStringToInsert, ")");
		*/		
		
		String modifiedFile = FileUtils.readFileToString(file);
		String routeDefinitionStringToInsert = "\n," + "\"" + domainClassName.toLowerCase() + "/:id\" : " +
				"\"get" + domainClassName + "\",\n" +
				"\"" + domainClassName.toLowerCase() + "s\" : " + "\"get" + domainClassName + "List\"\n";
		String routeDefinitionRegex = "routes\\s*:\\s*\\{[\\d\\w\\s\\\"\\/:,]*\\}";
		modifiedFile = modifier(modifiedFile, routeDefinitionRegex,
				routeDefinitionStringToInsert, "}");
		
		InputStream inputStream = 
				TemplateMerger.merge("/bsbuilder/resources/web/js/backbone/routers/router-template-fragment-03.js", mapOfValues);
		
		StringWriter mergeOutput = new StringWriter();
		IOUtils.copy(inputStream, mergeOutput);
		
		//String routeActionStringToInsert = "";
		String routeActionRegex = "[\\s\\d\\w\\'\\/]*[\\n]\\s*Backbone.history.start\\(\\);";
		modifiedFile = modifier(modifiedFile, routeActionRegex,
				"\n" + mergeOutput.toString(), "");
		
		InputStream modifiedFileContent = new ByteArrayInputStream(modifiedFile.getBytes());
		routerFile.delete(true, new NullProgressMonitor());
		routerFile.create(modifiedFileContent, IResource.FORCE, new NullProgressMonitor());
		
	}
	
	public String modifier(String fileContents, String expression, String stringToInsert, String stringToBefore){
		String newFileContents = "";
				
			//String contents = FileUtils.readFileToString(new File(fileName));
			
			StringBuffer buffer = null;
			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(fileContents);
			if(matcher.find()){
				String origString = matcher.group();
				buffer = new StringBuffer(origString);
				if(!stringToBefore.equals(""))
					buffer.insert(origString.lastIndexOf(stringToBefore), stringToInsert);
				else
					buffer.insert(0, stringToInsert);
			}
			//newFileContents = fileContents.replaceAll(expression, buffer.toString().replace("$", "\\$"));
			newFileContents = fileContents.replaceFirst(expression, buffer.toString().replace("$", "\\$"));
		
		return newFileContents;
	}
	
	public String modifier(File file, String expression, String stringToInsert, String stringToBefore)
	throws Exception{
		String contents = FileUtils.readFileToString(file);
		return modifier(contents, expression, stringToInsert, stringToBefore);
	}
	
	

	private String[] getLineToInsertNewRoutesTo(IFile routerFile)
			throws Exception {		
		List<String> lines = new ArrayList<String>();
		InputStream inputStream =  routerFile.getContents();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		int lineNumber = 0;
		int blankLineNumber = 0;
		boolean blankLineFound = false;
		while((line = bufferedReader.readLine()) != null){
			lines.add(line + "\n");
			if(line.trim().equals("") && ! blankLineFound)
				blankLineNumber = lineNumber;
			if(line.indexOf("Backbone.history.start()") > -1){
				blankLineFound = true;
			}
			lineNumber++;
		}
		
		lines.add(blankLineNumber, "//HEY FOUND IT\n");
		lines.add(blankLineNumber, "\n");
		bufferedReader.close();
		//System.out.println("The last blank line before start is: " + blankLineNumber);
		String[] linesToReturn = new String[lines.size()];
		lines.toArray(linesToReturn);
		return linesToReturn;
	}

}
