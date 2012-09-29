package com.pauzies.minimap.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.ViewPart;

public class SampleView extends ViewPart {

	public static final String ID = "com.pauzies.minimap.views.SampleView";
	
	private static final Color COLOR_DEFAULT = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color COLOR_HIGHLIGHT = new Color(Display.getCurrent(), 200,230,230);
	
	private static void fillMiniMap(StyledText minimap, TextEditor editor) {
		StyledText text = editor.getMySourceViewer().getTextWidget();
		minimap.setContent(text.getContent());
	}
	
	private static void highlightVisibleRegion(StyledText minimap, TextEditor editor) {
		int start = editor.getMySourceViewer().getTopIndexStartOffset();
		int end = editor.getMySourceViewer().getBottomIndexEndOffset();
		int startLine = minimap.getLineAtOffset(start);
		int endLine = minimap.getLineAtOffset(end);
		// Reset
		minimap.setLineBackground(0, minimap.getLineCount() -1, COLOR_DEFAULT);
		minimap.setLineBackground(startLine, endLine - startLine, COLOR_HIGHLIGHT);
	}
	
	@Override
	public void createPartControl(Composite parent) {

		final StyledText minimap = new StyledText(parent, SWT.NONE);
		minimap.setEditable(false);
		minimap.setEnabled(false);

		Font font = new Font(parent.getDisplay(), "Arial", 4, SWT.NONE);
		minimap.setFont(font);

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				System.err.println(part.toString() + selection);
				if (part instanceof TextEditor) {
					TextEditor editor = (TextEditor) part;
					fillMiniMap(minimap, editor);
					highlightVisibleRegion(minimap, editor);
				}

			}
		});

		// getSourceViewer().getTextWidget().

	}

	@Override
	public void setFocus() {

	}

}
