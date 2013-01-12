package com.pauzies.minimap.views;

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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class MiniMapView extends ViewPart {

	public static final String ID = "com.pauzies.minimap.views.MiniMapView";

	private static final Color COLOR_DEFAULT = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	private static final Color COLOR_HIGHLIGHT = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
	private static final Color COLOR_TEXT = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
	private ISelectionListener selectionListener;
	private ControlListener controlListener;
	private BidiSegmentListener segmentListener;
	private Font font = new Font(Display.getCurrent(), "Arial", 4, SWT.NONE);
	private AbstractTextEditor selectedPart;
	private MyTextEditor editor;

	class MyTextEditor {
		private final AbstractTextEditor editor;

		public MyTextEditor(AbstractTextEditor editor) {
			this.editor = editor;
		}

		public ISourceViewer getSourceViewer() {
			try {
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
		minimap.setLineBackground(startLine, endLine - startLine +1, COLOR_HIGHLIGHT);
	}

	@Override
	public void createPartControl(Composite parent) {

		final StyledText minimap = new StyledText(parent, SWT.NONE);
		minimap.setEditable(false);
		// minimap.set
		minimap.setEnabled(true);
		minimap.setForeground(COLOR_TEXT);
		minimap.setFont(font);
		minimap.setCursor(null);
		minimap.setCaret(null);
		minimap.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				StyledText text = (StyledText) e.getSource();
				int line = text.getLineAtOffset(text.getCaretOffset());
				editor.getSourceViewer().getTextWidget().setCaretOffset(text.getCaretOffset());
				editor.getSourceViewer().getTextWidget().setTopIndex(line);
			}

		});
		minimap.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int offset = minimap.getCaretOffset();
				if (offset == event.x) {
					// right to left select
					minimap.setSelection(event.x, event.x);
				} else {
					// left to right select
					minimap.setSelection(event.y, event.y);
				}
			}
		});

		controlListener = new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				highlightVisibleRegion(minimap, editor);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		};

		segmentListener = new BidiSegmentListener() {

			@Override
			public void lineGetSegments(BidiSegmentEvent event) {
				highlightVisibleRegion(minimap, editor);
			}
		};

		selectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if (part == selectedPart) {
					return;
				}
				if (!(part instanceof AbstractTextEditor)) {
					return;
				}
				selectedPart = (AbstractTextEditor) part;
				editor = new MyTextEditor((AbstractTextEditor) part);
				fillMiniMap(minimap, editor);
				highlightVisibleRegion(minimap, editor);
				editor.getSourceViewer().getTextWidget().addControlListener(controlListener);
				editor.getSourceViewer().getTextWidget().addBidiSegmentListener(segmentListener);
			}

		};

		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
		editor.getSourceViewer().getTextWidget().removeControlListener(controlListener);
		editor.getSourceViewer().getTextWidget().removeBidiSegmentListener(segmentListener);
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
	}

}
