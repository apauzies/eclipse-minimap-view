package com.pauzies.minimap.views;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BidiSegmentEvent;
import org.eclipse.swt.custom.BidiSegmentListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
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

	private static final Color COLOR_DEFAULT = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	private static final Color COLOR_HIGHLIGHT = new Color(Display.getCurrent(), 200, 230, 230);

	class MyTextEditor {
		private final AbstractTextEditor editor;

		public MyTextEditor(AbstractTextEditor editor) {
			this.editor = editor;
		}

		public ISourceViewer getSourceViewer() {
			try {
				//for (Method m : AbstractTextEditor.class.getDeclaredMethods()) {
				//	System.err.println(m);
				//}
				//for (Field f : AbstractTextEditor.class.getDeclaredFields()) {
				//	System.err.println(f);
				//}
				//Field f = AbstractTextEditor.class.getDeclaredField("fSourceViewer");
				//f.setAccessible(true);
				//return (ISourceViewer)f.get(editor);
				Method m = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer");
				m.setAccessible(true);
				return (ISourceViewer) m.invoke(editor);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private static void fillMiniMap(StyledText minimap, MyTextEditor editor) {
		StyledText text = editor.getSourceViewer().getTextWidget();
		minimap.setContent(text.getContent());
	}

	private static void highlightVisibleRegion(StyledText minimap, MyTextEditor editor) {
		int start = editor.getSourceViewer().getTopIndexStartOffset();
		int end = editor.getSourceViewer().getBottomIndexEndOffset();
		int startLine = minimap.getLineAtOffset(start);
		int endLine = minimap.getLineAtOffset(end);
		// Reset
		minimap.setLineBackground(0, minimap.getLineCount() - 1, COLOR_DEFAULT);
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
				if (part instanceof AbstractTextEditor) {
					final MyTextEditor editor = new MyTextEditor((AbstractTextEditor) part);
					fillMiniMap(minimap, editor);
					highlightVisibleRegion(minimap, editor);
					editor.getSourceViewer().getTextWidget().addControlListener(new ControlListener() {

						@Override
						public void controlResized(ControlEvent e) {
							// TODO Auto-generated method stub
							highlightVisibleRegion(minimap, editor);
						}

						@Override
						public void controlMoved(ControlEvent e) {
							// TODO Auto-generated method stub

						}
					});
					editor.getSourceViewer().getTextWidget().addBidiSegmentListener(new BidiSegmentListener() {

						@Override
						public void lineGetSegments(BidiSegmentEvent event) {
							// TODO Auto-generated method stub
							highlightVisibleRegion(minimap, editor);
						}
					});
				}

			}
		});

		// getSourceViewer().getTextWidget().

	}

	@Override
	public void setFocus() {

	}

}
