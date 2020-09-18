package duplicateassertiondetector.handlers;

import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import java.util.Set;
import java.util.HashSet;

public class DuplicateAssertionDetectorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		// Initializing the display message as an empty string.
		String displayText = new String("");

		// Source code of the file currently opened in the editor.
		IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ITextEditor textEditor = (ITextEditor)activeEditor;
		IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		String sourcecode = document.get();

		Scanner scanner = new Scanner(sourcecode);

		String[] assertions = null;
		
		int duplicateAssertionCount = 0;
		Set<String> temp = new HashSet<>();

		while (scanner.hasNextLine()) {

			String line = scanner.nextLine();

			Pattern assertionPattern = Pattern.compile("^([^//]*)[a-zA-Z]*[\\.]?(assert)[a-zA-Z]*[\\s]*[\\(]?", Pattern.MULTILINE);
			
			if(assertionPattern.matcher(line).find()) {
				
				assertions = append(assertions,line);
				
			}	

		}

		scanner.close();
		
		for (String assertion : assertions) {
			if(temp.add(assertion) == false) {
				duplicateAssertionCount+=1;
			}
		}

		displayText = "The ratio of unique assertions = " + ((assertions.length-duplicateAssertionCount)*100/assertions.length) + "%";
		

		MessageDialog.openInformation(window.getShell(), "Duplicate Assertion Detector Outcome", displayText);


		return null;
	}

	private String[] append(String[] assertions, String line) {

		return (String[]) ArrayUtils.add(assertions,line);
		
	}
}
