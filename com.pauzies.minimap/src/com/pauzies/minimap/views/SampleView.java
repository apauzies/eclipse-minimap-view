package com.pauzies.minimap.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class SampleView extends ViewPart {

	public static final String ID = "com.pauzies.minimap.views.SampleView";

	TextEditor editor;
	
	@Override
	public void createPartControl(Composite parent) {

		final StyledText text2 = new StyledText(parent, SWT.NONE);
		text2.setEditable(false);
		
		Font font = new Font(parent.getDisplay(), "Arial", 4, SWT.NONE);
		text2.setFont(font);

		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part,
							ISelection selection) {
						System.err.println(part.toString() + selection);
						if (part instanceof TextEditor) {
							editor = (TextEditor) part;
							StyledText text = editor.getMySourceViewer().getTextWidget();
							text2.setContent(text.getContent());
							if (selection instanceof TextSelection) {
								TextSelection textSel = (TextSelection) selection;
								int startLine = textSel.getStartLine();
								int start = startLine > 20 ? startLine - 20 : 0;
								int nbLine = 10;
								text2.setLineBackground(start, nbLine, Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
							}
							
							text2.setLineBackground(0, 36, Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
//							text2.setSelectionRange(0, 800);
//							text2.setBlockSelectionBounds(0, 0, 400, 400);
						}

					}
				});

		// getSourceViewer().getTextWidget().

	}

	@Override
	public void setFocus() {

	}

}
